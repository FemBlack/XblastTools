package ind.fem.black.xposed.mods;

import java.nio.FloatBuffer;

import android.animation.ObjectAnimator;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class CrtEffect {
    private static final String TAG = "CrtEffect";
    public static final String CLASS_DISPLAY_POWER_STATE = "com.android.server.power.DisplayPowerState";
    public static final String CLASS_ELECTRON_BEAM_STATE = "com.android.server.power.ElectronBeam";
    private static final String CLASS_DISPLAY_POWER_CONTROLLER = "com.android.server.power.DisplayPowerController";
    private static int ebMode = 4;
    public static void init(final XSharedPreferences prefs) {
    	XposedBridge.log(TAG + ": init");
        try {
            final Class<?> clsDisplayPowerState = XposedHelpers.findClass(CLASS_DISPLAY_POWER_STATE, null);
            final Class<?> clsDisplaPowerController = XposedHelpers.findClass(CLASS_DISPLAY_POWER_CONTROLLER, null);
            
            XposedHelpers.findAndHookMethod(clsDisplayPowerState, "prepareElectronBeam", int.class,
                    new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
                    prefs.reload();
                    ebMode = prefs.getInt("crt_effect_mode", 4);
                    
							if (ebMode == 4) {
								return;
							}
							// prefs.getBoolean(GravityBoxSettings.PREF_KEY_CRT_OFF_EFFECT,
							// false) ? 1 : 2;
							if (ebMode == 3) {
								param.args[0] = 1;
								XposedBridge.log("ebMode 2" + ebMode);
								init();

							} else {
								param.args[0] = ebMode;
								XposedBridge.log("ebMode 3" + ebMode);
							}
                   
                }
                
            });
            
            XposedHelpers.findAndHookMethod(clsDisplaPowerController, "initialize", new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    ObjectAnimator oa = (ObjectAnimator)
                            XposedHelpers.getObjectField(param.thisObject, "mElectronBeamOffAnimator");
                    if (oa != null) {
                        oa.setDuration(400);
                    }
                }
            });
            
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }
        public static void init() {
        try {
            final Class<?> clsEB = XposedHelpers.findClass(CLASS_ELECTRON_BEAM_STATE, null);

            XposedBridge.hookAllConstructors(clsEB, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                	float HSTRETCH_DURATION = XposedHelpers.getStaticFloatField(clsEB, "HSTRETCH_DURATION");
                	float VSTRETCH_DURATION = XposedHelpers.getStaticFloatField(clsEB, "VSTRETCH_DURATION");
                	
                	VSTRETCH_DURATION = 0.5f;
                	HSTRETCH_DURATION = 1.0f - VSTRETCH_DURATION;
                	
                	
                	XposedHelpers.setStaticFloatField(clsEB, "VSTRETCH_DURATION", VSTRETCH_DURATION);
                	XposedHelpers.setStaticFloatField(clsEB, "HSTRETCH_DURATION", HSTRETCH_DURATION);
                	
                }
            });
            
        } catch (Exception e) {
            XposedBridge.log(e);
        }
        
        try {
            final Class<?> clsEB = XposedHelpers.findClass(CLASS_ELECTRON_BEAM_STATE, null);

            XposedHelpers.findAndHookMethod(clsEB, "setVStretchQuad", FloatBuffer.class,float.class, float.class, float.class,
                    new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(final MethodHookParam param) throws Throwable {
                	 final float w;
                     final float h;
                     
                     w = (Float)param.args[1] - ((Float)param.args[1] * (Float)param.args[3]);
                     h = (Float)param.args[2] + ((Float)param.args[2] * (Float)param.args[3]);
                     
                    /* if (false) {
                         w = (Float)param.args[1] - ((Float)param.args[1] * (Float)param.args[3]);
                         h = (Float)param.args[2] + ((Float)param.args[2] * (Float)param.args[3]);
                     } else {
                         w = (Float)param.args[1] + ((Float)param.args[1] * (Float)param.args[3]);
                         h = (Float)param.args[2] - ((Float)param.args[2] * (Float)param.args[3]);
                     }*/
                     final float x = ((Float)param.args[1] - w) * 0.5f;
                     final float y = ((Float)param.args[2] - h) * 0.5f;
                    
                     XposedHelpers.callStaticMethod(clsEB, "setQuad",param.args[0],x,y,w,h);
                     
					return null;
                }
                
            });
            
        } catch (Exception e) {
            XposedBridge.log(e);
        }
        
        try {
            final Class<?> clsEB = XposedHelpers.findClass(CLASS_ELECTRON_BEAM_STATE, null);

            XposedHelpers.findAndHookMethod(clsEB, "setHStretchQuad", FloatBuffer.class,float.class, float.class, float.class,
                    new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(final MethodHookParam param) throws Throwable {
                	 final float w;
                     final float h;
                     w = 1.0f;
                     h = (Float)param.args[1] + ((Float)param.args[1] * (Float)param.args[3]);
                     /*if (false) {
                         w = 1.0f;
                         h = (Float)param.args[1] + ((Float)param.args[1] * (Float)param.args[3]);
                     } else {
                         w = (Float)param.args[1] + ((Float)param.args[1] * (Float)param.args[3]);
                         h = 1.0f;
                     }*/
                     final float x = ((Float)param.args[1] - w) * 0.5f;
                     final float y = ((Float)param.args[2] - h) * 0.5f;
                     
                     XposedHelpers.callStaticMethod(clsEB, "setQuad",param.args[0],x,y,w,h);
                    
					return null;
                }
                
            });
            
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }
}