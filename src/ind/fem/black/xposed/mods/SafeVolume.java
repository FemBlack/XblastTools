package ind.fem.black.xposed.mods;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XResources;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class SafeVolume {
    private static final String TAG = "SafeVolume";
    private static final String CLASS_AUDIO_SERVICE = "android.media.AudioService";
    private static boolean mSafeMediaVolumeEnabled;

    private static void log(String message) {
        XposedBridge.log(TAG + ": " + message);
    }

    private static BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            log("Broadcast received: " + intent.toString());
            if (intent.getAction().equals(XblastSettings.ACTION_PREF_SAFE_MEDIA_VOLUME_CHANGED)) {
                mSafeMediaVolumeEnabled = intent.getBooleanExtra(
                        XblastSettings.EXTRA_SAFE_MEDIA_VOLUME_ENABLED, false);
                log("Safe headset media volume set to: " + mSafeMediaVolumeEnabled);
            }
        }
    };

    public static void initZygote(final XSharedPreferences prefs) {

        XResources.setSystemWideReplacement("android", "bool", "config_safe_media_volume_enabled", true);

        /*if (prefs.getBoolean(XblastSettings.PREF_KEY_MUSIC_VOLUME_STEPS, false)) {
            initMusicStream();
        }*/

        final Class<?> classAudioService = XposedHelpers.findClass(CLASS_AUDIO_SERVICE, null);
        mSafeMediaVolumeEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_SAFE_MEDIA_VOLUME, false);
        log("Safe headset media volume set to: " + mSafeMediaVolumeEnabled);

        XposedBridge.hookAllConstructors(classAudioService, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                if (context == null) return;

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(XblastSettings.ACTION_PREF_SAFE_MEDIA_VOLUME_CHANGED);
                context.registerReceiver(mBroadcastReceiver, intentFilter);
                log("AudioService constructed. Broadcast receiver registered");
            }
        });

        XposedHelpers.findAndHookMethod(classAudioService, "enforceSafeMediaVolume", new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (!mSafeMediaVolumeEnabled) {
                    param.setResult(null);
                    return;
                }
            }
        });

        XposedHelpers.findAndHookMethod(classAudioService, "checkSafeMediaVolume", 
                int.class, int.class, int.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (!mSafeMediaVolumeEnabled) {
                    param.setResult(true);
                    return;
                }
            }
        });
    }
}