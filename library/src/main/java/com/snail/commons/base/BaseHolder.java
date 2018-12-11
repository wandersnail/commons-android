package com.snail.commons.base;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * 主要用于ListView的item布局创建及数据设置
 */
public abstract class BaseHolder<T> {
	private View convertView;

	public BaseHolder() {
		convertView = createConvertView();
		convertView.setTag(this);
	}

	public View getConvertView() {
		return convertView;
	}

	/**
	 * 设置数据
	 */
	protected abstract void setData(@NonNull T data, int position);
	
	/**
	 * 创建界面
	 */
	protected abstract View createConvertView();
}