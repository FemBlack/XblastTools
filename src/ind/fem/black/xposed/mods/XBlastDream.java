package ind.fem.black.xposed.mods;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;


public class XBlastDream{

	 private static final String TAG = "DayDream";
	 private static final String CLASS_CLOCK_DREAMS = "com.android.deskclock.Screensaver";
	 
	 private static void log(String message) {
	        XposedBridge.log(TAG + ": " + message);
	    }
	 
	 private static Context mContext;
	 private static View mContentView;
	 private static XSharedPreferences prefs;
	 
	 public static void handleLoadPackage(final XSharedPreferences prefs, ClassLoader classLoader) {
	        log("handleLoadPackage");

	        XBlastDream.prefs = prefs;
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
             
	     	 Resources res = mContext.getResources();
	     	 if (xDreamBgColorEnabled) {
	     		mContentView.setBackgroundColor(xDreamBgColor);
	     	 } else {
	     		mContentView.setBackground(createImage(prefs));
	     	 }
	     	 
	     	if (xDreamClockColorEnabled) {
	     		 TextView timeDisplayHours = (TextView) mContentView.findViewById(res.getIdentifier("timeDisplayHours", "id", Black.DESKCLOCK));
		         timeDisplayHours.setTextColor(xDreamClockColor);
		         TextView timeDisplayMinutes = (TextView) mContentView.findViewById(res.getIdentifier("timeDisplayMinutes", "id", Black.DESKCLOCK));
		         timeDisplayMinutes.setTextColor(xDreamClockColor);
		         TextView am_pm = (TextView) mContentView.findViewById(res.getIdentifier("am_pm", "id", Black.DESKCLOCK));
		         am_pm.setTextColor(xDreamClockColor);
	     	 }
	     	 
	         
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
