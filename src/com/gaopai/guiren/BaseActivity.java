package com.gaopai.guiren;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gaopai.guiren.utils.Constant;
import com.gaopai.guiren.utils.StringUtils;
import com.gaopai.guiren.view.TitleBar;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
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
	public TitleBar mTitleBar = null;

	/** 主内容布�?. */
	protected RelativeLayout contentLayout = null;

	/** 屏幕宽度. */
	public int displayWidth = 320;

	/** 屏幕高度. */
	public int displayHeight = 480;

	/** Window 管理�?. */
	private WindowManager mWindowManager = null;

	/** 弹出的Dialog的左右边�?. */
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
		layoutParamsFF = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParamsFW = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParamsWF = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParamsWW = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		// 主标题栏
		mTitleBar = new TitleBar(this);

		ab_base = new RelativeLayout(this);
		ab_base.setBackgroundResource(R.color.welcome_bg_color);
		contentLayout = new RelativeLayout(this);
		contentLayout.setPadding(0, 0, 0, 0);

		// 填入View
		ab_base.addView(mTitleBar, layoutParamsFW);

		RelativeLayout.LayoutParams layoutParamsFW1 = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParamsFW1.addRule(RelativeLayout.BELOW, mTitleBar.getId());
		ab_base.addView(contentLayout, layoutParamsFW1);

		setContentView(ab_base, layoutParamsFF);
	}

	/**
	 * 描述：用指定的View填充主界
	 * 
	 * @param contentView
	 *            指定的View
	 */
	public void setAbContentView(View contentView) {
		contentLayout.removeAllViews();
		contentLayout.addView(contentView, layoutParamsFF);
	}

	/**
	 * 描述：用指定资源ID表示的View填充主界
	 * 
	 * @param resId
	 *            指定的View的资源ID
	 */
	public void setAbContentView(int resId) {
		setAbContentView(mInflater.inflate(resId, null));
	}

	/**
	 * toast提示
	 * 
	 * @param resId
	 */
	public void showToast(int resId) {
		showToast(this.getResources().getText(resId).toString());
	}

	public void showToast(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(BaseActivity.this, "" + text, Toast.LENGTH_SHORT)
						.show();
			}
		});

	}

	
	/**
	 * 结束fragment
	 */
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
				setDialogLayoutParams(mBottomDialog, dialogPadding,
						Gravity.BOTTOM);
			}
			mBottomDialog.setContentView(mBottomDialogView, new LayoutParams(
					displayWidth - dialogPadding,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			showDialog(id);
		} else if (id == Constant.DIALOGCENTER) {
			mCenterDialogView = view;
			if (mCenterDialog == null) {
				mCenterDialog = new Dialog(this);
				setDialogLayoutParams(mCenterDialog, dialogPadding,
						Gravity.CENTER);
			}
			mCenterDialog.setContentView(mCenterDialogView, new LayoutParams(
					displayWidth - dialogPadding,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			showDialog(id);
		} else if (id == Constant.DIALOGTOP) {
			mTopDialogView = view;
			if (mTopDialog == null) {
				mTopDialog = new Dialog(this);
				setDialogLayoutParams(mTopDialog, dialogPadding, Gravity.TOP);
			}
			mTopDialog.setContentView(mTopDialogView, new LayoutParams(
					displayWidth - dialogPadding,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			showDialog(id);
		} else {
			Log.i(TAG, "Dialog的ID传错了，请参考AbConstant类定�?");
		}
	}

	/**
	 * 描述：设置弹出Dialog的属�?.
	 * 
	 * @param dialog
	 *            弹出Dialog
	 * @param dialogPadding
	 *            如果Dialog不是充满屏幕，要设置这个�?
	 * @param gravity
	 *            the gravity
	 */
	private void setDialogLayoutParams(Dialog dialog, int dialogPadding,
			int gravity) {
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

	/**
	 * 描述：对话框dialog （确认，取消�?.
	 * 
	 * @param title
	 *            对话框标题内�?
	 * @param msg
	 *            对话框提示内�?
	 * @param mOkOnClickListener
	 *            点击确认按钮的事件监�?
	 */
	public void showDialog(String title, String msg,
			DialogInterface.OnClickListener mOkOnClickListener) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(msg);
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

	/**
	 * 描述：对话框dialog （确认，取消�?.
	 * 
	 * @param title
	 *            对话框标题内�?
	 * @param view
	 *            对话框提示内�?
	 * @param mOkOnClickListener
	 *            点击确认按钮的事件监�?
	 */
	public AlertDialog showDialog(String title, View view,
			DialogInterface.OnClickListener mOkOnClickListener) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(title);
		builder.setView(view);
		builder.setPositiveButton("确认", mOkOnClickListener);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog mAlertDialog = builder.create();
		mAlertDialog.show();
		return mAlertDialog;
	}

	/**
	 * 描述：对话框dialog （无按钮�?.
	 * 
	 * @param title
	 *            对话框标题内�?
	 * @param msg
	 *            对话框提示内�?
	 */
	public AlertDialog showDialog(String title, String msg) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(msg);
		builder.setTitle(title);
		builder.create();
		AlertDialog mAlertDialog = builder.create();
		mAlertDialog.show();
		return mAlertDialog;
	}

	/**
	 * 描述：对话框dialog （无按钮�?.
	 * 
	 * @param title
	 *            对话框标题内�?
	 * @param view
	 *            对话框提示内�?
	 */
	public AlertDialog showDialog(String title, View view) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(title);
		builder.setView(view);
		builder.create();
		AlertDialog mAlertDialog = builder.create();
		mAlertDialog.show();
		return mAlertDialog;
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if (mTitleBar != null)
			loadDefaultStyle();
	}

	@Override
	protected void onDestroy() {
		MobclickAgent.onPause(this);
		super.onDestroy();
	}

	public void loadDefaultStyle() {
		if (mTitleBar != null) {
			mTitleBar.setTitleBarBackground(R.drawable.title_bar);
			mTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		}
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

	protected UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.gaopai.guiren");

	protected void initShare() {
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		mController.getConfig().setSsoHandler(
				new QZoneSsoHandler(this, "100424468",
						"c7394704798a158208a74ab60104f0ba"));
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "100424468",
				"c7394704798a158208a74ab60104f0ba");
		qqSsoHandler.addToSocialSDK();
		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();
		EmailHandler emailHandler = new EmailHandler();
		emailHandler.addToSocialSDK();
		UMWXHandler wxHandler = new UMWXHandler(this, "wx3d14f400726b7471");
		wxHandler.addToSocialSDK();
		UMWXHandler wxCircleHandler = new UMWXHandler(this,
				"wx3d14f400726b7471");
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}
	
	
	public boolean isModeInCall = false;
	protected void initVoicePlayMode() {
		setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
		isModeInCall = DamiApp.getInstance().getPou()
				.getBoolean(DamiApp.VOOICE_PLAY_MODE, false);
	}

	/**
	 * 
	 * @param isModeInCall
	 *            true 听筒 false 扬声器
	 */
	public void setPlayMode(boolean isModeInCall) {// true ear
		this.isModeInCall = isModeInCall;
		DamiApp.getInstance().getPou()
				.setBoolean(DamiApp.VOOICE_PLAY_MODE, isModeInCall);
	}
	
	

}
