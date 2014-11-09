package com.gaopai.guiren.view;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.utils.MyUtils;
import com.google.gson.annotations.Until;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ChatGridLayout extends ViewGroup {

	public ChatGridLayout(Context context) {
		super(context);

	}

	public ChatGridLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ChatGridLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private EditText mEditText;

	public void setEditText(EditText paramEditText) {
		this.mEditText = paramEditText;
	}

	public void show(Activity paramActivity) {
		hideSoftInput(this.mEditText);
		setVisibility(View.VISIBLE);
	}

	public void hideSoftInput(View paramEditText) {
		((InputMethodManager) DamiApp.getInstance().getSystemService(
				"input_method")).hideSoftInputFromWindow(
				paramEditText.getWindowToken(), 0);
	}

	public void hide(Activity paramActivity) {
		setVisibility(View.GONE);
		paramActivity.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int childCount = getChildCount();
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
//		Log.d("onMeasure", "l="+height + "  w="+width);
		int childWidthSpec = MeasureSpec.makeMeasureSpec(width / 3,
				MeasureSpec.EXACTLY);
		int childHeightSpec = MeasureSpec.makeMeasureSpec(MyUtils.dip2px(getContext(), 80),
				MeasureSpec.EXACTLY);
		
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.measure(childWidthSpec, childHeightSpec);
		}
//		Log.d("onMeasure", "cl = " + getChildAt(0).getMeasuredHeight() + "   " 
//				+ " cw =" + getChildAt(0).getMeasuredWidth());
		height = ((childCount-1)/3 + 1) * getChildAt(0).getMeasuredHeight();
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int childCount = getChildCount();
		int row = 0;
		int childWidth;
		int childHeight;
//		Log.d("onLayout", "l="+l + "  t="+t+"   r="+r  + "   b="+b);

		int column = 0;
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			childHeight = child.getMeasuredHeight();
			childWidth = child.getMeasuredWidth();
			row = i/3;
			column = i % 3;
			child.layout(column * childWidth, row * childHeight, (column + 1)
					* childWidth, (row + 1) * childHeight);
		}

	}

}
