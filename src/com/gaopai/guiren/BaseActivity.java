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
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gaopai.guiren.utils.Constant;
import com.gaopai.guiren.utils.StringUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.TitleBar;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

public class BaseActivity extends FragmentActivity {

	protected DamiApp mApplication;

	protected Context mContext;

	/** The tag. */
	protected String TAG = BaseActivity.class.getSimpleName();

	/** 加载框的文字说明. */
	private String mProgressMessage = "";

	/** 底部弹出的Dialog. */
	private Dialog mBottomDialog;

	/** 居中弹出的Dialog. */
	private Dialog mCenterDialog;

	/** 顶部弹出的Dialog. */
	private Dialog mTopDialog;

	/** 底部弹出的Dialog的View. */
	private View mBottomDialogView = null;

	/** 居中弹出的Dialog的View. */
	private View mCenterDialogView = null;

	/** 顶部弹出的Dialog的View. */
	private View mTopDialogView = null;

	/** 全局的加载框对象，已经完成初始化. */
	public ProgressDialog mProgressDialog;

	/** 全局的LayoutInflater对象，已经完成初始化. */
	public LayoutInflater mInflater;

	public LinearLayout windowLayout = null;
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
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		mInflater = LayoutInflater.from(this);
		mWindowManager = getWindowManager();
		Display display = mWindowManager.getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();
		mApplication = (DamiApp) getApplication();
	}

	protected void initTitleBar() {
		mTitleBar = new TitleBar(this);
		windowLayout = new LinearLayout(this);
		windowLayout.setOrientation(LinearLayout.VERTICAL);
		windowLayout.addView(mTitleBar, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		windowLayout.addView(ViewUtil.creatTitleBarLineView(mContext));

		contentLayout = new FrameLayout(this);
		contentLayout.setPadding(0, 0, 0, 0);
		contentLayout.setBackgroundColor(getResources().getColor(R.color.general_background));
		windowLayout.addView(contentLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(windowLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	protected void addTitleBar(ViewGroup holder) {
		mTitleBar = new TitleBar(this);
		holder.addView(mTitleBar, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		holder.addView(ViewUtil.creatTitleBarLineView(mContext));
	}

	public void setAbContentView(View contentView) {
		contentLayout.removeAllViews();
		contentLayout.addView(contentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	public void setAbContentView(int resId) {
		setAbContentView(mInflater.inflate(resId, null));
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
		// 此处可以设置dialog显示的位�?
		window.setGravity(gravity);
		// 设置宽度
		lp.width = displayWidth - dialogPadding;
		lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
		// 背景透明
		// lp.screenBrightness = 0.2f;
		lp.alpha = 0.8f;
		lp.dimAmount = 0f;
		window.setAttributes(lp);
		// 添加动画
		window.setWindowAnimations(android.R.style.Animation_Dialog);
		// 设置点击屏幕Dialog不消�?
		dialog.setCanceledOnTouchOutside(false);

	}

	public void showDialog(String title, String msg, DialogInterface.OnClickListener mOkOnClickListener) {
		AlertDialog.Builder builder = new Builder(this);
		if (!TextUtils.isEmpty(msg)) {
			builder.setMessage(msg);
		}
		builder.setTitle(title);
		builder.setPositiveButton("确认", mOkOnClickListener);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

//	public AlertDialog showDialog(String title, View view, DialogInterface.OnClickListener mOkOnClickListener) {
//		AlertDialog.Builder builder = new Builder(this);
//		builder.setTitle(title);
//		builder.setView(view);
//		builder.setPositiveButton("确认", mOkOnClickListener);
//		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}
//		});
//		AlertDialog mAlertDialog = builder.create();
//		mAlertDialog.show();
//		return mAlertDialog;
//	}

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
	
	public void showMutiDialog (String title, String[] array, DialogInterface.OnClickListener onClickListener) {
		AlertDialog dialog = new AlertDialog.Builder(mContext).setItems(array, onClickListener).create();
		if (TextUtils.isEmpty(title)) {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		} else {
			dialog.setTitle(title);
		}
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

	protected UMSocialService mController = UMServiceFactory.getUMSocialService("com.gaopai.guiren");

	protected void initShare() {
		// mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// mController.getConfig().setSinaCallbackUrl("http://www.kaopuhui.com/dami");
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		mController.getConfig().setSsoHandler(
				new QZoneSsoHandler(this, "100424468", "c7394704798a158208a74ab60104f0ba"));
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "100424468", "c7394704798a158208a74ab60104f0ba");
		qqSsoHandler.addToSocialSDK();
		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();
		EmailHandler emailHandler = new EmailHandler();
		emailHandler.addToSocialSDK();
		UMWXHandler wxHandler = new UMWXHandler(this, "wx3d14f400726b7471");
		wxHandler.addToSocialSDK();
		UMWXHandler wxCircleHandler = new UMWXHandler(this, "wx3d14f400726b7471");
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	public void share() {
		mController.setShareContent("aaaaaaaaaaaaaaaaaa");
		mController.openShare(this, new SnsPostListener() {

			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
				// Toast.makeText(BaseActivity.this, "分享成功", 1).show();
			}
		});
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
	}
	
	private boolean mIsRegisterReceiver = false;
	
	//for little actions
	protected IntentFilter registerReceiver(String...actions) {
		IntentFilter filter = new IntentFilter();
		for (String action:actions) {
			filter.addAction(action);
		}
		registerReceiver(mReceiver, filter);
		mIsRegisterReceiver = true;
		return filter;
	}
	
	//for many actions
	protected void registerReceiver(IntentFilter intentFilter) {
		registerReceiver(mReceiver, intentFilter);
		mIsRegisterReceiver = true;
	}
	
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			BaseActivity.this.onReceive(intent);
		}
	};
	
	protected void onReceive(Intent intent){}; 
	
	private void unregisterReceiver() {
		if (mIsRegisterReceiver) {
			unregisterReceiver(mReceiver);
		}
		mIsRegisterReceiver = false;
	}
	

}
