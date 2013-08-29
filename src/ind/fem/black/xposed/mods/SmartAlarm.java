package ind.fem.black.xposed.mods;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
			/*String nextAlarm = Settings.System.getString(
					context.getContentResolver(),
					android.provider.Settings.System.NEXT_ALARM_FORMATTED);
			DateFormat sdf = new SimpleDateFormat("EEE hh:mm aa");*/
			Date alarmDate = getNextAlarm(context);
			/*try {
				alarmDate = sdf.parse(nextAlarm);
			} catch (ParseException e) {
				e.printStackTrace();
			}*/
			int alarmDay = alarmDate.getDay();
			Calendar now = Calendar.getInstance();
			Date currentDate = now.getTime();
			int todayDay = currentDate.getDay();
			int daysDiff = 0;
			if (todayDay > alarmDay) {
				daysDiff = ((7 + (alarmDay - todayDay)) % 7);
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
	
	public static Date getNextAlarm(Context context) {
	    // let's collect short names of days :-)        
	    DateFormatSymbols symbols = new DateFormatSymbols();
	    // and fill with those names map...
	    Map<String, Integer> map = new HashMap<String, Integer>();
	    String[] dayNames = symbols.getShortWeekdays();
	    // filing :-)
	    map.put(dayNames[Calendar.MONDAY],Calendar.MONDAY);
	    map.put(dayNames[Calendar.TUESDAY],Calendar.TUESDAY);
	    map.put(dayNames[Calendar.WEDNESDAY],Calendar.WEDNESDAY);
	    map.put(dayNames[Calendar.THURSDAY],Calendar.THURSDAY);
	    map.put(dayNames[Calendar.FRIDAY],Calendar.FRIDAY);
	    map.put(dayNames[Calendar.SATURDAY],Calendar.SATURDAY);
	    map.put(dayNames[Calendar.SUNDAY],Calendar.SUNDAY);
	    // Yeah, knowing next alarm will help.....
	    String nextAlarm = Settings.System.getString(context.getContentResolver(),Settings.System.NEXT_ALARM_FORMATTED).trim();
	    log("nextAlarm" + nextAlarm);
	    // In case if it isn't set.....
	    if ((nextAlarm==null) || ("".equals(nextAlarm))) return null;
	    // let's see a day....
	    String nextAlarmDay = nextAlarm.split(" ")[0];
	    log("nextAlarmDay" + nextAlarmDay);
	    // and its number....
	    int alarmDay = map.get(nextAlarmDay);

	    // the same for day of week (I'm not sure why I didn't use Calendar.get(Calendar.DAY_OF_WEEK) here...
	    Date now = new Date();      
	    String dayOfWeek = new SimpleDateFormat("EE", Locale.getDefault()).format(now);     
	    int today = map.get(dayOfWeek);

	    // OK, so let's calculate how many days we have to next alarm :-)
	    int daysToAlarm = alarmDay-today;
	    // yep, sometimes it will  be negtive number so add 7.
	    if (daysToAlarm<0) daysToAlarm+=7;



	    // Now we will build date, and parse it.....
	    try {
	        Calendar cal2 = Calendar.getInstance();
	        String str = cal2.get(Calendar.YEAR)+"-"+(cal2.get(Calendar.MONTH)+1)+"-"+(cal2.get(Calendar.DAY_OF_MONTH));

	        SimpleDateFormat df  = new SimpleDateFormat("yyyy-MM-d hh:mm");

	        cal2.setTime(df.parse(str+nextAlarm.substring(nextAlarm.indexOf(" "))));
	        cal2.add(Calendar.DAY_OF_YEAR, daysToAlarm);
	        // and return it
	        return cal2.getTime();
	    } catch (Exception e) {
	    	log("excep" + e.getMessage());
	    }
	    // in case if we cannot calculate...
	    return null;
	}

}