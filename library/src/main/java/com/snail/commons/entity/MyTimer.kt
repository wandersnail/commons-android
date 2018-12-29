package com.snail.commons.entity

import android.os.Handler
import android.os.Looper
import java.util.*

/**
 * Created by zengfs on 2016/1/21.
 * 计时器，回调可运行在UI线程或子线程
 */
class MyTimer {
    private val lock = Any()
    private var timer: Timer? = null
    private val handler = Handler(Looper.getMainLooper())

    val isRunning: Boolean
        get() = timer != null

    interface UiTimerTaskCallback {
        fun runOnUiTimerTask()
    }

    interface TimerTaskCallback {
        fun runTimerTask()
    }

    /**
     * 开始计时器
     * @param delay 延时多长时间开始计时
     * @param period 执行任务的周期
     */
    fun startTimer(delay: Long, period: Long, callback: TimerTaskCallback?) {
        synchronized(lock) {
            if (timer == null) {
                timer = Timer()
                val task = object : TimerTask() {
                    override fun run() {
                        callback?.runTimerTask()
                    }
                }
                timer!!.schedule(task, delay, period)
            }
        }
    }

    /**
     * 开始计时器
     * @param delay 延时多长时间开始计时
     * @param period 执行任务的周期
     * @param callback 运行在ui线程的回调
     */
    fun startTimer(delay: Long, period: Long, callback: UiTimerTaskCallback?) {
        synchronized(lock) {
            if (timer == null) {
                timer = Timer()
                val task = object : TimerTask() {
                    override fun run() {
                        if (callback != null) {
                            handler.post { callback.runOnUiTimerTask() }
                        }
                    }
                }
                timer!!.schedule(task, delay, period)
            }
        }
    }

    fun stopTimer() {
        synchronized(lock) {
            timer?.cancel()
            timer = null
        }
    }
}
