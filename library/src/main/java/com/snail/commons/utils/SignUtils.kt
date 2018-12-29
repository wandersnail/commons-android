package com.snail.commons.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build

/**
 * 描述: 签名信息获取工具
 * 时间: 2018/5/20 14:52
 * 作者: zengfansheng
 */
object SignUtils {

    class SignInfo {
        var hashCode: Int = 0
        var md5: String? = null
        var sha1: String? = null

        /**
         * 在MD5或SHA1加密过的字符串基础上加上分隔符
         * @param code MD5或SHA1加密过的字符串
         * @param separator 分隔符
         */
        fun addSeparator(code: String, separator: String): String? {
            return EncryptUtils.addSeparator(code, separator)
        }
    }

    private fun getSignature(signature: Signature): SignInfo? {
        val info = SignInfo()
        info.hashCode = signature.hashCode()
        info.md5 = EncryptUtils.encryptByMessageDigest(signature.toByteArray(), EncryptUtils.MD5)
        if (info.md5 == null) {
            return null
        }
        info.sha1 = EncryptUtils.encryptByMessageDigest(signature.toByteArray(), EncryptUtils.SHA1)
        return if (info.sha1 == null) {
            null
        } else info
    }

    private fun getSignature(info: PackageInfo): SignInfo? {
        val signature: Signature = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (info.signingInfo.hasMultipleSigners()) {
                info.signingInfo.apkContentsSigners[0]
            } else {
                info.signingInfo.signingCertificateHistory[0]
            }
        } else {
            info.signatures[0]
        }
        return getSignature(signature)
    }

    /**
     * 从APK中读取签名
     */
    fun getSignatureFromApk(context: Context, apkPath: String): SignInfo? {
        try {
            val packageSign: PackageInfo
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val info =
                    context.packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNING_CERTIFICATES)
                packageSign =
                        context.packageManager.getPackageInfo(info.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                getSignature(packageSign)
            } else {
                packageSign = context.packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_SIGNATURES)
                getSignature(packageSign)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 从已安装的应用读取签名
     */
    fun getSignatureInstalled(context: Context): SignInfo? {
        try {
            val infos: List<PackageInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getInstalledPackages(PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                context.packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES)
            }
            for (info in infos) {
                if (info.packageName == context.packageName) {
                    return getSignature(info)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
