package ind.fem.black.xposed.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XResources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class NotificationPanel {
	
	private static final String TAG = "NotificationPanel";
    public static final String PACKAGE_NAME = "android";
    /*private static XSharedPreferences prefs;
	private static InitPackageResourcesParam resparam;
	private static XModuleResources moduleResources;*/
	
	  private static void log(String message) {
	        XposedBridge.log(TAG + ": " + message);
	    }
	
	/*public static void notificationTextColor1(final XSharedPreferences prefs) {
			
			try {
				findAndHookMethod(Notification.class, "setLatestEventInfo" , Context.class, CharSequence.class, CharSequence.class, PendingIntent.class, new XC_MethodHook() {
					
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						log("setLatestEventInfo");
						Context mContext = (Context) param.args[0];
						
						RemoteViews contentView = (RemoteViews) getObjectField(param.thisObject, "contentView");
						Resources res = mContext.getResources();
						int titleId = res.getIdentifier("title", "id", "android");
						int textId = res.getIdentifier("text", "id", "android");
						int infoId = res.getIdentifier("info", "id", "android");
						int timeId = res.getIdentifier("time", "id", "android");
						
						Notification n = (Notification) param.thisObject;
						int titleColor = prefs.getInt(XblastSettings.PREF_KEY_NOTIF_TITLE_COLOR, 0);
						boolean titleColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_NOTIF_TITLE_COLOR_ENABLE, false);
						int contentColor = prefs.getInt(XblastSettings.PREF_KEY_NOTIF_CONTENT_COLOR, 0);
						boolean contentColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_NOTIF_CONTENT_COLOR_ENABLE, false);
						if (titleColorEnabled) {
							contentView.setTextColor(titleId, titleColor);
							contentView.setTextColor(textId, titleColor);
							if(n.when > 0) {
								contentView.setTextColor(timeId, titleColor);
							}
						}
						if (contentColorEnabled) {
							contentView.setTextColor(infoId, contentColor);	
						}
						
						log("setLatestEventInfo completed");
					}
				});
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
			
			
		}*/
	
	  public static void handleInit(InitPackageResourcesParam resparam, final XSharedPreferences prefs) {
		try {

			boolean removehandle = prefs.getBoolean("removehandle", false);

			if (removehandle) {
				resparam.res.setReplacement(Black.SYSTEM_UI, "dimen",
						"close_handle_height", Xmod.modRes.fwd(R.dimen.zero));
			}

		} catch (Exception e) {
			log(e.toString());
		}
		}
	  
	  public static void toastColor(final XSharedPreferences prefs) {
		 try {
				findAndHookMethod(Toast.class, "show" , new XC_MethodHook() {
					
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						//log("show");
						Context mContext = (Context) getObjectField(param.thisObject, "mContext");
						View view = (View) getObjectField(param.thisObject, "mNextView");
						Resources res = mContext.getResources();
						int messageId = res.getIdentifier("message", "id", "android");
						
						int toastTextColor = prefs.getInt(XblastSettings.PREF_KEY_TOAST_TEXT_COLOR, 0);
						boolean toastTextColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_TOAST_TEXT_COLOR_ENABLE, false);
						int toastBgColor = prefs.getInt(XblastSettings.PREF_KEY_TOAST_BG_COLOR, 0);
						boolean toastBgColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_TOAST_BG_COLOR_ENABLE, false);
						
						if (toastBgColorEnabled) {
							view.setBackgroundColor(toastBgColor);
						}
						
						if (toastTextColorEnabled) {
							TextView toastView = (TextView) view.findViewById(messageId);
							if (toastView != null) {
								toastView.setTextColor(toastTextColor);
							}
						}
						
						//log("show completed");
					}
				});
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
	 }
	 
	    public static void initZygote(XSharedPreferences prefs) {
	        log("initZygote");
	        
			try {
				notificationTextColor(prefs);
				toastColor(prefs);
				final int nbgNormalColor = prefs.getInt(XblastSettings.PREF_KEY_NOTIF_NORMAL_COLOR, 0);
				final boolean nbgNormalColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_NOTIF_NORMAL_COLOR_ENABLE, false);
				final int nbgPressedColor = prefs.getInt(XblastSettings.PREF_KEY_NOTIF_PRESSED_COLOR, 0);
				final boolean nbgPressedColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_NOTIF_PRESSED_COLOR_ENABLE,false);
				if (nbgNormalColorEnabled) {
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
					XResources.setSystemWideReplacement("android", "drawable", "notification_template_icon_bg",new XResources.DrawableLoader() {
						@Override
						public Drawable newDrawable(XResources res, int id) throws Throwable {
							return new ColorDrawable(nbgNormalColor);
						}
					});
					XResources.setSystemWideReplacement("android", "drawable", "notification_template_icon_low_bg",new XResources.DrawableLoader() {
						@Override
						public Drawable newDrawable(XResources res, int id) throws Throwable {
							return new ColorDrawable(nbgNormalColor);
						}
					});
				}
				
				if (nbgPressedColorEnabled) {
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
			
			log("Completed");

	    }
	    
	    public static void notificationTextColor(final XSharedPreferences prefs) {
	    	try {
				final int sdk = Build.VERSION.SDK_INT;
				final int titleColor = prefs.getInt(XblastSettings.PREF_KEY_NOTIF_TITLE_COLOR, 0);
				final boolean titleColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_NOTIF_TITLE_COLOR_ENABLE, false);
				final int contentColor = prefs.getInt(XblastSettings.PREF_KEY_NOTIF_CONTENT_COLOR, 0);
				final boolean contentColorEnabled = prefs.getBoolean(XblastSettings.PREF_KEY_NOTIF_CONTENT_COLOR_ENABLE, false);
				
				XC_MethodHook notifyHook = new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						

						Notification n;
						if (sdk <= 15 || sdk >= 18)
							n = (Notification) param.args[6];
						else
							n = (Notification) param.args[5];

						Context mContext = (Context) getObjectField(param.thisObject, "mContext");
						
						Resources res = mContext.getResources();
						int titleId = res.getIdentifier("title", "id", "android");
						int textId = res.getIdentifier("text", "id", "android");
						int infoId = res.getIdentifier("info", "id", "android");
						int timeId = res.getIdentifier("time", "id", "android");
						int textId2 = res.getIdentifier("text2", "id", "android");
						int inbox_more = res.getIdentifier("inbox_more", "id", "android");
						int inbox_text0 = res.getIdentifier("inbox_text0", "id", "android");
						int inbox_text1 = res.getIdentifier("inbox_text1", "id", "android");
						int inbox_text2 = res.getIdentifier("inbox_text2", "id", "android");
						int inbox_text3 = res.getIdentifier("inbox_text3", "id", "android");
						int inbox_text4 = res.getIdentifier("inbox_text4", "id", "android");
						int inbox_text5 = res.getIdentifier("inbox_text5", "id", "android");
						int inbox_text6 = res.getIdentifier("inbox_text6", "id", "android");
						//int right_icon = res.getIdentifier("icon", "id", "android");
						
						int big_text = res.getIdentifier("big_text", "id", "android");
						
							try {
								RemoteViews bigContentView = (RemoteViews) getObjectField(n, "bigContentView");
								RemoteViews contentView = (RemoteViews) getObjectField(n, "contentView");
								/*if (n.largeIcon != null) {
									Bitmap mutableBitmap = n.largeIcon.copy(Bitmap.Config.ARGB_8888, true);
									mutableBitmap.eraseColor(Color.RED);
									n.largeIcon = mutableBitmap;
								}*/
								
								//Drawable drawable = res.getDrawable(n.icon);
								
								if (contentView != null ) {
									
									if (titleColorEnabled) {
										contentView.setTextColor(titleId, titleColor);
										contentView.setTextColor(textId, titleColor);
										contentView.setTextColor(textId2, titleColor);
										if(n.when > 0) {
											contentView.setTextColor(timeId, titleColor);
										}
									}
									if (contentColorEnabled) {
										//contentView.setTextColor(infoId, contentColor);
										contentView.setTextColor(infoId, contentColor);
										contentView.setTextColor(inbox_more, contentColor);
										contentView.setTextColor(inbox_text0, contentColor);
										contentView.setTextColor(big_text, contentColor);
										
										contentView.setTextColor(inbox_text0, contentColor);
										contentView.setTextColor(inbox_text1, contentColor);
										contentView.setTextColor(inbox_text2, contentColor);
										contentView.setTextColor(inbox_text3, contentColor);
										contentView.setTextColor(inbox_text4, contentColor);
										contentView.setTextColor(inbox_text5, contentColor);
										contentView.setTextColor(inbox_text6, contentColor);
									}
									
									
									
									
									/*drawable.setColorFilter(Color.RED,Mode.SRC_IN);
									Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
									contentView.setImageViewBitmap(n.icon, bitmap);*/
									
								}
								if (bigContentView != null ) {
									
									if (titleColorEnabled) {
										bigContentView.setTextColor(titleId, titleColor);
										bigContentView.setTextColor(textId, titleColor);
										bigContentView.setTextColor(textId2, titleColor);
										if(n.when > 0) {
											bigContentView.setTextColor(timeId, titleColor);
										}
									}
									if (contentColorEnabled) {
										//bigContentView.setTextColor(infoId, contentColor);
										bigContentView.setTextColor(infoId, contentColor);
										bigContentView.setTextColor(inbox_more, contentColor);
										bigContentView.setTextColor(inbox_text0, contentColor);
										bigContentView.setTextColor(big_text, contentColor);
										
										bigContentView.setTextColor(inbox_text0, contentColor);
										bigContentView.setTextColor(inbox_text1, contentColor);
										bigContentView.setTextColor(inbox_text2, contentColor);
										bigContentView.setTextColor(inbox_text3, contentColor);
										bigContentView.setTextColor(inbox_text4, contentColor);
										bigContentView.setTextColor(inbox_text5, contentColor);
										bigContentView.setTextColor(inbox_text6, contentColor);
									}
									
									
								}
								/*log("bigContentView>>>>" + bigContentView);
								log("contentView" + contentView);*/
							} catch (Exception e) {
								XposedBridge.log(e);
							}
						
					}
				};
				if (sdk <= 15) {
					findAndHookMethod("com.android.server.NotificationManagerService", null, "enqueueNotificationInternal", String.class, int.class, int.class,
							String.class, int.class, int.class, Notification.class, int[].class,
							notifyHook);
				} else if (sdk == 16) {
					findAndHookMethod("com.android.server.NotificationManagerService", null, "enqueueNotificationInternal", String.class, int.class, int.class,
							String.class, int.class, Notification.class, int[].class,
							notifyHook);
				} else if (sdk == 17) {
					findAndHookMethod("com.android.server.NotificationManagerService", null, "enqueueNotificationInternal", String.class, int.class, int.class,
							String.class, int.class, Notification.class, int[].class, int.class,
							notifyHook);
				} else if (sdk >= 18) {
					findAndHookMethod("com.android.server.NotificationManagerService", null, "enqueueNotificationInternal", String.class, String.class,
							int.class, int.class, String.class, int.class, Notification.class, int[].class, int.class,
							notifyHook);
				}
			} catch (Throwable t) {
				XposedBridge.log(t);
			}
	    }
}
