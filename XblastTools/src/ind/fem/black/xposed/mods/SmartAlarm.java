package ind.fem.black.xposed.mods;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class SmartAlarm {

	private static XSharedPreferences prefs;
	private static ClassLoader classLoader;
	private static final String TAG = "SmartAlarm";
	public static void init(XSharedPreferences prefs, ClassLoader classLoader) {
		log("init");
		SmartAlarm.prefs = prefs;
		SmartAlarm.classLoader = classLoader;

		if (prefs.getBoolean("hideAlarmClockIcon", false)) {
			hideAlarmIcon();
		}

	}

	private static void log(String message) {
        XposedBridge.log(TAG + ": " + message);
    }
	
	private static void hideAlarmIcon() {
		XposedHelpers.findAndHookMethod(Black.SYSTEM_UI
				+ ".statusbar.phone.PhoneStatusBarPolicy", classLoader,
				"updateAlarm", Intent.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						int timeout = prefs.getInt("smartAlarmIconTime", 0);
						if (timeout == 0) {
							((Intent) param.args[0])
									.putExtra("alarmSet", false);
						} else {
							setupSmartAlarm(param, timeout);
						}
					}
				});
		
		log("Completed");
	}

	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("deprecation")
	private static void setupSmartAlarm(MethodHookParam param, int timeout) {
		if (((Intent) param.args[0]).getBooleanExtra("alarmSet", false)) {
			Context context = (Context) XposedHelpers.getObjectField(
					param.thisObject, "mContext");
			String nextAlarm = Settings.System.getString(
					context.getContentResolver(),
					android.provider.Settings.System.NEXT_ALARM_FORMATTED);
			DateFormat sdf = new SimpleDateFormat("EEE hh:mm aa");
			Date alarmDate = null;
			try {
				alarmDate = sdf.parse(nextAlarm);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			int alarmDay = alarmDate.getDay();
			Calendar now = Calendar.getInstance();
			Date currentDate = now.getTime();
			int todayDay = currentDate.getDay();
			int daysDiff = 0;
			if (todayDay > alarmDay) {
				daysDiff = ((7 + (alarmDay - todayDay)) % 7) + 1;
			} else {
				daysDiff = ((7 + (alarmDay - todayDay)) % 7);
			}
			now.add(Calendar.DATE, daysDiff);
			Date alarmFulldate = now.getTime();
			alarmFulldate.setHours(alarmDate.getHours());
			alarmFulldate.setMinutes(alarmDate.getMinutes());
			alarmFulldate.setSeconds(0);
			long millis = alarmFulldate.getTime() - new Date().getTime();

			if (millis > (timeout * 60 * 1000)) {
				((Intent) param.args[0]).putExtra("alarmSet", false);
			}
		}
	}

}