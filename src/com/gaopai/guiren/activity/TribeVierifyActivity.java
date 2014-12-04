package com.gaopai.guiren.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class TribeVierifyActivity extends BaseActivity implements OnClickListener {

	private EditText etPassword;
	private EditText etSaySomething;
	private View layoutGrid;

	private TextView tvInfo;

	private Tribe tribe;
	private int type;// 0tribe 1meeting
	private SimpleResponseListener resultListener;
	private SimpleResponseListener enterListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_tribe_verification);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.apply_join);
		ViewUtil.findViewById(this, R.id.btn_enter).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.btn_confirm).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.btn_hide_grid).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.btn_cancel).setOnClickListener(this);
		tvInfo = ViewUtil.findViewById(this, R.id.tv_info);
		layoutGrid = ViewUtil.findViewById(this, R.id.layout_enter_tribe_window);
		layoutGrid.setVisibility(View.GONE);
		etPassword = ViewUtil.findViewById(this, R.id.et_enter_password);
		etSaySomething = ViewUtil.findViewById(this, R.id.et_say_something_to_manager);
		tribe = (Tribe) getIntent().getSerializableExtra("tribe");
		type = getIntent().getIntExtra("type", 0);

		tvInfo.setOnClickListener(this);
		setInfoRichText();

		resultListener = new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					showToast(R.string.apply_success);
					TribeVierifyActivity.this.finish();
				} else {
					otherCondition(data.state, TribeVierifyActivity.this);
				}
			}
		};
		enterListener = new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					if (type == 0) {
						startActivity(ChatTribeActivity.getIntent(mContext, tribe, ChatTribeActivity.CHAT_TYPE_TRIBE));
					} else {
						startActivity(ChatTribeActivity.getIntent(mContext, tribe, ChatTribeActivity.CHAT_TYPE_MEETING));
					}
					TribeVierifyActivity.this.finish();
				} else {
					otherCondition(data.state, TribeVierifyActivity.this);
				}
			}
		};

	}

	private void setInfoRichText() {
		String text1 = "不知道圈子密码？";
		String text2 = "申请加入圈子";
		if (type == 1) {
			text1 = "不知道会议密码？";
			text2 = "申请加入会议";
		}
		String text3 = "点击这里";
		String text = text1 + text3 + text2;
		SpannableString spString = new SpannableString(text);
		spString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.general_text_gray)), 0,
				text1.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		spString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red_dongtai_bg)), text1.length(),
				(text1 + text3).length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		spString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.general_text_gray)),
				(text1 + text3).length(), text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		tvInfo.setText(spString);
	}

	public static Intent getIntent(Context context, Tribe tribe, int type) {
		Intent intent = new Intent(context, TribeVierifyActivity.class);
		intent.putExtra("tribe", tribe);
		intent.putExtra("type", type);
		return intent;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_enter:
			if (TextUtils.isEmpty(etPassword.getText().toString())) {
				showToast(R.string.input_can_not_be_empty);
				return;
			}
			if (type == 1) {
				DamiInfo.joinMeetingByPassword(tribe.id, etPassword.getText().toString(), enterListener);
			} else {
				DamiInfo.joinTribeByPasswd(tribe.id, etPassword.getText().toString(), enterListener);
			}
			break;
		case R.id.btn_confirm:
			if (TextUtils.isEmpty(etSaySomething.getText().toString())) {
				showToast(R.string.input_can_not_be_empty);
				return;
			}
			if (type == 0) {
				DamiInfo.applyTribe(tribe.id, etSaySomething.getText().toString(), resultListener);
			} else {
				DamiInfo.applyMeeting(tribe.id, etSaySomething.getText().toString(), resultListener);
			}
			break;
		case R.id.btn_hide_grid:
		case R.id.btn_cancel:
			slideOut();
			break;
		case R.id.tv_info:
			slideIn();
			break;
		default:
			break;
		}
	}

	private void slideIn() {
		layoutGrid.clearAnimation();
		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_bottom);
		layoutGrid.setAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				layoutGrid.setVisibility(View.VISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
			}
		});
		animation.start();
	}

	private void slideOut() {
		layoutGrid.clearAnimation();
		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_to_bottom);
		layoutGrid.setAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				layoutGrid.setVisibility(View.GONE);
			}
		});
		animation.start();
	}
}
