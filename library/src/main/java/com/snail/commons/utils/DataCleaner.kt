package com.snail.commons.utils

import android.content.Context
import android.os.Environment

import java.io.File

/**
 * Created by zeng on 2016/9/7.
 * 应用数据清理
 */
object DataCleaner {
    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)
     */
    fun cleanInternalCache(context: Context) {
        FileUtils.deleteDir(context.cacheDir, false)
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases)
     */
    fun cleanDatabases(context: Context) {
        FileUtils.deleteDir(File(context.filesDir.parent, "databases"), false)
    }

    /**
     * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs)
     */
    fun cleanSharedPreference(context: Context) {
        FileUtils.deleteDir(File(context.filesDir.parent, "shared_prefs"), false)
    }

    /**
     * 按名字清除本应用数据库
     */
    fun cleanDatabaseByName(context: Context, dbName: String) {
        context.deleteDatabase(dbName)
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容
     */
    fun cleanFiles(context: Context) {
        FileUtils.deleteDir(context.filesDir, false)
    }

    /**
     * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     */
    fun cleanExternalCache(context: Context) {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val cacheDir = context.externalCacheDir
            if (cacheDir != null) {
                FileUtils.deleteDir(cacheDir, false)
            }
        }
    }

    /**
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除
     */
    fun cleanCustomCache(filePath: String) {
        FileUtils.deleteDir(File(filePath), false)
    }

    /**
     * 清除本应用所有的数据
     */
    fun cleanApplicationData(context: Context, vararg filepath: String) {
        cleanInternalCache(context)
        cleanExternalCache(context)
        cleanDatabases(context)
        cleanSharedPreference(context)
        cleanFiles(context)
        for (filePath in filepath) {
            cleanCustomCache(filePath)
        }
    }
}
