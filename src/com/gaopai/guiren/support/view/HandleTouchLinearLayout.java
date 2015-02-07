package com.gaopai.guiren.support.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;

public class HandleTouchLinearLayout extends PullToRefreshListView {

	public HandleTouchLinearLayout(Context context) {
		super(context);
	}

	public HandleTouchLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean dispatchTouchEvent(final MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			this.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (actionDownListener != null) {
						actionDownListener.onTouch(HandleTouchLinearLayout.this, ev);
					}
				}
			}, 300);

		}
		return super.dispatchTouchEvent(ev);
	}

	private OnTouchListener actionDownListener;

	public void setActionDownTouchListener(OnTouchListener listener) {
		actionDownListener = listener;
	}
}
