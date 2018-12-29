package com.snail.commons.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import java.util.*

abstract class BaseListAdapter<T> : BaseAdapter {
    var context: Context? = null
        private set
    private var data: List<T>

    constructor(context: Context) {
        data = ArrayList()
        this.context = context
    }

    constructor(context: Context, data: List<T>) {
        this.context = context
        this.data = data
    }

    fun getData(): List<T>? {
        return data
    }

    fun setData(data: List<T>) {
        this.data = data
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