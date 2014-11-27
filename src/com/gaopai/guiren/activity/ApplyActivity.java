package com.gaopai.guiren.activity;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.ViewUtil;

/**
 * 消息详情界面，包括查看评论以及回复评论等功能
 */
public class ApplyActivity extends BaseActivity {

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
		setInviteNumText(20);
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
		spString.setSpan(new AbsoluteSizeSpan(20, true),
				text1.length(), (text1 + num).length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		spString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.jiav_text_num_light_black)), (text1 + num).length(),
				text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		tvInviteNum.setText(spString);
	}

}
