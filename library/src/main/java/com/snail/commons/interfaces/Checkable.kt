package com.snail.commons.interfaces

/**
 * 描述: 可选中的
 * 时间: 2018/9/7 09:11
 * 作者: zengfansheng
 */
interface Checkable<T> {
    val isChecked: Boolean
    fun setChecked(isChecked: Boolean): T
}
