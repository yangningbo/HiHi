package com.gaopai.guiren;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.MediaColumns;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.gaopai.guiren.service.SnsService;

public class FeatureFunction {
	private static final String TAG = "FeatureFunction";
	private static final int ONE_MINUTE = 60; // Seconds
	private static final int ONE_HOUR = 60 * ONE_MINUTE;
	private static final int ONE_DAY = 24 * ONE_HOUR;

	public static final String PUB_TEMP_DIRECTORY = "/Dami/";
	public static final long TIME = 300000;

	/**
	 * Check SD card
	 * 
	 * @return true if SD card is mounted
	 */
	public static boolean checkSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	public static String calculaterReleasedTime(Context context, Date date) {
		Date currentDate = new Date();
		long duration = (currentDate.getTime() - date.getTime()) / 1000; // Seconds

		// Not normal
		if (currentDate.before(date)) {
			if (Math.abs(duration) < ONE_MINUTE * 5) {
				return context.getString(R.string.just_now);
			} else {
				return getDateString(context, date,
						currentDate.getYear() != date.getYear());
			}
		}

		if (duration >= ONE_DAY) {
			return getDateString(context, date,
					currentDate.getYear() != date.getYear());
		} else if (duration >= ONE_HOUR) {
			return duration / ONE_HOUR + context.getString(R.string.hour)
					+ context.getString(R.string.before);
		} else if (duration >= ONE_MINUTE) {
			return duration / ONE_MINUTE + context.getString(R.string.minutes)
					+ context.getString(R.string.before);
		} else {
			return duration + context.getString(R.string.second)
					+ context.getString(R.string.before);
		}
	}

	public static String calculateFileSize(long size) {
		if (size < 1024l) {
			return size + "B";
		} else if (size < (1024l * 1024l)) {
			return Math.round((size * 100 >> 10)) / 100.00 + "KB";
		} else if (size < (1024l * 1024l * 1024l)) {
			return (Math.round((size * 100 >> 20)) / 100.00) + "MB";
		} else {
			return Math.round((size * 100 >> 30)) / 100.00 + "GB";
		}
	}

	public static String getDateString(Context context, Date date,
			boolean withYearString) {
		String time = "";
		if (withYearString) {
			time += (date.getYear() + 1900) + context.getString(R.string.year);
		}

		return time + (date.getMonth() + 1) + context.getString(R.string.month)
				+ date.getDate() + context.getString(R.string.day);
	}

	public static int chineseCompare(String chineseString1,
			String chineseString2) {
		return Collator.getInstance(Locale.CHINESE).compare(chineseString1,
				chineseString2);
	}

	public static boolean createWholePermissionFolder(String path) {
		Log.d(TAG, "+ createWholePermissionFolder()");

		Process p;
		int status = -1;
		boolean isSuccess = false;

		try {
			File destDir = new File(path);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}

			p = Runtime.getRuntime().exec("chmod 777 " + destDir);
			status = p.waitFor();
			if (status == 0) {
				Log.d(TAG, "Modify folder permission success!");
				isSuccess = true;
			} else {
				Log.i(TAG, "Modify folder permission fail!");
			}
		} catch (Exception e) {
			Log.i(TAG, "Modify folder permission exception!: " + e.toString());
		}

