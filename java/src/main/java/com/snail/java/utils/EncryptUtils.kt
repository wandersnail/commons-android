package com.snail.java.utils

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

/**
 * 描述: 加密工具
 * 时间: 2018/9/4 18:11
 * 作者: zengfansheng
 */
object EncryptUtils {
    const val MD5 = "MD5"
    const val SHA1 = "SHA1"

    /**
     * 在MD5或SHA1加密过的字符串基础上加上分隔符
     * @param code MD5或SHA1加密过的字符串
     * @param separator 分隔符
     */
    @JvmStatic 
    fun addSeparator(code: String, separator: String?): String? {
        try {
            val sb = StringBuilder()
            val loopTimes = code.length
            var i = 0
            while (i < loopTimes) {
                if (i != loopTimes - 2) {
                    sb.append(code.substring(i, i + 2)).append(separator ?: "")
                } else {
                    sb.append(code.substring(i, i + 2))
                }
                i += 2
            }
            return sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取经过MD5加密后的字符串
     * @param plainText 要加密的字符串
     * @param iterations 迭代加密的次数，0表示不加密，1表示md5(plainText), 2表示md5(md5(plainText))...
     * @return 经过MD5算法加密的字符串形式的32位16进制,如果参数表示的字符串为空，返回null
     */
    @JvmStatic 
    fun getMD5Code(plainText: String, iterations: Int): String? {
        var iters = iterations
        if (iters > 0) {
            iters--
            var result = getMD5Code(plainText)
            if (iters > 0 && result != null) {
                result = getMD5Code(result, iters)
            }
            return result
        }
        return null
    }

    /**
     * 获取经过SHA1加密后的字符串
     * @param plainText 要加密的字符串
     * @param iterations 迭代加密的次数，0表示不加密，1表示md5(plainText), 2表示md5(md5(plainText))...
     * @return 经过SHA1算法加密的字符串形式的32位16进制,如果参数表示的字符串为空，返回null
     */
    @JvmStatic 
    fun getSHA1Code(plainText: String, iterations: Int): String? {
        var iters = iterations
        if (iters > 0) {
            iters--
            var result = getSHA1Code(plainText)
            if (iters > 0 && result != null) {
                result = getSHA1Code(result, iters)
            }
            return result
        }
        return null
    }

    /**
     * 将MD5或SHA1码一段字符串替换成随机16进制字符
     * @param code 需要替换的MD5或SHA1码
     * @param offset 偏移量，即从第几个字符开始替换
     * @param len 要替换的字符数
     * @return 替换后的新字符串，如果参数表示的MD5码为空，则返回null
     */
    @JvmStatic 
    fun replaceMessageDigestCharacter(code: String, offset: Int, len: Int): String {
        val charArr = "1234567890abcdef".toCharArray()
        val md5Arr = code.toCharArray()
        val n = charArr.size
        val random = Random()
        for (i in offset until offset + len) {
            val randomChar = charArr[random.nextInt(n)]
            md5Arr[i] = randomChar
        }
        return String(md5Arr, 0, md5Arr.size)
    }

    /**
     * 获取经过MD5加密后的字符串
     * @param plainText 要加密的字符串
     */
    @JvmStatic 
    fun getMD5Code(plainText: String): String? {
        return encryptByMessageDigest(
            plainText.toByteArray(),
            MD5
        )
    }

    /**
     * 获取经过SHA1加密后的字符串
     * @param plainText 要加密的字符串
     */
    @JvmStatic 
    fun getSHA1Code(plainText: String): String? {
        return encryptByMessageDigest(
            plainText.toByteArray(),
            SHA1
        )
    }

    /**
     * 获取文件的md5值
     * @param path 文件的路径
     * @return md5值，文件不存在返回null
     */
    @JvmStatic 
    fun getFileMD5Code(path: String): String? {
        try {
            val fis = FileInputStream(path)
            return getMD5Code(fis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取文件的SHA1值
     * @param path 文件的路径
     * @return SHA1值，文件不存在返回null
     */
    @JvmStatic 
    fun getFileSHA1Code(path: String): String? {
        try {
            val fis = FileInputStream(path)
            return getSHA1Code(fis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取文件的md5值
     * @param file 文件
     * @return md5值，文件不存在返回null
     */
    @JvmStatic 
    fun getFileMD5Code(file: File): String? {
        try {
            val fis = FileInputStream(file)
            return getMD5Code(fis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取文件的SHA1值
     * @param file 文件
     * @return SHA1值，文件不存在返回null
     */
    @JvmStatic 
    fun getFileSHA1Code(file: File): String? {
        try {
            val fis = FileInputStream(file)
            return getSHA1Code(fis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 从输入流获取MD5值
     * @param inputStream 输入流
     */
    @JvmStatic 
    fun getMD5Code(inputStream: InputStream): String? {
        return encryptByMessageDigest(
            inputStream,
            MD5
        )
    }

    /**
     * 从输入流获取SHA1值
     * @param inputStream 输入流
     */
    @JvmStatic 
    fun getSHA1Code(inputStream: InputStream): String? {
        return encryptByMessageDigest(
            inputStream,
            SHA1
        )
    }

    /**
     * 加密输入流
     * @param inputStream 输入流
     * @param algorithm 算法。[MD5], [SHA1]
     */
    @JvmStatic 
    fun encryptByMessageDigest(inputStream: InputStream, algorithm: String): String? {
        try {
            val messageDigest = MessageDigest.getInstance(algorithm)
            val buf = ByteArray(1024)
            var len= inputStream.read(buf)
            while (len != -1) {
                messageDigest.update(buf, 0, len)
                len= inputStream.read(buf)
            }
            inputStream.close()
            return encryptByMessageDigest(messageDigest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 执行加密
     * @param bytes 需要加密的字节
     * @param algorithm 算法。[MD5], [SHA1]
     */
    @JvmStatic 
    fun encryptByMessageDigest(bytes: ByteArray, algorithm: String): String? {
        try {
            val md = MessageDigest.getInstance(algorithm)
            md.update(bytes)
            return encryptByMessageDigest(md)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun encryptByMessageDigest(md: MessageDigest): String {
        val digest = md.digest()
        val toRet = StringBuilder()
        for (b in digest) {
            val hex = Integer.toHexString(b.toInt() and 0xff)
            if (hex.length == 1) {
                toRet.append("0")
            }
            toRet.append(hex)
        }
        return toRet.toString()
    }

    /**
     * 加密一个文本，返回base64编码后的内容。
     * @param seed  种子 密码
     * @param plain  原文
     * @return 密文
     */
    @Throws(Exception::class)
    @JvmStatic 
    fun encrypt(seed: String, plain: String): String {
        val rawKey = getRawKey(seed.toByteArray())
        val encrypted = encrypt(rawKey, plain.toByteArray())
        return Base64.getEncoder().encodeToString(encrypted)
    }

    /**
     * 解密base64编码后的密文
     * @param seed  种子 密码
     * @param encrypted  密文
     * @return 原文
     */
    @Throws(Exception::class)
    @JvmStatic 
    fun decrypt(seed: String, encrypted: String): String {
        val rawKey = getRawKey(seed.toByteArray())        
        val enc = Base64.getDecoder().decode(encrypted.toByteArray())
        val result = decrypt(rawKey, enc)
        return String(result)
    }

    @Throws(Exception::class)
    private fun getRawKey(seed: ByteArray): ByteArray {
        val keygen = KeyGenerator.getInstance("AES")
        val random = SecureRandom.getInstance("SHA1PRNG")
        random.setSeed(seed)
        keygen.init(128, random) // 192 and 256 bits may not be available
        val key = keygen.generateKey()
        return key.encoded
    }

    @Throws(Exception::class)
    private fun encrypt(raw: ByteArray, plain: ByteArray): ByteArray {
        val keySpec = SecretKeySpec(raw, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        return cipher.doFinal(plain)
    }

    @Throws(Exception::class)
    private fun decrypt(raw: ByteArray, encrypted: ByteArray): ByteArray {
        val keySpec = SecretKeySpec(raw, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        return cipher.doFinal(encrypted)
    }
}
