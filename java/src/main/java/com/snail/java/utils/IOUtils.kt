package com.snail.java.utils

import java.io.*
import java.nio.charset.Charset

object IOUtils {

    /**
     * 关闭一个或多个流对象
     * @param closeables 可关闭的流对象列表
     */
    @Throws(IOException::class)
    @JvmStatic 
    fun close(vararg closeables: Closeable?) {
        closeables.forEach { it?.close() }
    }

    /**
     * 关闭一个或多个流对象，内部捕获IO异常
     * @param closeables 可关闭的流对象列表
     */
    @JvmStatic 
    fun closeQuietly(vararg closeables: Closeable?) {
        try {
            close(*closeables)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }    
}

/**
 * 将输入流数据写入输出流中
 * 
 * @param out 输出流
 */
@JvmOverloads
@Throws(IOException::class)
fun InputStream.writeTo(out: OutputStream, bufferSize: Int = 10240) = copyTo(out, bufferSize)

/**
 * 从输入流中获取字符串，不主动关闭输入流
 *
 * @param charset 返回的字符串采用的字符集, 如果为null则使用平台默认的字符集
 */
fun InputStream.toString(charset: Charset): String? {
    val out = ByteArrayOutputStream()
    try {
        val buf = ByteArray(10240)
        var len = read(buf)
        while (len != -1) {
            out.write(buf, 0, len)
            len = read(buf)
        }
        val s = out.toString(charset.name())
        out.close()
        return s
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}