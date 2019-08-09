package com.snail.commons;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * date: 2019/8/6 10:02
 * author: zengfansheng
 */
public class AppHolder implements Application.ActivityLifecycleCallbacks {
    //正在运行的Activity
    private final Map<String, WeakReference<Activity>>  runningActivites = new ConcurrentHashMap<>();
    //是否完全退出
    private boolean isCompleteExit = false;
    private Application application;
    private Looper mainLooper;

    private AppHolder() {
        mainLooper = Looper.getMainLooper();
        //尝试获取application
        application = tryGetApplication();
        if (application != null) {
            application.registerActivityLifecycleCallbacks(AppHolder.this);
        }       
    }

    private static final class Holder {
        private static final AppHolder INSTANCE = new AppHolder();
    }
    
    @NonNull
    public static AppHolder getInstance() {
        return Holder.INSTANCE;
    }
    
    @SuppressLint("PrivateApi")
    @Nullable
    private Application tryGetApplication() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityThread");
            Method catMethod = cls.getMethod("currentActivityThread");
            catMethod.setAccessible(true);
            Object aThread = catMethod.invoke(null);
            Method method = aThread.getClass().getMethod("getApplication");
            return (Application) method.invoke(aThread);
        } catch (Exception e) {
            return null;
        }
    }

    @CallSuper
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        runningActivites.put(activity.getClass().getName(), new WeakReference<>(activity));
    }

    @CallSuper
    @Override
    public void onActivityStarted(Activity activity) {

    }

    @CallSuper
    @Override
    public void onActivityResumed(Activity activity) {

    }

    @CallSuper
    @Override
    public void onActivityPaused(Activity activity) {

    }

    @CallSuper
    @Override
    public void onActivityStopped(Activity activity) {

    }

    @CallSuper
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @CallSuper
    @Override
    public void onActivityDestroyed(Activity activity) {
        runningActivites.remove(activity.getClass().getName());
        if (isCompleteExit && runningActivites.isEmpty()) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }
    
    public static void initialize(@NonNull Application application) {
        Objects.requireNonNull(application, "application is null");
        //如果自动获取的和传入的不是同一个Application，重新注册生命周期监听
        if (Holder.INSTANCE.application != null && Holder.INSTANCE.application != application) {
            Holder.INSTANCE.application.unregisterActivityLifecycleCallbacks(Holder.INSTANCE);
            application.registerActivityLifecycleCallbacks(Holder.INSTANCE);
        }
        Holder.INSTANCE.application = application;        
    }
    
    public boolean isMainThread() {
        return Looper.myLooper() == mainLooper;
    }
    
    @NonNull
    public Looper getMainLooper() {
        return mainLooper;
    }
    
    @NonNull
    public Context getContext() {
        Objects.requireNonNull(application, "The AppHolder has not been initialized, make sure to call AppHolder.initialize(app) first.");
        return application;
    }
    
    @Nullable
    public PackageInfo getPackageInfo() {
        try {
            PackageManager pm = application.getPackageManager();
            return pm.getPackageInfo(application.getPackageName(), 0);
        } catch (Exception ignore) {
        }
        return null;
    }

    /**
     * 程序是否在前台运行
     */
    public boolean isAppOnForeground() {
        ActivityManager am = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
            if (processes != null) {
                for (ActivityManager.RunningAppProcessInfo process : processes) {
                    if (application.getPackageName().equals(process.processName) &&
                            ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND == process.importance) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    //数组是否包含某元素
    private boolean contains(Object[] array, Object obj) {
        if (array != null && array.length > 0) {
            for (Object o : array) {
                if (o.equals(obj)) {
                    return true;
                }
            }
        }
        return false;
    }
        
    /**
     * finish掉Activity
     */
    public void finish(String className, String... classNames) {
        List<WeakReference<Activity>> list = new ArrayList<>(runningActivites.values());
        Collections.reverse(list);//倒序，后开的先finish
        for (WeakReference<Activity> reference : list) {
            Activity activity = reference.get();
            if (activity != null) {
                String name = activity.getClass().getName();
                if (name.equals(className) || contains(classNames, name)) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * finish掉除参数外的所有Activity
     *
     * @param classNames 此Activity的类名，如果是null将finish所有Activity
     */
    public void finishAllWithout(@Nullable String className, String... classNames) {
        List<WeakReference<Activity>> list = new ArrayList<>(runningActivites.values());
        Collections.reverse(list);//倒序，后开的先finish
        for (WeakReference<Activity> reference : list) {
            Activity activity = reference.get();
            if (activity != null) {
                String name = activity.getClass().getName();
                if (!name.equals(className) && !contains(classNames, name)) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * finish掉所有Activity
     */
    public void finishAll() {
        finishAllWithout(null);
    }

    /**
     * 退回到指定Activity
     *
     * @param className 完整类名
     */
    public void backBo(String className) {
        List<WeakReference<Activity>> list = new ArrayList<>(runningActivites.values());
        Collections.reverse(list);//倒序，后开的先finish
        for (WeakReference<Activity> reference : list) {
            Activity activity = reference.get();
            if (activity != null) {
                String name = activity.getClass().getName();
                if (name.equals(className)) {
                    activity.finish();
                    return;
                }
            }
        }
    }
    
    @Nullable
    public Activity getActivity(String className) {
        WeakReference<Activity> reference = runningActivites.get(className);
        return reference == null ? null : reference.get();
    }
    
    public boolean isAllFinished() {
        return runningActivites.isEmpty();
    }
    
    public List<Activity> getAllActivities() {
        List<Activity> activities = new ArrayList<>();
        for (WeakReference<Activity> reference : runningActivites.values()) {
            Activity activity = reference.get();
            if (activity != null) {
                activities.add(activity);
            }
        }
        return activities;
    }

    /**
     * finish所有Activity后杀死进程
     */
    public void completeExit() {
        isCompleteExit = true;
        List<WeakReference<Activity>> list = new ArrayList<>(runningActivites.values());
        Collections.reverse(list);//倒序，后开的先finish
        for (WeakReference<Activity> reference : list) {
            Activity activity = reference.get();
            if (activity != null) {
                activity.finish();
            }
        }
    }
}
