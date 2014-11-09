package com.gaopai.guiren.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.MyUtils;

public class MyGridLayout extends ViewGroup {

	private int mColumns;
	private boolean isSquare = false;

	public MyGridLayout(Context context) {
		this(context, null);

	}

	public MyGridLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyGridLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyGridLayout, defStyle, 0);
		mColumns = typedArray.getInt(R.styleable.MyGridLayout_columns, 3);
		isSquare = typedArray.getBoolean(R.styleable.MyGridLayout_is_square, false);
		typedArray.recycle();
	}

	private boolean isMeasure = false;
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int childCount = getChildCount();
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		int childWidthSpec = MeasureSpec.makeMeasureSpec((width - getPaddingLeft() - getPaddingRight()) / mColumns,
				MeasureSpec.EXACTLY);
		int childHeightSpec;
		if (isSquare) {
			childHeightSpec = childWidthSpec;
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.AT_MOST);
		}
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.measure(childWidthSpec, childHeightSpec);
		}
		if (childCount > 0) {
			height = ((childCount - 1) / mColumns + 1) * getChildAt(0).getMeasuredHeight();
		}
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height + getPaddingBottom() + getPaddingTop(),
				MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int childCount = getChildCount();
		int row = 0;
		int childWidth;
		int childHeight;

		int column = 0;
		int paddingLeft = getPaddingLeft();
		int paddingTop = getPaddingTop();
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			childHeight = child.getMeasuredHeight();
			childWidth = child.getMeasuredWidth();
			row = i / mColumns;
			column = i % mColumns;
			child.layout(column * childWidth + paddingLeft, row * childHeight + paddingTop, (column + 1) * childWidth
					+ paddingLeft, (row + 1) * childHeight + paddingTop);
		}

	}

	private OnClickListener mItemListener;

	public void setOnItemClickListener(OnClickListener clickListener) {
		mItemListener = clickListener;
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			v.setOnClickListener(clickListener);
		}
	}

	@Override
	public void addView(View child) {
		// TODO Auto-generated method stub
		super.addView(child);
		child.setOnClickListener(mItemListener);
	}

}