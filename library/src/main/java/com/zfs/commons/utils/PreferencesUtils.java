package com.zfs.commons.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.zfs.commons.AppHolder;

/**
 * Created by zengfs on 2015/10/30.
 */
public class PreferencesUtils {

	private PreferencesUtils() {
		throw new AssertionError();
	}
	
	private static SharedPreferences getSharedPreferences(String name) {
	    if (TextUtils.isEmpty(name)) {
            return PreferenceManager.getDefaultSharedPreferences(AppHolder.getContext());
	    } else {
            return AppHolder.getContext().getSharedPreferences(name, 0);
	    }
    }
	
	public static void putString(String key, String value) {
        getSharedPreferences(null).edit().putString(key, value).apply();
	}

    public static void putStringImmediately(String key, String value) {
        getSharedPreferences(null).edit().putString(key, value).commit();
    }
    
	/**
	 * 失败返回默认值为null
	 */
	public static String getString(String key) {
		return getString(key, null);
	}

	public static String getString(String key, String defaultValue) {
		return getSharedPreferences(null).getString(key, defaultValue);
	}

	public static void putInt(String key, int value) {
        getSharedPreferences(null).edit().putInt(key, value).apply();
	}

    public static void putIntImmediately(String key, int value) {
        getSharedPreferences(null).edit().putInt(key, value).commit();
    }

	/**
	 * 失败返回默认值为-1
	 */
	public static int getInt(String key) {
		return getInt(key, -1);
	}

	public static int getInt(String key, int defaultValue) {
		return getSharedPreferences(null).getInt(key, defaultValue);
	}

	public static void putLong(String key, long value) {
        getSharedPreferences(null).edit().putLong(key, value).apply();
	}

    public static void putLongImmediately(String key, long value) {
        getSharedPreferences(null).edit().putLong(key, value).commit();
    }

	/**
	 * 失败返回默认值为-1
	 */
	public static long getLong(String key) {
		return getLong(key, -1L);
	}

	public static long getLong(String key, long defaultValue) {
		return getSharedPreferences(null).getLong(key, defaultValue);
	}

	public static void putFloat(String key, float value) {
        getSharedPreferences(null).edit().putFloat(key, value).apply();
	}

    public static void putFloatImmediately(String key, float value) {
        getSharedPreferences(null).edit().putFloat(key, value).commit();
    }

	/**
	 * 失败返回默认值为-1
	 */
	public static float getFloat(String key) {
		return getFloat(key, -1F);
	}

	public static float getFloat(String key, float defaultValue) {
		return getSharedPreferences(null).getFloat(key, defaultValue);
	}

	public static void putBoolean(String key, boolean value) {
        getSharedPreferences(null).edit().putBoolean(key, value).apply();
	}

    public static void putBooleanImmediately(String key, boolean value) {
        getSharedPreferences(null).edit().putBoolean(key, value).commit();
    }

	public static boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		return getSharedPreferences(null).getBoolean(key, defaultValue);
	}

	public static void removeKey(String key) {
        getSharedPreferences(null).edit().remove(key).apply();
	}
	
	public static void removeKeyImmediately(String key) {
        getSharedPreferences(null).edit().remove(key).commit();
	}

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static void putString(String name, String key, String value) {
        getSharedPreferences(name).edit().putString(key, value).apply();
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static void putStringImmediately(String name, String key, String value) {
        getSharedPreferences(name).edit().putString(key, value).commit();
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static String getString(String name, String key, String defaultValue) {
        return getSharedPreferences(name).getString(key, defaultValue);
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static void putInt(String name, String key, int value) {
        getSharedPreferences(name).edit().putInt(key, value).apply();
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static void putIntImmediately(String name, String key, int value) {
        getSharedPreferences(name).edit().putInt(key, value).commit();
    }

    /**
     * 失败返回默认值为-1
     */
    public static int getInt(String name, String key) {
        return getInt(name, key, -1);
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static int getInt(String name, String key, int defaultValue) {
        return getSharedPreferences(name).getInt(key, defaultValue);
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static void putLong(String name, String key, long value) {
        getSharedPreferences(name).edit().putLong(key, value).apply();
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static void putLongImmediately(String name, String key, long value) {
        getSharedPreferences(name).edit().putLong(key, value).commit();
    }

    /**
     * 失败返回默认值为-1
     */
    public static long getLong(String name, String key) {
        return getLong(name, key, -1L);
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static long getLong(String name, String key, long defaultValue) {
        return getSharedPreferences(name).getLong(key, defaultValue);
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static void putFloat(String name, String key, float value) {
        getSharedPreferences(name).edit().putFloat(key, value).apply();
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static void putFloatImmediately(String name, String key, float value) {
        getSharedPreferences(name).edit().putFloat(key, value).commit();
    }

    /**
     * 失败返回默认值为-1
     */
    public static float getFloat(String name, String key) {
        return getFloat(name, key, -1F);
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static float getFloat(String name, String key, float defaultValue) {
        return getSharedPreferences(name).getFloat(key, defaultValue);
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static void putBoolean(String name, String key, boolean value) {
        getSharedPreferences(name).edit().putBoolean(key, value).apply();
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static void putBooleanImmediately(String name, String key, boolean value) {
        getSharedPreferences(name).edit().putBoolean(key, value).commit();
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static boolean getBoolean(String name, String key) {
        return getBoolean(name, key, false);
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static boolean getBoolean(String name, String key, boolean defaultValue) {
        return getSharedPreferences(name).getBoolean(key, defaultValue);
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static void removeKey(String name, String key) {
        getSharedPreferences(name).edit().remove(key).apply();
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    public static void removeKeyImmediately(String name, String key) {
        getSharedPreferences(name).edit().remove(key).commit();
    }
}
