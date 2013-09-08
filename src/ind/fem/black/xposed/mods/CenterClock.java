package ind.fem.black.xposed.mods;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class CenterClock {
    public static final String PACKAGE_NAME = "com.android.systemui";
    private static final String TAG = "CenterClock";
    private static final String CLASS_PHONE_STATUSBAR = "com.android.systemui.statusbar.phone.PhoneStatusBar";
    private static final String CLASS_TICKER = "com.android.systemui.statusbar.phone.PhoneStatusBar$MyTicker";

    private static ViewGroup mIconArea;
    private static ViewGroup mRootView;
    private static LinearLayout mLayoutClock;
    private static TextView mClock;
    private static Object mPhoneStatusBar;
    private static Context mContext;
    private static int mAnimPushUpOut;
    private static int mAnimPushDownIn;
    private static int mAnimFadeIn;
    private static boolean mClockCentered = false;
    private static int mClockOriginalPaddingLeft;
    private static boolean mClockHide = false;
    //private static boolean mAmPmHide = false;
    
    private static Traffic mTraffic;
    private static LinearLayout mTrafficLayout;
   private static void log(String message) {
        XposedBridge.log(TAG + ": " + message);
        //XposedBridge.log(TAG + ": " + flag);
    }

    private static BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            log("Broadcast received: " + intent.toString());
            if (intent.getAction().equals(XblastSettings.ACTION_PREF_CLOCK_CHANGED)) {
                //setClockPosition(intent.getBooleanExtra(XblastSettings.EXTRA_CENTER_CLOCK, false));
            }
            
            /*if (intent.hasExtra(XblastSettings.EXTRA_AMPM_HIDE)) {
                mAmPmHide = intent.getBooleanExtra(XblastSettings.EXTRA_AMPM_HIDE, false);
                if (mClock != null) {
                    XposedHelpers.callMethod(mClock, "updateClock");
                }
            }*/
            
            if (intent.hasExtra(XblastSettings.EXTRA_CLOCK_HIDE)) {
                mClockHide = intent.getBooleanExtra(XblastSettings.EXTRA_CLOCK_HIDE, false);
                if (mClock != null) {
                	mClock.setVisibility(mClockHide ? View.GONE : View.VISIBLE);
                }
            }
        }
    };

    public static void initResources(final XSharedPreferences prefs, final InitPackageResourcesParam resparam) {
        	log("initResources");
        	
        	//flag = false;
        	
        	int ssbId = resparam.res.getIdentifier("super_status_bar", "layout",
    				"com.android.systemui");
    		int sbId = resparam.res.getIdentifier("status_bar", "layout",
    				"com.android.systemui");
    		
    		int twssbId = resparam.res.getIdentifier("tw_super_status_bar",
    				"layout", "com.android.systemui");
    		int twsbId = resparam.res.getIdentifier("tw_status_bar", "layout",
    				"com.android.systemui");
    		mClockHide = prefs.getBoolean(XblastSettings.PREF_KEY_STATUSBAR_CLOCK_HIDE, false);
    		//mAmPmHide = prefs.getBoolean(XblastSettings.PREF_KEY_STATUSBAR_CLOCK_AMPM_HIDE, false);
    		if (ssbId != 0) {
    			//log("inside super_status_bar>>>");
    		try {
            resparam.res.hookLayout(PACKAGE_NAME, "layout", "super_status_bar", new XC_LayoutInflated() {

                @Override
                public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                	log(" super_status_bar>>>inflated");
                    mIconArea = (ViewGroup) liparam.view.findViewById(
                            liparam.res.getIdentifier("system_icon_area", "id", PACKAGE_NAME));
                    log("super_status_bar>>>system_icon_area" + mIconArea);
                    if (mIconArea == null) {
                    	mIconArea = (ViewGroup) liparam.view.findViewById(
                                liparam.res.getIdentifier("icons", "id", PACKAGE_NAME));
                    	log("super_status_bar>>>icons" + mIconArea);
                    	if (mIconArea == null) {
                    		return;
                    	}
                    }
                    	
                   
                    mRootView = (ViewGroup) mIconArea.getParent().getParent();
                    if (mRootView == null) return;

                    mClock = (TextView) mIconArea.findViewById(
                            liparam.res.getIdentifier("clock", "id", PACKAGE_NAME));
                    if (mClock == null) return;
                   // ModStatusbarColor.setClock(mClock);
                    // use this additional field to identify the instance of Clock that resides in status bar
                    //XposedHelpers.setAdditionalInstanceField(mClock, "sbClock", true);
                    mClockOriginalPaddingLeft = mClock.getPaddingLeft();
                    if (mClockHide) {
                        mClock.setVisibility(View.GONE);
                    }
                   
                    
                    // inject new clock layout
                    mLayoutClock = new LinearLayout(liparam.view.getContext());
                    mLayoutClock.setLayoutParams(new LinearLayout.LayoutParams(
                                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                    mLayoutClock.setGravity(Gravity.CENTER);
                    mLayoutClock.setVisibility(View.GONE);
                    mRootView.addView(mLayoutClock);
                    
                    mTrafficLayout = new LinearLayout(liparam.view.getContext());
                    mTrafficLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                    mTrafficLayout.setGravity(Gravity.CENTER);
                    mRootView.addView(mTrafficLayout);
                    //mTrafficLayout.setVisibility(View.GONE);
                   /* mRootView.addView(mTrafficLayout);
                    
                    mTraffic.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    mTraffic.setLayoutParams(new LinearLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
                    mTraffic.setPadding(mClockOriginalPaddingLeft, 0, 0, 0);
                    mLayoutClock.removeView(mTraffic);
                    mIconArea.addView(mTraffic);*/
                    
                    log("mLayoutClock & mTrafficLayout injected");

                    prefs.reload();
                    
                    //updateClockSettings();
                    //flag = true;
                    setClockPosition(prefs.getBoolean("center_clock", false), resparam);
                    return;
                }
            });
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
    		} 
    		
    		if (twssbId != 0) {	
        		try {
        			//log("inside tw_super_status_bar>>>");
                resparam.res.hookLayout(PACKAGE_NAME, "layout", "tw_super_status_bar", new XC_LayoutInflated() {

                    @Override
                    public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                    	log(" tw_super_status_bar>>>inflated");
                        mIconArea = (ViewGroup) liparam.view.findViewById(
                                liparam.res.getIdentifier("icons", "id", PACKAGE_NAME));
                        log("tw_super_status_bar>>>icons" + mIconArea);
                        if (mIconArea == null) {
                        	mIconArea = (ViewGroup) liparam.view.findViewById(
                                    liparam.res.getIdentifier("system_icon_area", "id", PACKAGE_NAME));
                        	 log("tw_super_status_bar>>>system_icon_area" + mIconArea);
                        	if (mIconArea == null) {
                        		return;
                        	}
                        } 

                        mRootView = (ViewGroup) mIconArea.getParent().getParent();
                        if (mRootView == null) return;

                        mClock = (TextView) mIconArea.findViewById(
                                liparam.res.getIdentifier("clock", "id", PACKAGE_NAME));
                        if (mClock == null) return;

                        //XposedHelpers.setAdditionalInstanceField(mClock, "sbClock", true);
                        mClockOriginalPaddingLeft = mClock.getPaddingLeft();
                        
                        if (mClockHide) {
                            mClock.setVisibility(View.GONE);
                        }
                        
                        // inject new clock layout
                        mLayoutClock = new LinearLayout(liparam.view.getContext());
                        mLayoutClock.setLayoutParams(new LinearLayout.LayoutParams(
                                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                        mLayoutClock.setGravity(Gravity.CENTER);
                        mLayoutClock.setVisibility(View.GONE);
                        mRootView.addView(mLayoutClock);
                        
                        mTrafficLayout = new LinearLayout(liparam.view.getContext());
                        mTrafficLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                        mTrafficLayout.setGravity(Gravity.CENTER);
                        mRootView.addView(mTrafficLayout);
                        log("mLayoutClock & mTrafficLayout injected");

                        prefs.reload();
                        //updateClockSettings();
                        //flag = true;
                        setClockPosition(prefs.getBoolean("center_clock", false), resparam);
                        return;
                    }
                });
            } catch (Throwable e) {
                XposedBridge.log(e);
            }
        		} 
    		//log("black status_bar>>>");
    		if (sbId != 0) {	
    		try {
    			//log("inside status_bar>>>");
                resparam.res.hookLayout(PACKAGE_NAME, "layout", "status_bar", new XC_LayoutInflated() {

                    @Override
                    public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                    	log("status_bar>>>inflated");
                        mIconArea = (ViewGroup) liparam.view.findViewById(
                                liparam.res.getIdentifier("icons", "id", PACKAGE_NAME));
                        log("status_bar>>>parent" + mIconArea);
                        log("status_bar>>>parent" + mIconArea.getParent());
                        log("status_bar>>>parent" + mIconArea.getParent().getParent());
                        if (mIconArea == null) return;

                        mRootView = (ViewGroup) mIconArea.getParent();
                        if (mRootView == null) {
                        	XposedBridge.log("rootview1");
                        	mRootView = (ViewGroup) mIconArea.getParent().getParent();
                        	if (mRootView == null) {
                        		XposedBridge.log("rootview2");
                        		return;
                        	}
                        }
                        	
                        	

                        mClock = (TextView) mIconArea.findViewById(
                                liparam.res.getIdentifier("clock", "id", PACKAGE_NAME));
                        if (mClock == null) {
                        	XposedBridge.log("mClock");
                        	return;
                        }

                        //XposedHelpers.setAdditionalInstanceField(mClock, "sbClock", true);
                        mClockOriginalPaddingLeft = mClock.getPaddingLeft();
                        
                        if (mClockHide) {
                            mClock.setVisibility(View.GONE);
                        }
                        
                        // inject new clock layout
                        mLayoutClock = new LinearLayout(liparam.view.getContext());
                        mLayoutClock.setLayoutParams(new LinearLayout.LayoutParams(
                                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                        mLayoutClock.setGravity(Gravity.CENTER);
                        mLayoutClock.setVisibility(View.GONE);
                        mRootView.addView(mLayoutClock);
                        
                        mTrafficLayout = new LinearLayout(liparam.view.getContext());
                        mTrafficLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                        mTrafficLayout.setGravity(Gravity.CENTER);
                        mRootView.addView(mTrafficLayout);
                        
                        log("mLayoutClock & mTrafficLayout injected");

                        prefs.reload();
                        //updateClockSettings();
                        //flag = true;
                        setClockPosition(prefs.getBoolean("center_clock", false), resparam);
                        return;
                    }
                });
            } catch (Throwable e) {
                XposedBridge.log(e);
            }
    }
    		
    		if (twsbId != 0) {
    			//log("inside tw_status_bar>>>");
        		try {
                    resparam.res.hookLayout(PACKAGE_NAME, "layout", "tw_status_bar", new XC_LayoutInflated() {

                        @Override
                        public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
                        	log(" tw_status_bar>>>inflated");
                            mIconArea = (ViewGroup) liparam.view.findViewById(
                                    liparam.res.getIdentifier("icons", "id", PACKAGE_NAME));
                            log("tw_status_bar>>>parent" + mIconArea);
                            log("tw_status_bar>>>parent" + mIconArea.getParent());
                            log("tw_status_bar>>>parent" + mIconArea.getParent().getParent());
                            if (mIconArea == null) return;

                            mRootView = (ViewGroup) mIconArea.getParent();
                            if (mRootView == null) {
                            	XposedBridge.log("rootview1");
                            	mRootView = (ViewGroup) mIconArea.getParent().getParent();
                            	if (mRootView == null) {
                            		XposedBridge.log("rootview2");
                            		return;
                            	}
                            }
                            	

                            mClock = (TextView) mIconArea.findViewById(
                                    liparam.res.getIdentifier("clock", "id", PACKAGE_NAME));
                            if (mClock == null) {
                            	XposedBridge.log("mClock");
                            	return;
                            }
                            //XposedHelpers.setAdditionalInstanceField(mClock, "sbClock", true);
                            mClockOriginalPaddingLeft = mClock.getPaddingLeft();
                            
                            if (mClockHide) {
                                mClock.setVisibility(View.GONE);
                            }
                            // inject new clock layout
                            mLayoutClock = new LinearLayout(liparam.view.getContext());
                            mLayoutClock.setLayoutParams(new LinearLayout.LayoutParams(
                                            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                            mLayoutClock.setGravity(Gravity.CENTER);
                            mLayoutClock.setVisibility(View.GONE);
                            mRootView.addView(mLayoutClock);
                            
                            mTrafficLayout = new LinearLayout(liparam.view.getContext());
                            mTrafficLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                            mTrafficLayout.setGravity(Gravity.CENTER);
                            mRootView.addView(mTrafficLayout);

                            log("mLayoutClock & mTrafficLayout injected");

                            prefs.reload();
                            //updateClockSettings();
                            //flag = true;
                            setClockPosition(prefs.getBoolean("center_clock", false), resparam);
                            return;
                        }
                    });
                } catch (Throwable e) {
                    XposedBridge.log(e);
                }
        }
    		}

    public static void init(final XSharedPreferences prefs, final ClassLoader classLoader) {
        try {
            final Class<?> phoneStatusBarClass =
                    XposedHelpers.findClass(CLASS_PHONE_STATUSBAR, classLoader);
            final Class<?> tickerClass =
                    XposedHelpers.findClass(CLASS_TICKER, classLoader);
           
            final Class<?>[] loadAnimParamArgs = new Class<?>[2];
            loadAnimParamArgs[0] = int.class;
            loadAnimParamArgs[1] = Animation.AnimationListener.class;

            XposedHelpers.findAndHookMethod(phoneStatusBarClass, "makeStatusBarView", new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    mPhoneStatusBar = param.thisObject;
                    mContext = (Context) XposedHelpers.getObjectField(mPhoneStatusBar, "mContext");
                    Resources res = mContext.getResources();
                    mAnimPushUpOut = res.getIdentifier("push_up_out", "anim", "android");
                    mAnimPushDownIn = res.getIdentifier("push_down_in", "anim", "android");
                    mAnimFadeIn = res.getIdentifier("fade_in", "anim", "android");
                    
                    int tickerColor = prefs.getInt(XblastSettings.PREF_KEY_TICKER_COLOR, 0);
                    boolean tickerColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_TICKER_COLOR_ENABLE, false);
                    if (tickerColorEnabled) {
	                    View mTickerView = (View) XposedHelpers.getObjectField(mPhoneStatusBar, "mTickerView");
	                    ImageSwitcher mIconSwitcher = (ImageSwitcher) mTickerView.findViewById(res.getIdentifier("tickerIcon", "id", PACKAGE_NAME));
	                    ImageView image = (ImageView) mIconSwitcher.getChildAt(0);
	                    image.setColorFilter(tickerColor);
	                    ImageView image1 = (ImageView)mIconSwitcher.getChildAt(1);
	                    image1.setColorFilter(tickerColor);
	                    
	                    TextSwitcher mTextSwitcher = (TextSwitcher) mTickerView.findViewById(res.getIdentifier("tickerText", "id", PACKAGE_NAME));
	                    TextView text = (TextView)mTextSwitcher.getChildAt(0);
	                    text.setTextColor(tickerColor);
	                    TextView text1 = (TextView)mTextSwitcher.getChildAt(1);
	                    text1.setTextColor(tickerColor);
                    }
                    
                    getTraffic(prefs);
                   
                    XposedBridge.log("CenterClock: Traffic, mContext set");
                    
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction(XblastSettings.ACTION_PREF_CLOCK_CHANGED);
                    mContext.registerReceiver(mBroadcastReceiver, intentFilter);
                }
            });

            XposedHelpers.findAndHookMethod(tickerClass, "tickerStarting", new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (mLayoutClock == null || !mClockCentered) return;

                    mLayoutClock.setVisibility(View.GONE);
                    Animation anim = (Animation) XposedHelpers.callMethod(
                            mPhoneStatusBar, "loadAnim", loadAnimParamArgs, mAnimPushUpOut, null);
                    mLayoutClock.startAnimation(anim);
                }
            });

           /* XposedHelpers.findAndHookMethod(phoneStatusBarClass, "showClock", boolean.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (mClock == null) return;

                    boolean visible = (Boolean) param.args[0] && !mClockHide;
                    mClock.setVisibility(visible ? View.VISIBLE : View.GONE);
                }
            });*/
            
            XposedHelpers.findAndHookMethod(tickerClass, "tickerDone", new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (mLayoutClock == null || !mClockCentered) return;

                    mLayoutClock.setVisibility(View.VISIBLE);
                    Animation anim = (Animation) XposedHelpers.callMethod(
                            mPhoneStatusBar, "loadAnim", loadAnimParamArgs, mAnimPushDownIn, null);
                    mLayoutClock.startAnimation(anim);
                }
            });

            XposedHelpers.findAndHookMethod(tickerClass, "tickerHalting", new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (mLayoutClock == null || !mClockCentered) return;

                    mLayoutClock.setVisibility(View.VISIBLE);
                    Animation anim = (Animation) XposedHelpers.callMethod(
                            mPhoneStatusBar, "loadAnim", loadAnimParamArgs, mAnimFadeIn, null);
                    mLayoutClock.startAnimation(anim);
                }
            });
        }
        catch (Throwable e) {
            XposedBridge.log(e);
        }
        
      
    }

    private static void getTraffic(XSharedPreferences prefs) {
    	
    	 if (mIconArea == null || mTrafficLayout == null) {
             return;
         }
    	 
    	 if (prefs.getBoolean(XblastSettings.PREF_KEY_TRAFFIC, false)) {
         	int trafficColor = prefs.getInt(XblastSettings.PREF_KEY_TRAFFIC_COLOR, 0);
             boolean trafficColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_TRAFFIC_COLOR_ENABLE, false);
             mTraffic = new Traffic(mContext);
             if(trafficColorEnabled) {
             	mTraffic.setTextColor(trafficColor);
             }
             mTraffic.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
             mTraffic.setLayoutParams(new LinearLayout.LayoutParams(
                     LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
             mTraffic.setPadding(mClockOriginalPaddingLeft, 0, 0, 0);
             mLayoutClock.removeView(mTraffic);
             mIconArea.addView(mTraffic);
         }
    	
    }
    private static void setClockPosition(boolean center, final InitPackageResourcesParam resparam) {
        if (mClockCentered == center || mClock == null ||
                mIconArea == null || mLayoutClock == null) {
        	log("setClockPosition returned without center clock");
            return;
        }

        if (center) {
            mClock.setGravity(Gravity.CENTER);
            mClock.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            mClock.setPadding(0, 0, 0, 0);
            mIconArea.removeView(mClock);
            ViewGroup vg =  ((ViewGroup)mLayoutClock.getParent());
            View clockView = vg.findViewById(resparam.res.getIdentifier("clock", "id", PACKAGE_NAME));
            log("Existing clock view" + clockView);
            if (clockView != null) {
            	 log("Clock vg" + vg);
            	 mLayoutClock.removeView(mClock);
            	 log("Existing clock view removed");
            }
            mLayoutClock.addView(mClock);
            mLayoutClock.setVisibility(View.VISIBLE);
            log("Clock set to center position");
        } else {
            mClock.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            mClock.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            mClock.setPadding(mClockOriginalPaddingLeft, 0, 0, 0);
            mLayoutClock.removeView(mClock);
            mIconArea.addView(mClock);
            mLayoutClock.setVisibility(View.GONE);
            log("Clock set to normal position");
        }

        mClockCentered = center;
    }
   /* private static final boolean DEBUG = true;
    private static void  updateClockSettings() {
    	
    	XposedHelpers.findAndHookMethod(mClock.getClass(), "getSmallTime", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                // is this a status bar Clock instance?
                // yes, if it contains our additional sbClock field
            	
                Object sbClock = XposedHelpers.getAdditionalInstanceField(param.thisObject, "sbClock");
                if (sbClock != null) {
                    if (mClockHide) {
                    	log("Clock Gone");
                        mClock.setVisibility(View.GONE);
                        return;
                    }
                    
                    mClock.setVisibility(View.VISIBLE);
                    if (DEBUG) log("Is statusbar clock: " + (sbClock == null ? "false" : "true"));
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                    String clockText = param.getResult().toString();
                    if (DEBUG) log("Original clockText: '" + clockText + "'");
                    String amPm = calendar.getDisplayName(
                            Calendar.AM_PM, Calendar.SHORT, Locale.getDefault());
                    if (DEBUG) log("Locale specific AM/PM string: '" + amPm + "'");
                    int amPmIndex = clockText.indexOf(amPm);
                    if (DEBUG) log("Original AM/PM index: " + amPmIndex);
                    if (mAmPmHide && amPmIndex != -1) {
                        clockText = clockText.replace(amPm, "").trim();
                        if (DEBUG) log("AM/PM removed. New clockText: '" + clockText + "'");
                        amPmIndex = -1;
                    } else if (!mAmPmHide
                                && !DateFormat.is24HourFormat(mClock.getContext())
                                && amPmIndex == -1) {
                        // insert AM/PM if missing
                        clockText += " " + amPm;
                        amPmIndex = clockText.indexOf(amPm);
                        if (DEBUG) log("AM/PM added. New clockText: '" + clockText + "'; New AM/PM index: " + amPmIndex);
                    }
                    CharSequence dow = "";
                    // apply day of week only to statusbar clock, not the notification panel clock
                    
                    clockText = dow + clockText;
                    SpannableStringBuilder sb = new SpannableStringBuilder(clockText);
                    sb.setSpan(new RelativeSizeSpan(0.7f), 0, dow.length(),
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    if (amPmIndex > -1) {
                        int offset = Character.isWhitespace(clockText.charAt(dow.length() + amPmIndex - 1)) ?
                                1 : 0;
                        sb.setSpan(new RelativeSizeSpan(0.7f), dow.length() + amPmIndex - offset,
                                dow.length() + amPmIndex + amPm.length(),
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    if (DEBUG) log("Final clockText: '" + sb + "'");
                    param.setResult(sb);
                }
            }
        });
    	
    }*/
    
    
}