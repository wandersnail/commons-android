package com.snail.java.network.upload

import com.snail.java.network.converter.ResponseConverter
import com.snail.java.network.TaskInfo
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.util.*

/**
 *
 *
 * date: 2019/2/28 13:00
 * author: zengfansheng
 */
open class UploadInfo<R> @JvmOverloads constructor(
    url: String,
    /** 待上传文件 */
        val file: File,
    /** 响应体转换器 */
        internal val converter: ResponseConverter<R>,
    /** 文件类型 */
        val mediaType: MediaType? = null,
    /** 请求参数 */
        val args: Map<String, @JvmSuppressWildcards RequestBody>? = null,
    tag: String = UUID.randomUUID().toString()) : TaskInfo(url, tag) {

    /**
     * 服务端响应数据
     */
    var response: R? = null
}