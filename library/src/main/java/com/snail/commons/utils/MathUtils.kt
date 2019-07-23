package com.snail.commons.utils

import java.util.*
import kotlin.math.min
import kotlin.math.pow

/**
 * Created by zeng on 2016/6/1.
 */
object MathUtils {
    /**
     * 精确到几位小数，不进行4舍5入
     *
     * @param num   数字
     * @param scale 取几位小数
     */
    @JvmStatic 
    fun setDoubleAccuracy(num: Double, scale: Int): Double {
        return (num * 10.0.pow(scale.toDouble())).toInt() / 10.0.pow(scale.toDouble())
    }

    /**
     * 计算各值占的比例，相加为100%
     *
     * @param scale 取几位小数。12.3%表示1位小数
     */
    @JvmStatic 
    fun getPercents(scale: Int, vararg values: Float): FloatArray {
        var total = 0f
        val list = ArrayList<Int>()
        for (i in values.indices) {
            if (values[i] != 0f) {
                list.add(i)
            }
            total += values[i]
        }

        if (total == 0f) {
            return FloatArray(values.size)
        }

        val fs = FloatArray(values.size)
        val sc = 10.0.pow((scale + 2).toDouble()).toInt()
        var sum = 0f
        for (i in list.indices) {
            val index = list[i]
            if (i == list.size - 1) {
                fs[index] = 1 - sum
            } else {
                //先强转int不进行4舍5入，再转为float计算
                fs[index] = (values[index] / total * sc).toInt() / sc.toFloat()
                sum += fs[index]
            }
        }
        return fs
    }

    /**
     * 将整数转字节数组
     *
     * @param bigEndian true表示高位在前，false表示低位在前
     * @param value     整数，short、int、long
     * @param len 结果取几个字节，如是高位在前，从数组后端向前计数；如是低位在前，从数组前端向后计数
     */
    @JvmStatic 
    fun numberToBytes(bigEndian: Boolean, value: Long, len: Int): ByteArray {
        val bytes = ByteArray(8)
        for (i in 0..7) {
            val j = if (bigEndian) 7 - i else i
            bytes[i] = (value shr 8 * j and 0xFF).toByte()
        }
        return if (len > 8) bytes else Arrays.copyOfRange(bytes, if (bigEndian) 8 - len else 0, if (bigEndian) 8 else len)
    }

    /**
     * 将字节数组转long数值
     *
     * @param bigEndian true表示高位在前，false表示低位在前
     * @param src       待转字节数组
     */
    @JvmStatic 
    fun bytesToLong(bigEndian: Boolean, vararg src: Byte): Long {
        val len = min(8, src.size)
        val bs = ByteArray(8)
        System.arraycopy(src, 0, bs, if (bigEndian) 8 - len else 0, len)
        var value: Long = 0
        // 循环读取每个字节通过移位运算完成long的8个字节拼装
        for (i in 0..7) {
            val shift = (if (bigEndian) 7 - i else i) shl 3
            value = value or (0xff.toLong() shl shift and (bs[i].toLong() shl shift))
        }
        return when {
            src.size == 1 -> value.toByte().toLong()
            src.size == 2 -> value.toShort().toLong()
            src.size <= 4 -> value.toInt().toLong()
            else -> value
        }
    }

    /**
     * 将字节数组转int数值
     *
     * @param bigEndian true表示高位在前，false表示低位在前
     * @param src      待转字节数组
     */
    @JvmStatic 
    fun bytesToInt(bigEndian: Boolean, vararg src: Byte): Int {
        return bytesToLong(bigEndian, *src).toInt()
    }

    /**
     * 将字节数组转int数值
     *
     * @param bigEndian true表示高位在前，false表示低位在前
     * @param src      待转字节数组
     */
    @JvmStatic 
    fun bytesToShort(bigEndian: Boolean, vararg src: Byte): Short {
        return bytesToLong(bigEndian, *src).toShort()
    }

    /**
     * 翻转整个数组，每个bit。如10000110 00110001转换成10001100 01100001
     */
    @JvmStatic 
    fun reverseBitAndByte(src: ByteArray): ByteArray {
        if (src.isEmpty()) {
            return src
        }
        val target = ByteArray(src.size)
        //翻转byte同时翻转bit
        for (i in src.indices) {
            //翻转bit
            var value = 0
            var tmp = src[src.size - 1 - i].toInt()
            for (j in 7 downTo 0) {
                value = value or (tmp and 0x01 shl j)
                tmp = tmp shr 1
            }
            target[i] = value.toByte()
        }
        return target
    }

    /**
     * 分包
     *
     * @param src  源
     * @param size 包大小，字节
     * @return 分好的包的集合
     */
    @JvmStatic 
    fun splitPackage(src: ByteArray, size: Int): List<ByteArray> {
        val list = ArrayList<ByteArray>()
        val loopCount = src.size / size + if (src.size % size == 0) 0 else 1
        for (j in 0 until loopCount) {
            val from = j * size
            val to = min(src.size, from + size)
            list.add(Arrays.copyOfRange(src, j * size, to))
        }
        return list
    }

    /**
     * 合包
     *
     * @param src 源
     * @return 合好的字节数组
     */
    @JvmStatic 
    fun joinPackage(vararg src: ByteArray): ByteArray {
        var bytes = ByteArray(0)
        for (bs in src) {
            bytes = bytes.copyOf(bytes.size + bs.size)
            System.arraycopy(bs, 0, bytes, bytes.size - bs.size, bs.size)
        }
        return bytes
    }

    /**
     * CRC16校验，Modbus
     */
    @JvmStatic 
    fun calcCRC16_Modbus(data: ByteArray): Int {
        var crc = 0xffff //16位  
        for (b in data) {
            crc = if (b < 0) {
                crc xor b.toInt() + 256 // XOR byte into least sig. byte of
            } else {
                crc xor b.toInt() // XOR byte into least sig. byte of crc  
            }
            for (i in 8 downTo 1) { // Loop over each bit  
                if (crc and 0x0001 != 0) { // If the LSB is set  
                    crc = crc shr 1 // Shift right and XOR 0xA001  
                    crc = crc xor 0xA001
                } else
                // Else LSB is not set  
                    crc = crc shr 1 // Just shift right  
            }
        }
        return crc
    }

    /**
     * CRC校验，CRC-CCITT (XModem)
     */
    @JvmStatic 
    fun calcCRC_CCITT_XModem(bytes: ByteArray): Int {
        var crc = 0x00          // initial value  
        val polynomial = 0x1021
        for (b in bytes) {
            for (i in 0..7) {
                val bit = b.toInt() shr 7 - i and 1 == 1
                val c15 = crc shr 15 and 1 == 1
                crc = crc shl 1
                if (c15 xor bit) crc = crc xor polynomial
            }
        }
        crc = crc and 0xffff
        return crc
    }

    /**
     * CRC校验，CRC-CCITT (0xFFFF)
     */
    @JvmStatic 
    fun calcCRC_CCITT_0xFFFF(bytes: ByteArray): Int {
        var crc = 0xffff // initial value
        val polynomial = 0x1021 // poly value
        for (b in bytes) {
            for (i in 0..7) {
                val bit = b.toInt() shr 7 - i and 1 == 1
                val c15 = crc shr 15 and 1 == 1
                crc = crc shl 1
                if (c15 xor bit)
                    crc = crc xor polynomial
            }
        }
        crc = crc and 0xffff
        return crc
    }
}