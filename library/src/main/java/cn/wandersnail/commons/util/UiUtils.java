package cn.wandersnail.commons.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import cn.wandersnail.commons.base.AppHolder;

/**
 * date: 2019/8/7 23:27
 * author: zengfansheng
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

    /**
     * 获取显示屏幕宽度，不包含状态栏和导航栏
     */
    public static int getDisplayScreenWidth() {
        return AppHolder.getInstance().getContext().getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取显示屏幕高度，不包含状态栏和导航栏
     */
    public static int getDisplayScreenHeight() {
        return AppHolder.getInstance().getContext().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight() {
        int result = 0;
        Resources resources = AppHolder.getInstance().getContext().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取导航栏高度
     */
    public static int getNavigationBarHeight() {
        int result = 0;
        Resources resources = AppHolder.getInstance().getContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取资源ID
     *
     * @param context 上下文
     * @param resType 资源类型。{@link #STRING}, {@link #ANIM}...
     * @param name    资源名称
     */
    public static int getResId(@NonNull Context context, @NonNull String resType, @NonNull String name) {
        return context.getResources().getIdentifier(name, resType, context.getPackageName());
    }

    /**
     * @return int[0]:宽度，int[1]:高度。
     */
    public static int[] getRealScreenResolution(@NonNull Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dp2pxF(float value) {
        return value * AppHolder.getInstance().getContext().getResources().getDisplayMetrics().density + 0.5f;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float px2dpF(float value) {
        return value / AppHolder.getInstance().getContext().getResources().getDisplayMetrics().density + 0.5f;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float value) {
        return (int) (value * AppHolder.getInstance().getContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float value) {
        return (int) (value / AppHolder.getInstance().getContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 将自己从容器中移除
     */
    public static void removeFromContainer(@NonNull View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
    }

    /**
     * 获取ActionBar的高度
     */
    public static float getActionBarSize(@NonNull Context context) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        float height = ta.getDimension(0, 0f);
        ta.recycle();
        return height;
    }

    /**
     * 设置TextView的字体，字体为外部文件，目录在assets
     *
     * @param root     根布局
     * @param fontName 字体名
     */
    public static void setFont(@NonNull View root, @NonNull String fontName) {
        try {
            if (root instanceof ViewGroup) {
                ViewGroup view = (ViewGroup) root;
                for (int i = 0; i < view.getChildCount(); i++) {
                    setFont(view.getChildAt(i), fontName);
                }
            } else if (root instanceof TextView) {
                ((TextView) root).setTypeface(Typeface.createFromAsset(root.getContext().getAssets(), fontName));
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * 设置TextView的字体
     *
     * @param root 根布局
     * @param tf   字体
     */
    public static void setFont(@NonNull View root, @NonNull Typeface tf) {
        try {
            if (root instanceof ViewGroup) {
                ViewGroup view = (ViewGroup) root;
                for (int i = 0; i < view.getChildCount(); i++) {
                    setFont(view.getChildAt(i), tf);
                }
            } else if (root instanceof TextView) {
                ((TextView) root).setTypeface(tf);
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * 设置布局中所有TextView的字体大小
     *
     * @param root 根布局
     */
    public static void setTextSize(@NonNull View root, int unit, float size) {
        if (root instanceof ViewGroup) {
            ViewGroup view = (ViewGroup) root;
            for (int i = 0; i < view.getChildCount(); i++) {
                setTextSize(view.getChildAt(i), unit, size);
            }
        } else if (root instanceof TextView) {
            ((TextView) root).setTextSize(unit, size);
        }
    }

    /**
     * 设置布局中所有TextView的字体大小
     *
     * @param root 根布局
     */
    public static void setTextColor(@NonNull View root, int color) {
        if (root instanceof ViewGroup) {
            ViewGroup view = (ViewGroup) root;
            for (int i = 0; i < view.getChildCount(); i++) {
                setTextColor(view.getChildAt(i), color);
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
    public static Typeface getTypefaceFromAsset(@NonNull String path) {
        return Typeface.createFromAsset(AppHolder.getInstance().getContext().getAssets(), path);
    }

    /**
     * 将View的高度设置成状态栏高
     */
    public static void setToStatusBarHeight(@NonNull View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = getStatusBarHeight();
        view.setLayoutParams(params);
    }
}
