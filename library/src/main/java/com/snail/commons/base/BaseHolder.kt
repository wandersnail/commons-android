package com.snail.commons.base

import android.view.View

/**
 * 主要用于ListView的item布局创建及数据设置
 */
@Suppress("LeakingThis")
abstract class BaseHolder<T> {
    val convertView: View

    init {
        convertView = createConvertView()
        convertView.tag = this
    }

    /**
     * 设置数据
     */
    abstract fun setData(data: T, position: Int)

    /**
     * 创建界面
     */
    protected abstract fun createConvertView(): View
}