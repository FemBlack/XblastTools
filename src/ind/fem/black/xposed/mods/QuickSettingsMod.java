package ind.fem.black.xposed.mods;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class QuickSettingsMod {
    private static final String TAG = "ModQuickSettings";
    
    private static void log(String message) {
        XposedBridge.log(TAG + ": " + message);
    }
    
	

	public static void handleInit(InitPackageResourcesParam resparam, final XSharedPreferences prefs) {
		try {
			
			int quickSettingsTiles = prefs.getInt("quickSettingsColumns", 3);
			boolean quickSettingsTilesGap = prefs.getBoolean("quickSettingsTilesGap", false);
			
			if(quickSettingsTiles > 3) {
				resparam.res.setReplacement(Black.SYSTEM_UI, "integer",
						"quick_settings_num_columns", quickSettingsTiles);
			}
			
			if (quickSettingsTilesGap) {
				resparam.res.setReplacement(Black.SYSTEM_UI, "dimen", "quick_settings_cell_gap", Xmod.modRes.fwd(R.dimen.zero));
			}
			
			
			} catch (Exception e) {
	            log(e.toString());
	        }
	}
}