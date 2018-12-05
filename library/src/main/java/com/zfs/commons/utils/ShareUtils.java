package com.zfs.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 调用系统分享工具
 * 时间: 2018/9/27 23:54
 * 作者: zengfansheng
 */
public class ShareUtils {
    private static void startShare(Context context, Intent intent, @NonNull String title, boolean isFile) {
        if (isFile && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(Intent.createChooser(intent, title));
    }
    
    /**
     * 分享文本
     * @param context 上下文
     * @param title 系统分享对话框的标题
     * @param text 分享的内容
     */
    public static void shareText(Context context, @NonNull String title, @NonNull String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startShare(context, intent, title, false);
    }

    /**
     * 分享单张图片
     * @param context 上下文
     * @param title 系统分享对话框的标题
     * @param file 文件
     */
    public static void shareImage(Context context, @NonNull String title, @NonNull File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = MediaUtils.getImageContentUri(context, file);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startShare(context, intent, title, true);
    }

    /**
     * 分享多张图片
     * @param context 上下文
     * @param title 系统分享对话框的标题
     * @param files 文件
     */
    public static void shareImages(Context context, @NonNull String title, @NonNull List<File> files) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        ArrayList<Uri> imageUris = new ArrayList<>();
        for (File file : files) {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = MediaUtils.getImageContentUri(context, file);
            } else {
                uri = Uri.fromFile(file);
            }
            imageUris.add(uri);
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        startShare(context, intent, title, true);
    }

    /**
     * 分享视频
     * @param context 上下文
     * @param title 系统分享对话框的标题
     * @param file 文件
     */
    public static void shareVideo(Context context, @NonNull String title, @NonNull File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = MediaUtils.getVideoContentUri(context, file);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startShare(context, intent, title, true);
    }
}
