package cn.wandersnail.commons.observer;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.wandersnail.commons.poster.MethodInfo;
import cn.wandersnail.commons.poster.PosterDispatcher;

/**
 * 消息发布者、被观察者
 * <p>
 * date: 2019/8/3 13:14
 * author: zengfansheng
 */
public final class Observable {
    private final List<ObserverInfo> observerInfos = new ArrayList<>();
    private final PosterDispatcher posterDispatcher;
    private final ObserverMethodHelper helper;

    /**
     * @param posterDispatcher            方法分发者
     * @param isObserveAnnotationRequired 是否强制使用{@link Observe}注解才会收到被观察者的消息。强制使用的话，性能会好一些
     */
    public Observable(@NonNull PosterDispatcher posterDispatcher, boolean isObserveAnnotationRequired) {
        this.posterDispatcher = posterDispatcher;
        helper = new ObserverMethodHelper(isObserveAnnotationRequired);
    }

    /**
     * 方法分发者
     */
    public PosterDispatcher getPosterDispatcher() {
        return posterDispatcher;
    }

    /**
     * 将观察者添加到注册集合里
     *
     * @param observer 需要注册的观察者
     */
    public void registerObserver(@NonNull Observer observer) {
        Objects.requireNonNull(observer, "observer can't be null");
        synchronized (observerInfos) {
            boolean registered = false;
            for (Iterator<ObserverInfo> it = observerInfos.iterator(); it.hasNext(); ) {
                ObserverInfo info = it.next();
                Observer o = info.weakObserver.get();
                if (o == null) {
                    it.remove();
                } else if (o == observer) {
                    registered = true;
                }
            }
            if (registered) {
                Log.e("Observable", "", new Error("Observer " + observer + " is already registered."));
                return;
            }
            Map<String, Method> methodMap = helper.findObserverMethod(observer);
            observerInfos.add(new ObserverInfo(observer, methodMap));
        }
    }

    /**
     * 查询观察者是否注册
     *
     * @param observer 要查询的观察者
     */
    public boolean isRegistered(@NonNull Observer observer) {
        synchronized (observerInfos) {
            for (ObserverInfo info : observerInfos) {
                if (info.weakObserver.get() == observer) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 将观察者从注册集合里移除
     *
     * @param observer 需要取消注册的观察者
     */
    public void unregisterObserver(@NonNull Observer observer) {
        synchronized (observerInfos) {
            for (Iterator<ObserverInfo> it = observerInfos.iterator(); it.hasNext(); ) {
                ObserverInfo info = it.next();
                Observer o = info.weakObserver.get();
                if (o == null || observer == o) {
                    it.remove();
                }
            }
        }
    }

    /**
     * 将所有观察者从注册集合中移除
     */
    public void unregisterAll() {
        synchronized (observerInfos) {
            observerInfos.clear();
        }
        helper.clearCache();
    }

    private List<ObserverInfo> getObserverInfos() {
        synchronized (observerInfos) {
            ArrayList<ObserverInfo> infos = new ArrayList<>();
            for (ObserverInfo info : observerInfos) {
                Observer observer = info.weakObserver.get();
                if (observer != null) {
                    infos.add(info);
                }
            }
            return infos;
        }
    }

    /**
     * 通知所有观察者事件变化
     *
     * @param methodName 要调用观察者的方法名
     * @param parameters 方法参数信息对
     */
    public void notifyObservers(@NonNull String methodName, @Nullable MethodInfo.Parameter... parameters) {
        notifyObservers(new MethodInfo(methodName, parameters));
    }

    /**
     * 通知所有观察者事件变化
     *
     * @param info 方法信息实例
     */
    public void notifyObservers(@NonNull MethodInfo info) {
        List<ObserverInfo> infos = getObserverInfos();
        for (ObserverInfo oi : infos) {
            Observer observer = oi.weakObserver.get();
            if (observer != null) {
                String key = helper.generateKey(info.getTag(), info.getName(), info.getParameterTypes());
                Method method = oi.methodMap.get(key);
                if (method != null) {
                    Runnable runnable = helper.generateRunnable(observer, method, info);
                    posterDispatcher.post(method, runnable);
                }
            }
        }
    }
}
