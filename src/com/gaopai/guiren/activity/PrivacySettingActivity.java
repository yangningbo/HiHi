package com.gaopai.guiren.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.PrivacySettingResult;
import com.gaopai.guiren.bean.PrivacySettingResult.PrivacySettingBean;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class PrivacySettingActivity extends BaseActivity implements OnClickListener {

	private TextView tvCheckPhone;
	private TextView tvCheckEmail;
	private TextView tvCheckWeixin;
	private TextView tvCheckWeibo;
	private TextView tvCheckConnection;

	private PrivacySettingBean settingBean;

	private int switchOff;
	private int switchOn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_privacy_setting);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.private_setting);
		initComponent();
		getSettings();
	}

	private void getSettings() {
		// TODO Auto-generated method stub
		DamiInfo.getPrivacyConfig(new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				PrivacySettingResult data = (PrivacySettingResult) o;
				if (data.state != null && data.state.code == 0) {
					settingBean = data.data;
					bindView();
				} else {
					otherCondition(data.state, PrivacySettingActivity.this);
				}
			}

		});
	}

	private void bindView() {
		// TODO Auto-generated method stub
		setSwitchState(tvCheckConnection, settingBean.renmai);
		setSwitchState(tvCheckEmail, settingBean.mail);
		setSwitchState(tvCheckPhone, settingBean.phone);
		setSwitchState(tvCheckWeibo, settingBean.weibo);
		setSwitchState(tvCheckWeixin, settingBean.wechat);

	}

	private void setSwitchState(TextView textView, int state) {
		textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, (state == 0) ? switchOff : switchOn, 0);
	}

	private void initComponent() {
		// TODO Auto-generated method stub
		tvCheckPhone = (TextView) findViewById(R.id.tv_check_phone_number);
		tvCheckEmail = (TextView) findViewById(R.id.tv_check_email);
		tvCheckWeixin = (TextView) findViewById(R.id.tv_check_weixin_number);
		tvCheckWeibo = (TextView) findViewById(R.id.tv_check_sina_weibo);
		tvCheckConnection = (TextView) findViewById(R.id.tv_check_connection);

		tvCheckPhone.setOnClickListener(this);
		tvCheckEmail.setOnClickListener(this);
		tvCheckWeixin.setOnClickListener(this);
		tvCheckWeibo.setOnClickListener(this);
		tvCheckConnection.setOnClickListener(this);

		switchOff = R.drawable.icon_switch_normal;
		switchOn = R.drawable.icon_switch_active;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_check_phone_number:
			setPrivacy(1 - settingBean.phone, settingBean.mail, settingBean.wechat, settingBean.weibo,
					settingBean.renmai);
			break;
		case R.id.tv_check_email:
			setPrivacy(settingBean.phone, 1 - settingBean.mail, settingBean.wechat, settingBean.weibo,
					settingBean.renmai);
			break;
		case R.id.tv_check_weixin_number:
			setPrivacy(settingBean.phone, settingBean.mail, 1 - settingBean.wechat, settingBean.weibo,
					settingBean.renmai);
			break;
		case R.id.tv_check_sina_weibo:
			setPrivacy(settingBean.phone, settingBean.mail, settingBean.wechat, 1 - settingBean.weibo,
					settingBean.renmai);
			break;
		case R.id.tv_check_connection:
			setPrivacy(settingBean.phone, settingBean.mail, settingBean.wechat, settingBean.weibo,
					1 - settingBean.renmai);
			break;
		default:
			break;
		}
	}

	private void setPrivacy(final int phone, final int email, final int weixin, final int weibo, final int renmai) {
		DamiInfo.setPrivacyConfig(phone, email, weixin, weibo, renmai, new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					settingBean.mail = email;
					settingBean.wechat = weixin;
					settingBean.weibo = weibo;
					settingBean.renmai = renmai;
					settingBean.phone = phone;
					bindView();
				} else {
					otherCondition(data.state, PrivacySettingActivity.this);
				}
			}
		});
	}

}
