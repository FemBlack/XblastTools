package ind.fem.black.xposed.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import android.content.Context;
import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class FullscreenPicCall {
    private static final String TAG = "FullscreenPicCall";
    public static final String PACKAGE_NAME = "com.android.phone";
    private static final String CLASS_CALLCARD = "com.android.phone.CallCard";
    private static final String CLASS_PHONE_CONSTANTS_STATE = 
            "com.android.internal.telephony.PhoneConstants$State";
    private static final String CLASS_PHONE_CONSTANTS_STATE1 = 
            "com.android.internal.telephony.Phone$State";
    private static final String CLASS_CALL = "com.android.internal.telephony.Call";
    private static final String CLASS_IN_CALL_TOUCH_UI = "com.android.phone.InCallTouchUi";
    
    private static Class<?> phoneConstStateClass;
    private static Class<?> phoneStateClass;
    private static Class<?> callClass;


    public static void initZygote() {
        XposedBridge.log(TAG + ": initZygote");
        if(Black.findClassInPhone(CLASS_PHONE_CONSTANTS_STATE)) {
        	phoneConstStateClass = Black.loadedClass;
        }
        if(Black.findClassInPhone(CLASS_PHONE_CONSTANTS_STATE1)) {
        	phoneStateClass = Black.loadedClass;
        }
        if(Black.findClassInPhone(CLASS_CALL)) {
        	callClass = Black.loadedClass;
        }
        
    }

    public static void init(final XSharedPreferences prefs, ClassLoader classLoader) {
        XposedBridge.log(TAG + ": init");
        
        try {
			findAndHookMethod("com.android.internal.policy.impl.PhoneWindow", null, "generateLayout",
					"com.android.internal.policy.impl.PhoneWindow.DecorView", new XC_MethodHook() {
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					if (!prefs.getBoolean("fullscreen_caller_photo", false))
                        return;
					
					if (!prefs.getBoolean("fullscreen_caller_sb", false))
                        return;
					
					Window window = (Window) param.thisObject;
					window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
			});
		} catch (Throwable e) {
			XposedBridge.log(e);
		}

        try {
            Class<?> callCardClass = XposedHelpers.findClass(CLASS_CALLCARD, classLoader);
            Class<?> inCallTouchUiClass = XposedHelpers.findClass(CLASS_IN_CALL_TOUCH_UI, classLoader);

            if (phoneConstStateClass != null) {
	            XposedHelpers.findAndHookMethod(callCardClass, "updateCallInfoLayout", phoneConstStateClass,
	                    new XC_MethodHook() {
	                @Override
	                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
	                    prefs.reload();
	                    if (!prefs.getBoolean("fullscreen_caller_photo", false))
	                        return;
	                    //XposedBridge.log(TAG + ": CallCard: after updateCallInfoLayout" + param.thisObject.getClass().getName());
	
	                    LinearLayout layout = (LinearLayout) param.thisObject;
	                    ViewGroup.MarginLayoutParams mlParams = 
	                            (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
	                    if (mlParams != null) {
	                        mlParams.bottomMargin = 0;
	                    }
	                }
	            });
            } else if (phoneStateClass != null) {
            	 XposedHelpers.findAndHookMethod(callCardClass, "updateCallInfoLayout", phoneStateClass,
 	                    new XC_MethodHook() {
 	                @Override
 	                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
 	                    prefs.reload();
 	                    if (!prefs.getBoolean("fullscreen_caller_photo", false))
 	                        return;
 	                    //XposedBridge.log(TAG + ": CallCard: after updateCallInfoLayout" + param.thisObject.getClass().getName());
 	
 	                    LinearLayout layout = (LinearLayout) param.thisObject;
 	                    ViewGroup.MarginLayoutParams mlParams = 
 	                            (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
 	                    if (mlParams != null) {
 	                        mlParams.bottomMargin = 0;
 	                    }
 	                }
 	            });
            }

            XposedHelpers.findAndHookMethod(callCardClass, "updateCallStateWidgets", 
                    callClass, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                	 
                    prefs.reload();
                    if (!prefs.getBoolean("fullscreen_caller_photo", false))
                        return;
                    //XposedBridge.log(TAG + ": CallCard: after updateCallStateWidgets" + param.thisObject.getClass().getName());

                    /*TextView simIndicator = 
                            (TextView) XposedHelpers.getObjectField(param.thisObject, "mSimIndicator");
                    if (simIndicator != null) {
                        simIndicator.setBackgroundResource(0);
                    }*/
                    
                    ViewGroup mPrimaryCallBanner = 
                            (ViewGroup) XposedHelpers.getObjectField(param.thisObject, "mPrimaryCallBanner");
                    
                    if (mPrimaryCallBanner != null) {
                    	mPrimaryCallBanner.setBackgroundResource(0);
                    }
                    
                    ViewGroup secondaryInfoContainer =
                            (ViewGroup) XposedHelpers.getObjectField(param.thisObject, "mSecondaryInfoContainer");
                    
                    if (secondaryInfoContainer != null) {
                        secondaryInfoContainer.setBackgroundResource(0);
                    }
                }
            });
            
          /*  XposedHelpers.findAndHookMethod(callCardClass, "updateDisplayForPerson",
            		callerInfoClass, int.class, boolean.class, callClass, callConn, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    XposedBridge.log(TAG + ": updateDisplayForPerson");
                    prefs.reload();
                    if (!prefs.getBoolean("fullscreen_caller_photo", false))
                        return;
                    
                     TextView mName  = (TextView) XposedHelpers.getObjectField(param.thisObject, "mName");
                     mName.setTextColor(Color.RED);
                     
                     TextView mPhoneNumber  = (TextView) XposedHelpers.getObjectField(param.thisObject, "mPhoneNumber");
                     mPhoneNumber.setTextColor(Color.RED);
                    
                     TextView mLabel  = (TextView) XposedHelpers.getObjectField(param.thisObject, "mLabel");
                     mLabel.setTextColor(Color.RED);
                }
            });*/

            XposedHelpers.findAndHookMethod(callCardClass, "onImageLoadComplete", int.class, Drawable.class, Bitmap .class, Object.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                	XposedBridge.log(TAG + ": onImageLoadComplete");
                    prefs.reload();
                    if (!prefs.getBoolean("fullscreen_caller_photo", false))
                        return;
                    
                    if (param.args[1] != null) {
                    } else if (param.args[2] != null) {
                    } else {
                    	try {
                    		
                    		String uriString = prefs.getString(
                    				"callerBgImageUri", "");
                    				if (uriString == null || uriString.equals("")) {
                    				XposedBridge.log("Image Uri Is null");
                    				return;
                    				}
                    				
                    				
                    				
                    		Context context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                    		Uri uri = Uri.parse(uriString);
            				Bitmap bitmap = MediaStore.Images.Media
            				.getBitmap(context.getContentResolver(), uri);

            				/*panelView.setBackgroundDrawable(new BitmapDrawable(
            				bitmap));
                    		Context blackContext = context.createPackageContext(Xmod.PACKAGE_NAME, 0);
                    		String wallpaperFile = blackContext.getFilesDir() + "/defaultCallerImage";
                    		File myFile = new File(wallpaperFile);
                    		if(!myFile.exists())
                    			return;
                    		
                    		Bitmap background = BitmapFactory.decodeFile(wallpaperFile);*/
                    		
                    		Drawable d = new BitmapDrawable(context.getResources(), bitmap);
                    		param.args[1] = d;
                    		//param.args[2] = bitmap;
                            XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                        } catch (Throwable e) {
                            XposedBridge.log(e);
                        }
                    }
                }
            });
            
            XposedHelpers.findAndHookMethod(callCardClass, "onFinishInflate", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                	XposedBridge.log(TAG + ": updateDisplayForPerson");
                    prefs.reload();
                    if (!prefs.getBoolean("fullscreen_caller_photo", false))
                        return;
                    int callBannerColor = prefs.getInt("call_banner_color", 0);
                    if (callBannerColor == 0)
                        return;
                    
                    final String fontName = prefs.getString(XblastSettings.PREF_KEY_CALLBANNER_FONT_LIST,"Default");
                    Typeface tf = null; 
					if (!fontName.equalsIgnoreCase("Default")) {
						tf = Black.getSelectedFont(fontName);
					}
                    
                    //int callBannerColor = prefs.getInt("call_banner_color", Color.BLACK);
                     
                    TextView mName  = (TextView) XposedHelpers.getObjectField(param.thisObject, "mName");
                     mName.setTextColor(callBannerColor);
                     
                     TextView mPhoneNumber  = (TextView) XposedHelpers.getObjectField(param.thisObject, "mPhoneNumber");
                     mPhoneNumber.setTextColor(callBannerColor);
                    
                     TextView mLabel  = (TextView) XposedHelpers.getObjectField(param.thisObject, "mLabel");
                     mLabel.setTextColor(callBannerColor);
                     
                     TextView mCallStateLabel  = (TextView) XposedHelpers.getObjectField(param.thisObject, "mCallStateLabel");
                     mCallStateLabel.setTextColor(callBannerColor);
                     
                     TextView mElapsedTime  = (TextView) XposedHelpers.getObjectField(param.thisObject, "mElapsedTime");
                     mElapsedTime.setTextColor(callBannerColor);

							if (tf != null) {
								mName.setTypeface(tf);
								mPhoneNumber.setTypeface(tf);
								mLabel.setTypeface(tf);
								mCallStateLabel.setTypeface(tf);
								mElapsedTime.setTypeface(tf);
							}

                     /*ImageView mPhoto  = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mPhoto");
                     mPhoto.setScaleType(ScaleType.CENTER_CROP);*/
                }
            });
            

            XposedHelpers.findAndHookMethod(inCallTouchUiClass, "showIncomingCallWidget",
                    callClass, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    //XposedBridge.log(TAG + ": InCallTouchUi: after showIncomingCallWidget");
                    prefs.reload();
                    boolean showFullscreen = 
                            prefs.getBoolean("fullscreen_caller_photo", false);

                    View incomingCallWidget =
                            (View) XposedHelpers.getObjectField(param.thisObject, "mIncomingCallWidget");
                    if (incomingCallWidget != null) {
                        incomingCallWidget.setBackgroundColor(showFullscreen ? Color.TRANSPARENT : Color.BLACK);
                    }
                }
            });
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
    }
    
    public static void handlePackage(String path, InitPackageResourcesParam resparam, XSharedPreferences prefs) {
    	XposedBridge.log(TAG + ": handlePackage");
       
		try {
			 prefs.reload();
             if (!prefs.getBoolean("fullscreen_caller_photo", false))
                 return;
             
             int callBannerColor = prefs.getInt("call_banner_color", 0);
             if (callBannerColor == 0)
                 return;
			
			XModuleResources modRes = XModuleResources.createInstance(path, resparam.res);
			final Drawable d= modRes.getDrawable(R.drawable.local_ic_lockscreen_glowdot);
			final int glowDotColor = callBannerColor;
			
				resparam.res.setReplacement("android", "drawable", "ic_lockscreen_glowdot",new XResources.DrawableLoader() {
					@Override
					public Drawable newDrawable(XResources res, int id) throws Throwable {
						d.setColorFilter(glowDotColor, Mode.SRC_IN);
						return d;
					}
				});
		} catch (Throwable t) {
			XposedBridge.log("ic_lockscreen_glowdot is not available");
		}
		
		XposedBridge.log(TAG + ": Completed");

    }
}