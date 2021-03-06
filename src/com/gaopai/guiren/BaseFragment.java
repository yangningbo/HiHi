package com.gaopai.guiren;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.gaopai.guiren.activity.CreatMeetingActivity;
import com.gaopai.guiren.activity.CreatTribeActivity;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.activity.SearchActivity;
import com.gaopai.guiren.activity.SendDynamicMsgActivity;
import com.gaopai.guiren.support.DynamicHelper;
import com.gaopai.guiren.utils.Constant;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.TitleBar;

public class BaseFragment extends Fragment implements OnClickListener {

	private static final String TAG = BaseFragment.class.getName();

	public LayoutInflater mInflater;
	public LinearLayout.LayoutParams layoutParamsFF = null;
	public LinearLayout.LayoutParams layoutParamsFW = null;
	public LinearLayout.LayoutParams layoutParamsWF = null;
	public LinearLayout.LayoutParams layoutParamsWW = null;

	public LinearLayout windowLayout = null;
	public TitleBar mTitleBar = null;
	protected FrameLayout contentLayout = null;

	public int displayWidth = 320;
	public int displayHeight = 480;

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
//		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		if (windowLayout == null) {
			initTitleBar();
			contentLayout.setClickable(true);
			addChildView(contentLayout);
		} else {
			((ViewGroup) windowLayout.getParent()).removeView(windowLayout);
		}
		return windowLayout;
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

		mTitleBar = new TitleBar(act);
		
		windowLayout = new LinearLayout(act);
		windowLayout.setOrientation(LinearLayout.VERTICAL);
		windowLayout.addView(mTitleBar, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//		windowLayout.addView(ViewUtil.creatTitleBarLineView(act));

		contentLayout = new FrameLayout(act);
		contentLayout.setPadding(0, 0, 0, 0);
		contentLayout.setBackgroundColor(getResources().getColor(R.color.general_background));
		windowLayout.addView(contentLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	protected DamiApp getMyApplication() {
		return (DamiApp) act.getApplication();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

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



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		default:
			break;
		}
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
	
	public BaseActivity getBaseActivity() {
		return ((BaseActivity) getActivity());
	}
	
	private boolean mIsRegisterReceiver = false;
	
	protected IntentFilter registerReceiver(String...actions) {
		IntentFilter filter = new IntentFilter();
		for (String action:actions) {
			filter.addAction(action);
		}
		getActivity().registerReceiver(mReceiver, filter);
		mIsRegisterReceiver = true;
		return filter;
	}
	
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			BaseFragment.this.onReceive(intent);
		}
	};
	
	protected void onReceive(Intent intent){}; 
	
	private void unregisterReceiver() {
		if (mIsRegisterReceiver) {
			getActivity().unregisterReceiver(mReceiver);
		}
		mIsRegisterReceiver = false;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver();
	}
}
