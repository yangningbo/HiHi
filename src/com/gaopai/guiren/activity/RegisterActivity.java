package com.gaopai.guiren.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.bean.net.RegisterResult;
import com.gaopai.guiren.bean.net.VerificationResult;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class RegisterActivity extends BaseActivity implements OnClickListener {

	private EditText etPhone;
	private EditText etVeryfication;
	private Button btnConfirm;
	private Button btnSendVeryfication;
	private EditText etPassword;
	private EditText etName;
	private TextView tvSelectCountry;
	private TextView tvRequestVeryficaion;

	private TextView tvCountryCode;
	private ImageButton cbAgreeGuiren;
	private TextView tvAgreeGuiren;

	private Button btnEye;
	private Handler mHandler;
	private String countryCode = "86";

	public final static int TYPE_REGISTER = 0;
	public final static int TYPE_FORGET_PASSWORD = 1;
	public final static int TYPE_BIND_PHONE = 2;
	public final static int TYPE_RE_BIND_PHONE = 3;
	public final static String KEY_TYPE = "type";
	public final static String KEY_USER = "user";
	private int type;

	private boolean isChecked = false;
	
	private User mLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_register);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(getString(R.string.register));
		type = getIntent().getIntExtra(KEY_TYPE, 0);
		mLogin = (User) getIntent().getSerializableExtra("user");
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
		etName = ViewUtil.findViewById(this, R.id.et_input_name);

		btnSendVeryfication = ViewUtil.findViewById(this, R.id.btn_send_veryfication);
		btnSendVeryfication.setOnClickListener(this);
		tvSelectCountry = ViewUtil.findViewById(this, R.id.tv_select_country);
		tvSelectCountry.setOnClickListener(this);
		tvRequestVeryficaion = ViewUtil.findViewById(this, R.id.tv_request_veryfication);

		btnEye = ViewUtil.findViewById(this, R.id.btn_pswd_eye);
		btnEye.setOnClickListener(this);

		tvCountryCode = ViewUtil.findViewById(this, R.id.tv_country_num);
		cbAgreeGuiren = ViewUtil.findViewById(this, R.id.cb_agree_guiren);
		cbAgreeGuiren.setOnClickListener(this);
		tvAgreeGuiren = ViewUtil.findViewById(this, R.id.tv_agree_guiren);
		tvAgreeGuiren.setOnClickListener(this);

		tvCountryCode.setText(countryCode);

		if (type == TYPE_REGISTER) {
			btnConfirm.setText(R.string.confirm_register);
			mTitleBar.setTitleText(R.string.register);
		} else if (type == TYPE_FORGET_PASSWORD) {
			btnConfirm.setText(R.string.confirm_modify);
			mTitleBar.setTitleText(R.string.modify);
			etName.setVisibility(View.GONE);
		} else if (type == TYPE_BIND_PHONE || type == TYPE_RE_BIND_PHONE) {
			if (type == TYPE_BIND_PHONE) {
				btnConfirm.setText(R.string.confirm_bind_phone);
				mTitleBar.setTitleText(R.string.bind_phone);
			} else {
				btnConfirm.setText(R.string.confirm_modify);
				mTitleBar.setTitleText(R.string.re_bind_phone);
			}
			etPassword.setVisibility(View.GONE);
			findViewById(R.id.layout_input_password).setVisibility(View.GONE);
			findViewById(R.id.layout_agree_guiren).setVisibility(View.GONE);
			findViewById(R.id.tv_register_info).setVisibility(View.GONE);
			((LinearLayout) btnConfirm.getParent()).setGravity(Gravity.TOP);
			if (mLogin != null) {
				etName.setText(User.getUserName(mLogin));
			}
			etName.setEnabled(false);
		}

		etPhone.addTextChangedListener(new ViewUtil.SimpleWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 0) {
					btnSendVeryfication.setVisibility(View.VISIBLE);
				} else {
					btnSendVeryfication.setVisibility(View.GONE);
				}
			}
		});
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

			@Override
			public void onFailure(Object o) {
				endCount();
				super.onFailure(o);
			}

			@Override
			public void onTimeOut() {
				endCount();
				super.onTimeOut();
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

	private void register(String realName, String phone, String password, String code) {
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
							showToast(R.string.modify_success);
						}
						RegisterActivity.this.finish();
					}
				} else {
					otherCondition(data.state, RegisterActivity.this);
				}
			}
		};
		if (type == TYPE_REGISTER) {
			DamiInfo.register(realName, phone, password, code, listener);
		} else {
			DamiInfo.resetPassword(phone, password, code, listener);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {
			String code = intent.getStringExtra(CountryCodeActivity.KEY_COUNTRY_CODE);
			String name = intent.getStringExtra(CountryCodeActivity.KEY_COUNTRY_NAME);
			countryCode = code;
			tvCountryCode.setText(countryCode);
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
		case R.id.tv_agree_guiren:
			startActivity(UserProtocalActivity.getIntent(mContext, 0));
			return;
		case R.id.cb_agree_guiren:
			isChecked = !isChecked;
			cbAgreeGuiren.setImageResource(isChecked ? R.drawable.icon_check_box_hook : R.drawable.transparent);
			break;
		default:
			break;
		}
	}

	private void confirm() {
		if (!isChecked && type != TYPE_BIND_PHONE && type != TYPE_RE_BIND_PHONE) {
			showToast(R.string.please_agree_guiren_first);
			return;
		}
		String password = etPassword.getText().toString();
		String veryfication = etVeryfication.getText().toString();
		if (TextUtils.isEmpty(etPhone.getText())) {
			showToast(R.string.phone_can_not_be_empty);
			return;
		}

		if (TextUtils.isEmpty(veryfication)) {
			showToast(R.string.veryficaion_can_not_be_empty);
			return;
		}

		if (type == TYPE_BIND_PHONE || type == TYPE_RE_BIND_PHONE) {
			bindPhone();
			return;
		}
		if (TextUtils.isEmpty(password)) {
			showToast(R.string.password_can_not_be_empty);
			return;
		}

		if (TextUtils.isEmpty(etName.getText())) {
			showToast(R.string.name_can_not_be_empty);
			return;
		}

		register(etName.getText().toString(), etPhone.getText().toString(), password, veryfication);
	}

	private void bindPhone() {
		DamiInfo.bindPhone(etPhone.getText().toString(), etVeryfication.getText().toString(), etName.getText()
				.toString(), new SimpleResponseListener(mContext, R.string.request_internet_now) {
			@Override
			public void onSuccess(Object o) {
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					if (type == TYPE_BIND_PHONE) {
						showToast(R.string.bind_phone_success);
						if (mLogin != null) {
							DamiCommon.saveLoginResult(mContext, mLogin);
							sendBroadcast(new Intent(MainActivity.LOGIN_SUCCESS_ACTION));
						}
					} else {
						showToast(R.string.modify_success);
					}
					User user = DamiCommon.getLoginResult(mContext);
					user.phone = etPhone.getText().toString();
					DamiCommon.saveLoginResult(mContext, user);
					RegisterActivity.this.setResult(RESULT_OK);
				} else {
					otherCondition(data.state, RegisterActivity.this);
				}
			}
		});
	}

	public static Intent getIntent(Context context, int type) {
		Intent intent = new Intent(context, RegisterActivity.class);
		intent.putExtra(KEY_TYPE, type);
		return intent;
	}
	public static Intent getIntent(Context context, int type, User user) {
		Intent intent = new Intent(context, RegisterActivity.class);
		intent.putExtra(KEY_TYPE, type);
		intent.putExtra(KEY_USER, user);
		return intent;
	}
}
