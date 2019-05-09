package com.snail.commons.entity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.*

/**
 * 描述: 动态申请权限
 * 时间: 2018/7/14 15:17
 * 作者: zengfansheng
 */
class PermissionsRequester {
    private val allPermissions = ArrayList<String>()
    private val refusedPermissions = ArrayList<String>()
    private var requestResultListener: OnRequestResultListener? = null
    private var activity: Activity? = null
    private var fragment: Fragment? = null
    
    constructor(activity: Activity) {
        this.activity = activity
    }
    
    constructor(fragment: Fragment) {
        this.fragment = fragment
    }

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
        val context: Context = if (activity != null) activity!! else fragment!!.context!!
        if (permissions.remove(Manifest.permission.WRITE_SETTINGS) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                if (!onlyCheck) {
                    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + context.packageName))
                    if (activity != null) {
                        activity!!.startActivityForResult(intent,
                            REQUEST_CODE_WRITE_SETTINGS
                        )
                    } else {
                        fragment!!.startActivityForResult(intent,
                            REQUEST_CODE_WRITE_SETTINGS
                        )
                    }
                }
                return false
            }            
        }
        if (permissions.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                if (!onlyCheck) {
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + context.packageName))
                    if (activity != null) {
                        activity!!.startActivityForResult(intent,
                            REQUEST_CODE_UNKNOWN_APP_SOURCES
                        )
                    } else {
                        fragment!!.startActivityForResult(intent,
                            REQUEST_CODE_UNKNOWN_APP_SOURCES
                        )
                    }
                }
                return false
            }
        }
        val needRequestPermissonList = findDeniedPermissions(permissions)
        return when {
            onlyCheck -> needRequestPermissonList.isEmpty()
            needRequestPermissonList.isNotEmpty() -> {
                if (activity != null) {
                    ActivityCompat.requestPermissions(activity!!, needRequestPermissonList.toTypedArray(),
                        PERMISSON_REQUESTCODE
                    )
                } else {
                    fragment!!.requestPermissions(needRequestPermissonList.toTypedArray(),
                        PERMISSON_REQUESTCODE
                    )
                }
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
        val activity = if (this.activity != null) this.activity!! else fragment!!.activity!!
        permissions.forEach { perm ->
            if (ContextCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED || ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
                needRequestPermissonList.add(perm)
            }
        }
        return needRequestPermissonList
    }

    fun onActivityResult(requestCode: Int) {
        val context: Context = if (activity != null) activity!! else fragment!!.context!!
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                refusedPermissions.add(Manifest.permission.WRITE_SETTINGS)
            }
            checkPermissions(allPermissions, false)
        }
        if (requestCode == REQUEST_CODE_UNKNOWN_APP_SOURCES && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
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
        fun onRequestResult(refusedPermissions: MutableList<String>)
    }

    companion object {
        private const val PERMISSON_REQUESTCODE = 0
        private const val REQUEST_CODE_WRITE_SETTINGS = 1
        private const val REQUEST_CODE_UNKNOWN_APP_SOURCES = 2
    }
}
