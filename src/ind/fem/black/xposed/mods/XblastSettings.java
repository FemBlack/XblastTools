package ind.fem.black.xposed.mods;

import ind.fem.black.xposed.dialogs.MoveApptoDataFolderDialog;
import ind.fem.black.xposed.dialogs.RestoreDialog;
import ind.fem.black.xposed.dialogs.RestoreDialog.RestoreDialogListener;
import ind.fem.black.xposed.dialogs.SaveDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.ramdroid.roottools.ex.AsyncShell;
import com.ramdroid.roottools.ex.ErrorCode;
import com.ramdroid.roottools.ex.ErrorCode.OutputListener;
import com.stericson.RootTools.RootTools;

import de.devmil.common.ui.color.ColorSelectorDialog;
import de.robv.android.xposed.library.ui.ListPreferenceFixedSummary;

public class XblastSettings extends Activity implements RestoreDialogListener{
    
    public static final String PREF_KEY_ENABLE_ALL_ROTATION = "enable_all_rotation";
    public static final String PREF_KEY_POWEROFF_ADVANCED = "poweroff";
	public static final String PREF_KEY_RECENTS_CLEAR_ALL = "recents_clear_all";
	public static final String PREF_KEY_RAM_BAR_USAGE = "ram_usage_bar";
	public static final String PREF_KEY_HOLO_BG_SOLID_BLACK = "holo_solid_black";
	public static final String PREF_KEY_NBG_PULLUP_PULLDOWN_SPEED = "np_pu_pd_speed";
    public static final String PREF_KEY_STATUSBAR_COLOR_ENABLE = "statusbar_color_enabled";
    public static final String PREF_KEY_ABOUT = "pref_about_app";
    public static final String PREF_KEY_ABOUT_XPOSED = "pref_about_xposed";
    public static final String PREF_KEY_ABOUT_DONATE = "pref_about_donate";
    public static final String PREF_KEY_STATUSBAR_COLOR = "statusbar_color";
    public static final String PREF_KEY_SB_CLOCK_COLOR = "sb_clock_color";
    public static final String PREF_KEY_SB_CLOCK_COLOR_ENABLE = "sb_clock_color_enabled";
    public static final String PREF_KEY_CUSTOM_TEXT = "custom_text";
    public static final String PREF_KEY_FONT_LIST = "font_list";
    public static final String PREF_KEY_NP_CLOCK_COLOR = "np_clock_color";
    public static final String PREF_KEY_CUSTOM_TEXT_NP = "custom_text_np";
    public static final String PREF_KEY_FONT_LIST_NP = "font_list_np";
    public static final String PREF_KEY_CALLBANNER_FONT_LIST = "call_banner_font";
    public static final String PREF_KEY_NOTIF_NORMAL_COLOR = "notif_normal_color";
    public static final String PREF_KEY_NOTIF_NORMAL_COLOR_ENABLE = "notif_normal_color_enabled";
    public static final String PREF_KEY_NOTIF_PRESSED_COLOR = "notif_pressed_color";
    public static final String PREF_KEY_NOTIF_PRESSED_COLOR_ENABLE = "notif_pressed_color_enabled";
    public static final String PREF_KEY_NOTIF_TITLE_COLOR = "notif_Title_text_color";
    public static final String PREF_KEY_NOTIF_TITLE_COLOR_ENABLE = "notif_Title_text_color_enabled";
    public static final String PREF_KEY_NOTIF_CONTENT_COLOR = "notif_Content_color";
    public static final String PREF_KEY_NOTIF_CONTENT_COLOR_ENABLE = "notif_Content_color_enabled";
    public static final String PREF_KEY_TOAST_TEXT_COLOR = "toast_text_color";
    public static final String PREF_KEY_TOAST_TEXT_COLOR_ENABLE = "toast_text_color_enabled";
    public static final String PREF_KEY_TOAST_BG_COLOR = "toast_bg_color";
    public static final String PREF_KEY_TOAST_BG_COLOR_ENABLE = "toast_bg_color_enabled";
    public static final String PREF_KEY_TRAFFIC = "traffic";
    public static final String PREF_KEY_TRAFFIC_COLOR = "traffic_color";
    public static final String PREF_KEY_TRAFFIC_COLOR_ENABLE = "traffic_color_enabled";
    
    public static final String PREF_KEY_TICKER_COLOR = "ticker_color";
    public static final String PREF_KEY_TICKER_COLOR_ENABLE = "ticker_color_enabled";
    
    public static final String PREF_KEY_MOBILE_SIGNAL_COLOR = "mobile_signal_color";
    public static final String PREF_KEY_MOBILE_SIGNAL_COLOR_ENABLE = "mobile_signal_color_enabled";
    
    public static final String PREF_KEY_MOBILE_INOUT_COLOR = "mobile_inout_color";
    public static final String PREF_KEY_MOBILE_INOUT_COLOR_ENABLE = "mobile_inout_color_enabled";
    
    public static final String PREF_KEY_WIFI_SIGNAL_COLOR = "wifi_signal_color";
    public static final String PREF_KEY_WIFI_SIGNAL_COLOR_ENABLE = "wifi_signal_color_enabled";
    
    public static final String PREF_KEY_WIFI_INOUT_COLOR = "wifi_inout_color";
    public static final String PREF_KEY_WIFI_INOUT_COLOR_ENABLE = "wifi_inout_color_enabled";
    
    public static final String PREF_KEY_BATTERY_COLOR = "battery_color";
    public static final String PREF_KEY_BATTERY_COLOR_ENABLE = "battery_color_enabled";
    
    public static final String PREF_KEY_CALL_BANNER_COLOR = "call_banner_color";
    public static final String PREF_KEY_CALL_BANNER_COLOR_ENABLE = "call_banner_color_enabled";
    
    public static final String PREF_KEY_CCLLS_COLOR = "CCLLS_color";
    public static final String PREF_KEY_CCLLS_COLOR_ENABLE = "CCLLS_color_enabled";
    
    public static final String PREF_KEY_CCLNC_COLOR = "CCLNC_color";
    public static final String PREF_KEY_CCLNC_COLOR_ENABLE = "CCLNC_color_enabled";
    
    public static final String PREF_KEY_BOOT_ANIMATION = "boot_animation";
    public static final String PREF_KEY_XDREAM_BG_COLOR = "pref_xdream_bg_color";
    public static final String PREF_KEY_XDREAM_BG_COLOR_ENABLE = "pref_xdream_bg_color_enabled";
    public static final String PREF_KEY_XDREAM_CLOCK_COLOR = "pref_xdream_clock_color";
    public static final String PREF_KEY_XDREAM_CLOCK_COLOR_ENABLE = "pref_xdream_clock_color_enabled";
    public static final String PREF_KEY_XDREAM_BG_IMAGE = "pref_xdream_bg_Image";
    public static final String PREF_KEY_XDREAM_BG_IMAGE_ALPHA = "pref_xdream_bg_Image_alpha";
    public static final String PREF_KEY_XDREAM_CUSTOM_TEXT = "customDreamText";
    public static final String PREF_KEY_XDREAM_CUSTOM_TEXT_SIZE = "customDreamTextSize";
    public static final String PREF_KEY_XDREAM_FONT_LIST = "dream_font_list";
    
    public final static String VERSION = "version";
    public static int SELECTED_NBG_COLOR = -1;
	public final static String CONST_NGB_COLOR = "nbg_color";
	public static SharedPreferences mPrefs = null;
	public static final String PACKAGE_NAME = XblastSettings.class.getPackage().getName();
	
