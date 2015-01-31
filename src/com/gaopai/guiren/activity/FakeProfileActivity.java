package com.gaopai.guiren.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.InviteFriendActivity.InviteUrlResult;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class FakeProfileActivity extends BaseActivity implements OnClickListener {

	private TextView tvUserName;
	private TextView tvPhoneNum;

	private User user;

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
		user = (User) getIntent().getSerializableExtra("user");
		tvUserName = ViewUtil.findViewById(this, R.id.tv_name);
		tvPhoneNum = ViewUtil.findViewById(this, R.id.tv_phone_num);
		tvUserName.setText(user.realname);
		tvUserName.setShadowLayer(4F, 2f, 2f, Color.BLACK);
		tvPhoneNum.setText(user.phone);
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
			if (!User.checkCanInvite(DamiCommon.getLoginResult(mContext), FakeProfileActivity.this)) {
				return;
			}
			getInviteUrl(mContext, user.phone);
			break;

		default:
			break;
		}
	}

	public static Intent getIntent(Context context, User user) {
		Intent intent = new Intent(context, FakeProfileActivity.class);
		intent.putExtra("user", user);
		return intent;
	}

	public static void getInviteUrl(final Context mContext, final String phone) {
		DamiInfo.getUserInvitation(new SimpleResponseListener(mContext, R.string.request_share_url) {
			@Override
			public void onSuccess(Object o) {
				InviteUrlResult data = (InviteUrlResult) o;
				if (data.state != null && data.state.code == 0) {
					String shareStr = mContext.getString(R.string.invite_str_1);
					User mLogin = DamiCommon.getLoginResult(mContext);
					if (mLogin != null) {
						shareStr = String.format(mContext.getString(R.string.invite_str_fake),
								User.getUserName(mLogin), mLogin.company, mLogin.post);
					}
					if (!TextUtils.isEmpty(data.data)) {
						shareStr = shareStr + data.data;
					} else {
						return;
					}
					MyUtils.sendSms(mContext, phone, shareStr);
				} else {
					otherCondition(data.state, (Activity) mContext);
				}
			}
		});
	}
}
