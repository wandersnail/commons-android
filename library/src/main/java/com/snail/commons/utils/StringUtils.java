package com.snail.commons.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

/**
 * Created by zengfs on 2015/11/12.
 */
public class StringUtils {
    /**
     * byte数组转换成16进制字符串
     * @param src 源
     * @param separator 用来分隔的字符串
     */
    public static String bytesToHexString(byte[] src, String separator) {
        if (src == null || src.length <= 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            if (separator != null) {
                stringBuilder.append(separator);
            }
        }
        String s = stringBuilder.toString().toUpperCase(Locale.ENGLISH);
        if (separator != null) {
            s = s.substring(0, s.length() - separator.length());
        }
        return s;
    }

    /**
     * byte数组转换成2进制字符串
     */
    public static String bytesToBinaryString(byte[] src, String separator) {
        if (src == null || src.length <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toBinaryString(v);
            for (int i = 0; i < 8 - hv.length(); i++) {
                sb.append(0);
            }
            sb.append(hv);
            if (separator != null) {
				sb.append(separator);
            }
        }
        String s = sb.toString();
		if (separator != null) {
			s = s.substring(0, s.length() - separator.length());
		}
        return s;
    }
	
	/**
     * 使用java正则表达式去掉小数点后多余的0，如最后一位是.则去掉
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0  
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }
        return s;
    }

	/**
     * 将异常信息转换成字符串
     */
    public static String getDetailMsg(Throwable t) {
        if (t != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            return sw.toString();
        }
        return "";
    }
	
	/**
	 * 格式00:00:00
     * @param duration 时长，单位：秒
	 */
	public static String formatDuration(int duration) {
        return String.format(Locale.US, "%02d:%02d:%02d", duration / 3600, duration % 3600 / 60, duration % 60);
	}
}
