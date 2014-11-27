package com.gaopai.guiren;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gaopai.guiren.activity.CreatMeetingActivity;
import com.gaopai.guiren.activity.CreatTribeActivity;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.activity.SearchActivity;
import com.gaopai.guiren.activity.SendDynamicMsgActivity;
import com.gaopai.guiren.utils.Constant;
import com.gaopai.guiren.view.TitleBar;

public class BaseFragment extends Fragment implements OnClickListener {

	private static final String TAG = BaseFragment.class.getName();

	/** 全局的LayoutInflater对象，已经完成初始化. */
	public LayoutInflater mInflater;

	/**
	 * LinearLayout.LayoutParams，已经初始化为FILL_PARENT, FILL_PARENT
	 */
	public LinearLayout.LayoutParams layoutParamsFF = null;

	/**
	 * LinearLayout.LayoutParams，已经初始化为FILL_PARENT, WRAP_CONTENT
	 */
	public LinearLayout.LayoutParams layoutParamsFW = null;

	/**
	 * LinearLayout.LayoutParams，已经初始化为WRAP_CONTENT, FILL_PARENT
	 */
	public LinearLayout.LayoutParams layoutParamsWF = null;

	/**
	 * LinearLayout.LayoutParams，已经初始化为WRAP_CONTENT, WRAP_CONTENT
	 */
	public LinearLayout.LayoutParams layoutParamsWW = null;

	/** 总布�?. */
	public RelativeLayout ab_base = null;

	/** 标题栏布�?. */
	protected TitleBar mTitleBar = null;

	/** 主内容布�?. */
	protected RelativeLayout contentLayout = null;

	/** 屏幕宽度. */
	public int displayWidth = 320;

	/** 屏幕高度. */
	public int displayHeight = 480;

	/** Window 管理�?. */
	private WindowManager mWindowManager = null;

	protected BaseActivity act;

	protected View mView;

	protected DamiApp mApplication;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		act = (BaseActivity) getActivity();
		mApplication = DamiApp.getInstance();
		mInflater = LayoutInflater.from(act);
		mWindowManager = act.getWindowManager();
		Display display = mWindowManager.getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		if (ab_base == null) {
			initTitleBar();

			contentLayout.setClickable(true);
			addChildView(contentLayout);
		} else {
			((ViewGroup) ab_base.getParent()).removeView(ab_base);
		}
		return ab_base;
	}

	protected void addButtonToTitleBar() {
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

	public void addChildView(ViewGroup contentLayout) {

	}

	protected void initTitleBar() {
		layoutParamsFF = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParamsFW = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParamsWF = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParamsWW = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		// 主标题栏
		mTitleBar = new TitleBar(act);

		ab_base = new RelativeLayout(act);
		ab_base.setBackgroundResource(R.color.welcome_bg_color);
		contentLayout = new RelativeLayout(act);
		contentLayout.setPadding(0, 0, 0, 0);

		// 填入View
		ab_base.addView(mTitleBar, layoutParamsFW);

		RelativeLayout.LayoutParams layoutParamsFW1 = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParamsFW1.addRule(RelativeLayout.BELOW, mTitleBar.getId());
		ab_base.addView(contentLayout, layoutParamsFW1);

	}

	protected DamiApp getMyApplication() {
		return (DamiApp) act.getApplication();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mTitleBar != null)
			loadDefaultStyle();
	}

	public void loadDefaultStyle() {
		mTitleBar.setTitleBarBackground(R.drawable.title_bar);
		mTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
	}

	// /**
	// * 系统自带的activity启动方式
	// */
	// public void startDefaultActivity(Intent intent) {
	// super.startActivity(intent);
	// }
	//
	// public void startDefaultActivityForResult(Intent intent, int requestCode)
	// {
	// super.startActivityForResult(intent, requestCode);
	// }
	//
	// /**
	// * 自定义启动activity的方式，向左滑动出来
	// */
	// @Override
	// public void startActivity(Intent intent) {
	// super.startActivity(intent);
	// act.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	// }
	//
	// /**
	// * 自定义启动activity的方式，向左滑动出来
	// */
	// @Override
	// public void startActivityForResult(Intent intent, int requestCode) {
	// super.startActivityForResult(intent, requestCode);
	// act.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	// }
	//
	// /**
	// * 自定义关闭activity的方式，向右滑动出去
	// */
	// public void finish() {
	// act.overridePendingTransition(R.anim.push_right_in,
	// R.anim.push_right_out);
	// }
	//
	// /**
	// * 系统自带的activity启动方式
	// */
	// public void defaultFinish() {
	// act.finish();
	// }

	/** 通过Class跳转界面 **/
	protected void startActivity(Class<?> cls) {
		startActivity(cls, null);
	}

	/** 含有Bundle通过Class跳转界面 **/
	protected void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(act, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/** 通过Action跳转界面 **/
	protected void startActivity(String action) {
		startActivity(action, null);
	}

	/** 含有Bundle通过Action跳转界面 **/
	protected void startActivity(String action, Bundle bundle) {
		Intent intent = new Intent();
		intent.setAction(action);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	private static ViewGroup dropDownView;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ab_add:
			if (dropDownView == null) {
				LayoutInflater layoutInflater = getActivity().getLayoutInflater();
				dropDownView = (ViewGroup) layoutInflater.inflate(R.layout.titlebar_add_popup_window, null);
				initAddMoreViews(dropDownView, layoutInflater);
			}
			mTitleBar.showWindow(v, dropDownView);
			break;

		case R.id.ab_search: {
			startActivity(SearchActivity.class);
			break;
		}
		case R.id.ab_logo: {
//			startActivity(ProfileActivity.class);
			((MainActivity)getActivity()).toggle();
			break;
		}
		case R.id.tv_creat_meeting: {
			startActivity(CreatMeetingActivity.class);
			mTitleBar.closeWindow();
			break;
		}
		case R.id.tv_send_dynamic_:
			startActivity(SendDynamicMsgActivity.class);
			mTitleBar.closeWindow();
			break;
		case R.id.tv_creat_tribe:
			startActivity(CreatTribeActivity.class);
			mTitleBar.closeWindow();
			break;
		case R.id.tv_start_chat:
			mTitleBar.closeWindow();
			break;

		default:
			break;
		}
	}

	private void initAddMoreViews(ViewGroup viewGroup, LayoutInflater inflater) {
		View tvCreatMeeting = viewGroup.findViewById(R.id.tv_creat_meeting);
		tvCreatMeeting.setOnClickListener(this);
		View tvSendDynamicMsg = viewGroup.findViewById(R.id.tv_send_dynamic_);
		tvSendDynamicMsg.setOnClickListener(this);
		View tvCreatTribe = viewGroup.findViewById(R.id.tv_creat_tribe);
		tvCreatTribe.setOnClickListener(this);
		View tvAddFriend = viewGroup.findViewById(R.id.tv_add_friends);
		tvAddFriend.setOnClickListener(this);
		View tvStartChat = viewGroup.findViewById(R.id.tv_start_chat);
		tvStartChat.setOnClickListener(this);
	}

	/**
	 * 描述：显示进度框.
	 */
	public void showProgressDialog() {
		((BaseActivity) getActivity()).showProgressDialog();
	}

	public void showProgressDialog(String message) {
		((BaseActivity) getActivity()).showProgressDialog(message);
	}

	public void showProgressDialog(int messageResId) {
		((BaseActivity) getActivity()).showProgressDialog(messageResId);
	}

	public void removeProgressDialog() {
		((BaseActivity) getActivity()).removeDialog(Constant.DIALOGPROGRESS);
	}

}
