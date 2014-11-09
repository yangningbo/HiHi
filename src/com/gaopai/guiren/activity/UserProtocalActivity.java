package com.gaopai.guiren.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;

/**
 * 展示预备信息。 由LoginActivty的用户协议跳转过来<br/>
 * 由SettingActivity的使用帮助跳转过来。
 * 
 */
public class UserProtocalActivity extends BaseActivity {

	private WebView mWebView;
	private int mType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_user_protocal);
		initComponent();
	}

	private void initComponent() {
		mType = getIntent().getIntExtra("type", 0);
		String title = "";
		String path = "";
		if (mType == 0) {
			title = getString(R.string.user_protocol);
			path = "file:///android_asset/user_protocal.html";
		} else if (mType == 1) {
			title = getString(R.string.use_help);
			path = "file:///android_asset/use_help.html";
		}
		mTitleBar.setTitleText(title);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);

		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);

		mWebView.loadUrl(path);
	}

}
