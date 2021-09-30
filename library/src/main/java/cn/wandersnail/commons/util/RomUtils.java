package cn.wandersnail.commons.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * Created by zeng on 2016/10/23.
 */

public class RomUtils {
	//MIUI
	private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
	private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
	private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
	//EMUI
	private static final String KEY_EMUI_VERSION_CODE = "ro.build.hw_emui_api_level";
	private static final String KEY_EMUI_VERSION = "ro.build.version.emui";
	private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";
	//vivo
	private static final String KEY_VIVO_VERSION = "ro.vivo.os.version";
	//oppo
	private static final String KEY_OPPO_VERSION = "ro.build.version.opporom";
	//锤子
	private static final String KEY_SMARTISAN = "ro.smartisan.version";
	
	public static boolean isMIUI() {
		try {
			final BuildProperties prop = BuildProperties.newInstance();
			return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
					|| prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
					|| prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
		} catch (IOException e) {
			return !TextUtils.isEmpty(getProp(KEY_MIUI_VERSION_CODE)) || !TextUtils.isEmpty(getProp(KEY_MIUI_VERSION_NAME)) || !TextUtils.isEmpty(getProp(KEY_MIUI_INTERNAL_STORAGE)) || 
                    Build.HOST.toLowerCase().contains("miui") || Build.DISPLAY.toUpperCase().matches("OPM\\d+\\.\\d+\\.\\d+") ||
                    Build.MANUFACTURER.equalsIgnoreCase("xiaomi");
		}
	}

	public static boolean isEMUI() {
		try {
            BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_EMUI_VERSION_CODE, null) != null ||
                    prop.getProperty(KEY_EMUI_VERSION, null) != null ||
                    prop.getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null;
		} catch (IOException e) {
			return !TextUtils.isEmpty(getProp(KEY_EMUI_VERSION_CODE)) || !TextUtils.isEmpty(getProp(KEY_EMUI_VERSION)) || !TextUtils.isEmpty(getProp(KEY_EMUI_CONFIG_HW_SYS_VERSION)) ||
                    Build.HOST.toLowerCase().contains("emui") || Build.MANUFACTURER.equalsIgnoreCase("huawei");
		}
	}

	public static boolean isHarmonyOS(){
		try {
			Class<?> buildExClass = Class.forName("com.huawei.system.BuildEx");
			Object osBrand = buildExClass.getMethod("getOsBrand").invoke(buildExClass);
			return "harmony".equalsIgnoreCase(osBrand != null ? osBrand.toString() : null);
		} catch (Throwable e){
			return false;
		}
	}
	
	public static boolean isVivoOS() {
		try {
			final BuildProperties prop = BuildProperties.newInstance();
			return prop.getProperty(KEY_VIVO_VERSION, null) != null;
		} catch (IOException e) {
			return !TextUtils.isEmpty(getProp(KEY_VIVO_VERSION));
		}
	}

	public static boolean isOppoOS() {
		try {
			final BuildProperties prop = BuildProperties.newInstance();
			return prop.getProperty(KEY_OPPO_VERSION, null) != null;
		} catch (IOException e) {
			return !TextUtils.isEmpty(getProp(KEY_OPPO_VERSION));
		}
	}

	public static boolean isFlyme() {
		return Build.DISPLAY.toLowerCase(Locale.ENGLISH).toLowerCase().contains("flyme");
	}

	public static boolean isSmartisan() {
		try {
			final BuildProperties prop = BuildProperties.newInstance();
			return prop.getProperty(KEY_SMARTISAN, null) != null;
		} catch (IOException e) {
			return !TextUtils.isEmpty(getProp(KEY_SMARTISAN));
		}
	}

	public static boolean isSamsung() {
		return Build.BRAND != null && Build.BRAND.toLowerCase(Locale.ENGLISH).contains("samsung");
	}
	
	public static boolean isRealMeUI(@NonNull Context context) {
		return SystemUtils.isSystemApp(context, "com.heytap.market");
	}
	
	public static boolean isOnePlus(@NonNull Context context) {
		return SystemUtils.isSystemApp(context, "com.oneplus.market");
	}
	
    public static String getProp(String name) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
			Logger.d(RomUtils.class.getSimpleName(), "getprop = " + line);
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }
}