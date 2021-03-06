package com.gaopai.guiren;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gaopai.guiren.utils.Constant;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.StringUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.TitleBar;
import com.umeng.analytics.MobclickAgent;


public class BaseActivity extends FragmentActivity {

	protected DamiApp mApplication;
	public static final String ACTION_FINISH = "com.gaopai.guiren.intent.action.ACTION_FINISH";

	protected Context mContext;

	protected String TAG = BaseActivity.class.getSimpleName();

	private String mProgressMessage = "";

	private Dialog mBottomDialog;

	private Dialog mCenterDialog;

	private Dialog mTopDialog;

	private View mBottomDialogView = null;

	private View mCenterDialogView = null;

	private View mTopDialogView = null;

	public ProgressDialog mProgressDialog;

	public LayoutInflater mInflater;

	public RelativeLayout windowLayout = null;
	public TitleBar mTitleBar = null;
	protected FrameLayout contentLayout = null;

	public int displayWidth = 320;
	public int displayHeight = 480;

	private WindowManager mWindowManager = null;

	private int dialogPadding = 40;

	/**
	 * 主要Handler类，在线程中可用 what�?0.提示文本信息,1.等待�? ,2.移除等待�?
	 */
	private Handler baseHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.SHOW_TOAST:
				showToast(msg.getData().getString("Msg"));
				break;
			case Constant.SHOW_PROGRESS:
				showProgressDialog(mProgressMessage);
				break;
			case Constant.REMOVE_PROGRESS:
				removeProgressDialog();
				break;
			case Constant.REMOVE_DIALOGBOTTOM:
				removeDialog(Constant.DIALOGBOTTOM);
			case Constant.REMOVE_DIALOGCENTER:
				removeDialog(Constant.DIALOGCENTER);
			case Constant.REMOVE_DIALOGTOP:
				removeDialog(Constant.DIALOGTOP);
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		mInflater = LayoutInflater.from(this);
		mWindowManager = getWindowManager();
		Display display = mWindowManager.getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();
		mApplication = (DamiApp) getApplication();
		registerBaseAction();
	}

	protected void initTitleBar() {
		initTitleBar(false);
	}

	protected void initTitleBar(boolean isFloat) {
		mTitleBar = new TitleBar(this);
		windowLayout = new RelativeLayout(this);

		contentLayout = new FrameLayout(this);
		contentLayout.setPadding(0, 0, 0, 0);
		contentLayout.setBackgroundColor(getResources().getColor(R.color.general_background));
		RelativeLayout.LayoutParams lpBar = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		lpBar.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		RelativeLayout.LayoutParams lpContent = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		if (isFloat) {
			windowLayout.addView(contentLayout, lpContent);
			windowLayout.addView(mTitleBar, lpBar);
		} else {
			windowLayout.addView(mTitleBar, lpBar);
			lpContent.addRule(RelativeLayout.BELOW, R.id.action_bar);
			windowLayout.addView(contentLayout, lpContent);
		}

		setContentView(windowLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	protected void addTitleBar(ViewGroup holder) {
		mTitleBar = new TitleBar(this);
		holder.addView(mTitleBar, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		// holder.addView(ViewUtil.creatTitleBarLineView(mContext));
	}

	public void setAbContentView(View contentView) {
		layoutContent = contentView;
		contentLayout.removeAllViews();
		contentLayout.addView(contentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	public void addLoadingView() {
		layoutLoading = mInflater.inflate(R.layout.layout_fetch_data, null);
		contentLayout.addView(layoutLoading, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	public View layoutContent;
	public View layoutLoading;

	public void setAbContentView(int resId) {
		setAbContentView(mInflater.inflate(resId, null));
	}

	public void showContent() {
		layoutContent.setVisibility(View.VISIBLE);
		layoutLoading.setVisibility(View.GONE);
	}

	public void showLoadingView() {
		layoutContent.setVisibility(View.GONE);
		layoutLoading.setVisibility(View.VISIBLE);
		((ViewGroup) layoutLoading).getChildAt(0).setVisibility(View.VISIBLE);
		((ViewGroup) layoutLoading).getChildAt(1).setOnClickListener(null);
		Logger.d(this, getString(R.string.now_loading));
		((TextView) ((ViewGroup) layoutLoading).getChildAt(1)).setText(R.string.now_loading);
	}

	public void showErrorView(OnClickListener listener) {
		layoutContent.setVisibility(View.GONE);
		layoutLoading.setVisibility(View.VISIBLE);
		((ViewGroup) layoutLoading).getChildAt(0).setVisibility(View.GONE);
		((ViewGroup) layoutLoading).getChildAt(1).setOnClickListener(listener);
		Logger.d(this, getString(R.string.loading_error_click_retry));
		((TextView) ((ViewGroup) layoutLoading).getChildAt(1)).setText(R.string.loading_error_click_retry);
	}

	public void showToast(int resId) {
		showToast(this.getResources().getText(resId).toString());
	}

	public void showToast(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(BaseActivity.this, "" + text, Toast.LENGTH_SHORT).show();
			}
		});

	}

	public void backToFragment() {
		getSupportFragmentManager().popBackStack();
	}

	public void showProgressDialog(int messageResId) {
		if (messageResId != 0) {
			mProgressMessage = getString(messageResId);
		}

		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				setProgressDialog();
				showDialog(Constant.DIALOGPROGRESS);
			}
		});
	}

	public void showProgressDialog(String message) {
		if (!StringUtils.isEmpty(message)) {
			mProgressMessage = message;
		}

		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				setProgressDialog();
				showDialog(Constant.DIALOGPROGRESS);
			}
		});
	}

	public void setProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new android.app.ProgressDialog(BaseActivity.this);
			mProgressDialog.setMessage(mProgressMessage);
			mProgressDialog.setCanceledOnTouchOutside(false);
		}
	}

	/**
	 * 描述：显示进度框.
	 */
	public void showProgressDialog() {
		showProgressDialog(null);
	}

	/**
	 * 描述：移除进度框.
	 */
	public void removeProgressDialog() {
		removeDialog(Constant.DIALOGPROGRESS);
	}

	/**
	 * 描述：对话框初始�?.
	 * 
	 * @param id
	 *            the id
	 * @return the dialog
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case Constant.DIALOGPROGRESS:
			if (mProgressDialog == null) {
				Log.i(TAG, "Dialog方法调用错误,请调用showProgressDialog()!");
			}
			return mProgressDialog;
		case Constant.DIALOGBOTTOM:
			if (mBottomDialog == null) {
				Log.i(TAG, "Dialog方法调用错误,请调用showDialog(int id,View view)!");
			}
			return mBottomDialog;
		case Constant.DIALOGCENTER:
			if (mCenterDialog == null) {
				Log.i(TAG, "Dialog方法调用错误,请调用showDialog(int id,View view)!");
			}
			return mCenterDialog;
		case Constant.DIALOGTOP:
			if (mTopDialog == null) {
				Log.i(TAG, "Dialog方法调用错误,请调用showDialog(int id,View view)!");
			}
			return mTopDialog;
		default:
			break;
		}
		return dialog;
	}

	/**
	 * 描述：在底部显示�?个Dialog,id�?1,在中间显示id�?2.
	 * 
	 * @param id
	 *            Dialog的类�?
	 * @param view
	 *            指定�?个新View
	 * @see AbConstant.DIALOGBOTTOM
	 */
	public void showDialog(int id, View view) {

		if (id == Constant.DIALOGBOTTOM) {
			mBottomDialogView = view;
			if (mBottomDialog == null) {
				mBottomDialog = new Dialog(this);
				setDialogLayoutParams(mBottomDialog, dialogPadding, Gravity.BOTTOM);
			}
			mBottomDialog.setContentView(mBottomDialogView, new LayoutParams(displayWidth - dialogPadding,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			showDialog(id);
		} else if (id == Constant.DIALOGCENTER) {
			mCenterDialogView = view;
			if (mCenterDialog == null) {
				mCenterDialog = new Dialog(this);
				setDialogLayoutParams(mCenterDialog, dialogPadding, Gravity.CENTER);
			}
			mCenterDialog.setContentView(mCenterDialogView, new LayoutParams(displayWidth - dialogPadding,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			showDialog(id);
		} else if (id == Constant.DIALOGTOP) {
			mTopDialogView = view;
			if (mTopDialog == null) {
				mTopDialog = new Dialog(this);
				setDialogLayoutParams(mTopDialog, dialogPadding, Gravity.TOP);
			}
			mTopDialog.setContentView(mTopDialogView, new LayoutParams(displayWidth - dialogPadding,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			showDialog(id);
		} else {
			Log.i(TAG, "Dialog的ID传错了，请参考AbConstant类定�?");
		}
	}

	private void setDialogLayoutParams(Dialog dialog, int dialogPadding, int gravity) {
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity(gravity);
		lp.width = displayWidth - dialogPadding;
		lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
		lp.alpha = 0.8f;
		lp.dimAmount = 0f;
		window.setAttributes(lp);
		window.setWindowAnimations(android.R.style.Animation_Dialog);
		dialog.setCanceledOnTouchOutside(false);

	}

	public void showDialog(String title, String msg, DialogInterface.OnClickListener mOkOnClickListener) {
		showDialog(title, msg, getString(R.string.ok), mOkOnClickListener);
	}

	public void showDialog(String title, String msg, String okStr, DialogInterface.OnClickListener mOkOnClickListener) {
		AlertDialog.Builder builder = new Builder(this);
		if (!TextUtils.isEmpty(msg)) {
			builder.setMessage(msg);
		}
		builder.setPositiveButton(R.string.ok, mOkOnClickListener);
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		Dialog dialog = builder.create();
		if (TextUtils.isEmpty(title)) {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		} else {
			dialog.setTitle(title);
		}
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	public AlertDialog showDialog(String title, String msg) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(msg);
		builder.setTitle(title);
		builder.create();
		AlertDialog mAlertDialog = builder.create();
		mAlertDialog.show();
		return mAlertDialog;
	}

	public AlertDialog showDialog(String title, View view) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(title);
		builder.setView(view);
		builder.create();
		AlertDialog mAlertDialog = builder.create();
		mAlertDialog.show();
		return mAlertDialog;
	}

	public void showMutiDialog(String title, String[] array, DialogInterface.OnClickListener onClickListener) {
		AlertDialog dialog = new AlertDialog.Builder(mContext).setItems(array, onClickListener).create();
		if (TextUtils.isEmpty(title)) {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		} else {
			dialog.setTitle(title);
		}
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onDestroy() {
		MobclickAgent.onPause(this);
		unregisterReceiver();
		super.onDestroy();
	}

	/** 通过Class跳转界面 **/
	public void startActivity(Class<?> cls) {
		startActivity(cls, null);
	}

	public void startActivityForResult(Class<?> cls, int requestCode) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		startActivityForResult(intent, requestCode);
	}

	/** 含有Bundle通过Class跳转界面 **/
	protected void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}




	public boolean isModeInCall = false;

	protected void initVoicePlayMode() {
		setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
		isModeInCall = DamiApp.getInstance().getPou().getBoolean(DamiApp.VOOICE_PLAY_MODE, false);
	}

	/**
	 * 
	 * @param isModeInCall
	 *            true 听筒 false 扬声器
	 */
	public void setPlayMode(boolean isModeInCall) {// true ear
		this.isModeInCall = isModeInCall;
		DamiApp.getInstance().getPou().setBoolean(DamiApp.VOOICE_PLAY_MODE, isModeInCall);
		DamiApp.getInstance().setPlayMode();
	}

	private boolean mIsRegisterReceiver = false;

	private void registerBaseAction() {
		IntentFilter filter = new IntentFilter();

		registerReceiver(filter);
		registerReceiver(mReceiver, filter);
		mIsRegisterReceiver = true;
	}

	// if activity don't want to be finished, like mainActivity, do not call
	// super method
	protected void registerReceiver(IntentFilter intentFilter) {
		intentFilter.addAction(ACTION_FINISH);
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_FINISH)) {
				BaseActivity.this.finish();
			}
			BaseActivity.this.onReceive(intent);
		}
	};

	protected void onReceive(Intent intent) {
	};

	private void unregisterReceiver() {
		if (mIsRegisterReceiver) {
			unregisterReceiver(mReceiver);
		}
		mIsRegisterReceiver = false;
	}

	/**
	 * 
	 * @param targetView
	 * @param type
	 *            1,右下 2 左下 3上
	 * @return
	 */
	public int[] getLocation(final View targetView, final View myView, final int type) {
		final int[] arrs = new int[4];
		ViewTreeObserver vto2 = targetView.getViewTreeObserver();

		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				int arrs1[] = new int[2];
				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);
				int mWidth = metrics.widthPixels;
				int mHeight = metrics.heightPixels;
				targetView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				targetView.getLocationOnScreen(arrs1);
				arrs[0] = arrs1[0];
				arrs[1] = arrs1[1];
				arrs[2] = targetView.getWidth();
				arrs[3] = targetView.getMeasuredHeight();
				ViewUtil.measure(myView);
				if (type == 1) {
					setLayout(myView, arrs[0] + 10, arrs[1] + 10);
				} else if (type == 2) {
					setLayout(myView, arrs[0] - myView.getMeasuredWidth() + arrs[2] / 2, arrs[1]);
				} else if (type == 3) {

					Log.d("CHEN", "mHeight ======" + mHeight + "     viewHeight===" + arrs[3] + "      myViewHeigth"
							+ myView.getMeasuredHeight() + "   y==" + arrs[1]);
					if (mHeight > 1280)
						setLayout(myView, arrs[0], arrs[1] - myView.getMeasuredHeight() - arrs[3]);
					else {
						setLayout(myView, arrs[0], arrs[1] - myView.getMeasuredHeight());
					}
				}
			}
		});
		return arrs;
	}

	/**
	 * 设置控件所在的位置，并且不改变宽高， XY为绝对位置
	 */
	private void setLayout(View view, int x, int y) {
		MarginLayoutParams margin = new MarginLayoutParams(view.getLayoutParams());
		margin.setMargins(x, y, 0, 0);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
		view.setLayoutParams(layoutParams);
	}

}
