package com.snail.java.utils

import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

/**
 * Created by zengfs on 2015/11/12.
 */
object StringUtils {

    /**
     * 生成一个uuid字符串，不带短杠
     */
    @JvmStatic
    fun randomUuid(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
}

/*############################################ 扩展函数 #########################################*/

/**
 * byte数组转换成16进制字符串
 * @param separator 用来分隔的字符串
 */
@JvmOverloads
fun ByteArray.toHexString(separator: String = ""): String  {
    if (isEmpty()) {
        return ""
    }
    val sb = StringBuilder()
    for (aSrc in this) {
        val v = aSrc.toInt() and 0xFF
        val hv = v.toString(16)
        if (hv.length < 2) {
            sb.append(0)
        }
        sb.append(hv)
        if (separator.isNotEmpty()) {
            sb.append(separator)
        }
    }
    var s = sb.toString().toUpperCase(Locale.ENGLISH)
    if (separator.isNotEmpty()) {
        s = s.substring(0, s.length - separator.length)
    }
    return s
}

/**
 * byte数组转换成2进制字符串
 * @param separator 用来分隔的字符串
 */
@JvmOverloads
fun ByteArray.toBinaryString(separator: String = ""): String  {
    if (isEmpty()) {
        return ""
    }
    val sb = StringBuilder()
    for (aSrc in this) {
        val v = aSrc.toInt() and 0xFF
        val hv = v.toString(2)
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
fun String.subZeroAndDot(): String {
    var s1 = this
    if (s1.indexOf(".") > 0) {
        s1 = s1.replace("0+?$".toRegex(), "")//去掉多余的0  
        s1 = s1.replace("[.]$".toRegex(), "")//如最后一位是.则去掉  
    }
    return s1
}

/**
 * 将异常信息转换成字符串
 */
fun Throwable.toDetailMsg(): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    printStackTrace(pw)
    pw.close()
    return sw.toString()
}

/**
 * 格式00:00:00
 * @param duration 时长，单位：秒
 */
@JvmOverloads
fun Int.toDuration(duration: Int, format: String = "%02d:%02d:%02d"): String {
    return String.format(Locale.US, format, duration / 3600, duration % 3600 / 60, duration % 60)
}