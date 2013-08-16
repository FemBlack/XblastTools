package ind.fem.black.xposed.mods;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class Playstore {
	
	private static final String TAG = "Playstore";
	public static final String CLASS_VENDING = "com.android.vending";
	
public static void handleInit(final InitPackageResourcesParam resparam, final XSharedPreferences prefs) {
		
		log("handleInit");
		
		if (!resparam.packageName.equals(CLASS_VENDING))
			return;
		
		// different ways to specify the resources to be replaced
		
		try {
			
			resparam.res.setSystemWideReplacement(CLASS_VENDING, "color", "play_main_background", Color.RED);
			
		} catch (Throwable t) {
			log("play_main_background is not available");
		}
		
		//resparam.res.setReplacement("com.android.systemui:string/quickpanel_bluetooth_text", "WOO!");
		
		//resparam.res.setReplacement("com.android.systemui", "integer", "config_maxLevelOfSignalStrengthIndicator", 6);
		log("Finished");
		
	}
	
	private static void log(String message) {
	    XposedBridge.log(TAG + ": " + message);
	}
}
