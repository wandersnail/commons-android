package com.snail.java.network

import com.snail.java.network.callback.MultiTaskListener
import com.snail.java.network.callback.TaskListener
import com.snail.java.network.callback.TaskObserver
import java.util.concurrent.ConcurrentHashMap

/**
 *
 *
 * date: 2019/2/28 20:11
 * author: zengfansheng
 */
abstract class TaskWorker<R, T : TaskInfo> {
    protected val taskMap = ConcurrentHashMap<T, TaskObserver<R, T>>()
    protected val listener: TaskListener<T>?
    private val totalTasks: Int
    private var successCount = 0
    private var failedCount = 0
    
    internal constructor(info: T, listener: TaskListener<T>?) {
        this.listener = listener
        totalTasks = 1
        info.reset()
        this.execute(info)
    }

    internal constructor(infos: List<T>, listener: MultiTaskListener<T>?) {
        this.listener = listener
        totalTasks = infos.size
        infos.forEach {
            it.reset()
            this.execute(it)
        }
    }

    protected abstract fun execute(info: T)
    
    protected inner class LocalTaskListener : TaskListener<T> {
        override fun onStateChange(info: T, t: Throwable?) {
            listener?.onStateChange(info, t)
            if (totalTasks > 1) {
                if (info.state == TaskInfo.State.COMPLETED) {
                    taskMap.remove(info)
                    successCount++
                    (listener as? MultiTaskListener)?.onTotalProgress(successCount, failedCount, totalTasks)
                } else if (info.state == TaskInfo.State.CANCEL || info.state == TaskInfo.State.ERROR) {
                    taskMap.remove(info)
                    failedCount++
                    (listener as? MultiTaskListener)?.onTotalProgress(successCount, failedCount, totalTasks)
                } else if (info.state == TaskInfo.State.START) {
                    (listener as? MultiTaskListener)?.onTotalProgress(successCount, failedCount, totalTasks)
                }
            }
        }

        override fun onProgress(info: T) {
            listener?.onProgress(info)
        }
    }

    /**
     * 任务是否正在进行
     */
    fun isOngoing(): Boolean {
        return remaining() > 0
    }
    
    /**
     * 进行中的下载任务数
     */
    fun remaining(): Int {
        return taskMap.size
    }

    /**
     * 取消所有下载
     */
    fun cancel() {
        taskMap.values.forEach { it.dispose(true) }
        taskMap.clear()
    }

    /**
     * 取消单个下载
     */
    fun cancel(info: T) {
        taskMap.remove(info)?.dispose(true)
    }
}