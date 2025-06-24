package cn.wandersnail.commons.util;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import java.io.FileInputStream;

/**
 * Created by 曾繁盛 on 2025/4/16 18:00
 */
public class ProcessUtil {
    /**
     * 获取正在运行的进程数
     */
    public static int getRunningProcessCount(@NonNull Context context) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        return am == null ? 0 : am.getRunningAppProcesses().size();
    }


    public static String getProcessName(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // API 28及以上使用系统方法
            return Application.getProcessName();
        } else {
            String processName = readProcessNameFromProc();
            if (processName != null) {
                return processName;
            }
            return getProcessNameFromActivityManager(context);
        }
    }

    // 读取/proc/self/cmdline获取进程名
    private static String readProcessNameFromProc() {
        try (FileInputStream in = new FileInputStream("/proc/self/cmdline")) {
            byte[] buffer = new byte[256];
            int len = in.read(buffer);
            if (len > 0) {
                int end = 0;
                // 查找第一个null字节的位置
                while (end < len && buffer[end] != 0) {
                    end++;
                }
                return new String(buffer, 0, end);
            }
        } catch (Throwable ignore) {
        }
        return null;
    }

    private static String getProcessNameFromActivityManager(Context context) {
        if (context == null) return null;
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return null;
        for (ActivityManager.RunningAppProcessInfo processInfo : am.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return null;
    }
}
