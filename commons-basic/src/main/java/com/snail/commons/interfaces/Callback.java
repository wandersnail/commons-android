package com.snail.commons.interfaces;

import androidx.annotation.Nullable;

/**
 * date: 2019/8/6 10:04
 * author: zengfansheng
 */
public interface Callback<T> {
    void onCallback(@Nullable T obj);
}
