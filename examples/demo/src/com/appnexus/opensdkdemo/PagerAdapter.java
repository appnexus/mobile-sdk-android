package com.appnexus.opensdkdemo;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
	
	private List<Fragment> fragments;

	public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments=fragments;
	}

	@Override
	public Fragment getItem(int index) {
		if(fragments.size()>index) return fragments.get(index);
		return null;
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}

}
