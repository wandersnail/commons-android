package com.snail.commons.utils;

import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

public class Logger {
    public static final int NONE = 1;
    public static final int VERBOSE = NONE << 1;
    public static final int DEBUG = VERBOSE << 1;
    public static final int INFO = DEBUG << 1;
    public static final int WARN = INFO << 1;
    public static final int ERROR = WARN << 1;
    public static final int ALL = VERBOSE | INFO | DEBUG | WARN | ERROR;

    public interface Filter {
        boolean accept(@NonNull String log);
    }

    private static boolean isSaveEnabled = false;
    private static int printLevel = NONE;
    private static Filter filter;

    /**
     * 控制输出级别<br>{@link #NONE}, {@link #VERBOSE}, {@link #DEBUG}, {@link #INFO}, {@link #WARN}, {@link #ERROR}
     */
    public static void setPrintLevel(int printLevel) {
        Logger.printLevel = printLevel;
    }

    public static void setFilter(Filter filter) {
        Logger.filter = filter;
    }

    /**
     * 控制是否执行saveLog方法
     */
    public static void setSaveEnabled(boolean isSaveEnabled) {
        Logger.isSaveEnabled = isSaveEnabled;
    }

    private static boolean accept(int priority, @NonNull String msg) {
        int level = getLevel(priority);
        return (printLevel & NONE) != NONE && (printLevel & level) == level && (filter == null || filter.accept(msg));
    }

    private static int getLevel(int priority) {
        switch (priority) {
            case Log.ERROR:
                return ERROR;
            case Log.WARN:
                return WARN;
            case Log.INFO:
                return INFO;
            case Log.DEBUG:
                return DEBUG;
            case Log.VERBOSE:
                return VERBOSE;
            default:
                return NONE;
        }
    }

    public static void v(String tag, String msg) {
        if (accept(Log.VERBOSE, msg)) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable t) {
        if (accept(Log.VERBOSE, msg)) {
            Log.v(tag, msg, t);
        }
    }

    public static void d(String tag, String msg) {
        if (accept(Log.DEBUG, msg)) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        if (accept(Log.DEBUG, msg)) {
            Log.d(tag, msg, t);
        }
    }

    public static void i(String tag, String msg) {
        if (accept(Log.INFO, msg)) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable t) {
        if (accept(Log.INFO, msg)) {
            Log.i(tag, msg, t);
        }
    }

    public static void w(String tag, String msg) {
        if (accept(Log.WARN, msg)) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable t) {
        if (accept(Log.WARN, msg)) {
            Log.w(tag, msg, t);
        }
    }

    public static void e(String tag, String msg) {
        if (accept(Log.ERROR, msg)) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (accept(Log.ERROR, msg)) {
            Log.e(tag, msg, t);
        }
    }

    public static void saveLog(File file, String log) {
        if (isSaveEnabled) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
                out.write(log);
                out.newLine();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveLog(File file, String appVersion, Throwable t) {
        if (isSaveEnabled) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                //获取手机的环境
                Field[] fields = Build.class.getDeclaredFields();
                pw.println("ERROR_OCCURRENCE_TIME=" + DateUtils.formatDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
                for (Field field : fields) {
                    field.setAccessible(true);
                    pw.println(field.getName() + "=" + field.get(null));
                }
                pw.println("AppVersion=" + appVersion);
                t.printStackTrace(pw);
                pw.println("\n");
                FileOutputStream fos = new FileOutputStream(file, true);
                fos.write(sw.toString().getBytes());
                fos.close();
                pw.close();
                sw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
