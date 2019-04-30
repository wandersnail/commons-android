package com.snail.java.network.exception

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * 时间: 2017/7/8 22:16
 * 作者: zengfansheng
 * 功能:
 */

internal class RetryWhenException @JvmOverloads constructor(
    //重试次数    
    private val count: Int = 3, 
    //延时
    private val delay: Long = 3000,
    //叠加延迟
    private val increaseDelay: Long = 3000) : Function<Observable<out Throwable>, Observable<*>> {

    private class Wrapper constructor(val throwable: Throwable, val index: Int)

    @Throws(Exception::class)
    override fun apply(observable: Observable<out Throwable>): Observable<*> {
        return observable.zipWith(Observable.range(1, count + 1), BiFunction<Throwable, Int, Wrapper> { throwable, index ->
            Wrapper(throwable, index)
        }).flatMap { wrapper ->
            if ((wrapper.throwable is ConnectException || wrapper.throwable is SocketTimeoutException || 
                            wrapper.throwable is TimeoutException) && wrapper.index < count + 1) {
                //如果超出重试次数也抛出错误，否则默认是会进入onCompleted
                Observable.timer(delay + (wrapper.index - 1) * increaseDelay, TimeUnit.MILLISECONDS)
            }
            Observable.error<Any>(wrapper.throwable)
        }
    }
}
