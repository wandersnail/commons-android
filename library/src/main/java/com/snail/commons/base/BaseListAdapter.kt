package com.snail.commons.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import java.util.*

abstract class BaseListAdapter<T> @JvmOverloads constructor(val context: Context, list: MutableList<T> = ArrayList()) : BaseAdapter() {
    var data: MutableList<T> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        this.data = list
    }
        
    fun refresh(list: List<T>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }
    
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): T {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @Suppress("unchecked_cast")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: BaseHolder<T> = if (convertView == null) {
            getHolder(position)
        } else {
            convertView.tag as BaseHolder<T>
        }
        holder.setData(data[position], position)
        return holder.convertView
    }

    protected abstract fun getHolder(position: Int): BaseHolder<T>
}