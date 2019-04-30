package com.snail.java.network.callback

/**
 * 时间: 2017/7/8 20:23
 * 作者: zengfansheng
 * 功能: 下载进度监听
 */

internal interface ProgressListener {
    /**
     * 进度更新
     * 
     * @param progress 已完成的大小
     * @param max 总大小
     */
    fun onProgress(progress: Long, max: Long)
}
