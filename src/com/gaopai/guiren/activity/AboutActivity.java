package com.gaopai.guiren.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;

/**
 * 关于我们界面，由settingActivity跳转过来
 * 
 */

public class AboutActivity extends BaseActivity implements OnClickListener {

	private TextView mVersionText;
	private View mSecretaryLayout, tvQrCode;
	private TextView mWebsiteView, mTelView;

	private final static String PROJECT_UID = "1";
	private final static String DOWNLOAD_URL = "http://guirenhui.cn";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_about);
		initComponent();
	}

	private void initComponent() {

		mTitleBar.setTitleText(R.string.about_us);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);

		mVersionText = (TextView) findViewById(R.id.version);
		mVersionText.setText("V" + FeatureFunction.getAppVersionName(AboutActivity.this));
		mSecretaryLayout = findViewById(R.id.project_secretary_layout);
		mSecretaryLayout.setOnClickListener(this);

		tvQrCode = findViewById(R.id.tv_qrcode);
		tvQrCode.setOnClickListener(this);

		mWebsiteView = (TextView) findViewById(R.id.website);
		mWebsiteView.setOnClickListener(this);

		mTelView = (TextView) findViewById(R.id.server_tel);
		mTelView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.project_secretary_layout:
			startActivity(ProfileActivity.getIntent(mContext, PROJECT_UID));
			break;

		case R.id.tv_qrcode:
			startActivity(WebActivity.getIntent(mContext, DOWNLOAD_URL,
					getString(R.string.download_app_address_qr_code)));
			break;

		case R.id.website:
			startActivity(WebActivity.getIntent(mContext, DOWNLOAD_URL, getString(R.string.website)));
			break;

		case R.id.server_tel:
			try {
				Intent Telintent = new Intent();
				Telintent.setAction(Intent.ACTION_CALL);
				Telintent.setData(Uri.parse("tel:" + mContext.getString(R.string.server_tel)));
				mContext.startActivity(Telintent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}
}
