package com.gaopai.guiren.activity;

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
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.ApplyActivity.GetVerifyResult.Case;
import com.gaopai.guiren.activity.ApplyActivity.GetVerifyResult.Data;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.support.view.ProgressView;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class ApplyActivity extends BaseActivity implements OnClickListener {

	private TextView tvInviteNum;
	private TextView tvRedPercent;
	private ProgressView pvJiaV;

	private Button btnJiav;

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
		ViewUtil.findViewById(this, R.id.btn_invite_to_guiren).setOnClickListener(this);

		btnJiav = ViewUtil.findViewById(this, R.id.btn_confirm);
		btnJiav.setOnClickListener(this);
		btnJiav.setEnabled(false);

		pvJiaV = (ProgressView) findViewById(R.id.tv_invite_num_1);
		// getInviteUserNum();
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
		setInviteNumText(data.invite.num);
		int percent = data.base.iscomplete * 20 + data.invite.num * 4;
		pvJiaV.setProgress(percent);
		tvRedPercent.setText(percent + "%");
		if (percent == 100) {
			btnJiav.setEnabled(true);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_fill_profile:
			startActivity(ReverificationActivity.class);
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
						// showToast(R.string.)
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

	// "data": {
	// "base": { //认证资料
	// "iscomplete": 0, //是否完成，0没有完成，1完成
	// "case": {
	// "realname": 1, //真实姓名是否填写，0没有填写，1填写
	// "company": 1, //公司是否填写，0没有填写，1填写
	// "depa": 0, //部门是否填写，0没有填写，1填写
	// "post": 1 //职位/职业是否填写，0没有填写，1填写
	// }
	// },
	// "invite": { //邀请好友数据
	// "num": "0", //已完成的邀请数据
	// "totalnum": 20 //需邀请的用户总数
	// }
	// },

	// "data": {
	// "base": {
	// "iscomplete": 0,
	// "item": {
	// "result": "等待审核",
	// "realname": 1,
	// "company": 1,
	// "depa": 0,
	// "post": 1
	// }
	// },
	// "invite": {
	// "num": "0",
	// "totalnum": 20
	// }
	// },
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
}
