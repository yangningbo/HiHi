package com.gaopai.guiren.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends BaseActivity implements OnClickListener {

	private User mUser;

	private ImageView ivHeader;
	private TextView tvUserName;
	private TextView tvUserInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_profile);

		mTitleBar.setTitleText(getString(R.string.profile));
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		View setting = mTitleBar.addRightImageButtonView(android.R.drawable.ic_menu_send);
		setting.setId(R.id.ab_setting);
		setting.setOnClickListener(this);

		ivHeader = (ImageView) findViewById(R.id.iv_header);
		tvUserInfo = (TextView) findViewById(R.id.tv_user_info);
		tvUserName = (TextView) findViewById(R.id.tv_user_name);

		mUser = DamiCommon.getLoginResult(this);

		Picasso.with(mContext).load(mUser.headsmall).placeholder(R.drawable.default_header)
				.error(R.drawable.default_header).into(ivHeader);

		tvUserInfo.setText(mUser.realname);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ab_setting: {
			startActivity(SettingActivity.class);
			break;
		}
		default:
			break;
		}
	}

}
