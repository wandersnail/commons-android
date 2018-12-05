package com.zfs.commons.base;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by zengfs on 2016/2/21.
 * 基本ViewPager的基类
 */
public class BasePagerAdapter extends PagerAdapter {

	private List<? extends BasePager> pagers;

	public BasePagerAdapter(@NonNull List<? extends BasePager> pagers) {
		this.pagers = pagers;
	}

	public List<? extends BasePager> getPagers() {
		return pagers;
	}
	
	@Override
	public int getCount() {
		return pagers.size();
	}

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }
    
	@Override
	public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
		return view == o;
	}

	@Override
	public @NonNull Object instantiateItem(@NonNull ViewGroup container, int position) {
		View view = pagers.get(position).getContentView();
        container.addView(view);
		return view;
	}

	@Override
	public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
		container.removeView((View) object);
	}
}
