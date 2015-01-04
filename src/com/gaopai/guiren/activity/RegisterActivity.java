package com.gaopai.guiren.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.net.RegisterResult;
import com.gaopai.guiren.bean.net.VerificationResult;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class RegisterActivity extends BaseActivity implements OnClickListener {

	private EditText etPhone;
	private EditText etVeryfication;
	private Button btnConfirm;
	private Button btnSendVeryfication;
	private EditText etPassword;
	private TextView tvSelectCountry;
	private TextView tvRequestVeryficaion;

	private Button btnEye;
	private Handler mHandler;
	private String countryCode = "86";

	public final static int TYPE_REGISTER = 0;
	public final static int TYPE_FORGET_PASSWORD = 1;
	public final static String KEY_TYPE = "type";
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_register);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(getString(R.string.register));
		type = getIntent().getIntExtra(KEY_TYPE, 0);
		initView();
		mHandler = new Handler(getMainLooper()) {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					setCountDownText(msg.arg1);
				} else {
					endCount();
				}
			}
		};
	}

	private void initView() {
		etPhone = ViewUtil.findViewById(this, R.id.et_input_phone);
		etVeryfication = ViewUtil.findViewById(this, R.id.et_input_veryfication_code);
		btnConfirm = ViewUtil.findViewById(this, R.id.btn_confirm);
		btnConfirm.setOnClickListener(this);
		etPassword = ViewUtil.findViewById(this, R.id.et_input_password);

		btnSendVeryfication = ViewUtil.findViewById(this, R.id.btn_send_veryfication);
		btnSendVeryfication.setOnClickListener(this);
		tvSelectCountry = ViewUtil.findViewById(this, R.id.tv_select_country);
		tvSelectCountry.setOnClickListener(this);
		tvRequestVeryficaion = ViewUtil.findViewById(this, R.id.tv_request_veryfication);

		btnEye = ViewUtil.findViewById(this, R.id.btn_pswd_eye);
		btnEye.setOnClickListener(this);

		if (type == TYPE_REGISTER) {
			btnConfirm.setText(R.string.confirm_register);
			mTitleBar.setTitleText(R.string.register);
		} else {
			btnConfirm.setText(R.string.confirm_modify);
			mTitleBar.setTitleText(R.string.modify);
		}
	}

	private boolean isCountDown = false;

	private void setCountDownText(int num) {
		String text1 = "没收到短信？";
		String text2 = "秒后重新获取";
		String text = text1 + num + text2;
		SpannableString spString = new SpannableString(text);
		spString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.general_text_gray)), 0,
				text1.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		spString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.general_blue)), text1.length(),
				(text1 + num).length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		spString.setSpan(new AbsoluteSizeSpan(20, true), text1.length(), (text1 + num).length(),
				Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		spString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.general_text_gray)),
				(text1 + num).length(), text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		tvRequestVeryficaion.setText(spString);
	}

	class CountDownRunnable implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int time = 60;
			while (isCountDown) {
				sendMessage(0, time);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				time--;
				if (time < 0) {
					sendMessage(1, time);
				}
			}
		}
	}

	private void sendMessage(int what, int time) {
		Message message = mHandler.obtainMessage();
		message.what = what;
		message.arg1 = time;
		message.sendToTarget();
	}

	private void getSmsCode(String phone, String countryCode) {

		DamiInfo.getSmsCode(phone, countryCode, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final VerificationResult data = (VerificationResult) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null) {
					}
				} else {
					endCount();
					otherCondition(data.state, RegisterActivity.this);
				}
			}
		});
		startCount();
	}

	private void startCount() {
		isCountDown = true;
		btnSendVeryfication.setEnabled(false);
		tvRequestVeryficaion.setVisibility(View.VISIBLE);
		setCountDownText(60);
		new Thread(new CountDownRunnable()).start();
	}

	private void endCount() {
		isCountDown = false;
		btnSendVeryfication.setEnabled(true);
		tvRequestVeryficaion.setVisibility(View.GONE);
	}

	private void register(String phone, String password, String code) {
		SimpleResponseListener listener = new SimpleResponseListener(mContext, R.string.request_internet_now) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final RegisterResult data = (RegisterResult) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null) {
						if (type == TYPE_REGISTER) {
							showToast(R.string.register_success);
						} else {
							showToast(R.string.modify_password);
						}
						RegisterActivity.this.finish();
					}
				} else {
					otherCondition(data.state, RegisterActivity.this);
				}
			}
		};
		if (type == TYPE_REGISTER) {
			DamiInfo.register(phone, password, code, listener);
		} else {
			DamiInfo.resetPassword(phone, password, code, listener);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			String code = intent.getStringExtra(CountryCodeActivity.KEY_COUNTRY_CODE);
			String name = intent.getStringExtra(CountryCodeActivity.KEY_COUNTRY_NAME);
			countryCode = code;
			tvSelectCountry.setText(name);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isCountDown = false;
	}

	private boolean isShowPswd = true;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_send_veryfication:
			if (TextUtils.isEmpty(etPhone.getText())) {
				showToast(R.string.phone_can_not_be_empty);
				return;
			}
			getSmsCode(etPhone.getText().toString(), countryCode);
			break;
		case R.id.btn_confirm:
			confirm();
			break;
		case R.id.tv_select_country:
			startActivityForResult(CountryCodeActivity.class, 0);
			break;
		case R.id.btn_pswd_eye:
			isShowPswd = !isShowPswd;
			int selection = etPassword.getSelectionStart();
			if (isShowPswd) {
				etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			} else {
				etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
			}
			etPassword.setSelection(selection);
			break;
		default:
			break;
		}
	}

	private void confirm() {
		String password = etPassword.getText().toString();
		String veryfication = etVeryfication.getText().toString();
		if (TextUtils.isEmpty(etPhone.getText())) {
			showToast(R.string.phone_can_not_be_empty);
			return;
		}
		if (TextUtils.isEmpty(password)) {
			showToast(R.string.password_can_not_be_empty);
			return;
		}
		if (TextUtils.isEmpty(veryfication)) {
			showToast(R.string.veryficaion_can_not_be_empty);
			return;
		}

		register(etPhone.getText().toString(), password, veryfication);
	}

}
