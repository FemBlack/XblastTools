package ind.fem.black.xposed.mods;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class RecentPanelMod {
    public static final String PACKAGE_NAME = "com.android.systemui";
    public static final String CLASS_RECENT_VERTICAL_SCROLL_VIEW = "com.android.systemui.recent.RecentsVerticalScrollView";
    public static final String CLASS_RECENT_HORIZONTAL_SCROLL_VIEW = "com.android.systemui.recent.RecentsHorizontalScrollView";
    public static final String CLASS_RECENT_PANEL_VIEW = "com.android.systemui.recent.RecentsPanelView";

    private static LinearColorBar mRamUsageBar;
    private static Context mContext;
    private static AttributeSet attrs;
    static Handler mhandler=new Handler();
    static TextView mBackgroundProcessText;
    static TextView mForegroundProcessText;
    static TextView mText;
    static ClassLoader classLoaderr;
    static XSharedPreferences prefsLocal;
    public static void init(final XSharedPreferences prefs, ClassLoader classLoader) {
        XposedBridge.log("RecentPanelMod: init");
        prefsLocal = prefs;
        try {
        	classLoaderr = classLoader;
            Class<?> recentPanelViewClass = XposedHelpers.findClass(CLASS_RECENT_PANEL_VIEW, classLoader);
            Class<?> recentVerticalScrollView = XposedHelpers.findClass(CLASS_RECENT_VERTICAL_SCROLL_VIEW, classLoader);
            Class<?> recentHorizontalScrollView = XposedHelpers.findClass(CLASS_RECENT_HORIZONTAL_SCROLL_VIEW, classLoader);
            
            try {
               
                XposedBridge.hookAllConstructors(recentPanelViewClass, new XC_MethodHook() {
                   @Override
                   protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                       mContext = (Context) param.args[0];
                       attrs = (AttributeSet) param.args[1];
                       XposedBridge.log("RecentPanelMod: recentPanelViewClass constructed, mContext set");
                   }
                });
            } catch (Exception e) {
                XposedBridge.log(e);
            }
            XposedHelpers.findAndHookMethod(recentPanelViewClass, "onFinishInflate", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    prefs.reload();
                   
                    XposedBridge.log("RecentPanelMod: RecentsPanelView onFinishInflate");

                    View view = (View) param.thisObject;
                    Resources res = view.getResources();
                    ViewGroup vg = (ViewGroup) view.findViewById(res.getIdentifier("recents_bg_protect", "id", PACKAGE_NAME));

                    

                    // otherwise create and inject new ImageView and set onClick listener to handle action
                    ImageView imgView = new ImageView(vg.getContext());
                    imgView.setImageDrawable(res.getDrawable(res.getIdentifier("ic_notify_clear", "drawable", PACKAGE_NAME)));
                    //int sizeDp = (int)(50 * res.getDisplayMetrics().density);
                    int sizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, res.getDisplayMetrics()); 
                    FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(
                    		sizePx, sizePx, Gravity.BOTTOM | Gravity.RIGHT);
                    /*if (prefs.getBoolean(XblastSettings.PREF_KEY_RAM_BAR_USAGE, false)) {
                    	lParams.topMargin = 40;
                        lParams.rightMargin = 1;
                    } else {
                    	lParams.topMargin = 10;
                        lParams.rightMargin = 10;
                    }*/
                    
                    int clearAllPosition = prefs.getInt("clear_all_position", 1);
                    if (clearAllPosition != 1) {
                    	lParams = new FrameLayout.LayoutParams(
                        		sizePx, sizePx, Gravity.BOTTOM | Gravity.LEFT);
                    }
                    
                    lParams.topMargin = 10;
                    lParams.rightMargin = 10;
                    
                    imgView.setLayoutParams(lParams);
                    imgView.setScaleType(ScaleType.CENTER);
                    imgView.setClickable(true);
                    imgView.setOnClickListener(new View.OnClickListener() {
                        
                        public void onClick(View v) {
                            XposedBridge.log("RecentPanelMod: recents_clear ImageView onClick();");
                            ViewGroup mRecentsContainer = (ViewGroup) XposedHelpers.getObjectField(
                                    param.thisObject, "mRecentsContainer");
                            // passing null parameter in this case is our action flag to remove all views
                            mRecentsContainer.removeViewInLayout(null);
                            mhandler.post(updateRamBarTask);
                        }
                    });
                   
                    if (prefs.getBoolean(XblastSettings.PREF_KEY_RAM_BAR_USAGE, false)) {
	                    mRamUsageBar = createRamBarView();
	                    vg.addView(mRamUsageBar);
	                    mhandler.post(updateRamBarTask);
                    }
                    if (prefs.getBoolean(XblastSettings.PREF_KEY_RECENTS_CLEAR_ALL, false)) {
                    	vg.addView(imgView);
                    }
                    XposedBridge.log("RecentPanelMod: ImageView injected");
                }
            });
            
            try {
            	 XposedHelpers.findAndHookMethod(recentPanelViewClass, "refreshViews", new XC_MethodHook() {
                     @Override
                     protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                         prefs.reload();
                         if (!prefs.getBoolean(XblastSettings.PREF_KEY_RAM_BAR_USAGE, false))
                             return;
                         
                         XposedBridge.invokeOriginalMethod(param.method, param.thisObject, null);
                         mhandler.post(updateRamBarTask);
                     }
                 });
    		} catch (Throwable t) {
    			XposedBridge.log("refreshViews method is not available");
    		}
            
            try {
            	 XposedHelpers.findAndHookMethod(recentPanelViewClass, "handleSwipe", View.class, new XC_MethodHook() {
                     @Override
                     protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                         prefs.reload();
                         if (!prefs.getBoolean(XblastSettings.PREF_KEY_RAM_BAR_USAGE, false))
                             return;
                         
                         XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                         mhandler.post(updateRamBarTask);
                     }
                 });
   		} catch (Throwable t) {
   			XposedBridge.log(t);
   		}
            
           

            // for portrait mode
            XposedHelpers.findAndHookMethod(recentVerticalScrollView, "dismissChild", View.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
                    handleDismissChild(param);
                    mhandler.post(updateRamBarTask);
                }
            });

            // for landscape mode
            XposedHelpers.findAndHookMethod(recentHorizontalScrollView, "dismissChild", View.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
                    handleDismissChild(param);
                    mhandler.post(updateRamBarTask);
                }
            });
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    private static void handleDismissChild(final MethodHookParam param) {
        // skip if non-null view passed - fall back to original method
        if (param.args[0] != null)
            return;

        XposedBridge.log("RecentPanelMod: handleDismissChild - removing all views");

        LinearLayout mLinearLayout = (LinearLayout) XposedHelpers.getObjectField(param.thisObject, "mLinearLayout");
        Handler handler = new Handler();

        int count = mLinearLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = mLinearLayout.getChildAt(i);
            handler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        Object[] newArgs = new Object[1];
                        newArgs[0] = child;
                        XposedBridge.invokeOriginalMethod(param.method, param.thisObject, newArgs);
                    } catch (Exception e) {
                        XposedBridge.log(e);
                    }
                }
                
            }, 150 * i);
        }

        // don't call original method
        param.setResult(null);
    }
    
    private static LinearColorBar createRamBarView() {
    	
    	mBackgroundProcessText = new TextView(mContext, attrs);
        mForegroundProcessText = new TextView(mContext, attrs);
        mText = new TextView(mContext, attrs);
        
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
   		     0, LinearLayout.LayoutParams.WRAP_CONTENT,1);
        
        params1.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        mBackgroundProcessText.setLayoutParams(params1);
        mBackgroundProcessText.setFocusable(true);
        mBackgroundProcessText.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        mBackgroundProcessText.setSingleLine();
        mBackgroundProcessText.setTextColor(Color.BLACK);
        //mBackgroundProcessText.setTextSize(android.R.style.TextAppearance_Small_Inverse);
        
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
      		     0, LinearLayout.LayoutParams.WRAP_CONTENT,1);
        
        mForegroundProcessText.setLayoutParams(params2);
        mForegroundProcessText.setFocusable(true);
        mForegroundProcessText.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        mForegroundProcessText.setSingleLine();
        mForegroundProcessText.setTextColor(Color.BLACK);
        //mForegroundProcessText.setTextSize(android.R.style.TextAppearance_Small_Inverse);
        
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,0);
        params3.setMargins(0, -30, 0, 0);
        mText.setLayoutParams(params3);
        //mText.setFocusable(true);
       // mText.setGravity(Gravity.LEFT);
        mText.setSingleLine();
        mText.setTextColor(LinearColorBar.LEFT_COLOR);
        mText.setText("RAM");
        mText.setTypeface(null, Typeface.BOLD);
        //mText.setTextSize(android.R.style.TextAppearance_Small_Inverse);
        
        
    	LinearColorBar rambarView = new LinearColorBar(mContext, attrs);
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
    		     LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    	params.setMargins(20, -5, 20, 0);
    	rambarView.setLayoutParams(params);
    	rambarView.setOrientation(LinearLayout.HORIZONTAL);
    	rambarView.setClipChildren(false);
    	rambarView.setClipToPadding(false);
    	rambarView.setPadding(4, 30, 4, 1);
    	
    	rambarView.addView(mForegroundProcessText);
    	rambarView.addView(mText);
    	rambarView.addView(mBackgroundProcessText);
		return rambarView;
    }
    
    private final static Runnable updateRamBarTask = new Runnable() {
        public void run() {        	
        	 
             if (!prefsLocal.getBoolean(XblastSettings.PREF_KEY_RAM_BAR_USAGE, false))
                 return;
        	    
                ActivityManager mAm = (ActivityManager)
                        mContext.getSystemService(Context.ACTIVITY_SERVICE);
                ActivityManager.MemoryInfo mMemInfo  = new ActivityManager.MemoryInfo();
               
                mAm.getMemoryInfo(mMemInfo);
              
                long availMem = mMemInfo.availMem;
                long totalMem =  mMemInfo.totalMem;
                
        	    Context blackContext = null;
				try {
					blackContext = mContext.createPackageContext(XblastSettings.PACKAGE_NAME, 0);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					XposedBridge.log(e);
				}
        	    String sizeStr = Formatter.formatShortFileSize(mContext, totalMem-availMem);
                mForegroundProcessText.setText(blackContext.getString(
                        R.string.service_foreground_processes, sizeStr));
                sizeStr = Formatter.formatShortFileSize(mContext, availMem);
                mBackgroundProcessText.setText(blackContext.getString(
                        R.string.service_background_processes, sizeStr));


                float fTotalMem = totalMem;
                float fAvailMem = availMem ;
            /*XposedBridge.log("fTotalMem>>>>>>>>>>>>" + fTotalMem);
            XposedBridge.log("fAvailMem>>>>>>>>>>>>" + fAvailMem);*/
            mRamUsageBar.setRatios((fTotalMem - fAvailMem) / fTotalMem, 0, 0);
        }
    };
}