package com.snail.commons.interfaces;

/**
 * date: 2019/8/6 10:05
 * author: zengfansheng
 */
public interface Checkable<T> {
    boolean isChecked();
    
    T setChecked(boolean isChecked);
}
