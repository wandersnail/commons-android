package com.snail.commons.utils

import android.content.res.ColorStateList
import android.graphics.Color

/**
 * 颜色相关工具类
 *
 * date: 2019/4/10 11:27
 * author: zengfansheng
 */
object ColorUtils {

    /**
     * 取过渡色
     *
     * @param offset 取值范围：0 ~ 1
     */
    @JvmStatic
    fun getColor(startColor: Int, endColor: Int, offset: Float): Int {
        val aa = startColor shr 24 and 0xff
        val ra = startColor shr 16 and 0xff
        val ga = startColor shr 8 and 0xff
        val ba = startColor and 0xff
        val ab = endColor shr 24 and 0xff
        val rb = endColor shr 16 and 0xff
        val gb = endColor shr 8 and 0xff
        val bb = endColor and 0xff
        val a = (aa + (ab - aa) * offset).toInt()
        val r = (ra + (rb - ra) * offset).toInt()
        val g = (ga + (gb - ga) * offset).toInt()
        val b = (ba + (bb - ba) * offset).toInt()
        return Color.argb(a, r, g, b)
    }

    /**
     * @param normal  正常时的颜色
     * @param pressed 按压时的颜色
     * @param disabled 不可用时的颜色
     */
    @JvmStatic
    fun createColorStateList(normal: Int, pressed: Int, disabled: Int): ColorStateList {
        //normal一定要最后
        val states = arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled), intArrayOf())
        return ColorStateList(states, intArrayOf(disabled, pressed, normal))
    }

    /**
     * @param normal  正常时的颜色
     * @param pressed 按压时的颜色
     * @param selected 选中时的颜色
     * @param disabled 不可用时的颜色
     */
    @JvmStatic
    fun createColorStateList(normal: Int, pressed: Int, selected: Int, disabled: Int): ColorStateList {
        //normal一定要最后
        val states = arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled), intArrayOf(android.R.attr.state_selected, android.R.attr.state_enabled), intArrayOf())
        return ColorStateList(states, intArrayOf(disabled, pressed, selected, normal))
    }
}

/*############################################ 扩展函数 #########################################*/

/**
 * Color转换为颜色字符串，格式：#ffffffff
 */
fun Int.toHexColor(): String {
    val bs = ByteArray(4)
    bs[0] = (this shr 24).toByte()
    bs[1] = (this shr 16).toByte()
    bs[2] = (this shr 8).toByte()
    bs[3] = this.toByte()
    return "#${bs.toHexString()}"
}

/**
 * 判断颜色是否深色
 */
fun Int.isColorDark(): Boolean {
    return 1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255 >= 0.5
}