package com.snail.commons.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import java.io.File
import java.util.*

/**
 * 描述: 调用系统分享工具
 * 时间: 2018/9/27 23:54
 * 作者: zengfansheng
 */
object SysShareUtils {
    private fun startShare(context: Context, intent: Intent, title: String, isFile: Boolean) {
        if (isFile && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }

    /**
     * 分享文本
     * @param context 上下文
     * @param title 系统分享对话框的标题
     * @param text 分享的内容
     */
    @JvmStatic
    fun shareText(context: Context, title: String, text: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startShare(context, intent, title, false)
    }

    /**
     * 分享单张图片
     * @param context 上下文
     * @param title 系统分享对话框的标题
     * @param file 文件
     */
    @JvmStatic
    fun shareImage(context: Context, title: String, file: File) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            MediaUtils.getImageContentUri(context, file)
        } else {
            Uri.fromFile(file)
        }
        if (uri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            startShare(context, intent, title, true)
        }
    }

    /**
     * 分享多张图片
     * @param context 上下文
     * @param title 系统分享对话框的标题
     * @param files 文件
     */
    @JvmStatic
    fun shareImages(context: Context, title: String, files: List<File>) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.type = "image/*"
        val imageUris = ArrayList<Uri>()
        for (file in files) {
            val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                MediaUtils.getImageContentUri(context, file)
            } else {
                Uri.fromFile(file)
            }
            if (uri != null) {
                imageUris.add(uri)
            }
        }
        if (imageUris.isNotEmpty()) {
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
            startShare(context, intent, title, true)
        }
    }

    /**
     * 分享视频
     * @param context 上下文
     * @param title 系统分享对话框的标题
     * @param file 文件
     */
    @JvmStatic
    fun shareVideo(context: Context, title: String, file: File) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "video/*"
        val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            MediaUtils.getVideoContentUri(context, file)
        } else {
            Uri.fromFile(file)
        }
        if (uri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            startShare(context, intent, title, true)
        }
    }

    /**
     * 分享文件
     * @param context 上下文
     * @param title 系统分享对话框的标题
     * @param file 文件
     */
    @JvmStatic
    fun shareFile(context: Context, title: String, file: File) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = FileUtils.getMimeType(file.absolutePath)
        intent.putExtra(Intent.EXTRA_STREAM, FileProviderUtils.getUriForFile(context, file))
        startShare(context, intent, title, true)
    }
}
