package com.zfs.commons.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;

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

    private static String getSignature(Signature signature, String algorithm, String separator) {
        String code = EncryptUtils.encryptByMessageDigest(signature.toByteArray(), algorithm);
        return EncryptUtils.addSeparator(code, separator);
    }

    private static String getSignature(PackageInfo info, String algorithm, String separator) {
        Signature signature;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (info.signingInfo.hasMultipleSigners()) {
                signature = info.signingInfo.getApkContentsSigners()[0];
            } else {
                signature = info.signingInfo.getSigningCertificateHistory()[0];
            }
        } else {
            signature = info.signatures[0];
        }
        return getSignature(signature, algorithm, separator);
    }

    /**
     * 从APK中读取签名
     *
     * @param algorithm 算法。{@link EncryptUtils#MD5}, {@link EncryptUtils#SHA1}
     * @param separator 用来分隔的字符串
     */
    public static String getSignatureFromApk(Context context, String apkPath, String algorithm, String separator) {
        try {
            PackageInfo packageSign;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageInfo info = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNING_CERTIFICATES);
                packageSign = context.getPackageManager().getPackageInfo(info.packageName, PackageManager.GET_SIGNING_CERTIFICATES);
                return getSignature(packageSign, algorithm, separator);
            } else {
                packageSign = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNATURES);
                return getSignature(packageSign, algorithm, separator);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            List<PackageInfo> infos;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                infos = context.getPackageManager().getInstalledPackages(PackageManager.GET_SIGNING_CERTIFICATES);
            } else {
                infos = context.getPackageManager().getInstalledPackages(PackageManager.GET_SIGNATURES);
            }
            for (PackageInfo info : infos) {
                if (info.packageName.equals(packageName)) {
                    return getSignature(info, algorithm, separator);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
