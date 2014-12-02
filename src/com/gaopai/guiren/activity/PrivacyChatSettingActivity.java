package com.gaopai.guiren.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.support.MessageHelper;
import com.gaopai.guiren.support.MessageHelper.DeleteCallback;
import com.gaopai.guiren.utils.PreferenceOperateUtils;
import com.gaopai.guiren.utils.SPConst;

public class PrivacyChatSettingActivity extends BaseActivity implements OnClickListener {
	private TextView tvReport;
	private TextView tvClearMsg;
	private TextView tvAvoidDisturb;

	private String uid;
	public final static String KEY_UID = "uid";

	private PreferenceOperateUtils po;
	private int switchOn;
	private int switchOff;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_privacy_chat_setting);
		uid = getIntent().getStringExtra(KEY_UID);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.private_chat_setting);

		po = new PreferenceOperateUtils(mContext, SPConst.SP_AVOID_DISTURB);
		switchOff = R.drawable.icon_switch_normal;
		switchOn = R.drawable.icon_switch_active;

		tvAvoidDisturb = (TextView) findViewById(R.id.tv_avoid_disturb);
		tvAvoidDisturb.setOnClickListener(this);
		tvClearMsg = (TextView) findViewById(R.id.tv_clear_local_msg);
		tvClearMsg.setOnClickListener(this);
		tvReport = (TextView) findViewById(R.id.tv_report);
		tvReport.setOnClickListener(this);
		bindView();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_avoid_disturb: {
			int set = po.getInt(SPConst.getTribeUserId(mContext, uid), 0);
			po.setInt(SPConst.getTribeUserId(mContext, uid), 1 - set);
			setSwitchState(tvAvoidDisturb, 1 - set);
			break;
		}
		case R.id.tv_clear_local_msg:
			MessageHelper.clearChatCache(mContext, uid, 100, deleteCallback);
			break;
		case R.id.tv_report:
			startActivity(ReportPeopleActivity.getIntent(mContext, uid));
			break;
		default:
			break;
		}
	}
	
	private DeleteCallback deleteCallback = new DeleteCallback() {
		@Override
		public void onStart() {
			showProgressDialog(R.string.clear_cache_now);
		}

		@Override
		public void onEnd() {
			removeProgressDialog();
			showToast(R.string.clear_cache_success);
		}
	};

	private void bindView() {
		int set = po.getInt(SPConst.getTribeUserId(mContext, uid), 0);
		setSwitchState(tvAvoidDisturb, set);
	}

	private void setSwitchState(TextView textView, int state) {
		textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, (state == 0) ? switchOff : switchOn, 0);
	}
}
