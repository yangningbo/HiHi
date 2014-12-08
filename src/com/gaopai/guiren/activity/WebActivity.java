package com.gaopai.guiren.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;

/**
 * 内置浏览器界面
 * 
 */
public class WebActivity extends BaseActivity implements OnClickListener {

	private WebView mWebView;
	private String mReportUrl;
	private String mTitle;
	private GestureDetector mGestureDetector;
	private ImageView mBackWardBtn, mForwardBtn, mRefreshBtn;
	private LinearLayout mBottomLayout;
	private int mType = 0;
	
	public final static String KEY_URL = "url";
	public final static String KEY_TITLE = "title";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_web);
		mContext = this;
		initComponent();
	}

	private void initComponent() {

		mTitle = getIntent().getStringExtra(KEY_TITLE);
		mReportUrl = getIntent().getStringExtra(KEY_URL);
		mType = getIntent().getIntExtra("type", 0);
		
		if (TextUtils.isEmpty(mReportUrl)) {
			Uri data = getIntent().getData();
			mReportUrl = data.toString().substring(
					data.toString().indexOf("//") + 2);
			mTitle = mReportUrl;
		}
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(mTitle);

		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);

		mBottomLayout = (LinearLayout) findViewById(R.id.bottomlayout);

		mBackWardBtn = (ImageView) findViewById(R.id.backward);
		mBackWardBtn.setOnClickListener(this);
		mForwardBtn = (ImageView) findViewById(R.id.forward);
		mForwardBtn.setImageResource(R.drawable.forwardward_gray);
		mForwardBtn.setEnabled(false);
		mForwardBtn.setOnClickListener(this);
		mRefreshBtn = (ImageView) findViewById(R.id.refresh);
		mRefreshBtn.setOnClickListener(this);

		mGestureDetector = new GestureDetector(mContext, new OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {

			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				if ((mWebView.getHeight() + mWebView.getScrollY() + FeatureFunction.dip2px(mContext, 100)) >= mWebView
						.getContentHeight() * mWebView.getScale()) {
					mBottomLayout.setVisibility(View.GONE);
				} else if (mWebView.getContentHeight() * mWebView.getScale() > (mWebView.getHeight()
						+ mWebView.getScrollY() + FeatureFunction.dip2px(mContext, 50))) {
					mBottomLayout.setVisibility(View.VISIBLE);
				}
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {

			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				return false;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		});

		mWebView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		});

		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (mProgressDialog == null || !mProgressDialog.isShowing()) {
					showProgressDialog(mContext.getString(R.string.add_more_loading));
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				removeProgressDialog();
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				if (mProgressDialog == null || !mProgressDialog.isShowing()) {
					showProgressDialog(mContext.getString(R.string.add_more_loading));
				}
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				removeProgressDialog();
				if (mWebView.canGoForward()) {
					mForwardBtn.setEnabled(true);
					mForwardBtn.setImageResource(R.drawable.forward);
				} else {
					mForwardBtn.setEnabled(false);
					mForwardBtn.setImageResource(R.drawable.forwardward_gray);
				}
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();
			}

		});

		mWebView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
					if (mWebView.canGoBack()) {
						mWebView.goBack();
					} else {
						WebActivity.this.finish();
					}
					return true;
				}
				return false;
			}
		});

		mWebView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
					long contentLength) {
				// 实现下载的代码
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});

		if (mType == 0) {
			mWebView.loadUrl(mReportUrl + "?display=0");
		} else {
			mWebView.loadUrl(mReportUrl);
		}
		showProgressDialog();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.backward:
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			} else {
				this.finish();
			}
			break;

		case R.id.forward:
			if (mWebView.canGoForward()) {
				mWebView.goForward();
			}
			break;

		case R.id.refresh:
			mWebView.reload();
			break;

		default:
			break;
		}
	}

}
