package com.snail.java.network.factory

import com.snail.java.network.converter.FastJsonRequestBodyConverter
import com.snail.java.network.converter.FastJsonResponseBodyConverter
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


/**
 * 使用阿里巴巴的FastJson实现的转换工厂类
 *
 * date: 2019/4/14 17:33
 * author: zengfansheng
 */
class FastJsonConverterFactory private constructor() : Converter.Factory() {
    companion object {
        @JvmStatic
        fun create(): FastJsonConverterFactory {
            return FastJsonConverterFactory()
        }
    }

    /**
     * 需要重写父类中responseBodyConverter，该方法用来转换服务器返回数据
     */
    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *> {
        return FastJsonResponseBodyConverter(type)
    }

    /**
     * 需要重写父类中responseBodyConverter，该方法用来转换发送给服务器的数据
     */
    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>, retrofit: Retrofit): Converter<*, RequestBody> {
        return FastJsonRequestBodyConverter()
    }
}