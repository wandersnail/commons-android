package com.snail.network.utils

import com.snail.java.network.converter.Converter
import io.reactivex.Observable
import okhttp3.ResponseBody



/**
 * 描述:
 * 时间: 2018/12/24 11:07
 * 作者: zengfansheng
 */
object HttpUtils {    

    /**
     * 截取baseurl
     */
    @JvmStatic
    fun getBaseUrl(url: String): String {
        var index = url.indexOf("://")
        val subUrl = url.substring(index + 3)
        val urlHead = url.substring(0, index + 3)
        index = subUrl.indexOf("/")
        return if (index != -1) {
            urlHead + subUrl.substring(0, index)
        } else url
    }

    internal fun <T> convertObservable(observable: Observable<ResponseBody>, converter: Converter<ResponseBody, T>): Observable<T> {
        return observable.map { converter.convert(it) }
    }
}
