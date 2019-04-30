package com.snail.java.network.callback

/**
 * 多任务进度监听
 *
 * date: 2019/2/28 13:39
 * author: zengfansheng
 */
interface MultiProgressListener {
    /**
     * 总体任务进度
     *
     * @param successCount 成功个数
     * @param failedCount 失败个数，包含主动取消的
     * @param total 总任务数
     */
    fun onTotalProgress(successCount: Int, failedCount: Int, total: Int)
}