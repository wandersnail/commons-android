package com.zfs.commons.entity;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zeng on 2016/8/24.
 * 任意时长Toast
 */
public class AnyDurationToast {
    private Timer loopTimer;
    private Timer taskTimer;
    private Toast toast;
    private Handler handler;

    public AnyDurationToast(@NonNull Context context) {
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        handler = new Handler(Looper.getMainLooper());
    }

    private void cancelTask() {
        if (loopTimer != null) {
            loopTimer.cancel();
            loopTimer = null;
        }
        if (taskTimer != null) {
            taskTimer.cancel();
            taskTimer = null;
        }
    }

    private Toast updateToast(CharSequence text, int duration) {
        Toast toast = Toast.makeText(this.toast.getView().getContext(), text, duration);
        toast.setMargin(this.toast.getHorizontalMargin(), this.toast.getVerticalMargin());
        toast.setGravity(this.toast.getGravity(), this.toast.getXOffset(), this.toast.getYOffset());
        this.toast.cancel();
        this.toast = toast;
        return toast;
    }
    
    /**
     * 显示任意时长的Toast
     * @param duration 毫秒
     */
    public void show(final CharSequence text, final int duration) {
        cancelTask();
        loopTimer = new Timer();
        loopTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateToast(text, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 0, 3000);
        taskTimer = new Timer();
        taskTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                AnyDurationToast.this.cancel();
            }
        }, duration);
    }

    /**
     * 显示任意时长的Toast
     * @param duration 毫秒
     */
    public void show(@StringRes int resId, int duration) {
        show(toast.getView().getContext().getText(resId), duration);
    }

    /**
     * 显示时长为{@link Toast#LENGTH_SHORT}的Toast
     */
    public void showShort(final CharSequence text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                cancelTask();
                updateToast(text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 显示时长为{@link Toast#LENGTH_SHORT}的Toast
     */
    public void showShort(@StringRes int resId) {
        showShort(toast.getView().getContext().getText(resId));
    }

    /**
     * 显示时长为{@link Toast#LENGTH_LONG}的Toast
     */
    public void showLong(final CharSequence text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                cancelTask();
                updateToast(text, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 显示时长为{@link Toast#LENGTH_LONG}的Toast
     */
    public void showLong(@StringRes int resId) {
        showLong(toast.getView().getContext().getText(resId));
    }

    public void cancel() {
        toast.cancel();
        cancelTask();
    }

    /**
     * Set the margins of the view.
     *
     * @param horizontalMargin The horizontal margin, in percentage of the
     *        container width, between the container's edges and the
     *        notification
     * @param verticalMargin The vertical margin, in percentage of the
     *        container height, between the container's edges and the
     *        notification
     */
    public void setMargin(float horizontalMargin, float verticalMargin) {
        toast.setMargin(horizontalMargin, verticalMargin);
    }

    /**
     * Return the horizontal margin.
     */
    public float getHorizontalMargin() {
        return toast.getHorizontalMargin();
    }

    /**
     * Return the vertical margin.
     */
    public float getVerticalMargin() {
        return toast.getVerticalMargin();
    }

    /**
     * Set the location at which the notification should appear on the screen.
     * @see android.view.Gravity
     * @see #getGravity
     */
    public void setGravity(int gravity, int xOffset, int yOffset) {
        toast.setGravity(gravity, xOffset, yOffset);
    }

    /**
     * Get the location at which the notification should appear on the screen.
     * @see android.view.Gravity
     * @see #getGravity
     */
    public int getGravity() {
        return toast.getGravity();
    }

    /**
     * Return the X offset in pixels to apply to the gravity's location.
     */
    public int getXOffset() {
        return toast.getXOffset();
    }

    /**
     * Return the Y offset in pixels to apply to the gravity's location.
     */
    public int getYOffset() {
        return toast.getYOffset();
    }
}
