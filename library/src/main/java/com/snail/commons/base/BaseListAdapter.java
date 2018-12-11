package com.snail.commons.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListAdapter<T> extends BaseAdapter {
	private Context context;
	private List<T> data;

	public BaseListAdapter(@NonNull Context context) {
		data = new ArrayList<>();
		this.context = context;
	}
	
	public BaseListAdapter(@NonNull Context context, @NonNull List<T> data) {
		this.context = context;
		this.data = data;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(@NonNull List<T> data) {
		this.data = data;
		notifyDataSetChanged();
	}
	
	public Context getContext() {
        return context;
    }
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public T getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BaseHolder<T> holder;
		if (convertView == null) {
			holder = getHolder(position);
		} else {
			holder = (BaseHolder<T>) convertView.getTag();
		}
		holder.setData(data.get(position), position);
		return holder.getConvertView();
	}

	protected abstract BaseHolder<T> getHolder(int position);
}