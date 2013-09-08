package ind.fem.black.xposed.mods;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class QuickSettingsMod {
    private static final String TAG = "ModQuickSettings";
    public static final String PACKAGE_NAME = "com.android.systemui";
    private static void log(String message) {
        XposedBridge.log(TAG + ": " + message);
    }
    
	private static int quickSettingsTiles = 3;

	public static void handleInit(InitPackageResourcesParam resparam, final XSharedPreferences prefs) {
		try {
		quickSettingsTiles = prefs.getInt("quickSettingsColumns", 3);
		resparam.res.setReplacement(Black.SYSTEM_UI, "integer",
				"quick_settings_num_columns", quickSettingsTiles);
		} catch (Exception e) {
            log("quick_settings_num_columns is not avilable");
        }
	}
}