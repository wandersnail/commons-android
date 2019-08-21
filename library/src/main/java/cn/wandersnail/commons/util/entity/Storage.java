package cn.wandersnail.commons.util.entity;

import androidx.annotation.NonNull;
import androidx.core.os.EnvironmentCompat;

/**
 * date: 2019/8/6 13:52
 * author: zengfansheng
 */
public class Storage {
    private String path;
    private String description;
    private String state;
    private long availaleSize;
    private long totalSize;
    private boolean isRemovable;
    private boolean isUsb;
    private boolean isPrimary;
    private boolean isAllowMassStorage;

    /**
     * 路径
     */
    @NonNull
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 描述
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 状态。
     * <p>
     * {@link EnvironmentCompat#MEDIA_UNKNOWN}    
     */
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * 可用空间
     */
    public long getAvailaleSize() {
        return availaleSize;
    }

    public void setAvailaleSize(long availaleSize) {
        this.availaleSize = availaleSize;
    }

    /**
     * 总空间
     */
    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    /**
     * 是否可移除
     */
    public boolean isRemovable() {
        return isRemovable;
    }

    public void setRemovable(boolean removable) {
        isRemovable = removable;
    }

    /**
     * 是否USB存储
     */
    public boolean isUsb() {
        return isUsb;
    }

    public void setUsb(boolean usb) {
        isUsb = usb;
    }

    /**
     * 是否主存储
     */
    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    /**
     * 是否支持UMS功能
     */
    public boolean isAllowMassStorage() {
        return isAllowMassStorage;
    }

    public void setAllowMassStorage(boolean allowMassStorage) {
        isAllowMassStorage = allowMassStorage;
    }
}
