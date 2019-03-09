package com.snail.commons.base

/**
 * 描述: 基本ViewPager的基类
 * 时间: 2018/8/26 22:57
 * 作者: zengfansheng
 */
open class BaseFragmentPagerAdapter(fm: androidx.fragment.app.FragmentManager, private val fragments: MutableList<out androidx.fragment.app.Fragment>) : androidx.fragment.app.FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItemPosition(any: Any): Int {
        return androidx.viewpager.widget.PagerAdapter.POSITION_NONE
    }
}
