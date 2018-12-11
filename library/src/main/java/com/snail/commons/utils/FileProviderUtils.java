package com.snail.commons.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * 描述: 
 * 时间: 2018/6/9 09:09
 * 作者: zengfansheng
 */
public class FileProviderUtils {

    /**
     * 兼容Android7.0以上，获取Intent传递的File的Uri
     */
    public static Uri getUriForFile(@NonNull Context context, @NonNull File file) {
        Uri uri;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }
    
    public static void setIntentDataAndType(@NonNull Context context, @NonNull Intent intent, @NonNull String type, @NonNull File file, boolean writeable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(getUriForFile(context, file), type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeable) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }
}
