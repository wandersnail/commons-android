package cn.zfs.commonsdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

/**
 * 描述: 下拉刷新，控制在顶部才拦截触摸事件
 * 时间: 2018/6/8 09:20
 * 作者: zengfansheng
 */
public class PullRefreshLayout extends SwipeRefreshLayout {
    private AdapterView lv;
    
    public PullRefreshLayout(@NonNull Context context) {
        super(context);
    }

    public PullRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            View view = getChildAt(0);
            if (view instanceof AdapterView) {
                lv = (AdapterView) view;
            }
        }
    }

    private boolean isListViewNotAtTop() {
        if (lv != null) {
            View child = lv.getChildAt(0);
            return child != null && (lv.getFirstVisiblePosition() != 0 || child.getTop() != 0);
        }
        return true;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isListViewNotAtTop() && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isListViewNotAtTop() && super.onInterceptTouchEvent(ev);
    }
}
