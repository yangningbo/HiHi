package com.gaopai.guiren.support.view;

import com.gaopai.guiren.utils.Logger;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class DynamicLayout extends RelativeLayout {

	public DynamicLayout(Context context) {
		super(context);
	}

	public DynamicLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DynamicLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		Logger.startCountTime();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		Logger.time(this, "onMeasure");
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		Logger.startCountTime();
		super.onLayout(changed, l, t, r, b);
//		Logger.time(this, "onLayout");
	}

}
