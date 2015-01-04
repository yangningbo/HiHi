package com.gaopai.guiren.support;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class TextLimitWatcher implements TextWatcher {
	private TextView tvDest;
	private int maxCount;

	public TextLimitWatcher(TextView tvDest, int maxCount) {
		this.tvDest = tvDest;
		this.maxCount = maxCount;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		tvDest.setText("还能输入" + (maxCount - s.length()) + "字");
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}
}
