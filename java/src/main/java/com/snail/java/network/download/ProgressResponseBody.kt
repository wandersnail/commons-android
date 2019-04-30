package com.snail.java.network.download

import com.snail.java.network.callback.ProgressListener
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

import java.io.IOException

/**
 * 时间: 2017/7/6 14:50
 * 作者: zengfansheng
 * 
 * @param listener 进度监听
 */

internal class ProgressResponseBody(private val responseBody: ResponseBody, private val listener: ProgressListener?) : ResponseBody() {
    private var bufferedSource: BufferedSource? = null

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()))
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalReadBytes: Long = 0

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val readBytes = super.read(sink, byteCount)
                totalReadBytes += if (readBytes != -1L) readBytes else 0
                if (totalReadBytes > 0 && contentLength() > 0) {
                    listener?.onProgress(totalReadBytes, responseBody.contentLength())
                }
                return readBytes
            }
        }
    }
}
