package com.zfs.commons.base;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.List;

/**
 * 描述: 基本ViewPager的基类
 * 时间: 2018/8/26 22:57
 * 作者: zengfansheng
 */
public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    
    public BaseFragmentPagerAdapter(FragmentManager fm, @NonNull List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
