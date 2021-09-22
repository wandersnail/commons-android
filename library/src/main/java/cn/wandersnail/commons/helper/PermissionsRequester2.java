package cn.wandersnail.commons.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

/**
 * 动态申请权限，需ActivityResultCaller，注意API版本
 * 
 * date: 2021/9/17 16:59
 * author: zengfansheng
 */
public class PermissionsRequester2 extends BasePermissionsRequester {    
    private final ComponentActivity activity;    
    private final ActivityResultLauncher<Intent> writeSettingsLauncher;
    private final ActivityResultLauncher<Intent> installPackagesLauncher;
    private final ActivityResultLauncher<String[]> permissionsLauncher;

    /**
     * 实例化必须在activity的onCreate()方法里进行
     */
    public PermissionsRequester2(@NonNull ComponentActivity activity) {
        this.activity = activity;
        writeSettingsLauncher = registerWriteSettingsLauncher();
        installPackagesLauncher = registerInstallPackagesLauncher();
        permissionsLauncher = registerPermissionsLauncher();
    }

    private ActivityResultLauncher<Intent> registerWriteSettingsLauncher() {
        return activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(activity)) {
                    refusedPermissions.add(Manifest.permission.WRITE_SETTINGS);
                }
            }
            checkPermissions(allPermissions, false);
        });
    }
    
    private ActivityResultLauncher<Intent> registerInstallPackagesLauncher() {
        return activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!activity.getPackageManager().canRequestPackageInstalls()) {
                    refusedPermissions.add(Manifest.permission.REQUEST_INSTALL_PACKAGES);
                }
            }
            checkPermissions(allPermissions, false);
        });
    }
    
    private ActivityResultLauncher<String[]> registerPermissionsLauncher() {
        return activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        if (allPermissions.remove(entry.getKey()) && !Boolean.TRUE.equals(entry.getValue())) {
                            refusedPermissions.add(entry.getKey());
                        }
                    }
                    if (callback != null && checking) {
                        callback.onRequestResult(refusedPermissions);
                    }
                    checking = false;
                });
    }

    @NonNull
    @Override
    protected Activity getActivity() {
        return activity;
    }

    @Override
    protected void requestWriteSettingsPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
            writeSettingsLauncher.launch(intent);
        }
    }

    @Override
    protected void requestInstallPackagesPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + activity.getPackageName()));
            installPackagesLauncher.launch(intent);
        }
    }

    @Override
    protected void requestOtherPermissions(@NonNull List<String> permissions) {
        permissionsLauncher.launch(permissions.toArray(new String[0]));
    }
}
