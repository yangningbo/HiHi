package com.gaopai.guiren.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.BaseBean;
import com.gaopai.guiren.utils.StringUtils;
import com.gaopai.guiren.volley.IResponseListener;

/**
 * 第二个阶段开放注册时的认证界面
 * 
 */

public class RealVerifyActivity extends BaseActivity implements
		OnClickListener, OnTouchListener {

	private EditText mUsernameEdit, mCompanyEdit, mJobEdit, mPhoneEditText;
	private LinearLayout mRootLayout;
	private Button mVerifyBtn;
	private final static int HIDE_PROGRESS_DIALOG = 15453;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actual_verify);
		initComponent();
	}

	private void initComponent() {
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setLogoOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(RESULT_OK);
				hideKeyboard(RealVerifyActivity.this);
				finish();
			}
		});
		mTitleBar.addRightButtonView(R.string.verify_later).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						setResult(RESULT_OK);
					}
				});
		mTitleBar.setTitleText(R.string.verify_credit);

		mRootLayout = (LinearLayout) findViewById(R.id.rootlayout);
		mRootLayout.setOnTouchListener(this);

		mVerifyBtn = (Button) findViewById(R.id.apply_btn);
		mVerifyBtn.setOnClickListener(this);

		mUsernameEdit = (EditText) findViewById(R.id.real_name);
		mCompanyEdit = (EditText) findViewById(R.id.company);
		mJobEdit = (EditText) findViewById(R.id.post);
		mPhoneEditText = (EditText) findViewById(R.id.mobile_num);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.apply_btn:
			hideKeyboard(RealVerifyActivity.this);
			checkApply();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null) {
				InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}

	private void checkApply() {

		String username = mUsernameEdit.getText().toString().trim();
		String company = mCompanyEdit.getText().toString().trim();
		String job = mJobEdit.getText().toString().trim();
		String phone = mPhoneEditText.getText().toString().trim();
		if (TextUtils.isEmpty(username)) {
			String prompt = getString(R.string.please_input)
					+ getString(R.string.recommended_name);
			showToast(prompt);
			return;
		}
		if (TextUtils.isEmpty(company)) {
			String prompt = getString(R.string.please_input)
					+ getString(R.string.recommended_company);
			showToast(prompt);
			return;
		}
		if (TextUtils.isEmpty(job)) {
			String prompt = getString(R.string.please_input)
					+ getString(R.string.recommended_job);
			showToast(prompt);
			return;
		}
		if (TextUtils.isEmpty(phone)) {
			String prompt = getString(R.string.please_input)
					+ getString(R.string.recommended_phone);
			showToast(prompt);
			return;
		}

		if (phone.length() != 11) {
			String prompt = getString(R.string.please_input_correct_mobile_num);
			showToast(prompt);
			return;
		}

		realVerify(phone, username, company, job);
	}

	public void hideKeyboard(final Activity a) {
		InputMethodManager inputManager = (InputMethodManager) a
				.getApplicationContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
		if (inputManager != null) {
			inputManager.hideSoftInputFromWindow(a.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private void realVerify(final String phone, final String realName,
			final String company, final String job) {

		DamiInfo.realVerify(phone, realName, company, job,
				new IResponseListener() {

					@Override
					public void onSuccess(Object o) {
						BaseBean data = (BaseBean) o;
						if (data.state != null && data.state.code == 0) {
							showToast(R.string.apply_tribe_sucess);
							RealVerifyActivity.this.finish();
						} else if (data.state != null
								&& data.state.code == DamiCommon.EXPIRED_CODE) {
							setResult(InvitationVerifyActivity.VERIFY_TOKEN_EXPIRED);
							RealVerifyActivity.this.finish();
						} else {
							String str;
							if (data.state != null
									&& !StringUtils.isEmpty(data.state.msg)) {
								str = data.state.msg;
							} else {
								str = getString(R.string.load_error);
							}
							showToast(str);
						}
					}

					@Override
					public void onReqStart() {
						showProgressDialog(R.string.send_loading);
					}

					@Override
					public void onFinish() {
						removeProgressDialog();

					}

					@Override
					public void onFailure(Object o) {
						showToast(R.string.load_error);
					}
					
					@Override
					public void onTimeOut() {
						// TODO Auto-generated method stub
						showToast(R.string.request_timeout);
					}
				});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MainActivity.LOGIN_REQUEST:
			if (resultCode == RESULT_OK) {
				checkApply();
			}
			break;
		default:
			break;
		}
	}

}
