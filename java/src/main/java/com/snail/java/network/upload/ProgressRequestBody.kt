package com.snail.java.network.upload

import com.snail.java.network.callback.ProgressListener
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.internal.Util
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

/**
 *
 *
 * date: 2019/2/28 12:25
 * author: zengfansheng
 */
internal class ProgressRequestBody(private val contentType: MediaType?, private val file: File, private val listener: ProgressListener?) : RequestBody() {
    override fun contentType(): MediaType? {
        return contentType
    }

    override fun contentLength(): Long {
        return file.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        var inputStream: InputStream? = null
        var uploaded = 0L
        try {
            val buffer = ByteArray(50000)//一次上传多少
            inputStream = FileInputStream(file)
            var len = inputStream.read(buffer)
            while (len != -1) {
                sink.write(buffer, 0, len)
                uploaded += len
                listener?.onProgress(uploaded, contentLength())
                len = inputStream.read(buffer)
            }
        } finally {
            Util.closeQuietly(inputStream)
        }
    }
}