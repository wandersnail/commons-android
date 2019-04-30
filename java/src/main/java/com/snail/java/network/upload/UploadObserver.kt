package com.snail.java.network.upload

import com.snail.java.network.callback.TaskListener
import com.snail.java.network.callback.TaskObserver

/**
 * 上传任务观察者
 *
 * date: 2019/2/28 12:58
 * author: zengfansheng
 * @property R 响应数据类型 
 */
internal class UploadObserver<R, T : UploadInfo<R>> @JvmOverloads constructor(info: T, listener: TaskListener<T>? = null) : TaskObserver<R, T>(info, listener) {
    override fun onCancel() {
    }

    override fun onNext(t: R) {
        info.response = t
    }
    
    override fun onComplete() {
        handleSuccess()
    }
}