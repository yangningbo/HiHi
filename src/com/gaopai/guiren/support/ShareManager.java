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

	public void shareContentToDy(String title, String info, String link, String picLink) {
		DamiInfo.spreadDynamic(6, "", title, picLink, link, info, new SimpleResponseListener(mActivity) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					showToast(R.string.spread_success);
				} else {
					otherCondition(data.state, mActivity);
				}
			}
		});
	}

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

		if (!TextUtils.isEmpty(content)) {
			circleMedia.setShareContent(content);
			weixinContent.setShareContent(content);
			qzoneContent.setShareContent(content);
			mController.setShareContent(content);
		}
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

	// public void shareContentRecommend(String title, String url) {
	// String content = title + " : " + url;
	// mController.setShareContent(content);
	//
	// QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mActivity,
	// APPID_QQ, APPKEY_QQ);
	// qZoneSsoHandler.addToSocialSDK();
	// UMImage iconImage = new UMImage(mActivity, R.drawable.logo);
	// QZoneShareContent qzoneContent = new QZoneShareContent();
	// qzoneContent.setTitle(title);
	// qzoneContent.setShareMedia(iconImage);
	// qzoneContent.setTargetUrl(url);
	// qzoneContent.setShareContent(content);
	// mController.setShareMedia(qzoneContent);
	//
	// UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mActivity, APPID_QQ,
	// APPKEY_QQ);
	// // qqSsoHandler.setTargetUrl(url);
	// // qqSsoHandler.setTitle(title);
	// qqSsoHandler.addToSocialSDK();
	// mController.setShareMedia(iconImage);
	// //
	//
	// WeiXinShareContent weixinContent = new WeiXinShareContent();
	// weixinContent.setTargetUrl(url);
	// weixinContent.setTitle(title);
	// weixinContent.setShareMedia(iconImage);
	// if (!TextUtils.isEmpty(content)) {
	// weixinContent.setShareContent(content);
	// }
	// mController.setShareMedia(weixinContent);
	//
	// // 设置朋友圈分享的内容
	// CircleShareContent circleMedia = new CircleShareContent();
	// circleMedia.setTargetUrl(url);
	// circleMedia.setTitle(title);
	// circleMedia.setShareMedia(iconImage);
	// if (!TextUtils.isEmpty(content)) {
	// circleMedia.setShareContent(content);
	// }
	// mController.setShareMedia(circleMedia);
	//
	// // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
	// String appId = APPID_WECHAT;
	// UMWXHandler wxHandler = new UMWXHandler(mActivity, appId);
	// wxHandler.addToSocialSDK();
	//
	// UMWXHandler wxCircleHandler = new UMWXHandler(mActivity, appId);
	// wxCircleHandler.setToCircle(true);
	// wxCircleHandler.addToSocialSDK();
	//
	// showShareWindow(mActivity);
	// }

	public final static String APPID_QQ = "100424468";
	public final static String APPKEY_QQ = "c7394704798a158208a74ab60104f0ba";
	public final static String APPID_WECHAT = "wx3d14f400726b7471";
	private UMQQSsoHandler qqSsoHandler;

	private void addHandler() {
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
