package com.snail.commons.interfaces

import com.snail.commons.annotation.RunThread

/**
 * 时间: 2017/9/24 22:42
 * 作者: zengfansheng
 */
interface Callback<T> {
    @RunThread
    fun onCallback(obj: T?)
}
