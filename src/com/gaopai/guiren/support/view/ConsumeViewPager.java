package com.gaopai.guiren.support.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

public class ConsumeViewPager extends ViewPager {

	public ConsumeViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ConsumeViewPager(Context context) {
		super(context);
	}

	// In Dynamic Fragment, always let emotion panel consume touch event.
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		final ViewParent parent = getParent();
		if (parent != null) {
			parent.requestDisallowInterceptTouchEvent(true);
		}
		return true;
	}
}
