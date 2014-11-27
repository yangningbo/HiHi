package com.gaopai.guiren.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import org.apache.http.util.VersionInfo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.CheckUpdateResult;
import com.gaopai.guiren.bean.Version;
import com.gaopai.guiren.net.DamiException;
import com.gaopai.guiren.volley.SimpleResponseListener;

/**
 * 应用程序更新工具包
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.1
 * @created 2012-6-29
 */
public class UpdateManager {

	private static final int DOWN_NOSDCARD = 0;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private static final int SHOW_FAIL = 3;
	private static final int SHOW_SUCCESS_NONEW = 4;
	private static final int SHOW_SUCCESS_BEGIN = 5;

	private static final int DIALOG_TYPE_LATEST = 0;
	private static final int DIALOG_TYPE_FAIL = 1;

	private static UpdateManager updateManager;
	private Dialog mDialog;

	private Context act;
	// // 通知对话框
	// private Dialog noticeDialog;
	// // 下载对话框
	// private Dialog downloadDialog;
	// // '已经是最新' 或者 '无法获取最新版本' 的对话框
	// private Dialog latestOrFailDialog;
	// // 进度条
	private ProgressBar mProgress;
	// // 显示下载数值
	// private TextView mProgressText;
	// // 查询动画
	// private ProgressDialog mProDialog;
	// // 进度值
	private int progress;
	// 下载线程
	private Thread downLoadThread;
	// 终止标记
	private boolean interceptFlag;
	// 提示语
	private String updateMsg = "";
	// 返回的安装包url
	private String apkUrl = "";
	// 下载包保存路径
	private String savePath = "";
	// apk保存完整路径
	private String apkFilePath = "";
	// 临时下载文件路径
	private String tmpFilePath = "";
	// // 下载文件大小
	private double apkFileSize;
	// // 已下载文件大小
	private double tmpFileSize;
	//
	// private NotificationCompat.Builder builder;
	// private NotificationManager mNotificationManager;
	private int mId;

	private int curVersionCode;
	private Version mVersion;;
	private static View mView;
	private File ApkFile;

	// 是否取消
	private boolean isCancle = false;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				installApk();
				break;
			case DOWN_NOSDCARD:
				Toast.makeText(act, "无法下载安装文件，请检查SD卡是否挂载", Toast.LENGTH_SHORT)
						.show();
				break;
			case SHOW_FAIL:
				showLatestOrFailDialog(DIALOG_TYPE_FAIL);
				break;
			case SHOW_SUCCESS_NONEW:
				showLatestOrFailDialog(DIALOG_TYPE_LATEST);
				break;
			case SHOW_SUCCESS_BEGIN:
				apkUrl = mVersion.url;
				updateMsg = mVersion.description;
				showNoticeDialog();
				break;
			}
		};
	};

	public static UpdateManager getUpdateManager() {
		if (updateManager == null) {
			updateManager = new UpdateManager();
		}
		updateManager.interceptFlag = false;
		return updateManager;
	}

	/**
	 * 检查App更新
	 * 
	 * @param context
	 * @param isShowMsg
	 *            是否显示提示消息
	 */
	View mDialogView;

	public void checkAppUpdate(Context context, final boolean isShowMsg) {
		this.act = context;
		curVersionCode = MyUtils.getVersionCode(context);
		if (isShowMsg) {
			if (mDialog == null) {
				mDialogView = View.inflate(act, R.layout.dialog_notify, null);
				mDialog = showAnimationDialog(mDialogView, -1, true);
				TextView btn_submit = (TextView) mDialogView
						.findViewById(R.id.btn_submit);
				TextView btn_cancle = (TextView) mDialogView
						.findViewById(R.id.btn_cancle);
				TextView tv_value = (TextView) mDialogView
						.findViewById(R.id.tv_value);
				btn_submit.setVisibility(View.GONE);
				tv_value.setText("正在检测，请稍后...");
				btn_cancle.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						isCancle = true;
						closeDialog();
					}
				});
			} else if (mDialog.isShowing()) {
				return;
			}
		}
		checkUpgrade(context, isShowMsg);

	}
	
	private void checkUpgrade(final Context mContext, final boolean isShowMsg) {
		Version version = new Version();
		version.updateTime = System.currentTimeMillis();
		DamiCommon.saveVersionResult(mContext, mVersion);
		DamiInfo.checkUpgrade(new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				CheckUpdateResult data = (CheckUpdateResult) o;
				if (data.state != null && data.state.code == 0) {
					mVersion = data.data;
					mVersion.updateTime = System.currentTimeMillis();
					DamiCommon.saveVersionResult(mContext, mVersion);
					if (mVersion != null && mVersion.hasNewVersion==1) {
						mHandler.sendEmptyMessage(SHOW_SUCCESS_BEGIN);
					} else {
						if (isShowMsg) {
							mHandler.sendEmptyMessage(SHOW_SUCCESS_NONEW);
						}
					}
					closeDialog();
				} else {
					otherCondition(data.state, (Activity) mContext);
				}
			}

		});
	}

