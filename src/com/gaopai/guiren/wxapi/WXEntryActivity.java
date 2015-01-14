package com.gaopai.guiren.wxapi;

import java.io.Serializable;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.LoginActivity;
import com.gaopai.guiren.support.ShareManager;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.volley.SimpleResponseListener;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;

public class WXEntryActivity extends BaseActivity {
	public final static String ACTION_LOGIN_WECHAT= "com.guiren.intent.action.ACTION_LOGIN_WECHAT";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.d(this, "taskid = " + this.getTaskId());
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
		if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
			Logger.d(this, "code==================" + resp.code);
			DamiInfo.getWxAccessToken(ShareManager.APPID_WECHAT, ShareManager.APPSECRET_WECHAT, resp.code,
					new SimpleResponseListener(this, R.string.request_internet_now) {
						@Override
						public void onSuccess(Object o) {
							TokenBean data = (TokenBean) o;
							if (data != null && !TextUtils.isEmpty(data.access_token)) {
								DamiInfo.getWxUserInfo(data.access_token, data.openid, new SimpleResponseListener(
										WXEntryActivity.this, R.string.request_internet_now) {
									@Override
									public void onSuccess(Object o) {
										WxUserInfo userInfo = (WxUserInfo) o;
										if (TextUtils.isEmpty(userInfo.openid)) {
											WXEntryActivity.this.finish();
											return;
										}
										Intent intent = new Intent(WXEntryActivity.this, LoginActivity.class);
										intent.setAction(ACTION_LOGIN_WECHAT);
										intent.putExtra("data", userInfo);
										intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
										startActivity(intent);
									}
								});
							}
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
}
