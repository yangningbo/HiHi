package com.gaopai.guiren.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.gaopai.guiren.FeatureFunction;

public class MyUtils {

	/**
	 * 获取版本Name
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	public static boolean checkSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	public static boolean isNetConnected(Context context) {
		boolean isNetConnected;
		// 获得网络连接服务
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			// String name = info.getTypeName();
			// L.i("当前网络名称：" + name);
			isNetConnected = true;
		} else {
			// Log.i(" ","没有可用网络");
			isNetConnected = false;
		}
		return isNetConnected;
	}

	public static boolean isDate(String date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		Date d = null;
		try {
			d = df.parse(date);
		} catch (Exception e) {
			// 如果不能转换,肯定是错误格式
			return false;
		}
		String s1 = df.format(d);
		// 转换后的日期再转换回String,如果不等,逻辑错误.如format为"yyyy-MM-dd",date为
		// "2006-02-31",转换为日期后再转换回字符串为"2006-03-03",说明格式虽然对,但日期
		// 逻辑上不对.
		return date.equals(s1);
	}

	/**
	 * 比较两个时间（hh:mm:ss）大小
	 * 
	 * @param date1
	 * @param date2
	 * @return true 前者大 false 后者大
	 */
	public static boolean compare2Date(String date1, String date2) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d1 = null;
		Date d2 = null;
		if (!StringUtils.isEmpty(date1) && StringUtils.isEmpty(date2)) {
			return true;
		}
		try {
			d1 = sdf.parse(date1 + ":00");
			d2 = sdf.parse(date2 + ":00");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (d1.getTime() > d2.getTime()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	/**
	 * 隐藏键盘
	 * 
	 * @param context
	 * @param v
	 */
	public static void hideKeyboard(Context context, View v) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	public static void hideKeyboard(Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
		if (isOpen)
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 显示键盘
	 * 
	 * @param context
	 */
	public static void showKeyboard(Context context, View v) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
	}

	public static double round(double value, int scale, int roundingMode) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, roundingMode);
		double d = bd.doubleValue();
		bd = null;
		return d;
	}

	/**
	 * 
	 * @param cxt
	 * @return
	 */
	public static String getUrl(Context cxt) {
		InputStream fis = null;
		try {
			fis = cxt.getResources().getAssets().open("pro.properties");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Properties pro = new Properties();

		try {
			pro.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return pro.getProperty("com.asktun.json.api.url");
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppId(Context context) {
		return getMetaData(context, "appID");
	}

	public static String getMetaData(Context cxt, String key) {
		ApplicationInfo appInfo = null;
		try {
			appInfo = cxt.getPackageManager().getApplicationInfo(cxt.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		Bundle b = appInfo.metaData;
		String str = b.getString(key);
		if (str == null) {
			return b.getInt(key) + "";
		} else {
			return str;
		}
	}

	/**
	 * 获取屏幕尺寸
	 * 
	 * @param context
	 * @return
	 */
	public static DisplayMetrics getScreenSize(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Resources res, float dpValue) {
		final float scale = res.getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static String addZero_2(int i) {
		String pattern = "00";
		java.text.DecimalFormat df = new java.text.DecimalFormat(pattern);
		return df.format(i);
	}

	/**
	 * 
	 * @param path
	 *            被遍历的文件夹的地址
	 * 
	 * @return 返回保存当前文件夹根目录下的所有音频文件 完整路径List集合
	 */
	public static ArrayList<String> GetAllImagesFilesPathFromFolder(String path) {
		ArrayList<String> AudioPathList = new ArrayList<String>();
		File file = new File(path);
		File[] files = null;
		if (file.exists()) {

			// check all mp3 file
			files = file.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					if (pathname.getName().matches("^.*?\\.(jpg|png|bmp|gif|jpeg)$")) {
						return true;
					}
					return false;
				}
			});
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					AudioPathList.add(files[i].getAbsolutePath());
				}
			}
		}

		return AudioPathList;
	}

	public static String getIMEI(Context context) {
		TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String szImei = TelephonyMgr.getDeviceId();
		return szImei;
	}

	public static String getAudioName() {
		Date date = new Date(System.currentTimeMillis());

		SimpleDateFormat format = new SimpleDateFormat("'AUDIO'_yyyyMMdd_HHmmss");
		return format.format(date) + ".spx";
	}

	/**
	 * 获取外部内存路径.（SD卡不能使用时，会抛出Io异常）
	 * 
	 * @param context
	 * @return 作者:fighter <br />
	 *         创建时间:2013-6-4<br />
	 *         修改时间:<br />
	 */
	public static File getAudioPath(Context context) {
		File file = new File(FeatureFunction.getExternalCacheDir(context), "voice");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	public static void clearCache(Context context) {
		File file = FeatureFunction.getExternalCacheDir(context);
		deleteFolder(file);
	}

	private static void deleteFolder(File file) {
		if (file == null) {
			return;
		}
		if (file.isFile()) {
			if (file.exists()) {
				file.delete();
			}
		} else {
			File[] files = file.listFiles();
			if (files == null) {
				// file.delete();
				return;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					if (files[i].exists()) {
						files[i].delete();
					}
				} else {
					deleteFolder(files[i]);
				}
			}
			// file.delete();
		}
	}

	public static String getCacheSize(Context context) {
		int mb = 0;
		int kb = 0;
		long size = getFolderSize(FeatureFunction.getExternalCacheDir(context).getAbsolutePath());
		kb = (int) (size / 1024);
		mb = kb / 1024;
		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		return decimalFormat.format(mb + (kb - mb * 1024)/1024f);
	}

	public static long getFolderSize(String dir) {
		File file = new File(dir);
		long size = 0;
		if (!file.exists()) {
			return 0;
		}
		if (file.isFile())
			return file.length();
		else {
			String[] arrFileName = file.list();
			for (int i = 0; i < arrFileName.length; i++) {
				size += getFolderSize(dir + "/" + arrFileName[i]);
			}
		}

		return size;
	}
	
	public static void makePhonecall(Context context, String phone) {
		Intent Telintent = new Intent();
		Telintent.setAction(Intent.ACTION_CALL);
		Telintent.setData(Uri.parse("tel:" + phone));
		try {
			context.startActivity(Telintent);
		} catch (Exception e) {
		}
	}
}
