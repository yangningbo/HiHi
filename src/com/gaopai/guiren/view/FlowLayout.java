package com.gaopai.guiren.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;

import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.MyUtils;

public class FlowLayout extends ViewGroup {
	protected MarginLayoutParams textLayoutParams;
	protected Context mContext;
	private int mColumns = 0;

	public FlowLayout(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;
		textLayoutParams = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textLayoutParams.leftMargin = MyUtils.dip2px(context, 2);
		textLayoutParams.rightMargin = MyUtils.dip2px(context, 2);
		textLayoutParams.topMargin = MyUtils.dip2px(context, 2);
		textLayoutParams.bottomMargin = MyUtils.dip2px(context, 2);
		TypedArray typedArray = context.getTheme()
				.obtainStyledAttributes(attrs, R.styleable.TasFlowLayout, defStyle, 0);
		mColumns = typedArray.getInt(R.styleable.TasFlowLayout_tag_columns, 0);
		typedArray.recycle();
	}

	public MarginLayoutParams getTextLayoutParams() {
		return textLayoutParams;
	}

	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

		int cCount = getChildCount();
		if (mColumns != 0 && cCount > 0) {
			MarginLayoutParams lp = (MarginLayoutParams) getChildAt(0).getLayoutParams();
			int childWidth = (sizeWidth - getPaddingLeft() - getPaddingRight()) / mColumns - lp.leftMargin
					- lp.rightMargin;
			int childHeight = MyUtils.dip2px(mContext, 25);
			int childWidthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
			int childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);

			for (int i = 0; i < cCount; i++) {
				View child = getChildAt(i);
				child.measure(childWidthSpec, childHeightSpec);
			}
			int parentHeigth = getPaddingTop() + getPaddingBottom() + ((cCount - 1) / mColumns + 1)
					* (childHeight + lp.topMargin + lp.bottomMargin);
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(parentHeigth, MeasureSpec.EXACTLY);
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}
		int width = 0;
		int height = 0;
		int lineWidth = getPaddingLeft() + getPaddingRight();
		int lineHeight = getPaddingTop();

		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
			int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
			int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

			if (lineWidth + childWidth > sizeWidth) {
				width = Math.max(lineWidth, childWidth);// 取最大的
				lineWidth = childWidth + getPaddingLeft() + getPaddingRight(); // 重新开启新行，开始记录
				height += lineHeight;
				lineHeight = childHeight;
			} else {
				lineWidth += childWidth;
				lineHeight = Math.max(lineHeight, childHeight);
			}
			if (i == cCount - 1) {
				width = Math.max(width, lineWidth);
				height += lineHeight;
			}
		}
		setMeasuredDimension(sizeWidth, height + getPaddingTop()+getPaddingBottom());

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		int width = getMeasuredWidth();
		int lineWidth = getPaddingLeft();
		int lineHeight = getPaddingTop();
		int cl, ct, cr, cb;
		int cCount = getChildCount();
		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();

			if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) {
				lineWidth = getPaddingLeft();
				lineHeight = lineHeight + childHeight + lp.topMargin + lp.bottomMargin;
			}
			cl = lineWidth + lp.leftMargin;
			cr = cl + childWidth;
			ct = lineHeight + lp.topMargin;
			cb = ct + childHeight;
			lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
			child.layout(cl, ct, cr, cb);
		}
	}

}
