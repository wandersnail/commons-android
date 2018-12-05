package com.zfs.commons.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
    /**
     * 将输入流数据写入输出流中
     * @param in 输入流
     * @param out 输出流
     */
    public static void inToOut(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len=in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }

    /**
     * 关闭一个或多个流对象
     * @param closeables 可关闭的流对象列表
     */
    public static void close(Closeable... closeables) throws IOException {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        }
    }

    /**
     * 关闭一个或多个流对象，内部捕获IO异常
     * @param closeables 可关闭的流对象列表
     */
    public static void closeQuietly(Closeable... closeables) {
        try {
            close(closeables);
        } catch (IOException e) {
            // do nothing
        }
    }

    /**
     * 从输入流中获取字符串
     * @param in 输入流
     * @param enc 返回的字符串采用的字符集, 如果为null则使用平台默认的字符集
     */
    public static String toString(InputStream in, String enc) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            String s;
            if (enc == null) {
                s = out.toString();
            } else {
                s = out.toString(enc);
            }            
            out.close();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}