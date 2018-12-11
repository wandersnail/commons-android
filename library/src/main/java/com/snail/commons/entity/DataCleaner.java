package com.snail.commons.entity;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.snail.commons.utils.FileUtils;

import java.io.File;

/**
 * Created by zeng on 2016/9/7.
 * 应用数据清理
 */
public class DataCleaner {
    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)
     */
    public static void cleanInternalCache(@NonNull Context context) {
        FileUtils.deleteDir(context.getCacheDir(), false);
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases)
     */
    public static void cleanDatabases(@NonNull Context context) {
        FileUtils.deleteDir(new File("/data/data/" + context.getPackageName() + "/databases"), false);
    }

    /**
     * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs)
     */
    public static void cleanSharedPreference(@NonNull Context context) {
        FileUtils.deleteDir(new File("/data/data/" + context.getPackageName() + "/shared_prefs"), false);
    }

    /**
     * 按名字清除本应用数据库
     */
    public static void cleanDatabaseByName(@NonNull Context context, @NonNull String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容
     */
    public static void cleanFiles(@NonNull Context context) {
        FileUtils.deleteDir(context.getFilesDir(), false);
    }

    /**
     * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     */
    public static void cleanExternalCache(@NonNull Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileUtils.deleteDir(context.getExternalCacheDir(), false);
        }
    }
    /**
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除
     * */
    public static void cleanCustomCache(@NonNull String filePath) {
        FileUtils.deleteDir(new File(filePath), false);
    }

    /**
     * 清除本应用所有的数据
     */
    public static void cleanApplicationData(@NonNull Context context, @NonNull String... filepath) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
        for (String filePath : filepath) {
            cleanCustomCache(filePath);
        }
    }
}
