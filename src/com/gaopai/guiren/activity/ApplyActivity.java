package com.gaopai.guiren.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.support.view.ProgressView;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class ApplyActivity extends BaseActivity implements OnClickListener {

	private TextView tvInviteNum;
	private TextView tvRedPercent;
	private ProgressView pvJiaV;

	private Button btnJiav;
	private Button btnInvite;

	private int requiredTotal = 20;

	private User mLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_jiav);
		addLoadingView();
		showLoadingView();
		mTitleBar.setTitleText(R.string.jiav_title);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		tvInviteNum = ViewUtil.findViewById(this, R.id.tv_invite_num);
		tvRedPercent = ViewUtil.findViewById(this, R.id.tv_jiav_process);
		MyTextUtils.changeToBold(tvRedPercent);

		ViewUtil.findViewById(this, R.id.btn_fill_profile).setOnClickListener(this);
		btnInvite = ViewUtil.findViewById(this, R.id.btn_invite_to_guiren);
		btnInvite.setOnClickListener(this);

		btnJiav = ViewUtil.findViewById(this, R.id.btn_confirm);
		btnJiav.setOnClickListener(this);
		btnJiav.setEnabled(false);

		pvJiaV = (ProgressView) findViewById(R.id.tv_invite_num_1);
		mLogin = DamiCommon.getLoginResult(mContext);
		getVerifyDetail();
	}

	GetVerifyResult.Data bean;

	private void getVerifyDetail() {
		DamiInfo.getVerifyResult(new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				GetVerifyResult data = (GetVerifyResult) o;
				if (data.state != null && data.state.code == 0) {
					showContent();
					bean = data.data;
					bindView(bean);
				} else {
					showErrorView();
					otherCondition(data.state, ApplyActivity.this);
				}
			}

			@Override
			public void onFailure(Object o) {
				showErrorView();
			}
		});
	}

	private void showErrorView() {
		showErrorView(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getVerifyDetail();
				showLoadingView();
			}
		});
	}

	private void bindView(GetVerifyResult.Data data) {
		try {
			setInviteNumText(data.invite.num);
			int percent = data.base.iscomplete * 20 + data.invite.num * (80 / requiredTotal);
			if (percent > 100) {
				percent = 100;
			}
			pvJiaV.setProgress(percent);
			if (data.base.iscomplete == 1) {
				btnInvite.setEnabled(true);
			} else {
				btnInvite.setEnabled(false);
			}
			tvRedPercent.setText(percent + "%");
			if (percent == 100) {
				if (mLogin.bigv == 0) {
					btnJiav.setEnabled(true);
				} else {
					btnJiav.setEnabled(true);
					btnJiav.setText(R.string.jiav_success);
					btnJiav.setOnClickListener(null);
				}
			}
		} catch (NullPointerException e) {
		}

	}

	private void setInviteNumText(int num) {
		String text1 = "已邀请";
		String text2 = "位好友";
		String text = text1 + num + text2;
		SpannableString spString = new SpannableString(text);
		spString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.jiav_text_num_light_black)), 0,
				text1.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		spString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.jiav_text_num_highlight)),
				text1.length(), (text1 + num).length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		spString.setSpan(new AbsoluteSizeSpan(20, true), text1.length(), (text1 + num).length(),
				Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		spString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.jiav_text_num_light_black)),
				(text1 + num).length(), text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		tvInviteNum.setText(spString);
	}

	public final static int REQUEST_VERIFY_PROFILE = 2;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_fill_profile:
			startActivityForResult(ReverificationActivity.class, REQUEST_VERIFY_PROFILE);
			break;
		case R.id.btn_invite_to_guiren:
			if (bean.base.iscomplete == 0) {
				showToast(R.string.please_finish_profile);
				return;
			}
			startActivity(InviteFriendActivity.class);
			break;
		case R.id.btn_confirm:
			DamiInfo.setUserAuthV(new SimpleResponseListener(mContext, R.string.request_internet_now) {
				@Override
				public void onSuccess(Object o) {
					BaseNetBean data = (BaseNetBean) o;
					if (data.state != null && data.state.code == 0) {
						User mLoginUser = DamiCommon.getLoginResult(mContext);
						mLoginUser.bigv = 1;
						DamiCommon.saveLoginResult(mContext, mLoginUser);
						sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
						btnJiav.setText(R.string.jiav_success);
						btnJiav.setOnClickListener(null);
					} else {
						otherCondition(data.state, ApplyActivity.this);
					}
				}
			});
			break;

		default:
			break;
		}
	}

	public static class GetVerifyResult extends BaseNetBean {
		public Data data;

		public static class Data {
			public Base base;
			public Invite invite;
		}

		public static class Base {
			public int iscomplete;
			public Case item;
		}

		public static class Case {
			public int realname;
			public int company;
			public int depa;
			public int post;
			public String result;
		}

		public static class Invite {
			public int num;
			public int totalnum;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_VERIFY_PROFILE) {
				getVerifyDetail();
			}
		}
	}
}
