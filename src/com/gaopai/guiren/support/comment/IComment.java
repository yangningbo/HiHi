package com.gaopai.guiren.support.comment;

import android.view.View.OnClickListener;
import android.widget.EditText;

import com.gaopai.guiren.BaseActivity;

public abstract class IComment {
	protected String title;
	protected String btnText;
	protected String hint;
	protected OnClickListener btnClickListener;
	protected BaseActivity activity;
	protected EditText editText;

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setBtnText(String btnText) {
		this.btnText = btnText;
	}

	public String getBtnText() {
		return btnText;
	}
	
	public void setEditHint(String hint) {
		this.hint = hint;
	}
	
	public String getHint() {
		return hint;
	}

	public void setOnClickListener(OnClickListener btnClickListener) {
		this.btnClickListener = btnClickListener;
	}

	public OnClickListener getClickListener() {
		return btnClickListener;
	}

	public void onCreat(BaseActivity activity) {
		this.activity = activity;
	}
	
	public void setEdittext(EditText editText) {
		this.editText = editText;
	}
}
