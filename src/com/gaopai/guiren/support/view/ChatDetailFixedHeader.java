package com.gaopai.guiren.support.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.gaopai.guiren.R;

public class ChatDetailFixedHeader extends LinearLayout {
	private View layoutZan;
	private View layoutComment;
	
	private int zanOriginTop;
	private int commentOriginTop;

	public ChatDetailFixedHeader(Context context) {
		super(context);
	}

	public ChatDetailFixedHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		layoutZan = findViewById(R.id.ll_zan);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
//		drawChild(canvas, layoutZan, getDrawingTime());
	}
}
