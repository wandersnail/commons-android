package com.snail.java.network.utils

import io.reactivex.*
import io.reactivex.schedulers.Schedulers

/**
 *
 *
 * date: 2019/2/23 18:54
 * author: zengfansheng
 */
object SchedulerUtils {
    @JvmStatic
    fun <T> applyGeneralObservableSchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
        }
    }

    @JvmStatic
    fun <T> applyGeneralFlowableSchedulers(): FlowableTransformer<T, T> {
        return FlowableTransformer {
            it.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
        }
    }

    @JvmStatic
    fun <T> applyGeneralSingleSchedulers(): SingleTransformer<T, T> {
        return SingleTransformer { 
            it.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
        }
    }

    @JvmStatic
    fun <T> applyGeneralMaybeSchedulers(): MaybeTransformer<T, T> {
        return MaybeTransformer {
            it.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
        }
    }

    @JvmStatic
    fun applyGeneralCompletableSchedulers(): CompletableTransformer {
        return CompletableTransformer {
            it.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
        }
    }
}