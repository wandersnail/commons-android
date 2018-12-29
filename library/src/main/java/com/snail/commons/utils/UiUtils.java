package com.snail.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.snail.commons.AppHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Zeng on 2015/7/13.
 * 其中很多方法都依赖于自定义Application类
 */
public class UiUtils {
    public static final String ANIM = "anim";
    public static final String ARRAY = "array";
    public static final String ATTR = "attr";
    public static final String BOOL = "bool";
    public static final String COLOR = "color";
    public static final String DIMEN = "dimen";
    public static final String DRAWABLE = "drawable";
    public static final String ID = "id";
    public static final String INTEGER = "integer";
    public static final String LAYOUT = "layout";
    public static final String MIPMAP = "mipmap";
    public static final String STRING = "string";
    public static final String STYLE = "style";

    @StringDef({ANIM, ARRAY, ATTR, BOOL, COLOR, DIMEN, DRAWABLE, ID, INTEGER, LAYOUT, MIPMAP, STRING, STYLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ResType {}

    public static int getResId(@NonNull Context context, @ResType String resType, String name) {
        return context.getResources().getIdentifier(name, resType, context.getPackageName());
    }
    
    /**
     * @return int[0]:宽度，int[1]:高度。
     */
    public static int[] getRealScreenResolution(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

    /**
     * 获取显示屏幕宽度，不包含状态栏和导航栏
     */
    public static int getDisplayScreenWidth() {
        return AppHolder.getContext().getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取显示屏幕高度
     */
    public static int getDisplayScreenHeight() {
        return AppHolder.getContext().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dp2px(float dpValue) {
        float scale = AppHolder.getContext().getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float px2dp(float pxValue) {
        float scale = AppHolder.getContext().getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    /**
     * 判断当前是否是主线程
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * Color转换为字符串
     */
    public static String toHexWithoutAlpha(int color) {
        StringBuilder sb = new StringBuilder(Integer.toHexString(color & 0x00FFFFFF));
        while (sb.length() < 6) {
            sb.insert(0, "0");
        }
        sb.insert(0, "#");
        return sb.toString();
    }

    /**
     * 将自己从容器中移除
     */
    public static void removeFromContainer(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = AppHolder.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = AppHolder.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取ActionBar的高度
     */
    public static float getActionBarSize(Context context) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        float height = ta.getDimension(0, 0);
        ta.recycle();
        return height;
    }

    public static void sendBroadcast(Intent intent) {
        AppHolder.getContext().sendBroadcast(intent);
    }

    /**
     * 设置TextView的字体，字体为外部文件，目录在assets
     *
     * @param context  上下文
     * @param root     根布局
     * @param fontName 字体名
     */
    public static void setFont(Context context, View root, String fontName) {
        try {
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    setFont(context, viewGroup.getChildAt(i), fontName);
                }
            } else if (root instanceof TextView) {
                ((TextView) root).setTypeface(Typeface.createFromAsset(context.getAssets(), fontName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置TextView的字体
     *
     * @param root 根布局
     * @param tf   字体
     */
    public static void setFont(View root, Typeface tf) {
        try {
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    setFont(viewGroup.getChildAt(i), tf);
                }
            } else if (root instanceof TextView) {
                ((TextView) root).setTypeface(tf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置布局中所有TextView的字体大小
     * @param root 根布局
     */
    public static void setTextSize(View root, int unit, float size) {
        if (root instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setTextSize(viewGroup.getChildAt(i), unit, size);
            }
        } else if (root instanceof TextView) {
            ((TextView) root).setTextSize(unit, size);
        }
    }

    /**
     * 设置布局中所有TextView的字体大小
     * @param root 根布局
     */
    public static void setTextColor(View root, int color) {
        if (root instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setTextColor(viewGroup.getChildAt(i), color);
            }
        } else if (root instanceof TextView) {
            ((TextView) root).setTextColor(color);
        }
    }

    /**
     * 获取字体
     *
     * @param path 字体在assets的路径
     */
    public static Typeface getTypefaceFromAsset(String path) {
        return Typeface.createFromAsset(AppHolder.getContext().getAssets(), path);
    }

    /**
     * 转黑白
     */
    public static void colourToMonochrome(ImageView iv) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        iv.setColorFilter(filter);
    }

    /**
     * 将View的高度设置成状态栏高
     */
    public static void setToStatusBarHeight(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = getStatusBarHeight();
        view.setLayoutParams(params);
    }

    /**
     * 取过渡色
     *
     * @param offset 取值范围：0 ~ 1
     */
    public static int getColor(int startColor, int endColor, float offset) {
        int Aa = startColor >> 24 & 0xff;
        int Ra = startColor >> 16 & 0xff;
        int Ga = startColor >> 8 & 0xff;
        int Ba = startColor & 0xff;
        int Ab = endColor >> 24 & 0xff;
        int Rb = endColor >> 16 & 0xff;
        int Gb = endColor >> 8 & 0xff;
        int Bb = endColor & 0xff;
        int a = (int) (Aa + (Ab - Aa) * offset);
        int r = (int) (Ra + (Rb - Ra) * offset);
        int g = (int) (Ga + (Gb - Ga) * offset);
        int b = (int) (Ba + (Bb - Ba) * offset);
        return Color.argb(a, r, g, b);
    }

    /**
     * @param normal  正常时的颜色
     * @param pressed 按压时的颜色
     * @param disabled 不可用时的颜色
     */
    public static ColorStateList createColorStateList(int normal, int pressed, int disabled) {
        //normal一定要最后
        int[][] states = new int[][]{
                {-android.R.attr.state_enabled},
                {android.R.attr.state_pressed, android.R.attr.state_enabled},
                {}
        };
        return new ColorStateList(states, new int[]{disabled, pressed, normal});
    }

    /**
     * @param normal  正常时的颜色
     * @param pressed 按压时的颜色
     * @param selected 选中时的颜色
     * @param disabled 不可用时的颜色
     */
    public static ColorStateList createColorStateList(int normal, int pressed, int selected, int disabled) {
        //normal一定要最后
        int[][] states = new int[][]{
                {-android.R.attr.state_enabled},
                {android.R.attr.state_pressed, android.R.attr.state_enabled},
                {android.R.attr.state_selected, android.R.attr.state_enabled},
                {}
        };
        return new ColorStateList(states, new int[]{disabled, pressed, selected, normal});
    }
}
