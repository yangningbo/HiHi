package com.gaopai.guiren.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.CustomFragmentPagerAdapter;
import com.gaopai.guiren.bean.LoginResult;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.fragment.ConnectionFragment;
import com.gaopai.guiren.fragment.DynamicFragment;
import com.gaopai.guiren.fragment.DynamicFragment.BackPressedListener;
import com.gaopai.guiren.fragment.MeetingFragment;
import com.gaopai.guiren.fragment.NotificationFragment;
import com.gaopai.guiren.slidemenu.SlidingMenu;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.StringUtils;
import com.gaopai.guiren.view.slide.DragLayout;
import com.gaopai.guiren.view.slide.DragLayout.DragListener;
import com.gaopai.guiren.volley.IResponseListener;
import com.gaopai.guiren.volley.MyVolley;
import com.squareup.picasso.Picasso;

public class MainActivity extends BaseActivity {
	private ViewPager mTabPager;
	private ArrayList<Fragment> pagerItemList = null;
	private int currIndex = 0;
	private int pagerCount;
	private LinearLayout main_bottom;
	private Fragment page1;
	private Fragment page2;
	private Fragment page3;
	private Fragment page4;
	private User mUser;

	/** 左边滑动菜单 **/
	public static SlidingMenu mSlidingMenu;

	public final static int LOGIN_REQUEST = 29312;
	public final static int SHOW_GUIDE_REQUEST = 6541;
	public final static int UNLOGIN_REQUEST = 1634365;
	public final static int RESULT_EXIT = 702;
	public final static int MSG_LOAD_ERROR = 11818;
	public final static int INVITATION_VERIFY_REQUEST = 12545;
	public final static int REAL_VERIFY_REQUEST = 12546;

	public final static String ACTION_NETWORK_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	public static final String ACTION_SHOW_TOAST = "com.guiren.intent.action.ACTION_SHOW_TOAST";
	/** 注销 */
	public static final String ACTION_LOGIN_OUT = "com.guiren.intent.action.ACTION_LOGIN_OUT";
	public final static String LOGIN_SUCCESS_ACTION = "com.guiren.intent.action.LOGIN_SUCCESS_ACTION";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initSlidingMenu();
		setContentView(R.layout.activity_main);
		initDragLayout();
		mTabPager = (ViewPager) findViewById(R.id.vPager);
		main_bottom = (LinearLayout) findViewById(R.id.main_bottom);
		registerNetWorkMonitor();

