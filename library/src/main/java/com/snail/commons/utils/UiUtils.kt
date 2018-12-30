package com.snail.commons.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Typeface
import android.os.Looper
import android.support.annotation.StringDef
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.snail.commons.AppHolder

/**
 * Created by Zeng on 2015/7/13.
 * 其中很多方法都依赖于自定义Application类
 */
object UiUtils {
    const val ANIM = "anim"
    const val ARRAY = "array"
    const val ATTR = "attr"
    const val BOOL = "bool"
    const val COLOR = "color"
    const val DIMEN = "dimen"
    const val DRAWABLE = "drawable"
    const val ID = "id"
    const val INTEGER = "integer"
    const val LAYOUT = "layout"
    const val MIPMAP = "mipmap"
    const val STRING = "string"
    const val STYLE = "style"

    /**
     * 获取显示屏幕宽度，不包含状态栏和导航栏
     */
    val displayScreenWidth: Int
        get() = AppHolder.context.resources.displayMetrics.widthPixels

    /**
     * 获取显示屏幕高度
     */
    val displayScreenHeight: Int
        get() = AppHolder.context.resources.displayMetrics.heightPixels

    /**
     * 判断当前是否是主线程
     */
    val isMainThread: Boolean
        get() = Looper.myLooper() == Looper.getMainLooper()

    /**
     * 获取状态栏高度
     */
    val statusBarHeight: Int
        get() {
            var result = 0
            val resourceId = AppHolder.context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = AppHolder.context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    @StringDef(ANIM, ARRAY, ATTR, BOOL, COLOR, DIMEN, DRAWABLE, ID, INTEGER, LAYOUT, MIPMAP, STRING, STYLE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ResType

    fun getResId(context: Context, @ResType resType: String, name: String): Int {
        return context.resources.getIdentifier(name, resType, context.packageName)
    }

    /**
     * @return int[0]:宽度，int[1]:高度。
     */
    fun getRealScreenResolution(activity: Activity): IntArray {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getRealMetrics(metrics)
        return intArrayOf(metrics.widthPixels, metrics.heightPixels)
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dp2px(dpValue: Float): Float {
        return dpValue * AppHolder.context.resources.displayMetrics.density + 0.5f
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dp(pxValue: Float): Float {
        return pxValue / AppHolder.context.resources.displayMetrics.density + 0.5f
    }

    /**
     * Color转换为字符串
     */
    fun toHexWithoutAlpha(color: Int): String {
        val sb = StringBuilder(Integer.toHexString(color and 0x00FFFFFF))
        while (sb.length < 6) {
            sb.insert(0, "0")
        }
        sb.insert(0, "#")
        return sb.toString()
    }

    /**
     * 将自己从容器中移除
     */
    fun removeFromContainer(view: View) {
        val parent = view.parent
        if (parent is ViewGroup) {
            parent.removeView(view)
        }
    }

    /**
     * 获取ActionBar的高度
     */
    fun getActionBarSize(context: Context): Float {
        val ta = context.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val height = ta.getDimension(0, 0f)
        ta.recycle()
        return height
    }

    fun sendBroadcast(intent: Intent) {
        AppHolder.context.sendBroadcast(intent)
    }

    /**
     * 设置TextView的字体，字体为外部文件，目录在assets
     *
     * @param context  上下文
     * @param root     根布局
     * @param fontName 字体名
     */
    fun setFont(context: Context, root: View, fontName: String) {
        try {
            if (root is ViewGroup) {
                for (i in 0 until root.childCount) {
                    setFont(context, root.getChildAt(i), fontName)
                }
            } else if (root is TextView) {
                root.typeface = Typeface.createFromAsset(context.assets, fontName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 设置TextView的字体
     *
     * @param root 根布局
     * @param tf   字体
     */
    fun setFont(root: View, tf: Typeface) {
        try {
            if (root is ViewGroup) {
                for (i in 0 until root.childCount) {
                    setFont(root.getChildAt(i), tf)
                }
            } else if (root is TextView) {
                root.typeface = tf
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 设置布局中所有TextView的字体大小
     * @param root 根布局
     */
    fun setTextSize(root: View, unit: Int, size: Float) {
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                setTextSize(root.getChildAt(i), unit, size)
            }
        } else if (root is TextView) {
            root.setTextSize(unit, size)
        }
    }

    /**
     * 设置布局中所有TextView的字体大小
     * @param root 根布局
     */
    fun setTextColor(root: View, color: Int) {
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                setTextColor(root.getChildAt(i), color)
            }
        } else if (root is TextView) {
            root.setTextColor(color)
        }
    }

    /**
     * 获取字体
     *
     * @param path 字体在assets的路径
     */
    fun getTypefaceFromAsset(path: String): Typeface {
        return Typeface.createFromAsset(AppHolder.context.assets, path)
    }

    /**
     * 转黑白
     */
    fun colourToMonochrome(iv: ImageView) {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(matrix)
        iv.colorFilter = filter
    }

    /**
     * 将View的高度设置成状态栏高
     */
    fun setToStatusBarHeight(view: View) {
        val params = view.layoutParams
        params.height = statusBarHeight
        view.layoutParams = params
    }

    /**
     * 取过渡色
     *
     * @param offset 取值范围：0 ~ 1
     */
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
    fun createColorStateList(normal: Int, pressed: Int, selected: Int, disabled: Int): ColorStateList {
        //normal一定要最后
        val states = arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled), intArrayOf(android.R.attr.state_selected, android.R.attr.state_enabled), intArrayOf())
        return ColorStateList(states, intArrayOf(disabled, pressed, selected, normal))
    }
}
