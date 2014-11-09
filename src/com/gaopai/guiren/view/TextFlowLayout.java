package com.gaopai.guiren.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.MyUtils;

public class TextFlowLayout extends FlowLayout {


	public TextFlowLayout(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public TextFlowLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public TextFlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		
	}

	public TextView creatTextView(String text) {
		TextView textView = new TextView(mContext);
		textView.setBackgroundResource(R.drawable.tag_bg);
		textView.setText(text);
		textView.setTextColor(Color.WHITE);
		this.addView(textView, textLayoutParams);
		return textView;
	}

}
