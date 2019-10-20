package cn.wandersnail.commons.util;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.lang.ref.WeakReference;

import cn.wandersnail.commons.base.AppHolder;

/**
 * 单例Toast工具类
 * <p>
 * date: 2019/8/6 20:19
 * author: zengfansheng
 */
public final class ToastUtils {
    private static WeakReference<View> weakRef;
    private static Toast toast;
    private static Handler handler;
    private static Looper mainLooper;
    
    /**
     * 只适用于依赖了cn.wandersnail:common-base时
     */
    public static void reset() {
        postToMainThread(() -> {
            weakRef = null;
            toast.cancel();
            toast = Toast.makeText(AppHolder.getInstance().getContext(), "", Toast.LENGTH_SHORT);
        });
    }
        
    public static void cancel() {
        postToMainThread(() -> toast.cancel());
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
    public static void setMargin(final float horizontalMargin, final float verticalMargin) {
        postToMainThread(() -> toast.setMargin(horizontalMargin, verticalMargin));
    }

    /**
     * Set the location at which the notification should appear on the screen.
     *
     * @see android.view.Gravity
     * @see Toast#getGravity
     */
    public static void setGravity(final int gravity, final int xOffset, final int yOffset) {
        postToMainThread(() -> toast.setGravity(gravity, xOffset, yOffset));
    }

    /**
     * Set the view to show.
     */
    public static void setView(@NonNull final View view) {
        postToMainThread(() -> {
            weakRef = new WeakReference<>(view);
            toast.setView(view);
        });
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
        postToMainThread(() -> {
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        });
    }
    
    public static void showShort(@NonNull final CharSequence text) {
        postToMainThread(() -> show(text, Toast.LENGTH_SHORT));
    }

    public static void showShort(@StringRes final int resId) {
        postToMainThread(() -> show(AppHolder.getInstance().getContext().getText(resId), Toast.LENGTH_SHORT));
    }

    public static void showLong() {
        postToMainThread(() -> {
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
        });
    }
    
    public static void showLong(@NonNull final CharSequence text) {
        postToMainThread(() -> show(text, Toast.LENGTH_LONG));
    }

    public static void showLong(@StringRes final int resId) {
        postToMainThread(() -> show(AppHolder.getInstance().getContext().getText(resId), Toast.LENGTH_LONG));
    }

    private static void postToMainThread(final Runnable runnable) {
        if (mainLooper == null) {
            mainLooper = Looper.getMainLooper();
        }
        if (toast == null) {
            handler = new Handler(mainLooper);
            handler.post(() -> {
                toast = Toast.makeText(AppHolder.getInstance().getContext(), "", Toast.LENGTH_SHORT);
                runnable.run();
            });
        } else if (Looper.myLooper() == mainLooper) {
            runnable.run();
        } else {
            handler.post(runnable);
        }
    }
}
