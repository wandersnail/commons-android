package com.snail.commons.util;

import android.content.Context;
import android.os.Environment;
import androidx.annotation.NonNull;

import java.io.File;

/**
 * date: 2019/8/7 15:49
 * author: zengfansheng
 */
public class DataCleaner {
    /**
     * 清除本应用内部缓存(/items/items/com.xxx.xxx/cache)
     */
    public static void cleanInternalCache(@NonNull Context context) {
        FileUtils.emptyDir(context.getCacheDir());
    }

    /**
     * 清除本应用所有数据库(/items/items/com.xxx.xxx/databases)
     */
    public static void cleanDatabases(@NonNull Context context) {
        FileUtils.emptyDir(new File(context.getFilesDir().getParent(), "databases"));
    }

    /**
     * 清除本应用SharedPreference(/items/items/com.xxx.xxx/shared_prefs)
     */
    public static void cleanSharedPreference(@NonNull Context context) {
        FileUtils.emptyDir(new File(context.getFilesDir().getParent(), "shared_prefs"));
    }

    /**
     * 按名字清除本应用数据库
     */
    public static void cleanDatabaseByName(@NonNull Context context, @NonNull String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * 清除/items/items/com.xxx.xxx/files下的内容
     */
    public static void cleanFiles(@NonNull Context context) {
        FileUtils.emptyDir(context.getFilesDir());
    }

    /**
     * 清除外部cache下的内容(/mnt/sdcard/android/items/com.xxx.xxx/cache)
     */
    public static void cleanExternalCache(@NonNull Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = context.getExternalCacheDir();
            if (file != null) {
                FileUtils.emptyDir(file);
            }
        }
    }

    /**
     * 清除本应用所有的数据
     */
    public static void cleanApplicationData(@NonNull Context context) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
    }
}
