/*
 * 
 */
package com.gaopai.guiren.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class AbFragmentPagerAdapter.
 */
public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

	/** The m fragment list. */
	private ArrayList<Fragment> mFragmentList = null;

	/**
	 * Instantiates a new ab fragment pager adapter.
	 * 
	 * @param mFragmentManager
	 *            the m fragment manager
	 * @param fragmentList
	 *            the fragment list
	 */
	public CustomFragmentPagerAdapter(FragmentManager mFragmentManager,
			ArrayList<Fragment> fragmentList) {
		super(mFragmentManager);
		mFragmentList = fragmentList;
	}

	/**
	 * ��������ȡ����.
	 * 
	 * @return the count
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	/**
	 * ��������ȡ����λ�õ�Fragment.
	 * 
	 * @param position
	 *            the position
	 * @return the item
	 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int position) {

		Fragment fragment = null;
		if (position < mFragmentList.size()) {
			fragment = mFragmentList.get(position);
		} else {
			fragment = mFragmentList.get(0);
		}
		return fragment;

	}
}