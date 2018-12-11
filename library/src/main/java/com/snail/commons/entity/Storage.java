package com.snail.commons.entity;

import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;

/**
 * 描述: 存储
 * 时间: 2018/5/27 13:18
 * 作者: zengfansheng
 */
public class Storage {
    /**
     * 路径
     */
    public String path;
    /**
     * 描述
     */
    public String description;
    /**
     * 可用空间
     */
    public long availaleSize;
    /**
     * 总空间
     */
    public long totalSize;
    /**
     * one of {@link EnvironmentCompat#MEDIA_UNKNOWN}, {@link Environment#MEDIA_REMOVED},
     *         {@link Environment#MEDIA_UNMOUNTED},
     *         {@link Environment#MEDIA_CHECKING},
     *         {@link Environment#MEDIA_NOFS},
     *         {@link Environment#MEDIA_MOUNTED},
     *         {@link Environment#MEDIA_MOUNTED_READ_ONLY},
     *         {@link Environment#MEDIA_SHARED},
     *         {@link Environment#MEDIA_BAD_REMOVAL}, or
     *         {@link Environment#MEDIA_UNMOUNTABLE}.
     */
    public String state;
    /**
     * 是否可移除
     */
    public boolean isRemovable;
    /**
     * 是否USB存储
     */
    public boolean isUsb;
    /**
     * 是否主存储
     */
    public boolean isPrimary;

    /**
     * 是否支持UMS功能
     */
    public boolean isAllowMassStorage;
        
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getAvailaleSize() {
        return availaleSize;
    }

    public void setAvailaleSize(long availaleSize) {
        this.availaleSize = availaleSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isRemovable() {
        return isRemovable;
    }

    public void setRemovable(boolean removable) {
        this.isRemovable = removable;
    }

    public boolean isUsb() {
        return isUsb;
    }

    public void setUsb(boolean usb) {
        isUsb = usb;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public boolean isAllowMassStorage() {
        return isAllowMassStorage;
    }

    public void setAllowMassStorage(boolean allowMassStorage) {
        this.isAllowMassStorage = allowMassStorage;
    }
}