		if (!startGuidePage()) {
			if (TextUtils.isEmpty(DamiCommon.getToken(this))) {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivityForResult(intent, LOGIN_REQUEST);
			} else {
				getLogin();
			}
		}

	}

	private DragLayout dragLayout;

	private void initDragLayout() {
		dragLayout = (DragLayout) findViewById(R.id.dl);
		dragLayout.setDragListener(new DragListener() {
			@Override
			public void onOpen() {
			}

			@Override
			public void onClose() {
			}

			@Override
			public void onDrag(float percent) {
			}
		});

		int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
		int screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
		findViewById(R.id.slide_left_holder).getLayoutParams().width = (int) (screenWidth * 0.75);

		View view = (View) findViewById(R.id.slide_btn_my_profile);
		view.setOnClickListener(slideMenuClickListener);
		view = (View) findViewById(R.id.slide_btn_my_followers);
		view.setOnClickListener(slideMenuClickListener);
		view = (View) findViewById(R.id.slide_btn_my_fans);
		view.setOnClickListener(slideMenuClickListener);
		view = (View) findViewById(R.id.slide_btn_my_tribe);
		view.setOnClickListener(slideMenuClickListener);
		view = (View) findViewById(R.id.slide_btn_my_meeting);
		view.setOnClickListener(slideMenuClickListener);
		view = (View) findViewById(R.id.slide_btn_my_favourite);
		view.setOnClickListener(slideMenuClickListener);
		view = (View) findViewById(R.id.slide_btn_my_dynamics);
		view.setOnClickListener(slideMenuClickListener);
		view = (View) findViewById(R.id.slide_btn_invite_friend);
		view.setOnClickListener(slideMenuClickListener);
		view = (View) findViewById(R.id.slide_btn_plus_v);
		view.setOnClickListener(slideMenuClickListener);
		view = (View) findViewById(R.id.slide_btn_setting);
		view.setOnClickListener(slideMenuClickListener);

	}

	private void bindUserView() {
		mUser = DamiCommon.getLoginResult(this);
		ImageView ivHeader = (ImageView) findViewById(R.id.iv_user_header);
		Picasso.with(mContext).load(mUser.headsmall).placeholder(R.drawable.default_header)
				.error(R.drawable.default_header).into(ivHeader);
		TextView textView = (TextView) findViewById(R.id.tv_user_name);
		textView.setText(mUser.realname);

		textView = (TextView) findViewById(R.id.tv_slide_count_followers);
		textView.setText(String.valueOf(mUser.followers));
		textView = (TextView) findViewById(R.id.tv_slide_count_fans);
		textView.setText(String.valueOf(mUser.fansers));
		textView = (TextView) findViewById(R.id.tv_slide_count_tribes);
		textView.setText(String.valueOf(mUser.tribeCount));
		textView = (TextView) findViewById(R.id.tv_slide_count_meetings);
		textView.setText(String.valueOf(mUser.meetingCount));
		textView = (TextView) findViewById(R.id.tv_slide_count_favourite);
		textView.setText(String.valueOf(mUser.favoriteCount));
		textView = (TextView) findViewById(R.id.tv_slide_count_dynamics);
		textView.setText(String.valueOf(mUser.dynamicCount));
	}

	private OnClickListener slideMenuClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.slide_btn_my_profile: {
				Intent intent = new Intent(mContext, ProfileActivity.class);
				intent.putExtra(ProfileActivity.KEY_UID, mUser.uid);
				startActivity(intent);
				break;
			}
			case R.id.slide_btn_my_followers:
				goToContact(ContactActivity.TYPE_FOLLOWERS);
				break;
			case R.id.slide_btn_my_fans:
				goToContact(ContactActivity.TYPE_FANS);
				break;
			case R.id.slide_btn_my_tribe:
				startActivity(TribeActivity.class);
				break;
			case R.id.slide_btn_my_meeting:
				startActivity(MyMeetingActivity.class);
				break;
			case R.id.slide_btn_my_favourite:
				startActivity(MyFavoriteActivity.class);
				break;
			case R.id.slide_btn_my_dynamics:
				startActivity(MyDynamicActivity.class);
				break;
			case R.id.slide_btn_invite_friend:
				startActivity(InviteFriendActivity.class);
				break;
			case R.id.slide_btn_plus_v:
				startActivity(ApplyActivity.class);
				break;
			case R.id.slide_btn_setting:
				startActivity(SettingActivity.class);
				break;
			default:
				break;
			}
		}
	};

	private void goToContact(int type) {
		Intent intent = new Intent();
		intent.putExtra(ContactActivity.KEY_UID, mUser.uid);
		intent.putExtra(ContactActivity.KEY_TYPE, type);
		intent.setClass(mContext, ContactActivity.class);
		startActivity(intent);
	}

	private void registerNetWorkMonitor() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_NETWORK_CHANGE);
		// filter.addAction(EXIT_ACTION);
		// filter.addAction(ACTION_REFRESH_NOTIFIY);
		// filter.addAction(ACTION_UPDATE_NOTIFY_SESSION_COUNT);
		// filter.addAction(ACTION_UPDATE_TRIBE_SESSION_COUNT);
		// filter.addAction(ACTION_UPDATE_MESSAGE_SESSION_COUNT);
		// filter.addAction(ACTION_UPDATE_MEETING_SESSION_COUNT);
		// filter.addAction(ACTION_CALLBACK);
		// filter.addAction(ACTION_REFRESH_FRIEND);
		filter.addAction(ACTION_LOGIN_OUT);
		// filter.addAction(LOGIN_SUCCESS_ACTION);
		// filter.addAction(SYSTEM_EXIT);
		filter.addAction(ACTION_SHOW_TOAST);
		// filter.addAction(ACTION_HIDE_NOTIFY);
		registerReceiver(mReceiver, filter);
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_NETWORK_CHANGE)) {
				boolean isNetConnect = false;
				ConnectivityManager connectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);

				NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
				if (activeNetInfo != null) {
					if (activeNetInfo.isConnected()) {
						isNetConnect = true;
						Toast.makeText(context,
								getResources().getString(R.string.message_net_connect) + activeNetInfo.getTypeName(),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context,
								getResources().getString(R.string.network_error) + " " + activeNetInfo.getTypeName(),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(context, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT)
							.show();
				}
				DamiCommon.setNetWorkState(isNetConnect);
			} else if (ACTION_LOGIN_OUT.equals(action)) {
				Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
				startActivityForResult(loginIntent, LOGIN_REQUEST);

			} else if (LOGIN_SUCCESS_ACTION.equals(action)) {
				bindUserView();
				dragLayout.close();
				FeatureFunction.startService(MainActivity.this);
				// refreshNotifyCount();
				// refreshTribeCount();
				// refreshMeetingCount();
				// refreshMessageCount();
			} else if (ACTION_SHOW_TOAST.equals(action)) {
				String str = intent.getStringExtra("toast_msg");
				showToast(str);
			}
		}
	};

	private void initPage() {
		page1 = new MeetingFragment();
		page2 = new DynamicFragment();
		page3 = new ConnectionFragment();
		page4 = new NotificationFragment();
		pagerItemList = new ArrayList<Fragment>();
		pagerItemList.add(page1);
		pagerItemList.add(page2);
		pagerItemList.add(page3);
		pagerItemList.add(page4);
		pagerCount = pagerItemList.size();
		FragmentManager mFragmentManager = this.getSupportFragmentManager();
		CustomFragmentPagerAdapter mFragmentPagerAdapter = new CustomFragmentPagerAdapter(mFragmentManager,
				pagerItemList);
		mTabPager.setAdapter(mFragmentPagerAdapter);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mTabPager.setOffscreenPageLimit(3);
		changeBg(0);
	}

	/** 初始化左边滑动菜单 **/
	// private void initSlidingMenu() {
	// setBehindContentView(R.layout.slidingmenu_behind);
	// mSlidingMenu = getSlidingMenu();
	// mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	// mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
	// mSlidingMenu.setShadowDrawable(R.drawable.shadow);
	// mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
	// mSlidingMenu.setBehindScrollScale(0.5f);
	// mSlidingMenu.setFadeDegree(0.25f);
	//
	// View btnProfile = mSlidingMenu.getMenu().findViewById(R.id.btn_profile);
	// btnProfile.setOnClickListener(slideMenuClickListener);
	// }

	public void toggle() {
		dragLayout.toggle();
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// ((ConnectionFragment) page3).hideIndexedTextWhenChangePage();
		}

		@Override
		public void onPageSelected(int arg0) {
			// if (arg0 == 0) {
			// mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			// } else {
			// mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			// }
			changeBg(arg0);
			currIndex = arg0;
		}

	}

	public class MyOnClickListener implements OnClickListener {
		int index = 0;

		public MyOnClickListener(int i) {
			this.index = i;
		}

		@Override
		public void onClick(View v) {
			mTabPager.setCurrentItem(index, false);
		}
	}

	/**
	 * @param arg0
	 */
	public void changeBg(int arg0) {
		for (int i = 0; i < pagerCount; i++) {
			int imgId = getResources().getIdentifier("img_" + (i + 1), "id", MainActivity.this.getPackageName());
			int rlId = getResources().getIdentifier("layout_tab_indicator" + (i + 1), "id",
					MainActivity.this.getPackageName());
			int textId = getResources().getIdentifier("title_" + (i + 1), "id", MainActivity.this.getPackageName());
			RelativeLayout rl = (RelativeLayout) findViewById(rlId);
			ImageView iv = (ImageView) findViewById(imgId);
			TextView tv = (TextView) findViewById(textId);
			rl.setOnClickListener(new MyOnClickListener(i));
			int resId = getResources().getIdentifier("tabbar_item" + i + "_d", "drawable",
					MainActivity.this.getPackageName());
			Drawable drawable = getResources().getDrawable(resId);
			int resId2 = getResources().getIdentifier("tabbar_item" + i + "_n", "drawable",
					MainActivity.this.getPackageName());
			Drawable drawable2 = getResources().getDrawable(resId2);
			if (arg0 == i) {
				iv.setImageDrawable(drawable);
				tv.setTextColor(getResources().getColor(R.color.tab_text_d_color));
			} else {
				iv.setImageDrawable(drawable2);
				tv.setTextColor(getResources().getColor(R.color.tab_text_n_color));
			}
		}
	}

	public int getBottomHeight() {
		main_bottom.measure(0, 0);
		return main_bottom.getMeasuredHeight();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("keydown", "00000");
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (backPressedListener != null && backPressedListener.onBack()) {
				return true;
			}
			moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyVolley.getRequestQueue().cancelAll(this);
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void changeItem(int index) {
		mTabPager.setCurrentItem(index, false);
		changeBg(index);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void getLogin() {
		DamiInfo.hiddenLogin(new IResponseListener() {

			@Override
			public void onSuccess(Object o) {
				LoginResult data = (LoginResult) o;
				if (data.state != null && data.state.code == 0) {

					if (data.data != null) {
						DamiCommon.saveLoginResult(MainActivity.this, data.data);
						DamiCommon.setUid(data.data.uid);
						DamiCommon.setToken(data.data.token);
						SQLiteDatabase db = DBHelper.getInstance(MainActivity.this).getWritableDatabase();
						MessageTable table = new MessageTable(db);
						if (data.data.roomids != null)
							table.deleteMore(data.data.roomids.tribelist, data.data.roomids.meetinglist);
					}
					if (data.data.auth == 0) {
						if (data.data.authStage == 1) {
							Intent intent = new Intent(MainActivity.this, InvitationVerifyActivity.class);
							intent.putExtra("user", data.data);
							startActivityForResult(intent, INVITATION_VERIFY_REQUEST);
						} else {
							Intent intent = new Intent(MainActivity.this, RealVerifyActivity.class);
							intent.putExtra("user", data.data);
							startActivityForResult(intent, REAL_VERIFY_REQUEST);
						}
					} else {// 开启服务 刷新提示 隐登录成功
						bindUserView();
						dragLayout.close();
						FeatureFunction.startService(MainActivity.this);
						initPage();
					}
					return;
				} else {
					String str;
					if (data.state != null && !StringUtils.isEmpty(data.state.msg)) {
						str = data.state.msg;
					} else {
						str = getString(R.string.login_error);
					}
					showToast(str);
					reLogin();
				}
			}

			@Override
			public void onReqStart() {
				showProgressDialog(R.string.loading_login);
			}

			@Override
			public void onFinish() {
				removeProgressDialog();
			}

			@Override
			public void onFailure(Object o) {
				showToast(R.string.login_error);
				reLogin();
			}

			@Override
			public void onTimeOut() {
				// TODO Auto-generated method stub
				showToast(R.string.request_timeout);
				reLogin();
			}
		});
	}

	private void reLogin() {
		DamiCommon.saveLoginResult(this, null);
		DamiCommon.setUid("");
		DamiCommon.setToken("");

		Intent intent = new Intent(this, LoginActivity.class);
		startActivityForResult(intent, LOGIN_REQUEST);
	}

	/**
	 * 根据版本判断是否显示引导界面
	 * 
	 * @return isShowGudie ture显示
	 */
	private boolean startGuidePage() {
		boolean isShowGudie = false;

		SharedPreferences preferences = this.getSharedPreferences(DamiCommon.SHOWGUDIEVERSION, 0);
		int version = preferences.getInt("app_version", 0);
		// version = 0;
		if (version != MyUtils.getVersionCode(this)) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, GuideActivity.class);
			startActivityForResult(intent, SHOW_GUIDE_REQUEST);
			isShowGudie = true;
		}
		return isShowGudie;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case LOGIN_REQUEST:
			if (resultCode == RESULT_EXIT) {// dl repair
				MainActivity.this.finish();
				return;
			} else if (resultCode == RESULT_OK) {
				initPage();
				bindUserView();
				dragLayout.close();
				FeatureFunction.startService(mContext);
			} else if (resultCode == UNLOGIN_REQUEST) {
				initPage();
				FeatureFunction.startService(mContext);
			} else {
				MainActivity.this.finish();
				System.exit(0);
			}
			break;

		case SHOW_GUIDE_REQUEST:
			if (resultCode == RESULT_OK) {
				if (TextUtils.isEmpty(DamiCommon.getToken(MainActivity.this))) {
					Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
					startActivityForResult(loginIntent, LOGIN_REQUEST);
				} else {
					getLogin();
				}
			}
			break;
		case INVITATION_VERIFY_REQUEST:
			initPage();
			sendBroadcast(new Intent(LOGIN_SUCCESS_ACTION));
			break;

		case REAL_VERIFY_REQUEST:
			initPage();
			sendBroadcast(new Intent(LOGIN_SUCCESS_ACTION));
			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, intent);
	}

	public void hideBottomTab() {
		main_bottom.setVisibility(View.GONE);
	}

	public void showBottomTab() {
		main_bottom.setVisibility(View.VISIBLE);
	}

	private DynamicFragment.BackPressedListener backPressedListener;

	public void setBackPressedListener(BackPressedListener listener) {
		this.backPressedListener = listener;
	}

}
