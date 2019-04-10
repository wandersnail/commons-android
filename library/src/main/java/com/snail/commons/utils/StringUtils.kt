package com.snail.commons.utils

import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

/**
 * Created by zengfs on 2015/11/12.
 */
object StringUtils {
    /**
     * byte数组转换成16进制字符串
     * @param src 源
     * @param separator 用来分隔的字符串
     */
    @JvmStatic
    fun bytesToHexString(src: ByteArray, separator: String = ""): String {
        if (src.isEmpty()) {
            return ""
        }
        val stringBuilder = StringBuilder()
        for (aSrc in src) {
            val v = aSrc.toInt() and 0xFF
            val hv = Integer.toHexString(v)
            if (hv.length < 2) {
                stringBuilder.append(0)
            }
            stringBuilder.append(hv)
            if (separator.isNotEmpty()) {
                stringBuilder.append(separator)
            }
        }
        var s = stringBuilder.toString().toUpperCase(Locale.ENGLISH)
        if (separator.isNotEmpty()) {
            s = s.substring(0, s.length - separator.length)
        }
        return s
    }

    /**
     * byte数组转换成2进制字符串
     */
    @JvmStatic
    fun bytesToBinaryString(src: ByteArray, separator: String = ""): String {
        if (src.isEmpty()) {
            return ""
        }
        val sb = StringBuilder()
        for (aSrc in src) {
            val v = aSrc.toInt() and 0xFF
            val hv = Integer.toBinaryString(v)
            for (i in 0 until 8 - hv.length) {
                sb.append(0)
            }
            sb.append(hv)
            if (separator.isNotEmpty()) {
                sb.append(separator)
            }
        }
        var s = sb.toString()
        if (separator.isNotEmpty()) {
            s = s.substring(0, s.length - separator.length)
        }
        return s
    }

    /**
     * 使用java正则表达式去掉小数点后多余的0，如最后一位是.则去掉
     */
    @JvmStatic
    fun subZeroAndDot(s: String): String {
        var s1 = s
        if (s1.indexOf(".") > 0) {
            s1 = s1.replace("0+?$".toRegex(), "")//去掉多余的0  
            s1 = s1.replace("[.]$".toRegex(), "")//如最后一位是.则去掉  
        }
        return s1
    }

    /**
     * 将异常信息转换成字符串
     */
    @JvmStatic
    fun getDetailMsg(t: Throwable?): String {
        if (t != null) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            t.printStackTrace(pw)
            pw.close()
            return sw.toString()
        }
        return ""
    }

    /**
     * 格式00:00:00
     * @param duration 时长，单位：秒
     */
    @JvmStatic
    fun formatDuration(duration: Int): String {
        return String.format(Locale.US, "%02d:%02d:%02d", duration / 3600, duration % 3600 / 60, duration % 60)
    }
}
