package com.gaopai.guiren.support.view;

import com.gaopai.guiren.fragment.DynamicFragment;
import com.gaopai.guiren.fragment.DynamicFragment.BackPressedListener;
import com.gaopai.guiren.utils.Logger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class CustomEditText extends EditText {

	public CustomEditText(Context context) {
		super(context);
	}

	public CustomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private DynamicFragment.BackPressedListener backPressedListener;

	public void setBackPressedListener(BackPressedListener listener) {
		this.backPressedListener = listener;
	}

	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP
				&& (backPressedListener != null)) {
			if (this.getVisibility() == View.VISIBLE) {
				hideSoftKeyboard(this);
				backPressedListener.onBack();
				return true;
			}
		}
		return super.dispatchKeyEventPreIme(event);
	}

	public void hideSoftKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public void showSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}

}
