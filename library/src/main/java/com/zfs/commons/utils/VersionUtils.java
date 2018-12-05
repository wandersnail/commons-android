package com.zfs.commons.utils;

/**
 * Created by zeng on 2016/8/30.
 * 版本号工具
 */
public class VersionUtils {
    /**
     * 抽取版本号。如果是xxx1.2.3 Rev 456之类的，只对1.2.3进行抽取
     */
    public static String extractVer(String ver) {
        if (ver == null) {
            return null;
        }
        if (ver.contains(" ")) {
            ver = ver.substring(0, ver.indexOf(" "));
        }
        ver = ver.replaceAll("^\\D+", "");
        return ver;
    }

    /**
     * 将数字抽取出来。如果是xxx1.2.3 Rev 456之类的，只对1.2.3进行抽取
     */
    public static String[] splitVer(String ver) {
        ver = extractVer(ver);
        if (ver == null) {
            return null;
        }
        return ver.split("\\D+");
    }

    /**
     * 递归比较大小
     * @param index 从第几个元素开始比较
     * @return 相等，则返回值0；小于，则返回负数；大于，则返回正数。
     */
    private static int compare(String[] ver1, String[] ver2, int index) {
        try {
            int a = Integer.valueOf(ver1[index]);
            int b = Integer.valueOf(ver2[index]);
            if (a == b) {
                if (ver1.length - 1 == index || ver2.length - 1 == index) {
                    return 0;
                } else {
                    return compare(ver1, ver2, index + 1);
                }
            } else {
                return a - b;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 比较版本号大小
     * @return 相等，则返回值0；小于，则返回负数；大于，则返回正数。
     */
    public static int compareVersion(String ver1, String ver2) {
        return compare(splitVer(ver1), splitVer(ver2), 0);
    }
}
