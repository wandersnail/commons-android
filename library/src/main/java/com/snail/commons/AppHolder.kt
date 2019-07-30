package com.snail.commons

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import java.lang.ref.WeakReference
import java.util.*
import kotlin.system.exitProcess

/**
 * 描述:
 * 时间: 2018/8/17 13:23
 * 作者: zengfansheng
 */
class AppHolder private constructor() : Application.ActivityLifecycleCallbacks {
    private val activityMap = HashMap<String, WeakReference<Activity>>()
    private var isCompleteExit: Boolean = false
    private var app: Application? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        //尝试获取application
        mainHandler.post {
            if (app == null) {
                synchronized(this@AppHolder) {
                    if (app == null) {
                        app = tryGetApplication()
                        app?.registerActivityLifecycleCallbacks(this@AppHolder)
                    }
                }
            }
        }
    }

    private fun tryGetApplication(): Application? {
        try {
            val clazz = Class.forName("android.app.ActivityThread")
            val acThreadMethod = clazz.getMethod("currentActivityThread")
            acThreadMethod.isAccessible = true
            val acThread = acThreadMethod.invoke(null)
            val appMethod = acThread.javaClass.getMethod("getApplication")
            return appMethod.invoke(acThread) as Application
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private object Holder {
        internal val appHolder = AppHolder()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        synchronized(this) {
            activityMap.put(activity.javaClass.name, WeakReference(activity))
        }
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        synchronized(this) {
            activityMap.remove(activity.javaClass.name)
            if (isCompleteExit && activityMap.isEmpty()) {
                android.os.Process.killProcess(android.os.Process.myPid())
                exitProcess(0)
            }
        }
    }

    companion object {

        @JvmStatic
        fun init(app: Application) {
            synchronized(Holder.appHolder) {
                if (Holder.appHolder.app != null) {
                    Holder.appHolder.app!!.unregisterActivityLifecycleCallbacks(Holder.appHolder)
                }
                Holder.appHolder.app = app
                app.registerActivityLifecycleCallbacks(Holder.appHolder)
            }
        }

        @JvmStatic
        val context: Context
            get() = if (Holder.appHolder.app == null) {
                Holder.appHolder.tryGetApplication() ?: throw RuntimeException("The AppHolder has not been initialized, make sure to call AppHolder.init(app) first.")
            } else Holder.appHolder.app!!

        @JvmStatic
        fun postToMainThread(runnable: Runnable) {
            if (Looper.myLooper() == Looper.getMainLooper()) { //判断是否在主线程
                runnable.run()
            } else {
                Holder.appHolder.mainHandler.post { runnable.run() }
            }
        }

        @JvmStatic
        val packageInfo: PackageInfo?
            get() {
                val pm = context.packageManager
                try {
                    return pm?.getPackageInfo(context.packageName, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }

        /** 程序是否在前台运行  */
        @JvmStatic
        val isAppOnForeground: Boolean
            get() {
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
                if (activityManager != null) {
                    val appProcesses = activityManager.runningAppProcesses ?: return false
                    return appProcesses.firstOrNull { it.processName == context.packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND } != null
                }
                return false
            }

        /**
         * finish掉Activity
         */
        @JvmStatic
        fun finish(className: String, vararg classNames: String) {
            val iterator = Holder.appHolder.activityMap.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val value = entry.value
                if (value.get() == null) {
                    iterator.remove()
                } else if (classNames.contains(entry.key) || entry.key == className) {
                    value.get()!!.finish()
                }
            }
        }

        /**
         * finish掉除参数外的所有Activity
         *
         * @param classNames 此Activity的类名，如果是null将finish所有Activity
         */
        @JvmStatic
        fun finishAllWithout(className: String?, vararg classNames: String) {
            val iterator = Holder.appHolder.activityMap.entries.reversed().iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val value = entry.value
                if (value.get() == null) {
                    Holder.appHolder.activityMap.remove(entry.key)
                } else if (!classNames.contains(entry.key) && entry.key != className) {
                    value.get()!!.finish()
                }
            }
        }

        /**
         * finish掉所有Activity
         */
        @JvmStatic
        fun finishAll() {
            finishAllWithout(null)
        }

        /**
         * 退回到指定Activity
         *
         * @param className 完整类名
         */
        @JvmStatic
        fun backTo(className: String) {
            val list = ArrayList(Holder.appHolder.activityMap.values).reversed()
            for (i in list.indices) {
                val ref = list[i]
                val activity = ref.get()
                if (activity != null) {
                    if (activity.javaClass.name == className) {
                        break
                    } else {
                        activity.finish()
                    }
                }
            }
        }

        @JvmStatic
        fun getActivity(className: String): Activity? {
            val reference = Holder.appHolder.activityMap[className]
            return reference?.get()
        }

        @JvmStatic
        val isAllActivitiesFinished: Boolean
            get() = Holder.appHolder.activityMap.isEmpty()

        @JvmStatic
        val allActivities: List<Activity>
            get() {
                val list = ArrayList<Activity>()
                for (reference in Holder.appHolder.activityMap.values) {
                    if (reference.get() != null) {
                        list.add(reference.get()!!)
                    }
                }
                return list
            }

        /**
         * 完全退出，杀死进程
         */
        @JvmStatic
        fun completeExit() {
            Holder.appHolder.isCompleteExit = true
            val iterator = Holder.appHolder.activityMap.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val value = entry.value
                if (value.get() == null) {
                    iterator.remove()
                } else {
                    value.get()!!.finish()
                }
            }
        }
    }
}
