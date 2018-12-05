package com.zfs.commons.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * 描述: 签名信息获取工具
 * 时间: 2018/5/20 14:52
 * 作者: zengfansheng
 */
public class SignUtils {

    /**
     * 从APK中读取签名
     *
     * @param algorithm 算法。{@link EncryptUtils#MD5}, {@link EncryptUtils#SHA1}
     */
    public static String getSignatureFromApk(Context context, String apkPath, String algorithm) {
        return getSignatureFromApk(context, apkPath, algorithm, "");
    }
    
    /**
     * 从APK中读取签名
     *
     * @param algorithm 算法。{@link EncryptUtils#MD5}, {@link EncryptUtils#SHA1}
     * @param separator 用来分隔的字符串
     */
    public static String getSignatureFromApk(Context context, String apkPath, String algorithm, String separator) {
        PackageInfo packageSign = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNATURES);
        if (packageSign != null) {
            try {
                String code = EncryptUtils.encryptByMessageDigest(packageSign.signatures[0].toByteArray(), algorithm);
                if (code != null) {
                    return EncryptUtils.addSeparator(code, separator);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 从已安装的应用读取签名
     * @param algorithm 算法。{@link EncryptUtils#MD5}, {@link EncryptUtils#SHA1}
     */
    public static String getSignatureInstalled(Context context, String packageName, String algorithm) {
        return getSignatureInstalled(context, packageName, algorithm, "");
    }

    /**
     * 从已安装的应用读取签名
     * @param algorithm 算法。{@link EncryptUtils#MD5}, {@link EncryptUtils#SHA1}
     * @param separator 用来分隔的字符串
     */
    public static String getSignatureInstalled(Context context, String packageName, String algorithm, String separator) {
        try {
            List<PackageInfo> infos = context.getPackageManager().getInstalledPackages(PackageManager.GET_SIGNATURES);
            for (PackageInfo info : infos) {
                if (info.packageName.equals(packageName)) {
                    String code = EncryptUtils.encryptByMessageDigest(info.signatures[0].toByteArray(), algorithm);
                    if (code != null) {
                        return EncryptUtils.addSeparator(code, separator);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
