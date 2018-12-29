package com.snail.commons.interfaces

import com.snail.commons.annotation.RunThread

/**
 * 时间: 2017/9/24 22:42
 * 作者: 曾繁盛
 * 邮箱: 43068145@qq.com
 */
interface Callback<T> {
    @RunThread
    fun onCallback(obj: T?)
}
