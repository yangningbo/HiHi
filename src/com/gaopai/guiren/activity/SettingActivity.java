package com.gaopai.guiren.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.MyUtils;

public class SettingActivity extends BaseActivity implements OnClickListener {

	private TextView tvClearCache;
	private TextView tvPrivateSetting;
	private Button btnLoginOut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_setting);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.setting);
		tvClearCache = (TextView) findViewById(R.id.tv_clear_cache);
		tvClearCache.setOnClickListener(this);
		
		btnLoginOut = (Button) findViewById(R.id.btn_login_out);
		btnLoginOut.setOnClickListener(this);
		tvPrivateSetting = (TextView) findViewById(R.id.tv_privacy_setting);
		tvPrivateSetting.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ab_setting: {
			startActivity(SettingActivity.class);
			break;
		}
		case R.id.tv_clear_cache:
			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.sendEmptyMessage(0);
					MyUtils.clearCache(mContext);
					mHandler.sendEmptyMessage(1);
				}
			}).start();
			break;
		case R.id.btn_login_out:
			SharedPreferences preferences = mContext.getSharedPreferences(DamiCommon.LOGIN_SHARED, 0);
			Editor editor = preferences.edit();
			editor.remove(DamiCommon.LOGIN_RESULT);
			editor.commit();
			DamiCommon.setUid("");
			DamiCommon.setToken("");
			FeatureFunction.stopService(mContext);
			sendBroadcast(new Intent(MainActivity.ACTION_LOGIN_OUT));
			SettingActivity.this.finish();
			NotificationManager notificationManager = (NotificationManager) mContext
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancelAll();
			break;
		case R.id.tv_privacy_setting:
			startActivity(PrivacySettingActivity.class);
			break;
		default:
			break;
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				showProgressDialog("正在清空缓存...");
				break;
			case 1:
				removeProgressDialog();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

}
