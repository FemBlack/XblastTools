package ind.fem.black.xposed.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import android.content.Context;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class SettingsMod {

	public static void init(XSharedPreferences prefs, ClassLoader classLoader)
			throws Throwable {
	/*	if (lpparam.packageName.equals("com.android.settings")) {
			findAndHookMethod("com.android.settings.DisplaySettings",
					lpparam.classLoader, "onCreate", android.os.Bundle.class,
					DisplaySettings_onCreate);
		}
	}

	public final XC_MethodHook DisplaySettings_onCreate = new XC_MethodHook() {
		@Override
		protected void afterHookedMethod(MethodHookParam param)
				throws Throwable {
			PreferenceScreen screen = ((PreferenceFragment) param.thisObject)
					.getPreferenceScreen();
			Context context = ((PreferenceFragment) param.thisObject)
					.getActivity();

			PreferenceCategory preferenceCategory = new PreferenceCategory(
					context);
			preferenceCategory.setTitle("Xposed Additions");

			
		}
	};*/

	findAndHookMethod("com.android.settings.DisplaySettings",
			classLoader, "onCreate", android.os.Bundle.class, new XC_MethodHook() {
		
		@Override
		protected void afterHookedMethod(MethodHookParam param)
				throws Throwable {
			PreferenceScreen screen = ((PreferenceFragment) param.thisObject)
					.getPreferenceScreen();
			Context context = ((PreferenceFragment) param.thisObject)
					.getActivity();

			/*PreferenceCategory preferenceCategory = new PreferenceCategory(
					context);
			preferenceCategory.setTitle("Xposed Additions");*/

			XposedBridge.log("in settings " + screen.getPreferenceCount());
		
				}
			});
}}