	public static final String ACTION_PREF_STATUSBAR_BGCOLOR_CHANGED = "xblast.intent.action.SB_BGCOLOR_CHANGED";
	public static final String EXTRA_SB_BGCOLOR = "bgColor";
	public static final String PREF_KEY_STATUSBAR_CLOCK_HIDE = "pref_clock_hide";
	public static final String PREF_KEY_STATUSBAR_CLOCK_AMPM_HIDE = "amPm";
	public static final String PREF_KEY_STATUSBAR_CENTER_CLOCK = "center_clock";
	public static final String ACTION_PREF_CLOCK_CHANGED = "xblast.intent.action.CENTER_CLOCK_CHANGED";
	public static final String EXTRA_CENTER_CLOCK = "centerClock";
    public static final String EXTRA_CLOCK_HIDE = "clockHide";
    public static final String EXTRA_AMPM_HIDE = "ampmHide";
	
	public static final String ACTION_PREF_QUICKSETTINGS_CHANGED = "xblast.intent.action.QUICKSETTINGS_CHANGED";
    public static final String EXTRA_QS_PREFS = "qsPrefs";
    public static final String EXTRA_QS_COLS = "qsCols";
    
    public static final String ACTION_PREF_BOOTANIMATION_CHANGED = "xblast.intent.action.BOOTANIMATION_CHANGED";
    
    
    public static final String PREF_KEY_VOL_MUSIC_CONTROLS = "pref_vol_music_controls";
    public static final String PREF_KEY_SAFE_MEDIA_VOLUME = "pref_safe_media_volume";
    
    public static final String ACTION_PREF_SAFE_MEDIA_VOLUME_CHANGED = "xblast.intent.action.SAFE_MEDIA_VOLUME_CHANGED";
    public static final String EXTRA_SAFE_MEDIA_VOLUME_ENABLED = "enabled";
    
    public static final String PREF_KEY_VOL_KEY_CURSOR_CONTROL = "pref_vol_key_cursor_control";
    public static final int VOL_KEY_CURSOR_CONTROL_OFF = 0;
    public static final int VOL_KEY_CURSOR_CONTROL_ON = 1;
    public static final int VOL_KEY_CURSOR_CONTROL_ON_REVERSE = 2;
    public static final String PREF_KEY_GOOGLE_PLUS = "pref_about_gplus";
    
    public static final String PREF_KEY_REMAINING_SB_ICONS_COLOR = "remaining_sb_icons_color";
    public static final String PREF_KEY_REMAINING_SB_ICONS_COLOR_ENABLE = "remaining_sb_icons_color_enabled";
    
    public static final String PREF_KEY_GRADIENT_SETTINGS_ENABLE = "gradient_Settings_enable";
    public static final String PREF_KEY_NOTIF_GRADIENT_COLOR = "notifi_gradient_color";
    public static final String PREF_KEY_NOTIF_GRADIENT_COLOR_ENABLE = "notifi_gradient_color_enabled";
    public static final String PREF_KEY_XDREAM_GRADIENT_COLOR = "xdream_gradient_color";
    public static final String PREF_KEY_XDREAM_GRADIENT_COLOR_ENABLE = "xdream_gradient_color_enabled";
    public static final String PREF_KEY_GRADIENT_COLOR_ORIENTATION = "gradient_color_orientation";
    
    public static final String PREF_KEY_HOLO_BG_COLOR = "holo_bg_color";
    public static final String PREF_KEY_HOLO_BG_COLOR_ENABLE = "holo_bg_color_enabled";
    public static final String PREF_KEY_HOLO_BG_IMAGE = "holo_bg_Image";
    public static final String PREF_KEY_HOLO_GRADIENT_COLOR = "holo_gradient_color";
    public static final String PREF_KEY_HOLO_GRADIENT_COLOR_ENABLE = "holo_gradient_color_enabled";
    
	
	private static Context mContext;
	private static PreferenceManager mPreferenceManager;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getWindow().getContext();
        
        checkPrerequisite();
        
