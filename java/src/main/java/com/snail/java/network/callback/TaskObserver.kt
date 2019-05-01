package com.snail.java.network.callback

import com.snail.java.network.TaskInfo
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 *
 *
 * date: 2019/2/28 14:37
 * author: zengfansheng
 */
abstract class TaskObserver<R, T : TaskInfo> @JvmOverloads constructor(protected val info: T, protected val listener: TaskListener<T>? = null) :
        Observer<R>, ProgressListener {
    private var disposable: Disposable? = null
    private var lastUpdateTime: Long = 0//上次进度更新时间
    
    override fun onSubscribe(d: Disposable) {
        disposable = d
        info.state = TaskInfo.State.START
        listener?.onStateChange(info, null)
    }

    override fun onError(e: Throwable) {
        info.state = TaskInfo.State.ERROR
        listener?.onStateChange(info, e)
    }

    override fun onProgress(progress: Long, max: Long) {
        var completionLength = progress
        if (info.contentLength > max) {
            completionLength += info.contentLength - max
        } else {
            info.contentLength = max
        }
        info.completionLength = completionLength
        if (System.currentTimeMillis() - lastUpdateTime >= UPDATE_LIMIT_DURATION && (info.state == TaskInfo.State.IDLE ||
                    info.state == TaskInfo.State.START || info.state == TaskInfo.State.ONGOING)) {
            if (info.state != TaskInfo.State.ONGOING) {
                info.state = TaskInfo.State.ONGOING
                listener?.onStateChange(info, null)
            }
            updateProgress()
            lastUpdateTime = System.currentTimeMillis()
        }
    }
    
    private fun updateProgress() {
        if (info.completionLength > 0 && info.contentLength > 0) {
            listener?.onProgress(info)
        }
    }
    
    protected fun handleSuccess() {
        //更新进度
        info.completionLength = info.contentLength
        updateProgress()
        info.state = TaskInfo.State.COMPLETED
        listener?.onStateChange(info, null)
    }

    fun dispose(cancel: Boolean) {
        disposable?.dispose()
        if (info.state == TaskInfo.State.ONGOING || info.state == TaskInfo.State.START) {
            if (cancel) {
                info.state = TaskInfo.State.CANCEL
                onCancel()
            } else {
                info.state = TaskInfo.State.PAUSE
            }
            listener?.onStateChange(info, null)
        }
    }
    
    abstract fun onCancel()

    companion object {
        private const val UPDATE_LIMIT_DURATION = 500//限制进度更新频率，毫秒
    }
}