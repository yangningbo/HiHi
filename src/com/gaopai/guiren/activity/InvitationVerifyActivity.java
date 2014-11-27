package com.gaopai.guiren.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.app.Activity;
import android.content.Context;
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
import com.gaopai.guiren.bean.LoginResult;
import com.gaopai.guiren.utils.StringUtils;
import com.gaopai.guiren.volley.IResponseListener;

/**
 * 验证贵人码界面
 * 
 */
public class InvitationVerifyActivity extends BaseActivity implements
		OnClickListener, OnTouchListener {

	@ViewInject(id = R.id.code)
	private EditText mCodeEditText;
	@ViewInject(id = R.id.verify_btn)
	private Button mVerifyBtn;
	@ViewInject(id = R.id.rootlayout)
	private LinearLayout mRootLayout;

	public static final int VERIFY_TOKEN_EXPIRED = 21545;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_invitation_verify);
		FinalActivity.initInjectedView(this);
		initView();
	}

	private void initView() {
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setLogoOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(RESULT_OK);
				hideKeyboard(InvitationVerifyActivity.this);
				finish();
			}
		});
		mTitleBar.addRightTextView(R.string.verify_later).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						setResult(RESULT_OK);
					}
				});
		mTitleBar.setTitleText(R.string.verify_credit);
		mVerifyBtn.setOnClickListener(this);
		mRootLayout.setOnTouchListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.verify_btn:
			hideKeyboard(InvitationVerifyActivity.this);
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
		String code = mCodeEditText.getText().toString().trim();
		if (TextUtils.isEmpty(code)) {
			showToast(R.string.please_input_invitation_code);
			return;
		}
		verifyInvitationCode(code);
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

	private void verifyInvitationCode(final String code) {

		DamiInfo.verifyInvitationCode(code, new IResponseListener() {

			@Override
			public void onSuccess(Object o) {
				LoginResult data = (LoginResult) o;
				if (data.state != null && data.state.code == 0) {
					DamiCommon.saveLoginResult(InvitationVerifyActivity.this,
							data.data);
					DamiCommon.setUid(data.data.uid);
					DamiCommon.setToken(data.data.token);
					showToast(R.string.verify_success);
					setResult(RESULT_OK);
					InvitationVerifyActivity.this.finish();
				} else {
					if (data.state != null
							&& data.state.code == DamiCommon.EXPIRED_CODE) {
						setResult(VERIFY_TOKEN_EXPIRED);
						InvitationVerifyActivity.this.finish();
					} else {
						String str;
						if (data.state != null
								&& !StringUtils.isEmpty(data.state.msg)) {
							str = data.state.msg;
						} else {
							str = getString(R.string.verify_failed);
						}
						showToast(str);
					}
				}
			}

			@Override
			public void onReqStart() {
				// TODO Auto-generated method stub
				showToast(R.string.send_loading);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				removeProgressDialog();
			}

			@Override
			public void onFailure(Object o) {
				showToast(R.string.verify_failed);
			}

			@Override
			public void onTimeOut() {
				// TODO Auto-generated method stub
				showToast(R.string.request_timeout);
			}
		});

	}
}
