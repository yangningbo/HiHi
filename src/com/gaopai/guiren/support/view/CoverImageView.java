package com.gaopai.guiren.support.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.gaopai.guiren.R;

public class CoverImageView extends ImageView {
	private Paint paint = new Paint();
	private boolean pressed = false;

	public CoverImageView(Context context) {
		this(context, null);
	}

	public CoverImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CoverImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		paint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (pressed) {
			canvas.drawColor(getResources().getColor(R.color.transparent_cover));
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			pressed = true;
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			pressed = false;
			invalidate();
			break;
		}
		return super.onTouchEvent(event);
	}

}
