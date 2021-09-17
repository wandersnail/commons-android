package cn.wandersnail.commons.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;

import cn.wandersnail.commons.util.FileUtils;

/**
 * apk安装器，需ActivityResultCaller，注意API版本
 * 
 * date: 2021/9/17 14:25
 * author: zengfansheng
 */
public class ApkInstaller {
    private ComponentActivity activity;
    private Fragment fragment;
    private File apkFile;
    private final ActivityResultLauncher<Intent> launcher;

    /**
     * 实例化必须在activity的onCreate()方法里进行
     */
    public ApkInstaller(@NonNull ComponentActivity activity) {
        this.activity = activity;
        launcher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            onActivityResult();
        });
    }

    /**
     * 实例化必须在fragment的onCreate()、onAttach()方法里进行
     */
    public ApkInstaller(@NonNull Fragment fragment) {
        this.fragment = fragment;
        launcher = fragment.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            onActivityResult();
        });
    }
    
    @Nullable
    private Activity getActivity() {
        if (activity != null) {
            return activity;
        } else if (fragment.getActivity() != null) {
            return fragment.getActivity();
        } else {
            return null;
        }
    }
    
    private void onActivityResult() {
        Activity activity = getActivity();
        if (activity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (activity.getPackageManager().canRequestPackageInstalls()) {
                install(activity);
            }
        }
    }

    /**
     * 安装apk
     */
    public void install(@NonNull File apkFile) {
        this.apkFile = apkFile;
        Activity activity = getActivity();
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && 
                    !activity.getPackageManager().canRequestPackageInstalls()) {
                Uri uri = Uri.parse("package:" + activity.getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri);
                launcher.launch(intent);
            } else {
                install(activity);
            }
        }
    }

    private void install(Activity activity) {
        if (apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            FileUtils.setIntentDataAndType(apkFile, activity, intent, "application/vnd.android.package-archive", false);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    }
    
    public void destroy() {
        launcher.unregister();
    }
}
