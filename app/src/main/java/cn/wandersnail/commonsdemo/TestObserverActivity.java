package cn.wandersnail.commonsdemo;

import android.os.Bundle;

import org.jetbrains.annotations.Nullable;

import androidx.annotation.NonNull;
import cn.wandersnail.commons.observer.Observable;
import cn.wandersnail.commons.poster.MethodInfo;

/**
 * date: 2019/9/2 16:04
 * author: zengfansheng
 */
public class TestObserverActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_observer);
        findViewById(R.id.btnPublish).setOnClickListener(v -> {
            MethodInfo info = new MethodInfo("test",
                    new MethodInfo.Parameter(int.class, 1),
                    new MethodInfo.Parameter(float.class, 2f),
                    new MethodInfo.Parameter(double.class, 2d),
                    new MethodInfo.Parameter(byte.class, (byte) 2),
                    new MethodInfo.Parameter(boolean.class, false),
                    new MethodInfo.Parameter(char.class, 'c'),
                    new MethodInfo.Parameter(long.class, 2L),
                    new MethodInfo.Parameter(short.class, (short) 2));
            Observable observable = App.Companion.getInstance().getObservable();
            observable.notifyObservers(info);
        });
    }

    private MethodInfo.Parameter paramPrimary(@NonNull Object value) {
        if (Integer.class.isAssignableFrom(value.getClass())) {
            return new MethodInfo.Parameter(int.class, value);
        } else if (Long.class.isAssignableFrom(value.getClass())) {
            return new MethodInfo.Parameter(long.class, value);
        }else if (Character.class.isAssignableFrom(value.getClass())) {
            return new MethodInfo.Parameter(char.class, value);
        } else if (Byte.class.isAssignableFrom(value.getClass())) {
            return new MethodInfo.Parameter(byte.class, value);
        } else if (Short.class.isAssignableFrom(value.getClass())) {
            return new MethodInfo.Parameter(short.class, value);
        } else if (Float.class.isAssignableFrom(value.getClass())) {
            return new MethodInfo.Parameter(float.class, value);
        } else if (Double.class.isAssignableFrom(value.getClass())) {
            return new MethodInfo.Parameter(double.class, value);
        } else if (Boolean.class.isAssignableFrom(value.getClass())) {
            return new MethodInfo.Parameter(boolean.class, value);
        }
        return new MethodInfo.Parameter(value.getClass(), value);
    }
}
