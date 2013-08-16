package ind.fem.black.xposed.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class NotificationPanel {
	
	private static final String TAG = "NotificationPanel";
    public static final String PACKAGE_NAME = "android";
    
	  private static void log(String message) {
	        XposedBridge.log(TAG + ": " + message);
	    }
	  
	  public static void initZygote1(XSharedPreferences prefs) {
			log("initZygote");
			
			/*final boolean nbgEnabled = prefs.getBoolean("nbgEnabled", false);
			final int ngbColor = prefs.getInt(XblastSettings.CONST_NGB_COLOR, -1);*/
			/*try {
				//final Class<?> notifClass = Class.forName("android.app.Notification");
				XposedHelpers.findAndHookMethod(Notification.class, "makeContentView", new XC_MethodHook() {
							@Override
							protected void afterHookedMethod(MethodHookParam param)
									throws Throwable {
								try {
									log("mContentView");
									View mContentView = (View)param.thisObject;
									log("mContentView" + mContentView);
									Resources res = mContentView.getResources();
									 	if(true) {
									 		TextView title = (TextView) mContentView.findViewById(res.getIdentifier(
							                        "title", "id", "android"));
									 		log("title" + title);
									 		title.setTextColor(Color.RED);
									 	} 
									 	log("makeContentView completed");
								} catch (Throwable t) {
									XposedBridge.log(t);
								}
								
							}
						});
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
			*/
			
			/*try {
				findAndHookMethod(Notification.class, "setLatestEventInfo" , Context.class, CharSequence.class, CharSequence.class, PendingIntent.class, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						log("mContentView");
						//Context mContext = (Context) getObjectField(param.thisObject, "mContext");
						RemoteViews contentView = (RemoteViews) getObjectField(param.thisObject, "contentView");
						CharSequence contentTitle = (CharSequence) getObjectField(param.thisObject, "contentTitle");
						CharSequence contentText = (CharSequence) getObjectField(param.thisObject, "contentText");
						log("contentView" + contentView);
						log("contentTitle" + contentTitle);
						log("contentText" + contentText);
						
						//Resources res = mContext.getResources();
						 	if(true) {
						 		TextView title = (TextView) mContentView.findViewById(res.getIdentifier(
				                        "title", "id", "android"));
						 		log("title" + title);
						 		title.setTextColor(Color.RED);
						 	} 
						 	log("makeContentView completed");
					}
				});
			} catch (Throwable t) {
				XposedBridge.log(t);
			}*/
			
			try {
				findAndHookMethod(Notification.Builder.class, "buildUnstyled", new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						log("mContentView");
						Context mContext = (Context) getObjectField(param.thisObject, "mContext");
						RemoteViews mContentView = (RemoteViews) getObjectField(param.thisObject, "mContentView");
						RemoteViews mTickerView = (RemoteViews) getObjectField(param.thisObject, "mTickerView");
						log("mContentView" + mContentView);
						Resources res = mContext.getResources();
						 	/*if(true) {
						 		TextView title = (TextView) mContentView.findViewById(res.getIdentifier(
				                        "title", "id", "android"));
						 		log("title" + title);
						 		title.setTextColor(Color.RED);
						 	} */
						 	log("makeContentView completed");
					}
				});
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
			
		

		}
	    public static void initZygote(XSharedPreferences prefs) {
	        log("handlePackage");
	      
			try {
				
				//XModuleResources modRes = XModuleResources.createInstance(path, resparam.res);
				//final Drawable d= modRes.getDrawable(R.drawable.local_ic_lockscreen_glowdot);
				final int nbgNormalColor = prefs.getInt(XblastSettings.PREF_KEY_NOTIF_NORMAL_COLOR, Color.WHITE);
				final int nbgPressedColor = prefs.getInt(XblastSettings.PREF_KEY_NOTIF_PRESSED_COLOR, Color.WHITE);
				if (prefs.getBoolean(XblastSettings.PREF_KEY_NOTIF_NORMAL_COLOR_ENABLE, false)) {
					XResources.setSystemWideReplacement("android", "drawable", "notification_bg_normal",new XResources.DrawableLoader() {
						@Override
						public Drawable newDrawable(XResources res, int id) throws Throwable {
							return new ColorDrawable(nbgNormalColor);
						}
					});
					
					XResources.setSystemWideReplacement("android", "drawable", "notification_bg_low_normal",new XResources.DrawableLoader() {
						@Override
						public Drawable newDrawable(XResources res, int id) throws Throwable {
							return new ColorDrawable(nbgNormalColor);
						}
					});
				}
				
				if (prefs.getBoolean(XblastSettings.PREF_KEY_NOTIF_PRESSED_COLOR_ENABLE, false)) {
					XResources.setSystemWideReplacement("android", "drawable", "notification_bg_normal_pressed",new XResources.DrawableLoader() {
						@Override
						public Drawable newDrawable(XResources res, int id) throws Throwable {
							return new ColorDrawable(nbgPressedColor);
						}
					});
					XResources.setSystemWideReplacement("android", "drawable", "notification_bg_low_pressed",new XResources.DrawableLoader() {
						@Override
						public Drawable newDrawable(XResources res, int id) throws Throwable {
							return new ColorDrawable(nbgPressedColor);
						}
					});
				}
				
			} catch (Throwable t) {
				log("Error in NotificationPanel");
			}
			
			/*int sblecId = resparam.res.getIdentifier("status_bar_latest_event_content","layout", "android");
			

			log("status_bar_latest_event_content>>>" + sblecId);
			
			
			
			if (sblecId != 0) {
				
				try {
					resparam.res.hookLayout("android", "layout",
							"status_bar_latest_event_content", new XC_LayoutInflated() {
								@Override
								public void handleLayoutInflated(
										LayoutInflatedParam liparam)
										throws Throwable {
									log(" status_bar_latest_event_content>>>inflated");
											
										View latestEvent = (View) liparam.view
												.findViewById(liparam.res.getIdentifier(
														"notification_template_base", "id", "android"));
										if (latestEvent != null) {
											if(true) {
												TextView title = (TextView) latestEvent.findViewById(liparam.res.getIdentifier(
														"title", "id", "com.android.systemui"));
												title.setTextColor(Color.BLUE);
										 	} 
										}
									
							
									log("status_bar_latest_event_content completed");
									return;
								}
							});
					
					resparam.res.hookLayout("android", "layout",
							"notification_template_base", new XC_LayoutInflated() {
								@Override
								public void handleLayoutInflated(
										LayoutInflatedParam liparam)
										throws Throwable {
									log(" notification_template_base>>>inflated");
											
										View latestEvent = (View) liparam.view
												.findViewById(liparam.res.getIdentifier(
														"notification_template_base", "id", "android"));
										if (latestEvent != null) {
											if(true) {
												TextView title = (TextView) latestEvent.findViewById(liparam.res.getIdentifier(
														"title", "id", "com.android.systemui"));
												title.setTextColor(Color.BLUE);
										 	} 
										}
									
							
									log("notification_template_base completed");
									return;
								}
							});
				} catch (Throwable t) {
					log("super_status_bar is not available");
					XposedBridge.log(t);
				}
			} */
			
			log("Completed");

	    }
	    
	   
}
