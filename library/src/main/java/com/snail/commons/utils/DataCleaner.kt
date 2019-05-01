package com.snail.commons.utils

import android.content.Context
import android.os.Environment
import com.snail.commons.AppHolder.Companion.context

import java.io.File

/**
 * 清除本应用内部缓存(/items/items/com.xxx.xxx/cache)
 */
fun Context.cleanInternalCache() = cacheDir.clear()

/**
 * 清除本应用所有数据库(/items/items/com.xxx.xxx/databases)
 */
fun Context.cleanDatabases() = File(filesDir.parent, "databases").clear()

/**
 * 清除本应用SharedPreference(/items/items/com.xxx.xxx/shared_prefs)
 */
fun Context.cleanSharedPreference() = File(filesDir.parent, "shared_prefs").clear()

/**
 * 按名字清除本应用数据库
 */
fun Context.cleanDatabaseByName(dbName: String) = deleteDatabase(dbName)

/**
 * 清除/items/items/com.xxx.xxx/files下的内容
 */
fun Context.cleanFiles() = filesDir.clear()

/**
 * 清除外部cache下的内容(/mnt/sdcard/android/items/com.xxx.xxx/cache)
 */
fun Context.cleanExternalCache() {
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        context.externalCacheDir?.clear()
    }
}

/**
 * 清除本应用所有的数据
 */
fun Context.cleanApplicationData() {
    cleanInternalCache()
    cleanExternalCache()
    cleanDatabases()
    cleanSharedPreference()
    cleanFiles()
}
