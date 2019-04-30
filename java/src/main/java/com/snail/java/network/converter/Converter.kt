package com.snail.java.network.converter

import com.snail.java.network.exception.ConvertException

/**
 *
 *
 * date: 2019/2/26 12:26
 * author: zengfansheng
 * 
 * @property S 待转源
 * @property T 转换目录
 */
interface Converter<S, T> {
    @Throws(ConvertException::class)
    fun convert(value: S): T
}