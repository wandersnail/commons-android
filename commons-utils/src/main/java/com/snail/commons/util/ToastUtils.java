package com.snail.commons.util;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.snail.commons.AppHolder;

import java.lang.ref.WeakReference;

/**
 * 单例Toast工具类，依赖AppHolder，需要在AppHolder初始化后方可使用
 * <p>
 * date: 2019/8/6 20:19
 * author: zengfansheng
 */
public final class ToastUtils {
    private static WeakReference<View> weakRef;
    private static Toast toast = Toast.makeText(AppHolder.getInstance().getContext(), "", Toast.LENGTH_SHORT);
    private static final Handler handler = new Handler(AppHolder.getInstance().getMainLooper());

    public static void reset() {
        weakRef = null;
        toast.cancel();
        toast = Toast.makeText(AppHolder.getInstance().getContext(), "", Toast.LENGTH_SHORT);
    }
    
    public static void cancel() {
        toast.cancel();
    }

    /**
     * Set the margins of the view.
     *
     * @param horizontalMargin The horizontal margin, in percentage of the
     *                         container width, between the container's edges and the
     *                         notification
     * @param verticalMargin   The vertical margin, in percentage of the
     *                         container height, between the container's edges and the
     *                         notification
     */
    public static void setMargin(float horizontalMargin, float verticalMargin) {
        toast.setMargin(horizontalMargin, verticalMargin);
    }

    /**
     * Set the location at which the notification should appear on the screen.
     *
     * @see android.view.Gravity
     * @see Toast#getGravity
     */
    public static void setGravity(int gravity, int xOffset, int yOffset) {
        toast.setGravity(gravity, xOffset, yOffset);
    }

    /**
     * Set the view to show.
     */
    public static void setView(@NonNull View view) {
        weakRef = new WeakReference<>(view);
        toast.setView(view);
    }

    private static void show(CharSequence text, int duration) {
        ToastUtils.toast.cancel();
        Toast toast = Toast.makeText(AppHolder.getInstance().getContext(), "", Toast.LENGTH_SHORT);
        toast.setDuration(duration);
        toast.setText(text);
        if (weakRef != null && weakRef.get() != null) {
            toast.setView(weakRef.get());
        }
        toast.setGravity(ToastUtils.toast.getGravity(), ToastUtils.toast.getXOffset(), ToastUtils.toast.getYOffset());
        toast.setMargin(ToastUtils.toast.getHorizontalMargin(), ToastUtils.toast.getVerticalMargin());
        toast.show();
        ToastUtils.toast = toast;
    }

    public static void showShort() {
        postToMainThread(new Runnable() {
            @Override
            public void run() {
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
    
    public static void showShort(@NonNull final CharSequence text) {
        postToMainThread(new Runnable() {
            @Override
            public void run() {
                show(text, Toast.LENGTH_SHORT);
            }
        });
    }

    public static void showShort(@StringRes final int resId) {
        postToMainThread(new Runnable() {
            @Override
            public void run() {
                show(AppHolder.getInstance().getContext().getText(resId), Toast.LENGTH_SHORT);
            }
        });
    }

    public static void showLong() {
        postToMainThread(new Runnable() {
            @Override
            public void run() {
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
    
    public static void showLong(@NonNull final CharSequence text) {
        postToMainThread(new Runnable() {
            @Override
            public void run() {
                show(text, Toast.LENGTH_LONG);
            }
        });
    }

    public static void showLong(@StringRes final int resId) {
        postToMainThread(new Runnable() {
            @Override
            public void run() {
                show(AppHolder.getInstance().getContext().getText(resId), Toast.LENGTH_LONG);
            }
        });
    }

    private static void postToMainThread(Runnable runnable) {
        if (Looper.myLooper() == AppHolder.getInstance().getMainLooper()) {
            runnable.run();
        } else {
            handler.post(runnable);
        }
    }
}
