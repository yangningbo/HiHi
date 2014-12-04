package com.gaopai.guiren.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.QrCordBean.QrCodeResult;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.utils.ViewUtil;
import com.squareup.picasso.Picasso;

public class TwoDimensionActivity extends BaseActivity {
	private TextView tvUserName;
	private TextView tvUserInfo;
	private TextView tvUserStar;
	private ImageView ivHeader;
	private ImageView ivErWeima;
	private ImageView ivHeader1;

	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_erweima);
		user = (User) getIntent().getSerializableExtra("user");
		mTitleBar.setTitleText("我的二维码").setTextColor(getResources().getColor(R.color.white));
		mTitleBar.setBackgroundColor(getResources().getColor(R.color.black));
		mTitleBar.setLogo(R.drawable.selector_titlebar_back_in_dark);

		tvUserName = ViewUtil.findViewById(this, R.id.tv_user_name);
		tvUserInfo = ViewUtil.findViewById(this, R.id.tv_user_info);
		tvUserStar = ViewUtil.findViewById(this, R.id.tv_star_number);
		ivHeader = ViewUtil.findViewById(this, R.id.iv_header);
		ivHeader1 = ViewUtil.findViewById(this, R.id.iv_header1);
		ivErWeima = ViewUtil.findViewById(this, R.id.iv_erweima);
		bindBasicView();
	}

	QrCodeResult qrCodeResult;


	private void bindBasicView() {
		if (!TextUtils.isEmpty(user.headsmall)) {
			Picasso.with(mContext).load(user.headsmall).placeholder(R.drawable.default_header)
					.error(R.drawable.default_header).into(ivHeader);
			Picasso.with(mContext).load(user.headsmall).placeholder(R.drawable.default_header)
					.error(R.drawable.default_header).into(ivHeader1);
		}
		tvUserStar.setText(String.valueOf(user.integral));
		tvUserName.setText(user.realname);
		tvUserInfo.setText(user.company);
		if (!TextUtils.isEmpty(user.codeurl)) {
			Picasso.with(mContext).load(user.codeurl).placeholder(R.drawable.default_header)
					.error(R.drawable.default_header).into(ivErWeima);
		}
	}
}
