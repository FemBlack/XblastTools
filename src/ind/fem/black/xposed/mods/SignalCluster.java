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
							try {
								if (prefs.getBoolean("mobile_signal_color_enabled", false)) {
									ImageView mMobile = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mMobile");
									mMobile.setColorFilter(
											prefs.getInt("mobile_signal_color", 0xFFffffff),
											Mode.SRC_IN);
								}
							} catch (Throwable t) {
								XposedBridge.log(t);
							}
							
							try {
								if (prefs.getBoolean("mobile_signal_color_enabled", false)) {
									ImageView mMobileType = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mMobileType");
									mMobileType.setColorFilter(
											prefs.getInt("mobile_signal_color", 0xFFffffff),
											Mode.SRC_IN);
								}
							} catch (Throwable t) {
								XposedBridge.log(t);
							}
							
							try {
								if (prefs.getBoolean("mobile_signal_color_enabled", false)) {
									ImageView mAirplane = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mAirplane");
									mAirplane.setColorFilter(
											prefs.getInt("mobile_signal_color", 0xFFffffff),
											Mode.SRC_IN);
								}
							} catch (Throwable t) {
								XposedBridge.log(t);
							}
							
							try {
								if (prefs.getBoolean("mobile_inout_color_enabled", false)) {
								ImageView mobile_inout = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mMobileActivity");
								mobile_inout.setColorFilter(
										prefs.getInt("mobile_inout_color", 0xFFffffff),
										Mode.SRC_IN);
								}
							} catch (Throwable t) {
								XposedBridge.log(t);
							}
							
							try {
								if (prefs.getBoolean("wifi_signal_color_enabled", false)) {
								ImageView mWifi = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mWifi");
								mWifi.setColorFilter(
										prefs.getInt("wifi_signal_color", 0xFFffffff),
										Mode.SRC_IN);
								}
							} catch (Throwable t) {
								XposedBridge.log(t);
							}
							
							try {
								if (prefs.getBoolean("wifi_inout_color_enabled", false)) {
								ImageView wifi_inout = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mWifiActivity");
								wifi_inout.setColorFilter(
										prefs.getInt("wifi_inout_color", 0xFFffffff),
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
