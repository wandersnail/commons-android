package com.snail.commons.base

import android.view.View

/**
 * 主要用于ListView的item布局创建及数据设置
 */
@Suppress("LeakingThis")
abstract class BaseViewHolder<T> {
    val convertView = createConvertView()

    /**
     * 和Adapter绑定了，可在此设置View的数据，更新View
     */
    abstract fun onBind(item: T, position: Int)

    /**
     * 创建界面
     */
    protected abstract fun createConvertView(): View
}