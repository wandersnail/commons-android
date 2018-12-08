package com.zfs.commons.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * 描述: 签名信息获取工具
 * 时间: 2018/5/20 14:52
 * 作者: zengfansheng
 */
public class SignUtils {

    public static final class SignInfo {
        public int hashCode;
        public String md5;
        public String sha1;

        /**
         * 在MD5或SHA1加密过的字符串基础上加上分隔符
         * @param code MD5或SHA1加密过的字符串
         * @param separator 分隔符
         */
        public String addSeparator(@NonNull String code, @NonNull String separator) {
            return EncryptUtils.addSeparator(code, separator);
        }
    }
    
    private static SignInfo getSignature(Signature signature) {
        SignInfo info = new SignInfo();
        info.hashCode = signature.hashCode();
        info.md5 = EncryptUtils.encryptByMessageDigest(signature.toByteArray(), EncryptUtils.MD5);
        if (info.md5 == null) {
            return null;
        }
        info.sha1 = EncryptUtils.encryptByMessageDigest(signature.toByteArray(), EncryptUtils.SHA1);
        if (info.sha1 == null) {
            return null;
        }
        return info;
    }

    private static SignInfo getSignature(PackageInfo info) {
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
        return getSignature(signature);
    }

    /**
     * 从APK中读取签名
     */
    public static SignInfo getSignatureFromApk(Context context, String apkPath) {
        try {
            PackageInfo packageSign;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageInfo info = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNING_CERTIFICATES);
                packageSign = context.getPackageManager().getPackageInfo(info.packageName, PackageManager.GET_SIGNING_CERTIFICATES);
                return getSignature(packageSign);
            } else {
                packageSign = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNATURES);
                return getSignature(packageSign);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从已安装的应用读取签名
     */
    public static SignInfo getSignatureInstalled(Context context) {
        try {
            List<PackageInfo> infos;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                infos = context.getPackageManager().getInstalledPackages(PackageManager.GET_SIGNING_CERTIFICATES);
            } else {
                infos = context.getPackageManager().getInstalledPackages(PackageManager.GET_SIGNATURES);
            }
            for (PackageInfo info : infos) {
                if (info.packageName.equals(context.getPackageName())) {
                    return getSignature(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
