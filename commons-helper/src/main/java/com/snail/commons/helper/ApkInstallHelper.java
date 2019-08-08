package com.snail.commons.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.snail.commons.util.FileUtils;

import java.io.File;
import java.util.Objects;

/**
 * apk安装帮助类
 * <p>
 * date: 2019/8/6 14:03
 * author: zengfansheng
 */
public class ApkInstallHelper {
    private static final int REQUEST_CODE = 3984;
    private final Activity activity;
    private File apkFile;

    public ApkInstallHelper(@NonNull Activity activity) {
        this.activity = activity;
    }

    /**
     * 如果是Android8.0以上需要在Activity中的onActivityResult调用此方法
     */
    public void onActivityResult(int requestCode) {
        if (requestCode == REQUEST_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (activity.getPackageManager().canRequestPackageInstalls()) {
                install();
            }
        }
    }

    /**
     * 安装apk
     */
    public void install(@NonNull File apkFile) {
        Objects.requireNonNull(apkFile, "apkFile is null");
        this.apkFile = apkFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !activity.getPackageManager().canRequestPackageInstalls()) {
            Uri uri = Uri.parse("package:" + activity.getPackageName());
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri);
            activity.startActivityForResult(intent, REQUEST_CODE);
        } else {
            install();
        }
    }

    private void install() {
        if (apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            FileUtils.setIntentDataAndType(apkFile, activity, intent, "application/vnd.android.package-archive", false);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    }
}
