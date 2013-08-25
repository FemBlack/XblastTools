package ind.fem.black.xposed.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import android.graphics.PorterDuff.Mode;
import android.widget.ImageView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class SignalCluster {
	private static final String TAG = "SignalCluster";
	
	public static void init(final XSharedPreferences prefs, ClassLoader classLoader) {
        XposedBridge.log(TAG + ": init");
       
        try {
			findAndHookMethod(
					"com.android.systemui.statusbar.SignalClusterView",
					classLoader, "onAttachedToWindow", new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param)
								throws Throwable {
							 final int mobileSignalColor = prefs.getInt(XblastSettings.PREF_KEY_MOBILE_SIGNAL_COLOR, 0);
						     final boolean mobileSignalColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_MOBILE_SIGNAL_COLOR_ENABLE, false);
							try {
								if (mobileSignalColorEnabled) {
									ImageView mMobile = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mMobile");
									mMobile.setColorFilter(mobileSignalColor,
											Mode.SRC_IN);
								}
							} catch (Throwable t) {
								XposedBridge.log(t);
							}
							
							try {
								if (mobileSignalColorEnabled) {
									ImageView mMobileType = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mMobileType");
									mMobileType.setColorFilter(mobileSignalColor,
											Mode.SRC_IN);
								}
							} catch (Throwable t) {
								XposedBridge.log(t);
							}
							
							try {
								if (mobileSignalColorEnabled) {
									ImageView mAirplane = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mAirplane");
									mAirplane.setColorFilter(mobileSignalColor,
											Mode.SRC_IN);
								}
							} catch (Throwable t) {
								XposedBridge.log(t);
							}
							
							try {
								final int mobileSignalInOutColor = prefs.getInt(XblastSettings.PREF_KEY_MOBILE_INOUT_COLOR, 0);
							    final boolean mobileSignalInOutColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_MOBILE_INOUT_COLOR_ENABLE, false);
								if (mobileSignalInOutColorEnabled) {
								ImageView mobile_inout = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mMobileActivity");
								mobile_inout.setColorFilter(mobileSignalInOutColor,
										Mode.SRC_IN);
								}
							} catch (Throwable t) {
								XposedBridge.log(t);
							}
							
							try {
								final int wifiSignalColor = prefs.getInt(XblastSettings.PREF_KEY_WIFI_SIGNAL_COLOR, 0);
							    final boolean wifiSignalColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_WIFI_SIGNAL_COLOR_ENABLE, false);
								if (wifiSignalColorEnabled) {
								ImageView mWifi = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mWifi");
								mWifi.setColorFilter(wifiSignalColor,
										Mode.SRC_IN);
								}
							} catch (Throwable t) {
								XposedBridge.log(t);
							}
							
							try {
								final int wifiSignalInOutColor = prefs.getInt(XblastSettings.PREF_KEY_WIFI_INOUT_COLOR, 0);
							    final boolean wifiSignalColorInOutEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_WIFI_INOUT_COLOR_ENABLE, false);
								if (wifiSignalColorInOutEnabled) {
								ImageView wifi_inout = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mWifiActivity");
								wifi_inout.setColorFilter(wifiSignalInOutColor,
										Mode.SRC_IN);
								}
							} catch (Throwable t) {
								XposedBridge.log(t);
							}
						}
					});
		} catch (Throwable t) {
			XposedBridge.log(t);
		}

	}
}
