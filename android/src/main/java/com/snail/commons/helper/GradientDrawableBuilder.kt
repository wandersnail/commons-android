package com.snail.commons.helper

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import androidx.annotation.ColorInt
import com.snail.commons.entity.RoundConfig
import com.snail.commons.interfaces.DrawableBuilder

/**
 *
 *
 * date: 2019/4/12 17:23
 * author: zengfansheng
 */
class GradientDrawableBuilder : DrawableBuilder, RoundConfig() {
    private var orientation = GradientDrawable.Orientation.TOP_BOTTOM
    private var normal = intArrayOf(Color.LTGRAY)
    private var pressed: IntArray? = null
    private var selected: IntArray? = null
    private var disabled: IntArray? = null
    private var checked: IntArray? = null

    /**
     * 设置渐变方向
     */
    fun setOrientation(orientation: GradientDrawable.Orientation) {
        this.orientation = orientation
    }

    fun setNormalColors(@ColorInt colors: IntArray) {
        normal = colors
    }

    fun setPressedColors(@ColorInt colors: IntArray) {
        pressed = colors
    }

    fun setDisabledColors(@ColorInt colors: IntArray) {
        disabled = colors
    }

    fun setSelectedColors(@ColorInt colors: IntArray) {
        selected = colors
    }

    fun setCheckedColors(@ColorInt colors: IntArray) {
        checked = colors
    }

    override fun build(): Drawable {
        val drawable = StateListDrawable()
        if (disabled != null) {
            drawable.addState(intArrayOf(-android.R.attr.state_enabled), createDrawable(disabled!!))
        }
        if (checked != null) {
            drawable.addState(intArrayOf(android.R.attr.state_checked), createDrawable(checked!!))
        }
        if (selected != null) {
            drawable.addState(intArrayOf(android.R.attr.state_selected), createDrawable(selected!!))
        }
        if (pressed != null) {
            drawable.addState(intArrayOf(android.R.attr.state_pressed), createDrawable(pressed!!))
        }
        drawable.addState(intArrayOf(), createDrawable(normal)) //normal一定要最后
        return drawable
    }

    private fun createDrawable(colors: IntArray): Drawable {
        val drawable = GradientDrawable()
        drawable.orientation = orientation
        drawable.cornerRadii = getCornerRadii()
        drawable.colors = colors
        return drawable
    }
}