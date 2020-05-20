package cn.wandersnail.commons.util;

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
    private static AbstractLogger logger = new AbstractLogger() {
        @Override
        protected boolean accept(int priority, String tag, String msg) {
            int level = getLevel(priority);
            return (printLevel & NONE) != NONE && (printLevel & level) == level &&
                    (filter == null || filter.accept(tag, msg));
        }
    };

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

    public static void v(String tag, String msg) {
        logger.v(tag, msg);
    }

    public static void v(String tag, String msg, Throwable t) {
        logger.v(tag, msg, t);
    }

    public static void d(String tag, String msg) {
        logger.d(tag, msg);
    }

    public static void d(String tag, String msg, Throwable t) {
        logger.d(tag, msg, t);
    }

    public static void i(String tag, String msg) {
        logger.i(tag, msg);
    }

    public static void i(String tag, String msg, Throwable t) {
        logger.i(tag, msg, t);
    }

    public static void w(String tag, String msg) {
        logger.w(tag, msg);
    }

    public static void w(String tag, String msg, Throwable t) {
        logger.w(tag, msg, t);
    }

    public static void e(String tag, String msg) {
        logger.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable t) {
        logger.e(tag, msg, t);
    }
}
