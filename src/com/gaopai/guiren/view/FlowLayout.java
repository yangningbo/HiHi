package com.gaopai.guiren.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.gaopai.guiren.utils.MyUtils;

public class FlowLayout extends ViewGroup {
	protected MarginLayoutParams textLayoutParams;
	protected Context mContext;

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
	}

	public MarginLayoutParams getTextLayoutParams() {
		return textLayoutParams;
	}

	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}

	/**
	 * 负责设置子控件的测量模式和大小 根据所有子控件设置自己的宽和高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

		// 如果是warp_content情况下，记录宽和高
		int width = 0;
		int height = 0;
		/**
		 * 记录每一行的宽度，width不断取最大宽度
		 */
		int lineWidth = 0;
		/**
		 * 每一行的高度，累加至height
		 */
		int lineHeight = 0;

		int cCount = getChildCount();

		// 遍历每个子元素
		// Log.d("parent", "meaurespec = " + modeWidth + "   " + sizeWidth);
		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
			int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
			int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

			if (lineWidth + childWidth > sizeWidth) {
				width = Math.max(lineWidth, childWidth);// 取最大的
				lineWidth = childWidth; // 重新开启新行，开始记录
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
		setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : width,
				(modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		int width = getMeasuredWidth();
		int lineWidth = 0;
		int lineHeight = 0;
		int cl, ct, cr, cb;
		int cCount = getChildCount();
		for (int i = 0; i < cCount; i++) {
			View child = getChildAt(i);
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();

			if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) {
				lineWidth = 0;
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
