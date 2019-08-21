package cn.wandersnail.commons.util;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 版本号工具
 * <p>
 * date: 2019/8/6 18:21
 * author: zengfansheng
 */
public class VersionUtils {
    /**
     * 抽取版本号。如果是xxx1.2.3 Rev 456之类的，只对1.2.3进行抽取
     */
    @NonNull
    public static String extractVersion(@NonNull String version) {
        if (version.contains(" ")) {
            version = version.substring(0, version.indexOf(" "));
        }
        return version.replace("^\\D+", "");
    }

    /**
     * 将数字抽取出来。如果是xxx1.2.3 Rev 456之类的，只对1.2.3进行抽取
     */
    public static String[] splitVersion(@NonNull String version) {
        version = extractVersion(version);
        String[] strings = version.split("\\D+");
        List<String> list = new ArrayList<>();
        for (String s : strings) {
            if (s.isEmpty()) {
                break;
            } else {
                list.add(s);
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * 递归比较大小
     *
     * @param index 从第几个元素开始比较
     * @return 相等，则返回值0；小于，则返回负数；大于，则返回正数。
     */
    private static int compare(String[] ver1, String[] ver2, int index) {
        try {
            int a = Integer.valueOf(ver1[index]);
            int b = Integer.valueOf(ver2[index]);
            if (a == b) {
                if (ver1.length - 1 == index || ver2.length - 1 == index) {
                    return Integer.compare(ver1.length, ver2.length);
                } else {
                    return compare(ver1, ver2, index + 1);
                }
            } else {
                return a - b;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 比较版本号大小
     *
     * @return 相等，则返回值0；小于，则返回负数；大于，则返回正数。
     */
    public static int compareVersion(@NonNull String ver1, @NonNull String ver2) {
        if (ver1.isEmpty() && ver2.isEmpty()) {
            return 0;
        } else if (ver1.isEmpty()) {
            return -1;
        } else if (ver2.isEmpty()) {
            return 1;
        }
        try {
            //如果是纯数字，则转换成Long型直接比较
            return ver1.compareTo(ver2);
        } catch (Exception e) {
            //转换失败，则进行字符串比较
            return compare(splitVersion(ver1), splitVersion(ver2), 0);
        }
    }
}
