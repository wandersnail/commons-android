package com.snail.java.network.callback

/**
 * 请求结果回调
 *
 * date: 2019/5/1 10:12
 * author: zengfansheng
 */
interface RequestCallback<T> {
    fun onSuccess(parsedResp: T)
    
    fun onError(t: Throwable)
}