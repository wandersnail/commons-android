package com.snail.commons.utils

import android.support.annotation.StringRes
import android.widget.Toast

import com.snail.commons.AppHolder
import com.snail.commons.entity.AnyDurationToast


/**
 * 时间: 2017/10/10 15:10
 * 作者: 曾繁盛
 * 邮箱: 43068145@qq.com
 * 功能: 单例Toast工具类
 */

object ToastUtils {

    private object Holder {
        internal val toast = AnyDurationToast(AppHolder.context)
    }

    /**
     * 显示时长为[Toast.LENGTH_SHORT]的Toast
     */
    fun showShort(text: CharSequence) {
        AppHolder.postToMainThread(Runnable { Holder.toast.showShort(text) })
    }

    /**
     * 显示时长为[Toast.LENGTH_SHORT]的Toast
     */
    fun showShort(@StringRes resId: Int) {
        AppHolder.postToMainThread(Runnable { Holder.toast.showShort(resId) })
    }

    /**
     * 显示时长为[Toast.LENGTH_LONG]的Toast
     */
    fun showLong(text: CharSequence) {
        AppHolder.postToMainThread(Runnable { Holder.toast.showLong(text) })
    }

    /**
     * 显示时长为[Toast.LENGTH_LONG]的Toast
     */
    fun showLong(@StringRes resId: Int) {
        AppHolder.postToMainThread(Runnable { Holder.toast.showLong(resId) })
    }

    /**
     * 显示一个任意时长Toast
     */
    fun showAnyDuration(text: CharSequence, duration: Int) {
        AppHolder.postToMainThread(Runnable { Holder.toast.show(text, duration) })
    }

    /**
     * 显示一个任意时长Toast
     */
    fun showAnyDuration(@StringRes resId: Int, duration: Int) {
        AppHolder.postToMainThread(Runnable { Holder.toast.show(resId, duration) })
    }
}
