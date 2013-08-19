package ind.fem.black.xposed.mods;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class StatusbarColor {
    private static final String TAG = "StatusbarColor";
    public static final String PACKAGE_NAME = "com.android.systemui";
    private static final String CLASS_PHONE_WINDOW_MANAGER = "com.android.internal.policy.impl.PhoneWindowManager";
    private static final String CLASS_PANEL_BAR = "com.android.systemui.statusbar.phone.PanelBar";
    private static final String CLASS_PHONE_STATUSBAR_VIEW = "com.android.systemui.statusbar.phone.PhoneStatusBarView";

    private static View mPanelBar;
    private static View mPhoneSbView;
    
    private static void log(String message) {
        XposedBridge.log(TAG + ": " + message);
    }

    private static BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            log("received broadcast: " + intent.toString());
            if (intent.getAction().equals(XblastSettings.ACTION_PREF_STATUSBAR_BGCOLOR_CHANGED) &&
                            intent.hasExtra(XblastSettings.EXTRA_SB_BGCOLOR)) {
                int bgColor = intent.getIntExtra(XblastSettings.EXTRA_SB_BGCOLOR, Color.BLACK);
                setStatusbarBgColor(bgColor);
            }
        }
    };

    public static void initZygote() {
        log("initZygote");

        try {
            final Class<?> phoneWindowManagerClass = XposedHelpers.findClass(CLASS_PHONE_WINDOW_MANAGER, null);
            XposedHelpers.findAndHookMethod(phoneWindowManagerClass,
                    "getSystemDecorRectLw", Rect.class, new XC_MethodReplacement() {

                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            Rect rect = (Rect) param.args[0];
                            rect.left = XposedHelpers.getIntField(param.thisObject, "mSystemLeft");
                            rect.top = XposedHelpers.getIntField(param.thisObject, "mSystemTop");
                            rect.right = XposedHelpers.getIntField(param.thisObject, "mSystemRight");
                            rect.bottom = XposedHelpers.getIntField(param.thisObject, "mSystemBottom");
                            return 0;
                        }
            });
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    public static void init(final XSharedPreferences prefs, final ClassLoader classLoader) {
        log("init");

        try {
             Class<?> panelBarClass = null; 
             Class<?> phoneSBViewClass = null;
            try {
             panelBarClass  = XposedHelpers.findClass(CLASS_PANEL_BAR, classLoader);
            } catch (Throwable t) {
            	XposedBridge.log(CLASS_PANEL_BAR + ": not found");
            }
            try {
            	phoneSBViewClass  = XposedHelpers.findClass(CLASS_PHONE_STATUSBAR_VIEW, classLoader);
               } catch (Throwable t) {
               	XposedBridge.log(CLASS_PHONE_STATUSBAR_VIEW + ": not found");
              }
            final int bgColor = prefs.getInt(XblastSettings.PREF_KEY_STATUSBAR_COLOR, 0);
            if (panelBarClass != null) {
	            XposedBridge.hookAllConstructors(panelBarClass, new XC_MethodHook() {
	
	                @Override
	                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
	                    prefs.reload();
	                    mPanelBar = (View) param.thisObject;
	                    if (bgColor != 0) {
	                   //int bgColor = prefs.getInt(XblastSettings.PREF_KEY_STATUSBAR_COLOR, Color.BLACK);
	                    setStatusbarBgColor(bgColor);
	
	                    IntentFilter intentFilter = new IntentFilter(XblastSettings.ACTION_PREF_STATUSBAR_BGCOLOR_CHANGED);
	                    mPanelBar.getContext().registerReceiver(mBroadcastReceiver, intentFilter);
	                    }
	                }
	            });
            } else if (phoneSBViewClass != null) {
            	 XposedBridge.hookAllConstructors(phoneSBViewClass, new XC_MethodHook() {
            			
 	                @Override
 	                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
 	                    prefs.reload();
 	                    mPhoneSbView = (View) param.thisObject;
 	                    if (bgColor != 0) {
 	                    //int bgColor = prefs.getInt(XblastSettings.PREF_KEY_STATUSBAR_COLOR, Color.BLACK);
 	                    setStatusbarBgColor1(bgColor);
 	
 	                    IntentFilter intentFilter = new IntentFilter(XblastSettings.ACTION_PREF_STATUSBAR_BGCOLOR_CHANGED);
 	                    mPhoneSbView.getContext().registerReceiver(mBroadcastReceiver, intentFilter);
 	                    }
 	                }
 	            });
            }

        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    private static void setStatusbarBgColor(int color) {
        if (mPanelBar == null) return;

        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(color);
        mPanelBar.setBackground(colorDrawable);
        log("statusbar background color set to: " + color);
    }
    
    private static void setStatusbarBgColor1(int color) {
        if (mPhoneSbView == null) return;

        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(color);
        mPhoneSbView.setBackground(colorDrawable);
        log("statusbar1 background color set to: " + color);
    }
}