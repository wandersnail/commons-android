package com.snail.commons.utils

import android.os.Build
import android.util.Log
import java.io.*

object Logger {
    const val NONE = 1
    const val VERBOSE = NONE shl 1
    const val DEBUG = VERBOSE shl 1
    const val INFO = DEBUG shl 1
    const val WARN = INFO shl 1
    const val ERROR = WARN shl 1
    const val ALL = VERBOSE or INFO or DEBUG or WARN or ERROR

    private var isSaveEnabled = false
    private var printLevel = NONE
    private var filter: Filter? = null

    interface Filter {
        fun accept(tag: String, log: String): Boolean
    }

    /**
     * 控制输出级别.[NONE], [VERBOSE], [DEBUG], [INFO], [WARN], [ERROR]
     */
    fun setPrintLevel(printLevel: Int) {
        Logger.printLevel = printLevel
    }

    fun setFilter(filter: Filter) {
        Logger.filter = filter
    }

    /**
     * 控制是否执行saveLog方法
     */
    fun setSaveEnabled(isSaveEnabled: Boolean) {
        Logger.isSaveEnabled = isSaveEnabled
    }

    private fun accept(priority: Int, tag: String, msg: String): Boolean {
        val level = getLevel(priority)
        return printLevel and NONE != NONE && printLevel and level == level && (filter == null || filter!!.accept(tag, msg))
    }

    private fun getLevel(priority: Int): Int {
        return when (priority) {
            Log.ERROR -> ERROR
            Log.WARN -> WARN
            Log.INFO -> INFO
            Log.DEBUG -> DEBUG
            Log.VERBOSE -> VERBOSE
            else -> NONE
        }
    }

    fun v(tag: String, msg: String) {
        if (accept(Log.VERBOSE, tag, msg)) {
            Log.v(tag, msg)
        }
    }

    fun v(tag: String, msg: String, t: Throwable) {
        if (accept(Log.VERBOSE, tag, msg)) {
            Log.v(tag, msg, t)
        }
    }

    fun d(tag: String, msg: String) {
        if (accept(Log.DEBUG, tag, msg)) {
            Log.d(tag, msg)
        }
    }

    fun d(tag: String, msg: String, t: Throwable) {
        if (accept(Log.DEBUG, tag, msg)) {
            Log.d(tag, msg, t)
        }
    }

    fun i(tag: String, msg: String) {
        if (accept(Log.INFO, tag, msg)) {
            Log.i(tag, msg)
        }
    }

    fun i(tag: String, msg: String, t: Throwable) {
        if (accept(Log.INFO, tag, msg)) {
            Log.i(tag, msg, t)
        }
    }

    fun w(tag: String, msg: String) {
        if (accept(Log.WARN, tag, msg)) {
            Log.w(tag, msg)
        }
    }

    fun w(tag: String, msg: String, t: Throwable) {
        if (accept(Log.WARN, tag, msg)) {
            Log.w(tag, msg, t)
        }
    }

    fun e(tag: String, msg: String) {
        if (accept(Log.ERROR, tag, msg)) {
            Log.e(tag, msg)
        }
    }

    fun e(tag: String, msg: String, t: Throwable) {
        if (accept(Log.ERROR, tag, msg)) {
            Log.e(tag, msg, t)
        }
    }

    fun saveLog(file: File, log: String) {
        if (isSaveEnabled) {
            try {
                val out = BufferedWriter(FileWriter(file, true))
                out.write(log)
                out.newLine()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun saveLog(file: File, appVersion: String, t: Throwable) {
        if (isSaveEnabled) {
            try {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                //获取手机的环境
                val fields = Build::class.java.declaredFields
                pw.println("ERROR_OCCURRENCE_TIME=" + DateUtils.formatDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"))
                for (field in fields) {
                    field.isAccessible = true
                    pw.println(field.name + "=" + field.get(null))
                }
                pw.println("AppVersion=$appVersion")
                t.printStackTrace(pw)
                pw.println("\n")
                val fos = FileOutputStream(file, true)
                fos.write(sw.toString().toByteArray())
                fos.close()
                pw.close()
                sw.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
