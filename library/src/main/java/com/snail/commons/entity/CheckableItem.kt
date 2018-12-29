package com.snail.commons.entity

/**
 * 描述: 可选中
 * 时间: 2018/9/7 10:01
 * 作者: zengfansheng
 */
open class CheckableItem<T> @JvmOverloads constructor(var data: T? = null, var isChecked: Boolean = false)
