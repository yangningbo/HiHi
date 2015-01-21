package com.gaopai.guiren.support.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class InterceptTouchViewPager extends ViewPager {

	public InterceptTouchViewPager(Context context) {
		super(context);
	}

	public InterceptTouchViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Considering a situation, ViewPager2 is embed in ViewPager1, we use this
	 * method in ViewPager1 to make ViewPager2 unscrollable.
	 */
@Override
protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
	return false;
}
}
