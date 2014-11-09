package com.gaopai.guiren.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.net.RegisterResult;
import com.gaopai.guiren.bean.net.VerificationResult;
import com.gaopai.guiren.bean.net.RegisterResult.RegisterBean;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class RegisterActivity extends BaseActivity {
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;

	private EditText et1;
	private EditText et2;

	private Button btnNextStep;

	private int mStage = 1;// 1 2 3
	private Handler mHandler;

	private String phoneNum;
	private String smsCode;

	private EditText etCountryCode;
	private View viewChoseCountry;
	private TextView tvCountry;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_register);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(getString(R.string.register));
		initView();
		changeViewByStage();
		mHandler = new Handler(getMainLooper()) {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (msg.what == 0) {
					rightTextView.setText("请于" + String.valueOf(msg.arg1) + "秒后重新获取");
				} else {
					if (mStage == 2) {
						rightTextView.setText("点击获取验证码");
					} else {
						removeRightTitle();
					}
				}
			}
		};
	}

	private void initView() {
		tv1 = (TextView) findViewById(R.id.tv_register1);
		tv2 = (TextView) findViewById(R.id.tv_register2);
		tv3 = (TextView) findViewById(R.id.tv_register3);

		et1 = (EditText) findViewById(R.id.et_register1);
		et2 = (EditText) findViewById(R.id.et_register2);

		etCountryCode = (EditText) findViewById(R.id.et_country_code);
		moveEditTextCursor(etCountryCode);
		tvCountry = (TextView) findViewById(R.id.tv_country);
		viewChoseCountry = findViewById(R.id.rl_chose_country);

		viewChoseCountry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivityForResult(CountryCodeActivity.class, 0);
			}
		});

		btnNextStep = (Button) findViewById(R.id.btn_next_step);
		btnNextStep.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (mStage) {
				case 1: {
					String set1 = et1.getText().toString();
					String countryCode = etCountryCode.getText().toString();
					if (TextUtils.isEmpty(set1)) {
						showToast("输入不能为空");
						return;
					}
					if (!Patterns.PHONE.matcher(set1).matches()) {
						showToast("请输入正确的电话号码");
						return;
					}
					getSmsCode(set1, countryCode);
					break;
				}
				case 2: {
					String set1 = et1.getText().toString();
					if (TextUtils.isEmpty(set1)) {
						showToast("输入不能为空");
						return;
					}
					isCountDown = false;
					mStage = 3;
					changeViewByStage();
					break;
				}
				case 3: {
					String set1 = et1.getText().toString();
					String set2 = et2.getText().toString();
					if (TextUtils.isEmpty(set1) || TextUtils.isEmpty(set2)) {
						showToast("输入不能为空");
						return;
					}
					if (!set1.equals(set2)) {
						showToast("两次输入不相同");
						return;
					}
					register(phoneNum, set1, smsCode);
					break;
				}
				default:
					break;
				}
			}
		});
	}

	private void changeViewByStage() {
		switch (mStage) {
		case 1:
			tv1.setTextColor(getResources().getColor(R.color.red_dongtai_bg));
			tv2.setTextColor(getResources().getColor(R.color.gray));
			tv3.setTextColor(getResources().getColor(R.color.gray));
			break;
		case 2:
			tv1.setTextColor(getResources().getColor(R.color.gray));
			tv2.setTextColor(getResources().getColor(R.color.red_dongtai_bg));
			tv3.setTextColor(getResources().getColor(R.color.gray));
			viewChoseCountry.setVisibility(View.GONE);
			etCountryCode.setVisibility(View.GONE);
			// et1.setText("");
			 et1.setHint("请输入验证码");
			break;
		case 3:
			tv1.setTextColor(getResources().getColor(R.color.gray));
			tv2.setTextColor(getResources().getColor(R.color.gray));
			tv3.setTextColor(getResources().getColor(R.color.red_dongtai_bg));
			et2.setVisibility(View.VISIBLE);
			removeRightTitle();
			emptyTextOfView(et1);
			emptyTextOfView(et2);
			et1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			et2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			et1.setHint("请输入密码");
			et2.setHint("请再次输入密码");
			btnNextStep.setText("确定");
			break;
		default:
			break;
		}
	}

	private boolean isCountDown = false;
	private TextView rightTextView;

	private void addRightCountDownText() {
		if (rightTextView == null) {
			rightTextView = mTitleBar.addRightButtonView("请于" + String.valueOf(60) + "秒后重新获取");
			mTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
			emptyTextOfView(rightTextView);
			rightTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (isCountDown || (mStage != 2)) {
						return;
					}
					isCountDown = true;
					new Thread(new CountDownRunnable()).start();
				}
			});
		}
	}

	class CountDownRunnable implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int time = 60;
			while (isCountDown) {
				Message message = mHandler.obtainMessage();
				message.what = 0;
				message.arg1 = time;
				message.sendToTarget();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				time--;
				if (time < 0) {
					isCountDown = false;
				}
			}
			Message message = mHandler.obtainMessage();
			message.what = 1;
			message.sendToTarget();
		}
	}

	private void getSmsCode(String phone, String countryCode) {
		DamiInfo.getSmsCode(phone, countryCode, new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final VerificationResult data = (VerificationResult) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null) {
						VerificationResult.SmsCode sms = data.data;
						et1.setText(sms.code);
						moveEditTextCursor(et1);
						phoneNum = sms.phone;
						smsCode = sms.code;
						mStage = 2;
						changeViewByStage();
						addRightCountDownText();
						isCountDown = true;
						new Thread(new CountDownRunnable()).start();
					}
				} else {
					this.showError(data);
				}
			}
		});
	}

	private void register(String phone, String password, String code) {
		DamiInfo.register(phone, password, code, new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final RegisterResult data = (RegisterResult) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null) {
						showToast("注册成功");
						RegisterActivity.this.finish();
					}
				} else {
					this.showError(data);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			String code = intent.getStringExtra(CountryCodeActivity.KEY_COUNTRY_CODE);
			String name = intent.getStringExtra(CountryCodeActivity.KEY_COUNTRY_NAME);
			etCountryCode.setText(code);
			moveEditTextCursor(etCountryCode);
			tvCountry.setText(name);
		}
	}
	
	private void moveEditTextCursor(EditText editText) {
		editText.setSelection(editText.length());
	}
	
	private void emptyTextOfView(TextView textView) {
		textView.setText("");
	}
	
	private void removeRightTitle() {
		rightTextView.setText("");
		mTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
	}

}
