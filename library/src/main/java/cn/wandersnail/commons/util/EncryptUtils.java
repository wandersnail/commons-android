package cn.wandersnail.commons.util;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * date: 2019/8/7 16:19
 * author: zengfansheng
 */
public class EncryptUtils {
    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA1";
    public static final String SHA256 = "SHA-256";

    /**
     * 在各算法的哈希结果基础上加上分隔符
     *
     * @param code      MD5或SHA1加密过的字符串
     * @param separator 分隔符
     */
    public static String addSeparator(@NonNull String code, String separator) {
        try {
            StringBuilder sb = new StringBuilder();
            int loopTimes = code.length();
            int i = 0;
            while (i < loopTimes) {
                if (i != loopTimes - 2) {
                    sb.append(code.substring(i, i + 2)).append(separator == null ? "" : separator);
                } else {
                    sb.append(code.substring(i, i + 2));
                }
                i += 2;
            }
            return sb.toString();
        } catch (Exception e) {
            Log.e("EncryptUtils", "addSeparator", e);
        }
        return null;
    }

    /**
     * 获取通过MD5计算的哈希值
     *
     * @param plainText  要加密的字符串
     * @param iterations 迭代加密的次数，0表示不加密，1表示md5(plainText), 2表示md5(md5(plainText))...
     * @return 经过MD5算法计算的哈希值形式的32位16进制, 如果参数表示的字符串为空，返回null
     */
    public static String getMD5Code(@NonNull String plainText, int iterations) {
        if (iterations > 0) {
            iterations--;
            String result = getMD5Code(plainText);
            if (iterations > 0 && result != null) {
                result = getMD5Code(result, iterations);
            }
            return result;
        }
        return null;
    }

    /**
     * 获取通过SHA1计算的哈希值
     *
     * @param plainText  要加密的字符串
     * @param iterations 迭代加密的次数，0表示不加密，1表示md5(plainText), 2表示md5(md5(plainText))...
     * @return 经过SHA1算法计算的哈希值形式的32位16进制, 如果参数表示的字符串为空，返回null
     */
    public static String getSHA1Code(@NonNull String plainText, int iterations) {
        if (iterations > 0) {
            iterations--;
            String result = getSHA1Code(plainText);
            if (iterations > 0 && result != null) {
                result = getSHA1Code(result, iterations);
            }
            return result;
        }
        return null;
    }

    /**
     * 获取通过SHA256计算的哈希值
     *
     * @param plainText  要加密的字符串
     * @param iterations 迭代加密的次数，0表示不加密，1表示md5(plainText), 2表示md5(md5(plainText))...
     * @return 经过SHA256算法计算的哈希值形式的32位16进制, 如果参数表示的字符串为空，返回null
     */
    public static String getSHA256Code(@NonNull String plainText, int iterations) {
        if (iterations > 0) {
            iterations--;
            String result = getSHA256Code(plainText);
            if (iterations > 0 && result != null) {
                result = getSHA256Code(result, iterations);
            }
            return result;
        }
        return null;
    }

    /**
     * 将哈希值的一段字符串替换成随机16进制字符
     *
     * @param code   需要替换的哈希值
     * @param offset 偏移量，即从第几个字符开始替换
     * @param len    要替换的字符数
     * @return 替换后的新字符串，如果参数表示的MD5码为空，则返回null
     */
    public static String replaceMessageDigestCharacter(String code, int offset, int len) {
        char[] charArr = "1234567890abcdef".toCharArray();
        char[] md5Arr = code.toCharArray();
        Random random = new Random();
        for (int i = offset; i < offset + len; i++) {
            char randomChar = charArr[random.nextInt(charArr.length)];
            md5Arr[i] = randomChar;
        }
        return new String(md5Arr, 0, md5Arr.length);
    }

    /**
     * 获取通过MD5计算的哈希值
     *
     * @param plainText 要计算的字符串
     */
    public static String getMD5Code(String plainText) {
        return encryptByMessageDigest(plainText.getBytes(), MD5);
    }

    /**
     * 获取通过SHA1计算的哈希值
     *
     * @param plainText 要计算的字符串
     */
    public static String getSHA1Code(String plainText) {
        return encryptByMessageDigest(plainText.getBytes(), SHA1);
    }

    /**
     * 获取通过SHA256计算的哈希值
     *
     * @param plainText 要计算的字符串
     */
    public static String getSHA256Code(String plainText) {
        return encryptByMessageDigest(plainText.getBytes(), SHA256);
    }

