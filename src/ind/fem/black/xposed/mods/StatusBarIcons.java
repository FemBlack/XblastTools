package ind.fem.black.xposed.mods;

import android.content.Context;
import android.graphics.PorterDuff;
import android.widget.ImageView;
import android.widget.LinearLayout;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class StatusBarIcons {

	
	private static final String TAG = "StatusBarIcons";
	private static final String CLASS_PHONE_STATUSBAR = "com.android.systemui.statusbar.phone.PhoneStatusBar";
	private static final String CLASS_PHONE_STATUSBAR_ICON = "com.android.internal.statusbar.StatusBarIcon";
	private static final String CLASS_PHONE_STATUSBAR_ICON_VIEW = "com.android.systemui.statusbar.StatusBarIconView";
	
	public static void init(XSharedPreferences prefs, ClassLoader classLoader) {
		log("init");
		applyIconColors(prefs,classLoader);
	}

	private static void log(String message) {
        XposedBridge.log(TAG + ": " + message);
    }
	
	private static void applyIconColors(final XSharedPreferences prefs, ClassLoader classLoader) {
		
		final Class<?> StatusBarIconClass =
                 XposedHelpers.findClass(CLASS_PHONE_STATUSBAR_ICON, classLoader);
		final Class<?> StatusBarIconViewClass =
                 XposedHelpers.findClass(CLASS_PHONE_STATUSBAR_ICON_VIEW, classLoader);
		
		 XposedHelpers.findAndHookMethod(CLASS_PHONE_STATUSBAR, classLoader, "addIcon",String.class, int.class,int.class,StatusBarIconClass, new XC_MethodHook() {
			 @Override
             protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            	 
            	 	Context mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
					ImageView statusBarIcon = (ImageView) XposedHelpers.newInstance(StatusBarIconViewClass, mContext, param.args[0],null);
					prefs.reload();
					final boolean sbIconsEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_REMAINING_SB_ICONS_COLOR_ENABLE, false);
					final int sbIconsColor = prefs.getInt(XblastSettings.PREF_KEY_REMAINING_SB_ICONS_COLOR, 0);
					if (sbIconsEnabled) {
						statusBarIcon.setColorFilter(sbIconsColor, PorterDuff.Mode.SRC_IN);
					}
					
             }
         });
		 
		 XposedHelpers.findAndHookMethod(CLASS_PHONE_STATUSBAR, classLoader, "updateIcon",String.class, int.class,int.class,StatusBarIconClass,StatusBarIconClass, new XC_MethodHook() {
             @Override
             protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            	 LinearLayout mStatusIcons = (LinearLayout) XposedHelpers.getObjectField(param.thisObject, "mStatusIcons");
            	 ImageView statusBarIcon = (ImageView) mStatusIcons.getChildAt((Integer) param.args[2]);
            	 prefs.reload();
				 final boolean sbIconsEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_REMAINING_SB_ICONS_COLOR_ENABLE, false);
				 final int sbIconsColor = prefs.getInt(XblastSettings.PREF_KEY_REMAINING_SB_ICONS_COLOR, 0);
				 if (sbIconsEnabled) {
				 	statusBarIcon.setColorFilter(sbIconsColor, PorterDuff.Mode.SRC_IN);
				 }
             }
         });
		log("Completed");
	}

	 public static void handleInit(InitPackageResourcesParam resparam, final XSharedPreferences prefs) {
			try {

				boolean hideNotifIcons = prefs.getBoolean("hideNotifIcons", false);

				if (hideNotifIcons) {
					resparam.res.setReplacement(Black.SYSTEM_UI, "dimen", "status_bar_icon_drawing_alpha", Xmod.modRes.fwd(R.dimen.status_bar_icon_drawing_alpha));
				}

			} catch (Exception e) {
				log(e.toString());
			}
			}

}