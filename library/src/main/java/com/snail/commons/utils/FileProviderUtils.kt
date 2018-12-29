package com.snail.commons.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider

import java.io.File

/**
 * 描述:
 * 时间: 2018/6/9 09:09
 * 作者: zengfansheng
 */
object FileProviderUtils {

    /**
     * 兼容Android7.0以上，获取Intent传递的File的Uri
     */
    fun getUriForFile(context: Context, file: File): Uri {
        // 判断版本大于等于7.0
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        } else {
            Uri.fromFile(file)
        }
    }

    fun setIntentDataAndType(context: Context, intent: Intent, type: String, file: File, writeable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(getUriForFile(context, file), type)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (writeable) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type)
        }
    }
}
