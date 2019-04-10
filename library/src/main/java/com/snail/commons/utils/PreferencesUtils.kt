package com.snail.commons.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.TextUtils

import com.snail.commons.AppHolder

/**
 * Created by zengfs on 2015/10/30.
 */
@Deprecated(message = "建议使用MMKV，详情：https://github.com/Tencent/MMKV")
object PreferencesUtils {

    private fun getSharedPreferences(name: String?): SharedPreferences {
        return if (TextUtils.isEmpty(name)) {
            PreferenceManager.getDefaultSharedPreferences(AppHolder.context)
        } else {
            AppHolder.context.getSharedPreferences(name, 0)
        }
    }

    @JvmStatic 
    fun putString(key: String, value: String) {
        getSharedPreferences(null).edit().putString(key, value).apply()
    }

    @JvmStatic 
    fun putStringImmediately(key: String, value: String) {
        getSharedPreferences(null).edit().putString(key, value).commit()
    }

    @JvmStatic 
    fun getString(key: String, defaultValue: String): String {
        return getSharedPreferences(null).getString(key, defaultValue)
    }
    
    @JvmStatic 
    fun getString(key: String): String? {
        return getSharedPreferences(null).getString(key, null)
    }

    @JvmStatic 
    fun putInt(key: String, value: Int) {
        getSharedPreferences(null).edit().putInt(key, value).apply()
    }

    @JvmStatic 
    fun putIntImmediately(key: String, value: Int) {
        getSharedPreferences(null).edit().putInt(key, value).commit()
    }

    @JvmOverloads
    @JvmStatic 
    fun getInt(key: String, defaultValue: Int = -1): Int {
        return getSharedPreferences(null).getInt(key, defaultValue)
    }

    @JvmStatic 
    fun putLong(key: String, value: Long) {
        getSharedPreferences(null).edit().putLong(key, value).apply()
    }

    @JvmStatic 
    fun putLongImmediately(key: String, value: Long) {
        getSharedPreferences(null).edit().putLong(key, value).commit()
    }

    @JvmOverloads
    @JvmStatic 
    fun getLong(key: String, defaultValue: Long = -1L): Long {
        return getSharedPreferences(null).getLong(key, defaultValue)
    }

    @JvmStatic 
    fun putFloat(key: String, value: Float) {
        getSharedPreferences(null).edit().putFloat(key, value).apply()
    }

    @JvmStatic 
    fun putFloatImmediately(key: String, value: Float) {
        getSharedPreferences(null).edit().putFloat(key, value).commit()
    }

    @JvmOverloads
    @JvmStatic 
    fun getFloat(key: String, defaultValue: Float = -1f): Float {
        return getSharedPreferences(null).getFloat(key, defaultValue)
    }

    @JvmStatic 
    fun putBoolean(key: String, value: Boolean) {
        getSharedPreferences(null).edit().putBoolean(key, value).apply()
    }

    @JvmStatic 
    fun putBooleanImmediately(key: String, value: Boolean) {
        getSharedPreferences(null).edit().putBoolean(key, value).commit()
    }

    @JvmOverloads
    @JvmStatic 
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return getSharedPreferences(null).getBoolean(key, defaultValue)
    }

    @JvmStatic 
    fun removeKey(key: String) {
        getSharedPreferences(null).edit().remove(key).apply()
    }

    @JvmStatic 
    fun removeKeyImmediately(key: String) {
        getSharedPreferences(null).edit().remove(key).commit()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmStatic 
    fun putString(name: String, key: String, value: String) {
        getSharedPreferences(name).edit().putString(key, value).apply()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmStatic 
    fun putStringImmediately(name: String, key: String, value: String) {
        getSharedPreferences(name).edit().putString(key, value).commit()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmStatic 
    fun getString(name: String, key: String, defaultValue: String): String? {
        return getSharedPreferences(name).getString(key, defaultValue)
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmStatic 
    fun putInt(name: String, key: String, value: Int) {
        getSharedPreferences(name).edit().putInt(key, value).apply()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmStatic 
    fun putIntImmediately(name: String, key: String, value: Int) {
        getSharedPreferences(name).edit().putInt(key, value).commit()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmOverloads
    @JvmStatic 
    fun getInt(name: String, key: String, defaultValue: Int = -1): Int {
        return getSharedPreferences(name).getInt(key, defaultValue)
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmStatic 
    fun putLong(name: String, key: String, value: Long) {
        getSharedPreferences(name).edit().putLong(key, value).apply()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmStatic 
    fun putLongImmediately(name: String, key: String, value: Long) {
        getSharedPreferences(name).edit().putLong(key, value).commit()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmOverloads
    @JvmStatic 
    fun getLong(name: String, key: String, defaultValue: Long = -1L): Long {
        return getSharedPreferences(name).getLong(key, defaultValue)
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmStatic 
    fun putFloat(name: String, key: String, value: Float) {
        getSharedPreferences(name).edit().putFloat(key, value).apply()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmStatic 
    fun putFloatImmediately(name: String, key: String, value: Float) {
        getSharedPreferences(name).edit().putFloat(key, value).commit()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmOverloads
    @JvmStatic 
    fun getFloat(name: String, key: String, defaultValue: Float = -1f): Float {
        return getSharedPreferences(name).getFloat(key, defaultValue)
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmStatic 
    fun putBoolean(name: String, key: String, value: Boolean) {
        getSharedPreferences(name).edit().putBoolean(key, value).apply()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmStatic 
    fun putBooleanImmediately(name: String, key: String, value: Boolean) {
        getSharedPreferences(name).edit().putBoolean(key, value).commit()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmOverloads
    @JvmStatic 
    fun getBoolean(name: String, key: String, defaultValue: Boolean = false): Boolean {
        return getSharedPreferences(name).getBoolean(key, defaultValue)
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmStatic 
    fun removeKey(name: String, key: String) {
        getSharedPreferences(name).edit().remove(key).apply()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmStatic 
    fun removeKeyImmediately(name: String, key: String) {
        getSharedPreferences(name).edit().remove(key).commit()
    }
}