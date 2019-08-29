package cn.wandersnail.commons.base.callback;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

import cn.wandersnail.commons.base.interfaces.Callback;

/**
 * 弱引用回调
 * <p>
 * date: 2019/8/6 10:04
 * author: zengfansheng
 */
public abstract class WeakCallback<R, T> implements Callback<T> {
    private WeakReference<R> weakRef;

    public WeakCallback(@NonNull R referent) {
        weakRef = new WeakReference<>(referent);
    }

    public void onCallback(T obj) {
        R referent = weakRef.get();
        if (referent != null) {
            onFinalCallback(referent, obj);
        }
    }

    /**
     * 弱引用里的对象不为空时回调
     *
     * @param referent 弱引用持有的对象
     * @param obj      回调的数据
     */
    protected abstract void onFinalCallback(@NonNull R referent, T obj);
}
