package com.gaopai.guiren.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyUtils;

public class MyGridLayout extends ViewGroup {

	private int mColumns;
	private boolean isSquare = false;
	/**
	 * make sure child views has equal gaps between each other.
	 */
	private int gridGap;

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
		gridGap = typedArray.getDimensionPixelSize(R.styleable.MyGridLayout_grid_gap, 0);
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

		int childWidthSpec = MeasureSpec.makeMeasureSpec((width - getPaddingLeft() - getPaddingRight() - mColumns
				* gridGap)
				/ mColumns, MeasureSpec.EXACTLY);

		int childHeightSpec;
		if (isSquare) {
			childHeightSpec = childWidthSpec;
		} else {
			childHeightSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingBottom() + getPaddingTop(),
					LayoutParams.WRAP_CONTENT);
		}
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.measure(childWidthSpec, childHeightSpec);
		}

		int rows = (childCount - 1) / mColumns + 1;
		if (childCount > 0) {
			height = rows * getChildAt(0).getMeasuredHeight();
		}

		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height + getPaddingBottom() + getPaddingTop() + (rows - 1)
				* gridGap, MeasureSpec.EXACTLY);
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
			child.layout(column * childWidth + paddingLeft + (column - 1) * gridGap, row * childHeight + paddingTop
					+ (row - 1) * gridGap, (column + 1) * childWidth + paddingLeft + (column - 1) * gridGap, (row + 1)
					* childHeight + (row - 1) * gridGap + paddingTop);
		}

	}

	@Override
	public void addView(View child) {
		super.addView(child);
	}

}