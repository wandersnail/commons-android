package com.snail.commons.helper;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import com.snail.commons.interfaces.DrawableBuilder;

/**
 * date: 2019/8/6 16:24
 * author: zengfansheng
 */
public class GradientDrawableBuilder extends RoundConfig implements DrawableBuilder {
    private GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TOP_BOTTOM;
    private int[] normal = new int[] {Color.LTGRAY};
    private int[] pressed;
    private int[] selected;
    private int[] disabled;
    private int[] checked;

    /**
     * 设置渐变方向
     */
    public void setOrientation(GradientDrawable.Orientation orientation) {
        this.orientation = orientation;
    }

    public void setNormalColors(@ColorInt int[] colors) {
        normal = colors;
    }

    public void setPressedColors(@ColorInt int[] colors) {
        pressed = colors;
    }

    public void setDisabledColors(@ColorInt int[] colors) {
        disabled = colors;
    }

    public void setSelectedColors(@ColorInt int[] colors) {
        selected = colors;
    }

    public void setCheckedColors(@ColorInt int[] colors) {
        checked = colors;
    }

    @NonNull
    @Override
    public Drawable build() {
        StateListDrawable drawable = new StateListDrawable();
        if (disabled != null) {
            drawable.addState(new int[]{-android.R.attr.state_enabled}, createDrawable(disabled));
        }
        if (checked != null) {
            drawable.addState(new int[]{android.R.attr.state_checked}, createDrawable(checked));
        }
        if (selected != null) {
            drawable.addState(new int[]{android.R.attr.state_selected}, createDrawable(selected));
        }
        if (pressed != null) {
            drawable.addState(new int[]{android.R.attr.state_pressed}, createDrawable(pressed));
        }
        drawable.addState(new int[0], createDrawable(normal)); //normal一定要最后
        return drawable;
    }

    private Drawable createDrawable(int[] colors) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setOrientation(orientation);
        drawable.setCornerRadii(getCornerRadii());
        drawable.setColors(colors);
        return drawable;
    }
}
