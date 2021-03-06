package com.gaopai.guiren.support;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.volley.SimpleResponseListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class ShareManager implements OnClickListener {
	private ViewGroup chatGridLayout;
	private PopupWindow moreWindow;
	private Activity mActivity;

	public ShareManager(Activity activity) {
		mActivity = activity;
		addHandler();
	}

	public void showShareWindow(Activity activity) {
		if (chatGridLayout == null) {
			chatGridLayout = (ViewGroup) getGridView(activity);
		}
		if (moreWindow == null) {
			moreWindow = new PopupWindow(chatGridLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}
		moreWindow.setBackgroundDrawable(new BitmapDrawable());
		moreWindow.setOutsideTouchable(true);
		moreWindow.setFocusable(true);
		moreWindow.setAnimationStyle(R.style.window_bottom_animation);
		moreWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
	}

	private void hideMoreWindow() {
		if (moreWindow != null && moreWindow.isShowing()) {
			moreWindow.dismiss();
		}
	}

	private ViewGroup getGridView(Context context) {
		LayoutInflater mInflater = LayoutInflater.from(context);
		ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.grid_share_window, null);
		ViewUtil.findViewById(viewGroup, R.id.grid_share_wechat).setOnClickListener(this);
		ViewUtil.findViewById(viewGroup, R.id.grid_share_wechat_group).setOnClickListener(this);
		ViewUtil.findViewById(viewGroup, R.id.grid_share_weibo).setOnClickListener(this);
		ViewUtil.findViewById(viewGroup, R.id.grid_share_guiren_dy).setOnClickListener(this);
		ViewUtil.findViewById(viewGroup, R.id.grid_share_qq).setOnClickListener(this);
		ViewUtil.findViewById(viewGroup, R.id.grid_share_qq_zone).setOnClickListener(this);
		ViewUtil.findViewById(viewGroup, R.id.btn_hide_grid).setOnClickListener(this);
		ViewUtil.findViewById(viewGroup, R.id.btn_hide_grid_2).setOnClickListener(this);
		return viewGroup;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.grid_share_wechat:
			mController.directShare(mActivity, SHARE_MEDIA.WEIXIN, mShareListener);
			break;
		case R.id.grid_share_wechat_group:
			mController.directShare(mActivity, SHARE_MEDIA.WEIXIN_CIRCLE, mShareListener);
			break;
		case R.id.grid_share_weibo:
			mController.postShare(mActivity, SHARE_MEDIA.SINA, mShareListener);
			break;
		case R.id.grid_share_guiren_dy:
			callDyback.spreadDy();
			break;
		case R.id.grid_share_qq:
			mController.directShare(mActivity, SHARE_MEDIA.QQ, mShareListener);
			break;
		case R.id.grid_share_qq_zone:
			mController.directShare(mActivity, SHARE_MEDIA.QZONE, mShareListener);
			break;
		case R.id.btn_hide_grid_2:
		case R.id.btn_hide_grid:
			hideMoreWindow();
			break;
		default:
			break;
		}
		hideMoreWindow();
	}

	private CallDyback callDyback;

	public void setDyCallback(CallDyback callDyback) {
		this.callDyback = callDyback;
	}

	public static interface CallDyback {
		public void spreadDy();
	}

	protected UMSocialService mController = UMServiceFactory.getUMSocialService("com.gaopai.guiren");

	public void setShareContent(String img, String title, String content, String url) {
		setShareContent(new UMImage(mActivity, img), title, content, url);
	}

	public void setShareContent(int img, String title, String content, String url) {
		setShareContent(new UMImage(mActivity, img), title, content, url);
	}

	public void setShareContent(UMImage iconImage, String title, String content, String url) {

		QZoneShareContent qzoneContent = new QZoneShareContent();
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		CircleShareContent circleMedia = new CircleShareContent();

		qzoneContent.setShareMedia(iconImage);
		weixinContent.setShareMedia(iconImage);
		circleMedia.setShareMedia(iconImage);
		mController.setShareImage(iconImage);

		if (!TextUtils.isEmpty(url)) {
			qzoneContent.setTargetUrl(url);
			circleMedia.setTargetUrl(url);
			weixinContent.setTargetUrl(url);
			qqSsoHandler.setTargetUrl(url);
		}
		if (!TextUtils.isEmpty(title)) {
			circleMedia.setTitle(title);
			qzoneContent.setTitle(title);
			qqSsoHandler.setTitle(title);
			weixinContent.setTitle(title);
		}

		if (TextUtils.isEmpty(content)) {
			content = url;
		} else if (!content.contains(url)) {
			content = content + url;
		}
		circleMedia.setShareContent(content);
		weixinContent.setShareContent(content);
		qzoneContent.setShareContent(content);
		mController.setShareContent(content);

		mController.setShareMedia(circleMedia);
		mController.setShareMedia(weixinContent);
		mController.setShareMedia(qzoneContent);
	}

	public void shareContentRecommend(String title, String url) {
		setShareContent(R.drawable.logo, title, title + url, url);
		showShareWindow(mActivity);
	}

	public void shareTribeLink(String title, String content, String url) {
		setShareContent(R.drawable.logo, title, content, url);
		showShareWindow(mActivity);
	}

	public void shareWebLink(String title, String image, String content, String url) {
		setShareContent(image, title, content, url);
		showShareWindow(mActivity);
	}

	public void shareWebLink(String title, int image, String content, String url) {
		setShareContent(image, title, content, url);
		showShareWindow(mActivity);
	}

	public final static String APPID_QQ = "100424468";
	public final static String APPKEY_QQ = "c7394704798a158208a74ab60104f0ba";
	// public final static String APPID_WECHAT = "wx3d14f400726b7471";
	// public final static String APPSECRET_WECHAT =
	// "828653a1ee0829d6e3e13f6ba2aeda85";
	public final static String APPID_WECHAT = "wx68ac0fad4eac8a24";
	public final static String APPSECRET_WECHAT = "441650f67e7ef862e33098c459ee2122";
	private UMQQSsoHandler qqSsoHandler;

	private void addHandler() {

		SinaSsoHandler sinaSsoHandler = new SinaSsoHandler();
		sinaSsoHandler.addToSocialSDK();
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mActivity, APPID_QQ, APPKEY_QQ);
		qZoneSsoHandler.addToSocialSDK();

		qqSsoHandler = new UMQQSsoHandler(mActivity, APPID_QQ, APPKEY_QQ);
		qqSsoHandler.addToSocialSDK();

		UMWXHandler wxHandler = new UMWXHandler(mActivity, APPID_WECHAT);
		wxHandler.addToSocialSDK();

		UMWXHandler wxCircleHandler = new UMWXHandler(mActivity, APPID_WECHAT);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	SnsPostListener mShareListener = new SnsPostListener() {

		@Override
		public void onStart() {
		}

		@Override
		public void onComplete(SHARE_MEDIA platform, int stCode, SocializeEntity entity) {
			// if (stCode == 200) {
			// Toast.makeText(mActivity, "分享成功", Toast.LENGTH_SHORT).show();
			// } else {
			// Toast.makeText(mActivity, "分享失败 : error code : " + stCode,
			// Toast.LENGTH_SHORT).show();
			// }
		}
	};

	public void shareQQ(String content, String title, String url) {
		setShareContent(R.drawable.logo, title, content, url);
		mController.directShare(mActivity, SHARE_MEDIA.QQ, mShareListener);
	}

	public void shareWechat(String content, String title, String url) {
		setShareContent(R.drawable.logo, title, content, url);
		mController.directShare(mActivity, SHARE_MEDIA.WEIXIN, mShareListener);
	}

	public void shareWeibo(String content, String title, String url) {
		setShareContent(R.drawable.logo, title, content, url);
		mController.postShare(mActivity, SHARE_MEDIA.SINA, mShareListener);
	}

	public void shareContact() {
	}

}
