package com.snail.java.network.converter

import com.alibaba.fastjson.JSON
import com.snail.java.network.exception.ConvertException
import okhttp3.ResponseBody

/**
 *
 *
 * date: 2019/3/1 09:10
 * author: zengfansheng
 */
class JsonResponseConverter<T>(private val cls: Class<T>) : ResponseConverter<T> {
    override fun convert(value: ResponseBody): T {
        try {
            return JSON.parseObject(value.string(), cls)
        } catch (e: Throwable) {
            throw ConvertException(e.message, e)
        }
    }
}