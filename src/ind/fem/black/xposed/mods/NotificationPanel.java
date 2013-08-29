package ind.fem.black.xposed.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XResources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class NotificationPanel {
	
	private static final String TAG = "NotificationPanel";
    public static final String PACKAGE_NAME = "android";
    /*private static XSharedPreferences prefs;
	private static InitPackageResourcesParam resparam;
	private static XModuleResources moduleResources;*/
	
	  private static void log(String message) {
	        XposedBridge.log(TAG + ": " + message);
	    }
	
	public static void notificationTextColor(final XSharedPreferences prefs) {
			
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
							toastView.setTextColor(toastTextColor);
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
	    
	   /* public static void doHook(XSharedPreferences prefs,
				InitPackageResourcesParam resparam, XModuleResources moduleResources) {
	    	NotificationPanel.prefs = prefs;
	    	NotificationPanel.resparam = resparam;
	    	NotificationPanel.moduleResources = moduleResources;
	    	//setCloseHandleBar();
	    }*/
	    
	    /*private static void setCloseHandleBar() {
			resparam.res.setReplacement(Black.SYSTEM_UI, "drawable",
					"status_bar_close", new XResources.DrawableLoader() {
						@Override
						public Drawable newDrawable(XResources res, int id)
								throws Throwable {

							Drawable[] layers = new Drawable[2];
							if (prefs.getBoolean(
									"notificationHandleBackgroundColorEnabled",
									true)) {
								layers[0] = new ColorDrawable(prefs.getInt(
										"notificationHandleBackgroundColor",
										Color.RED));
							} else {
								layers[0] = new ColorDrawable(Color.BLACK);
							}
							Drawable closeOn = moduleResources
									.getDrawable(ind.fem.black.xposed.mods.R.drawable.status_bar_close);
							closeOn.setColorFilter(
									prefs.getInt("notificationHandleColor",
											Color.GREEN),
									PorterDuff.Mode.MULTIPLY);
							layers[1] = closeOn;

							return new LayerDrawable(layers);
						}
					});*/
			/*if (prefs.getBoolean("notificationHandleBackgroundColorEnabled", false)) {
				resparam.res.setReplacement(Black.SYSTEM_UI, "drawable",
						"close_handler_divider", new XResources.DrawableLoader() {
							@Override
							public Drawable newDrawable(XResources res, int id)
									throws Throwable {
								return new ColorDrawable(prefs.getInt(
										"notificationHandleBackgroundColor",
										Color.BLACK));
							}
						});
			}*/

		//}
	
}
