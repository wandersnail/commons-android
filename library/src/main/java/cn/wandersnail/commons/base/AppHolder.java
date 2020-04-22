package cn.wandersnail.commons.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * date: 2019/8/6 10:02
 * author: zengfansheng
 */
public class AppHolder implements Application.ActivityLifecycleCallbacks {
    //正在运行的Activity
    private final List<RunningActivity> runningActivities = new CopyOnWriteArrayList<>();
    //是否完全退出
    private boolean isCompleteExit = false;
    private Application application;
    private Looper mainLooper;
    private RunningActivity topActivity;

    private AppHolder() {
        mainLooper = Looper.getMainLooper();
        //尝试获取application
        application = tryGetApplication();
        if (application != null) {
            application.registerActivityLifecycleCallbacks(this);
        }       
    }

    private static final class Holder {
        private static final AppHolder INSTANCE = new AppHolder();
    }
    
    private static class RunningActivity {
        String name;
        WeakReference<Activity> weakActivity;

        RunningActivity(String name, WeakReference<Activity> weakActivity) {
            this.name = name;
            this.weakActivity = weakActivity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RunningActivity)) return false;
            RunningActivity runningActivity = (RunningActivity) o;
            return name.equals(runningActivity.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
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
        RunningActivity a = new RunningActivity(activity.getClass().getName(), new WeakReference<>(activity));
        if (!runningActivities.contains(a)) {
            runningActivities.add(a);            
        }
        topActivity = a;
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
        if (runningActivities.isEmpty()) {
            topActivity = null;
        }
        RunningActivity a = new RunningActivity(activity.getClass().getName(), new WeakReference<>(activity));
        runningActivities.remove(a);
        if (isCompleteExit && runningActivities.isEmpty()) {
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
        if (mainLooper == null) {
            mainLooper = Looper.getMainLooper();
        }
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
        List<RunningActivity> list = new ArrayList<>(runningActivities);
        Collections.reverse(list);//倒序，后开的先finish
        for (RunningActivity runningActivity : list) {
            Activity activity = runningActivity.weakActivity.get();
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
        List<RunningActivity> list = new ArrayList<>(runningActivities);
        Collections.reverse(list);//倒序，后开的先finish
        for (RunningActivity runningActivity : list) {
            Activity activity = runningActivity.weakActivity.get();
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
    public void backTo(String className) {
        List<RunningActivity> list = new ArrayList<>(runningActivities);
        Collections.reverse(list);//倒序，后开的先finish
        for (RunningActivity runningActivity : list) {
            Activity activity = runningActivity.weakActivity.get();
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
        for (RunningActivity runningActivity : runningActivities) {
            if (runningActivity.name.equals(className)) {
                return runningActivity.weakActivity.get();
            }
        }
        return null;
    }
    
    public boolean isAllFinished() {
        return runningActivities.isEmpty();
    }
    
    public List<Activity> getAllActivities() {
        List<Activity> activities = new ArrayList<>();
        for (RunningActivity runningActivity : runningActivities) {
            Activity activity = runningActivity.weakActivity.get();
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
        List<RunningActivity> list = new ArrayList<>(runningActivities);
        Collections.reverse(list);//倒序，后开的先finish
        for (RunningActivity runningActivity : list) {
            Activity activity = runningActivity.weakActivity.get();
            if (activity != null) {
                activity.finish();
            }
        }
    }
    
    public Activity getTopActivity() {
        return topActivity == null ? null : topActivity.weakActivity.get();
    }
}