    /**
     * 获取文件的md5值
     *
     * @param path 文件的路径
     * @return md5值，文件不存在返回null
     */
    public static String getFileMD5Code(String path) {
        InputStream fis = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                fis = Files.newInputStream(Paths.get(path));
            } else {
                fis = new FileInputStream(path);
            }
            String code = getMD5Code(fis);
            fis.close();
            return code;
        } catch (Exception e) {
            Log.e("EncryptUtils", "getFileMD5Code", e);
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    /**
     * 获取文件的SHA1值
     *
     * @param path 文件的路径
     * @return SHA1值，文件不存在返回null
     */
    public static String getFileSHA1Code(String path) {
        InputStream fis = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                fis = Files.newInputStream(Paths.get(path));
            } else {
                fis = new FileInputStream(path);
            }
            String code = getSHA1Code(fis);
            fis.close();
            return code;
        } catch (Exception e) {
            Log.e("EncryptUtils", "getFileSHA1Code", e);
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    /**
     * 获取文件的SHA256值
     *
     * @param path 文件的路径
     * @return SHA256值，文件不存在返回null
     */
    public static String getFileSHA256Code(String path) {
        InputStream fis = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                fis = Files.newInputStream(Paths.get(path));
            } else {
                fis = new FileInputStream(path);
            }
            String code = getSHA256Code(fis);
            fis.close();
            return code;
        } catch (Exception e) {
            Log.e("EncryptUtils", "getFileSHA256Code", e);
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    /**
     * 获取文件的md5值
     *
     * @param file 文件
     * @return md5值，文件不存在返回null
     */
    public static String getFileMD5Code(File file) {
        return file == null ? null : getFileMD5Code(file.getPath());
    }

    /**
     * 获取文件的SHA1值
     *
     * @param file 文件
     * @return SHA1值，文件不存在返回null
     */
    public static String getFileSHA1Code(File file) {
        return file == null ? null : getFileSHA1Code(file.getPath());
    }

    /**
     * 获取文件的SHA256值
     *
     * @param file 文件
     * @return SHA256值，文件不存在返回null
     */
    public static String getFileSHA256Code(File file) {
        return file == null ? null : getFileSHA256Code(file.getPath());
    }

    /**
     * 从输入流获取MD5值
     *
     * @param inputStream 输入流
     */
    public static String getMD5Code(InputStream inputStream) {
        return encryptByMessageDigest(inputStream, MD5);
    }

    /**
     * 从输入流获取SHA1值
     *
     * @param inputStream 输入流
     */
    public static String getSHA1Code(InputStream inputStream) {
        return encryptByMessageDigest(inputStream, SHA1);
    }

    /**
     * 从输入流获取SHA256值
     *
     * @param inputStream 输入流
     */
    public static String getSHA256Code(InputStream inputStream) {
        return encryptByMessageDigest(inputStream, SHA256);
    }

    /**
     * 加密输入流，不执行流关闭
     *
     * @param inputStream 输入流
     * @param algorithm   算法。{@link #MD5}, {@link #SHA1}, {@link #SHA256}
     */
    public static String encryptByMessageDigest(InputStream inputStream, String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byte[] buf = new byte[40960];
            int len;
            while ((len = inputStream.read(buf)) != -1) {
                messageDigest.update(buf, 0, len);
            }
            return encryptByMessageDigest(messageDigest);
        } catch (Exception e) {
            Log.e("EncryptUtils", "encryptByMessageDigest", e);
        }
        return null;
    }

    /**
     * 执行哈希计算
     *
     * @param bytes     需要加密的字节
     * @param algorithm 算法。{@link #MD5}, {@link #SHA1}, {@link #SHA256}
     */
    public static String encryptByMessageDigest(byte[] bytes, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(bytes);
            return encryptByMessageDigest(md);
        } catch (Exception e) {
            Log.e("EncryptUtils", "encryptByMessageDigest", e);
        }
        return null;
    }

    private static String encryptByMessageDigest(MessageDigest md) {
        byte[] digest = md.digest();
        StringBuilder toRet = new StringBuilder();
        for (byte b : digest) {
            String hex = StringUtils.toHex(((int) b) & 0xff);
            toRet.append(hex);
        }
        return toRet.toString();
    }

    /**
     * 加密一个文本，返回base64编码后的内容。
     *
     * @param seed  种子 密码
     * @param plain 原文
     * @return 密文
     */
    public static String encrypt(String seed, String plain, String iv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(seed.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        return Base64.encodeToString(cipher.doFinal(plain.getBytes()), Base64.NO_WRAP);
    }

    /**
     * 解密base64编码后的密文
     *
     * @param seed      种子 密码
     * @param encrypted 密文
     * @return 原文
     */
    public static String decrypt(String seed, String encrypted, String iv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(seed.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        byte[] enc = Base64.decode(encrypted.getBytes(), Base64.NO_WRAP);
        return new String(cipher.doFinal(enc));
    }
}
