package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.Random;

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
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
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
import com.gaopai.guiren.support.view.HeadView;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.PreferenceOperateUtils;
import com.gaopai.guiren.utils.SPConst;
import com.gaopai.guiren.utils.StringUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.slide.DragLayout;
import com.gaopai.guiren.view.slide.DragLayout.DragListener;
import com.gaopai.guiren.volley.IResponseListener;
import com.gaopai.guiren.volley.MyVolley;
import com.squareup.picasso.Picasso;

public class MainActivity extends BaseActivity implements OnClickListener {
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

	private View layoutWelcome;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		addTitleBar();
		initTitleBarLocal();
		initDragLayout();
		mTabPager = (ViewPager) findViewById(R.id.vPager);
		main_bottom = (LinearLayout) findViewById(R.id.main_bottom);
		registerNetWorkMonitor();

		layoutWelcome = ViewUtil.findViewById(this, R.id.layout_welcome);
		ImageView view = (ImageView) findViewById(R.id.iv_back);
		view.setImageDrawable(getWelcomeRandomDrawable());
		Animation welcomeAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
		view.startAnimation(welcomeAnimation);
		showMainpage();
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
				layoutWelcome.setVisibility(View.GONE);
				if (!startGuidePage()) {
					if (TextUtils.isEmpty(DamiCommon.getToken(mContext))) {
						Intent intent = new Intent(mContext, LoginActivity.class);
						startActivityForResult(intent, LOGIN_REQUEST);
					} else {
						getLogin();
					}
				}
			}
		}, 4000);
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
			}
		});

		int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
		int screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
		findViewById(R.id.slide_left_holder).getLayoutParams().width = (int) (screenWidth * 0.75);
		((TextView) findViewById(R.id.tv_user_name)).setMaxWidth((int) (screenWidth * 0.75)
				- MyUtils.dip2px(mContext, 85));

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
				startActivity(MyDynamicActivity.getIntent(mContext, mUser.uid));
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
			// ((ConnectionFragment) page3).hideIndexedTextWhenChangePage();
		}

		@Override
		public void onPageSelected(int arg0) {
			setTitleBarText(arg0);
			changeBg(arg0);
			currIndex = arg0;
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
	protected void onDestroy() {
		super.onDestroy();
		MyVolley.getRequestQueue().cancelAll(this);
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.d(this, "onResume");
		bindUserView();
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
					bindUserView();
					dragLayout.close();
					FeatureFunction.startService(MainActivity.this);
					initPage();
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
			sendBroadcast(new Intent(LOGIN_SUCCESS_ACTION));
			break;

		case REAL_VERIFY_REQUEST:
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

	private static ViewGroup dropDownView;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ab_add:
			if (dropDownView == null) {
				dropDownView = (ViewGroup) mInflater.inflate(R.layout.popup_main_titlebar_window, null);
				initAddMoreViews(dropDownView, mInflater);
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
		case R.id.tv_start_chat:
			startActivity(ContactActivity.getIntent(mContext, ContactActivity.TYPE_FOLLOWERS, mUser.uid, true));
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

}
