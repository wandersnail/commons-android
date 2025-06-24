package cn.wandersnail.commonsdemo

import androidx.viewbinding.ViewBinding

/**
 * date: 2022/10/4 01:04
 * author: zengfansheng
 */
interface ViewBindingClassProvider<VB : ViewBinding> {
    fun getViewBindingClass(): Class<VB>
}
