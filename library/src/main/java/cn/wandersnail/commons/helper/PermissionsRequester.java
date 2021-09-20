package cn.wandersnail.commons.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

/**
 * 动态申请权限
 * 
 * date: 2019/8/6 15:33
 * author: zengfansheng
 */
public class PermissionsRequester extends BasePermissionsRequester {
    private static final int PERMISSION_REQUEST_CODE = 10;
    private static final int REQUEST_CODE_WRITE_SETTINGS = 11;
    private static final int REQUEST_CODE_UNKNOWN_APP_SOURCES = 12;
    
    private Activity activity;
    private Fragment fragment;

    public PermissionsRequester(@NonNull Activity activity) {
        this.activity = activity;
    }
    
    public PermissionsRequester(@NonNull Fragment fragment) {
        this.fragment = fragment;
    }


    @NonNull
    @Override
    protected Activity getActivity() {
        return activity != null ? activity : fragment.requireActivity();
    }

    @Override
    protected void requestWriteSettingsPermission() {
        Context context = activity != null ? activity : fragment.getContext();
        if (context == null) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + context.getPackageName()));
            if (activity != null) {
                activity.startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
            } else {
                fragment.startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
            }
        }
    }

    @Override
    protected void requestInstallPackagesPermission() {
        Context context = activity != null ? activity : fragment.getContext();
        if (context == null) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + context.getPackageName()));
            if (activity != null) {
                activity.startActivityForResult(intent, REQUEST_CODE_UNKNOWN_APP_SOURCES);
            } else {
                fragment.startActivityForResult(intent, REQUEST_CODE_UNKNOWN_APP_SOURCES);
            }
        }
    }

    @Override
    protected void requestOtherPermissions(@NonNull List<String> permissions) {
        if (activity != null) {
            ActivityCompat.requestPermissions(activity, permissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        } else {
            fragment.requestPermissions(permissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
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
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (allPermissions.remove(permission) && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    refusedPermissions.add(permission);
                }
            }
            if (callback != null && checking) {
                callback.onRequestResult(refusedPermissions);
            }
            checking = false;
        }
    }
}
