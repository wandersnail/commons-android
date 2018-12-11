package com.snail.commons.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zeng on 2016/6/1.
 */
public class MathUtils {
    /**
     * 精确到几位小数，不进行4舍5入
     *
     * @param num   数字
     * @param scale 取几位小数
     */
    public static double setDoubleAccuracy(double num, int scale) {
        return ((int) (num * Math.pow(10, scale))) / Math.pow(10, scale);
    }

    /**
     * 计算各值占的比例，相加为100%
     *
     * @param scale 取几位小数。12.3%表示1位小数
     */
    public static float[] getPercents(int scale, float... values) {
        if (values != null) {
            float total = 0;
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < values.length; i++) {
                if (values[i] != 0) {
                    list.add(i);
                }
                total += values[i];
            }

            if (total == 0) {
                return new float[values.length];
            }

            float[] fs = new float[values.length];
            int sc = (int) Math.pow(10, scale + 2);
            float sum = 0;
            for (int i = 0; i < list.size(); i++) {
                int index = list.get(i);
                if (i == list.size() - 1) {
                    fs[index] = 1 - sum;
                } else {
                    //先强转int不进行4舍5入，再转为float计算
                    fs[index] = (int) (values[index] / total * sc) / (float) sc;
                    sum += fs[index];
                }
            }
            return fs;
        }
        return null;
    }

    /**
     * 将整数转字节数组
     *
     * @param bigEndian ture表示高位在前，false表示低位在前
     * @param value     整数，short、int、long
     * @param len 结果取几个字节，如是高位在前，从数组后端向前计数；如是低位在前，从数组前端向后计数
     */
    public static byte[] numberToBytes(boolean bigEndian, long value, int len) {
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            int j = bigEndian ? 7 - i : i;
            bytes[i] = (byte) ((value >> (8 * j)) & 0xFF);
        }
        return len > 8 ? bytes : Arrays.copyOfRange(bytes, bigEndian ? 8 - len : 0, bigEndian ? 8 : len);
    }

    /**
     * 将字节数组转long数值
     *
     * @param bigEndian ture表示高位在前，false表示低位在前
     * @param src       待转字节数组
     */
    public static long bytesToLong(boolean bigEndian, byte... src) {
        int len = Math.min(8, src.length);
        byte[] bs = new byte[8];
        System.arraycopy(src, 0, bs, bigEndian ? 8 - len : 0, len);
        long value = 0;
        // 循环读取每个字节通过移位运算完成long的8个字节拼装
        for (int i = 0; i < 8; ++i) {
            int shift = (bigEndian ? (7 - i) : i) << 3;
            value |= ((long) 0xff << shift) & ((long) bs[i] << shift);
        }
        if (src.length == 1) {
            return (byte) value;
        } else if (src.length == 2) {
            return (short) value;
        } else if (src.length <= 4) {
            return (int) value;
        }
        return value;
    }

    /**
     * 将字节数组转int数值
     *
     * @param bigEndian ture表示高位在前，false表示低位在前
     * @param src      待转字节数组
     */
    public static int bytesToInt(boolean bigEndian, byte... src) {
        return (int) bytesToLong(bigEndian, src);
    }

    /**
     * 将字节数组转int数值
     *
     * @param bigEndian ture表示高位在前，false表示低位在前
     * @param src      待转字节数组
     */
    public static short bytesToShort(boolean bigEndian, byte... src) {
        return (short) bytesToLong(bigEndian, src);
    }

    /**
     * 翻转整个数组，每个bit。如10000110 00110001转换成10001100 01100001
     */
    public static byte[] reverseBitAndByte(byte[] src) {
        if (src == null || src.length == 0) {
            return null;
        }
        byte[] target = new byte[src.length];
        //翻转byte同时翻转bit
        for (int i = 0; i < src.length; i++) {
            //翻转bit
            int value = 0;
            int tmp = src[src.length - 1 - i];
            for (int j = 7; j >= 0; j--) {
                value |= (tmp & 0x01) << j;
                tmp >>= 1;
            }
            target[i] = (byte) value;
        }
        return target;
    }

    /**
     * 分包
     *
     * @param src  源
     * @param size 包大小，字节
     * @return 分好的包的集合
     */
    public static List<byte[]> splitPackage(byte[] src, int size) {
        List<byte[]> list = new ArrayList<>();
        int loopCount = src.length / size + (src.length % size == 0 ? 0 : 1);
        for (int j = 0; j < loopCount; j++) {
            int from = j * size;
            int to = Math.min(src.length, from + size);
            list.add(Arrays.copyOfRange(src, j * size, to));
        }
        return list;
    }

    /**
     * 合包
     *
     * @param src 源
     * @return 合好的字节数组
     */
    public static byte[] joinPackage(byte[]... src) {
        byte[] bytes = new byte[0];
        if (src != null) {
            for (byte[] bs : src) {
                bytes = Arrays.copyOf(bytes, bytes.length + bs.length);
                System.arraycopy(bs, 0, bytes, bytes.length - bs.length, bs.length);
            }
        }
        return bytes;
    }

    /**
     * CRC16校验，Modbus
     */
    public static int calcCRC16_Modbus(byte[] data) {
        int crc = 0xffff;//16位  
        for (byte b : data) {
            if (b < 0) {
                crc ^= (int) b + 256; // XOR byte into least sig. byte of
            } else {
                crc ^= (int) b; // XOR byte into least sig. byte of crc  
            }
            for (int i = 8; i != 0; i--) { // Loop over each bit  
                if ((crc & 0x0001) != 0) { // If the LSB is set  
                    crc >>= 1; // Shift right and XOR 0xA001  
                    crc ^= 0xA001;
                } else
                    // Else LSB is not set  
                    crc >>= 1; // Just shift right  
            }
        }
        return crc;
    }

    /**
     * CRC校验，CRC-CCITT (XModem)
     */
    public static int calcCRC_CCITT_XModem(byte[] bytes) {
        int crc = 0x00;          // initial value  
        int polynomial = 0x1021;
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }
        crc &= 0xffff;
        return crc;
    }

    /**
     * CRC校验，CRC-CCITT (0xFFFF)
     */
    public static int calcCRC_CCITT_0xFFFF(byte[] bytes) {
        int crc = 0xffff; // initial value
        int polynomial = 0x1021; // poly value
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit)
                    crc ^= polynomial;
            }
        }
        crc &= 0xffff;
        return crc;
    }
}