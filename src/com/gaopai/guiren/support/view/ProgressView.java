package com.gaopai.guiren.support.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.gaopai.guiren.R;

public class ProgressView extends View {

	private Paint borderPaint;
	private Paint backgroundPaint;
	private Paint forgroundPaint;
	private Paint dotPaint;

	private int percent = 20;

	public ProgressView(Context context) {
		super(context);
		init();
	}

	public ProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		borderPaint = new Paint();
		backgroundPaint = new Paint();
		forgroundPaint = new Paint();
		dotPaint = new Paint();
		borderPaint.setColor(getResources().getColor(R.color.jiav_progressbar_border));
		borderPaint.setAntiAlias(true);
		borderPaint.setStrokeWidth(2);
		borderPaint.setStyle(Style.STROKE);

		backgroundPaint = new Paint();
		backgroundPaint.setColor(getResources().getColor(R.color.jiav_progressbar_background));
		backgroundPaint.setStyle(Style.FILL);

		forgroundPaint = new Paint();
		forgroundPaint.setColor(getResources().getColor(R.color.jiav_progressbar_forground));
		forgroundPaint.setStyle(Style.FILL);

		dotPaint = new Paint();
		dotPaint.setAntiAlias(true);
		dotPaint.setColor(getResources().getColor(R.color.jiav_progressbar_dot));
		dotPaint.setStyle(Style.FILL);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		int height = getHeight() * 2 / 3;
		int paddingTop = (getHeight() - height) / 2;
		int paddingLeft = 2;
		int paddingRight = 2;
		RectF background = new RectF(new Rect(paddingLeft, paddingTop, getWidth() - paddingRight, paddingTop + height));
		float radius = height / 2;
		canvas.drawRoundRect(background, radius, radius, backgroundPaint);

		int processWidth = getWidth() / 100 * percent;
		RectF forground = new RectF(new Rect(paddingLeft, paddingTop, processWidth, paddingTop + height));
		canvas.drawRoundRect(forground, radius, radius, forgroundPaint);

		canvas.drawRoundRect(background, radius, radius, borderPaint);
		canvas.drawCircle(processWidth, getHeight() / 2, getHeight() / 2, dotPaint);
	}

	public void setProgress(int progress) {
		if (progress > 100) {
			progress = 100;
		}
		if (progress < 0) {
			progress = 0;
		}
		this.percent = progress;
		invalidate();
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_MOVE:
//			float x = event.getX();
//			int pro = (int) (100 * x / getWidth());
//			setProgress(pro);
//			break;
//
//		default:
//			break;
//		}
//		return true;
//	}
}