        if (savedInstanceState == null)
            getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }
    
    @Override
    public void onResume()
    {
      super.onResume();
      checkPrerequisite();
    }

    public static class PrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
       
        //private SharedPreferences mPrefs;
       
        
        private Preference mPrefAbout;
        private Preference mPrefAboutDonate;
        private Preference mPrefGplus;
        Preference mNbgpref;
        Preference mFullScreenCallerImagepref;
        ListPreferenceFixedSummary mFontlistpref;
        ListPreferenceFixedSummary mCbfontlistpref;
        ListPreferenceFixedSummary mNpfontlistpref;
        ListPreferenceFixedSummary mFontlistXdreampref;
        
        private Preference mXDreamBgImagepref;
        private ColorPickerPreference mXDreamBgColorEnabledpref;
        
        private Preference mHoloBgImagepref;
        private ColorPickerPreference mHoloBgColorEnabledpref;
        
        Map< CharSequence, String > fontsMap = null;
        
        private static final String TAG = "ROMControl :PropModder";
        private static final String APPEND_CMD = "echo \"%s=%s\" >> /system/build.prop";
        private static final String KILL_PROP_CMD = "busybox sed -i \"/%s/D\" /system/build.prop";
        private static final String REPLACE_CMD = "busybox sed -i \"/%s/ c %<s=%s\" /system/build.prop";
        private static final String PROP_EXISTS_CMD = "grep -q %s /system/build.prop";
        private static final String DISABLE = "disable";
        private static final String SHOWBUILD_PATH = "/system/tmp/showbuild";
       // private static final String INIT_SCRIPT_LOGCAT = "/system/etc/init.d/72logcat";
        //private static final String INIT_SCRIPT_SDCARD = "/system/etc/init.d/72sdcard";
       // private static final String INIT_SCRIPT_TEMP_PATH = "/system/tmp/init_script";
        private static final String WIFI_SCAN_PREF = "pref_wifi_scan_interval";
        private static final String WIFI_SCAN_PROP = "wifi.supplicant_scan_interval";
        private static final String WIFI_SCAN_PERSIST_PROP = "persist.wifi_scan_interval";
        private static final String WIFI_SCAN_DEFAULT = System.getProperty(WIFI_SCAN_PROP);
        /* private static final String LCD_DENSITY_PREF = "pref_lcd_density";
    private static final String LCD_DENSITY_PROP = "ro.sf.lcd_density";
    private static final String LCD_DENSITY_PERSIST_PROP = "persist.lcd_density";
    private static final String LCD_DENSITY_DEFAULT = System.getProperty(LCD_DENSITY_PROP); */
        private static final String MAX_EVENTS_PREF = "pref_max_events";
        private static final String MAX_EVENTS_PROP = "windowsmgr.max_events_per_sec";
        private static final String MAX_EVENTS_PERSIST_PROP = "persist.max_events";
        private static final String MAX_EVENTS_DEFAULT = System.getProperty(MAX_EVENTS_PROP);
        private static final String RING_DELAY_PREF = "pref_ring_delay";
        private static final String RING_DELAY_PROP = "ro.telephony.call_ring.delay";
        private static final String RING_DELAY_PERSIST_PROP = "persist.call_ring.delay";
        private static final String RING_DELAY_DEFAULT = System.getProperty(RING_DELAY_PROP);
        private static final String VM_HEAPSIZE_PREF = "pref_vm_heapsize";
        private static final String VM_HEAPSIZE_PROP = "dalvik.vm.heapsize";
        private static final String VM_HEAPSIZE_PERSIST_PROP = "persist.vm_heapsize";
        private static final String VM_HEAPSIZE_DEFAULT = System.getProperty(VM_HEAPSIZE_PROP);
        private static final String FAST_UP_PREF = "pref_fast_up";
        private static final String FAST_UP_PROP = "ro.ril.hsxpa";
        private static final String FAST_UP_PERSIST_PROP = "persist.fast_up";
        private static final String FAST_UP_DEFAULT = System.getProperty(FAST_UP_PROP);
        private static final String PROX_DELAY_PREF = "pref_prox_delay";
        private static final String PROX_DELAY_PROP = "mot.proximity.delay";
        private static final String PROX_DELAY_PERSIST_PROP = "persist.prox.delay";
        private static final String PROX_DELAY_DEFAULT = System.getProperty(PROX_DELAY_PROP);
        private static final String MOD_VERSION_PREF = "pref_mod_version";
        private static final String MOD_VERSION_PROP = "ro.build.display.id";
        private static final String MOD_VERSION_PERSIST_PROP = "persist.build.display.id";
        private static final String MOD_VERSION_DEFAULT = System.getProperty(MOD_VERSION_PROP);
        private static final String SLEEP_PREF = "pref_sleep";
        private static final String SLEEP_PROP = "pm.sleep_mode";
        private static final String SLEEP_PERSIST_PROP = "persist.sleep";
        private static final String SLEEP_DEFAULT = System.getProperty(SLEEP_PROP);
        private static final String TCP_STACK_PREF = "pref_tcp_stack";
        private static final String TCP_STACK_PERSIST_PROP = "persist_tcp_stack";
        private static final String TCP_STACK_PROP_0 = "net.tcp.buffersize.default";
        private static final String TCP_STACK_PROP_1 = "net.tcp.buffersize.wifi";
        private static final String TCP_STACK_PROP_2 = "net.tcp.buffersize.umts";
        private static final String TCP_STACK_PROP_3 = "net.tcp.buffersize.gprs";
        private static final String TCP_STACK_PROP_4 = "net.tcp.buffersize.edge";
        private static final String TCP_STACK_BUFFER = "4096,87380,256960,4096,16384,256960";
        private static final String JIT_PREF = "pref_jit";
        private static final String JIT_PERSIST_PROP = "persist_jit";
        private static final String JIT_PROP = "dalvik.vm.execution-mode";
        private static final String CHECK_IN_PREF = "pref_check_in";
        private static final String CHECK_IN_PERSIST_PROP = "persist_check_in";
        private static final String CHECK_IN_PROP = "ro.config.nocheckin";
        private static final String CHECK_IN_PROP_HTC = "ro.config.htc.nocheckin";
        private static final String THREE_G_PREF = "pref_g_speed";
        private static final String THREE_G_PERSIST_PROP = "persist_3g_speed";
        private static final String THREE_G_PROP_0 = "ro.ril.enable.3g.prefix";
        private static final String THREE_G_PROP_1 = "ro.ril.hep";
        private static final String THREE_G_PROP_2 = FAST_UP_PROP;
        private static final String THREE_G_PROP_3 = "ro.ril.enable.dtm";
        private static final String THREE_G_PROP_4 = "ro.ril.gprsclass";
        private static final String THREE_G_PROP_5 = "ro.ril.hsdpa.category";
        private static final String THREE_G_PROP_6 = "ro.ril.enable.a53";
        private static final String THREE_G_PROP_7 = "ro.ril.hsupa.category";
        private static final String GPU_PREF = "pref_gpu";
        private static final String GPU_PERSIST_PROP = "persist_gpu";
        private static final String GPU_PROP = "debug.sf.hw";
        
        private static final String DISABLE_BOOT_ANIM_PREF = "boot_animation";
        private static final String DISABLE_BOOT_ANIM_PERSIST_PROP = "persist_dba";
        private static final String DISABLE_BOOT_ANIM_PROP = "debug.sf.nobootanimation";
        
        private static final String DISABLE_ADB_NOTIF_PREF = "disable_adb_notif";
        private static final String DISABLE_ADB_NOTIF_PERSIST_PROP = "persist_dadbn";
        private static final String DISABLE_ADB_NOTIF_PROP = "persist.adb.notify";
        
        private static final String AUDIO_VIDEO_PREF = "audio_video";
        private static final String AUDIO_VIDEO_PERSIST_PROP = "persist_audio_video";
        private static final String AUDIO_VIDEO_PROP_0 = "ro.media.enc.jpeg.quality";
        private static final String AUDIO_VIDEO_PROP_1 = "ro.media.dec.jpeg.memcap";
        private static final String AUDIO_VIDEO_PROP_2 = "ro.media.enc.hprof.vid.bps";
        private static final String AUDIO_VIDEO_PROP_3 = "ro.media.capture.maxres";
        private static final String AUDIO_VIDEO_PROP_4 = "ro.media.panorama.defres";
        private static final String AUDIO_VIDEO_PROP_5 = "ro.media.panorama.frameres";
        private static final String AUDIO_VIDEO_PROP_6 = "ro.camcorder.videoModes";
        private static final String AUDIO_VIDEO_PROP_7 = "ro.media.enc.hprof.vid.fps";
        
        private static final String GOOGLE_DNS_PREF = "google_dns";
        private static final String GOOGLE_DNS_PERSIST_PROP = "persist_google_dns";
        private static final String GOOGLE_DNS_PROP_0 = "net.rmnet0.dns1";
        private static final String GOOGLE_DNS_PROP_1 = "net.rmnet0.dns2";
        private static final String GOOGLE_DNS_PROP_2 = "net.dns1";
        private static final String GOOGLE_DNS_PROP_3 = "net.dns2";
        
        private String ModPrefHolder = System.getProperty(MOD_VERSION_PERSIST_PROP,
        		System.getProperty(MOD_VERSION_PROP, MOD_VERSION_DEFAULT));

        //private PreferenceScreen mRebootMsg;
        private ListPreference mWifiScanPref;
        // private ListPreference mLcdDensityPref;
        private ListPreference mMaxEventsPref;
        private ListPreference mRingDelayPref;
        private ListPreference mVmHeapsizePref;
        private ListPreference mFastUpPref;
        private ListPreference mProxDelayPref;
       // private CheckBoxPreference mLogcatPref;
        private EditTextPreference mModVersionPref;
        private ListPreference mSleepPref;
        private CheckBoxPreference mTcpStackPref;
        private CheckBoxPreference mJitPref;
        private CheckBoxPreference mCheckInPref;
        //private ListPreference mSdcardBufferPref;
        private CheckBoxPreference m3gSpeedPref;
        private CheckBoxPreference mGpuPref;
        private CheckBoxPreference mDisableBootAnimationPref;
        private CheckBoxPreference mHideadbPref;
        private CheckBoxPreference mAudioVideo;
        private CheckBoxPreference mGoogleDns;

       /* private File tmpDir = new File("/system/tmp");
        private File init_d = new File("/system/etc/init.d");
        private File initScriptLogcat = new File(INIT_SCRIPT_LOGCAT);
        private File initScriptSdcard = new File(INIT_SCRIPT_SDCARD);*/

        //handler for command processor
        private final CMDProcessor cmd = new CMDProcessor();
        //private PreferenceScreen prefSet;
        
        @SuppressWarnings("deprecation")
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // this is important because although the handler classes that read these settings
            // are in the same package, they are executed in the context of the hooked package
            getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
            addPreferencesFromResource(ind.fem.black.xposed.mods.R.xml.xblast);
            mPreferenceManager = getPreferenceManager();
            mPrefs = getPreferenceScreen().getSharedPreferences();
            
            if (Build.VERSION.SDK_INT < 17) {
            	PreferenceScreen xblastScreen = (PreferenceScreen) findPreference("XBlast");
                PreferenceScreen xDreamScreen = (PreferenceScreen) findPreference("xDream");
                
                xblastScreen.removePreference(xDreamScreen);
            }
            
            
            mPrefAbout = (Preference) findPreference(PREF_KEY_ABOUT);
            mFullScreenCallerImagepref = findPreference("defaultCallerImage");
            mFontlistpref = (ListPreferenceFixedSummary) findPreference(PREF_KEY_FONT_LIST);
            mCbfontlistpref = (ListPreferenceFixedSummary) findPreference(PREF_KEY_CALLBANNER_FONT_LIST);
            mFontlistXdreampref = (ListPreferenceFixedSummary) findPreference(PREF_KEY_XDREAM_FONT_LIST);
            
            
            fontsMap = FontManager.enumerateFonts();
    		Collection<CharSequence> fontNameCollection = fontsMap.keySet();
    		//List<CharSequence> fontNameList = new ArrayList<CharSequence>(fontNameCollection);
    		
    		CharSequence [] fontNameArray = fontNameCollection.toArray(new CharSequence[fontNameCollection.size()]);
    		
            mFontlistpref.setEntries(fontNameArray);  
            mFontlistpref.setEntryValues(fontNameArray);
            
            mCbfontlistpref.setEntries(fontNameArray);  
            mCbfontlistpref.setEntryValues(fontNameArray);
            
            mFontlistXdreampref.setEntries(fontNameArray);  
            mFontlistXdreampref.setEntryValues(fontNameArray);
            
            mXDreamBgImagepref = findPreference(PREF_KEY_XDREAM_BG_IMAGE);
            mXDreamBgColorEnabledpref = (ColorPickerPreference) findPreference(PREF_KEY_XDREAM_BG_COLOR);
            
            
            if(mXDreamBgColorEnabledpref!= null && mXDreamBgColorEnabledpref.isEnabled()) {
            	mXDreamBgImagepref.setEnabled(false);
            }
            
            mHoloBgImagepref = findPreference(PREF_KEY_HOLO_BG_IMAGE);
            mHoloBgColorEnabledpref = (ColorPickerPreference) findPreference(PREF_KEY_HOLO_BG_COLOR);
            
            
            if(mHoloBgColorEnabledpref!= null && mHoloBgColorEnabledpref.isEnabled()) {
            	mHoloBgImagepref.setEnabled(false);
            }
            //mNpfontlistpref.setEntries(fontNameArray);  
            //mNpfontlistpref.setEntryValues(fontNameArray);
            
            mNbgpref = findPreference("nbg");
            mNbgpref.setOnPreferenceClickListener(new OnPreferenceClickListener()
      	  {
        	    public boolean onPreferenceClick(final Preference paramAnonymousPreference)
        	    {
        	      new AlertDialog.Builder(mContext).setCancelable(true).setTitle(R.string.pref_title_notif_bg).setNeutralButton(R.string.image, new DialogInterface.OnClickListener()
        	      {
        	        public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
        	        {
        	        	Intent intent = new Intent();
        		        //intent.setAction("ind.fem.black.xposed.mods.CallerImageActivity");
        		        intent.setClassName(Black.PACKAGE_NAME, "ind.fem.black.xposed.mods.NbgImageActivity");
        		        startActivity(intent);
        	        }
        	      }).setPositiveButton(R.string.color, new DialogInterface.OnClickListener()
        	      {
        	        public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
        	        {
        	        	ColorSelectorDialog dlg = new ColorSelectorDialog(
        	        			mContext,
        						new ColorSelectorDialog.OnColorChangedListener() {
        							public void colorChanged(int color) {
        								SELECTED_NBG_COLOR = color;
        								//SharedPreferences.Editor editor = Xmod.prefs.edit();
        								getPreferenceManager().getSharedPreferences().edit().putBoolean("nbgEnabled", false).commit();
        								getPreferenceManager().getSharedPreferences().edit().putInt(CONST_NGB_COLOR, SELECTED_NBG_COLOR).commit();
        								//TextEffectsActivity.editor.commit();
        							}
        						}, SELECTED_NBG_COLOR);
        				dlg.setTitle(R.string.select_color);
        				// b.setBackgroundColor( SELECTED_COLOR );
        				// b.getBackground().setColorFilter(SELECTED_COLOR,PorterDuff.Mode.MULTIPLY);
        				dlg.show();
        	        }
        	      }).create().show();
        	      return true;
        	    }
        	  });
            
            
            String version = "";
            try {
                PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                version = " v" + pInfo.versionName;
                mPrefs.edit().putString(VERSION, version).commit();
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            } finally {
                mPrefAbout.setTitle(getActivity().getTitle() + version);
            }
            
            mPrefAboutDonate =  findPreference(PREF_KEY_ABOUT_DONATE);
            mPrefGplus = findPreference(PREF_KEY_GOOGLE_PLUS);
            
            //prefSet = getPreferenceScreen();
            
           /* mRebootMsg = (PreferenceScreen) findPreference(REBOOT_PREF);
            removePreference(mRebootMsg);*/

            mWifiScanPref = (ListPreference) findPreference(WIFI_SCAN_PREF);
            mWifiScanPref.setOnPreferenceChangeListener(this);

            /* mLcdDensityPref = (ListPreference) prefSet.findPreference(LCD_DENSITY_PREF);
    mLcdDensityPref.setOnPreferenceChangeListener(this); */

            mMaxEventsPref = (ListPreference) findPreference(MAX_EVENTS_PREF);
            mMaxEventsPref.setOnPreferenceChangeListener(this);

            mRingDelayPref = (ListPreference) findPreference(RING_DELAY_PREF);
            mRingDelayPref.setOnPreferenceChangeListener(this);

            mVmHeapsizePref = (ListPreference) findPreference(VM_HEAPSIZE_PREF);
            mVmHeapsizePref.setOnPreferenceChangeListener(this);

            mFastUpPref = (ListPreference) findPreference(FAST_UP_PREF);
            mFastUpPref.setOnPreferenceChangeListener(this);

            mProxDelayPref = (ListPreference) findPreference(PROX_DELAY_PREF);
            mProxDelayPref.setOnPreferenceChangeListener(this);

            //mLogcatPref = (CheckBoxPreference) findPreference(LOGCAT_PREF);

            mSleepPref = (ListPreference) findPreference(SLEEP_PREF);
            mSleepPref.setOnPreferenceChangeListener(this);

            mTcpStackPref = (CheckBoxPreference) findPreference(TCP_STACK_PREF);

            mJitPref = (CheckBoxPreference) findPreference(JIT_PREF);

            ((PreferenceGroup) findPreference("general_category")).removePreference(mJitPref);

            mModVersionPref = (EditTextPreference) findPreference(MOD_VERSION_PREF);
            String mod = Helpers.findBuildPropValueOf(MOD_VERSION_PROP);
            if (mModVersionPref != null) {
                EditText modET = mModVersionPref.getEditText();
                ModPrefHolder = mModVersionPref.getEditText().toString();
                if (modET != null){
                    InputFilter lengthFilter = new InputFilter.LengthFilter(32);
                    modET.setFilters(new InputFilter[]{lengthFilter});
                    modET.setSingleLine(true);
                }
                mModVersionPref.setSummary(String.format(getString(R.string.pref_mod_version_alt_summary), mod));
            }
            Log.d(TAG, String.format("ModPrefHoler = '%s' found build number = '%s'", ModPrefHolder, mod));
            mModVersionPref.setOnPreferenceChangeListener(this);

            mCheckInPref = (CheckBoxPreference) findPreference(CHECK_IN_PREF);

            //TODO check all init.d scripts for buffer values to display in summary
            // for now we will just let it go with a generic summary displayed
            //mSdcardBufferPref = (ListPreference) findPreference(SDCARD_BUFFER_PREF);
            //mSdcardBufferPref.setOnPreferenceChangeListener(this);

            m3gSpeedPref = (CheckBoxPreference) findPreference(THREE_G_PREF);

            mGpuPref = (CheckBoxPreference) findPreference(GPU_PREF);
            
            mDisableBootAnimationPref = (CheckBoxPreference) findPreference(DISABLE_BOOT_ANIM_PREF);
            
            mHideadbPref = (CheckBoxPreference) findPreference(DISABLE_ADB_NOTIF_PREF);
            
            mAudioVideo = (CheckBoxPreference) findPreference(AUDIO_VIDEO_PREF);
            
            mGoogleDns = (CheckBoxPreference) findPreference(GOOGLE_DNS_PREF);

            updateScreen();
           
            //Mounting takes the most time so lets avoid doing it if possible
            /*if (!tmpDir.isDirectory() || !init_d.isDirectory()) {
            	NEEDS_SETUP = true;
            }
            	
            	

            if (NEEDS_SETUP) {
                try {
                    if (!mount("rw")) throw new RuntimeException("Could not remount /system rw");
                    if (!tmpDir.isDirectory()) {
                         Log.d(TAG, "We need to make /system/tmp dir");
                        cmd.su.runWaitFor("mkdir /system/tmp");
                    }
                    if (!init_d.isDirectory()) {
                        Log.d(TAG, "We need to make /system/etc/init.d/ dir");
                        enableInit();
                    }
                } finally {
                    mount("ro");
                    NEEDS_SETUP = false;
                }
            }*/
        }

        @Override
        public void onResume() {
            super.onResume();
            mPrefs.registerOnSharedPreferenceChangeListener(this);
        }

       
        @Override
        public void onPause() {
            mPrefs.unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
          
           Intent intent = new Intent();
           if (key.equals(PREF_KEY_STATUSBAR_COLOR)) {
               intent.setAction(ACTION_PREF_STATUSBAR_BGCOLOR_CHANGED);
               intent.putExtra(EXTRA_SB_BGCOLOR, prefs.getInt(PREF_KEY_STATUSBAR_COLOR, Color.BLACK));
           } else if (key.equals(PREF_KEY_STATUSBAR_CLOCK_AMPM_HIDE)) {
               intent.setAction(ACTION_PREF_CLOCK_CHANGED);
               intent.putExtra(EXTRA_AMPM_HIDE, prefs.getBoolean(
                       PREF_KEY_STATUSBAR_CLOCK_AMPM_HIDE, false));
           } else if (key.equals(PREF_KEY_STATUSBAR_CLOCK_HIDE)) {
               intent.setAction(ACTION_PREF_CLOCK_CHANGED);
               intent.putExtra(EXTRA_CLOCK_HIDE, prefs.getBoolean(PREF_KEY_STATUSBAR_CLOCK_HIDE, false));
           } else if (key.equals(PREF_KEY_SAFE_MEDIA_VOLUME)) {
               intent.setAction(ACTION_PREF_SAFE_MEDIA_VOLUME_CHANGED);
               intent.putExtra(EXTRA_SAFE_MEDIA_VOLUME_ENABLED,
                       prefs.getBoolean(PREF_KEY_SAFE_MEDIA_VOLUME, false));
           } else if (key.equals(PREF_KEY_XDREAM_BG_COLOR_ENABLE)) {
          		if(mXDreamBgColorEnabledpref!= null && mXDreamBgColorEnabledpref.isEnabled()) {
                	mXDreamBgImagepref.setEnabled(false);
                } else if(mXDreamBgColorEnabledpref!= null) {
                	mXDreamBgImagepref.setEnabled(true);
                }
          } else if (key.equals(PREF_KEY_HOLO_BG_COLOR_ENABLE)) {
        		if(mHoloBgColorEnabledpref!= null && mHoloBgColorEnabledpref.isEnabled()) {
              	mHoloBgImagepref.setEnabled(false);
              } else if(mHoloBgColorEnabledpref!= null) {
            	  mHoloBgImagepref.setEnabled(true);
              }
        }
           
          
           if (intent.getAction() != null) {
               getActivity().sendBroadcast(intent);
           }

        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference preference) {
            Intent intent = null;

            if (preference == mPrefAbout) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_app)));
            } else if (preference == mPrefAboutDonate) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_store_version)));
            } else if (preference == mFullScreenCallerImagepref) {
            	intent = new Intent();
    	        intent.setClassName(PACKAGE_NAME, "ind.fem.black.xposed.mods.CallerImageActivity");
            } else if (preference == mXDreamBgImagepref) {
            	intent = new Intent();
    	        intent.setClassName(PACKAGE_NAME, "ind.fem.black.xposed.mods.XDreamImageActivity");
            } else if (preference == mHoloBgImagepref) {
            	intent = new Intent();
    	        intent.setClassName(PACKAGE_NAME, "ind.fem.black.xposed.mods.HoloImageActivity");
            }  else if (preference == mPrefGplus) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_google_plus)));
            } 
            
            boolean value;
           /* if (preference == mLogcatPref) {
                value = mLogcatPref.isChecked();
                boolean returnValue = false;
                mount("rw");
                if (value) {
                    returnValue = cmd.su.runWaitFor(String.format("echo %s > %s", LOGCAT_ENABLE, INIT_SCRIPT_LOGCAT)).success();
                    if (returnValue) {
                                            cmd.su.runWaitFor(String.format("chmod 555 %s", INIT_SCRIPT_LOGCAT)).success();
                                    }
                } else {
                    returnValue = cmd.su.runWaitFor(String.format("rm %s", INIT_SCRIPT_LOGCAT)).success();
                }
                mount("ro");
                rebootRequired();
                return returnValue;
            } else */if (preference == mTcpStackPref) {
                Log.d(TAG, "mTcpStackPref.onPreferenceTreeClick()");
                value = mTcpStackPref.isChecked();
                return doMod(null, TCP_STACK_PROP_0, String.valueOf(value ? TCP_STACK_BUFFER : DISABLE))
                        && doMod(null, TCP_STACK_PROP_1, String.valueOf(value ? TCP_STACK_BUFFER : DISABLE))
                        && doMod(null, TCP_STACK_PROP_2, String.valueOf(value ? TCP_STACK_BUFFER : DISABLE))
                        && doMod(null, TCP_STACK_PROP_3, String.valueOf(value ? TCP_STACK_BUFFER : DISABLE))
                        && doMod(TCP_STACK_PERSIST_PROP, TCP_STACK_PROP_4, String.valueOf(value ? TCP_STACK_BUFFER : DISABLE));
            } else if (preference == mJitPref) {
                Log.d(TAG, "mJitPref.onPreferenceTreeClick()");
                value = mJitPref.isChecked();
                return doMod(JIT_PERSIST_PROP, JIT_PROP, String.valueOf(value ? "int:fast" : "int:jit"));
            } else if (preference == mCheckInPref) {
                value = mCheckInPref.isChecked();
                return doMod(null, CHECK_IN_PROP_HTC, String.valueOf(value ? 1 : DISABLE))
                && doMod(CHECK_IN_PERSIST_PROP, CHECK_IN_PROP, String.valueOf(value ? 1 : DISABLE));
            } else if (preference == m3gSpeedPref) {
                value = m3gSpeedPref.isChecked();
                return doMod(THREE_G_PERSIST_PROP, THREE_G_PROP_0, String.valueOf(value ? 1 : DISABLE))
                    && doMod(null, THREE_G_PROP_1, String.valueOf(value ? 1 : DISABLE))
                    && doMod(null, THREE_G_PROP_2, String.valueOf(value ? 2 : DISABLE))
                    && doMod(null, THREE_G_PROP_3, String.valueOf(value ? 1 : DISABLE))
                    && doMod(null, THREE_G_PROP_4, String.valueOf(value ? 12 : DISABLE))
                    && doMod(null, THREE_G_PROP_5, String.valueOf(value ? 8 : DISABLE))
                    && doMod(null, THREE_G_PROP_6, String.valueOf(value ? 1 : DISABLE))
                    && doMod(null, THREE_G_PROP_7, String.valueOf(value ? 5 : DISABLE));
            } else if (preference == mGpuPref) {
                value = mGpuPref.isChecked();
                return doMod(GPU_PERSIST_PROP, GPU_PROP, String.valueOf(value ? 1 : DISABLE));
            } else if (preference == mDisableBootAnimationPref) {
                value = mDisableBootAnimationPref.isChecked();
                return doMod(DISABLE_BOOT_ANIM_PERSIST_PROP, DISABLE_BOOT_ANIM_PROP, String.valueOf(value ? 1 : DISABLE));
            } else if (preference == mHideadbPref) {
                value = mHideadbPref.isChecked();
                return doMod(DISABLE_ADB_NOTIF_PERSIST_PROP, DISABLE_ADB_NOTIF_PROP, String.valueOf(value ? 0 : DISABLE));
            } else if (preference == mAudioVideo) {
                value = mAudioVideo.isChecked();
                return doMod(AUDIO_VIDEO_PERSIST_PROP, AUDIO_VIDEO_PROP_0, String.valueOf(value ? 100 : DISABLE))
                    && doMod(null, AUDIO_VIDEO_PROP_1, String.valueOf(value ? 8000000 : DISABLE))
                    && doMod(null, AUDIO_VIDEO_PROP_2, String.valueOf(value ? 8000000 : DISABLE))
                    && doMod(null, AUDIO_VIDEO_PROP_3, String.valueOf(value ? "8m" : DISABLE))
                    && doMod(null, AUDIO_VIDEO_PROP_4, String.valueOf(value ? "3264x1840" : DISABLE))
                    && doMod(null, AUDIO_VIDEO_PROP_5, String.valueOf(value ? "1280x720" : DISABLE))
                    && doMod(null, AUDIO_VIDEO_PROP_6, String.valueOf(value ? true : DISABLE))
                    && doMod(null, AUDIO_VIDEO_PROP_7, String.valueOf(value ? 65 : DISABLE));
            } else if (preference == mGoogleDns) {
                value = mGoogleDns.isChecked();
                return doMod(GOOGLE_DNS_PERSIST_PROP, GOOGLE_DNS_PROP_0, String.valueOf(value ? "8.8.8.8" : DISABLE))
                    && doMod(null, GOOGLE_DNS_PROP_1, String.valueOf(value ? "8.8.4.4" : DISABLE))
                    && doMod(null, GOOGLE_DNS_PROP_2, String.valueOf(value ? "8.8.8.8" : DISABLE))
                    && doMod(null, GOOGLE_DNS_PROP_3, String.valueOf(value ? "8.8.4.4" : DISABLE));
            } 
            
            /*else if (preference == mRebootMsg) {
                return cmd.su.runWaitFor("reboot").success();
            }*/
            
            if (intent != null) {
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
            }

            return super.onPreferenceTreeClick(prefScreen, preference);
        }

        /* handle ListPreferences and EditTextPreferences */
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (newValue != null) {
                Log.e(TAG, "New preference selected: " + newValue);
                if (preference == mWifiScanPref) {
                    return doMod(WIFI_SCAN_PERSIST_PROP, WIFI_SCAN_PROP,
                            newValue.toString());
                // } else if (preference == mLcdDensityPref) {
                    // return doMod(LCD_DENSITY_PERSIST_PROP, LCD_DENSITY_PROP,
                            // newValue.toString());
                } else if (preference == mMaxEventsPref) {
                    return doMod(MAX_EVENTS_PERSIST_PROP, MAX_EVENTS_PROP,
                            newValue.toString());
                } else if (preference == mRingDelayPref) {
                    return doMod(RING_DELAY_PERSIST_PROP, RING_DELAY_PROP,
                            newValue.toString());
                } else if (preference == mVmHeapsizePref) {
                    return doMod(VM_HEAPSIZE_PERSIST_PROP, VM_HEAPSIZE_PROP,
                            newValue.toString());
                } else if (preference == mFastUpPref) {
                    return doMod(FAST_UP_PERSIST_PROP, FAST_UP_PROP,
                            newValue.toString());
                } else if (preference == mProxDelayPref) {
                     return doMod(PROX_DELAY_PERSIST_PROP, PROX_DELAY_PROP,
                            newValue.toString());
                } else if (preference == mModVersionPref) {
                     return doMod(MOD_VERSION_PERSIST_PROP, MOD_VERSION_PROP,
                            newValue.toString());
                } else if (preference == mSleepPref) {
                     return doMod(SLEEP_PERSIST_PROP, SLEEP_PROP,
                            newValue.toString());
                } /*else if (preference == mSdcardBufferPref) {
                    boolean returnValue = false;
                    mount("rw");
                    if (newValue.toString() == DISABLE) {
                        returnValue = cmd.su.runWaitFor(String.format("rm %s", INIT_SCRIPT_SDCARD)).success();
                    } else {
                        String newFormat = String.format(SDCARD_BUFFER_CMD, newValue.toString());
                        cmd.su.runWaitFor(String.format(SDCARD_BUFFER_CMD, newValue.toString()));
                        cmd.su.runWaitFor(String.format("echo '%s' > %s", newFormat, INIT_SCRIPT_SDCARD));
                        cmd.su.runWaitFor(String.format("chmod 555 %s", INIT_SCRIPT_SDCARD));
                        returnValue = true;
                    }
                    mount("ro");
                    rebootRequired();
                    return returnValue;
                }*/
            }

            return false;
        }
        
        /* method to handle mods */
        private boolean doMod(String persist, String key, String value) {

            if (persist != null) {
                System.setProperty(persist, value);
            }
            Log.d(TAG, String.format("Calling script with args '%s' and '%s'", key, value));
            backupBuildProp();
            if (!mount("rw")) {
                throw new RuntimeException("Could not remount /system rw");
            }
            boolean success = false;
            try {
                if (!propExists(key) && value.equals(DISABLE)) {
                    Log.d(TAG, String.format("We want {%s} DISABLED however it doesn't exist so we do nothing and move on", key));
                } else if (propExists(key)) {
                    if (value.equals(DISABLE)) {
                        Log.d(TAG, String.format("value == %s", DISABLE));
                        success = cmd.su.runWaitFor(String.format(KILL_PROP_CMD, key)).success();
                    } else {
                        Log.d(TAG, String.format("value != %s", DISABLE));
                        success = cmd.su.runWaitFor(String.format(REPLACE_CMD, key, value)).success();
                    }
                } else {
                    Log.d(TAG, "append command starting");
                    success = cmd.su.runWaitFor(String.format(APPEND_CMD, key, value)).success();
                }
                if (!success) {
                    restoreBuildProp();
                } else {
                    updateScreen();
                }
            } finally {
                mount("ro");
            }

            rebootRequired();
            return success;
        }

        private void rebootRequired() {
        	
        	Toast.makeText(mContext, R.string.reboot_required, Toast.LENGTH_SHORT)
			.show();
                    //prefSet.addPreference(mRebootMsg);
                    //mRebootMsg.setTitle("Reboot required");
                   // mRebootMsg.setSummary("values will take effect on next boot");
            }

        public boolean mount(String read_value) {
            Log.d(TAG, "Remounting /system " + read_value);
            return RootTools.remount("/system", read_value);
        }

        public boolean propExists(String prop) {
            Log.d(TAG, "Checking if prop " + prop + " exists in /system/build.prop");
            return cmd.su.runWaitFor(String.format(PROP_EXISTS_CMD, prop)).success();
        }

        public void updateShowBuild() {
            Log.d(TAG, "Setting up /system/tmp/showbuild");
            try {
                mount("rw");
                cmd.su.runWaitFor("cp -f /system/build.prop " + SHOWBUILD_PATH).success();
                cmd.su.runWaitFor("chmod 777 " + SHOWBUILD_PATH).success();
            } finally {
                mount("ro");
            }
        }

        /*public boolean initLogcat(boolean swap0) {
            if (swap0) {
                cmd.su.runWaitFor(String.format("echo 'rm /dev/log/main' > %s", INIT_SCRIPT_LOGCAT)).success();
                return cmd.su.runWaitFor(String.format("chmod 555 %s", INIT_SCRIPT_LOGCAT)).success();
            } else {
                return cmd.su.runWaitFor(String.format("rm -f %s", INIT_SCRIPT_LOGCAT)).success();
            }
        }

        public boolean initSdcard(boolean swap1) {
            if (swap1) {
                cmd.su.runWaitFor(String.format("echo 'rm /dev/log/main' > %s", INIT_SCRIPT_LOGCAT)).success();
                return cmd.su.runWaitFor(String.format("chmod 755 %s", INIT_SCRIPT_LOGCAT)).success();
            } else {
                return cmd.su.runWaitFor(String.format("rm -f %s", INIT_SCRIPT_LOGCAT)).success();
            }
        }*/

        /*public boolean enableInit() {
            FileWriter wAlive;
            try {
                wAlive = new FileWriter("/system/tmp/initscript");
                //forgive me but without all the \n's the script is one line long O:-)
                wAlive.write("#\n#enable init.d script by PropModder\n#\n\n");
                wAlive.write("log -p I -t boot \"Starting init.d ...\"\n");
                wAlive.write("busybox run-parts /system/etc/init.d");
                wAlive.flush();
                wAlive.close();
                cmd.su.runWaitFor("cp -f /system/tmp/initscript /system/usr/bin/init.sh");
                return cmd.su.runWaitFor("chmod 755 /system/usr/bin/pm_init.sh").success();
            } catch(Exception e) {
                Log.e(TAG, "enableInit install failed: " + e);
                e.printStackTrace();
            }
            return false;
        }*/

        public boolean backupBuildProp() {
            Log.d(TAG, "Backing up build.prop to /system/tmp/pm_build.prop");
            return cmd.su.runWaitFor("cp /system/build.prop /system/tmp/pm_build.prop").success();
        }

        public boolean restoreBuildProp() {
            Log.d(TAG, "Restoring build.prop from /system/tmp/pm_build.prop");
            return cmd.su.runWaitFor("cp /system/tmp/pm_build.prop /system/build.prop").success();
        }

        public void updateScreen() {
            //update all the summaries
            String wifi = Helpers.findBuildPropValueOf(WIFI_SCAN_PROP);
            if (!wifi.equals(DISABLE)) {
                mWifiScanPref.setValue(wifi);
                mWifiScanPref.setSummary(String.format(getString(R.string.pref_wifi_scan_alt_summary), wifi));
            } else {
                mWifiScanPref.setValue(WIFI_SCAN_DEFAULT);
            }
            // String lcd = Helpers.findBuildPropValueOf(LCD_DENSITY_PROP);
            // if (!lcd.equals(DISABLE)) {
                // mLcdDensityPref.setValue(lcd);
                // mLcdDensityPref.setSummary(String.format(getString(R.string.pref_lcd_density_alt_summary), lcd));
            // } else {
                // mLcdDensityPref.setValue(LCD_DENSITY_DEFAULT);
            // }
            String maxE = Helpers.findBuildPropValueOf(MAX_EVENTS_PROP);
            if (!maxE.equals(DISABLE)) {
                mMaxEventsPref.setValue(maxE);
                mMaxEventsPref.setSummary(String.format(getString(R.string.pref_max_events_alt_summary), maxE));
            } else {
                mMaxEventsPref.setValue(MAX_EVENTS_DEFAULT);
            }
            String ring = Helpers.findBuildPropValueOf(RING_DELAY_PROP);
            if (!ring.equals(DISABLE)) {
                mRingDelayPref.setValue(ring);
                mRingDelayPref.setSummary(String.format(getString(R.string.pref_ring_delay_alt_summary), ring));
            } else {
                mRingDelayPref.setValue(RING_DELAY_DEFAULT);
            }
            String vm = Helpers.findBuildPropValueOf(VM_HEAPSIZE_PROP);
            if (!vm.equals(DISABLE)) {
                mVmHeapsizePref.setValue(vm);
                mVmHeapsizePref.setSummary(String.format(getString(R.string.pref_vm_heapsize_alt_summary), vm));
            } else {
                mVmHeapsizePref.setValue(VM_HEAPSIZE_DEFAULT);
            }
            String fast = Helpers.findBuildPropValueOf(FAST_UP_PROP);
            if (!fast.equals(DISABLE)) {
                mFastUpPref.setValue(fast);
                mFastUpPref.setSummary(String.format(getString(R.string.pref_fast_up_alt_summary), fast));
            } else {
                mFastUpPref.setValue(FAST_UP_DEFAULT);
            }
            String prox = Helpers.findBuildPropValueOf(PROX_DELAY_PROP);
            if (!prox.equals(DISABLE)) {
                mProxDelayPref.setValue(prox);
                mProxDelayPref.setSummary(String.format(getString(R.string.pref_prox_delay_alt_summary), prox));
            } else {
                mProxDelayPref.setValue(PROX_DELAY_DEFAULT);
            }
            String sleep = Helpers.findBuildPropValueOf(SLEEP_PROP);
            if (!sleep.equals(DISABLE)) {
                mSleepPref.setValue(sleep);
                mSleepPref.setSummary(String.format(getString(R.string.pref_sleep_alt_summary), sleep));
            } else {
                mSleepPref.setValue(SLEEP_DEFAULT);
            }
            String tcp = Helpers.findBuildPropValueOf(TCP_STACK_PROP_0);
            if (tcp.equals(TCP_STACK_BUFFER)) {
                mTcpStackPref.setChecked(true);
            } else {
                mTcpStackPref.setChecked(false);
            }
            String jit = Helpers.findBuildPropValueOf(JIT_PROP);
            if (jit.equals("int:jit")) {
                mJitPref.setChecked(true);
            } else {
                mJitPref.setChecked(false);
            }
            String mod = Helpers.findBuildPropValueOf(MOD_VERSION_PROP);
            mModVersionPref.setSummary(String.format(getString(R.string.pref_mod_version_alt_summary), mod));
            String chk = Helpers.findBuildPropValueOf(CHECK_IN_PROP);
            if (!chk.equals(DISABLE)) {
                mCheckInPref.setChecked(true);
            } else {
                mCheckInPref.setChecked(false);
            }
            String g0 = Helpers.findBuildPropValueOf(THREE_G_PROP_0);
            String g3 = Helpers.findBuildPropValueOf(THREE_G_PROP_3);
            String g6 = Helpers.findBuildPropValueOf(THREE_G_PROP_6);
            if (g0.equals("1") && g3.equals("1") && g6.equals("1")) {
                m3gSpeedPref.setChecked(true);
            } else {
                m3gSpeedPref.setChecked(false);
            }
            String gpu = Helpers.findBuildPropValueOf(GPU_PROP);
            if (!gpu.equals(DISABLE)) {
                mGpuPref.setChecked(true);
            } else {
                mGpuPref.setChecked(false);;
            }
            
            String dba = Helpers.findBuildPropValueOf(DISABLE_BOOT_ANIM_PROP);
            if (!dba.equals(DISABLE)) {
            	mDisableBootAnimationPref.setChecked(true);
            } else {
            	mDisableBootAnimationPref.setChecked(false);;
            }
            String dadbn = Helpers.findBuildPropValueOf(DISABLE_ADB_NOTIF_PROP);
            if (!dadbn.equals(DISABLE)) {
            	mHideadbPref.setChecked(true);
            } else {
            	mHideadbPref.setChecked(false);;
            }
            
            String avh0 = Helpers.findBuildPropValueOf(AUDIO_VIDEO_PROP_0);
            String avh1 = Helpers.findBuildPropValueOf(AUDIO_VIDEO_PROP_1);
            String avh2 = Helpers.findBuildPropValueOf(AUDIO_VIDEO_PROP_2);
            String avh3 = Helpers.findBuildPropValueOf(AUDIO_VIDEO_PROP_3);
            String avh4 = Helpers.findBuildPropValueOf(AUDIO_VIDEO_PROP_4);
            String avh5 = Helpers.findBuildPropValueOf(AUDIO_VIDEO_PROP_5);
            String avh6 = Helpers.findBuildPropValueOf(AUDIO_VIDEO_PROP_6);
            String avh7 = Helpers.findBuildPropValueOf(AUDIO_VIDEO_PROP_7);
            if (!avh0.equals(DISABLE) && !avh1.equals(DISABLE) && !avh2.equals(DISABLE) && !avh3.equals(DISABLE) && !avh4.equals(DISABLE) && !avh5.equals(DISABLE) && !avh6.equals(DISABLE) && !avh7.equals(DISABLE)) {
            	mAudioVideo.setChecked(true);
            } else {
            	mAudioVideo.setChecked(false);;
            }
            
            String dns0 = Helpers.findBuildPropValueOf(GOOGLE_DNS_PROP_0);
            String dns1 = Helpers.findBuildPropValueOf(GOOGLE_DNS_PROP_1);
            String dns2 = Helpers.findBuildPropValueOf(GOOGLE_DNS_PROP_2);
            String dns3 = Helpers.findBuildPropValueOf(GOOGLE_DNS_PROP_3);
           
            if (!dns0.equals(DISABLE) && !dns1.equals(DISABLE) && !dns2.equals(DISABLE) && !dns3.equals("1")) {
            	mGoogleDns.setChecked(true);
            } else {
            	mGoogleDns.setChecked(false);;
            }
            /*if (initScriptLogcat.isFile()) {
                mLogcatPref.setChecked(true);
            } else {
                mLogcatPref.setChecked(false);
            }*/
        }
		
		}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.action_save:
			new SaveDialog().show(getFragmentManager(), "save");
			break;
		case R.id.action_restore:
			new RestoreDialog().show(getFragmentManager(), "restore");
			break;

		default:
			break;
		}
		return true;

	}

	@Override
	public void onRestoreDefaults() {
		mPreferenceManager.getSharedPreferences().edit().clear().commit();
		//PreferenceManager.setDefaultValues(this, R.xml.xblast, false);
		Toast.makeText(this, R.string.reset_successful, Toast.LENGTH_SHORT)
				.show();
		 Intent intent = getIntent();
		    finish();
		    startActivity(intent);
		    
		Toast.makeText(this, R.string.reboot_required, Toast.LENGTH_SHORT)
			.show();
		//RebootNotification.notify(this, 999, false);
	}

	@Override
	public void onRestoreBackup(final File backup) {
		new RestoreBackupTask(backup).execute();
	}

	class RestoreBackupTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog progressDialog;
		private File backup;

		public RestoreBackupTask(File backup) {
			this.backup = backup;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//unregisterPrefsReceiver();
			progressDialog = new ProgressDialog(XblastSettings.this);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage(getString(R.string.restoring_backup));
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			ObjectInputStream input = null;
			try {
				input = new ObjectInputStream(new FileInputStream(backup));
				Editor prefEdit = mPreferenceManager.getSharedPreferences()
						.edit();
				prefEdit.clear();
				@SuppressWarnings("unchecked")
				Map<String, ?> entries = (Map<String, ?>) input.readObject();
				for (Entry<String, ?> entry : entries.entrySet()) {
					Object v = entry.getValue();
					String key = entry.getKey();

					if (v instanceof Boolean)
						prefEdit.putBoolean(key, ((Boolean) v).booleanValue());
					else if (v instanceof Float)
						prefEdit.putFloat(key, ((Float) v).floatValue());
					else if (v instanceof Integer)
						prefEdit.putInt(key, ((Integer) v).intValue());
					else if (v instanceof Long)
						prefEdit.putLong(key, ((Long) v).longValue());
					else if (v instanceof String)
						prefEdit.putString(key, ((String) v));
				}
				prefEdit.commit();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (input != null) {
						input.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			SystemClock.sleep(1500);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			Toast.makeText(XblastSettings.this, R.string.backup_restored,
					Toast.LENGTH_SHORT).show();
			 Intent intent = getIntent();
			    finish();
			    startActivity(intent);
			Toast.makeText(XblastSettings.this, R.string.reboot_required, Toast.LENGTH_SHORT)
				.show();
			//RebootNotification.notify(NottachXposed.this, 999, false);
			//registerPrefsReceiver();
		}
		
		/*private void registerPrefsReceiver() {
			mPreferenceManager.getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);
		}
		private void unregisterPrefsReceiver() {
			mPreferenceManager.getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(this);
		}*/
		

	}
	
	private boolean appIsInstalledInMountASEC() {
		//System.out.println("black......" + getApplicationInfo().sourceDir);
		return getApplicationInfo().sourceDir.contains("asec/");
	}
	
	private void checkPrerequisite() {
		final boolean flag = appIsInstalledInMountASEC();
		AsyncShell.gotRoot(new OutputListener() {
            @Override
            public void onResult(int errorCode, List<String> output) {
                if (errorCode == ErrorCode.NONE) {
                    if(flag) {
                    	try
                        {
                          Runtime.getRuntime().exec("rm " + XblastSettings.this.getExternalCacheDir().getAbsolutePath() + "/tmp.apk");
                        }
                        catch (Exception localException1)
                        {
                          localException1.printStackTrace();
                        }
                    }
                    if(flag) {
                    	Fragment  MoveApptoDataFolderFragment =  XblastSettings.this.getFragmentManager().findFragmentByTag("MoveApptoDataFolder");
                        if (MoveApptoDataFolderFragment == null) {
                        	MoveApptoDataFolderFragment = MoveApptoDataFolderDialog.newInstance();
                        	((DialogFragment) MoveApptoDataFolderFragment).show(getFragmentManager(), "MoveApptoDataFolder");
                        }
                        
                    	//new MoveApptoDataFolderDialog().show(getFragmentManager(), "MoveApptoDataFolder");
                    }
                } else {
                	XblastSettings.this.finish();
                }
            }
        });		
		
	}
}

