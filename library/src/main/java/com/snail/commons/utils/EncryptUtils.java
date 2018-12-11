package com.snail.commons.utils;

import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 描述: 加密工具
 * 时间: 2018/9/4 18:11
 * 作者: zengfansheng
 */
public class EncryptUtils {
    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA1";

    /**
     * 在MD5或SHA1加密过的字符串基础上加上分隔符
     * @param code MD5或SHA1加密过的字符串
     * @param separator 分隔符
     */
    public static String addSeparator(@NonNull String code, String separator) {
        try {
            StringBuilder sb = new StringBuilder();
            int loopTimes = code.length();
            for (int i = 0; i < loopTimes; i += 2) {
                if (i != loopTimes - 2) {
                    sb.append(code.substring(i, i + 2)).append(separator == null ? "" : separator);
                } else {
                    sb.append(code.substring(i, i + 2));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获取经过MD5加密后的字符串
     * @param plainText 要加密的字符串
     * @param iterations 迭代加密的次数，0表示不加密，1表示md5(plainText), 2表示md5(md5(plainText))...
     * @return 经过MD5算法加密的字符串形式的32位16进制,如果参数表示的字符串为空，返回null
     */
    public static String getMD5Code(@NonNull String plainText, int iterations) {
        if (iterations > 0) {
            iterations--;
            String result = getMD5Code(plainText);
            if (iterations > 0) {
                result = getMD5Code(result, iterations);
            }
            return result;
        }
        return null;
    }

    /**
     * 获取经过SHA1加密后的字符串
     * @param plainText 要加密的字符串
     * @param iterations 迭代加密的次数，0表示不加密，1表示md5(plainText), 2表示md5(md5(plainText))...
     * @return 经过SHA1算法加密的字符串形式的32位16进制,如果参数表示的字符串为空，返回null
     */
    public static String getSHA1Code(@NonNull String plainText, int iterations) {
        if (iterations > 0) {
            iterations--;
            String result = getSHA1Code(plainText);
            if (iterations > 0) {
                result = getSHA1Code(result, iterations);
            }
            return result;
        }
        return null;
    }

    /**
     * 将MD5或SHA1码一段字符串替换成随机16进制字符
     * @param code 需要替换的MD5或SHA1码
     * @param offset 偏移量，即从第几个字符开始替换
     * @param len 要替换的字符数
     * @return 替换后的新字符串，如果参数表示的MD5码为空，则返回null
     */
    public static String replaceMessageDigestCharacter(@NonNull String code, int offset, int len) {
        char[] charArr = "1234567890abcdef".toCharArray();
        char[] md5Arr = code.toCharArray();
        int n = charArr.length;
        Random random = new Random();
        for (int i = offset; i < offset+len; i++) {
            char randomChar = charArr[random.nextInt(n)];
            md5Arr[i] = randomChar;
        }
        return new String(md5Arr, 0, md5Arr.length);
    }

    /**
     * 获取经过MD5加密后的字符串
     * @param plainText 要加密的字符串
     */
    public static String getMD5Code(@NonNull String plainText) {
        return encryptByMessageDigest(plainText.getBytes(), MD5);
    }

    /**
     * 获取经过SHA1加密后的字符串
     * @param plainText 要加密的字符串
     */
    public static String getSHA1Code(@NonNull String plainText) {
        return encryptByMessageDigest(plainText.getBytes(), SHA1);
    }

    /**
     * 获取文件的md5值
     * @param path 文件的路径
     * @return md5值，文件不存在返回null
     */
    public static String getFileMD5Code(@NonNull String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            return getMD5Code(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件的SHA1值
     * @param path 文件的路径
     * @return SHA1值，文件不存在返回null
     */
    public static String getFileSHA1Code(@NonNull String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            return getSHA1Code(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件的md5值
     * @param file 文件
     * @return md5值，文件不存在返回null
     */
    public static String getFileMD5Code(@NonNull File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return getMD5Code(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件的SHA1值
     * @param file 文件
     * @return SHA1值，文件不存在返回null
     */
    public static String getFileSHA1Code(@NonNull File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return getSHA1Code(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从输入流获取MD5值
     * @param in 输入流
     */
    public static String getMD5Code(@NonNull InputStream in) {
        return encryptByMessageDigest(in, MD5);
    }

    /**
     * 从输入流获取SHA1值
     * @param in 输入流
     */
    public static String getSHA1Code(@NonNull InputStream in) {
        return encryptByMessageDigest(in, SHA1);
    }
    
    /**
     * 加密输入流
     * @param in 输入流
     * @param algorithm 算法。{@link #MD5}, {@link #SHA1}
     */
    public static String encryptByMessageDigest(@NonNull InputStream in, @NonNull String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                messageDigest.update(buf, 0, len);
            }
            in.close();
            return encryptByMessageDigest(messageDigest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行加密
     * @param bytes 需要加密的字节
     * @param algorithm 算法。{@link #MD5}, {@link #SHA1}
     */
    public static String encryptByMessageDigest(@NonNull byte[] bytes, @NonNull String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(bytes);
            return encryptByMessageDigest(md);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static String encryptByMessageDigest(@NonNull MessageDigest md) {
        byte[] digest = md.digest();
        StringBuilder toRet = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() == 1) {
                toRet.append("0");
            }
            toRet.append(hex);
        }
        return toRet.toString();
    }

    /**
     * 加密一个文本，返回base64编码后的内容。
     * @param seed  种子 密码
     * @param plain  原文
     * @return 密文
     */
    public static String encrypt(String seed, String plain) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] encrypted = encrypt(rawKey, plain.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    /**
     * 解密base64编码后的密文
     * @param seed  种子 密码
     * @param encrypted  密文
     * @return 原文
     */
    public static String decrypt(String seed, String encrypted)
            throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] enc = Base64.decode(encrypted.getBytes(), Base64.DEFAULT);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        random.setSeed(seed);
        keygen.init(128, random); // 192 and 256 bits may not be available
        SecretKey key = keygen.generateKey();
        return key.getEncoded();
    }

    private static byte[] encrypt(byte[] raw, byte[] plain) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(plain);
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted)
            throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(encrypted);
    }
}
