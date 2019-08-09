package com.snail.commons.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态申请权限
 * 
 * date: 2019/8/6 15:33
 * author: zengfansheng
 */
public class PermissionsRequester {
    private static final int PERMISSON_REQUESTCODE = 0;
    private static final int REQUEST_CODE_WRITE_SETTINGS = 0;
    private static final int REQUEST_CODE_UNKNOWN_APP_SOURCES = 0;
    
    private final List<String> allPermissions = new ArrayList<>();
    private final List<String> refusedPermissions = new ArrayList<>();
    private Callback callback;
    private Activity activity;
    private Fragment fragment;

    public PermissionsRequester(@NonNull Activity activity) {
        this.activity = activity;
    }
    
    public PermissionsRequester(@NonNull Fragment fragment) {
        this.fragment = fragment;
    }
    
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * 开始检查并申请权限
     * @param permissions 需要申请的权限
     */
    public void checkAndRequest(@NonNull List<String> permissions) {
        refusedPermissions.clear();
        allPermissions.clear();
        allPermissions.addAll(permissions);
        checkPermissions(allPermissions, false);
    }

    public boolean hasPermissions(@NonNull List<String> permissions) {
        return checkPermissions(permissions, true);
    }
    
    @SuppressWarnings("all")
    private boolean checkPermissions(List<String> permissions, boolean onlyCheck) {
        Context context = activity != null ? activity : fragment.getContext();
        if (context == null) return false;
        if (permissions.remove(Manifest.permission.WRITE_SETTINGS) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                if (!onlyCheck) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + context.getPackageName()));
                    if (activity != null) {
                        activity.startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
                    } else {
                        fragment.startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
                    }
                }
                return false;
            }
        }
        if (permissions.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.getPackageManager().canRequestPackageInstalls()) {
                if (!onlyCheck) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + context.getPackageName()));
                    if (activity != null) {
                        activity.startActivityForResult(intent, REQUEST_CODE_UNKNOWN_APP_SOURCES);
                    } else {
                        fragment.startActivityForResult(intent, REQUEST_CODE_UNKNOWN_APP_SOURCES);
                    }
                }
                return false;
            }
        }
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (onlyCheck) {
            return needRequestPermissonList.isEmpty();
        } else if (!needRequestPermissonList.isEmpty()) {
            if (activity != null) {
                ActivityCompat.requestPermissions(activity, needRequestPermissonList.toArray(new String[0]), PERMISSON_REQUESTCODE);
            } else {
                fragment.requestPermissions(needRequestPermissonList.toArray(new String[0]), PERMISSON_REQUESTCODE);
            }
            return false;
        } else {
            if (callback != null) {
                callback.onRequestResult(refusedPermissions);
            }
            return true;
        }
    }

    //获取权限集中需要申请权限的列表
    private List<String> findDeniedPermissions(List<String> permissions) {
        List<String> needRequestPermissonList = new ArrayList<>();
        Activity activity = this.activity != null ? this.activity : fragment.getActivity();
        if (activity != null) {
            for (String perm : permissions) {
                if (ContextCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED || 
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
                    needRequestPermissonList.add(perm);
                }
            }
        }
        return needRequestPermissonList;
    }

    public void onActivityResult(int requestCode) {
        Context context = activity != null ? activity : fragment.getContext();
        if (context == null) return;
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                refusedPermissions.add(Manifest.permission.WRITE_SETTINGS);
            }
            checkPermissions(allPermissions, false);
        }
        if (requestCode == REQUEST_CODE_UNKNOWN_APP_SOURCES && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.getPackageManager().canRequestPackageInstalls()) {
                refusedPermissions.add(Manifest.permission.REQUEST_INSTALL_PACKAGES);
            }
            checkPermissions(allPermissions, false);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (allPermissions.remove(permission) && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    refusedPermissions.add(permission);
                }
            }
            if (callback != null) {
                callback.onRequestResult(refusedPermissions);
            }
        }
    }
    
    public interface Callback {
        /**
         * 请求结果
         * @param refusedPermissions 被拒绝的权限集合。size == 0时，表明申请的权限全部允许了
         */
        void onRequestResult(List<String> refusedPermissions);
    }
}
