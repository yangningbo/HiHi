package com.gaopai.guiren.support;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View.OnKeyListener;

import com.gaopai.guiren.activity.share.ShareFollowersFragment;

public class FragmentHelper {

	public static Fragment replaceFragment(int holder, FragmentManager fragmentManager, Class clazz) {
		Fragment fragment = null;
		try {
			fragment = fragmentManager.findFragmentByTag(clazz.getName());
			if (fragment == null) {
				fragment = (Fragment) clazz.newInstance();
			}
			fragmentManager.beginTransaction().replace(holder, fragment, clazz.getName()).addToBackStack(null).commit();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return fragment;
	}

	public static void registerBackListener(Fragment fragment, OnKeyListener onBackKeyListener) {
		fragment.getView().setFocusableInTouchMode(true);
		fragment.getView().setOnKeyListener(onBackKeyListener);
	}
}
