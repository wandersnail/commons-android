package com.snail.java.network

import retrofit2.Retrofit

/**
 * 配置
 *
 * date: 2019/4/1 11:17
 * author: zengfansheng
 */
class Configuration {
    var retrofit: Retrofit? = null
    internal var service: HttpService? = null
}