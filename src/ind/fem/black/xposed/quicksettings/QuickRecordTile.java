package ind.fem.black.xposed.quicksettings;

import ind.fem.black.xposed.mods.R;
import ind.fem.black.xposed.mods.RecordingService;

import java.io.File;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.robv.android.xposed.XposedBridge;

public class QuickRecordTile extends AQuickSettingsTile {
    private static final String TAG = "QuickRecordTile";

    private static final int STATE_IDLE = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_JUST_RECORDED = 3;
    private static final int STATE_NO_RECORDING = 4;

    private String mAudioFileName;
    private int mRecordingState = STATE_IDLE;
    private MediaPlayer mPlayer;
    private Handler mHandler;
    private TextView mTextView;

    private static void log(String message) {
        XposedBridge.log(TAG + ": " + message);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RecordingService.ACTION_RECORDING_STATUS_CHANGED) &&
                    intent.hasExtra(RecordingService.EXTRA_RECORDING_STATUS)) {
                int recordingStatus = intent.getIntExtra(
                        RecordingService.EXTRA_RECORDING_STATUS, RecordingService.RECORDING_STATUS_IDLE);
                log("Broadcast received: recordingStatus = " + recordingStatus);
                switch (recordingStatus) {
                    case RecordingService.RECORDING_STATUS_IDLE:
                        mRecordingState = STATE_IDLE;
                        mHandler.removeCallbacks(autoStopRecord);
                        break;
                    case RecordingService.RECORDING_STATUS_STARTED:
                        mRecordingState = STATE_RECORDING;
                        mHandler.postDelayed(autoStopRecord, 3600000);
                        log("Audio recording started");
                        break;
                    case RecordingService.RECORDING_STATUS_STOPPED:
                        mRecordingState = STATE_JUST_RECORDED;
                        mHandler.removeCallbacks(autoStopRecord);
                        log("Audio recording stopped");
                        break;
                    case RecordingService.RECORDING_STATUS_ERROR:
                    default:
                        mRecordingState = STATE_NO_RECORDING;
                        mHandler.removeCallbacks(autoStopRecord);
                        String statusMessage = intent.getStringExtra(RecordingService.EXTRA_STATUS_MESSAGE);
                        log("Audio recording error: " + statusMessage);
                        break;
                }
                updateResources();
            }
        }
        
    };

    public QuickRecordTile(Context context, Context gbContext, Object statusBar, Object panelBar) {
        super(context, gbContext, statusBar, panelBar);

        mAudioFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mAudioFileName += "/quickrecord.3gp";
        mHandler = new Handler();

        mOnClick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                File file = new File(mAudioFileName);
                if (!file.exists()) {
                    mRecordingState = STATE_NO_RECORDING;
                }

                switch (mRecordingState) {
                    case STATE_RECORDING:
                        stopRecording();
                        break;
                    case STATE_NO_RECORDING:
                        return;
                    case STATE_IDLE:
                    case STATE_JUST_RECORDED:
                        startPlaying();
                        break;
                    case STATE_PLAYING:
                        stopPlaying();
                        break;
                }

            }
        };

        mOnLongClick = new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                switch (mRecordingState) {
                    case STATE_NO_RECORDING:
                    case STATE_IDLE:
                    case STATE_JUST_RECORDED:
                        startRecording();
                        break;
                }
                return true;
            }
        };
    }

    @Override
    protected void onTileCreate() {
        LayoutInflater inflater = LayoutInflater.from(mGbContext);
        inflater.inflate(R.layout.quick_settings_tile_quickrecord, mTile);
        mTextView = (TextView) mTile.findViewById(R.id.quickrecord_tileview);

        IntentFilter intentFilter = new IntentFilter(RecordingService.ACTION_RECORDING_STATUS_CHANGED);
        mContext.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void updateTile() {
        final Resources res = mGbContext.getResources();

        File file = new File(mAudioFileName);
        if (!file.exists()) {
            mRecordingState = STATE_NO_RECORDING;
        }

        switch (mRecordingState) {
            case STATE_PLAYING:
                mLabel = res.getString(R.string.quick_settings_qr_playing);
                mDrawableId = R.drawable.ic_qs_qr_playing;
                break;
            case STATE_RECORDING:
                mLabel = res.getString(R.string.quick_settings_qr_recording);
                mDrawableId = R.drawable.ic_qs_qr_recording;
                break;
            case STATE_JUST_RECORDED:
                mLabel = res.getString(R.string.quick_settings_qr_recorded);
                mDrawableId = R.drawable.ic_qs_qr_recorded;
                break;
            case STATE_NO_RECORDING:
                mLabel = res.getString(R.string.quick_settings_qr_record);
                mDrawableId = R.drawable.ic_qs_qr_record;
                break;
            case STATE_IDLE:
            default:
                mLabel = res.getString(R.string.qs_tile_quickrecord);
                mDrawableId = R.drawable.ic_qs_qr_record;
                break;
        }

        mTextView.setText(mLabel);
        mTextView.setCompoundDrawablesWithIntrinsicBounds(0, mDrawableId, 0, 0);
    }

    final Runnable autoStopRecord = new Runnable() {
        public void run() {
            if (mRecordingState == STATE_RECORDING) {
                stopRecording();
            }
        }
    };

    final OnCompletionListener stoppedPlaying = new OnCompletionListener(){
        public void onCompletion(MediaPlayer mp) {
            mRecordingState = STATE_IDLE;
            updateResources();
        }
    };

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mAudioFileName);
            mPlayer.prepare();
            mPlayer.start();
            mRecordingState = STATE_PLAYING;
            updateResources();
            mPlayer.setOnCompletionListener(stoppedPlaying);
        } catch (IOException e) {
            log("startPlaying failed: " + e.getMessage());
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
        mRecordingState = STATE_IDLE;
        updateResources();
    }

    private void startRecording() {
        Intent si = new Intent(mGbContext, RecordingService.class);
        si.setAction(RecordingService.ACTION_RECORDING_START);
        si.putExtra(RecordingService.EXTRA_AUDIO_FILENAME, mAudioFileName);
        mGbContext.startService(si);
    }

    private void stopRecording() {
        Intent si = new Intent(mGbContext, RecordingService.class);
        si.setAction(RecordingService.ACTION_RECORDING_STOP);
        mGbContext.startService(si);
    }
}