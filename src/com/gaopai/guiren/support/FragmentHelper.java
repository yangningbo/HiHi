package com.gaopai.guiren.support;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.gaopai.guiren.activity.share.ShareFollowersFragment;

public class FragmentHelper {
	ShareFollowersFragment shareFollowersFragment = new ShareFollowersFragment();

	public static void replaceFragment(int holder, FragmentManager fragmentManager, Class clazz) {
		Fragment fragment;
		try {
			fragment = fragmentManager.findFragmentByTag(clazz.getName());
			if (fragment == null) {
				fragment = (Fragment) clazz.newInstance();
			}
			fragmentManager.beginTransaction().replace(holder, fragment, clazz.getName())
					.addToBackStack(null).commit();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
