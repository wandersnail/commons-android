package com.snail.commons.entity;

import com.snail.commons.interfaces.Checkable;

/**
 * date: 2019/8/6 12:35
 * author: zengfansheng
 */
public class CheckableItem<T> implements Checkable<CheckableItem<T>> {
    private T data;
    private boolean isChecked;

    public CheckableItem() {
    }

    public CheckableItem(T data) {
        this.data = data;
    }

    public CheckableItem(T data, boolean isChecked) {
        this.data = data;
        this.isChecked = isChecked;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public CheckableItem<T> setChecked(boolean isChecked) {
        this.isChecked = isChecked;
        return this;
    }
}
