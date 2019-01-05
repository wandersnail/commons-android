package com.snail.commons.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.TextUtils

import com.snail.commons.AppHolder

/**
 * Created by zengfs on 2015/10/30.
 */
object PreferencesUtils {

    private fun getSharedPreferences(name: String?): SharedPreferences {
        return if (TextUtils.isEmpty(name)) {
            PreferenceManager.getDefaultSharedPreferences(AppHolder.context)
        } else {
            AppHolder.context.getSharedPreferences(name, 0)
        }
    }

    fun putString(key: String, value: String) {
        getSharedPreferences(null).edit().putString(key, value).apply()
    }

    fun putStringImmediately(key: String, value: String) {
        getSharedPreferences(null).edit().putString(key, value).commit()
    }

    fun getString(key: String, defaultValue: String): String {
        return getSharedPreferences(null).getString(key, defaultValue)
    }
    
    fun getString(key: String): String? {
        return getSharedPreferences(null).getString(key, null)
    }

    fun putInt(key: String, value: Int) {
        getSharedPreferences(null).edit().putInt(key, value).apply()
    }

    fun putIntImmediately(key: String, value: Int) {
        getSharedPreferences(null).edit().putInt(key, value).commit()
    }

    @JvmOverloads
    fun getInt(key: String, defaultValue: Int = -1): Int {
        return getSharedPreferences(null).getInt(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        getSharedPreferences(null).edit().putLong(key, value).apply()
    }

    fun putLongImmediately(key: String, value: Long) {
        getSharedPreferences(null).edit().putLong(key, value).commit()
    }

    @JvmOverloads
    fun getLong(key: String, defaultValue: Long = -1L): Long {
        return getSharedPreferences(null).getLong(key, defaultValue)
    }

    fun putFloat(key: String, value: Float) {
        getSharedPreferences(null).edit().putFloat(key, value).apply()
    }

    fun putFloatImmediately(key: String, value: Float) {
        getSharedPreferences(null).edit().putFloat(key, value).commit()
    }

    @JvmOverloads
    fun getFloat(key: String, defaultValue: Float = -1f): Float {
        return getSharedPreferences(null).getFloat(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        getSharedPreferences(null).edit().putBoolean(key, value).apply()
    }

    fun putBooleanImmediately(key: String, value: Boolean) {
        getSharedPreferences(null).edit().putBoolean(key, value).commit()
    }

    @JvmOverloads
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return getSharedPreferences(null).getBoolean(key, defaultValue)
    }

    fun removeKey(key: String) {
        getSharedPreferences(null).edit().remove(key).apply()
    }

    fun removeKeyImmediately(key: String) {
        getSharedPreferences(null).edit().remove(key).commit()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    fun putString(name: String, key: String, value: String) {
        getSharedPreferences(name).edit().putString(key, value).apply()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    fun putStringImmediately(name: String, key: String, value: String) {
        getSharedPreferences(name).edit().putString(key, value).commit()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    fun getString(name: String, key: String, defaultValue: String): String? {
        return getSharedPreferences(name).getString(key, defaultValue)
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    fun putInt(name: String, key: String, value: Int) {
        getSharedPreferences(name).edit().putInt(key, value).apply()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    fun putIntImmediately(name: String, key: String, value: Int) {
        getSharedPreferences(name).edit().putInt(key, value).commit()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmOverloads
    fun getInt(name: String, key: String, defaultValue: Int = -1): Int {
        return getSharedPreferences(name).getInt(key, defaultValue)
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    fun putLong(name: String, key: String, value: Long) {
        getSharedPreferences(name).edit().putLong(key, value).apply()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    fun putLongImmediately(name: String, key: String, value: Long) {
        getSharedPreferences(name).edit().putLong(key, value).commit()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmOverloads
    fun getLong(name: String, key: String, defaultValue: Long = -1L): Long {
        return getSharedPreferences(name).getLong(key, defaultValue)
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    fun putFloat(name: String, key: String, value: Float) {
        getSharedPreferences(name).edit().putFloat(key, value).apply()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    fun putFloatImmediately(name: String, key: String, value: Float) {
        getSharedPreferences(name).edit().putFloat(key, value).commit()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmOverloads
    fun getFloat(name: String, key: String, defaultValue: Float = -1f): Float {
        return getSharedPreferences(name).getFloat(key, defaultValue)
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    fun putBoolean(name: String, key: String, value: Boolean) {
        getSharedPreferences(name).edit().putBoolean(key, value).apply()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    fun putBooleanImmediately(name: String, key: String, value: Boolean) {
        getSharedPreferences(name).edit().putBoolean(key, value).commit()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    @JvmOverloads
    fun getBoolean(name: String, key: String, defaultValue: Boolean = false): Boolean {
        return getSharedPreferences(name).getBoolean(key, defaultValue)
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    fun removeKey(name: String, key: String) {
        getSharedPreferences(name).edit().remove(key).apply()
    }

    /**
     * 使用指定的SharedPreferences
     * @param name SharedPreferences的名
     */
    fun removeKeyImmediately(name: String, key: String) {
        getSharedPreferences(name).edit().remove(key).commit()
    }
}