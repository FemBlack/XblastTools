package ind.fem.black.xposed.mods;

import java.lang.reflect.Constructor;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.WindowManager;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.callbacks.XCallback;

public class Xmod implements IXposedHookLoadPackage,
		IXposedHookInitPackageResources, IXposedHookZygoteInit {
	public static XSharedPreferences prefs;	
	public static String MODULE_PATH = null;
	public static XModuleResources modRes;
	
	public void initZygote(StartupParam startupParam) {		
		loadPrefs();
		getPhoneDetails();

		 MODULE_PATH = startupParam.modulePath;
		 modRes = XModuleResources.createInstance(MODULE_PATH, null);
		try {
			XResources.setSystemWideReplacement("android", "bool",
					"config_allowAllRotations", prefs.getBoolean(
							XblastSettings.PREF_KEY_ENABLE_ALL_ROTATION,
							false));
			
			/*XResources.setSystemWideReplacement("android", "dimen", "status_bar_height", modRes.fwd(R.dimen.status_bar_height));
			XResources.setSystemWideReplacement("android", "dimen", "status_bar_icon_size", modRes.fwd(R.dimen.status_bar_height));*/
			/*XResources.setSystemWideReplacement("android", "color",
					"primary_text_light", Color.RED);
			XResources.setSystemWideReplacement("android", "color",
					"background_dark", Color.RED);
			XResources.setSystemWideReplacement("android", "color",
					"background_light", Color.RED);
			XResources.setSystemWideReplacement("android", "color",
					"bright_foreground_dark_disabled", Color.RED);
			XResources.setSystemWideReplacement("android", "color",
					"bright_foreground_light_disabled", Color.RED);
			XResources.setSystemWideReplacement("android", "color",
					"dim_foreground_light", Color.RED);
			XResources.setSystemWideReplacement("android", "color",
					"dim_foreground_light_disabled", Color.RED);
			XResources.setSystemWideReplacement("android", "color",
					"bright_foreground_light", Color.RED);*/
			
			if (!Black.findClassInPhone(CrtEffect.CLASS_DISPLAY_POWER_STATE) || !Black.findClassInPhone(CrtEffect.CLASS_ELECTRON_BEAM_STATE)) {
				if (prefs.getInt("crt_effect_mode", 4) != 4) {
					XResources.setSystemWideReplacement("android", "bool",
							"config_animateScreenLights", false);
					XposedBridge.log("CRT Effect in Android 4.0.3 Enabled");
				}
			}
			if (prefs.getBoolean(XblastSettings.PREF_KEY_HOLO_BG_SOLID_BLACK, false)) {
				HoloBg.initZygote(prefs);
			}
			
			/*if (prefs.getBoolean(XblastSettings.PREF_KEY_HOLO_BG_SOLID_BLACK, false)) {
	            XResources.setSystemWideReplacement(
	                "android", "drawable", "background_holo_dark", modRes.fwd(R.drawable.background_holo_dark_solid));
	        } else {
	            XResources.setSystemWideReplacement(
	                    "android", "drawable", "background_holo_dark", modRes.fwd(R.drawable.background_holo_dark));
	        }
	        XResources.setSystemWideReplacement(
	                "android", "drawable", "background_holo_light", modRes.fwd(R.drawable.background_holo_light));*/
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		try {
			if ((prefs.getBoolean(XblastSettings.SOFT_KEYS, false))){
				NavigationBar.initZygote(prefs);	
			}
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		try {
			if ((prefs.getBoolean("fullscreen_caller_photo", false))){
				FullscreenPicCall.initZygote();	
			}
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		try {
			StatusbarColor.initZygote();
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		 /* try { ThirtyStepsVolumemod.initZygote(prefs); } catch (Throwable t) {
		  XposedBridge.log(t); }*/
		 
		try {
			CustomCarrierLabel.initZygote(prefs);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		try {			
			NotificationPanel.initZygote(prefs);			
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		try {			
			VolumeKeySkipTrack.initZygote(prefs);			
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		try {			
			VolKeyCursor.initZygote(prefs);			
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		try {			
			SafeVolume.initZygote(prefs);			
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		try {			
			LowBatteryWarning.initZygote(prefs);		
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		

	}

	public void handleLoadPackage(final LoadPackageParam lpparam)
			throws Throwable {

		/*if (lpparam.packageName.equals("com.android.settings")) {
			try {
				 AdvRebootMenu.init(prefs, lpparam.classLoader);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}

		}*/
		
		if (lpparam.packageName.equals(Black.DESKCLOCK) &&  Build.VERSION.SDK_INT >= 17) {
			try {			
				XBlastDream.handleLoadPackage(prefs,lpparam.classLoader);			
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		
		
		if (lpparam.packageName.equals(FullscreenPicCall.PACKAGE_NAME) &&  (prefs.getBoolean("fullscreen_caller_photo", false))) {
			try {
				FullscreenPicCall.init(prefs, lpparam.classLoader);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		
		
		if (lpparam.packageName.equals(AdvRebootMenu.PACKAGE_NAME)) {
			try {
				 AdvRebootMenu.init(prefs, lpparam.classLoader);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}

		}
		if (lpparam.packageName.equals(AdvRebootMenu.PACKAGE_NAME)) {
			try {
				if (Black.findClassInPhone(CrtEffect.CLASS_DISPLAY_POWER_STATE) && Black.findClassInPhone(CrtEffect.CLASS_ELECTRON_BEAM_STATE) && prefs.getInt("crt_effect_mode", 4) != 4) {
				CrtEffect.init(prefs);
				}
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		
		if (!lpparam.packageName.equals("com.android.systemui")) {
			return;
		}
		
		
		try {
			
			if (Build.VERSION.SDK_INT >= 17) {
				Nbg.init(prefs, lpparam.classLoader); 
			}
		} catch (Throwable t) {
			XposedBridge.log(Nbg.CLASS_NOTIFICATIONPANAL_VIEW + " not available");
		}
		try {
			Clock.init(prefs, lpparam.classLoader);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		try {
			RecentPanelMod.init(prefs, lpparam.classLoader);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}

		try {
			CustomCarrierLabel.init(prefs, lpparam.classLoader);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}

		if (prefs.getInt(
				XblastSettings.PREF_KEY_STATUSBAR_COLOR, 0) != 0) {
			// http://forum.xda-developers.com/showthread.php?t=1523703
			try {
				Constructor<?> constructLayoutParams = WindowManager.LayoutParams.class
						.getDeclaredConstructor(int.class, int.class,
								int.class, int.class, int.class);
				XposedBridge.hookMethod(constructLayoutParams,
						new XC_MethodHook(XCallback.PRIORITY_HIGHEST) {
							@Override
							protected void beforeHookedMethod(
									MethodHookParam param) throws Throwable {
								if ((Integer) param.args[4] == PixelFormat.RGB_565)
									param.args[4] = PixelFormat.TRANSLUCENT;
							}
						});
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		try {
			//if (lpparam.packageName.equals(CenterClock.PACKAGE_NAME) && prefs.getBoolean("center_clock", false)) {
			if (lpparam.packageName.equals(CenterClock.PACKAGE_NAME)) {
	            CenterClock.init(prefs, lpparam.classLoader);
	        }
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		 
		try {
			StatusbarColor.init(prefs, lpparam.classLoader);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		try {
			//NavigationBar.init(prefs, lpparam.classLoader);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		try {
			StatusBarIcons.init(prefs, lpparam.classLoader);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}

		try {
			SignalCluster.init(prefs, lpparam.classLoader);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		try {
			Battery.init(prefs, lpparam.classLoader);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		try {
			LowBatteryWarning.init(prefs, lpparam.classLoader);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		try {
			SmartAlarm.init(prefs, lpparam.classLoader);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		

	}

	public static void loadPrefs() {
		 prefs = new XSharedPreferences(XblastSettings.PACKAGE_NAME);
	}
	
	public void handleInitPackageResources(InitPackageResourcesParam resparam)
			throws Throwable {

		if (resparam.packageName.equals(Black.ANDROID)){
		try {
			//XposedBridge.log("etdfndfnxcvncvnvcbncvbncvb");
			//Playstore.handleInit(resparam, prefs);
			//NotificationPanel.handleInit(resparam, prefs);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
	}
		
		
		if (resparam.packageName.equals(FullscreenPicCall.PACKAGE_NAME) &&  (prefs.getBoolean("fullscreen_caller_photo", false))) {
			try {
				FullscreenPicCall.handlePackage(MODULE_PATH, resparam, prefs);
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
		}
		
		if (!resparam.packageName.equals("com.android.systemui"))
			return;
		
		final int statusBarColor = prefs.getInt(
				XblastSettings.PREF_KEY_STATUSBAR_COLOR, 0);
		
		
		try {
			if ((prefs.getBoolean(XblastSettings.SOFT_KEYS, false))){
				NavigationBar.handleInit(resparam, prefs);
			}
		} catch (Throwable t) {
			XposedBridge.log("status_bar_background is not available");
		}
		
		
		try {
			if(Build.VERSION.SDK_INT > 15) {
				NotificationPanel.handleInit(resparam , prefs);	
			}
					
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		try {
			//if (Build.VERSION.SDK_INT == 17) {
			//if (resparam.packageName.equals(CenterClock.PACKAGE_NAME) && prefs.getBoolean("center_clock", false)) {
			if (resparam.packageName.equals(CenterClock.PACKAGE_NAME)) {
				CenterClock.initResources(prefs, resparam);
			}
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		try {
			if (Build.VERSION.SDK_INT == 16) {
				Nbg.handleInit(resparam , prefs);
			}
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		try {
			Nbg.applyDimens(resparam , prefs);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		try {
			if(Build.VERSION.SDK_INT > 16) {
				QuickSettingsMod.handleInit(resparam , prefs);
			}
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		try {
			StatusBarIcons.handleInit(resparam, prefs);
			
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		
		
		/*if (notifBgTransEnabled) {
			try {
				final int statusbarColor = Color.TRANSPARENT;
				resparam.res.setReplacement("com.android.systemui", "drawable",
						"notification_panel_bg",
						new XResources.DrawableLoader() {
							@Override
							public Drawable newDrawable(XResources res, int id)
									throws Throwable {
								return new ColorDrawable(statusbarColor);
							}
						});
			} catch (Throwable t) {
				XposedBridge.log("notification_panel_bg is not available");
			}
		}*/
		try {
			//NotificationPanel.doHook(prefs, resparam, modRes);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		if (statusBarColor != 0) {
			try {
				final int statusbarColor = statusBarColor;
				resparam.res.setReplacement("com.android.systemui", "drawable",
						"status_bar_background",
						new XResources.DrawableLoader() {
							@Override
							public Drawable newDrawable(XResources res, int id)
									throws Throwable {
								return new ColorDrawable(statusbarColor);
							}
						});
			} catch (Throwable t) {
				XposedBridge.log("status_bar_background is not available");
			}
		}
	}

	

	private void getPhoneDetails() {
		XposedBridge.log("App Version>>>" + prefs.getString(XblastSettings.VERSION, ""));
		XposedBridge.log("MODEL Name>>>" + android.os.Build.MODEL);
		XposedBridge.log("BRAND Name>>>" + android.os.Build.BRAND);
		XposedBridge.log("DEVICE Name>>>" + android.os.Build.DEVICE);
		XposedBridge
				.log("MANUFACTURER Name>>>" + android.os.Build.MANUFACTURER);
		XposedBridge.log("PRODUCT Name>>>" + android.os.Build.PRODUCT);
		XposedBridge.log("ROM Name>>>" + android.os.Build.DISPLAY);
		XposedBridge.log("RELEASE Name>>>" + android.os.Build.VERSION.RELEASE);
		XposedBridge.log("SDK_INT Name>>>" + android.os.Build.VERSION.SDK_INT);
		XposedBridge.log("Phone has softkeys>>>" + prefs.getBoolean(XblastSettings.SOFT_KEYS, false));
	}
}