package com.snail.commons;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * 时间: 2018/8/17 13:23
 * 作者: zengfansheng
 */
public class AppHolder implements Application.ActivityLifecycleCallbacks {
    private Map<String, WeakReference<Activity>> activities = new HashMap<>();
    private boolean isCompleteExit;
    private Application app;
    private Handler mainHandler;
    
    private AppHolder() {
        mainHandler = new Handler(Looper.getMainLooper());
        //尝试获取application
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (app == null) {
                    synchronized (AppHolder.this) {
                        if (app == null) {
                            app = tryGetApplication();
                            if (app != null) {
                                app.registerActivityLifecycleCallbacks(AppHolder.this);
                            }
                        }
                    }
                }                
            }
        });
    }

    private Application tryGetApplication() {
        try {
            Class<?> clazz = Class.forName("android.app.ActivityThread");
            Method acThreadMethod = clazz.getMethod("currentActivityThread");
            acThreadMethod.setAccessible(true);
            Object acThread = acThreadMethod.invoke(null);
            Method appMethod = acThread.getClass().getMethod("getApplication");
            return (Application) appMethod.invoke(acThread);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class Holder {
        private static final AppHolder APP_HOLDER = new AppHolder();
    }
    
    public static void init(@NonNull Application app) {
        synchronized (Holder.APP_HOLDER) {
            if (Holder.APP_HOLDER.app != null) {
                Holder.APP_HOLDER.app.unregisterActivityLifecycleCallbacks(Holder.APP_HOLDER);
            }
            Holder.APP_HOLDER.app = app;
            app.registerActivityLifecycleCallbacks(Holder.APP_HOLDER);
        }
    }
    
    public static Context getContext() {
        if (Holder.APP_HOLDER.app == null) {
            Application app = Holder.APP_HOLDER.tryGetApplication();
            if (app == null) {
                throw new RuntimeException("AppHolder is uninitialized, please invoke AppHolder.init(app)");
            }
            return app;
        }
        return Holder.APP_HOLDER.app;
    }
    
    public static void postToMainThread(final Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {//判断是否在主线程
            runnable.run();            
        } else {
            Holder.APP_HOLDER.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            });
        }
    }
    
    public static PackageInfo getPackageInfo() {
        PackageManager pm = getContext().getPackageManager();
        try {
            return pm.getPackageInfo(getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 程序是否在前台运行 */
    public static boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) return false;
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(getContext().getPackageName()) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * finish掉Activity
     */
    public static void finish(String... classNames) {
        List<String> names = classNames == null ? new ArrayList<String>() : Arrays.asList(classNames);
        Iterator<Map.Entry<String, WeakReference<Activity>>> iterator = Holder.APP_HOLDER.activities.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, WeakReference<Activity>> entry = iterator.next();
            WeakReference<Activity> value = entry.getValue();
            if (value.get() == null) {
                iterator.remove();
            } else if (names.contains(entry.getKey())) {
                value.get().finish();
            }
        }
    }

    /**
     * finish掉除参数外的所以Activity
     *
     * @param classNames 此Activity的类名，如果是null将finish所有Activity
     */
    public static void finishAllWithout(String className, String... classNames) {
        List<String> names = classNames == null ? new ArrayList<String>() : Arrays.asList(classNames);
        Iterator<Map.Entry<String, WeakReference<Activity>>> iterator = Holder.APP_HOLDER.activities.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, WeakReference<Activity>> entry = iterator.next();
            WeakReference<Activity> value = entry.getValue();
            if (value.get() == null) {
                iterator.remove();
            } else if (!names.contains(entry.getKey()) && !entry.getKey().equals(className)) {
                value.get().finish();
            }
        }
    }

    /**
     * 退回到指定Activity
     *
     * @param className 完成类名
     */
    public static void backTo(String className) {
        List<WeakReference<Activity>> list = new ArrayList<>(Holder.APP_HOLDER.activities.values());
        int index = list.size() - 1;
        for (int i = index; i >= 0; i--) {
            WeakReference<Activity> ref = list.get(i);
            Activity activity = ref.get();
            if (activity != null && !activity.getClass().getName().equals(className)) {
                activity.finish();
            }
        }
    }

    public static Activity getActivity(String className) {
        WeakReference<Activity> reference = Holder.APP_HOLDER.activities.get(className);
        return reference == null ? null : reference.get();
    }

    public static boolean isAllActivitiesFinished() {
        return Holder.APP_HOLDER.activities.isEmpty();
    }

    public static List<Activity> getAllActivities() {
        List<Activity> list = new ArrayList<>();
        for (WeakReference<Activity> reference : Holder.APP_HOLDER.activities.values()) {
            if (reference.get() != null) {
                list.add(reference.get());
            }
        }
        return list;
    }

    /**
     * 完全退出，杀死进程
     */
    public static void completeExit() {
        Holder.APP_HOLDER.isCompleteExit = true;
        Iterator<Map.Entry<String, WeakReference<Activity>>> iterator = Holder.APP_HOLDER.activities.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, WeakReference<Activity>> entry = iterator.next();
            WeakReference<Activity> value = entry.getValue();
            if (value.get() == null) {
                iterator.remove();
            } else {
                value.get().finish();
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        synchronized (this) {
            activities.put(activity.getClass().getName(), new WeakReference<>(activity));
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        synchronized (this) {
            activities.remove(activity.getClass().getName());
            if (isCompleteExit && activities.isEmpty()) {
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }
}
