package com.snail.commons.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import java.io.File

/**
 * 描述:
 * 时间: 2018/9/27 23:18
 * 作者: zengfansheng
 */
object MediaUtils {

    /**
     * 根据文件获取content uri
     * @param context 上下文
     * @param baseUri 父uri
     * @param file 文件
     */
    private fun getContentUri(context: Context, baseUri: Uri, file: File): Uri? {
        val filePath = file.absolutePath
        val cursor = context.contentResolver.query(baseUri, arrayOf(BaseColumns._ID),
                MediaStore.MediaColumns.DATA + "=? ", arrayOf(filePath), null)
        return if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            cursor.close()
            Uri.withAppendedPath(baseUri, "" + id)
        } else {
            if (file.exists()) {
                val values = ContentValues()
                values.put(MediaStore.MediaColumns.DATA, filePath)
                context.contentResolver.insert(baseUri, values)
            } else {
                null
            }
        }
    }

    /**
     * 获取视频文件的content uri
     * @param file 文件
     */
    fun getVideoContentUri(context: Context, file: File): Uri? {
        return getContentUri(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, file)
    }

    /**
     * 获取图片文件的content uri
     * @param file 文件
     */
    fun getImageContentUri(context: Context, file: File): Uri? {
        return getContentUri(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, file)
    }

    /**
     * 获取音频文件的content uri
     * @param file 文件
     */
    fun getAudioContentUri(context: Context, file: File): Uri? {
        return getContentUri(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, file)
    }
}
