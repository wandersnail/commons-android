package cn.wandersnail.commons.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * date: 2021/9/17 18:28
 * author: zengfansheng
 */
public abstract class BasePermissionsRequester {
    protected final List<String> allPermissions = new ArrayList<>();
    protected final List<String> refusedPermissions = new ArrayList<>();
    protected Callback callback;
    protected boolean checking;
    
    BasePermissionsRequester() {}

    @NonNull
    protected abstract Activity getActivity();
    
    protected abstract void requestWriteSettingsPermission();

    protected abstract void requestInstallPackagesPermission();

    protected abstract void requestManageExternalStoragePermission();
    
    protected abstract void requestOtherPermissions(@NonNull List<String> permissions);
    
    public void setCallback(@Nullable Callback callback) {
        this.callback = callback;
    }

    /**
     * 开始检查并申请权限
     * @param permissions 需要申请的权限
     */
    public void checkAndRequest(@NonNull List<String> permissions) {
        checkPermissionsRegisterInManifest(permissions);
        if (checking) {
            return;
        }
        checking = true;
        refusedPermissions.clear();
        allPermissions.clear();
        allPermissions.addAll(permissions);
        checkPermissions(allPermissions, false);
    }

    private List<String> getManifestPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return Arrays.asList(pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions);
        } catch (Exception e) {
            return null;
        }
    }

    private void checkPermissionsRegisterInManifest(List<String> requestPermissions) {
        List<String> manifest = getManifestPermissions(getActivity());
        if (manifest != null && !manifest.isEmpty()) {
            for (String permission : requestPermissions) {
                if (!manifest.contains(permission)) {
                    throw new RuntimeException(permission + " 权限未在AndroidManifest中注册");
                }
            }
        }
    }
    
    public boolean hasPermissions(@NonNull List<String> permissions) {
        return checkPermissions(permissions, true);
    }

    @SuppressWarnings("all")
    protected boolean checkPermissions(List<String> permissions, boolean onlyCheck) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions.remove(Manifest.permission.WRITE_SETTINGS)) {
            if (!Settings.System.canWrite(getActivity())) {
                if (!onlyCheck) {
                    requestWriteSettingsPermission();
                    checking = true;
                }
                return false;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && permissions.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
            if (!getActivity().getPackageManager().canRequestPackageInstalls()) {
                if (!onlyCheck) {
                    requestInstallPackagesPermission();
                    checking = true;
                }
                return false;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && permissions.remove(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
            if (!Environment.isExternalStorageManager()) {
                if (!onlyCheck) {
                    requestManageExternalStoragePermission();
                    checking = true;
                }
                return false;
            }
        }
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (onlyCheck) {
            return needRequestPermissonList.isEmpty();
        } else if (!needRequestPermissonList.isEmpty()) {
            requestOtherPermissions(needRequestPermissonList);
            return false;
        } else {
            if (callback != null && checking) {
                callback.onRequestResult(refusedPermissions);
            }
            checking = false;
            return true;
        }
    }

    //获取权限集中需要申请权限的列表
    private List<String> findDeniedPermissions(List<String> permissions) {
        List<String> needRequestPermissionList = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), perm) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), perm)) {
                needRequestPermissionList.add(perm);
            }
        }
        return needRequestPermissionList;
    }
    
    public interface Callback {
        /**
         * 请求结果
         * @param refusedPermissions 被拒绝的权限集合。size == 0时，表明申请的权限全部允许了
         */
        void onRequestResult(List<String> refusedPermissions);
    }
}
