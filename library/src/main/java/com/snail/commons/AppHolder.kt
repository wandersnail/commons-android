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

/**
 * 描述:
 * 时间: 2018/8/17 13:23
 * 作者: zengfansheng
 */
class AppHolder private constructor() : Application.ActivityLifecycleCallbacks {
    private val activities = HashMap<String, WeakReference<Activity>>()
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
        internal val APP_HOLDER = AppHolder()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        synchronized(this) {
            activities.put(activity.javaClass.name, WeakReference(activity))
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
            activities.remove(activity.javaClass.name)
            if (isCompleteExit && activities.isEmpty()) {
                System.exit(0)
                android.os.Process.killProcess(android.os.Process.myPid())
            }
        }
    }

    companion object {

        fun init(app: Application) {
            synchronized(Holder.APP_HOLDER) {
                if (Holder.APP_HOLDER.app != null) {
                    Holder.APP_HOLDER.app!!.unregisterActivityLifecycleCallbacks(Holder.APP_HOLDER)
                }
                Holder.APP_HOLDER.app = app
                app.registerActivityLifecycleCallbacks(Holder.APP_HOLDER)
            }
        }

        val context: Context
            get() = if (Holder.APP_HOLDER.app == null) {
                Holder.APP_HOLDER.tryGetApplication() ?: throw RuntimeException("The AppHolder has not been initialized, make sure to call AppHolder.init(app) first.")
            } else Holder.APP_HOLDER.app!!

        fun postToMainThread(runnable: Runnable) {
            if (Looper.myLooper() == Looper.getMainLooper()) { //判断是否在主线程
                runnable.run()
            } else {
                Holder.APP_HOLDER.mainHandler.post { runnable.run() }
            }
        }

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
            val iterator = Holder.APP_HOLDER.activities.entries.iterator()
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
         * finish掉除参数外的所以Activity
         *
         * @param classNames 此Activity的类名，如果是null将finish所有Activity
         */
        fun finishAllWithout(className: String, vararg classNames: String) {
            val iterator = Holder.APP_HOLDER.activities.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val value = entry.value
                if (value.get() == null) {
                    iterator.remove()
                } else if (!classNames.contains(entry.key) && entry.key != className) {
                    value.get()!!.finish()
                }
            }
        }

        /**
         * 退回到指定Activity
         *
         * @param className 完成类名
         */
        fun backTo(className: String) {
            val list = ArrayList(Holder.APP_HOLDER.activities.values)
            val index = list.size - 1
            for (i in index downTo 0) {
                val ref = list[i]
                val activity = ref.get()
                if (activity != null && activity.javaClass.name != className) {
                    activity.finish()
                }
            }
        }

        fun getActivity(className: String): Activity? {
            val reference = Holder.APP_HOLDER.activities[className]
            return reference?.get()
        }

        val isAllActivitiesFinished: Boolean
            get() = Holder.APP_HOLDER.activities.isEmpty()

        val allActivities: List<Activity>
            get() {
                val list = ArrayList<Activity>()
                for (reference in Holder.APP_HOLDER.activities.values) {
                    if (reference.get() != null) {
                        list.add(reference.get()!!)
                    }
                }
                return list
            }

        /**
         * 完全退出，杀死进程
         */
        fun completeExit() {
            Holder.APP_HOLDER.isCompleteExit = true
            val iterator = Holder.APP_HOLDER.activities.entries.iterator()
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
