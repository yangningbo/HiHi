package com.gaopai.guiren;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gaopai.guiren.bean.CalenderPrompt;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.Version;
import com.gaopai.guiren.utils.MyUtils;

public class DamiCommon {
	private static Boolean mIsNetWorkAvailable = false;
	private static DamiInfo mDamiInfo = new DamiInfo();
	private static String mUid = "";
	private static String mToken = "";

	public static final String LOGIN_SHARED = "login_shared";
	public static final String LOGIN_RESULT = "login";

	public static final int LOAD_SIZE = 20;
	public static boolean mChatInit = false;
	public static final String SHOWGUDIEVERSION = "version_shared";

	public static final String REMENBER_SHARED = "remenber_shared";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String ISREMENBER = "isRemenber";
	public static boolean isApplicationActive = false;

	public static final String PREFERENCES_NAME = "com_weibo_sdk_android";
	public static final String KEY_UID = "uid";
	public static final String KEY_ACCESS_TOKEN = "access_token";
	public static final String KEY_EXPIRES_IN = "expires_in";

	private static double mCurrentLat = 0;// 30.739198684692383;// -1;
	private static double mCurrentLng = 0;// 103.97882080078125;// -1;
	public static final String LOCATION_SHARED = "location_shared";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public static final String MESSAGE_NOTIFY_SHARED = "message_notify_shared";
	public static final String MESSAGE_NOTIFY = "message_notify";
	public static final String SOUND = "sound";
	
	public final static int BASE_INTEGRA = 500;

	/**
	 * 版本号文件存储常量
	 */
	public static final String VERSION_SHARED = "version_shared"; // SharedPreference名字
	public static final String VERSION_RESULT = "version"; // 版本号Key

	/**
	 * 安装之后首次启动文件存储常量
	 */
	public static final String INSTALL_FIRST_SHARED = "install_first_shared"; // SharedPreference名字
	public static final String FIRST_SPLASH_RESULT = "first_splash"; // 是否是安装之后首次启动Key

	/** +++++++++++++++++++ 错误代码 +++++++++++++++++++++++ */
	public static final int EXPIRED_CODE = 2; // Token已过期
	public static final int PRIVATE_MSG_CONTROL_CODE = 3; // 私信控制发送失败
	public static final int MEETING_NO_START_CODE = 4; // 会议未开始
	public static final int MEETING_IS_OVER_CODE = 5; // 会议已结束
	public static final int MEETING_EXPIRED_CODE = 6; // 会议已过期
	public static final int SENSITIVE_WORD_CODE = 7; // 消息中包含敏感词汇
	public static final int IDENTITY_INVALID_CODE = 8; // 身份失效
	/** ------------------- 错误代码 ------------------------ */

	public static final String CALENDER_PROMPT_SHARED = "calender_prompt_shared";
	public static final String CALENDER_PROMPT_LIST = "calender_prompt_list";

	public static final int SUN_SCORE = 1250; // 一个太阳积分
	public static final int MOON_SCORE = 250; // 一个月亮积分
	public static final int STAR_SCORE = 50; // 一个星星积分

	/** +++++++++++++++++ 行为统计 ++++++++++++++++++ **/
	public static final String ADVERTISEMENT = "advertisement";
	public static final String TABBAR = "tabbar";
	public static final String CHATROOM = "chatroom";
	/** ----------------- 行为统计 ------------------ **/

	public static final int SAMPLERATE_LIMIT_LOWER = 1500;
	public static final int SAMPLERATE_LIMIT_HIGN = 1700;
	public static final int SAMPLERATE_OFFSET = 800;

	public static int mPlaySimpleRate = 8000;
	public static final int MESSAGE_CONTENT_LEN = 5000;
	private static int mIsReceive = -1;

	public static void setNetWorkState(boolean state) {
		mIsNetWorkAvailable = state;
	}

	public static boolean getNetWorkState() {
		return mIsNetWorkAvailable;
	}

