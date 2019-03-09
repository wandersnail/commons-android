package com.snail.commons.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import java.util.*
import kotlin.collections.HashMap

abstract class BaseListAdapter<T> @JvmOverloads constructor(val context: Context, list: MutableList<T> = ArrayList()) : BaseAdapter() {
    private val holders = HashMap<View, BaseViewHolder<T>>()
    
    var items: MutableList<T> = list
        set(value) {
            field = value
            notifyDataSetChanged()
        }
        
    fun refresh(list: List<T>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun notifyDataSetChanged() {
        holders.clear()
        super.notifyDataSetChanged()
    }
    
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): T {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: BaseViewHolder<T> = if (convertView == null) {
            val h = createViewHolder(position)
            holders[h.convertView] = h
            h
        } else {
            holders[convertView]!!
        }
        viewHolder.onBind(items[position], position)
        return viewHolder.convertView
    }

    protected abstract fun createViewHolder(position: Int): BaseViewHolder<T>
}