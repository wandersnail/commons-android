package com.snail.commons.base

import androidx.viewpager.widget.PagerAdapter
import android.view.View
import android.view.ViewGroup

/**
 * Created by zengfs on 2016/2/21.
 * 基本ViewPager的基类
 */
class BasePagerAdapter(private val pagers: MutableList<out BasePager>) : androidx.viewpager.widget.PagerAdapter() {

    override fun getCount(): Int {
        return pagers.size
    }

    override fun getItemPosition(any: Any): Int {
        return androidx.viewpager.widget.PagerAdapter.POSITION_NONE
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view == o
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = pagers[position].contentView
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View)
    }
}
