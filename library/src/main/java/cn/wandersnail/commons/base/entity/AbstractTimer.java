package cn.wandersnail.commons.base.entity;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 简单的定时器
 * 
 * date: 2019/8/6 13:31
 * author: zengfansheng
 */
public abstract class AbstractTimer {
    private ScheduledExecutorService schedule;
    private final Handler handler;
    private final boolean callbackOnMainThread;
    
    public AbstractTimer(boolean callbackOnMainThread) {
        handler = new Handler(Looper.getMainLooper());
        this.callbackOnMainThread = callbackOnMainThread;
    }

    /**
     * 回调
     */
    public abstract void onTick();

    /**
     * 开始
     */
    public synchronized final void start(long delay, long period) {
        if (schedule == null) {
            schedule = Executors.newSingleThreadScheduledExecutor();
            schedule.scheduleWithFixedDelay(() -> {
                if (callbackOnMainThread) {
                    handler.post(this::onTick);
                } else {
                    onTick();
                }
            }, delay, period, TimeUnit.MILLISECONDS);
        }
    }
    
    public synchronized final void stop() {
        if (schedule != null) {
            schedule.shutdownNow();
            schedule = null;
        }
    }
    
    public boolean isRunning() {
        return schedule != null;
    }
}
