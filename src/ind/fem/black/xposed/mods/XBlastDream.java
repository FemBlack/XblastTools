package ind.fem.black.xposed.mods;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;


public class XBlastDream{

	 private static final String TAG = "DayDream";
	 private static final String CLASS_CLOCK_DREAMS = "com.android.deskclock.Screensaver";
	 //private static final String CLASS_CLOCK_ZEROPADDING = "com.android.deskclock.ZeroTopPaddingTextView";
	 
	 private static void log(String message) {
	        XposedBridge.log(TAG + ": " + message);
	    }
	 
	 private static Context mContext;
	 private static View mContentView;
	 private static XSharedPreferences prefs;
	 private static float paddingRatio = 0;
	 private static float bottomPaddingRatio = 0;
	 private static int  mPaddingRight = 0;
	  
     
	 public static void handleLoadPackage(final XSharedPreferences prefs, ClassLoader classLoader) {
	        log("handleLoadPackage");
	        //final Class<?> classZeroPadding = XposedHelpers.findClass(CLASS_CLOCK_ZEROPADDING, classLoader);
	        XBlastDream.prefs = prefs;
	        
	       /* try {	            
	            XposedBridge.hookAllConstructors(classZeroPadding, new XC_MethodHook() {
	                        @Override
	                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
	                        	paddingRatio = XposedHelpers.getFloatField(param.thisObject, "BOLD_FONT_PADDING_RATIO");
	                        	bottomPaddingRatio = XposedHelpers.getFloatField(param.thisObject, "BOLD_FONT_BOTTOM_PADDING_RATIO");
	                        	mPaddingRight = XposedHelpers.getIntField(param.thisObject, "mPaddingRight");
	                        	log("paddingRatio" + paddingRatio);
	                        	log("bottomPaddingRatio" + bottomPaddingRatio);
	                        	log("mPaddingRight" + mPaddingRight);
	                        }
	            });
	        } catch (Throwable t) {
	            XposedBridge.log(t);
	        }*/
	        
	        try {	            
	            XposedHelpers.findAndHookMethod(CLASS_CLOCK_DREAMS, classLoader,
	                    "layoutClockSaver", new XC_MethodHook() {

	                        @Override
	                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
	                        	mContentView  = (View) XposedHelpers.getObjectField(param.thisObject, "mContentView");	                        	
	                        	mContext = mContentView.getContext();
	                        	ApplyDreamChanges(mContentView);
	                        }
	            });
	        } catch (Throwable t) {
	            XposedBridge.log(t);
	        }
	    }
	 
	 @SuppressLint("NewApi")
	private static void ApplyDreamChanges(View mContentView) {
			 
		 	 prefs.reload();
		 	 
		 	 final int xDreamBgColor = prefs.getInt(XblastSettings.PREF_KEY_XDREAM_BG_COLOR, 0);
             final boolean xDreamBgColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_XDREAM_BG_COLOR_ENABLE, false);
             final int xDreamClockColor = prefs.getInt(XblastSettings.PREF_KEY_XDREAM_CLOCK_COLOR, 0);
             final boolean xDreamClockColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_XDREAM_CLOCK_COLOR_ENABLE, false);
             final String xDreamCustomText = prefs.getString(XblastSettings.PREF_KEY_XDREAM_CUSTOM_TEXT, "");
 			 final String textSize = prefs.getString(XblastSettings.PREF_KEY_XDREAM_CUSTOM_TEXT_SIZE, "Small");
 			 final String fontName = prefs.getString(XblastSettings.PREF_KEY_XDREAM_FONT_LIST,"Default");
 			 
 			 final boolean gradientSettingsEnable = prefs.getBoolean(XblastSettings.PREF_KEY_GRADIENT_SETTINGS_ENABLE, false);
 			 final int gradientColor = prefs.getInt(XblastSettings.PREF_KEY_XDREAM_GRADIENT_COLOR, 0);
 			 final boolean gradientColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_XDREAM_GRADIENT_COLOR_ENABLE, false);
 			
 			 final int gradientColorOrientation = Integer.valueOf(prefs.getString(XblastSettings.PREF_KEY_GRADIENT_COLOR_ORIENTATION, "0")); 
 			 final Orientation orientation = Black.getGradientOrientation(gradientColorOrientation);
 			
 			
             Typeface tf = null; 
				if (!fontName.equalsIgnoreCase("Default")) {
					tf = Black.getSelectedFont(fontName);
				}
            
	     	 Resources res = mContext.getResources();
	     	
	     	 if (xDreamBgColorEnabled) {
	     		if(!gradientSettingsEnable) {
	     			mContentView.setBackgroundColor(xDreamBgColor);
		 		} else {
		 			GradientDrawable mDrawable;
		 			if(gradientColorEnabled) {
		 				//int colors[] = {Black.enlight(xDreamBgColor,0.25f), Black.enlight(xDreamBgColor,0.50f), Black.enlight(gradientColor,0.75f), Black.enlight(gradientColor,1.0f)};
		 				int colors[] = {xDreamBgColor, gradientColor};
		 				mDrawable = new GradientDrawable(orientation, colors);
		 			} else {
		 				int colors[] = {Black.enlight(xDreamBgColor,0.25f), Black.enlight(xDreamBgColor,0.50f), Black.enlight(xDreamBgColor,0.75f), Black.enlight(xDreamBgColor,1.0f)};
		 				mDrawable = new GradientDrawable(orientation, colors);
		 			}
		 			mContentView.setBackground(mDrawable);
		 		}
	     		
	     	 } else {
	     		mContentView.setBackground(createImage(prefs));
	     	 }
	     	 
	     	 TextView timeDisplayHours = (TextView) mContentView.findViewById(res.getIdentifier("timeDisplayHours", "id", Black.DESKCLOCK));
	         TextView timeDisplayMinutes = (TextView) mContentView.findViewById(res.getIdentifier("timeDisplayMinutes", "id", Black.DESKCLOCK));
	         TextView am_pm = (TextView) mContentView.findViewById(res.getIdentifier("am_pm", "id", Black.DESKCLOCK));
	         
	     	if (xDreamClockColorEnabled) {
		         timeDisplayHours.setTextColor(xDreamClockColor);
		         timeDisplayMinutes.setTextColor(xDreamClockColor);
		         am_pm.setTextColor(xDreamClockColor);
	     	 }
	     	
	     	if (tf != null) {
	     		
	     		timeDisplayHours.setTypeface(tf);
	     		timeDisplayMinutes.setTypeface(tf,Typeface.NORMAL);
	     		am_pm.setTypeface(tf,Typeface.NORMAL);
	     		setPadding(timeDisplayHours);
	     		setPadding(timeDisplayMinutes);
	     		//setPadding(am_pm);
	        }
	     	
			float size = Black.getTextSize(textSize);
				
			
			if (xDreamCustomText != null
					&& xDreamCustomText.length() > 0) {
				LinearLayout.LayoutParams mLayout = new LinearLayout.LayoutParams(
		        		LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,0);
		     	mLayout.gravity = Gravity.CENTER;
		     	
		     	TextView customValue = new TextView(mContext);
		        customValue.setText(xDreamCustomText);
		        if (size > 0) {
		        	customValue.setTextSize(size);
				}
		        if (tf != null) {
		        	customValue.setTypeface(tf);
		        }
		        customValue.setLayoutParams(mLayout);
		        if (xDreamClockColorEnabled) {
		        	customValue.setTextColor(xDreamClockColor);
		        }
		        
		        LinearLayout mLayoutText = (LinearLayout) mContentView.findViewById(res.getIdentifier("main_clock", "id", Black.DESKCLOCK));
		        mLayoutText.addView(customValue);
			}
	     	
	     	 
	         
	 }
	 
	 private static TextView setPadding (TextView tv) {
		 tv.setPadding(0, (int) (paddingRatio * tv.getTextSize()), mPaddingRight,
                 (int) (-bottomPaddingRatio * tv.getTextSize()));
		 return tv;
		 
	 }
	 private static Drawable createImage(final XSharedPreferences prefs) {
			//mContext = NotifPV.getContext();
			Drawable drwImage = null;
			try {
				Context blackContext = mContext.createPackageContext(XblastSettings.PACKAGE_NAME, 0);
				String wallpaperFile = blackContext.getFilesDir() + "/xDreamImage";
				File myFile = new File(wallpaperFile);
				
				if(!myFile.exists())
					return new ColorDrawable();
				
				Bitmap background = BitmapFactory.decodeFile(wallpaperFile);
				drwImage = new BitmapDrawable(mContext.getResources(), background);
				int alpha = prefs.getInt(XblastSettings.PREF_KEY_XDREAM_BG_IMAGE_ALPHA, 100);
				drwImage.setAlpha((int) (2.55 * alpha));
				
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
			return drwImage;
		}
}
