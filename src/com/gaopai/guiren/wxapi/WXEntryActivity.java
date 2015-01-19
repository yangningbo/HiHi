package com.gaopai.guiren.wxapi;

import java.io.Serializable;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.support.ShareManager;
import com.gaopai.guiren.utils.Constant;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.volley.SimpleResponseListener;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {
	public final static String ACTION_LOGIN_WECHAT = "com.guiren.intent.action.ACTION_LOGIN_WECHAT";
	
	private boolean hasReceivedIntent = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handleIntent(getIntent());
	}

	@Override
	protected void handleIntent(Intent intent) {
		SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
		if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
			if (resp.state == null || !resp.state.equals("wxlogin")) {
				super.handleIntent(intent);
				return;
			}
			// Login with wechat
			hasReceivedIntent = true;
			showProgressDialog(getString(R.string.request_internet_now));
			DamiInfo.getWxAccessToken(ShareManager.APPID_WECHAT, ShareManager.APPSECRET_WECHAT, resp.code,
					new SimpleResponseListener(this) {
						@Override
						public void onSuccess(Object o) {
							TokenBean data = (TokenBean) o;
							if (data == null || TextUtils.isEmpty(data.access_token)) {
								WXEntryActivity.this.finish();
								return;
							}
							DamiInfo.getWxUserInfo(data.access_token, data.openid, new SimpleResponseListener(
									WXEntryActivity.this) {
								@Override
								public void onSuccess(Object o) {
									WxUserInfo userInfo = (WxUserInfo) o;
									if (TextUtils.isEmpty(userInfo.openid)) {
										WXEntryActivity.this.finish();
										return;
									}
									Intent intent = new Intent(ACTION_LOGIN_WECHAT);
									intent.putExtra("data", userInfo);
									WXEntryActivity.this.sendBroadcast(intent);
									WXEntryActivity.this.finish();
									removeProgressDialog();
								}
							});

						}
					});
		}
	}

	public static class TokenBean {
		public String access_token;
		public String expires_in;
		public String refresh_token;
		public String openid;
		public String scope;
		public int errcode;
		public String errmsg;
	}

	public static class WxUserInfo implements Serializable {
		public String openid;
		public String nickname;
		public int sex;
		public String province;
		public String city;
		public String country;
		public String headimgurl;
		public String unionid;
		public List<String> privilege;
	}

	private ProgressDialog mProgressDialog;

	public void showProgressDialog(String message) {
		if (mProgressDialog == null) {
			mProgressDialog = new android.app.ProgressDialog(WXEntryActivity.this);
			mProgressDialog.setMessage(message);
			mProgressDialog.setCanceledOnTouchOutside(false);
		}
		showDialog(Constant.DIALOGPROGRESS);
	}

	public void removeProgressDialog() {
		removeDialog(Constant.DIALOGPROGRESS);
	}

}
