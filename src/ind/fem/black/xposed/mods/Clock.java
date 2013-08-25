package ind.fem.black.xposed.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import android.graphics.Typeface;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Clock {
	
	private static final String TAG = "Clock";
	
	public static void init(final XSharedPreferences prefs, ClassLoader classLoader) {
        XposedBridge.log(TAG + ": init");
        final int clockColor = prefs.getInt(XblastSettings.PREF_KEY_SB_CLOCK_COLOR, 0);
        final boolean clockColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_SB_CLOCK_COLOR_ENABLE, false);
        final String text = prefs.getString(XblastSettings.PREF_KEY_CUSTOM_TEXT, "");
		/*final int shadowColor = prefs.getInt(
				SeekBarActivity.CONST_SHADOW_COLOR, 0xFFffffff);
		final int dx = prefs.getInt(TextEffectsActivity.CONST_SHADOW_DX, 0);
		final int dy = prefs.getInt(TextEffectsActivity.CONST_SHADOW_DY, 0);
		final int rad = prefs.getInt(TextEffectsActivity.CONST_SHADOW_RAD, 0);
		final String message = prefs.getString(
				TextEffectsActivity.EXTRA_MESSAGE, "");
		final String fontName = prefs.getString(TextEffectsActivity.CONST_FONT,
				"Pick a Font");
		final Typeface tf = Black.getSelectedFont(fontName);*/
        
        final String fontName = prefs.getString(XblastSettings.PREF_KEY_FONT_LIST,"Default");
        
        /*final int clockColorNp = prefs.getInt(XblastSettings.PREF_KEY_NP_CLOCK_COLOR, 0);
        final String textNp = prefs.getString(XblastSettings.PREF_KEY_CUSTOM_TEXT_NP, "");
        final String fontNameNp = prefs.getString(XblastSettings.PREF_KEY_FONT_LIST_NP,"Default");*/
		 
		

		findAndHookMethod("com.android.systemui.statusbar.policy.Clock",
				classLoader, "updateClock", new XC_MethodHook() {
			
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						try {
							if (prefs.getBoolean("amPm", false)) {
								XposedHelpers.setIntField(param.thisObject,
										"AM_PM_STYLE", 2);
							}
						} catch (Throwable t) {
							XposedBridge.log(t);
						}
					}
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						try {
							TextView tv = (TextView) param.thisObject;
							tv.append(" " + text);
							
							if(clockColorEnabled) {
								tv.setTextColor(clockColor);
							}
							Typeface tf = null; 
							if (!fontName.equalsIgnoreCase("Default")) {
								tf = Black.getSelectedFont(fontName);
							}
							//tv.setShadowLayer(rad, dx, dy, shadowColor);
							if (tf != null) {
								tv.setTypeface(tf);
							}
							
						} catch (Throwable t) {
							XposedBridge.log(t);
						}
					}
				});

		findAndHookMethod("com.android.systemui.statusbar.policy.DateView",
				classLoader, "updateClock", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						try {
							TextView tv = (TextView) param.thisObject;
							/*XposedBridge.log("textNp" + textNp);
							XposedBridge.log("clockColorNp" + clockColorNp);
							XposedBridge.log("fontNameNp" + fontNameNp);*/
							//String text = tv.getText().toString();
							//tv.setText(text);
							/*if(!textNp.isEmpty()) {
								tv.append(" " + textNp);
							}*/
							
							if(clockColorEnabled) {
								tv.setTextColor(clockColor);	
							}
							Typeface tf = null; 
							if (!fontName.equalsIgnoreCase("Default")) {
								tf = Black.getSelectedFont(fontName);
							}
							if (tf != null) {
								tv.setTypeface(tf);
							}
						} catch (Throwable t) {
							XposedBridge.log(t);
						}
					}
				});

	}
}
