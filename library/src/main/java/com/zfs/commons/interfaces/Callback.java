package com.zfs.commons.interfaces;

import com.zfs.commons.annotation.RunThread;

/**
 * 时间: 2017/9/24 22:42
 * 作者: 曾繁盛
 * 邮箱: 43068145@qq.com
 */
public interface Callback<T> {
	@RunThread
	void onCallback(T obj);
}
