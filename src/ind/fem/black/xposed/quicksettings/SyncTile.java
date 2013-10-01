package ind.fem.black.xposed.quicksettings;

import ind.fem.black.xposed.mods.R;
import ind.fem.black.xposed.mods.XblastResultReceiver;
import ind.fem.black.xposed.mods.XblastResultReceiver.Receiver;
import ind.fem.black.xposed.mods.XblastService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class SyncTile extends AQuickSettingsTile {

    private TextView mTextView;
    private Handler mHandler;
    private Object mSyncObserverHandle = null;
    private XblastResultReceiver mReceiver;
    private boolean mSyncState;

    public SyncTile(Context context, Context gbContext, Object statusBar, Object panelBar) {
        super(context, gbContext, statusBar, panelBar);

        mOnClick = new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                toggleState();
            }
        };

        mHandler = new Handler();
    }

    @Override
    protected void onTileCreate() {
        LayoutInflater inflater = LayoutInflater.from(mGbContext);
        inflater.inflate(R.layout.quick_settings_tile_sync, mTile);
        mTextView = (TextView) mTile.findViewById(R.id.sync_tileview);

        mReceiver = new XblastResultReceiver(mHandler);
        mReceiver.setReceiver(new Receiver() {

            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == XblastService.RESULT_SYNC_STATUS) {
                    mSyncState = resultData.getBoolean(XblastService.KEY_SYNC_STATUS);
                    updateResources();
                }
            }
            
        });

        mSyncObserverHandle = ContentResolver.addStatusChangeListener(
                ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS, mSyncObserver);
    }

    @Override
    protected void onTilePostCreate() {
        getSyncState();
    }

    @Override
    protected void updateTile() {
        if (mSyncState) {
            mDrawableId = R.drawable.ic_qs_sync_on;
            mLabel = mGbResources.getString(R.string.quick_settings_sync_on);
        } else {
            mDrawableId = R.drawable.ic_qs_sync_off;
            mLabel = mGbResources.getString(R.string.quick_settings_sync_off);
        }

        mTextView.setText(mLabel);
        mTextView.setCompoundDrawablesWithIntrinsicBounds(0, mDrawableId, 0, 0);
    }

    private void getSyncState() {
        Intent si = new Intent(mGbContext, XblastService.class);
        si.setAction(XblastService.ACTION_GET_SYNC_STATUS);
        si.putExtra("receiver", mReceiver);
        mGbContext.startService(si);
    }

    private void toggleState() {
        Intent si = new Intent(mGbContext, XblastService.class);
        si.setAction(XblastService.ACTION_TOGGLE_SYNC);
        mGbContext.startService(si);
    }

    private SyncStatusObserver mSyncObserver = new SyncStatusObserver() {
        public void onStatusChanged(int which) {
            // update state/view if something happened
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    getSyncState();
                }
            });
        }
    };
}