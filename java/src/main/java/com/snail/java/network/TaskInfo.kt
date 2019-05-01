package com.snail.java.network

import com.snail.java.network.utils.HttpUtils
import java.util.*

/**
 *
 *
 * date: 2019/2/28 14:46
 * author: zengfansheng
 */
abstract class TaskInfo(
        /** 请求地址 */
        val url: String,
        /** 唯一标识 */
        val tag: String = UUID.randomUUID().toString()) {

    /** 下载状态 */
    var state = State.IDLE
        internal set
    /** 基础url */
    val baseUrl: String = HttpUtils.getBaseUrl(url)
    /** 进度：总长度 */
    var contentLength: Long = 0
        internal set
    /** 进度：已完成长度 */
    var completionLength: Long = 0
        internal set

    internal open fun reset() {}

    enum class State {
        IDLE, START, ONGOING, COMPLETED, CANCEL, ERROR, PAUSE
    }
}