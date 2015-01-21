package com.gaopai.guiren.activity;

import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.BaseInfo;
import com.gaopai.guiren.bean.LoginResult;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.support.ShareManager;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.SPConst;
import com.gaopai.guiren.utils.StringUtils;
import com.gaopai.guiren.volley.IResponseListener;
import com.gaopai.guiren.wxapi.WXEntryActivity;
import com.gaopai.guiren.wxapi.WXEntryActivity.WxUserInfo;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMSsoHandler;
public class LoginActivity extends BaseActivity implements OnClickListener, OnTouchListener {

	private Button btnQQLogin;
	private Button btnWeiboLogin;
	private Button btnWeixinLogin;

	private EditText etUserName;
	private EditText etPassword;

	private Button btLogin;
	private Button btRegister;

	private TextView tvForgetPassword;

	public final static int RESULT_FINISH = 9874;
	private UserInfo mInfo;
	public static String TENCENT_APP_ID = "101061639";
	public static String TENCENT_APP_KEY = "5c67409b26d3b6bae3d84a6f2de8e445";
	private QQAuth mQQAuth;
	private Tencent mTencent;
	private BaseInfo mUser;
	public static final String SINA_APP_KEY = "609349870";
	public static final String SINA_REDIRECT_URL = "http://www.kaopuhui.com/dami";
	public final static int INVITATION_VERIFY_REQUEST = 12545;
	public final static int REAL_VERIFY_REQUEST = 12546;

	public static final String LOGIN_TYPE_REGULAR = "reguser";
	public static final String LOGIN_TYPE_QQ = "qq";
	public static final String LOGIN_TYPE_WEIBO = "sina";
	public static final String LOGIN_TYPE_WEIXIN = "weixin";

