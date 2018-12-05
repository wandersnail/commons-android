package com.zfs.commons.utils;

import android.support.annotation.StringRes;
import android.widget.Toast;

import com.zfs.commons.AppHolder;
import com.zfs.commons.entity.AnyDurationToast;


/**
 * 时间: 2017/10/10 15:10
 * 作者: 曾繁盛
 * 邮箱: 43068145@qq.com
 * 功能: 单例Toast工具类
 */

public class ToastUtils {
    
    private static class Holder {
        private static AnyDurationToast toast = new AnyDurationToast(AppHolder.getContext());
    }
    
    /**
     * 显示时长为{@link Toast#LENGTH_SHORT}的Toast
     */
    public static void showShort(final CharSequence text) {
        AppHolder.postToMainThread(new Runnable() {
            @Override
            public void run() {
                Holder.toast.showShort(text);
            }
        });
    }

    /**
     * 显示时长为{@link Toast#LENGTH_SHORT}的Toast
     */
    public static void showShort(@StringRes final int resId) {
        AppHolder.postToMainThread(new Runnable() {
            @Override
            public void run() {
                Holder.toast.showShort(resId);
            }
        });        
    }

    /**
     * 显示时长为{@link Toast#LENGTH_LONG}的Toast
     */
    public static void showLong(final CharSequence text) {
        AppHolder.postToMainThread(new Runnable() {
            @Override
            public void run() {
                Holder.toast.showLong(text);
            }
        });        
    }

    /**
     * 显示时长为{@link Toast#LENGTH_LONG}的Toast
     */
    public static void showLong(@StringRes final int resId) {
        AppHolder.postToMainThread(new Runnable() {
            @Override
            public void run() {
                Holder.toast.showLong(resId);
            }
        });        
    }

    /**
     * 显示一个任意时长Toast
     */
    public static void showAnyDuration(final CharSequence text, final int duration) {
        AppHolder.postToMainThread(new Runnable() {
            @Override
            public void run() {
                Holder.toast.show(text, duration);
            }
        });        
    }

    /**
     * 显示一个任意时长Toast
     */
    public static void showAnyDuration(@StringRes final int resId, final int duration) {
        AppHolder.postToMainThread(new Runnable() {
            @Override
            public void run() {
                Holder.toast.show(resId, duration);
            }
        });        
    }
}