//	private void checkUpgrade(final Context mContext, final boolean isShowMsg) {
//		new Thread() {
//			@Override
//			public void run() {
//				if (DamiCommon.verifyNetwork(mContext)) {
//					try {
//						Version version = new Version();
//						version.updateTime = System.currentTimeMillis();
//						DamiCommon.saveVersionResult(mContext, mVersion);
//						CheckUpdateResult versionInfo = DamiCommon.getDamiInfo()
//								.checkUpgrade();
//						if (versionInfo != null && versionInfo.mState != null
//								&& versionInfo.mState.code == 0) {
//							mVersion = versionInfo.mVersion;
//							mVersion.updateTime = System.currentTimeMillis();
//							DamiCommon.saveVersionResult(mContext, mVersion);
//							if (mVersion != null && mVersion.hasNewVersion==1) {
//								mHandler.sendEmptyMessage(SHOW_SUCCESS_BEGIN);
//							} else {
//								if (isShowMsg)
//									mHandler.sendEmptyMessage(SHOW_SUCCESS_NONEW);
//							}
//						}
//					} catch (DamiException e) {
//						e.printStackTrace();
//						closeDialog();
//					}
//				} else {
//					if (isShowMsg)
//						mHandler.sendEmptyMessage(SHOW_FAIL);
//				}
//			}
//		}.start();
//	}

	/**
	 * 显示'已经是最新'或者'无法获取版本信息'对话框
	 */
	private void showLatestOrFailDialog(int dialogType) {
		closeDialog();
		String value = null;
		if (dialogType == DIALOG_TYPE_LATEST) {
			value = "您当前已经是最新版本";
		} else if (dialogType == DIALOG_TYPE_FAIL) {
			value = "无法获取版本更新信息";
		}
		mDialogView = View.inflate(act, R.layout.dialog_notify, null);
		mDialog = showAnimationDialog(mDialogView, -1, true);
		TextView btn_submit = (TextView) mDialogView
				.findViewById(R.id.btn_submit);
		TextView btn_cancle = (TextView) mDialogView
				.findViewById(R.id.btn_cancle);
		TextView tv_value = (TextView) mDialogView.findViewById(R.id.tv_value);
		btn_cancle.setVisibility(View.GONE);
		tv_value.setText(value);
		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				closeDialog();
			}
		});
	}

	/**
	 * 显示版本更新通知对话框
	 */
	private void showNoticeDialog() {
		closeDialog();
		mDialogView = View.inflate(act, R.layout.dialog_notify, null);
		mDialog = showAnimationDialog(mDialogView, -1, true);
		TextView btn_submit = (TextView) mDialogView
				.findViewById(R.id.btn_submit);
		TextView btn_cancle = (TextView) mDialogView
				.findViewById(R.id.btn_cancle);
		TextView tv_value = (TextView) mDialogView.findViewById(R.id.tv_value);
		btn_submit.setText("更新");
		btn_cancle.setText("取消");
		tv_value.setText("发现新版本：\n" + updateMsg);
		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				closeDialog();
				showDownloadDialog();
			}
		});
		btn_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				closeDialog();
			}
		});
	}

	/**
	 * 显示下载对话框
	 */
	private void showDownloadDialog() {
		closeDialog();
		mDialogView = View.inflate(act, R.layout.dialog_update, null);
		mDialog = showAnimationDialog(mDialogView, -1, false);
		TextView btn_cancle = (TextView) mDialogView
				.findViewById(R.id.btn_cancle);
		mProgress = (ProgressBar) mDialogView.findViewById(R.id.progress);
		btn_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				closeDialog();
				interceptFlag = true;
			}
		});
		downloadApk();
	}

	/**
	 * 显示下载对话框
	 */
	private void downloadApk() {
		String apkName = "guiren_" + mVersion.currVersion + ".apk";
		String tmpApk = "guiren_" + mVersion.currVersion + ".tmp";
		// 判断是否挂载了SD卡
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			savePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/Dami/update/";
			File file = new File(savePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			apkFilePath = savePath + apkName;
			tmpFilePath = savePath + tmpApk;
		}

		// 没有挂载SD卡，无法下载文件
		if (apkFilePath == null || apkFilePath == "") {
			mHandler.sendEmptyMessage(DOWN_NOSDCARD);
			return;
		}

		ApkFile = new File(apkFilePath);

		// 是否已下载更新文件
		if (ApkFile.exists()) {
			// downloadDialog.dismiss();
			installApk();
			return;
		}
		Toast.makeText(act, "开始下载新版本", Toast.LENGTH_SHORT).show();
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				String apkName = "studentcardApp_" + mVersion.currVersion + ".apk";
				String tmpApk = "studentcardApp_" + mVersion.currVersion + ".tmp";
				// 判断是否挂载了SD卡
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					savePath = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/Dami/update/";
					File file = new File(savePath);
					if (!file.exists()) {
						file.mkdirs();
					}
					apkFilePath = savePath + apkName;
					tmpFilePath = savePath + tmpApk;
				}

				// 没有挂载SD卡，无法下载文件
				if (apkFilePath == null || apkFilePath == "") {
					mHandler.sendEmptyMessage(DOWN_NOSDCARD);
					return;
				}

				File ApkFile = new File(apkFilePath);

				// 是否已下载更新文件
				if (ApkFile.exists()) {
					installApk();
					return;
				}

				// 输出临时下载文件
				File tmpFile = new File(tmpFilePath);
				FileOutputStream fos = new FileOutputStream(tmpFile);

				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				// 显示文件大小格式：2个小数点显示
				DecimalFormat df = new DecimalFormat("0.00");
				// 进度条下面显示的总文件大小
				apkFileSize = (float) length / 1024 / 1024;

				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numread = is.read(buf);
					count += numread;
					// 进度条下面显示的当前下载文件大小
					tmpFileSize = (float) count / 1024 / 1024;
					// 当前进度值
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成 - 将临时下载文件转成APK文件
						if (tmpFile.renameTo(ApkFile)) {
							// 通知安装
							mHandler.sendEmptyMessage(DOWN_OVER);
						}
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载

				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	private void installApk() {
		closeDialog();
		File apkfile = new File(apkFilePath);
		if (!apkfile.exists()) {
			return;
		}

		Intent i = new Intent(Intent.ACTION_VIEW);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		act.startActivity(i);
	}

	public void closeDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	public Dialog showAnimationDialog(View view, int animation,
			final boolean isClose) {
		Dialog dialog = new Dialog(act, R.style.dialog_middle);
		dialog.setContentView(view);

		Window dialogWindow = dialog.getWindow();
		if (animation > 0)
			dialogWindow.setWindowAnimations(animation); // 设置窗口弹出动画

		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		// lp.height = displayHeight; // 宽度

		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) act).getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		int mWidth = metrics.widthPixels;
		lp.width = (int) (mWidth * 0.8); // 宽度

		dialogWindow.setAttributes(lp);
		if (isClose) {
			dialog.setCanceledOnTouchOutside(true);
		} else {
			dialog.setCanceledOnTouchOutside(false);
		}
		dialog.show();

		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface arg0, int keyCode,
					KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK && !isClose) {
					return true;
				}
				return false; // 默认返回 false
			}
		});
		return dialog;
	}

}
