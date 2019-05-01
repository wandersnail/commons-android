package com.snail.commons.helper

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import androidx.annotation.ColorInt
import com.snail.commons.entity.RoundConfig
import com.snail.commons.interfaces.DrawableBuilder

/**
 * 纯色的
 *
 * date: 2019/4/12 17:22
 * author: zengfansheng
 */
class SolidDrawableBuilder : DrawableBuilder, RoundConfig() {
    private var normalFillColor = Color.LTGRAY
    private var normalStrokeColor = Color.TRANSPARENT
    private var normalStrokeWidth = 0
    private var pressedFillColor: Int? = null
    private var pressedStrokeColor = Color.TRANSPARENT
    private var pressedStrokeWidth = 0
    private var selectedFillColor: Int? = null
    private var selectedStrokeColor = Color.TRANSPARENT
    private var selectedStrokeWidth = 0
    private var disabledFillColor: Int? = null
    private var disabledStrokeColor = Color.TRANSPARENT
    private var disabledStrokeWidth = 0
    private var checkedFillColor: Int? = null
    private var checkedStrokeColor = Color.TRANSPARENT
    private var checkedStrokeWidth = 0

    @JvmOverloads
    fun setNormalColor(@ColorInt fillColor: Int, strokeWidth: Int = 0, @ColorInt strokeColor: Int = Color.TRANSPARENT) {
        normalFillColor = fillColor
        normalStrokeColor = strokeColor
        normalStrokeWidth = strokeWidth
    }

    @JvmOverloads
    fun setPressedColor(@ColorInt fillColor: Int, strokeWidth: Int = 0, @ColorInt strokeColor: Int = Color.TRANSPARENT) {
        pressedFillColor = fillColor
        pressedStrokeColor = strokeColor
        pressedStrokeWidth = strokeWidth
    }

    @JvmOverloads
    fun setDisabledColor(@ColorInt fillColor: Int, strokeWidth: Int = 0, @ColorInt strokeColor: Int = Color.TRANSPARENT) {
        disabledFillColor = fillColor
        disabledStrokeColor = strokeColor
        disabledStrokeWidth = strokeWidth
    }

    @JvmOverloads
    fun setSelectedColor(@ColorInt fillColor: Int, strokeWidth: Int = 0, @ColorInt strokeColor: Int = Color.TRANSPARENT) {
        selectedFillColor = fillColor
        selectedStrokeColor = strokeColor
        selectedStrokeWidth = strokeWidth
    }

    @JvmOverloads
    fun setCheckedColor(@ColorInt fillColor: Int, strokeWidth: Int = 0, @ColorInt strokeColor: Int = Color.TRANSPARENT) {
        checkedFillColor = fillColor
        checkedStrokeColor = strokeColor
        checkedStrokeWidth = strokeWidth
    }

    override fun build(): Drawable {
        val drawable = StateListDrawable()
        if (disabledFillColor != null) {
            drawable.addState(intArrayOf(-android.R.attr.state_enabled), createDrawable(disabledFillColor!!, disabledStrokeWidth, disabledStrokeColor))
        }
        if (checkedFillColor != null) {
            drawable.addState(intArrayOf(android.R.attr.state_checked), createDrawable(checkedFillColor!!, checkedStrokeWidth, checkedStrokeColor))
        }
        if (selectedFillColor != null) {
            drawable.addState(intArrayOf(android.R.attr.state_selected), createDrawable(selectedFillColor!!, selectedStrokeWidth, selectedStrokeColor))
        }
        if (pressedFillColor != null) {
            drawable.addState(intArrayOf(android.R.attr.state_pressed), createDrawable(pressedFillColor!!, pressedStrokeWidth, pressedStrokeColor))
        }
        drawable.addState(intArrayOf(), createDrawable(normalFillColor, normalStrokeWidth, normalStrokeColor)) //normal一定要最后
        return drawable
    }

    private fun createDrawable(fillColor: Int, strokeWidth: Int, strokeColor: Int): Drawable {
        val drawable = GradientDrawable()
        drawable.cornerRadii = getCornerRadii()
        drawable.setColor(fillColor)
        drawable.setStroke(strokeWidth, strokeColor)
        return drawable
    }
}