package com.gaopai.guiren.support.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ListView;

public class ChatDetailFixedHeaderListView extends ListView {

	public ChatDetailFixedHeaderListView(Context context) {
		super(context);
	}

	public ChatDetailFixedHeaderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ChatDetailFixedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.dispatchDraw(canvas);
	}
	
	
}
