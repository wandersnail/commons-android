package com.snail.commons.entity

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.annotation.StringRes
import android.widget.Toast
import java.util.*

/**
 * Created by zeng on 2016/8/24.
 * 任意时长Toast
 */
class AnyDurationToast(context: Context) {
    private var loopTimer: Timer? = null
    private var taskTimer: Timer? = null
    private var toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
    private val handler = Handler(Looper.getMainLooper())

    /**
     * Return the horizontal margin.
     */
    val horizontalMargin: Float
        get() = toast.horizontalMargin

    /**
     * Return the vertical margin.
     */
    val verticalMargin: Float
        get() = toast.verticalMargin

    /**
     * Get the location at which the notification should appear on the screen.
     * @see android.view.Gravity
     *
     * @see .getGravity
     */
    val gravity: Int
        get() = toast.gravity

    /**
     * Return the X offset in pixels to apply to the gravity's location.
     */
    val xOffset: Int
        get() = toast.xOffset

    /**
     * Return the Y offset in pixels to apply to the gravity's location.
     */
    val yOffset: Int
        get() = toast.yOffset

    private fun cancelTask() {
        if (loopTimer != null) {
            loopTimer!!.cancel()
            loopTimer = null
        }
        if (taskTimer != null) {
            taskTimer!!.cancel()
            taskTimer = null
        }
    }

    private fun updateToast(text: CharSequence, duration: Int): Toast {
        val toast = Toast.makeText(this.toast.view.context, text, duration)
        toast.setMargin(this.toast.horizontalMargin, this.toast.verticalMargin)
        toast.setGravity(this.toast.gravity, this.toast.xOffset, this.toast.yOffset)
        this.toast.cancel()
        this.toast = toast
        return toast
    }

    /**
     * 显示任意时长的Toast
     * @param duration 毫秒
     */
    fun show(text: CharSequence, duration: Int) {
        cancelTask()
        loopTimer = Timer()
        loopTimer!!.schedule(object : TimerTask() {
            override fun run() {
                handler.post { updateToast(text, Toast.LENGTH_SHORT).show() }
            }
        }, 0, 3000)
        taskTimer = Timer()
        taskTimer!!.schedule(object : TimerTask() {
            override fun run() {
                this@AnyDurationToast.cancel()
            }
        }, duration.toLong())
    }

    /**
     * 显示任意时长的Toast
     * @param duration 毫秒
     */
    fun show(@StringRes resId: Int, duration: Int) {
        show(toast.view.context.getText(resId), duration)
    }

    /**
     * 显示时长为[Toast.LENGTH_SHORT]的Toast
     */
    fun showShort(text: CharSequence) {
        handler.post {
            cancelTask()
            updateToast(text, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 显示时长为[Toast.LENGTH_SHORT]的Toast
     */
    fun showShort(@StringRes resId: Int) {
        showShort(toast.view.context.getText(resId))
    }

    /**
     * 显示时长为[Toast.LENGTH_LONG]的Toast
     */
    fun showLong(text: CharSequence) {
        handler.post {
            cancelTask()
            updateToast(text, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 显示时长为[Toast.LENGTH_LONG]的Toast
     */
    fun showLong(@StringRes resId: Int) {
        showLong(toast.view.context.getText(resId))
    }

    fun cancel() {
        toast.cancel()
        cancelTask()
    }

    /**
     * Set the margins of the view.
     *
     * @param horizontalMargin The horizontal margin, in percentage of the
     * container width, between the container's edges and the
     * notification
     * @param verticalMargin The vertical margin, in percentage of the
     * container height, between the container's edges and the
     * notification
     */
    fun setMargin(horizontalMargin: Float, verticalMargin: Float) {
        toast.setMargin(horizontalMargin, verticalMargin)
    }

    /**
     * Set the location at which the notification should appear on the screen.
     * @see android.view.Gravity
     *
     * @see .getGravity
     */
    fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        toast.setGravity(gravity, xOffset, yOffset)
    }
}
