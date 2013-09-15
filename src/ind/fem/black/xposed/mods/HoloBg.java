package ind.fem.black.xposed.mods;

import ind.fem.black.xposed.dialogs.SaveDialog;

import java.io.File;

import android.content.res.XResources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Environment;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class HoloBg {
	
	private static GradientDrawable mDrawable;
	private static String[] holo = new String[] {
			"background_holo_dark","background_holo_light","tw_background_holo_dark","tw_background_holo_light" };
	public static void initZygote(final XSharedPreferences prefs) {
       // XModuleResources modRes = XModuleResources.createInstance(Xmod.MODULE_PATH, null);
        
		final int holoBgColor = prefs.getInt(
				XblastSettings.PREF_KEY_HOLO_BG_COLOR, 0);
		final boolean holoBgColorEnabled = prefs.getBoolean(
				XblastSettings.PREF_KEY_HOLO_BG_COLOR_ENABLE, false);
		

		final boolean gradientSettingsEnable = prefs.getBoolean(
				XblastSettings.PREF_KEY_GRADIENT_SETTINGS_ENABLE, false);
		final int gradientColor = prefs.getInt(
				XblastSettings.PREF_KEY_HOLO_GRADIENT_COLOR, 0);
		final boolean gradientColorEnabled = prefs.getBoolean(
				XblastSettings.PREF_KEY_HOLO_GRADIENT_COLOR_ENABLE, false);

		final int gradientColorOrientation = Integer.valueOf(prefs.getString(
				XblastSettings.PREF_KEY_GRADIENT_COLOR_ORIENTATION, "0"));
		final Orientation orientation = Black
				.getGradientOrientation(gradientColorOrientation);
		
		final String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + SaveDialog.BACKUP_DIR;
		if (holoBgColorEnabled) {
			
     		if(!gradientSettingsEnable) {
     			for (final String holoStrings : holo) {
     				
     				int id = XResources.getSystem().getIdentifier(holoStrings, "drawable", Black.ANDROID);
     				
     				XposedBridge.log(holoStrings + ": " + id);
     				
     				if (id == 0) {
     					continue;
     				}
     				
     				XResources.setSystemWideReplacement(id, new XResources.DrawableLoader() {
         	   				@Override
         					public Drawable newDrawable(XResources res, int id) throws Throwable {
         						return new ColorDrawable(holoBgColor);
         					}
         				});
     			}
     			/*XResources.setSystemWideReplacement(
     	                "android", "drawable", "background_holo_dark", new XResources.DrawableLoader() {
     	   				@Override
     					public Drawable newDrawable(XResources res, int id) throws Throwable {
     						return new ColorDrawable(holoBgColor);
     					}
     				});
     			
     			XResources.setSystemWideReplacement(
    	                "android", "drawable", "background_holo_light", new XResources.DrawableLoader() {
         	   				@Override
         					public Drawable newDrawable(XResources res, int id) throws Throwable {
         						return new ColorDrawable(holoBgColor);
         					}
         				});*/
	 		} else {
	 			
	 			if(gradientColorEnabled) {
	 				//int colors[] = {Black.enlight(xDreamBgColor,0.25f), Black.enlight(xDreamBgColor,0.50f), Black.enlight(gradientColor,0.75f), Black.enlight(gradientColor,1.0f)};
	 				int colors[] = {holoBgColor, gradientColor};
	 				mDrawable = new GradientDrawable(orientation, colors);
	 			} else {
	 				int colors[] = {Black.enlight(holoBgColor,0.25f), Black.enlight(holoBgColor,0.50f), Black.enlight(holoBgColor,0.75f), Black.enlight(holoBgColor,1.0f)};
	 				mDrawable = new GradientDrawable(orientation, colors);
	 			}
	 			
	 			for (final String holoStrings : holo) {
	 				
	 				int id = XResources.getSystem().getIdentifier(holoStrings, "drawable", Black.ANDROID);
	 				XposedBridge.log(holoStrings + ": " + id);
     				if (id == 0) {
     					continue;
     				}
	 				XResources.setSystemWideReplacement(id, new XResources.DrawableLoader() {
	     	   				@Override
	     					public Drawable newDrawable(XResources res, int id) throws Throwable {
	     						return mDrawable;
	     					}
	     				});
     			}
	 			
	 			/*XResources.setSystemWideReplacement(
     	                "android", "drawable", "background_holo_dark", new XResources.DrawableLoader() {
     	   				@Override
     					public Drawable newDrawable(XResources res, int id) throws Throwable {
     						return mDrawable;
     					}
     				});
	 			
	 			XResources.setSystemWideReplacement(
		                "android", "drawable", "background_holo_light", new XResources.DrawableLoader() {
	     	   				@Override
	     					public Drawable newDrawable(XResources res, int id) throws Throwable {
	     						return mDrawable;
	     					}
	     				});*/
	 			
	 		}
     		
     	 } else {
     		for (final String holoStrings : holo) {
     			
     			int id = XResources.getSystem().getIdentifier(holoStrings, "drawable", Black.ANDROID);
     			XposedBridge.log(holoStrings + ": " + id);
 				if (id == 0) {
 					continue;
 				}
 				
     			XResources.setSystemWideReplacement(id, new XResources.DrawableLoader() {
     	   				@Override
     					public Drawable newDrawable(XResources res, int id) throws Throwable {
     	   				String wallpaperFile = path + "/holo.png";
	     	   			File myFile = new File(wallpaperFile);
	     	   			
	     	   			if(!myFile.exists()) {
	     	   				return new ColorDrawable();
	     	   			}
     						return Drawable.createFromPath(wallpaperFile);
     					}
     				});
 			}
     		/*XResources.setSystemWideReplacement(
 	                "android", "drawable", "background_holo_dark", new XResources.DrawableLoader() {
 	   				@Override
 					public Drawable newDrawable(XResources res, int id) throws Throwable {
 						return Drawable.createFromPath(path + "/holo.png");
 					}
 				});
 			
 			XResources.setSystemWideReplacement(
	                "android", "drawable", "background_holo_light", new XResources.DrawableLoader() {
     	   				@Override
     					public Drawable newDrawable(XResources res, int id) throws Throwable {
     						return  Drawable.createFromPath(path + "/holo.png");
     					}
     				});*/
	     	 }
    }

}
