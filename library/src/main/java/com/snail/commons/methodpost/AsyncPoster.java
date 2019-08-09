package com.snail.commons.methodpost;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * date: 2019/8/7 10:57
 * author: zengfansheng
 */
final class AsyncPoster implements Runnable, Poster {
    private final ExecutorService executorService;
    private final Queue<Runnable> queue;

    AsyncPoster(@NonNull ExecutorService executorService) {
        this.executorService = executorService;
        queue = new ConcurrentLinkedQueue<>();
    }
    
    @Override
    public void enqueue(@NonNull Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable is null, cannot be enqueued");
        queue.add(runnable);
        executorService.execute(this);
    }

    @Override
    public void clear() {
        synchronized (this) {
            queue.clear();
        }
    }

    @Override
    public void run() {
        Runnable runnable = queue.poll();
        if (runnable != null) {
            runnable.run();
        }
    }
}
