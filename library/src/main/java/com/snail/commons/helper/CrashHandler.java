package com.snail.commons.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * date: 2019/8/6 14:16
 * author: zengfansheng
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private final File logDir;
    private Thread.UncaughtExceptionHandler defaultHandler;
    private Callback callback;
    private final String appVerName;
    private final String packageName;
    private final String appName;

    public CrashHandler(@NonNull Context context, @NonNull File logDir, Callback callback) {
        Objects.requireNonNull(context, "context is null");
        Objects.requireNonNull(logDir, "logDir is null");
        this.logDir = logDir;
        this.callback = callback;
        packageName = context.getPackageName();
        String appName = "CrashLogs";
        String appVerName = "null";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            appVerName = packageInfo.versionName;
            appName = context.getResources().getString(packageInfo.applicationInfo.labelRes);
        } catch (Exception ignore) {
        }
        this.appName = appName;
        this.appVerName = appVerName;
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (saveErrorLog(e)) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } else if (defaultHandler != null) {
            defaultHandler.uncaughtException(t, e);
        }
    }

    private boolean saveErrorLog(Throwable e) {
        String time = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(System.currentTimeMillis());
        File file = new File(logDir, String.format("crash_log_%s.txt", time));
        StringWriter sw = new StringWriter();
        OutputStream out = null;
        try {
            PrintWriter pw = new PrintWriter(sw);
            pw.println("*********************************** CRASH START ***********************************");
            String crashTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH).format(System.currentTimeMillis());
            pw.println("CRASH_TIME=" + crashTime);
            //获取手机的环境
            appendParams(pw, Arrays.asList("DEVICE", "MODEL", "SUPPORTED_ABIS", "REGION", "SOFT_VERSION", "BRAND"),
                    Build.class.getDeclaredFields());
            appendParams(pw, Arrays.asList("RELEASE", "SECURITY_PATCH", "CODENAME"), Build.VERSION.class.getDeclaredFields());
            pw.println("APP_VERSION=" + appVerName);
            pw.println("APP_NAME=" + appName);
            pw.println("APP_PACKAGE_NAME=" + packageName);
            e.printStackTrace(pw);
            pw.println("*********************************** CRASH END ***********************************\n");
            String detailError = sw.toString();
            out = new FileOutputStream(file, true);
            out.write(detailError.getBytes());
            out.close();
            if (callback != null) {
                return callback.onSaved(detailError, e);
            } else {
                return false;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void appendParams(PrintWriter pw, List<String> needInfos, Field[] fields) throws IllegalAccessException {
        for (Field field : fields) {
            field.setAccessible(true);
            if (needInfos.contains(field.getName().toUpperCase(Locale.ENGLISH))) {
                String value = "";
                Object o = field.get(null);
                if (o != null) {
                    if (o.getClass().isArray()) {
                        StringBuilder sb = new StringBuilder();
                        Object[] os = (Object[]) o;
                        for (int i = 0; i < os.length; i++) {
                            Object o1 = os[i];
                            if (i == 0) {
                                sb.append("[");
                            }
                            if (i == os.length - 1) {
                                sb.append(o1);
                                sb.append("]");
                            }
                            if (i != os.length - 1) {
                                sb.append(o1).append(",");
                            }
                        }
                        value = sb.toString();
                    } else {
                        value = o.toString();
                    }
                }
                pw.println(field.getName() + "=" + value);
            }
        }
    }

    public interface Callback {
        /**
         * 日志保存完毕
         *
         * @param detailError 详细错误信息
         * @param e           原始的异常信息
         * @return true：直接杀死进程；false：交给默认处理器
         */
        boolean onSaved(@NonNull String detailError, @NonNull Throwable e);
    }
}