		Log.d(TAG, "- createWholePermissionFolder()");
		return isSuccess;
	}

	public static String saveTempBitmap(Bitmap bitmap, String fileName) {
		if (bitmap == null || fileName == null || fileName.length() == 0) {
			Log.i(TAG, "saveTempBitmap(), illegal param, bitmap = " + bitmap
					+ "filename = " + fileName);
			return "";
		}

		createWholePermissionFolder(Environment.getExternalStorageDirectory()
				+ PUB_TEMP_DIRECTORY);
		File bitmapFile = new File(Environment.getExternalStorageDirectory()
				+ PUB_TEMP_DIRECTORY, fileName);
		FileOutputStream bitmapWriter;
		String retPath = "";
		try {
			bitmapWriter = new FileOutputStream(bitmapFile);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bitmapWriter)) {
				Log.d("TAG", "Save picture successfully! file name = "
						+ PUB_TEMP_DIRECTORY + fileName);
				bitmapWriter.flush();
				bitmapWriter.close();
				retPath = Environment.getExternalStorageDirectory()
						+ PUB_TEMP_DIRECTORY + fileName;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return retPath;
	}
	
	public static Bitmap scalePicture(String filename) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeFile(filename, opt);
			int picWidth = opt.outWidth;
			int picHeight = opt.outHeight;

			int width = 1024;

			// isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
			opt.inSampleSize = 1;
			if (picWidth > picHeight) {
				if (picWidth > width) {
					opt.inSampleSize = picWidth / width;
				}
			} else {
				if (picHeight > width) {
					opt.inSampleSize = picHeight / width;
				}
			}

			// 这次再真正地生成一个有像素的，经过缩放了的bitmap
			opt.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(filename, opt);
			int afterwidth = bitmap.getWidth();
			int afterheight = bitmap.getHeight();
			float con = 1.0f;
			int bigger = afterheight > afterwidth ? afterheight : afterwidth;
			if (bigger > width) {
				con = (float) width / bigger;
			}

			bitmap = Bitmap.createScaledBitmap(bitmap, (int) (con * afterwidth), (int) (con * afterheight), true);
		} catch (Exception e) {
			// TODO: handle exception]
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * Judge if the characters in the string are all number
	 * 
	 * @param str
	 * @return
	 * @author mikewu
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static int dip2px(Context context, int dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getAppVersion(Context context) {

		int versionCode = 0;

		try {

			PackageManager pm = context.getPackageManager();

			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);

			versionCode = pi.versionCode;

		} catch (Exception e) {

			Log.i(TAG, "Exception", e);

		}
		return versionCode;
	}

	public static String getAppVersionName(Context context) {

		String versionName = "";

		try {

			PackageManager pm = context.getPackageManager();

			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);

			versionName = pi.versionName;

			if (versionName == null || versionName.length() <= 0) {

				return "";

			}

		} catch (Exception e) {

			Log.i(TAG, "Exception", e);

		}
		return versionName;
	}

	public static String replaceHtml(String html) {
		String regEx = "<.+?>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}

	public static void freeBitmap(HashMap<String, Bitmap> cache) {
		if (cache.isEmpty()) {
			return;
		}
		for (Bitmap bitmap : cache.values()) {
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;

			}
		}
		cache.clear();
		System.gc();
	}

	public static String getRefreshTime() {
		String strDate = "";
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		strDate = formatter.format(curDate);

		return strDate;
	}

	public static String getHour() {
		String strDate = "";
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		strDate = formatter.format(curDate);

		return strDate;
	}

	public static String getChatTime(long time) {
		String strDate = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date curDate = new Date(time);// 获取当前时间
		strDate = formatter.format(curDate);

		return strDate;
	}
	
	public static String getGeneralTime(long time) {
		String strDate = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(time);// 获取当前时间
		strDate = formatter.format(curDate);
		
		return strDate;
	}

