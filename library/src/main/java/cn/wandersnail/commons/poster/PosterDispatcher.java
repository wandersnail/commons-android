package cn.wandersnail.commons.poster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

/**
 * 任务分配
 * <p>
 * date: 2019/8/7 10:18
 * author: zengfansheng
 */
public class PosterDispatcher {
    private final ThreadMode defaultMode;
    private final Poster backgroundPoster;
    private final Poster mainThreadPoster;
    private final ExecutorService executorService;
    private final Poster asyncPoster;

    public PosterDispatcher(@NonNull ExecutorService executorService, @NonNull ThreadMode defaultMode) {
        this.defaultMode = defaultMode;
        this.executorService = executorService;
        backgroundPoster = new BackgroundPoster(executorService);
        mainThreadPoster = new MainThreadPoster();
        asyncPoster = new AsyncPoster(executorService);
    }

    /**
     * 获取默认运行线程
     */
    public ThreadMode getDefaultMode() {
        return defaultMode;
    }

    /**
     * 获取线程池
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * 清除所有队列中任务，存在执行的无法停止
     */
    public void clearTasks() {
        backgroundPoster.clear();
        mainThreadPoster.clear();
        asyncPoster.clear();
    }

    /**
     * 根据方法上带的{@link RunOn}注解，将任务post到指定线程执行。如果方法上没有带注解，使用配置的默认值
     *
     * @param method   方法
     * @param runnable 要执行的任务
     */
    public void post(@Nullable Method method, @NonNull Runnable runnable) {
        if (method != null) {
            RunOn annotation = method.getAnnotation(RunOn.class);
            ThreadMode mode = defaultMode;
            if (annotation != null) {
                mode = annotation.value();
            }
            post(mode, runnable);
        }
    }

    /**
     * 将任务post到指定线程执行。
     *
     * @param mode     指定任务执行线程
     * @param runnable 要执行的任务
     */
    public void post(@NonNull ThreadMode mode, @NonNull Runnable runnable) {
        if (mode == ThreadMode.UNSPECIFIED) {
            mode = defaultMode;
        }
        switch (mode) {
            case MAIN:
                mainThreadPoster.enqueue(runnable);
                break;
            case POSTING:
                runnable.run();
                break;
            case BACKGROUND:
                backgroundPoster.enqueue(runnable);
                break;
            case ASYNC:
                asyncPoster.enqueue(runnable);
                break;
        }
    }

    /**
     * 将任务post到指定线程执行
     *
     * @param owner      方法的所在的对象实例
     * @param methodName 方法名
     * @param tag        {@link Tag#value()}
     * @param parameters 参数信息
     */
    public void post(@NonNull Object owner, @NonNull String methodName, @NonNull String tag,
                     @Nullable MethodInfo.Parameter... parameters) {
        Class<?>[] classes = new Class[0];
        Object[] params = new Object[0];
        if (parameters != null) {
            params = new Object[parameters.length];
            classes = new Class[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                MethodInfo.Parameter parameter = parameters[i];
                classes[i] = parameter.getType();
                params[i] = parameter.getValue();
            }
        }
        Method[] methods = owner.getClass().getDeclaredMethods();
        Method tm = null;
        Method mm = null;
        for (Method method : methods) {
            Tag annotation = method.getAnnotation(Tag.class);
            if (annotation != null && !annotation.value().isEmpty() && annotation.value().equals(tag) &&
                    equalParamTypes(method.getParameterTypes(), classes)) {
                tm = method;
            }
            if (tm == null) {
                if (method.getName().equals(methodName) && equalParamTypes(method.getParameterTypes(), classes)) {
                    mm = method;
                }
            } else {
                break;
            }
        }
        Method method = tm == null ? mm : tm;
        if (method == null) {
            return;
        }
        try {
            Object[] finalParams = params;
            post(method, () -> {
                try {
                    method.invoke(owner, finalParams);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception ignore) {
        }
    }

    private boolean equalParamTypes(Class<?>[] params1, Class<?>[] params2) {
        if (params1.length == params2.length) {
            for (int i = 0; i < params1.length; i++) {
                if (params1[i] != params2[i])
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 将任务post到指定线程执行
     *
     * @param owner      方法的所在的对象实例
     * @param methodName 方法名
     * @param parameters 参数信息
     */
    public void post(@NonNull final Object owner, @NonNull String methodName, @Nullable MethodInfo.Parameter... parameters) {
        post(owner, methodName, "", parameters);
    }

    /**
     * 将任务post到指定线程执行
     *
     * @param owner      方法的所在的对象实例
     * @param methodInfo 方法信息实例
     */
    public void post(@NonNull Object owner, @NonNull MethodInfo methodInfo) {
        post(owner, methodInfo.getName(), methodInfo.getTag(), methodInfo.getParameters());
    }
}
