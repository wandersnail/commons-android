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
    fun setOnRequestResultListener(listener: OnRequestResultListener) {
        requestResultListener = listener
    }

    /**
     * 开始检查并申请权限
     * @param permissions 需要申请的权限
     */
    fun check(permissions: List<String>) {
        refusedPermissions.clear()
        allPermissions.clear()
        allPermissions.addAll(permissions)
        checkPermissions(allPermissions)
    }

    private fun checkPermissions(permissions: MutableList<String>) {
        if (permissions.remove(Manifest.permission.WRITE_SETTINGS) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(activity)) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + activity.packageName))
                activity.startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS)
                return
            }
        }
        if (permissions.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.packageManager.canRequestPackageInstalls()) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + activity.packageName))
                activity.startActivityForResult(intent, REQUEST_CODE_UNKNOWN_APP_SOURCES)
                return
            }
        }
        val needRequestPermissonList = findDeniedPermissions(permissions)
        if (needRequestPermissonList.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, needRequestPermissonList.toTypedArray(), PERMISSON_REQUESTCODE)
        } else if (requestResultListener != null) {
            requestResultListener!!.onRequestResult(refusedPermissions)
        }
    }

    //获取权限集中需要申请权限的列表
    private fun findDeniedPermissions(permissions: List<String>): List<String> {
        val needRequestPermissonList = ArrayList<String>()
        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED || ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
                needRequestPermissonList.add(perm)
            }
        }
        return needRequestPermissonList
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(activity)) {
                refusedPermissions.add(Manifest.permission.WRITE_SETTINGS)
            }
            checkPermissions(allPermissions)
        }
        if (requestCode == REQUEST_CODE_UNKNOWN_APP_SOURCES && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.packageManager.canRequestPackageInstalls()) {
                refusedPermissions.add(Manifest.permission.REQUEST_INSTALL_PACKAGES)
            }
            checkPermissions(allPermissions)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, paramArrayOfInt: IntArray) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                if (allPermissions.remove(permission) && paramArrayOfInt[i] != PackageManager.PERMISSION_GRANTED) {
                    refusedPermissions.add(permission)
                }
            }
            if (requestResultListener != null) {
                requestResultListener!!.onRequestResult(refusedPermissions)
            }
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
