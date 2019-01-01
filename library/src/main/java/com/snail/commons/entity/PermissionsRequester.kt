package com.snail.commons.entity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import java.util.*

/**
 * 描述: 动态申请权限
 * 时间: 2018/7/14 15:17
 * 作者: zengfansheng
 */
class PermissionsRequester(private val activity: Activity) {
    private val allPermissions = ArrayList<String>()
    private val refusedPermissions = ArrayList<String>()
    private var requestResultListener: OnRequestResultListener? = null

    /**
     * 设置请求结果监听回调
     */
    fun setOnRequestResultListener(listener: OnRequestResultListener?) {
        requestResultListener = listener
    }

    /**
     * 开始检查并申请权限
     * @param permissions 需要申请的权限
     */
    fun checkAndRequest(permissions: MutableList<String>) {
        refusedPermissions.clear()
        allPermissions.clear()
        allPermissions.addAll(permissions)
        checkPermissions(allPermissions, false)
    }
    
    fun hasPermissions(permissions: MutableList<String>): Boolean {
        return checkPermissions(permissions, true)
    }

    private fun checkPermissions(permissions: MutableList<String>, onlyCheck: Boolean): Boolean {
        if (permissions.remove(Manifest.permission.WRITE_SETTINGS) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(activity)) {
                if (!onlyCheck) {
                    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + activity.packageName))
                    activity.startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS)
                }
                return false
            }
        }
        if (permissions.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.packageManager.canRequestPackageInstalls()) {
                if (!onlyCheck) {
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + activity.packageName))
                    activity.startActivityForResult(intent, REQUEST_CODE_UNKNOWN_APP_SOURCES)
                }
                return false
            }
        }
        val needRequestPermissonList = findDeniedPermissions(permissions)
        return when {
            onlyCheck -> needRequestPermissonList.isEmpty()
            needRequestPermissonList.isNotEmpty() -> {
                ActivityCompat.requestPermissions(activity, needRequestPermissonList.toTypedArray(), PERMISSON_REQUESTCODE)
                false
            }
            else -> {
                requestResultListener?.onRequestResult(refusedPermissions)
                true
            }
        }
    }

    //获取权限集中需要申请权限的列表
    private fun findDeniedPermissions(permissions: MutableList<String>): MutableList<String> {
        val needRequestPermissonList = ArrayList<String>()
        permissions.forEach { perm ->
            if (ContextCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED || ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
                needRequestPermissonList.add(perm)
            }
        }
        return needRequestPermissonList
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(activity)) {
                refusedPermissions.add(Manifest.permission.WRITE_SETTINGS)
            }
            checkPermissions(allPermissions, false)
        }
        if (requestCode == REQUEST_CODE_UNKNOWN_APP_SOURCES && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.packageManager.canRequestPackageInstalls()) {
                refusedPermissions.add(Manifest.permission.REQUEST_INSTALL_PACKAGES)
            }
            checkPermissions(allPermissions, false)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, paramArrayOfInt: IntArray?) {
        if (requestCode == PERMISSON_REQUESTCODE && paramArrayOfInt != null) {
            permissions?.forEachIndexed { i, permission ->
                if (allPermissions.remove(permission) && paramArrayOfInt[i] != PackageManager.PERMISSION_GRANTED) {
                    refusedPermissions.add(permission)
                }
            }
            requestResultListener?.onRequestResult(refusedPermissions)
        }
    }

    interface OnRequestResultListener {
        /**
         * 请求结果
         * @param refusedPermissions 被拒绝的权限集合。size==0时，表明申请的权限全部允许了
         */
        fun onRequestResult(refusedPermissions: List<String>)
    }

    companion object {
        private const val PERMISSON_REQUESTCODE = 0
        private const val REQUEST_CODE_WRITE_SETTINGS = 1
        private const val REQUEST_CODE_UNKNOWN_APP_SOURCES = 2
    }
}
