package com.snail.java.network.converter

import com.alibaba.fastjson.JSON
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter

/**
 * 使用阿里巴巴的FastJson转换请求体
 *
 * date: 2019/4/14 17:42
 * author: zengfansheng
 */
class FastJsonRequestBodyConverter : Converter<Any, RequestBody> {
    override fun convert(value: Any): RequestBody? {
        val mediaType = MediaType.parse("application/json; charset=UTF-8")
        return RequestBody.create(mediaType, JSON.toJSONBytes(value))
    }
}