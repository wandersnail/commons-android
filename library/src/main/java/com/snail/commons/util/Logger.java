package com.snail.commons.util;

import android.util.Log;

/**
 * date: 2019/8/6 21:19
 * author: zengfansheng
 */
public class Logger {
    public static final int NONE = 1;
    public static final int VERBOSE = NONE << 1;
    public static final int DEBUG = VERBOSE << 1;
    public static final int INFO = DEBUG << 1;
    public static final int WARN = INFO << 1;
    public static final int ERROR = WARN << 1;
    public static final int ALL = VERBOSE | INFO | DEBUG | WARN | ERROR;

    private static int printLevel;
    private static Filter filter;

    public interface Filter {
        boolean accept(String tag, String log);
    }

    /**
     * 控制输出级别
     *
     * @param level {@link #NONE}, {@link #VERBOSE}, {@link #DEBUG}, {@link #INFO}, {@link #WARN}...
     */
    public static void setPrintLevel(int level) {
        printLevel = level;
    }

    public static void setFilter(Filter filter) {
        Logger.filter = filter;
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

    private static boolean accept(int priority, String tag, String msg) {
        int level = getLevel(priority);
        return (printLevel & NONE) != NONE && (printLevel & level) == level &&
                (filter == null || filter.accept(tag, msg));
    }

    public static void v(String tag, String msg) {
        if (accept(Log.VERBOSE, tag, msg)) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable t) {
        if (accept(Log.VERBOSE, tag, msg)) {
            Log.v(tag, msg, t);
        }
    }

    public static void d(String tag, String msg) {
        if (accept(Log.DEBUG, tag, msg)) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        if (accept(Log.DEBUG, tag, msg)) {
            Log.d(tag, msg, t);
        }
    }

    public static void i(String tag, String msg) {
        if (accept(Log.INFO, tag, msg)) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable t) {
        if (accept(Log.INFO, tag, msg)) {
            Log.i(tag, msg, t);
        }
    }

    public static void w(String tag, String msg) {
        if (accept(Log.WARN, tag, msg)) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable t) {
        if (accept(Log.WARN, tag, msg)) {
            Log.w(tag, msg, t);
        }
    }

    public static void e(String tag, String msg) {
        if (accept(Log.ERROR, tag, msg)) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (accept(Log.ERROR, tag, msg)) {
            Log.e(tag, msg, t);
        }
    }
}
