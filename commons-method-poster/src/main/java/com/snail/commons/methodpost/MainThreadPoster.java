package com.snail.commons.methodpost;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * date: 2019/8/7 10:45
 * author: zengfansheng
 */
final class MainThreadPoster extends Handler implements Poster {
    private final Queue<Runnable> queue;
    private boolean handlerActive;
    
    MainThreadPoster() {
        super(Looper.getMainLooper());
        queue = new ConcurrentLinkedQueue<>();
    }
    
    @Override
    public void enqueue(@NonNull Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable is null, cannot be enqueued");
        synchronized (this) {
            queue.add(runnable);
            if (!handlerActive) {
                handlerActive = true;
                if (!sendMessage(obtainMessage())) {
                    throw new RuntimeException("Could not send handler message");
                }
            }
        }
    }

    @Override
    public void clear() {
        synchronized (this) {
            queue.clear();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        try {
            while (true) {
                Runnable runnable = queue.poll();
                if (runnable == null) {
                    synchronized (this) {
                        runnable = queue.poll();
                        if (runnable == null) {
                            handlerActive = false;
                            return;
                        }
                    }
                }
                runnable.run();
            }
        } finally {
            handlerActive = false;
        }
    }
}
