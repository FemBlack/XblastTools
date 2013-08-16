package ind.fem.black.xposed.mods;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class XblastResultReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }
        
    public XblastResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }     
    } 
}