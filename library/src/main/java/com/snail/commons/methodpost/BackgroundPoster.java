package com.snail.commons.methodpost;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * 执行后台任务
 * <p>
 * date: 2019/8/7 10:40
 * author: zengfansheng
 */
final class BackgroundPoster implements Runnable, Poster {
    private final ExecutorService executorService;
    private final Queue<Runnable> queue;
    private volatile boolean executorRunning;

    BackgroundPoster(@NonNull ExecutorService executorService) {
        this.executorService = executorService;
        queue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void enqueue(@NonNull Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable is null, cannot be enqueued");
        synchronized (this) {
            queue.add(runnable);
            if (!executorRunning) {
                executorRunning = true;
                executorService.execute(this);
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
    public void run() {
        try {
            while (true) {
                Runnable runnable = queue.poll();
                if (runnable == null) {
                    synchronized (this) {
                        runnable = queue.poll();
                        if (runnable == null) {
                            executorRunning = false;
                            return;
                        }
                    }
                }
                runnable.run();
            }
        } finally {
            executorRunning = false;
        }
    }
}
