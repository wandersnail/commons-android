package cn.wandersnail.commons.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * date: 2019/8/7 17:22
 * author: zengfansheng
 */
public class SignUtils {
    public static class SignInfo {
        public int hashCode;
        public String md5;
        public String sha1;
        public Signature origin;
    }

    private static SignInfo getSignature(Signature signature) {
        SignInfo info = new SignInfo();
        info.origin = signature;
        info.hashCode = signature.hashCode();
        info.md5 = EncryptUtils.encryptByMessageDigest(signature.toByteArray(), EncryptUtils.MD5);
        if (info.md5 == null) {
            return null;
        }
        info.sha1 = EncryptUtils.encryptByMessageDigest(signature.toByteArray(), EncryptUtils.SHA1);
        return info.sha1 == null ? null : info;
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
    public static SignInfo getSignatureFromApk(@NonNull Context context, String apkPath) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageInfo info = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNING_CERTIFICATES);
                PackageInfo packageSign = context.getPackageManager().getPackageInfo(info.packageName, PackageManager.GET_SIGNING_CERTIFICATES);
                return getSignature(packageSign);
            } else {
                PackageInfo packageSign = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNATURES);
                return getSignature(packageSign);
            }
        } catch (Exception e) {
            Signature signature = getUninstallAPKSignature(apkPath);
            if (signature != null) {
                return getSignature(signature);
            }
        }
        return null;
    }

    @SuppressWarnings("all")
    @Nullable
    public static Signature getUninstallAPKSignature(@NonNull String apkPath) {
        String PATH_PackageParser = "android.content.pm.PackageParser";
        try {
            // apk包的文件路径
            // 这是一个Package 解释器, 是隐藏的
            // 构造函数的参数只有一个, apk文件的路径
            Class pkgParserCls = Class.forName(PATH_PackageParser);
            Class[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            // 这个是与显示有关的, 里面涉及到一些像素显示等等, 我们使用默认的情况
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            Constructor pkgParserCt = null;
            Object pkgParser = null;
            if (Build.VERSION.SDK_INT > 20) {
                pkgParserCt = pkgParserCls.getConstructor();
                pkgParser = pkgParserCt.newInstance();
                Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage", File.class, int.class);
                Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, new File(apkPath), PackageManager.GET_SIGNATURES);

                if (Build.VERSION.SDK_INT >= 28) {
                    Method collectCertificatesMtd = pkgParserCls.getDeclaredMethod("collectCertificates", pkgParserPkg.getClass(), Boolean.TYPE);
                    collectCertificatesMtd.invoke(pkgParser, pkgParserPkg, true);
                    Field mSigningDetailsField = pkgParserPkg.getClass().getDeclaredField("mSigningDetails"); // SigningDetails
                    mSigningDetailsField.setAccessible(true);
                    Object mSigningDetails = mSigningDetailsField.get(pkgParserPkg);
                    Field infoField = mSigningDetails.getClass().getDeclaredField("signatures");
                    infoField.setAccessible(true);
                    Signature[] info = (Signature[]) infoField.get(mSigningDetails);
                    return info[0];

                } else {
                    Method pkgParser_collectCertificatesMtd = pkgParserCls.getDeclaredMethod("collectCertificates", pkgParserPkg.getClass(), Integer.TYPE);
                    pkgParser_collectCertificatesMtd.invoke(pkgParser, pkgParserPkg, PackageManager.GET_SIGNATURES);
                    Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField("mSignatures");
                    Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
                    return info[0];
                }
            } else {
                pkgParserCt = pkgParserCls.getConstructor(typeArgs);
                pkgParser = pkgParserCt.newInstance(apkPath);
                Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage", File.class, String.class, DisplayMetrics.class, Integer.TYPE);
                Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, new File(apkPath), apkPath, metrics, PackageManager.GET_SIGNATURES);
                Method pkgParser_collectCertificatesMtd = pkgParserCls.getDeclaredMethod("collectCertificates", pkgParserPkg.getClass(), Integer.TYPE);
                pkgParser_collectCertificatesMtd.invoke(pkgParser, pkgParserPkg, PackageManager.GET_SIGNATURES);
                // 应用程序信息包, 这个公开的, 不过有些函数, 变量没公开
                Field packageInfoFld = pkgParserPkg.getClass().getDeclaredField("mSignatures");
                Signature[] info = (Signature[]) packageInfoFld.get(pkgParserPkg);
                return info[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 从已安装的应用读取签名
     */
    public static SignInfo getSignatureInstalled(@NonNull Context context) {
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
