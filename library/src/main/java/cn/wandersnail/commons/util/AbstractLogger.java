package cn.wandersnail.commons.util;

import android.util.Log;

/**
 * date: 2020/5/20 10:56
 * author: zengfansheng
 */
public abstract class AbstractLogger {
    protected abstract boolean accept(int priority, String tag, String msg);

    public void v(String tag, String msg) {
        if (accept(Log.VERBOSE, tag, msg)) {
            Log.v(tag, msg);
        }
    }

    public void v(String tag, String msg, Throwable t) {
        if (accept(Log.VERBOSE, tag, msg)) {
            Log.v(tag, msg, t);
        }
    }

    public void d(String tag, String msg) {
        if (accept(Log.DEBUG, tag, msg)) {
            Log.d(tag, msg);
        }
    }

    public void d(String tag, String msg, Throwable t) {
        if (accept(Log.DEBUG, tag, msg)) {
            Log.d(tag, msg, t);
        }
    }

    public void i(String tag, String msg) {
        if (accept(Log.INFO, tag, msg)) {
            Log.i(tag, msg);
        }
    }

    public void i(String tag, String msg, Throwable t) {
        if (accept(Log.INFO, tag, msg)) {
            Log.i(tag, msg, t);
        }
    }

    public void w(String tag, String msg) {
        if (accept(Log.WARN, tag, msg)) {
            Log.w(tag, msg);
        }
    }

    public void w(String tag, String msg, Throwable t) {
        if (accept(Log.WARN, tag, msg)) {
            Log.w(tag, msg, t);
        }
    }

    public void e(String tag, String msg) {
        if (accept(Log.ERROR, tag, msg)) {
            Log.e(tag, msg);
        }
    }

    public void e(String tag, String msg, Throwable t) {
        if (accept(Log.ERROR, tag, msg)) {
            Log.e(tag, msg, t);
        }
    }
}
