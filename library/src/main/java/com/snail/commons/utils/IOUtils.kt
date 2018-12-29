package com.snail.commons.utils

import java.io.*

object IOUtils {
    /**
     * 将输入流数据写入输出流中
     * @param inputStream 输入流
     * @param out 输出流
     */
    @Throws(IOException::class)
    fun inToOut(inputStream: InputStream, out: OutputStream) {
        val buf = ByteArray(1024)
        var len = inputStream.read(buf)
        while (len != -1) {
            out.write(buf, 0, len)
            len = inputStream.read(buf)
        }
    }

    /**
     * 关闭一个或多个流对象
     * @param closeables 可关闭的流对象列表
     */
    @Throws(IOException::class)
    fun close(vararg closeables: Closeable?) {
        closeables.forEach { it?.close() }
    }

    /**
     * 关闭一个或多个流对象，内部捕获IO异常
     * @param closeables 可关闭的流对象列表
     */
    fun closeQuietly(vararg closeables: Closeable?) {
        try {
            close(*closeables)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 从输入流中获取字符串
     * @param inputStream 输入流
     * @param enc 返回的字符串采用的字符集, 如果为null则使用平台默认的字符集
     */
    fun toString(inputStream: InputStream, enc: String?): String? {
        val out = ByteArrayOutputStream()
        try {
            val buf = ByteArray(1024)
            var len = inputStream.read(buf)
            while (len != -1) {
                out.write(buf, 0, len)
                len = inputStream.read(buf)
            }
            val s: String = if (enc == null) {
                out.toString()
            } else {
                out.toString(enc)
            }
            out.close()
            return s
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}