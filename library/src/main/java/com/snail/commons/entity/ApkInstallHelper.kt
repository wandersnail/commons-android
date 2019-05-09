package com.snail.commons.entity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.snail.commons.utils.setIntentDataAndType
import java.io.File

/**
 * apk安装帮助类
 *
 * date: 2019/5/8 10:42
 * author: zengfansheng
 */
class ApkInstallHelper(private val activity: Activity) {
    private var apkFile: File? = null
    /**
     * 如果是Android8.0以上需要在Activity中的onActivityResult调用此方法
     */
    fun onActivityResult(requestCode: Int) {
        if (requestCode == REQUEST_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (activity.packageManager.canRequestPackageInstalls()) {
                install()
            }
        }
    }

    /**
     * 只有下载的是apk文件时
     */
    fun install(apkFile: File) {
        this.apkFile = apkFile
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !activity.packageManager.canRequestPackageInstalls()) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + activity.packageName))
            activity.startActivityForResult(intent, REQUEST_CODE)
        } else {
            install()
        }
    }

    private fun install() {
        if (apkFile?.exists() == true) {
            val intent = Intent(Intent.ACTION_VIEW)
            apkFile!!.setIntentDataAndType(activity, intent, "application/vnd.android.package-archive", false)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
        }
    }

    companion object {
        private const val REQUEST_CODE = 3984
    }
}