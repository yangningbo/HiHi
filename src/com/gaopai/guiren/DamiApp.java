package com.gaopai.guiren;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.tsz.afinal.FinalBitmap;
import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.os.Environment;
import android.util.Log;

import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.PreferenceOperateUtils;
import com.gaopai.guiren.volley.MyVolley;
import com.gaopai.guiren.volley.UIHelperUtil;
import com.google.gson.Gson;
import com.iflytek.cloud.SpeechUtility;

/**
 * DamiApp操作与程序全局相关的方法
 * <p>
 * 包括：
 * </p>
 * <ul>
 * <li>初始化图片加载框架</li>
 * <li>初始化preference工具</li>
 * <li>设置播放语音的模式</li>
 * <li>写入错误日志</li>
 * </ul>
 * 
 */
public class DamiApp extends Application {

	/** 静态变量，唯一sington */
	public static DamiApp mApp;

	public static final String downloadPath = Environment
			.getExternalStorageDirectory().getPath() + "/Dami/";
	public static final String VOOICE_PLAY_MODE = "voice_play_mode";

	/**
	 * 操作preference的工具，包括读取和写入
	 * 
	 * @see PreferenceOperateUtils
	 */
	private PreferenceOperateUtils pou;
	private FinalBitmap fb;
	private Gson gson;

	@Override
	public void onCreate() {
		super.onCreate();
		mApp = this;
		SpeechUtility.createUtility(DamiApp.this, "appid="
				+ getString(R.string.app_id));
		UIHelperUtil.cxt = getBaseContext();
		MyVolley.init(this);
		ImageLoaderUtil.init(this);
		pou = new PreferenceOperateUtils(this);
		gson = new Gson();
		fb = FinalBitmap.create(getApplicationContext());

		Thread.setDefaultUncaughtExceptionHandler(handler); // 打印错误

	}

	UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			writeErrorLog(ex);
			System.exit(0);
		}
	};

	private static final String LOG_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/Dami/log/";
	private static final String LOG_NAME = getCurrentDateString() + ".txt";

	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	private static String getCurrentDateString() {
		String result = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		Date nowDate = new Date();
		result = sdf.format(nowDate);
		return result;
	}

	/**
	 * 打印错误日志
	 * 
	 * @param ex
	 */
	protected void writeErrorLog(Throwable ex) {
		String info = null;
		ByteArrayOutputStream baos = null;
		PrintStream printStream = null;
		try {
			baos = new ByteArrayOutputStream();
			printStream = new PrintStream(baos);
			ex.printStackTrace(printStream);
			byte[] data = baos.toByteArray();
			info = new String(data);
			data = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (printStream != null) {
					printStream.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Log.d("app_crash", "崩溃信息\n" + info);
		File dir = new File(LOG_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, LOG_NAME);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file, true);
			fileOutputStream.write(info.getBytes());
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static DamiApp getInstance() {
		return mApp;
	}
	
	public static PreferenceOperateUtils getPo() {
		return mApp.pou;
	}

	public PreferenceOperateUtils getPou() {
		return pou;
	}

	public FinalBitmap getFb() {
		return fb;
	}

	public Gson getGson() {
		return gson;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}


	public void setPlayMode() {
		if (checkHEADSET())
			setSpeakerphoneOn(true);
		else {
			boolean mode = !pou.getBoolean(VOOICE_PLAY_MODE, false);
			Log.d("CHEN", "Set Mode       "+ mode+"");
			setSpeakerphoneOn(mode);
		}
	}

	/**
	 * 设置播放模式
	 * 
	 * @param on
	 *            true喇叭，false听筒
	 */

	public void setSpeakerphoneOn(boolean on) {
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (on) {
			am.setSpeakerphoneOn(true);
			am.setMode(AudioManager.MODE_NORMAL);
		} else {
			am.setSpeakerphoneOn(false);// 关闭扬声器
			am.setMode(AudioManager.MODE_IN_CALL);
		}
	}

	/**
	 * 
	 * @return true 表示有蓝牙或耳机插入
	 */
	private boolean checkHEADSET() {
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (am.isWiredHeadsetOn() || am.isBluetoothA2dpOn()) {
			return true;
		}
		return false;
	}
}
