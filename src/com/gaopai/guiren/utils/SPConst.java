package com.gaopai.guiren.utils;

import android.content.Context;

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
	public static String getAlarmId(Context context, String tribeId) {
		return DamiCommon.getUid(context)+"&"+tribeId;
	}

}
