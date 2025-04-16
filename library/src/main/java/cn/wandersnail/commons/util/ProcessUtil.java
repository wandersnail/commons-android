package cn.wandersnail.commons.util;

import android.app.Application;
import android.os.Build;

import java.io.FileInputStream;

/**
 * Created by 曾繁盛 on 2025/4/16 18:00
 */
public class ProcessUtil {

    public static String getProcessName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // API 28及以上使用系统方法
            return Application.getProcessName();
        } else {
            // 读取/proc/self/cmdline获取进程名
            return readProcessNameFromProc();
        }
    }

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
        } catch (Exception ignore) {
        }
        return null;
    }
}
