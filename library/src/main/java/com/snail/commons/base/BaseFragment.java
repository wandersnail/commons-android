package com.snail.commons.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snail.commons.utils.UiUtils;

/**
 * Created by zengfs on 2016/1/23.
 * fragment基类
 */
public abstract class BaseFragment extends Fragment {
	protected View rootView;
	protected boolean isOnViewCreated;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) 
            rootView = inflater.inflate(getLayoutId(), container, false);
        UiUtils.removeFromContainer(rootView);
        return rootView;
    }
	
	@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        isOnViewCreated = true;
    }

    protected abstract int getLayoutId();
}
