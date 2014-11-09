package com.gaopai.guiren.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;

public class PrivacySettingActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_privacy_setting);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.private_setting);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		default:
			break;
		}
	}

}
