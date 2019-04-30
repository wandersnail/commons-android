package com.snail.java.network

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * 一般的请求任务
 *
 * date: 2019/4/1 11:47
 * author: zengfansheng
 */
internal class GeneralRequestTask<T>(private val configuration: Configuration, private val observer: Observer<T>?) : Observer<T> {    
    
    override fun onComplete() {
        observer?.onComplete()
    }

    override fun onSubscribe(d: Disposable) {
        observer?.onSubscribe(d)
    }

    override fun onNext(t: T) {
        observer?.onNext(t)
    }

    override fun onError(e: Throwable) {
        observer?.onError(e)
    }
}