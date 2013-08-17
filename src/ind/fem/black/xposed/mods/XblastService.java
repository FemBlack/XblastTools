package ind.fem.black.xposed.mods;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class XblastService extends IntentService {
    public static final String ACTION_TOGGLE_SYNC = "xblast.intent.action.TOGGLE_SYNC";
    public static final String ACTION_GET_SYNC_STATUS = "xblast.intent.action.GET_SYNC_STATUS";

    public static final int RESULT_SYNC_STATUS = 0;
    public static final String KEY_SYNC_STATUS = "syncStatus";

    public XblastService() {
        super("XblastService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(ACTION_TOGGLE_SYNC)) {
            ContentResolver.setMasterSyncAutomatically(!ContentResolver.getMasterSyncAutomatically());
        } else if (intent.getAction().equals(ACTION_GET_SYNC_STATUS)) {
            boolean syncStatus = ContentResolver.getMasterSyncAutomatically();
            ResultReceiver receiver = intent.getParcelableExtra("receiver");
            Bundle data = new Bundle();
            data.putBoolean(KEY_SYNC_STATUS, syncStatus);
            receiver.send(RESULT_SYNC_STATUS, data);
        }
    }

}
