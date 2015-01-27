package com.gaopai.guiren.support.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.gaopai.guiren.support.view.rounded.RoundedImageView;

public class BorderRoundedImageView extends RoundedImageView {
	RectF diskRectF;
	Paint diskPaint;

	public BorderRoundedImageView(Context context) {
		super(context);
		init();
	}

	public BorderRoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BorderRoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		diskPaint = new Paint();
		diskPaint.setStrokeWidth(1f);
		diskPaint.setAntiAlias(true);
		diskPaint.setColor(Color.parseColor("#80ffffff"));
		diskPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		diskRectF = new RectF(0, 0, w, h);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawOval(diskRectF, diskPaint);
		super.onDraw(canvas);
		diskPaint.setColor(Color.parseColor("#80666669"));
		diskPaint.setStyle(Paint.Style.STROKE);
		canvas.drawCircle(diskRectF.width() / 2, diskRectF.height() / 2, diskRectF.width() / 2, diskPaint);
	}
}
