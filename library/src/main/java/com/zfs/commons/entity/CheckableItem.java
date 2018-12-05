package com.zfs.commons.entity;

/**
 * 描述: 可选中
 * 时间: 2018/9/7 10:01
 * 作者: zengfansheng
 */
public class CheckableItem<T> {
    public T data;
    public boolean isChecked;

    public CheckableItem() {
    }

    public CheckableItem(T data) {
        this.data = data;
    }

    public CheckableItem(T data, boolean isChecked) {
        this.data = data;
        this.isChecked = isChecked;
    }
}
