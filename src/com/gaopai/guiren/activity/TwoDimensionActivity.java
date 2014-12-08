package com.gaopai.guiren.activity;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.ViewUtil;
import com.squareup.picasso.Picasso;

public class TwoDimensionActivity extends BaseActivity {
	private TextView tvUserName;
	private TextView tvUserInfo;
	private TextView tvUserStar;
	private ImageView ivHeader;
	private ImageView ivErWeima;
	private ImageView ivHeader1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_erweima);
		qrHolder = (QrHolder) getIntent().getSerializableExtra("qrholder");
		if (qrHolder == null) {
			this.finish();
			return;
		}
		mTitleBar.setTitleText(qrHolder.titleText).setTextColor(getResources().getColor(R.color.white));
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

	public static Intent getIntent(Context context, QrHolder qrHolder) {
		Intent intent = new Intent(context, TwoDimensionActivity.class);
		intent.putExtra("qrholder", qrHolder);
		return intent;
	}

	public static Intent getIntent(Context context, String headsmall, String name, String info, String integral,
			String codeurl, String titleText) {
		Intent intent = new Intent(context, TwoDimensionActivity.class);
		intent.putExtra("qrholder",
				TwoDimensionActivity.getQrHolder(headsmall, name, info, integral, codeurl, titleText));
		return intent;
	}

	public static Intent getIntent(Context context, Tribe mTribe) {
		Intent intent = new Intent(context, TwoDimensionActivity.class);
		intent.putExtra("qrholder", TwoDimensionActivity.getQrHolder(mTribe.logosmall, mTribe.name, mTribe.content, "",
				mTribe.codeurl, "圈子二维码"));
		return intent;
	}

	public static Intent getIntent(Context context, User user) {
		Intent intent = new Intent(context, TwoDimensionActivity.class);
		intent.putExtra(
				"qrholder",
				TwoDimensionActivity.getQrHolder(user.headsmall, user.realname, user.company,
						String.valueOf(user.integral), user.codeurl, "我的二维码"));
		return intent;
	}

	private QrHolder qrHolder;

	public static class QrHolder implements Serializable {
		public String headsmall;
		public String name;
		public String info;
		public String integral;// just for people
		public String codeurl;
		public String titleText;
	}

	public static QrHolder getQrHolder(String headsmall, String name, String info, String integral, String codeurl,
			String titleText) {
		QrHolder qrHolder = new QrHolder();
		qrHolder.codeurl = codeurl;
		qrHolder.headsmall = headsmall;
		qrHolder.name = name;
		qrHolder.info = info;
		qrHolder.integral = integral;
		qrHolder.titleText = titleText;
		return qrHolder;
	}

	private void bindBasicView() {
		if (!TextUtils.isEmpty(qrHolder.headsmall)) {
			Picasso.with(mContext).load(qrHolder.headsmall).placeholder(R.drawable.default_header)
					.error(R.drawable.default_header).into(ivHeader);
			Picasso.with(mContext).load(qrHolder.headsmall).placeholder(R.drawable.default_header)
					.error(R.drawable.default_header).into(ivHeader1);
		}
		if (!TextUtils.isEmpty(qrHolder.integral)) {
			tvUserStar.setText(qrHolder.integral);
		} else {
			tvUserStar.setVisibility(View.GONE);
			ViewUtil.findViewById(this, R.id.tv_meilizhi_info).setVisibility(View.GONE);
		}
		tvUserName.setText(qrHolder.name);
		tvUserInfo.setText(qrHolder.info);
		if (!TextUtils.isEmpty(qrHolder.codeurl)) {
			Picasso.with(mContext).load(qrHolder.codeurl).placeholder(R.drawable.default_header)
					.error(R.drawable.default_header).into(ivErWeima);
		}
	}

	// private void bindBasicView() {
	// if (!TextUtils.isEmpty(user.headsmall)) {
	// Picasso.with(mContext).load(user.headsmall).placeholder(R.drawable.default_header)
	// .error(R.drawable.default_header).into(ivHeader);
	// Picasso.with(mContext).load(user.headsmall).placeholder(R.drawable.default_header)
	// .error(R.drawable.default_header).into(ivHeader1);
	// }
	// tvUserStar.setText(String.valueOf(user.integral));
	// tvUserName.setText(user.realname);
	// tvUserInfo.setText(user.company);
	// if (!TextUtils.isEmpty(user.codeurl)) {
	// Picasso.with(mContext).load(user.codeurl).placeholder(R.drawable.default_header)
	// .error(R.drawable.default_header).into(ivErWeima);
	// }
	// }
}
