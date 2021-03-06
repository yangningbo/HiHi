package com.gaopai.guiren.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.UpdateManager;
import com.gaopai.guiren.utils.ViewUtil;

public class SettingActivity extends BaseActivity implements OnClickListener {

	private TextView tvCacheSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Logger.d(this, "save= null   " + (savedInstanceState == null));
		initTitleBar();
		setAbContentView(R.layout.activity_setting);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.setting);

		ViewUtil.findViewById(this, R.id.tv_check_update).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_privacy_setting).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.btn_login_out).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_receive_msg_background).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_clear_cache).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_help).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_send_feedback).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_about_us).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_help).setOnClickListener(this);
		ViewUtil.findViewById(this, R.id.tv_send_prise).setOnClickListener(this);
		tvCacheSize = ViewUtil.findViewById(this, R.id.tv_cache_size);

		new Thread(new Runnable() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 2;
				message.obj = MyUtils.getCacheSize(mContext);
				mHandler.sendMessage(message);
			}
		}).start();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_receive_msg_background:
			startActivity(NotifySettingActivity.class);
			break;
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
			showExitDialog();
			break;
		case R.id.tv_privacy_setting:
			startActivity(PrivacySettingActivity.class);
			break;
		case R.id.tv_check_update:
			if (DamiCommon.verifyNetwork(mContext)) { // 检查版本更新
				UpdateManager.getUpdateManager().checkAppUpdate(SettingActivity.this, true);
			}
			break;
		case R.id.tv_help:
			startActivity(UserProtocalActivity.getIntent(mContext, 1));
			break;
		case R.id.tv_send_feedback:
			Intent intent = new Intent();
			intent.putExtra(CommentGeneralActivity.KEY_TYPE, CommentGeneralActivity.TYPE_FEED_BACK);
			intent.setClass(mContext, CommentGeneralActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_about_us:
			Intent aboutIntent = new Intent(mContext, AboutActivity.class);
			startActivity(aboutIntent);
			break;
		case R.id.tv_send_prise:
			giveHaoping();
			break;

		default:
			break;
		}
	}

	private void giveHaoping() {
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			mContext.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				showProgressDialog(R.string.clear_cache);
				break;
			case 1:
				removeProgressDialog();
				tvCacheSize.setText("0.00Mb");
				showToast(R.string.clear_cache_success);
				break;
			case 2:
				tvCacheSize.setText(msg.obj + "Mb");
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void showExitDialog() {
		showDialog(getString(R.string.confirm_exit), null, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DamiCommon.removeUser(mContext);
				FeatureFunction.stopService(mContext);
				sendBroadcast(new Intent(MainActivity.ACTION_LOGIN_OUT));
				sendBroadcast(new Intent(ACTION_FINISH));
				SettingActivity.this.finish();
				NotificationManager notificationManager = (NotificationManager) mContext
						.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.cancelAll();
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Logger.d(this, "onSaveInstanceState");
	}
	
	

}
