package ind.fem.black.xposed.mods;

import ind.fem.black.xposed.adapters.BasicIconListItem;
import ind.fem.black.xposed.adapters.IIconListAdapterItem;
import ind.fem.black.xposed.adapters.IconListAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.Unhook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class AdvRebootMenu {
    private static final String TAG = "AdvRebootMenu";
    public static final String PACKAGE_NAME = "android";
    public static final String CLASS_GLOBAL_ACTIONS = "com.android.internal.policy.impl.GlobalActions";
    public static final String CLASS_ACTION = "com.android.internal.policy.impl.GlobalActions.Action";

    private static Context mContext;
    private static String mRebootStr;
    
    private static Drawable rebootIcon;
    private static Drawable softRebootIcon;
    private static Drawable recoveryIcon;
    private static List<IIconListAdapterItem> mRebootItemList;
   
    private static Unhook mRebootActionHook;
    private static Drawable ssIcon;
    private static Drawable fastbootIcon;

    private static void log(String message) {
        XposedBridge.log(TAG + ": " + message);
    }

    public static void init(final XSharedPreferences prefs, final ClassLoader classLoader) {
    	log("init");
        try {
            final Class<?> globalActionsClass = XposedHelpers.findClass(CLASS_GLOBAL_ACTIONS, classLoader);
            final Class<?> actionClass = XposedHelpers.findClass(CLASS_ACTION, classLoader);
            try {
            XposedBridge.hookAllConstructors(globalActionsClass, new XC_MethodHook() {
               @Override
               protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
            	   log("initConstructors");
                   mContext = (Context) param.args[0];
                   Context blackContext = mContext.createPackageContext(
                		   XblastSettings.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
                   Resources res = mContext.getResources();
                   Resources xModRes = blackContext.getResources();
                   log("hookAllConstructors");
                   int softBootStrId = xModRes.getIdentifier("soft_reboot", "string", PACKAGE_NAME);
                   int rebootStrId = res.getIdentifier("factorytest_reboot", "string", PACKAGE_NAME);
                   int recoveryStrId = xModRes.getIdentifier("poweroff_recovery", "string", XblastSettings.PACKAGE_NAME);
                   int ssStrId = xModRes.getIdentifier("screenshot", "string", XblastSettings.PACKAGE_NAME);
                   int fastbootStrId = xModRes.getIdentifier("fastboot", "string", XblastSettings.PACKAGE_NAME);
                   
                   log("rebootStrId" + rebootStrId);
                   
                   final String[] items = new String[5];
                   items[0] = (softBootStrId == 0) ? "Soft Reboot" : res.getString(softBootStrId);
                   items[1] = (rebootStrId == 0) ? "Reboot" : res.getString(rebootStrId);
                   items[2] = (recoveryStrId == 0) ? "Recovery" : xModRes.getString(recoveryStrId);
                   items[3] = (fastbootStrId == 0) ? "Fastboot" : xModRes.getString(fastbootStrId);
                   items[4] = (ssStrId == 0) ? "Screenshot" : xModRes.getString(ssStrId);
                   
                   mRebootStr = items[1];
                   
                   softRebootIcon = xModRes.getDrawable(
                           xModRes.getIdentifier("ic_lock_expanded_desktop", "drawable", XblastSettings.PACKAGE_NAME));
                   //setColorFilter(softRebootIcon);
                   rebootIcon = xModRes.getDrawable(
                           xModRes.getIdentifier("ic_lock_reboot", "drawable", XblastSettings.PACKAGE_NAME));
                  // setColorFilter(rebootIcon);
                   recoveryIcon = xModRes.getDrawable(
                           xModRes.getIdentifier("ic_lock_recovery", "drawable", XblastSettings.PACKAGE_NAME));
                  // setColorFilter(recoveryIcon);
                   ssIcon = xModRes.getDrawable(
                           xModRes.getIdentifier("ic_lock_screenshot", "drawable", XblastSettings.PACKAGE_NAME));
                  // setColorFilter(ssIcon);
                   fastbootIcon = xModRes.getDrawable(
                           xModRes.getIdentifier("ic_lock_reboot_bootloader", "drawable", XblastSettings.PACKAGE_NAME));
                   //setColorFilter(fastbootIcon);
                   log("reboot icon constructed");
                   mRebootItemList = new ArrayList<IIconListAdapterItem>();
                   mRebootItemList.add(new BasicIconListItem(items[0], null, softRebootIcon, null));
                   mRebootItemList.add(new BasicIconListItem(items[1], null, rebootIcon, null));
                   mRebootItemList.add(new BasicIconListItem(items[2], null, recoveryIcon, null));
                   mRebootItemList.add(new BasicIconListItem(items[3], null, fastbootIcon, null));
                   mRebootItemList.add(new BasicIconListItem(items[4], null, ssIcon, null));

                  /* mRebootConfirmStr = xModRes.getString(xModRes.getIdentifier(
                           "reboot_confirm", "string", Xmod.PACKAGE_NAME));
                   mRebootConfirmRecoveryStr = xModRes.getString(xModRes.getIdentifier(
                           "reboot_confirm_recovery", "string", Xmod.PACKAGE_NAME));*/
                   
                   log("GlobalActions constructed, resources set.");
               }
            });
            } catch (Exception e) {
            	 log("GlobalActions error.");
                XposedBridge.log(e);
            }
            XposedHelpers.findAndHookMethod(globalActionsClass, "createDialog", new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
                    if (mRebootActionHook != null) {
                        log("Unhooking previous hook of reboot action item");
                        mRebootActionHook.unhook();
                        mRebootActionHook = null;
                    }
                }

                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    if (mContext == null) return;

                    prefs.reload();
                    if (!prefs.getBoolean("advanced_reboot_menu", false)) {
                        return;
                    }

                    @SuppressWarnings("unchecked")
                    List<Object> mItems = (List<Object>) XposedHelpers.getObjectField(param.thisObject, "mItems");

                    // try to find out if reboot action item already exists in the list of GlobalActions items
                    // strategy:
                	// 1) check if Action has mIconResId field
                    // 2) check if the name of the corresponding resource contains "reboot" substring
                    log("Searching for existing reboot action item...");
                    Object rebootActionItem = null;
                    Resources res = mContext.getResources();
                    for (Object o : mItems) {
                        // search for drawable
                        try {
                            Field f = XposedHelpers.findField(o.getClass(), "mIconResId");
                            String resName = res.getResourceEntryName((Integer) f.get(o)).toLowerCase(Locale.US);
                            log("Drawable resName = " + resName);
                            if (resName.contains("reboot") || resName.contains("restart")) {
                                rebootActionItem = o;
                                break;
                            }
                        } catch (NoSuchFieldError nfe) {
                            // continue
                        } catch (Resources.NotFoundException resnfe) {
                            // continue
                        } catch (IllegalArgumentException iae) {
                            // continue
                        }

                        // search for text
                        try {
                            Field f = XposedHelpers.findField(o.getClass(), "mMessageResId");
                            String resName = res.getResourceEntryName((Integer) f.get(o)).toLowerCase(Locale.US);
                            log("Text resName = " + resName);
                            if (resName.contains("reboot") || resName.contains("restart")) {
                                rebootActionItem = o;
                                break;
                            }
                        } catch (NoSuchFieldError nfe) {
                            // continue
                        } catch (Resources.NotFoundException resnfe) {
                            // continue
                        } catch (IllegalArgumentException iae) {
                            // continue
                        }
                    }



                    if (rebootActionItem != null) {
                        log("Existing Reboot action item found! Replacing onPress()");
                        mRebootActionHook = XposedHelpers.findAndHookMethod(rebootActionItem.getClass(),
                                "onPress", new XC_MethodReplacement () {
                            @Override
                            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                                showDialog();
                                return null;
                            }
                        });
                    } else {
                        log("Existing Reboot action item NOT found! Adding new RebootAction item");
                        Object action = Proxy.newProxyInstance(classLoader, new Class<?>[] { actionClass },
                                new RebootAction());
                        log("Reboot action : " + action);
                        // add to the second position
                        mItems.add(1, action);
                        BaseAdapter mAdapter = (BaseAdapter) XposedHelpers.getObjectField(param.thisObject, "mAdapter");
                        mAdapter.notifyDataSetChanged();
                        log("Reboot action Finished ");
                    }
                }
            });
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    private static void showDialog() {
        if (mContext == null) {
            log("mContext is null - aborting");
            return;
        }

        try {
            log("about to build reboot dialog");

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(mRebootStr)
                .setAdapter(new IconListAdapter(mContext, mRebootItemList), new DialogInterface.OnClickListener() {
                    
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        log("onClick() item = " + which);
                        switch (which) {
                        case 4: // power off - fall back to original method
                            try {
                            	takeScreenshot();
                            } catch (Exception e) {
                                XposedBridge.log(e);
                            }
                            break;
                        default: // reboot or reboot to recovery
                        	handleReboot(mContext, mRebootStr, which);
                    }
                        
                    }
                })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
    
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                });
            AlertDialog dialog = builder.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
            dialog.show();
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    private static void handleReboot(Context context, String caption, final int mode) {
        try {
            final PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            Resources gbRes = mContext.createPackageContext(
            		XblastSettings.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY).getResources();
            String message = "";
            if (mode == 0) {
                message = gbRes.getString(gbRes.getIdentifier(
                        "soft_reboot_confirm", "string", XblastSettings.PACKAGE_NAME));
            } else if (mode == 1) {
                message = gbRes.getString(gbRes.getIdentifier(
                        "reboot_confirm", "string", XblastSettings.PACKAGE_NAME));
            } else if (mode == 2) {
                message = gbRes.getString(gbRes.getIdentifier(
                        "reboot_confirm_recovery", "string", XblastSettings.PACKAGE_NAME));
            } else if (mode == 3) {
                message = gbRes.getString(gbRes.getIdentifier(
                        "fastboot_confirm_recovery", "string", XblastSettings.PACKAGE_NAME));
            }
            
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(caption)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //pm.reboot((mode == 1) ? null : "recovery");
                        if (mode == 0) {
                        	softReboot();
                        } else if (mode == 1) {
                        	pm.reboot(null);
                        } else if (mode == 2) {
                        	pm.reboot("recovery");
                        } else if (mode == 3) {
                        	pm.reboot("bootloader");
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            AlertDialog dialog = builder.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
            dialog.show();
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
    }

    public static String softReboot() {
		return CallerImageActivity.executeScript("soft_reboot.sh");
	}
    
    private static class RebootAction implements InvocationHandler {
        private Context mContext;

        public RebootAction() {
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            log("methodName : " + methodName);
            if (methodName.equals("create")) {
            	 log("inside Create");
                mContext = (Context) args[0];
                Resources res = mContext.getResources();
                LayoutInflater li = (LayoutInflater) args[3];
                int layoutId = res.getIdentifier(
                        "global_actions_item", "layout", "android");
                View v = li.inflate(layoutId, (ViewGroup) args[2], false);

                ImageView icon = (ImageView) v.findViewById(res.getIdentifier(
                        "icon", "id", "android"));
                icon.setImageDrawable(rebootIcon);

                TextView messageView = (TextView) v.findViewById(res.getIdentifier(
                        "message", "id", "android"));
                messageView.setText(mRebootStr);

                TextView statusView = (TextView) v.findViewById(res.getIdentifier(
                        "status", "id", "android"));
                statusView.setVisibility(View.GONE);
                log("Exit Create");
                return v;
            } else if (methodName.equals("onPress")) {
            	log("inside onPress");
                showDialog();
                log("Exit onPress");
                return null;
            } else if (methodName.equals("onLongPress")) {
                handleReboot(mContext, mRebootStr, 0);
                return true;
            } else if (methodName.equals("showDuringKeyguard")) {
                return true;
            } else if (methodName.equals("showBeforeProvisioning")) {
                return true;
            } else if (methodName.equals("isEnabled")) {
                return true;
            } else {
                return null;
            }
        }
        
    }
    
    /*private static void setColorFilter(Drawable icon) {
		 icon.setColorFilter(Color.rgb(255, 79, 79), Mode.SRC_IN);
	 }*/
    
    final static Object mScreenshotLock = new Object();
    static ServiceConnection mScreenshotConnection = null;

    final Runnable mScreenshotTimeout = new Runnable() {
        public void run() {
            synchronized (mScreenshotLock) {
                if (mScreenshotConnection != null) {
                    mContext.unbindService(mScreenshotConnection);
                    mScreenshotConnection = null;
                }
            }
        }
    };
    
    private static void takeScreenshot() {
        synchronized (mScreenshotLock) {
            if (mScreenshotConnection != null) {
                return;
            }
            ComponentName cn = new ComponentName("com.android.systemui",
                    "com.android.systemui.screenshot.TakeScreenshotService");
            Intent intent = new Intent();
            intent.setComponent(cn);
            ServiceConnection conn = new ServiceConnection() {
                public void onServiceConnected(ComponentName name, IBinder service) {
                    synchronized (mScreenshotLock) {
                        if (mScreenshotConnection != this) {
                            return;
                        }
                        
                        Messenger messenger = new Messenger(service);
                        Message msg = Message.obtain(null, 1);
                        final ServiceConnection myConn = this;
                        mContext.getMainLooper();
						Handler h = new Handler( Looper.myLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                synchronized (mScreenshotLock) {
                                    if (mScreenshotConnection == myConn) {
                                        mContext.unbindService(mScreenshotConnection);
                                        mScreenshotConnection = null;
                                       // mContext.removeCallbacks(mScreenshotTimeout);
                                    }
                                }
                            }
                        };
                        msg.replyTo = new Messenger(h);
                        msg.arg1 = msg.arg2 = 0;

                        /* wait for the dialog box to close */
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                        }

                        /* take the screenshot */
                        try {
                            messenger.send(msg);
                        } catch (RemoteException e) {
                        }
                    }
                }
                public void onServiceDisconnected(ComponentName name) {}
            };
            
            if (mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE)) {
                mScreenshotConnection = conn;
                //mHandler.postDelayed(mScreenshotTimeout, 10000);
            }
        }
    }
}