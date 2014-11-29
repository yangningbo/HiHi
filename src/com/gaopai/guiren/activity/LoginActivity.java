package com.gaopai.guiren.activity;

import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.BaseInfo;
import com.gaopai.guiren.bean.LoginResult;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.utils.StringUtils;
import com.gaopai.guiren.volley.IResponseListener;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMSsoHandler;

/**
 * 登录界面。包括微博，qq
 * 
 * @author Administrator
 * 
 */
public class LoginActivity extends BaseActivity implements OnClickListener, OnTouchListener {

	private Button mLookBtn;
	private RelativeLayout mSinaLoginBtn, mQQLoginBtn;
	private RelativeLayout mRootLayout;
	
	private Button btnQQLogin;
	private Button btnWeixinLogin;
	private Button btnWeiboLogin;

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
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";

	private ImageView mAgreeProtocalView;
	private static final String SINA_USER_INFO_URL = "https://api.weibo.com/2/users/show.json";
	public SharedPreferences mPreferences;
	public final static int INVITATION_VERIFY_REQUEST = 12545;
	public final static int REAL_VERIFY_REQUEST = 12546;

	public static final String LOGIN_TYPE_REGULAR = "reguser";
	public static final String LOGIN_TYPE_QQ = "qq";
	public static final String LOGIN_TYPE_WEIBO = "sina";
	public static final String LOGIN_TYPE_WEIXIN = "weixin";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initShare();
		initTitleBar();
		setAbContentView(R.layout.activity_login);
		mTitleBar.setTitleText(R.string.login);
		initComponent();
	}

	private void initComponent() {
		mPreferences = this.getSharedPreferences(DamiCommon.REMENBER_SHARED, 0);
		mRootLayout = (RelativeLayout) findViewById(R.id.rootlayout);
		mSinaLoginBtn = (RelativeLayout) findViewById(R.id.sinalogin);
		mQQLoginBtn = (RelativeLayout) findViewById(R.id.tencentlogin);
		mLookBtn = (Button) findViewById(R.id.look);
		mAgreeProtocalView = (ImageView) findViewById(R.id.agree_protocal);
		
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

		mRootLayout.setOnTouchListener(this);
		mSinaLoginBtn.setOnClickListener(this);
		mQQLoginBtn.setOnClickListener(this);
		mLookBtn.setOnClickListener(this);
		mAgreeProtocalView.setOnClickListener(this);

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
			break;

		case R.id.look:
			setResult(MainActivity.UNLOGIN_REQUEST);
			LoginActivity.this.finish();
			break;

		case R.id.agree_protocal:
			startActivity(UserProtocalActivity.class);
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
	
	private void startRegister(int type) {
		Intent intent = new Intent(mContext, RegisterActivity.class);
		intent.putExtra(RegisterActivity.KEY_TYPE, type);
		startActivity(intent);
	}

	private void customLogin() {
		// TODO Auto-generated method stub
		String userName = etUserName.getText().toString();
		String password = etPassword.getText().toString();
		if(TextUtils.isEmpty(password) || TextUtils.isEmpty(userName)) {
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
		case INVITATION_VERIFY_REQUEST:
			if (resultCode != InvitationVerifyActivity.VERIFY_TOKEN_EXPIRED) {
				setResult(resultCode);
				if (resultCode == RESULT_OK) {
					sendBroadcast(new Intent(MainActivity.LOGIN_SUCCESS_ACTION));
					if (DamiCommon.getInstallFirst(LoginActivity.this)) {
						Intent intent = new Intent(LoginActivity.this, RecommendActivity.class);
						startActivity(intent);
					}
				}
			}
			this.finish();

			break;
		case REAL_VERIFY_REQUEST:
			if (resultCode != InvitationVerifyActivity.VERIFY_TOKEN_EXPIRED) {
				setResult(resultCode);
				if (resultCode == RESULT_OK) {
					sendBroadcast(new Intent(MainActivity.LOGIN_SUCCESS_ACTION));
					if (DamiCommon.getInstallFirst(LoginActivity.this)) {
						Intent intent = new Intent(LoginActivity.this, RecommendActivity.class);
						startActivity(intent);
					}
				}
			}
			this.finish();
			break;
		default:
			break;
		}
	}

	private void getLogin(final String type, final String sex, final String id, final String nickName,
			final String head, final String password) {

		String phone = getContacts();
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
						if (data.data.auth == 0) {
							if (data.data.authStage == 1) {
								Intent intent = new Intent(LoginActivity.this, InvitationVerifyActivity.class);
								intent.putExtra("user", data.data);
								startActivityForResult(intent, INVITATION_VERIFY_REQUEST);
							} else {
								Intent intent = new Intent(LoginActivity.this, RealVerifyActivity.class);
								intent.putExtra("user", data.data);
								startActivityForResult(intent, REAL_VERIFY_REQUEST);
							}

						} else {
							setResult(RESULT_OK);
							sendBroadcast(new Intent(MainActivity.LOGIN_SUCCESS_ACTION));

							if (DamiCommon.getInstallFirst(LoginActivity.this)) {
								Intent intent = new Intent(LoginActivity.this, RecommendActivity.class);
								startActivity(intent);
							}

							LoginActivity.this.finish();
						}

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

				// 得到手机号码
				String phoneNumber = cursor.getString(cursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				phoneNumber = phoneNumber.replaceAll(" ", "");
				// byte[] by = phoneNumber.getBytes();

				String contactName = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
				// Log.e("contactName", contactName + "---" + phoneNumber);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber)) {
					continue;
				}

				/*
				 * int length = phoneNumber.length(); if (length > 11) {
				 * phoneNumber = phoneNumber.substring(length - 11, length); }
				 * 
				 * if (!FeatureFunction.isMobileNum(phoneNumber)) { continue; }
				 */

				if (TextUtils.isEmpty(phoneStr)) {
					phoneStr = contactName;
					phoneStr += "," + phoneNumber;
				} else {
					phoneStr += "," + contactName;
					phoneStr += "," + phoneNumber;
				}

				// 得到联系人名称
				// String contactName =
				// cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));

				// 得到联系人ID
				// long contactid =
				// cursor.getLong(cursor.getColumnIndex(Phone.CONTACT_ID));

				/*
				 * for (int i = 0; i < mContactList.size(); i++) { if(contactid
				 * == mContactList.get(i).contactId){ continue; } }
				 */

				// 得到联系人头像ID
				// long photoid =
				// cursor.getLong(cursor.getColumnIndex(Phone.PHOTO_ID));

				// String sortKey =
				// cursor.getString(cursor.getColumnIndex("sort_key"));
				/*
				 * Bitmap contactPhoto = null; if(photoid > 0) { Uri uri
				 * =ContentUris
				 * .withAppendedId(ContactsContract.Contacts.CONTENT_URI
				 * ,contactid); InputStream input =
				 * ContactsContract.Contacts.openContactPhotoInputStream
				 * (contentResolver, uri); contactPhoto =
				 * BitmapFactory.decodeStream(input); }else { contactPhoto =
				 * BitmapFactory.decodeResource(getResources(),
				 * R.drawable.contact_default_header); }
				 */
			}

			cursor.close();

		}

		return phoneStr;
	}

	private void getInfo(final SHARE_MEDIA sm) {
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
					;
					String head = info.get("profile_image_url").toString();

					getLogin("sina", sex, id, nickName, head, "");

				} else {
					showToast("发生错误：" + status);
				}
			}
		});
	}
}
