package com.snail.commons.entity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 动态申请权限
 * 时间: 2018/7/14 15:17
 * 作者: zengfansheng
 */
public class PermissionsRequester {
    private static final int PERMISSON_REQUESTCODE = 0;
    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;
    private static final int REQUEST_CODE_UNKNOWN_APP_SOURCES = 2;
    private Activity activity;
    private List<String> allPermissions = new ArrayList<>();
    private List<String> refusedPermissions = new ArrayList<>();
    private OnRequestResultListener requestResultListener;

    public PermissionsRequester(@NonNull Activity activity) {
        this.activity = activity;
    }

    /**
     * 设置请求结果监听回调
     */
    public void setOnRequestResultListener(OnRequestResultListener listener) {
        requestResultListener = listener;
    }

    /**
     * 开始检查并申请权限
     * @param permissions 需要申请的权限
     */
    public void check(@NonNull List<String> permissions) {
        refusedPermissions.clear();
        allPermissions.clear();
        allPermissions.addAll(permissions);
        checkPermissions(allPermissions);        
    }

    private void checkPermissions(List<String> permissions) {
        if (permissions.remove(Manifest.permission.WRITE_SETTINGS) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
                return;
            }
        }
        if (permissions.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.getPackageManager().canRequestPackageInstalls()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, REQUEST_CODE_UNKNOWN_APP_SOURCES);
                return;
            }
        }
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(activity, needRequestPermissonList.toArray(new String[0]), PERMISSON_REQUESTCODE);
        } else if (requestResultListener != null) {
            requestResultListener.onRequestResult(refusedPermissions);
        }
    }

    //获取权限集中需要申请权限的列表
    private List<String> findDeniedPermissions(List<String> permissions) {
        List<String> needRequestPermissonList = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(activity)) {
                refusedPermissions.add(Manifest.permission.WRITE_SETTINGS);
            }
            checkPermissions(allPermissions);
        }
        if (requestCode == REQUEST_CODE_UNKNOWN_APP_SOURCES && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.getPackageManager().canRequestPackageInstalls()) {
                refusedPermissions.add(Manifest.permission.REQUEST_INSTALL_PACKAGES);
            }
            checkPermissions(allPermissions);
        }
    }
    
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (allPermissions.remove(permission) && paramArrayOfInt[i] != PackageManager.PERMISSION_GRANTED) {
                    refusedPermissions.add(permission);
                }
            }
            if (requestResultListener != null) {
                requestResultListener.onRequestResult(refusedPermissions);
            }
        }
    }
    
    public interface OnRequestResultListener {
        /**
         * 请求结果
         * @param refusedPermissions 被拒绝的权限集合。size==0时，表明申请的权限全部允许了
         */
        void onRequestResult(@NonNull List<String> refusedPermissions);
    }
}
