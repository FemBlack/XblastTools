package ind.fem.black.xposed.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import java.lang.reflect.Field;

import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class CustomCarrierLabel {
	
	 private static final String TAG = "CustomCarrierLabel";
	 private static void log(String message) {
	        XposedBridge.log(TAG + ": " + message);
	    }
	 
	 public static void initZygote(final XSharedPreferences prefs) {
	        log("initZygote");
	        
	        Class<?> carrierTextClass = null;
			Class<?> carrierTextClass412 = null;
			
	        final String customCarrierLabelLS = prefs.getString(
					"customCarrierLabelLS", "");
			final String clTextSize = prefs.getString("clSize", "Small");
			try {
				try {
					carrierTextClass = XposedHelpers
							.findClass(
									"com.android.internal.policy.impl.keyguard.CarrierText",
									null);
				} catch (Throwable t) {
					XposedBridge
							.log("com.android.internal.policy.impl.keyguard.CarrierText not found");
				}
				try {
					carrierTextClass412 = XposedHelpers
							.findClass(
									"com.android.internal.policy.impl.KeyguardStatusViewManager",
									null);
				} catch (Throwable t) {
					XposedBridge
							.log("com.android.internal.policy.impl.KeyguardStatusViewManager not found");
				}
				if (carrierTextClass != null) {
					findAndHookMethod(
							"com.android.internal.policy.impl.keyguard.CarrierText",
							null,
							"updateCarrierText",
							"com.android.internal.telephony.IccCardConstants.State",
							CharSequence.class, CharSequence.class,
							new XC_MethodHook() {
								@Override
								protected void afterHookedMethod(
										MethodHookParam param) throws Throwable {
									try {
										TextView cclls = (TextView) param.thisObject;
										if (customCarrierLabelLS != null
												&& customCarrierLabelLS.length() > 0) {
											cclls.setText(customCarrierLabelLS);
										}
										float size = Black.getTextSize(clTextSize);
										if (size > 0) {
											cclls.setTextSize(size);
										}
										
										if (prefs.getBoolean("CCLLS_color_enabled",
												false)) {
											cclls.setTextColor(prefs.getInt(
													"CCLLS_color", 0xFFffffff));
										}
									} catch (Throwable t) {
										XposedBridge.log(t);
									}
								}
							});
				}

				else if (carrierTextClass412 != null) {
					findAndHookMethod(
							"com.android.internal.policy.impl.KeyguardStatusViewManager",
							null, "updateCarrierText", new XC_MethodHook() {
								@Override
								protected void afterHookedMethod(
										MethodHookParam param) throws Throwable {
									try {
										TextView cclls = (TextView) getObjectField(param.thisObject, "mCarrierView");
										if (customCarrierLabelLS != null
												&& customCarrierLabelLS.length() > 0) {
											cclls.setText(customCarrierLabelLS);
										}
										
										float size = Black.getTextSize(clTextSize);
										if (size > 0) {
											cclls.setTextSize(size);
										}
										
										if (prefs.getBoolean("CCLLS_color_enabled",
												false)) {
											cclls.setTextColor(prefs.getInt(
													"CCLLS_color", 0xFFffffff));
										}
									} catch (Throwable t) {
										XposedBridge.log(t);
									}
								}
							});
				}

			} catch (Throwable t) {
				XposedBridge.log(t);
			}
	    }

	    public static void init(final XSharedPreferences prefs, final ClassLoader classLoader) {
	        log("init");

	        final String customCarrierLabelNC = prefs.getString(
					"customCarrierLabelNC", "");
			try {
				findAndHookMethod(
						"com.android.systemui.statusbar.policy.NetworkController",
						 classLoader, "updateNetworkName", boolean.class,
						String.class, boolean.class, String.class,
						new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(MethodHookParam param)
									throws Throwable {
								try {
									Field cclnc = param.thisObject.getClass()
											.getDeclaredField("mNetworkName");
									cclnc.setAccessible(true);

									if (customCarrierLabelNC != null
											&& customCarrierLabelNC.length() > 0) {
										cclnc.set(param.thisObject,
												customCarrierLabelNC);
									}
								} catch (Throwable t) {
									XposedBridge.log(t);
								}
							}
						});
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
			
			try {
				findAndHookMethod(
						"com.android.systemui.statusbar.phone.PhoneStatusBar",
						 classLoader, "makeStatusBarView",
						new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(MethodHookParam param)
									throws Throwable {
								try {
									
				                   // View mStatusBarView = (View) XposedHelpers.getObjectField(param.thisObject, "mStatusBarView");
				                    TextView mCarrierLabel = (TextView)XposedHelpers.getObjectField(param.thisObject, "mCarrierLabel");
									
										if (prefs.getBoolean("CCLNC_color_enabled", false)) {
											mCarrierLabel.setTextColor(prefs.getInt("CCLNC_color", 0xFFffffff));
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
