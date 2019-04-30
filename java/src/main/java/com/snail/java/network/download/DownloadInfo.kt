package com.snail.java.network.download

import com.snail.java.network.TaskInfo
import java.io.File
import java.util.*

/**
 * 时间: 2017/7/8 20:13
 * 作者: zengfansheng
 * 功能: 下载信息类，包含下载状态及进度监听
 */

open class DownloadInfo @JvmOverloads constructor(
    url: String,
    /** 文件保存路径 */
    val savePath: String,
    /** 唯一标识 */
    tag: String = UUID.randomUUID().toString()) : TaskInfo(url, tag) {

    /**
     * 获取下载的临时文件，下载完成后再重命名
     */
    internal val temporaryFilePath: String
        get() = "$savePath.temp"
    
    override fun reset() {
        completionLength = 0
        contentLength = 0
        File(temporaryFilePath).delete()
    }
    
    override fun equals(other: Any?): Boolean {
        return this === other || other is DownloadInfo && url == other.url
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }
}
