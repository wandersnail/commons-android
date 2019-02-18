package com.snail.commons.entity

import android.os.Environment
import androidx.core.os.EnvironmentCompat

/**
 * 描述: 存储
 * 时间: 2018/5/27 13:18
 * 作者: zengfansheng
 */
class Storage {
    /**
     * 路径
     */
    var path: String = ""
    /**
     * 描述
     */
    var description: String = ""
    /**
     * 可用空间
     */
    var availaleSize: Long = 0
    /**
     * 总空间
     */
    var totalSize: Long = 0
    /**
     * one of [EnvironmentCompat.MEDIA_UNKNOWN], [Environment.MEDIA_REMOVED],
     * [Environment.MEDIA_UNMOUNTED],
     * [Environment.MEDIA_CHECKING],
     * [Environment.MEDIA_NOFS],
     * [Environment.MEDIA_MOUNTED],
     * [Environment.MEDIA_MOUNTED_READ_ONLY],
     * [Environment.MEDIA_SHARED],
     * [Environment.MEDIA_BAD_REMOVAL], or
     * [Environment.MEDIA_UNMOUNTABLE].
     */
    var state: String = EnvironmentCompat.MEDIA_UNKNOWN
    /**
     * 是否可移除
     */
    var isRemovable: Boolean = false
    /**
     * 是否USB存储
     */
    var isUsb: Boolean = false
    /**
     * 是否主存储
     */
    var isPrimary: Boolean = false

    /**
     * 是否支持UMS功能
     */
    var isAllowMassStorage: Boolean = false
}
