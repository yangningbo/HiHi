package com.gaopai.guiren.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.chat.ChatMessageActivity;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
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
import com.gaopai.guiren.support.NotifyHelper;
import com.gaopai.guiren.support.view.HeadView;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.PreferenceOperateUtils;
import com.gaopai.guiren.utils.SPConst;
import com.gaopai.guiren.utils.UpdateManager;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.slide.DragLayout;
import com.gaopai.guiren.view.slide.DragLayout.DragListener;
import com.gaopai.guiren.volley.MyVolley;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class MainActivity extends BaseActivity implements OnClickListener {
	private ViewPager mTabPager;
	private ArrayList<Fragment> pagerItemList = null;
	private int pagerCount;
	private LinearLayout main_bottom;
	private Fragment page1;
	private Fragment page2;
	private Fragment page3;
	private Fragment page4;
	private User mUser;

	public final static int LOGIN_REQUEST = 29312;
	public final static int REQUES_SHOW_GUIDE = 6541;
	public final static int UNLOGIN_REQUEST = 1634365;
	public final static int MSG_LOAD_ERROR = 11818;

	public final static String ACTION_NETWORK_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	public static final String ACTION_SHOW_TOAST = "com.guiren.intent.action.ACTION_SHOW_TOAST";
	/** 注销 */
	public static final String ACTION_LOGIN_OUT = "com.guiren.intent.action.ACTION_LOGIN_OUT";
	public static final String ACTION_UPDATE_PROFILE = "com.guiren.intent.action.ACTION_UPDATE_PROFILE";
	public final static String LOGIN_SUCCESS_ACTION = "com.guiren.intent.action.LOGIN_SUCCESS_ACTION";

	// notification action
	public static final String ACTION_CHAT_PRIVATE = "com.guiren.intent.action.ACTION_CHAT_PRIVATE";
	public static final String ACTION_CHAT_TRIBE = "com.guiren.intent.action.ACTION_CHAT_TRIBE";
	public static final String ACTION_NOTIFY_SYSTEM = "com.guiren.intent.action.ACTION_NOTIFY_SYSTEM";

	public static final String ACTION_LOGIN_SHOW = "com.guiren.intent.action.ACTION_LOGIN_SHOW";

	private View layoutWelcome;

	private Intent notifyItent;
	
	private boolean isLogin = false;

	/**
	 * Initialize the MainActivity will take a long time, so I chose to embed
	 * welcome page in this activity to avoid a short-time black screen in
	 * transition. At the same time, a timer with 3 seconds delay is set up to
	 * hide welcome page and determine whether to show loginActivity (if token
	 * has expired) or stay in this page. For the first choice, we won't hide
	 * the welcome page until the loginActivity has run the method
	 * onWindowFocusChanged which will send an Intent with ACTION_LOGIN_SHOW.
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.d(this, "save= null   " + (savedInstanceState == null));
		notifyItent = getIntent();
		setContentView(R.layout.activity_main);
		initComponent();
		if (savedInstanceState != null) {
			isLogin = savedInstanceState.getBoolean("isLogin");
		}
		if (!isLogin) {
			showWelcomePage();
			showMainpage();
		} else {
			onLoginSuccess();
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("isLogin", isLogin);
	}

	private void initComponent() {
		addTitleBar();
		initTitleBarLocal();
		initDragLayout();
		mTabPager = (ViewPager) findViewById(R.id.vPager);
		main_bottom = (LinearLayout) findViewById(R.id.main_bottom);
		layoutWelcome = ViewUtil.findViewById(this, R.id.layout_welcome);
	}

	private void showWelcomePage() {
		ImageView view = (ImageView) findViewById(R.id.iv_back);
		TextView welcome = (TextView) findViewById(R.id.tv_welcome_info);
		welcome.setText(getWelcomeStr());
		view.setImageDrawable(getWelcomeRandomDrawable());
		Animation welcomeAnimation = AnimationUtils.loadAnimation(this, R.anim.welcome_scale);
		view.startAnimation(welcomeAnimation);
	}

	private String getWelcomeStr() {
		int i = (int) (Math.random() * 4);
		int id = getResources().getIdentifier("welcom_info" + i, "string", MainActivity.this.getPackageName());
		return getString(id);
	}

	private Drawable getWelcomeRandomDrawable() {
		int i = (int) (Math.random() * 3);
		int id = getResources().getIdentifier("icon_welcom_background_" + i, "drawable",
				MainActivity.this.getPackageName());
		return getResources().getDrawable(id);
	}

	public void showMainpage() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!startGuidePage()) {
					if (TextUtils.isEmpty(DamiCommon.getToken(mContext))) {
						Intent intent = new Intent(mContext, LoginActivity.class);
						startActivityForResult(intent, LOGIN_REQUEST);
					} else {
						getLogin();
						layoutWelcome.setVisibility(View.GONE);
					}
				}
			}
		}, 3000);
	}

	private void addTitleBar() {
		addTitleBar((ViewGroup) ViewUtil.findViewById(this, R.id.layout_titlebar));
	}

	private void initTitleBarLocal() {
		View view = mTitleBar.setLogo(R.drawable.selector_titlebar_home);
		view.setId(R.id.ab_logo);
		view.setOnClickListener(this);

		view = mTitleBar.addRightButtonView(R.drawable.selector_titlebar_search);
		view.setId(R.id.ab_search);
		view.setOnClickListener(this);
		view = mTitleBar.addRightButtonView(R.drawable.selector_titlebar_add);
		view.setId(R.id.ab_add);
		view.setOnClickListener(this);
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
				((DynamicFragment) page2).hideChatBox();
			}
		});

		int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
		findViewById(R.id.slide_left_holder).getLayoutParams().width = (int) (screenWidth * 0.75);
		((TextView) findViewById(R.id.tv_user_name)).setMaxWidth((int) (screenWidth * 0.75)
				- MyUtils.dip2px(mContext, 85));

		findViewById(R.id.slide_btn_my_profile).setOnClickListener(slideMenuClickListener);
		findViewById(R.id.slide_btn_my_followers).setOnClickListener(slideMenuClickListener);
		findViewById(R.id.slide_btn_my_tribe).setOnClickListener(slideMenuClickListener);
		findViewById(R.id.slide_btn_my_meeting).setOnClickListener(slideMenuClickListener);
		findViewById(R.id.slide_btn_my_favourite).setOnClickListener(slideMenuClickListener);
		findViewById(R.id.slide_btn_my_dynamics).setOnClickListener(slideMenuClickListener);
		findViewById(R.id.slide_btn_invite_friend).setOnClickListener(slideMenuClickListener);
		findViewById(R.id.slide_btn_plus_v).setOnClickListener(slideMenuClickListener);
		findViewById(R.id.slide_btn_setting).setOnClickListener(slideMenuClickListener);
	}

	private void bindUserView() {
		mUser = DamiCommon.getLoginResult(this);
		if (mUser == null) {
			return;
		}
		HeadView layoutHeader = (HeadView) findViewById(R.id.layout_header_mvp);
		layoutHeader.setImage(mUser.headsmall);
		TextView textView = (TextView) findViewById(R.id.tv_user_name);
		if (mUser.bigv == 1) {
			layoutHeader.setMVP(true);
			textView.setText(HeadView.getMvpName(mContext, mUser.realname + HeadView.MVP_NAME_STR));
		} else {
			textView.setText(mUser.realname);
			layoutHeader.setMVP(false);
		}

		textView = (TextView) findViewById(R.id.tv_slide_count_followers);
		textView.setText(String.valueOf(mUser.followers));
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
			case R.id.slide_btn_my_tribe:
				startActivity(TribeActivity.getIntent(mContext, mUser.uid));
				break;
			case R.id.slide_btn_my_meeting:
				startActivity(MyMeetingActivity.getIntent(mContext, mUser.uid));
				break;
			case R.id.slide_btn_my_favourite:
				startActivity(MyFavoriteActivity.class);
				break;
			case R.id.slide_btn_my_dynamics:
				startActivity(MyDynamicActivity.getIntent(mContext, mUser.uid));
				break;
			case R.id.slide_btn_invite_friend:
				if (User.checkCanInvite(mUser, MainActivity.this)) {
					startActivity(InviteFriendActivity.class);
				}
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

	@Override
	protected void registerReceiver(IntentFilter filter) {
		filter.addAction(ACTION_NETWORK_CHANGE);
		filter.addAction(ACTION_LOGIN_OUT);
		filter.addAction(LOGIN_SUCCESS_ACTION);
		filter.addAction(ACTION_SHOW_TOAST);
		filter.addAction(ACTION_UPDATE_PROFILE);
		filter.addAction(ACTION_LOGIN_SHOW);
	}

	@Override
	protected void onReceive(Intent intent) {
		String action = intent.getAction();
		if (action.equals(ACTION_NETWORK_CHANGE)) {
			boolean isNetConnect = false;
			ConnectivityManager connectivityManager = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
			if (activeNetInfo != null) {
				if (activeNetInfo.isConnected()) {
					isNetConnect = true;
					showToast(getString(R.string.message_net_connect) + activeNetInfo.getTypeName());
				} else {
					showToast(getResources().getString(R.string.network_error) + " " + activeNetInfo.getTypeName());
				}
			} else {
				showToast(R.string.network_error);
			}
			DamiCommon.setNetWorkState(isNetConnect);
		} else if (ACTION_LOGIN_OUT.equals(action)) {
			isLogin = false;
			dragLayout.closeQuick();
			Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
			startActivityForResult(loginIntent, LOGIN_REQUEST);
		} else if (LOGIN_SUCCESS_ACTION.equals(action)) {
			onLoginSuccess();
		} else if (ACTION_SHOW_TOAST.equals(action)) {
			showToast(intent.getStringExtra("toast_msg"));
		} else if (ACTION_UPDATE_PROFILE.equals(action)) {
			bindUserView();
		} else if (ACTION_LOGIN_SHOW.equals(action)) {
			layoutWelcome.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent == null) {
			return;
		}
		String action = intent.getAction();
		if (action == null) {
			return;
		}
		if (ACTION_CHAT_PRIVATE.equals(action)) {
			intent.setClass(mContext, ChatMessageActivity.class);
			mContext.startActivity(intent);
		} else if (ACTION_CHAT_TRIBE.equals(action)) {
			intent.setClass(mContext, ChatTribeActivity.class);
			mContext.startActivity(intent);
		} else if (ACTION_NOTIFY_SYSTEM.equals(action)) {
			intent.setClass(mContext, NotifySystemActivity.class);
			mContext.startActivity(intent);
		}
	}

	private boolean isIntialed = false;

	private void initPage() {
		if (isIntialed) {
			mTabPager.setCurrentItem(0);
			return;
		}
		isIntialed = true;
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
		mTabPager.setCurrentItem(0);
		setTitleBarText(0);
	}

	public void toggle() {
		dragLayout.toggle();
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			setTitleBarText(arg0);
			changeBg(arg0);
		}
	}

	private void setTitleBarText(int pos) {
		switch (pos) {
		case 0:
			mTitleBar.setTitleText(R.string.meeting);
			break;
		case 1:
			mTitleBar.setTitleText(R.string.dynamic);
			break;
		case 2:
			mTitleBar.setTitleText(R.string.connection);
			break;
		case 3:
			mTitleBar.setTitleText(R.string.message);
			break;

		default:
			break;
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

	public void changeBg(int page) {
		for (int i = 0; i < pagerCount; i++) {
			RelativeLayout layout = (RelativeLayout) findViewById(getId("layout_tab_indicator" + (i + 1)));
			ImageView iv = (ImageView) findViewById(getId("img_" + (i + 1)));
			TextView tv = (TextView) findViewById(getId("title_" + (i + 1)));
			layout.setOnClickListener(new MyOnClickListener(i));
			Drawable drawableDown = getResources().getDrawable(getDrawableId("tabbar_item" + i + "_d"));
			Drawable drawableNormal = getResources().getDrawable(getDrawableId("tabbar_item" + i + "_n"));
			if (page == i) {
				iv.setImageDrawable(drawableDown);
				tv.setTextColor(getResources().getColor(R.color.tab_text_d_color));
			} else {
				iv.setImageDrawable(drawableNormal);
				tv.setTextColor(getResources().getColor(R.color.tab_text_n_color));
			}
		}
	}

	private int getId(String name) {
		return getResources().getIdentifier(name, "id", MainActivity.this.getPackageName());
	}

	private int getDrawableId(String name) {
		return getResources().getIdentifier(name, "drawable", MainActivity.this.getPackageName());
	}

	public int getBottomHeight() {
		main_bottom.measure(0, 0);
		return main_bottom.getMeasuredHeight();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyVolley.getRequestQueue().cancelAll(this);
		NotifyHelper.clearAllNotification(mContext);
	}

	@Override
	public void onResume() {
		super.onResume();
		bindUserView();
	}

	public void changeItem(int index) {
		mTabPager.setCurrentItem(index, false);
		changeBg(index);
	}

	private void checkUpdate() {
		if (DamiCommon.verifyNetwork(mContext)) { // 检查版本更新
			UpdateManager.getUpdateManager().checkAppUpdate(this, false);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void onLoginSuccess() {
		isLogin = true;
		layoutWelcome.setVisibility(View.GONE);
		onNewIntent(notifyItent);
		bindUserView();
		dragLayout.close();
		FeatureFunction.startService(MainActivity.this);
		initPage();
		checkUpdate();
		showNotificationDot();
	}

	private void getLogin() {
		DamiInfo.hiddenLogin(new SimpleResponseListener(mContext, R.string.loading_login) {
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
						onLoginSuccess();
					}
					return;
				} else {
					otherCondition(data.state, MainActivity.this);
					reLogin();
				}
			}

			@Override
			public void onFailure(Object o) {
				showToast(R.string.login_error);
				reLogin();
			}

			@Override
			public void onTimeOut() {
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

	private boolean startGuidePage() {
		boolean isShowGudie = false;
		int result = DamiApp.getInstance().getPou().getInt(SPConst.KEY_GUIDE_START_PAGE, 0);
		int version = FeatureFunction.getAppVersion(this);
		Logger.d(this, "save version=" + result + "   current version=" + version);
		if (result != version) {
			layoutWelcome.setVisibility(View.GONE);
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, GuideActivity.class);
			startActivityForResult(intent, REQUES_SHOW_GUIDE);
			isShowGudie = true;
		}
		return isShowGudie;
	}

	public void showNotificationDot() {
		PreferenceOperateUtils operateUtils = new PreferenceOperateUtils(this);
		int count = operateUtils.getInt(SPConst.KEY_HAS_NOTIFICATION, 0);
		TextView viewDot = ViewUtil.findViewById(this, R.id.iv_count_4);
		if (count > 0) {
			viewDot.setVisibility(View.VISIBLE);
			viewDot.setText(count < 99 ? String.valueOf(count) : "99+");
		} else {
			viewDot.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case LOGIN_REQUEST:
			if (resultCode == RESULT_OK) {
				// Login activity has sent broadcast
			} else if (resultCode == UNLOGIN_REQUEST) {
				FeatureFunction.startService(mContext);
			} else {
				MainActivity.this.finish();
				System.exit(0);
			}
			break;

		case REQUES_SHOW_GUIDE:
			if (resultCode == RESULT_OK) {
				if (TextUtils.isEmpty(DamiCommon.getToken(MainActivity.this))) {
					Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
					startActivityForResult(loginIntent, LOGIN_REQUEST);
				} else {
					getLogin();
				}
			}
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

	private static ViewGroup dropDownView;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ab_add:
			if (dropDownView == null) {
				dropDownView = (ViewGroup) mInflater.inflate(R.layout.popup_main_titlebar_window, null);
				initAddMoreViews(dropDownView, mInflater);
			}
			if (dropDownView != null) {
				ViewGroup parent = ((ViewGroup) dropDownView.getParent());
				if (parent != null) {
					parent.removeView(dropDownView);
				}
			}
			mTitleBar.showWindow(v, dropDownView);
			break;

		case R.id.ab_search: {
			startActivity(SearchActivity.class);
			break;
		}
		case R.id.ab_logo:
			toggle();
			break;
		case R.id.tv_creat_meeting:
			startActivity(CreatMeetingActivity.class);
			mTitleBar.closeWindow();
			break;
		case R.id.tv_creat_tribe:
			startActivity(CreatTribeActivity.class);
			mTitleBar.closeWindow();
			break;
		case R.id.tv_send_dynamic:
			startActivity(SendDynamicMsgActivity.class);
			mTitleBar.closeWindow();
			break;
		case R.id.tv_scan:
			startActivity(CaptureActivity.class);
			mTitleBar.closeWindow();
			break;
		}
	}

	private void initAddMoreViews(ViewGroup viewGroup, LayoutInflater inflater) {
		View tvCreatMeeting = viewGroup.findViewById(R.id.tv_scan);
		tvCreatMeeting.setOnClickListener(this);
		View tvSendDynamicMsg = viewGroup.findViewById(R.id.tv_send_dynamic);
		tvSendDynamicMsg.setOnClickListener(this);
		View tvCreatTribe = viewGroup.findViewById(R.id.tv_creat_meeting);
		tvCreatTribe.setOnClickListener(this);
		tvCreatTribe = viewGroup.findViewById(R.id.tv_creat_tribe);
		tvCreatTribe.setOnClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Logger.d(this, "back pressed");
			if (backPressedListener != null && backPressedListener.onBack()) {
				return true;
			}
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public static void updateDynamicCount(Context context) {
		User user = DamiCommon.getLoginResult(context);
		user.dynamicCount = user.dynamicCount + 1;
		DamiCommon.saveLoginResult(context, user);
		context.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
	}

	public static void updateFansCount(Context context) {
		User user = DamiCommon.getLoginResult(context);
		user.fansers = user.fansers + 1;
		DamiCommon.saveLoginResult(context, user);
		context.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
	}

	// public static void addTribe(Context context) {
	// User user = DamiCommon.getLoginResult(context);
	// user.tribeCount = user.tribeCount + 1;
	// DamiCommon.saveLoginResult(context, user);
	// context.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
	// }
	//
	// public static void minusTribe(Context context) {
	// User user = DamiCommon.getLoginResult(context);
	// user.tribeCount = user.tribeCount - 1;
	// DamiCommon.saveLoginResult(context, user);
	// context.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
	// }
	//
	public static void addMeeting(Context context) {
		User user = DamiCommon.getLoginResult(context);
		user.meetingCount = user.meetingCount + 1;
		DamiCommon.saveLoginResult(context, user);
		context.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
	}

	//
	// public static void minusMeeting(Context context) {
	// User user = DamiCommon.getLoginResult(context);
	// user.meetingCount = user.meetingCount - 1;
	// DamiCommon.saveLoginResult(context, user);
	// context.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
	// }
	public static void minusDynamic(Context context) {
		User user = DamiCommon.getLoginResult(context);
		user.dynamicCount = user.dynamicCount - 1;
		DamiCommon.saveLoginResult(context, user);
		context.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
	}

	public static void minusFavoriteCount(Context mContext) {
		User user = DamiCommon.getLoginResult(mContext);
		user.favoriteCount = user.favoriteCount - 1;
		DamiCommon.saveLoginResult(mContext, user);
		mContext.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
	}
	

	public static void addFavoriteCount(Context mContext) {
		User user = DamiCommon.getLoginResult(mContext);
		user.favoriteCount = user.favoriteCount + 1;
		DamiCommon.saveLoginResult(mContext, user);
		mContext.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
	}

}
