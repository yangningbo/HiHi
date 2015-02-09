package com.gaopai.guiren.support.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.PreferenceOperateUtils;
import com.gaopai.guiren.utils.SPConst;

public class MeetingAlarmHelper {
	/**
	 * Set alarm for situations below 
	 * 1, meeting has passed 
	 * 2, join meeting success 
	 * 3, reset meeting start time
	 * 4, agree join meeting, agree be guest or zhuchiren
	 */
	public static void setAlarmForMeeting(Context context, Tribe mMeeting) {
		if (mMeeting == null) {
			 return;
		}
		if (mMeeting.start * 1000 < System.currentTimeMillis()) {
			if (context instanceof BaseActivity) {
				((BaseActivity) context).showToast(R.string.meeting_is_start);
			}
			return;
		}
		setAlarm(context, true, mMeeting.id);
		Intent intent = new Intent(context, AlarmReceiver.class); // 创建Intent对象
		intent.putExtra("id", "aaaaaaaaaaaaaaaaaa");
		intent.putExtra("name", mMeeting.name);
		intent.setAction(context.getPackageName() + ".meeting." + mMeeting.id);
		PendingIntent pi = PendingIntent.getBroadcast(context, 199823, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Logger.d("MeetingAlarmHelper",
				"current=" + System.currentTimeMillis() + "   diff="
						+ (System.currentTimeMillis() - mMeeting.start * 1000) / 1000);
		alarmManager.set(AlarmManager.RTC_WAKEUP, (mMeeting.start - 5 * 60) * 1000, pi); // 设置闹钟，当前时间就唤醒
		// alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
		// + 10 * 1000, pi); // 设置闹钟，当前时间就唤醒
	}

	/**
	 * Cancel alarm for situation below 
	 * 1, quit meeting 
	 * 2, has been kicked out of meeting
	 */
	public static void cancelMeetingAlarm(Context context, Tribe mMeeting) {
		if (mMeeting == null) {
			 return;
		}
		if (mMeeting.end * 1000 < System.currentTimeMillis()) {
			if (context instanceof BaseActivity) {
				((BaseActivity) context).showToast(R.string.meeting_is_over);
			}
			return;
		}
		setAlarm(context, false, mMeeting.id);
		Intent intent = new Intent(context, AlarmReceiver.class); // 创建Intent对象
		intent.setAction(context.getPackageName() + ".meeting." + mMeeting.id);
		PendingIntent pi = PendingIntent.getBroadcast(context, 199823, intent, 0); // 创建PendingIntent
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pi);
	}
	
	public static void setAlarm(Context mContext, boolean isAlarm, String mMeetingID) {
		PreferenceOperateUtils po = new PreferenceOperateUtils(mContext, SPConst.SP_ALARM);
		po.setBoolean(SPConst.getSingleSpId(mContext, mMeetingID), isAlarm);
	}
}
