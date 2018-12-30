package com.snail.commons.entity

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import com.snail.commons.utils.DateUtils
import com.snail.commons.utils.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Thread.UncaughtExceptionHandler
import java.lang.reflect.Field
import java.util.*

/**
 * 描述: 崩溃处理
 * 时间: 2018/8/17 11:46
 * 作者: zengfansheng
 */
class CrashHandler private constructor() : UncaughtExceptionHandler {
    private var logSaveDir: File? = null
    private var defaultHandler: UncaughtExceptionHandler? = null
    private var callback: HandleCallback? = null
    private var appVerName: String? = null
    private var packageName: String? = null
    private var appName: String? = null

    private object Holder {
        internal val HANDLER = CrashHandler()
    }

    /**
     * 初始化
     * @param logSaveDir 崩溃日志保存目录
     */
    fun init(context: Context, logSaveDir: File?, callback: HandleCallback?) {
        var saveDir = logSaveDir
        var appName = "CrashLogs"
        try {
            val packageManager = context.packageManager
            packageName = context.packageName
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            appName = context.resources.getString(packageInfo.applicationInfo.labelRes)
            this.appName = appName
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (saveDir == null) {
            saveDir = File(Environment.getExternalStorageDirectory(), appName)
        }
        if (!saveDir.exists()) {
            saveDir.mkdirs()
        }
        this.logSaveDir = saveDir
        this.callback = callback
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        val pm = context.packageManager
        try {
            appVerName = pm.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        if (saveErrorLog(e)) {
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(0)
        } else if (defaultHandler != null) {
            defaultHandler!!.uncaughtException(t, e)
        }
    }

    private fun saveErrorLog(e: Throwable): Boolean {
        var fos: FileOutputStream? = null
        var sw: StringWriter? = null
        var pw: PrintWriter? = null
        try {
            sw = StringWriter()
            pw = PrintWriter(sw)
            pw.println("CRASH_TIME=" + DateUtils.formatDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"))
            //获取手机的环境
            appendParams(pw, Arrays.asList("DEVICE", "MODEL", "SUPPORTED_ABIS", "REGION", "SOFT_VERSION", "BRAND"), Build::class.java.declaredFields)
            appendParams(pw, Arrays.asList("RELEASE", "SECURITY_PATCH", "CODENAME"), Build.VERSION::class.java.declaredFields)
            pw.println("APP_VERSION=" + appVerName!!)
            pw.println("APP_NAME=" + appName!!)
            pw.println("APP_PACKAGE_NAME=" + packageName!!)
            e.printStackTrace(pw)
            pw.println("\n")
            val file = File(logSaveDir, "crash_log_" + DateUtils.formatDate(System.currentTimeMillis(), "yyyy-MM-dd") + ".txt")
            fos = FileOutputStream(file, true)
            val detailError = sw.toString()
            fos.write(detailError.toByteArray())
            if (callback != null) {
                return callback!!.onSaved(detailError, e)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            IOUtils.close(sw, pw, fos)
        }
        return false
    }

    @Throws(IllegalAccessException::class)
    private fun appendParams(pw: PrintWriter, needInfos: List<String>, fields: Array<Field>) {
        for (field in fields) {
            field.isAccessible = true
            if (needInfos.contains(field.name.toUpperCase(Locale.ENGLISH))) {
                var value = ""
                val o = field.get(null)
                if (o != null) {
                    if (o.javaClass.isArray) {
                        val sb = StringBuilder()
                        val os = o as Array<Any>
                        for (i in os.indices) {
                            val o1 = os[i]
                            if (i == 0) {
                                sb.append("[")
                            }
                            if (i == os.size - 1) {
                                sb.append(o1)
                                sb.append("]")
                            }
                            if (i != os.size - 1) {
                                sb.append(o1).append(",")
                            }
                        }
                        value = sb.toString()
                    } else {
                        value = o.toString()
                    }
                }
                pw.println(field.name + "=" + value)
            }
        }
    }


    interface HandleCallback {
        /**
         * 日志保存完毕
         * @param detailError 详细错误信息
         * @param e 原始的异常信息
         * @return 返回true，则交给默认处理器，false则
         */
        fun onSaved(detailError: String, e: Throwable): Boolean
    }

    companion object {

        val instance: CrashHandler
            get() = Holder.HANDLER
    }
}