	public static boolean verifyNetwork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null) {
			if (activeNetInfo.isConnected()) {
				setNetWorkState(true);
				return true;
			} else {
				setNetWorkState(false);
				return false;
			}
		} else {
			setNetWorkState(false);
			return false;
		}
	}

	public static DamiInfo getDamiInfo() {
		return mDamiInfo;
	}

	public static void saveLoginResult(Context context, User user) {
		SharedPreferences preferences = context.getSharedPreferences(LOGIN_SHARED, Context.MODE_MULTI_PROCESS);
		Editor editor = preferences.edit();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(user);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 将Product对象放到OutputStream中
		// 将Product对象转换成byte数组，并将其进行base64编码
		String server = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
		// 将编码后的字符串写到base64.xml文件中
		editor.putString(LOGIN_RESULT, server);

		editor.commit();
	}

	public static User getLoginResult(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(LOGIN_SHARED, Context.MODE_MULTI_PROCESS);
		String str = preferences.getString(LOGIN_RESULT, "");
		User user = null;
		if (str.equals("")) {
			return null;
		}
		// 对Base64格式的字符串进行解码
		byte[] base64Bytes = Base64.decode(str.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(bais);
			// 从ObjectInputStream中读取Product对象
			// AddNewWord addWord= (AddNewWord ) ois.readObject();
			user = (User) ois.readObject();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return user;
	}

	public static boolean isLogin(Context context) {
		if (getLoginResult(context) == null) {
			return false;
		}

		return true;
	}

	public static void setUid(String uid) {
		mUid = uid;
	}

	public static String getUid(Context context) {
		String uid = "";
		if (mUid.equals("")) {
			User user = getLoginResult(context);
			if (user != null) {
				uid = user.uid;
			}
		} else {
			uid = mUid;
		}
		return uid;
	}

	public static void setToken(String token) {
		mToken = token;
	}

	public static String getToken(Context context) {
		String token = "";
		if (mToken.equals("")) {
			User user = getLoginResult(context);
			if (user != null) {
				token = user.token;
			}
		} else {
			token = mToken;
		}
		return token;
	}

	public static void setCurrentLat(double lat) {
		mCurrentLat = lat;
	}

	public static void setCurrentLng(double lng) {
		mCurrentLng = lng;
	}

	public static double getLat() {
		return mCurrentLat;
	}

	public static double getLng() {
		return mCurrentLng;
	}

	public static double getCurrentLat(Context context) {
		double lat = 0;
		if (mCurrentLat > 0) {
			lat = mCurrentLat;
		} else {
			SharedPreferences preferences = context.getSharedPreferences(LOCATION_SHARED, 0);
			String latStr = preferences.getString(LAT, "0");
			lat = Double.parseDouble(latStr);
			mCurrentLat = lat;
		}

		return lat;
	}

	public static double getCurrentLng(Context context) {
		double lng = 0;
		if (mCurrentLng > 0) {
			lng = mCurrentLng;
		} else {
			SharedPreferences preferences = context.getSharedPreferences(LOCATION_SHARED, 0);
			String latStr = preferences.getString(LNG, "0");
			lng = Double.parseDouble(latStr);
			mCurrentLng = lng;
		}

		return lng;
	}

	public static void sendMsg(Handler hander, int what, String string) {
		Message hintMsg = new Message();
		hintMsg.what = what;
		hintMsg.obj = string;
		hander.sendMessage(hintMsg);
	}

	public class GPSInfo {
		public double lat;
		public double lng;
	}

	public static void setAcceptMsgAuth(Context context, boolean isReceive) {

		if (isReceive) {
			mIsReceive = 1;
		} else {
			mIsReceive = 0;
		}

		int mode = Context.MODE_WORLD_WRITEABLE;
		if (Build.VERSION.SDK_INT >= 11) {
			mode = Context.MODE_MULTI_PROCESS;
		}
		SharedPreferences preferences = context.getSharedPreferences(MESSAGE_NOTIFY_SHARED, mode);
		Editor editor = preferences.edit();
		editor.putBoolean(MESSAGE_NOTIFY, isReceive);
		editor.commit();
	}

	public static void setOpenSound(Context context, boolean isOpen) {
		SharedPreferences preferences = context.getSharedPreferences(MESSAGE_NOTIFY_SHARED, 0);
		Editor editor = preferences.edit();
		editor.putBoolean(SOUND, isOpen);
		editor.commit();
	}

	public static boolean getAcceptMsgAuth(Context context) {
		boolean isReceive = true;
		if (mIsReceive == -1) {
			int mode = Context.MODE_WORLD_WRITEABLE;
			if (Build.VERSION.SDK_INT >= 11) {
				mode = Context.MODE_MULTI_PROCESS;
			}
			SharedPreferences preferences = context.getSharedPreferences(MESSAGE_NOTIFY_SHARED, mode);
			isReceive = preferences.getBoolean(MESSAGE_NOTIFY, true);
		} else {
			if (mIsReceive == 1) {
				isReceive = true;
			} else if (mIsReceive == 0) {
				isReceive = false;
			}
		}

		return isReceive;

	}

	public static boolean getOpenSound(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(MESSAGE_NOTIFY_SHARED, 0);
		boolean isOpen = true;
		isOpen = preferences.getBoolean(SOUND, true);
		return isOpen;
	}

	public static void saveVersionResult(Context context, Version version) {
		SharedPreferences preferences = context.getSharedPreferences(VERSION_SHARED, 0);
		Editor editor = preferences.edit();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(version);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 将Product对象放到OutputStream中
		// 将Product对象转换成byte数组，并将其进行base64编码
		String server = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
		// 将编码后的字符串写到base64.xml文件中
		editor.putString(VERSION_RESULT, server);

		editor.commit();
	}

	public static Version getVersionResult(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(VERSION_SHARED, 0);
		String str = preferences.getString(VERSION_RESULT, "");
		Version version = null;
		if (str.equals("")) {
			return null;
		}
		// 对Base64格式的字符串进行解码
		byte[] base64Bytes = Base64.decode(str.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(bais);
			// 从ObjectInputStream中读取Product对象
			// AddNewWord addWord= (AddNewWord ) ois.readObject();
			version = (Version) ois.readObject();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return version;
	}

	public static void savePromptList(Context context, List<CalenderPrompt> promptList) {
		SharedPreferences preferences = context.getSharedPreferences(CALENDER_PROMPT_SHARED, 0);
		Editor editor = preferences.edit();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(promptList);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 将Product对象放到OutputStream中
		// 将Product对象转换成byte数组，并将其进行base64编码
		String server = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
		// 将编码后的字符串写到base64.xml文件中
		editor.putString(CALENDER_PROMPT_LIST, server);

		editor.commit();
	}

	public static List<CalenderPrompt> getPromptList(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(CALENDER_PROMPT_SHARED, 0);
		String str = preferences.getString(CALENDER_PROMPT_LIST, "");
		List<CalenderPrompt> promptList = null;
		if (str.equals("")) {
			return null;
		}
		// 对Base64格式的字符串进行解码
		byte[] base64Bytes = Base64.decode(str.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(bais);
			// 从ObjectInputStream中读取Product对象
			// AddNewWord addWord= (AddNewWord ) ois.readObject();
			promptList = (List<CalenderPrompt>) ois.readObject();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return promptList;
	}


	// public static int getRandomSampleRate() {
	// Random r = new Random();
	//
	// int sampleRate = 0;
	//
	// int fh = 1 + r.nextInt(5);
	// switch (fh) {
	// case 1:
	// sampleRate = 14350;
	// break;
	//
	// case 2:
	// sampleRate = 17600;
	// break;
	//
	// case 3:
	// sampleRate = 17800;
	// break;
	//
	// case 4:
	// sampleRate = 18000;
	// break;
	//
	// case 5:
	// sampleRate = 20000;
	// break;
	//
	// }
	// return sampleRate;
	// }

	public static int getRandomSampleRate() {
		Random r = new Random();

		int sampleRate = 0;

		int fh = 1 + r.nextInt(5);
		switch (fh) {
		case 1:
			sampleRate = 6350;
			break;

		case 2:
			sampleRate = 15000;
			break;

		case 3:
			sampleRate = 14000;
			break;

		case 4:
			sampleRate = 13000;
			break;

		case 5:
			sampleRate = 12000;
			break;

		}
		return sampleRate;
	}

	public static void saveInstallFirst(Context context, boolean isFirst) {
		SharedPreferences preferences = context.getSharedPreferences(INSTALL_FIRST_SHARED, 0);
		Editor editor = preferences.edit();
		editor.putBoolean(FIRST_SPLASH_RESULT + DamiCommon.getUid(context), isFirst);
		editor.commit();
	}

	public static boolean getInstallFirst(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(INSTALL_FIRST_SHARED, 0);
		return preferences.getBoolean(FIRST_SPLASH_RESULT + DamiCommon.getUid(context), true);
	}

	public static void setChatType(Context context, int chatType) {

		int mode = Context.MODE_WORLD_WRITEABLE;
		if (Build.VERSION.SDK_INT >= 11) {
			mode = Context.MODE_MULTI_PROCESS;
		}
		SharedPreferences preferences = context.getSharedPreferences(MESSAGE_NOTIFY_SHARED, mode);
		Editor editor = preferences.edit();
		editor.putInt("chat_type", chatType);
		editor.commit();
	}

	public static int getChatType(Context context) {
		int chatType = 0;
		int mode = Context.MODE_WORLD_WRITEABLE;
		if (Build.VERSION.SDK_INT >= 11) {
			mode = Context.MODE_MULTI_PROCESS;
		}
		SharedPreferences preferences = context.getSharedPreferences(MESSAGE_NOTIFY_SHARED, mode);
		chatType = preferences.getInt("chat_type", 0);

		return chatType;
	}
}
