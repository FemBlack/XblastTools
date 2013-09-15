package ind.fem.black.xposed.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LayoutInflated.LayoutInflatedParam;

public class Nbg {
private static final String TAG = "Nbg";
private static Context mContext;
public static final String CLASS_NOTIFICATIONPANAL_VIEW = "com.android.systemui.statusbar.phone.NotificationPanelView";

private static void log(String message) {
    XposedBridge.log(TAG + ": " + message);
    //XposedBridge.log(TAG + ": " + flag);
}

	public static void init(final XSharedPreferences prefs, final ClassLoader classLoader) {
		log("init");
		final boolean nbgEnabled = prefs.getBoolean("nbgEnabled", false);
		final int ngbColor = prefs.getInt(XblastSettings.CONST_NGB_COLOR, 0);
		
		final boolean gradientSettingsEnable = prefs.getBoolean(XblastSettings.PREF_KEY_GRADIENT_SETTINGS_ENABLE, false);
		final int gradientColor = prefs.getInt(XblastSettings.PREF_KEY_NOTIF_GRADIENT_COLOR, 0);
		final boolean gradientColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_NOTIF_GRADIENT_COLOR_ENABLE, false);
		final int gradientColorOrientation = Integer.valueOf(prefs.getString(XblastSettings.PREF_KEY_GRADIENT_COLOR_ORIENTATION, "0")); 
		final Orientation orientation = Black.getGradientOrientation(gradientColorOrientation);
		
		
		try {
			findAndHookMethod(
					CLASS_NOTIFICATIONPANAL_VIEW,
					 classLoader, "onFinishInflate",
					new XC_MethodHook() {
						@SuppressLint("NewApi")
						@Override
						protected void afterHookedMethod(MethodHookParam param)
								throws Throwable {
							try {
								 	FrameLayout NotifPV = (FrameLayout)param.thisObject;
								 	if(nbgEnabled) {
									 	mContext = NotifPV.getContext();									 	
			                    		NotifPV.setBackground(createImage(mContext, prefs));
								 	} else {
								 		/*if (ngbColor == 0)
								 			return;*/
								 		
								 		if(!gradientSettingsEnable) {
								 			NotifPV.setBackground(new ColorDrawable(ngbColor));
								 		} else if (gradientSettingsEnable) {
								 			GradientDrawable mDrawable;
								 			if(gradientColorEnabled) {
								 				int colors[] = {ngbColor, gradientColor};
								 				//int colors[] = {Black.enlight(ngbColor,0.25f), Black.enlight(ngbColor,0.50f), Black.enlight(gradientColor,0.75f), Black.enlight(gradientColor,1.0f)};
								 				mDrawable = new GradientDrawable(orientation, colors);
								 			} else {
								 				int colors[] = {Black.enlight(ngbColor,0.25f), Black.enlight(ngbColor,0.50f), Black.enlight(ngbColor,0.75f), Black.enlight(ngbColor,1.0f)};
								 				mDrawable = new GradientDrawable(orientation, colors);
								 			}
									 	    NotifPV.setBackground(mDrawable);
								 		}
								 	}
								 	log("onFinishInflate completed");
							} catch (Throwable t) {
								XposedBridge.log(t);
							}
							
						}
					});
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		
		
		/*try {
			XposedBridge.log("handleLoadNotificationShade completed1");
			handleLoadNotificationShade(prefs, classLoader);
			XposedBridge.log("handleLoadNotificationShade completed");
		} catch (Throwable t) {
			XposedBridge.log(t);
		}*/

	/*	try {
			XposedBridge.log("handleUpdateTextResources completed1");
			handleUpdateTextResources(prefs, classLoader);
			XposedBridge.log("handleUpdateTextResources completed");
		} catch (Throwable t) {
			XposedBridge.log(t);
		}*/

	}
	

	
	private static Drawable createImage(Context mContext, final XSharedPreferences prefs) {
		//mContext = NotifPV.getContext();
		Drawable drwImage = null;
		try {
			Context blackContext = mContext.createPackageContext(XblastSettings.PACKAGE_NAME, 0);
			String wallpaperFile = blackContext.getFilesDir() + "/nbgImage";
			File myFile = new File(wallpaperFile);
			
			if(!myFile.exists())
				return new ColorDrawable();
			
			Bitmap background = BitmapFactory.decodeFile(wallpaperFile);
			drwImage = new BitmapDrawable(mContext.getResources(), background);
			int alpha = prefs.getInt("nbg_alpha", 100);
			drwImage.setAlpha((int) (2.55 * alpha));
			
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		return drwImage;
	}
	
	public static void handleInit(final InitPackageResourcesParam resparam, final XSharedPreferences prefs) {
		
		log("handleInit");
		
		int ssbId = resparam.res.getIdentifier("super_status_bar", "layout",
				"com.android.systemui");
		int twssbId = resparam.res.getIdentifier("tw_super_status_bar",
				"layout", "com.android.systemui");
		

		log("super_status_bar>>>" + ssbId);
		log("tw_super_status_bar>>>" + twssbId);
		
		
		if (ssbId != 0) {
			
			try {
				resparam.res.hookLayout("com.android.systemui", "layout",
						"super_status_bar", new XC_LayoutInflated() {
							@Override
							public void handleLayoutInflated(
									LayoutInflatedParam liparam)
									throws Throwable {
								log(" super_status_bar>>>inflated");
								applyChanges(liparam, true, prefs);
								log("super_status_bar completed");
								return;
							}
						});
			} catch (Throwable t) {
				log("super_status_bar is not available");
				XposedBridge.log(t);
			}
		} 
		if (twssbId != 0) {
			
			try {
				resparam.res.hookLayout("com.android.systemui", "layout",
						"tw_super_status_bar", new XC_LayoutInflated() {
							@Override
							public void handleLayoutInflated(
									LayoutInflatedParam liparam)
									throws Throwable {
								log(" tw_super_status_bar>>>inflated");
								applyChanges(liparam, true,prefs);
								log("tw_super_status_bar completed");
								return;
							}
						});
			} catch (Throwable t2) {
				log("tw_super_status_bar is not available");
				XposedBridge.log(t2);
			}
		} 
	}
	
	@SuppressLint("NewApi")
	private static void applyChanges(LayoutInflatedParam liparam,
			boolean isSuperStatusBar, XSharedPreferences prefs) {
		
		final boolean nbgEnabled = prefs.getBoolean("nbgEnabled", false);
		final int ngbColor = prefs.getInt(XblastSettings.CONST_NGB_COLOR, 0);
		
		final boolean gradientSettingsEnable = prefs.getBoolean(XblastSettings.PREF_KEY_GRADIENT_SETTINGS_ENABLE, false);
		final int gradientColor = prefs.getInt(XblastSettings.PREF_KEY_NOTIF_GRADIENT_COLOR, 0);
		final boolean gradientColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_NOTIF_GRADIENT_COLOR_ENABLE, false);
		final int gradientColorOrientation = Integer.valueOf(prefs.getString(XblastSettings.PREF_KEY_GRADIENT_COLOR_ORIENTATION, "0")); 
		final Orientation orientation = Black.getGradientOrientation(gradientColorOrientation);
		
		if (isSuperStatusBar) {
			try {				
					View mNotificationPanel = (View) liparam.view
							.findViewById(liparam.res.getIdentifier(
									"notification_panel", "id", "com.android.systemui"));
					if (mNotificationPanel != null) {
						if(nbgEnabled) {
						 	mContext = mNotificationPanel.getContext();	
						 	mNotificationPanel.setBackground(createImage(mContext, prefs));
					 	} else {
					 		/*if (ngbColor == 0)
					 			return;*/
					 		
					 		if(!gradientSettingsEnable) {
					 			mNotificationPanel.setBackground(new ColorDrawable(ngbColor));
					 		} else if (gradientSettingsEnable) {
					 			GradientDrawable mDrawable;
					 			if(gradientColorEnabled) {
					 				int colors[] = {ngbColor, gradientColor};
					 				//int colors[] = {Black.enlight(ngbColor,0.25f), Black.enlight(ngbColor,0.50f), Black.enlight(gradientColor,0.75f), Black.enlight(gradientColor,1.0f)};
					 				mDrawable = new GradientDrawable(orientation, colors);
					 			} else {
					 				int colors[] = {Black.enlight(ngbColor,0.25f), Black.enlight(ngbColor,0.50f), Black.enlight(ngbColor,0.75f), Black.enlight(ngbColor,1.0f)};
					 				mDrawable = new GradientDrawable(orientation, colors);
					 			}
					 			mNotificationPanel.setBackground(mDrawable);
					 		}
					 		
					 		//mNotificationPanel.setBackground(new ColorDrawable(ngbColor));
					 	}
					}
				
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
			
		}

	}

	private static void handleLoadNotificationShade( final XSharedPreferences prefs, ClassLoader classLoader) {
		XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.PhoneStatusBar", classLoader,
				"loadNotificationShade", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {

						/*if (prefs.getBoolean("hideNoNotificationsTitleBar",
								true)) {
							((LinearLayout) XposedHelpers.getObjectField(
									param.thisObject, "mNoNotificationsTitle"))
									.setVisibility(View.GONE);
						}*/
						if (prefs.getBoolean(
								"notificationHeaderButtonColorEnabled", true)) {
							String[] buttons = new String[] {
									"mSettingsButton","mNotificationButton" };
							final int iconColor = prefs.getInt(
									"notificationHeaderButtonColor",
									Color.RED);
							for (final String string : buttons) {
								ImageView settingIV = (ImageView) XposedHelpers
										.getObjectField(param.thisObject,
												string);
								settingIV.setColorFilter(iconColor,
										PorterDuff.Mode.MULTIPLY);
							}
						}
					}
				});

	}

	public static void applyDimens(InitPackageResourcesParam resparam , final XSharedPreferences prefs) {
		try {
			log("dimension started");
			if (prefs.getBoolean(XblastSettings.PREF_KEY_NBG_PULLUP_PULLDOWN_SPEED, false)) {
				 resparam.res.setReplacement(
	                "com.android.systemui", "dimen", "self_expand_velocity", "92000dp");
				 resparam.res.setReplacement(
		                "com.android.systemui", "dimen", "self_collapse_velocity", "92000dp");
				 resparam.res.setReplacement(
		                "com.android.systemui", "dimen", "fling_expand_min_velocity", "2000dp");
				 resparam.res.setReplacement(
		                "com.android.systemui", "dimen", "fling_collapse_min_velocity", "2000dp");
				 resparam.res.setReplacement(
		                "com.android.systemui", "dimen", "fling_gesture_max_x_velocity", "2000dp");
				 resparam.res.setReplacement(
		                "com.android.systemui", "dimen", "fling_gesture_max_output_velocity", "9000dp");
				 resparam.res.setReplacement(
		                "com.android.systemui", "dimen", "expand_accel", "92000dp");
				 resparam.res.setReplacement(
		                "com.android.systemui", "dimen", "collapse_accel", "92000dp");
				
				log("dimension finsished");
	        } 
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
	}
}

