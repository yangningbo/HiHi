package com.gaopai.guiren.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.SPConst;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.volley.SimpleResponseListener;

//认证界面
public class ReverificationActivity extends BaseActivity {

	private EditText etName;
	private EditText etCompany;
	private TextView etIndustry;
	private EditText etJob;
	private Button btnVerificaion;

	private EditText etEmail;
	private EditText etWeixin;
	private EditText etWeibo;

	private User mUser = null;

	public final static int TYPE_NORMAL = 0;
	public final static int TYPE_SHORT_OF_INTEGRA = 1;

	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_verification_profile);
		type = getIntent().getIntExtra("type", TYPE_NORMAL);
		mUser = DamiCommon.getLoginResult(this);
		if (type == TYPE_NORMAL) {
			mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		}
		mTitleBar.setTitleText(R.string.click_verify);
		if (type == TYPE_SHORT_OF_INTEGRA) {
			mTitleBar.addRightTextView(R.string.jump_over).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					goToRecomendPage();
				}
			});
		}
		etName = (EditText) findViewById(R.id.et_real_name);
		etName.setFilters(MyTextUtils.creatTextLengthFilter(15));
		etCompany = (EditText) findViewById(R.id.et_company);
		etIndustry = (TextView) findViewById(R.id.et_industry);
		etJob = (EditText) findViewById(R.id.et_job);

		etEmail = ViewUtil.findViewById(this, R.id.et_email);
		etWeibo = ViewUtil.findViewById(this, R.id.et_weibo);
		etWeixin = ViewUtil.findViewById(this, R.id.et_weixin);

		btnVerificaion = (Button) findViewById(R.id.btn_verificaition);
		btnVerificaion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String name = etName.getText().toString();
				final String company = etCompany.getText().toString();
				final String industry = etIndustry.getText().toString();
				final String job = etJob.getText().toString();
				final String email = etEmail.getText().toString();
				final String weixin = etWeixin.getText().toString();
				final String weibo = etWeibo.getText().toString();
				if (TextUtils.isEmpty(name.trim()) || TextUtils.isEmpty(company.trim())
						|| TextUtils.isEmpty(industry.trim()) || TextUtils.isEmpty(job.trim())
						|| TextUtils.isEmpty(email.trim())) {
					showToast(R.string.must_input_can_not_be_empty);
					return;
				}
				if (!MyTextUtils.checkIsEmail(email)) {
					showToast(R.string.please_input_correct_email);
					return;
				}
				DamiInfo.reAuth(industry, name, company, job, email, weibo, weixin, new SimpleResponseListener(
						mContext, R.string.request_internet_now) {
					@Override
					public void onSuccess(Object o) {
						BaseNetBean data = (BaseNetBean) o;
						if (data.state != null && data.state.code == 0) {
							mUser.realname = name;
							mUser.company = company;
							mUser.depa = industry;
							mUser.post = job;
							mUser.email = email;
							mUser.weixin = weixin;
							mUser.weibo = weibo;
							DamiCommon.saveLoginResult(mContext, mUser);
							
							Logger.d(this, DamiCommon.getLoginResult(ReverificationActivity.this).realname);
							if (type == TYPE_SHORT_OF_INTEGRA) {
								goToRecomendPage();
								return;
							}
//							sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
							setResult(RESULT_OK);
							ReverificationActivity.this.finish();
						} else {
							otherCondition(data.state, ReverificationActivity.this);
						}
					}
				});
			}
		});

		etName.setText(mUser.realname);
		etCompany.setText(mUser.company);
		etIndustry.setText(mUser.depa);
		etJob.setText(mUser.post);
		etWeibo.setText(mUser.weibo);
		etWeixin.setText(mUser.weixin);
		etEmail.setText(mUser.email);
		findViewById(R.id.layout_industry).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showChoseIndustryDialog();
			}
		});
	}

	private void goToRecomendPage() {
		Logger.d(this, "rec = " + showRecomendPage());
		if (showRecomendPage()) {
			DamiApp.getInstance().getPou().setBoolean(SPConst.getRecKey(mContext), false);
			Intent intent = new Intent(ReverificationActivity.this, RecommendActivity.class);
			startActivity(intent);
		} else {
			sendBroadcast(new Intent(MainActivity.LOGIN_SUCCESS_ACTION));
		}
		ReverificationActivity.this.finish();
	}

	private boolean showRecomendPage() {
		return DamiApp.getInstance().getPou().getBoolean(SPConst.getRecKey(mContext), true);
	}

	private String[] industries = new String[] { "电子商务", "移动互联网", "社交网络", "网络游戏", "大数据", "在线视频", "企业软件", "智能硬件", "金融业",
			"投资", "汽车业", "奢侈品", "房地产", "媒体", "其它" };

	private void showChoseIndustryDialog() {
		showMutiDialog(null, industries, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				etIndustry.setText(industries[which]);
			}
		});
	}

	public static Intent getIntent(Context context) {
		Intent intent = new Intent(context, ReverificationActivity.class);
		intent.putExtra("type", TYPE_SHORT_OF_INTEGRA);
		return intent;
	}

	@Override
	public void onBackPressed() {
		if (type == TYPE_SHORT_OF_INTEGRA) {
			goToRecomendPage();
		} else {
			super.onBackPressed();
		}
	}

}
