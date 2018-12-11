package com.snail.commons.entity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.snail.commons.utils.DateUtils;
import com.snail.commons.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * 描述: 崩溃处理
 * 时间: 2018/8/17 11:46
 * 作者: zengfansheng
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private File logSaveDir;
    private UncaughtExceptionHandler defaultHandler;
    private HandleCallback callback;
    private String appVerName;
    private String packageName;
    private String appName;
    
    private CrashHandler() {}
    
    private static class Holder {
        private static final CrashHandler HANDLER = new CrashHandler();
    }
    
    public static CrashHandler getInstance() {
        return Holder.HANDLER;
    }

    /**
     * 初始化
     * @param logSaveDir 崩溃日志保存目录
     */
    public void init(@NonNull Context context, File logSaveDir, HandleCallback callback) {
        String appName = "CrashLogs";
        try {
            PackageManager packageManager = context.getPackageManager();
            packageName = context.getPackageName();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            appName = context.getResources().getString(packageInfo.applicationInfo.labelRes);
            this.appName = appName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (logSaveDir == null) {
            logSaveDir = new File(Environment.getExternalStorageDirectory(), appName);
        }
        if (!logSaveDir.exists()) {
            logSaveDir.mkdirs();
        }
        this.logSaveDir = logSaveDir;
        this.callback = callback;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        PackageManager pm = context.getPackageManager();
        try {
            appVerName = pm.getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
        FileOutputStream fos = null;
        StringWriter sw = null;
        PrintWriter pw = null;
        try {            
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            pw.println("CRASH_TIME=" + DateUtils.formatDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS"));
            //获取手机的环境
            appendParams(pw, Arrays.asList("DEVICE", "MODEL", "SUPPORTED_ABIS", "REGION", "SOFT_VERSION", "BRAND"), Build.class.getDeclaredFields());
            appendParams(pw, Arrays.asList("RELEASE", "SECURITY_PATCH", "CODENAME"), Build.VERSION.class.getDeclaredFields());            
            pw.println("APP_VERSION=" + appVerName);
            pw.println("APP_NAME=" + appName);
            pw.println("APP_PACKAGE_NAME=" + packageName);
            e.printStackTrace(pw);
            pw.println("\n");
            File file = new File(logSaveDir, "crash_log_" + DateUtils.formatDate(System.currentTimeMillis(), "yyyy-MM-dd") + ".txt");
            fos = new FileOutputStream(file, true);
            String detailError = sw.toString();
            fos.write(detailError.getBytes());
            if (callback != null) {
                return callback.onSaved(detailError, e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();            
        } finally {
            IOUtils.closeQuietly(fos, pw, sw);            
        }
        return false;
    }

    private void appendParams(PrintWriter pw, List<String> needInfos, Field[] fields) throws IllegalAccessException {
        for (Field field : fields) {
            field.setAccessible(true);
            if (needInfos.contains(field.getName().toUpperCase())) {
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


    public interface HandleCallback {
        /**
         * 日志保存完毕
         * @param detailError 详细错误信息
         * @param e 原始的异常信息  
         * @return 返回true，则交给默认处理器，false则         
         */
        boolean onSaved(@NonNull String detailError, Throwable e);
    }
}
