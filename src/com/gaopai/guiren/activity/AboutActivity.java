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

public class AboutActivity extends BaseActivity implements OnClickListener{

	private TextView mVersionText;
	private RelativeLayout mSecretaryLayout, mQrCodeLayout;
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
	
	private void initComponent(){
		
		mTitleBar.setTitleText(R.string.about_us);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		
		mVersionText = (TextView) findViewById(R.id.version);
		mVersionText.setText("V" + FeatureFunction.getAppVersionName(AboutActivity.this));
		mSecretaryLayout = (RelativeLayout) findViewById(R.id.project_secretary_layout);
		mSecretaryLayout.setOnClickListener(this);
		
		mQrCodeLayout = (RelativeLayout) findViewById(R.id.qrlayout);
		mQrCodeLayout.setOnClickListener(this);
		
		mWebsiteView = (TextView) findViewById(R.id.website);
		mWebsiteView.setOnClickListener(this);
		
		mTelView = (TextView) findViewById(R.id.server_tel);
		mTelView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			
		case R.id.project_secretary_layout:
			Intent secretIntent = new Intent(mContext, UserInfoActivity.class);
			secretIntent.putExtra("uid", PROJECT_UID);
			startActivity(secretIntent);
			break;
			
		case R.id.qrlayout:
//			Intent qrIntent = new Intent(mContext, QrcodeImgActivity.class);
//			qrIntent.putExtra("title", mContext.getString(R.string.download_app_address_qr_code));
//			qrIntent.putExtra("imageurl", DOWNLOAD_URL);
//			startActivity(qrIntent);
			break;
			
		case R.id.website:
			showBrowser(mContext.getString(R.string.website));
			break;
			
		case R.id.server_tel:
			try {
				
				Intent Telintent = new Intent();
				Telintent.setAction(Intent.ACTION_CALL);
				Telintent.setData(Uri.parse("tel:"+ mContext.getString(R.string.server_tel)));
				mContext.startActivity(Telintent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}
	
	private void showBrowser(String url){
		try {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW , uri); 
			intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");   
			mContext.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
