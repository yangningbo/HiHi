package com.gaopai.guiren.activity;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.support.view.HeadView;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.ViewUtil;

public class TwoDimensionActivity extends BaseActivity {
	private TextView tvUserName;
	private TextView tvUserInfo;
	private TextView tvUserStar;
	private ImageView ivHeader;
	private ImageView ivErWeima;
	private ImageView ivHeader1;
	private HeadView layoutHeader;

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
		if (qrHolder.type == 1) {
			mTitleBar.setTitleText("圈子二维码");
		} else {
			mTitleBar.setTitleText("我的二维码");
		}
		tvUserName = ViewUtil.findViewById(this, R.id.tv_user_name);
		tvUserInfo = ViewUtil.findViewById(this, R.id.tv_user_info);
		ivHeader = ViewUtil.findViewById(this, R.id.iv_header);
		ivHeader1 = ViewUtil.findViewById(this, R.id.iv_header1);
		ivErWeima = ViewUtil.findViewById(this, R.id.iv_erweima);
		layoutHeader = ViewUtil.findViewById(this, R.id.layout_header_mvp);
		bindBasicView();
	}

	public static Intent getIntent(Context context, Tribe mTribe) {
		Intent intent = new Intent(context, TwoDimensionActivity.class);
		intent.putExtra("qrholder", TwoDimensionActivity.getQrHolder(mTribe));
		return intent;
	}

	public static Intent getIntent(Context context, User user) {
		Intent intent = new Intent(context, TwoDimensionActivity.class);
		intent.putExtra("qrholder", TwoDimensionActivity.getQrHolder(user));
		return intent;
	}

	private QrHolder qrHolder;

	public static class QrHolder implements Serializable {
		public int type;// 0 user 1 tribe
		public Tribe tribe;
		public User user;
		public String titleText;
	}

	public static QrHolder getQrHolder(Tribe tribe) {
		QrHolder qrHolder = new QrHolder();
		qrHolder.type = 1;
		qrHolder.tribe = tribe;
		return qrHolder;
	}

	public static QrHolder getQrHolder(User user) {
		QrHolder qrHolder = new QrHolder();
		qrHolder.type = 0;
		qrHolder.user = user;
		return qrHolder;
	}

	private void bindBasicView() {
		if (qrHolder.type == 0) {
			User user = qrHolder.user;
			if (user == null) {
				return;
			}
			ImageLoaderUtil.displayImage(user.headsmall, ivHeader, R.drawable.default_header);
			ImageLoaderUtil.displayImage(user.headsmall, ivHeader1, R.drawable.default_header);
			bindUserName(user);
			tvUserInfo.setText(user.post);
			ImageLoaderUtil.displayImage(user.codeurl, ivErWeima, R.drawable.default_header);
			layoutHeader.setImage(user.headsmall);
			if (user.bigv == 1) {
				layoutHeader.setMVP(true);
			} else {
				layoutHeader.setMVP(false);
			}
		} else {
			Tribe tribe = qrHolder.tribe;
			if (tribe == null) {
				return;
			}
			ImageLoaderUtil.displayImage(tribe.logosmall, ivHeader, R.drawable.default_header);
			ImageLoaderUtil.displayImage(tribe.logosmall, ivHeader1, R.drawable.default_header);
			bindTribeName(tribe);
			tvUserInfo.setText(tribe.content);
			ImageLoaderUtil.displayImage(tribe.codeurl, ivErWeima, R.drawable.default_tribe);
		}
	}

	private void bindTribeName(Tribe tribe) {
		SpannableString name = new SpannableString(tribe.name);
		MyTextUtils.setTextSize(name, 22);
		MyTextUtils.setTextColor(name, getResources().getColor(R.color.general_text_black));
		tvUserName.setText(name);
	}

	private void bindUserName(User tUser) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		SpannableString name;
		if (tUser.bigv == 1) {
			name = new SpannableString(User.getSubUserName(tUser, mContext, 3) + HeadView.MVP_NAME_STR);
			HeadView.getMvpName(mContext, name);
		} else {
			name = new SpannableString(User.getSubUserName(tUser, mContext, 3));
		}
		MyTextUtils.setTextSize(name, 20);
		MyTextUtils.setTextColor(name, getResources().getColor(R.color.white));

		SpannableString meiliInfo = new SpannableString("  " + getString(R.string.level_count));
		MyTextUtils.setTextSize(meiliInfo, 16);
		MyTextUtils.setTextColor(meiliInfo, getResources().getColor(R.color.white));

		SpannableString integra = new SpannableString(String.valueOf(tUser.integral));
		MyTextUtils.setTextSize(integra, 18);
		MyTextUtils.setTextColor(integra, getResources().getColor(R.color.red_dongtai_bg));

		tvUserName.setText(builder.append(name).append(meiliInfo).append(integra));
	}
}
