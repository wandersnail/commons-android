package com.snail.commons.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import java.io.File;

/**
 * 描述:
 * 时间: 2018/9/27 23:18
 * 作者: zengfansheng
 */
public class MediaUtils {

    /**
     * 根据文件获取content uri
     * @param context 上下文
     * @param baseUri 父uri
     * @param file 文件
     */
    private static Uri getContentUri(Context context, Uri baseUri, File file) {
        String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(baseUri, new String[] {BaseColumns._ID},
                MediaStore.MediaColumns.DATA + "=? ", new String[] {filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (file.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DATA, filePath);
                return context.getContentResolver().insert(baseUri, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 获取视频文件的content uri
     * @param file 文件
     */
    public static Uri getVideoContentUri(Context context, File file) {
        return getContentUri(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, file);
    }

    /**
     * 获取图片文件的content uri
     * @param file 文件
     */
    public static Uri getImageContentUri(Context context, File file) {
        return getContentUri(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, file);
    }

    /**
     * 获取音频文件的content uri
     * @param file 文件
     */
    public static Uri getAudioContentUri(Context context, File file) {
        return getContentUri(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, file);
    }
}
