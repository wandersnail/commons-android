package com.zfs.commons.interfaces;

/**
 * 描述: 可选中的
 * 时间: 2018/9/7 09:11
 * 作者: zengfansheng
 */
public interface Checkable<T> {
    T setChecked(boolean isChecked);
    
    boolean isChecked();
}
