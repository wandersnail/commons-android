package com.snail.java.network.converter

import okhttp3.ResponseBody

/**
 * 响应体的字符串
 *
 * date: 2019/2/27 23:49
 * author: zengfansheng
 */
class StringResponseConverter : ResponseConverter<String> {
    override fun convert(value: ResponseBody): String {
        return value.string()
    }
}