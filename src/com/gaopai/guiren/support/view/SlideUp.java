package com.gaopai.guiren.support.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class SlideUp extends ViewGroup {

	private ViewDragHelper mDragHelper;
	private View panelView;

	public SlideUp(Context context) {
		this(context, null);
	}

	public SlideUp(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
	}

	class DragHelperCallback extends ViewDragHelper.Callback {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			// TODO Auto-generated method stub
			return child == panelView;
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			// TODO Auto-generated method stub
			int topBound = getMeasuredHeight() - child.getMeasuredHeight();
			int bottomBound = getMeasuredHeight() - 100;
			return Math.min(Math.max(topBound, top), bottomBound);
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			// TODO Auto-generated method stub
			invalidate();
			super.onViewPositionChanged(changedView, left, top, dx, dy);
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// TODO Auto-generated method stub
			if (yvel > 2) {
				if (mDragHelper.smoothSlideViewTo(releasedChild, 0, getHeight() - 100)) {
					ViewCompat.postInvalidateOnAnimation(SlideUp.this);
				}
			} else {
				if (mDragHelper.smoothSlideViewTo(releasedChild, getWidth() - releasedChild.getWidth(), getHeight() - releasedChild.getHeight())) {
					ViewCompat.postInvalidateOnAnimation(SlideUp.this);
				}
			}

			super.onViewReleased(releasedChild, xvel, yvel);
		}
		
		
	}

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (mDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
		super.computeScroll();
	}
	
	

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		int action = MotionEventCompat.getActionMasked(ev);
		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
			mDragHelper.cancel();
			return false;
		}
		boolean should = mDragHelper.shouldInterceptTouchEvent(ev);

		return should;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		mDragHelper.processTouchEvent(event);
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		View child = getChildAt(0);
		child.measure(widthMeasureSpec, heightMeasureSpec);
		child = getChildAt(1);
		LayoutParams lp = child.getLayoutParams();
		panelView = child;
		child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		View child = getChildAt(0);
		child.layout(l, t, r, b);
		child = getChildAt(1);
		child.layout(l, getMeasuredHeight() - 100, r, getMeasuredHeight() - 100 + child.getMeasuredHeight());
	}

	private void close() {
		if (mDragHelper.smoothSlideViewTo(panelView, 0, getHeight() - 100)) {
			ViewCompat.postInvalidateOnAnimation(SlideUp.this);
		}
	}

}
