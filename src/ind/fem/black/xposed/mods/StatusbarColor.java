package ind.fem.black.xposed.mods;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XCallback;

@SuppressLint("NewApi")
public class StatusbarColor {
    private static final String TAG = "StatusbarColor";
    public static final String PACKAGE_NAME = "com.android.systemui";
    private static final String CLASS_PHONE_WINDOW_MANAGER = "com.android.internal.policy.impl.PhoneWindowManager";
    private static final String CLASS_PANEL_BAR = "com.android.systemui.statusbar.phone.PanelBar";
    private static final String CLASS_PHONE_STATUSBAR_VIEW = "com.android.systemui.statusbar.phone.PhoneStatusBarView";
    private static final String CLASS_PHONE_STATUSBAR = "com.android.systemui.statusbar.phone.PhoneStatusBar";
    private static final String CLASS_KEYBUTTON = "com.android.systemui.statusbar.policy.KeyButtonView";
    private static View mPanelBar;
    private static View mPhoneSbView;
    private static View mNavigationBarView;
    private static Context mContext;
    private static ImageView keyButtonView;
    
    private static Canvas mCurrentCanvas;
    private static Canvas mNewCanvas;
    private static TransitionDrawable mTransition;
    
    private static int navBgColor;
    private static boolean navBgColorEnabled;
    private static int navButtonColor;
    private static boolean navButtonEnabled;
    private static int navGlowColor;
    private static boolean navGlowEnabled;
    private static Drawable mGlowBG;
    
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
                setStatusbarBgColor1(bgColor);
            } else if (intent.getAction().equals(XblastSettings.ACTION_PREF_STATUSBAR_BGCOLOR_CHANGED) &&
                    intent.hasExtra(XblastSettings.EXTRA_NB_BGCOLOR)) {
            		navBgColor = intent.getIntExtra(XblastSettings.EXTRA_NB_BGCOLOR, Color.BLACK);
            		applyColor();
            	
            } else if (intent.getAction().equals(XblastSettings.ACTION_PREF_STATUSBAR_BGCOLOR_CHANGED) &&
                    intent.hasExtra(XblastSettings.EXTRA_NB_BUTTONCOLOR)) {
            		navButtonColor = intent.getIntExtra(XblastSettings.EXTRA_NB_BUTTONCOLOR, Color.BLACK);
            		applyColor();
            } else if (intent.getAction().equals(XblastSettings.ACTION_PREF_STATUSBAR_BGCOLOR_CHANGED) &&
                    intent.hasExtra(XblastSettings.EXTRA_NB_GLOWCOLOR)) {
            		navGlowColor = intent.getIntExtra(XblastSettings.EXTRA_NB_GLOWCOLOR, Color.BLACK);
            		applyColor();
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
        
        navBgColor = prefs.getInt(XblastSettings.PREF_KEY_NAV_BG_COLOR, 0);
        navBgColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_NAV_BG_COLOR_ENABLE, false);
        navButtonColor = prefs.getInt(XblastSettings.PREF_KEY_NAV_BUTTON_COLOR, 0);
        navButtonEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_NAV_BUTTON_COLOR_ENABLE, false);
        navGlowColor = prefs.getInt(XblastSettings.PREF_KEY_NAV_GLOW_COLOR, 0);
        navGlowEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_NAV_GLOW_COLOR_ENABLE, false);
        
        final Class<?> phoneStatusbarClass = XposedHelpers.findClass(CLASS_PHONE_STATUSBAR, classLoader);
        final Class<?> keyButtonViewClass = XposedHelpers.findClass(CLASS_KEYBUTTON, classLoader);
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
            final boolean bgColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_STATUSBAR_COLOR_ENABLE, false);
            if (panelBarClass != null) {
	            XposedBridge.hookAllConstructors(panelBarClass, new XC_MethodHook() {

	                @Override
	                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
	                    prefs.reload();
	                    mPanelBar = (View) param.thisObject;
	                    if (bgColorEnabled) {
	                   
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
 	                    if (bgColorEnabled) {
 	                    
 	                    setStatusbarBgColor1(bgColor);
 	
 	                    IntentFilter intentFilter = new IntentFilter(XblastSettings.ACTION_PREF_STATUSBAR_BGCOLOR_CHANGED);
 	                    mPhoneSbView.getContext().registerReceiver(mBroadcastReceiver, intentFilter);
 	                    }
 	                }
 	            });
            }
            
            try {
            	if ((prefs.getBoolean(XblastSettings.SOFT_KEYS, false))){
            	 XposedBridge.hookAllConstructors(keyButtonViewClass, new XC_MethodHook() {

                     @Override
                     protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    	 keyButtonView = (ImageView) param.thisObject;
                    	 mGlowBG = (Drawable)XposedHelpers.getObjectField(param.thisObject, "mGlowBG");
                    	
                    	 if(mGlowBG != null && navGlowEnabled) {
                    		 mGlowBG.clearColorFilter();
                    		 mGlowBG.setColorFilter(navGlowColor, PorterDuff.Mode.SRC_ATOP);
                    	 }
                    	 if(keyButtonView != null && navButtonEnabled) {
                    		 keyButtonView.clearColorFilter();
                    		 keyButtonView.setColorFilter(navButtonColor, PorterDuff.Mode.SRC_ATOP); 
                    	 }
                    	 //keyButtonView.clearColorFilter();
                    	 
                         //log("keyButtonViewClass constructed - keyButtonViewClass set");
                     }
                 });
            	 
           	 XposedHelpers.findAndHookMethod(phoneStatusbarClass, 
                        "makeStatusBarView", new XC_MethodHook(XCallback.PRIORITY_LOWEST) {

                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                        mNavigationBarView  = (View) XposedHelpers.getObjectField(param.thisObject, "mNavigationBarView");
                        
                        applyColor();
                        IntentFilter intentFilter = new IntentFilter(XblastSettings.ACTION_PREF_STATUSBAR_BGCOLOR_CHANGED);
                        mContext.registerReceiver(mBroadcastReceiver, intentFilter);
                    }
                });

                XposedHelpers.findAndHookMethod(phoneStatusbarClass, "getNavigationBarLayoutParams", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) param.getResult();
                        if (lp != null) {
                            lp.format = PixelFormat.TRANSLUCENT;
                            param.setResult(lp);
                        }
                    }
                });

                XposedHelpers.findAndHookMethod(phoneStatusbarClass, "disable", int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    	 applyColor();
                    }
                });

                XposedHelpers.findAndHookMethod(phoneStatusbarClass, "topAppWindowChanged",
                        boolean.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    	 applyColor();
                    }
                });
            }
           } catch (Throwable t) {
               XposedBridge.log(t);
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
        applyColor();
        log("statusbar background color set to: " + color);
    }
    
    private static void setStatusbarBgColor1(int color) {
        if (mPhoneSbView == null) return;

        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(color);
        mPhoneSbView.setBackground(colorDrawable);
        applyColor();
        log("statusbar1 background color set to: " + color);
    }
    
    @SuppressWarnings("deprecation")
	private static void applyColor() {
    	
    	if(mGlowBG != null && navGlowEnabled) {
    		mGlowBG.clearColorFilter();
   		 	mGlowBG.setColorFilter(navGlowColor, PorterDuff.Mode.SRC_ATOP);
   		
    	}
    	
    	if (mNavigationBarView != null && navBgColorEnabled) {
          	
        	// Reset all colors
            Bitmap currentBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            mCurrentCanvas = new Canvas(currentBitmap);
            mCurrentCanvas.drawColor(0xFF000000);
            BitmapDrawable currentBitmapDrawable = new BitmapDrawable(currentBitmap);

            Bitmap newBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            mNewCanvas = new Canvas(newBitmap);
            mNewCanvas.drawColor(0xFF000000);
           
			BitmapDrawable newBitmapDrawable = new BitmapDrawable(newBitmap);

            mTransition = new TransitionDrawable(new Drawable[]{currentBitmapDrawable, newBitmapDrawable});   
          	mNavigationBarView.setBackground(mTransition);
          	
          	 // Clear first layer, paint current color, reset mTransition to first layer
            mCurrentCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mCurrentCanvas.drawColor(navBgColor);
            mTransition.resetTransition();

            // Clear second layer, paint new color, start mTransition
            mNewCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mNewCanvas.drawColor(navBgColor);
            mTransition.startTransition(500);
           
           }
    	
    	if(keyButtonView != null && navButtonEnabled) {
    		keyButtonView.clearColorFilter();
   		 	keyButtonView.setColorFilter(navButtonColor, PorterDuff.Mode.SRC_ATOP);
   		 
    	}
   	 
    }
}