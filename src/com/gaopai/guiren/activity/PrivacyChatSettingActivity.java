package com.gaopai.guiren.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;

public class PrivacyChatSettingActivity extends BaseActivity implements OnClickListener {
	private TextView tvReport;
	private TextView tvClearMsg;
	private TextView tvAvoidDisturb;
	
	private String uid;
	public final static String KEY_UID = "uid";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_privacy_chat_setting);
		uid = getIntent().getStringExtra(KEY_UID);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.private_chat_setting);

		tvAvoidDisturb = (TextView) findViewById(R.id.tv_avoid_disturb);
		tvAvoidDisturb.setOnClickListener(this);
		tvClearMsg = (TextView) findViewById(R.id.tv_clear_local_msg);
		tvClearMsg.setOnClickListener(this);
		tvReport = (TextView) findViewById(R.id.tv_report);
		tvReport.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_avoid_disturb:
			break;
		case R.id.tv_clear_local_msg:
			break;
		case R.id.tv_report:
			break;
		default:
			break;
		}
	}

}