	private IWXAPI wxApi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.startCountTime();
		initTitleBar();
		setAbContentView(R.layout.activity_login);
		mTitleBar.setTitleText(R.string.login);
		initComponent();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Logger.time(this, "loginCreatTime");
		sendBroadcast(new Intent(MainActivity.ACTION_LOGIN_SHOW));
	}


	@Override
	protected void registerReceiver(IntentFilter intentFilter) {
		intentFilter.addAction(WXEntryActivity.ACTION_LOGIN_WECHAT);
	}
	
	

	@Override
	protected void onReceive(Intent intent) {
		super.onReceive(intent);
		String action = intent.getAction();
		if (action != null && action.equals(WXEntryActivity.ACTION_LOGIN_WECHAT)) {
			handleWxLogin(intent);
		}
	}

	private void initComponent() {

		btnQQLogin = (Button) findViewById(R.id.btn_qq_login);
		btnWeiboLogin = (Button) findViewById(R.id.btn_weibo_login);
		btnWeixinLogin = (Button) findViewById(R.id.btn_weixin_login);

		btnQQLogin.setOnClickListener(this);
		btnWeiboLogin.setOnClickListener(this);
		btnWeixinLogin.setOnClickListener(this);

		etUserName = (EditText) findViewById(R.id.et_username);
		etPassword = (EditText) findViewById(R.id.et_password);

		tvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
		btLogin = (Button) findViewById(R.id.bt_login);
		btRegister = (Button) findViewById(R.id.bt_register);

		btLogin.setOnClickListener(this);
		btRegister.setOnClickListener(this);
		tvForgetPassword.setOnClickListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
				InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_weibo_login:
			if (!DamiCommon.verifyNetwork(LoginActivity.this)) {
				showToast(R.string.network_error);
				return;
			}
			getSinaLogin();
			break;

		case R.id.btn_qq_login:
			if (!DamiCommon.verifyNetwork(LoginActivity.this)) {
				showToast(R.string.network_error);
				return;
			}
			getQQLogin();
			break;
		case R.id.btn_weixin_login:
			if (!DamiCommon.verifyNetwork(LoginActivity.this)) {
				showToast(R.string.network_error);
				return;
			}
			getWeixinLogin();
			break;

		case R.id.tv_forget_password:
			startRegister(RegisterActivity.TYPE_FORGET_PASSWORD);
			break;
		case R.id.bt_login:
			if (!DamiCommon.verifyNetwork(LoginActivity.this)) {
				showToast(R.string.network_error);
				return;
			}
			customLogin();
			break;
		case R.id.bt_register:
			startRegister(RegisterActivity.TYPE_REGISTER);
			break;
		default:
			break;
		}
	}

	private void handleWxLogin(Intent intent) {
		if (!TextUtils.isEmpty(intent.getAction()) && intent.getAction().equals(WXEntryActivity.ACTION_LOGIN_WECHAT)) {
			WXEntryActivity.WxUserInfo userInfo = (WxUserInfo) intent.getSerializableExtra("data");
			if (userInfo != null) {
				getLogin("wexin", String.valueOf(userInfo.sex), userInfo.openid, userInfo.nickname,
						userInfo.headimgurl, "");
			}
		}
	}

	private void startRegister(int type) {
		Intent intent = new Intent(mContext, RegisterActivity.class);
		intent.putExtra(RegisterActivity.KEY_TYPE, type);
		startActivity(intent);
	}

	private void customLogin() {
		String userName = etUserName.getText().toString();
		String password = etPassword.getText().toString();
		if (TextUtils.isEmpty(password) || TextUtils.isEmpty(userName)) {
			showToast(R.string.input_can_not_be_empty);
			return;
		}
		getLogin(LOGIN_TYPE_REGULAR, "", userName, "", "", password);
	}

	private void getQQLogin() {
		showProgressDialog(R.string.loading_login);
		mQQAuth = QQAuth.createInstance(TENCENT_APP_ID, this);
		mTencent = Tencent.createInstance(TENCENT_APP_ID, this);
		if (mQQAuth != null && mTencent != null) {
			mTencent.loginWithOEM(LoginActivity.this, "all", mListener, "", "", "");
		} else {
			showProgressDialog(R.string.login_error);
		}
	}

	private void getWeixinLogin() {
		wxApi = WXAPIFactory.createWXAPI(this, ShareManager.APPID_WECHAT, true);
		wxApi.registerApp(ShareManager.APPID_WECHAT);
		SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "wxlogin";
		wxApi.sendReq(req);
	}

	private void getSinaLogin() {
		mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.SINA, new UMAuthListener() {
			@Override
			public void onError(SocializeException e, SHARE_MEDIA platform) {
				Log.d("CHEN", e.getMessage());
			}

			@Override
			public void onComplete(Bundle value, SHARE_MEDIA platform) {
				if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
					getInfo(SHARE_MEDIA.SINA);
				} else {
					Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onCancel(SHARE_MEDIA platform) {
			}

			@Override
			public void onStart(SHARE_MEDIA platform) {
			}
		});

	}

	private IUiListener mListener = new BaseUiListener() {

		@Override
		public void onError(UiError e) {
			showToast(e.errorDetail);
		}

		@Override
		public void onCancel() {
			showToast(R.string.login_error);
		}

		@Override
		protected void doComplete(JSONObject values) {
			mInfo = new UserInfo(LoginActivity.this, mQQAuth.getQQToken());
			mInfo.getUserInfo(new IUiListener() {

				@Override
				public void onError(UiError arg0) {
					showToast(R.string.login_error);
				}

				@Override
				public void onComplete(Object object) {
					JSONObject json = (JSONObject) object;
					String nickName = "";
					String headimg = "";
					String gender = "";
					if (json != null) {
						try {
							nickName = json.getString("nickname");
							headimg = json.getString("figureurl_qq_2");
							gender = json.getString("gender");
						} catch (JSONException e) {
							e.printStackTrace();
						}

						mUser = new BaseInfo();
						mUser.screenName = nickName;
						mUser.headimgUrl = headimg;

						if (gender.equals(getString(R.string.male))) {
							mUser.mGender = "1";
						} else if (gender.equals(getString(R.string.female))) {
							mUser.mGender = "2";
						}
						mUser.mGender = gender;

					}

					if (mUser != null) {
						getLogin("qq", mUser.mGender, mTencent.getOpenId(), mUser.screenName, mUser.headimgUrl, "");
					} else {
						showToast(R.string.login_error);
					}
				}

				@Override
				public void onCancel() {
					showToast(R.string.login_error);
				}
			});

		}
	};

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			// Util.showResultDialog(mContext, response.toString(), "登录成功");
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			// Util.toastMessage(MainActivity.this, "onError: " +
			// e.errorDetail);
			// Util.dismissDialog();
		}

		@Override
		public void onCancel() {
			// Util.toastMessage(MainActivity.this, "onCancel: ");
			// Util.dismissDialog();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		} else {
			removeProgressDialog();
		}
		switch (requestCode) {
		default:
			break;
		}
	}

	private void getLogin(final String type, final String sex, final String id, final String nickName,
			final String head, final String password) {

		Logger.startCountTime();
		String phone = getContacts();
		Logger.time(this, "time");
		if (TextUtils.isEmpty(phone)) {
			showToast(R.string.login_need_contact);
			return;
		}
		DamiInfo.getLogin(type, sex, id, nickName, head, password, phone, new IResponseListener() {
			@Override
			public void onSuccess(Object o) {
				LoginResult data = (LoginResult) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null) {
						DamiCommon.saveLoginResult(LoginActivity.this, data.data);
						DamiCommon.setUid(data.data.uid);
						DamiCommon.setToken(data.data.token);
						SQLiteDatabase db = DBHelper.getInstance(LoginActivity.this).getWritableDatabase();
						MessageTable table = new MessageTable(db);
						if (data.data.roomids != null)
							table.deleteMore(data.data.roomids.tribelist, data.data.roomids.meetinglist);
						setResult(RESULT_OK);
						sendBroadcast(new Intent(MainActivity.LOGIN_SUCCESS_ACTION));
						if (showRecomendPage()) {
							DamiApp.getInstance().getPou().setBoolean(SPConst.getRecKey(mContext), false);
							Intent intent = new Intent(LoginActivity.this, RecommendActivity.class);
							startActivity(intent);
						}
						LoginActivity.this.finish();
					} else {
						showToast(R.string.login_error);
					}
				} else {
					if (data.state != null && !StringUtils.isEmpty(data.state.msg)) {
						showToast(data.state.msg);
					}
				}
			}

			@Override
			public void onReqStart() {
				showProgressDialog(R.string.loading_login);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				removeProgressDialog();
				if (mQQAuth != null) {
					mQQAuth.logout(LoginActivity.this);
				}
				if (mTencent != null) {
					mTencent.logout(LoginActivity.this);
				}
			}

			@Override
			public void onFailure(Object o) {
				showToast(R.string.login_error);
			}

			@Override
			public void onTimeOut() {
				showToast(R.string.request_timeout);
			}
		});

	}
	

	private boolean showRecomendPage() {
		return DamiApp.getInstance().getPou().getBoolean(SPConst.getRecKey(mContext), true);
	}

	private String getContacts() {

		ContentResolver contentResolver = getContentResolver();
		String[] projection = { BaseColumns._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
				ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
				ContactsContract.CommonDataKinds.Phone.CONTACT_STATUS_TIMESTAMP }; // 查询的列
		Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null,
				null, "sort_key COLLATE LOCALIZED ASC"); // 按照sort_key升序查询

		String phoneStr = "";

		if (cursor != null) {
			while (cursor.moveToNext()) {
				String phoneNumber = cursor.getString(cursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				phoneNumber = phoneNumber.replaceAll(" ", "");

				String contactName = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
				if (TextUtils.isEmpty(phoneNumber)) {
					continue;
				}
				if (TextUtils.isEmpty(phoneStr)) {
					phoneStr = contactName;
					phoneStr += "," + phoneNumber;
				} else {
					phoneStr += "," + contactName;
					phoneStr += "," + phoneNumber;
				}
			}

			cursor.close();

		}

		return phoneStr;
	}

	private void getInfo(final SHARE_MEDIA sm) {
		LoginActivity.this.showProgressDialog(R.string.loading_login);
		new Thread(new Runnable() {
			@Override
			public void run() {
				mController.getPlatformInfo(LoginActivity.this, sm, new UMDataListener() {
					@Override
					public void onStart() {
					}

					@Override
					public void onComplete(int status, Map<String, Object> info) {
						if (status == 200 && info != null) {
							StringBuilder sb = new StringBuilder();
							Set<String> keys = info.keySet();
							for (String kStr : keys) {
								sb.append(kStr + "=" + info.get(kStr).toString() + "\r\n");
							}
							Log.d("Chen", sb.toString());

							String id = info.get("uid").toString();
							String sex;
							if (info.get("gender").toString().equals("男")) {
								sex = "1";
							} else {
								sex = "2";
							}
							String nickName = info.get("screen_name").toString();
							String head = info.get("profile_image_url").toString();

							getLogin("sina", sex, id, nickName, head, "");

						} else {
							showToast("发生错误：" + status);
							btLogin.post(new Runnable() {

								@Override
								public void run() {
									LoginActivity.this.removeProgressDialog();
								}
							});

						}
					}
				});
			}
		}).start();
	}
}
