package com.gaopai.guiren.activity;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.InviteNumResult;
import com.gaopai.guiren.bean.InviteNumResult.InviteNumberBean;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class ApplyActivity extends BaseActivity implements OnClickListener {

	private TextView tvInviteNum;
	private TextView tvRedPercent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_jiav);
		mTitleBar.setTitleText(R.string.jiav_title);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		tvInviteNum = ViewUtil.findViewById(this, R.id.tv_invite_num);
		tvRedPercent = ViewUtil.findViewById(this, R.id.tv_jiav_process);
		MyTextUtils.changeToBold(tvRedPercent);

		ViewUtil.findViewById(this, R.id.btn_fill_profile).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.btn_invite_to_guiren).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.btn_confirm).setOnClickListener(this);
		getInviteUserNum();
	}

	private void getInviteUserNum() {
		// TODO Auto-generated method stub
		DamiInfo.getUserInvitationNum(new SimpleResponseListener(mContext) {
			
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				InviteNumResult data = (InviteNumResult) o;
				if (data.state != null && data.state.code == 0) {
					InviteNumberBean bean = data.data;
					setInviteNumText(bean.complete);
				}
			}
		});
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
			invite();
			break;
		case R.id.btn_confirm:
			break;

		default:
			break;
		}
	}

	private void invite() {
		DamiInfo.getUserInvitation(new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				InviteUrlResult data = (InviteUrlResult) o;
				if (data.state != null && data.state.code == 0) {
					startActivity(InviteFriendActivity.getIntent(mContext, data.data));
				}
			}
		});
	}

	public static class InviteUrlResult extends BaseNetBean {
		public String data;
	}
}
