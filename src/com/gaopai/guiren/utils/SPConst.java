package com.gaopai.guiren.utils;

import android.content.Context;
import android.os.Build;

import com.gaopai.guiren.DamiCommon;

public class SPConst {
	
	//用来储存免打扰的会议或部落
	public final static String SP_AVOID_DISTURB = "sp_avoid_disturb";
	public static String getTribeUserId(Context context, String tribeId) {
		return DamiCommon.getUid(context)+"&"+tribeId;
	}
	
	
	public final static String SP_SETTING = "sp_setting";
	public final static String KEY_AVOID_DISTURB_TIME_SEGMENT = "avoid_disturb_time_segment";
	public final static String KEY_AVOID_DISTURB_HOUR_FROM = "avoid_disturb_hour_from";
	public final static String KEY_AVOID_DISTURB_MINUTE_FROM = "avoid_disturb_minute_from";
	public final static String KEY_AVOID_DISTURB_HOUR_TO = "avoid_disturb_hour_to";
	public final static String KEY_AVOID_DISTURB_MINUTE_TO = "avoid_disturb_minute_to";
	public final static String KEY_NOTIFY_PLAY_RINGTONES = "notify_play_ringtones";
	public final static String KEY_NOTIFY_VIBRATE = "notify_vibrate";
	public final static String KEY_NOTIFY_DAMI = "notify_dami";
	
	//default
	public final static String KEY_HAS_NOTIFICATION = "has_notification";
	
	public final static String SP_ALARM = "sp_alarm";
	public static String getSingleSpId(Context context, String tribeId) {
		return DamiCommon.getUid(context)+"&"+tribeId;
	}
	
	public final static String SP_ANONY = "sp_anony";
	//0 实名 1匿名
	
	
	public final static String SP_DEFAULT = "sp_default";
	public final static String KEY_CHAT_CURRENT_ID = "chat_current_id";
	public static final String KEY_NOTIFICATION_TIME = "notification_time";
	
	
	public static int getMode() {
		int mode = Context.MODE_WORLD_WRITEABLE;
		if (Build.VERSION.SDK_INT > 11) {
			mode = Context.MODE_MULTI_PROCESS;
		}
		return mode;
	}
}
