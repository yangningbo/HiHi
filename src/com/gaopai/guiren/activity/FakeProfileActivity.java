package com.gaopai.guiren.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;

public class FakeProfileActivity extends BaseActivity implements OnClickListener {

	private TextView tvUserName;
	private TextView tvPhoneNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar(true);
		mTitleBar.setBackgroundColor(Color.TRANSPARENT);
		mTitleBar.setEnableDivider(false);
		setAbContentView(R.layout.activity_fake_profile);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back_in_dark);
		ViewUtil.findViewById(this, R.id.layout_phone).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.btn_invite).setOnClickListener(this);

		tvUserName = ViewUtil.findViewById(this, R.id.tv_name);
		tvPhoneNum = ViewUtil.findViewById(this, R.id.tv_phone_num);
		((TextView) ViewUtil.findViewById(this, R.id.tv_fake_info)).setShadowLayer(4F, 2f, 2f, Color.BLACK);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_phone:
			if (!TextUtils.isEmpty(tvPhoneNum.getText().toString().trim())) {
				MyUtils.makePhonecall(mContext, tvPhoneNum.getText().toString().trim());
			}
			break;
		case R.id.btn_invite:
			startActivity(InviteFriendActivity.class);
			break;

		default:
			break;
		}
	}

}
