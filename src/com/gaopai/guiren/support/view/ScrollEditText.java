package com.gaopai.guiren.support.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class ScrollEditText extends EditText {

	public ScrollEditText(Context context) {
		super(context);
	}

	public ScrollEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private float lastPos = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		getParent().requestDisallowInterceptTouchEvent(true);
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			lastPos = (int) event.getY();
			break;
		case MotionEvent.ACTION_UP:
			getParent().requestDisallowInterceptTouchEvent(false);
			break;
		case MotionEvent.ACTION_MOVE:
			float currentY = (int) event.getY();
			float deltaScroll = currentY - lastPos;
			if (getScrollY() == 0) {
				if (deltaScroll > 0) {
					getParent().requestDisallowInterceptTouchEvent(false);
				} else {
					getParent().requestDisallowInterceptTouchEvent(true);
				}
			}
			int boxHeight = getHeight() - getPaddingTop() - getPaddingBottom();
			int bottom = getLayout().getLineBottom(getLineCount() - 1);
//			Logger.d(this, "top=" + boxHeight + "  bottom=" + bottom + "   scrolly=" + getScrollY());
			if (Math.abs(boxHeight + getScrollY() - bottom) < 5) {
				if (deltaScroll > 0) {
					getParent().requestDisallowInterceptTouchEvent(true);
				} else {
					getParent().requestDisallowInterceptTouchEvent(false);
				}
			}
			lastPos = currentY;
			break;
		default:
			break;
		}
		super.onTouchEvent(event);
		return true;
	}
}
