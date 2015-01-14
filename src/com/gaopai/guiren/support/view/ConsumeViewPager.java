package com.gaopai.guiren.support.view;

import com.gaopai.guiren.utils.Logger;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ConsumeViewPager extends ViewPager {

	public ConsumeViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ConsumeViewPager(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		Logger.d(this, "itercept = " + super.onInterceptTouchEvent(arg0));
		return super.onInterceptTouchEvent(arg0);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Logger.d(this, "dispatchTouchEvent = " + super.dispatchTouchEvent(ev));
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		Logger.d(this, "onTouchEvent = " + super.onTouchEvent(arg0));
		return super.onTouchEvent(arg0);
	}

}
