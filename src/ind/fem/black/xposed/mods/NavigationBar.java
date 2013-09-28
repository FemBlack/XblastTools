package ind.fem.black.xposed.mods;

import android.content.res.XResources;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class NavigationBar {
	
	
	private static final String TAG = "NavigationBar";
	
	
	 public static void initZygote(final XSharedPreferences prefs) {
			try {

				final int navBarHeight = Integer.valueOf(
	                    prefs.getString(XblastSettings.PREF_KEY_NAVIGATION_HEIGHT, "48"));
				
				if(navBarHeight != 48) {
					int navbarSizeId = getNavBarHeightId(navBarHeight);
					XResources.setSystemWideReplacement("android", "dimen", "navigation_bar_height", Xmod.modRes.fwd(navbarSizeId));
					XResources.setSystemWideReplacement("android", "dimen", "navigation_bar_height_landscape", Xmod.modRes.fwd(navbarSizeId));
				}
				

			} catch (Exception e) {
				XposedBridge.log(e);
			}
		}



	private static int getNavBarHeightId(int navBarHeight) {
		
		int navbarSizeId;
		
		switch(navBarHeight)  
        {  
        case 52:   
        	navbarSizeId = R.dimen.navigation_bar_height_52; 
            break;  
  
        case 50:   
        	navbarSizeId = R.dimen.navigation_bar_height_50;  
            break;  
  
          
        case 48:   
        	navbarSizeId = R.dimen.navigation_bar_height_48; 
            break;  
  
              
        case 44:   
        	navbarSizeId = R.dimen.navigation_bar_height_44;   
            break;  
  
              
        case 40:   
        	navbarSizeId = R.dimen.navigation_bar_height_40;  
            break;
            
        case 38:   
        	navbarSizeId = R.dimen.navigation_bar_height_38; 
            break;  
  
        case 36:   
        	navbarSizeId = R.dimen.navigation_bar_height_36;   
            break;  
  
          
        case 34:   
        	navbarSizeId = R.dimen.navigation_bar_height_34;   
            break;  
  
              
        case 32:   
        	navbarSizeId = R.dimen.navigation_bar_height_32;   
            break;  
  
              
        case 30:   
        	navbarSizeId = R.dimen.navigation_bar_height_30;   
            break;
        case 28:   
        	navbarSizeId = R.dimen.navigation_bar_height_28;   
            break;  
  
              
        case 26:   
        	navbarSizeId = R.dimen.navigation_bar_height_26;   
            break;  
  
              
        case 24:   
        	navbarSizeId = R.dimen.navigation_bar_height_24;   
            break;
            
        case 0:   
        	navbarSizeId = R.dimen.navigation_bar_height_0;   
            break;
  
        default:  
        	navbarSizeId = R.dimen.navigation_bar_height_48; 
          
        }
		
		return navbarSizeId;
		
	}
	
	private static void log(String message) {
        XposedBridge.log(TAG + ": " + message);
    }
	
	
	public static void handleInit(InitPackageResourcesParam resparam, final XSharedPreferences prefs) {
		try {
			final int theme = Integer.valueOf(
                    prefs.getString(XblastSettings.PREF_KEY_NAVIGATION_THEME, "0"));
			
			if (theme != 0) {
			
			String suffix = "";
			
				switch (theme) {

				case 1:
					suffix = "_shadow";
					break;
				case 2:
					suffix = "_pix";
					break;
				case 3:
					suffix = "_z";
					break;

				}
	        	
			String[] navButtonIcons = Xmod.modRes
					.getStringArray(R.array.nav_button_replacements);
			
			for (String resName : navButtonIcons) {
				resparam.res
						.setReplacement(Black.SYSTEM_UI, "drawable", resName,
								Xmod.modRes.fwd(Xmod.modRes.getIdentifier(
										resName + suffix, "drawable",
										XblastSettings.PACKAGE_NAME)));
				}
			}
		} catch (Exception e) {
			log(e.toString());
		}
	}
	
	
}