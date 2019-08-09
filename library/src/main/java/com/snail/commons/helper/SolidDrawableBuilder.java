package com.snail.commons.helper;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import com.snail.commons.interfaces.DrawableBuilder;

/**
 * date: 2019/8/6 16:35
 * author: zengfansheng
 */
public class SolidDrawableBuilder extends RoundConfig implements DrawableBuilder {
    private int normalFillColor = Color.LTGRAY;
    private int normalStrokeColor = Color.TRANSPARENT;
    private int normalStrokeWidth;
    private Integer pressedFillColor;
    private int pressedStrokeColor = Color.TRANSPARENT;
    private int pressedStrokeWidth;
    private Integer selectedFillColor;
    private int selectedStrokeColor = Color.TRANSPARENT;
    private int selectedStrokeWidth;
    private Integer disabledFillColor;
    private int disabledStrokeColor = Color.TRANSPARENT;
    private int disabledStrokeWidth;
    private Integer checkedFillColor;
    private int checkedStrokeColor = Color.TRANSPARENT;
    private int checkedStrokeWidth;

    public void setNormalColor(@ColorInt int fillColor) {
        normalFillColor = fillColor;
    }

    public void setNormalColor(@ColorInt int fillColor, int strokeWidth, @ColorInt int strokeColor) {
        normalFillColor = fillColor;
        normalStrokeColor = strokeColor;
        normalStrokeWidth = strokeWidth;
    }

    public void setPressedColor(@ColorInt int fillColor) {
        pressedFillColor = fillColor;
    }
    
    public void setPressedColor(@ColorInt int fillColor, int strokeWidth, @ColorInt int strokeColor) {
        pressedFillColor = fillColor;
        pressedStrokeColor = strokeColor;
        pressedStrokeWidth = strokeWidth;
    }

    public void setDisabledColor(@ColorInt int fillColor) {
        disabledFillColor = fillColor;
    }
    
    public void setDisabledColor(@ColorInt int fillColor, int strokeWidth, @ColorInt int strokeColor) {
        disabledFillColor = fillColor;
        disabledStrokeColor = strokeColor;
        disabledStrokeWidth = strokeWidth;
    }

    public void setSelectedColor(@ColorInt int fillColor) {
        selectedFillColor = fillColor;
    }
    
    public void setSelectedColor(@ColorInt int fillColor, int strokeWidth, @ColorInt int strokeColor) {
        selectedFillColor = fillColor;
        selectedStrokeColor = strokeColor;
        selectedStrokeWidth = strokeWidth;
    }

    public void setCheckedColor(@ColorInt int fillColor) {
        checkedFillColor = fillColor;
    }
    
    public void setCheckedColor(@ColorInt int fillColor, int strokeWidth, @ColorInt int strokeColor) {
        checkedFillColor = fillColor;
        checkedStrokeColor = strokeColor;
        checkedStrokeWidth = strokeWidth;
    }    
    
    @NonNull
    @Override
    public Drawable build() {
        StateListDrawable drawable = new StateListDrawable();
        if (disabledFillColor != null) {
            Drawable disableDrawable = createDrawable(disabledFillColor, disabledStrokeWidth, disabledStrokeColor);
            drawable.addState(new int[]{-android.R.attr.state_enabled}, disableDrawable);
        }
        if (checkedFillColor != null) {
            Drawable checkedDrawable = createDrawable(checkedFillColor, checkedStrokeWidth, checkedStrokeColor);
            drawable.addState(new int[]{android.R.attr.state_checked}, checkedDrawable);
        }
        if (selectedFillColor != null) {
            Drawable selectedDrawable = createDrawable(selectedFillColor, selectedStrokeWidth, selectedStrokeColor);
            drawable.addState(new int[]{android.R.attr.state_selected}, selectedDrawable);
        }
        if (pressedFillColor != null) {
            Drawable pressedDrawable = createDrawable(pressedFillColor, pressedStrokeWidth, pressedStrokeColor);
            drawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
        }
        //normal一定要最后
        drawable.addState(new int[0], createDrawable(normalFillColor, normalStrokeWidth, normalStrokeColor)); 
        return drawable;
    }

    private Drawable createDrawable(int fillColor, int strokeWidth, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadii(getCornerRadii());
        drawable.setColor(fillColor);
        drawable.setStroke(strokeWidth, strokeColor);
        return drawable;
    }
}
