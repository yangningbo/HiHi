package com.gaopai.guiren.support.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.gaopai.guiren.R;

public class HandleDividerLayout extends RelativeLayout {

	private boolean isDividerEnable = true;
	private Paint dividerPaint;

	public HandleDividerLayout(Context context) {
		super(context);
		init();
	}

	public HandleDividerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HandleDividerLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void setDividerEnable(boolean isEnable) {
		this.isDividerEnable = isEnable;
	}

	public void init() {
		dividerPaint = new Paint();
		dividerPaint.setColor(getResources().getColor(R.color.titlebar_divider));
		dividerPaint.setStrokeWidth(2f);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isDividerEnable) {
			canvas.drawLine(0, getHeight(), getWidth(), getHeight(), dividerPaint);
		}
	}
}