//	public static String getNoYearTime(long time) {
//		String strDate = "";
//		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
//		Date curDate = new Date(time);// 获取当前时间
//		strDate = formatter.format(curDate);
//
//		return strDate;
//	}

	public static String getFormatTime(String time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/M/d HH:mm:ss");
		Date d;
		String formattime = "";
		try {
			d = formatter.parse(time);
			SimpleDateFormat formatter2 = new SimpleDateFormat("MM-dd HH:mm:ss");
			formattime = formatter2.format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return formattime;
	}

	public static boolean newFolder(String folderPath) {
		try {
			String filePath = folderPath;
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.mkdirs();
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String generator(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private static String bytesToHexString(byte[] bytes) {
		// http://stackoverflow.com/questions/332079
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	public static boolean reNameFile(File file, String newName) {
		return file.renameTo(new File(file.getParentFile(), newName));
	}

	public static boolean isPic(String filename) {

		String strPattern = "^.((jpg)|(png)|(jpeg))$";

		Pattern p = Pattern.compile(strPattern, Pattern.CASE_INSENSITIVE);

		Matcher m = p.matcher(filename);
		Log.d("m.matches()", String.valueOf(m.matches()));

		return m.matches();

	}

	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";

	}

	public static Date getTimeDate(long time) {
		// String strDate = "";
		// SimpleDateFormat formatter = new
		// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date curDate = new Date(time);// 获取当前时间
		return curDate;
	}

	/**
	 * 保留一位小数点
	 * 
	 * @param f
	 * @return 作者:fighter <br />
	 *         创建时间:2013-6-13<br />
	 *         修改时间:<br />
	 */
	public static String floatMac1(float f) {
		DecimalFormat decimalFormat = new DecimalFormat("####.#");
		try {
			return decimalFormat.format(f);
		} catch (Exception e) {
			return f + "";
		}
	}

	public static String floatMac(String floatStr) {
		DecimalFormat decimalFormat = new DecimalFormat("####.#");
		try {
			float f = Float.parseFloat(floatStr);
			return decimalFormat.format(f);
		} catch (Exception e) {
			return floatStr;
		}
	}


	/**
	 * 程序是否在前台运行
	 * 
	 * @return
	 */
	public static boolean isAppOnForeground(Context context) {
		// Returns a list of application processes that are running on the
		// device

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = context.getApplicationContext().getPackageName();

		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}

	public static String getFilePathByContentResolver(Context context, Uri uri) {
		if (null == uri) {
			return null;
		}
		Cursor c = context.getContentResolver().query(uri, null, null, null,
				null);
		String filePath = null;
		if (null == c) {
			throw new IllegalArgumentException("Query on " + uri
					+ " returns null result.");
		}
		try {
			if ((c.getCount() != 1) || !c.moveToFirst()) {
			} else {
				filePath = c.getString(c
						.getColumnIndexOrThrow(MediaColumns.DATA));
			}
		} finally {
			c.close();
		}
		return filePath;
	}

	
	public static boolean isEmail(String strEmail) {

		String strPattern = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,4}$";

		Pattern p = Pattern.compile(strPattern);

		Matcher m = p.matcher(strEmail);
		Log.d("m.matches()", String.valueOf(m.matches()));

		return m.matches();
	}

	public static String showdate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		int cYear = calendar.get(Calendar.YEAR);
		int cMonth = calendar.get(Calendar.MONTH);
		int cDay = calendar.get(Calendar.DAY_OF_MONTH);

		if (year > cYear) {
			return "";
		}

		if (year == cYear && month > cMonth) {
			return "";
		}

		if (year == cYear && month == cMonth && day > cDay) {
			return "";
		}

		int trueMonth = (month + 1);
		String sMonth = trueMonth > 9 ? (trueMonth + "") : ("0" + trueMonth);
		String sDay = day > 9 ? (day + "") : ("0" + day);
		String date = year + "-" + sMonth + "-" + sDay;
		return date;
	}

	/**
	 * 用来判断服务是否运行.
	 * 
	 * @param context
	 * @param className
	 *            判断的服务名字
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(
					"com.getup.service." + className)) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	public static boolean isMobileNum(String mobiles) {
		Pattern pattern = Pattern.compile("^(1(3|5|8)[0-9]{9})$");// "^13/d{9}||15[8,9]/d{8}$");
		Matcher m = pattern.matcher(mobiles);
		if (m.matches()) {
			return true;
		}
		return false;
	}

	public static String getDistance(double distance) {
		if (distance < 1000) {
			return ((int) distance) + "m";
		} else if (distance > 1000 * 1000) {
			return ">1000km";
		} else {
			// String dis = String.valueOf(distance/1000);
			// int index = dis.indexOf(".") + 2;
			// String subString = dis.subSequence(0, index).toString();

			distance = Math.floor(distance / 1000 * 10) / 10;
			return distance + "km";
		}
	}

	/**
	 * 获取程序外部的缓存目录
	 * 
	 * @param context
	 * @return
	 */
	public static File getExternalCacheDir(Context context) {
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
	}

	/*
	 * public static void startService(Context context){ Intent intent = new
	 * Intent(context, PushService.class); context.startService(intent); }
	 * 
	 * public static void stopService(Context context){ Intent intent = new
	 * Intent(context, PushService.class); context.stopService(intent); }
	 */

	public static String getMac() {
		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/wlan0/address");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return macSerial;
	}

	public static String getDeviceID() {
		TelephonyManager manager = (TelephonyManager) DamiApp.getInstance()
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceID = manager.getDeviceId();
		if (TextUtils.isEmpty(deviceID) || deviceID.equals("0000000000000")) {
			deviceID = getMac();
			if (TextUtils.isEmpty(deviceID)) {
				deviceID = UUID.randomUUID().toString();
			}
		}
		return deviceID;
	}

	public static void startService(Context context) {
		if (DamiCommon.isLogin(context)) {
			Intent intent = new Intent(context, SnsService.class);
			context.startService(intent);
		}
	}

	public static void stopService(Context context) {
		Intent intent = new Intent(context, SnsService.class);
		context.stopService(intent);
	}
	
	

	



}
