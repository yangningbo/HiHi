package com.gaopai.guiren.support;

import java.util.Calendar;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.NotifySystemActivity;
import com.gaopai.guiren.activity.chat.ChatMessageActivity;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.NotifiyVo;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.fragment.NotificationFragment;
import com.gaopai.guiren.receiver.ChatMessageNotifiy;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.PreferenceOperateUtils;
import com.gaopai.guiren.utils.SPConst;

public class NotifyHelper {
	// private

	public static final long NOTIFICATION_INTERVAL = 5000; // 通知震动时间间隔

	public static final int NOTIFYID_PRIVATE = 10000080;
	public static final int NOTIFYID_TRIBE = 10000081;
	public static final int NOTIFYID_MEETING = 10000082;
	public static final int NOTIFYD_SYSTEM = 1000000083;

	private Context mContext;
	private NotificationManager notificationManager;
	private SharedPreferences po;
	private SharedPreferences poChat;

	public NotifyHelper(Context context) {
		mContext = context;
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

	}

	public boolean isNeedNotify() {
		if (po.getInt(SPConst.KEY_AVOID_DISTURB_TIME_SEGMENT, 0) == 0) {
			return true;
		}
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int fromHour = po.getInt(SPConst.KEY_AVOID_DISTURB_HOUR_FROM, 0);
		int fromMinute = po.getInt(SPConst.KEY_AVOID_DISTURB_MINUTE_FROM, 0);
		int toHour = po.getInt(SPConst.KEY_AVOID_DISTURB_HOUR_TO, 0);
		int toMinute = po.getInt(SPConst.KEY_AVOID_DISTURB_MINUTE_TO, 0);
		if (fromHour == 0 && fromMinute == 0 && toHour == 0 && toMinute == 0) {
			return true;
		}
		if (hour * 60 + minute > fromHour * 60 + fromMinute && hour * 60 + minute < toHour * 60 + toMinute) {
			return false;
		}
		return true;
	}


	public boolean isPlayRingtone() {
		return po.getInt(SPConst.KEY_NOTIFY_PLAY_RINGTONES, 0) == 1;
	}

	public boolean isVibrate() {
		return po.getInt(SPConst.KEY_NOTIFY_VIBRATE, 0) == 1;
	}

	public boolean isDamiNotify() {
		return po.getInt(SPConst.KEY_NOTIFY_DAMI, 0) == 1;
	}

	public static void saveNotificationTime(Context context, long time) {
		PreferenceOperateUtils po = new PreferenceOperateUtils(context);
		po.setLong(SPConst.KEY_NOTIFICATION_TIME, time);
	}

	public static long getNotificationTime(Context context) {
		PreferenceOperateUtils po = new PreferenceOperateUtils(context);
		return po.getLong(SPConst.KEY_NOTIFICATION_TIME, 0L);
	}

	private void init() {
		po = mContext.getSharedPreferences(SPConst.SP_SETTING, SPConst.getMode());
		poChat = mContext.getSharedPreferences(SPConst.SP_AVOID_DISTURB, SPConst.getMode());
	}

	public void notifyChatMessage(MessageInfo messageInfo) {
		init();
		Logger.d(this, "isNeedNotify=" + isNeedNotify());
		if (!isNeedNotify()) {
			return;
		}
		// if (!FeatureFunction.isAppOnForeground(mContext) && !isReceive) {
		// return;
		// }
		NotificationCompat.Builder builder = getNotificationBuilder();
		String msg = "";
		switch (messageInfo.fileType) {
		case MessageType.PICTURE:
			msg = mContext.getString(R.string.get_picture);
			break;
		case MessageType.TEXT:
			msg = messageInfo.content;
			break;
		case MessageType.VOICE:
			msg = mContext.getString(R.string.get_voice);
			break;
		default:
			break;
		}

		String notifyMsg = "";
		if (messageInfo.type == 100) {
			notifyMsg = messageInfo.displayname;
		} else if (messageInfo.type == 200) {
			notifyMsg = messageInfo.title;
		} else if (messageInfo.type == 300) {
			notifyMsg = messageInfo.title;
		}

		builder.setContentTitle(notifyMsg);
		builder.setContentText(msg);
		builder.setContentIntent(getChatIntent(messageInfo));
		Logger.d(this, getCurrentChatId(mContext) + "  ==   " + messageInfo.conversion.toid);
		if (messageInfo.type == 100) {// 单聊
			if (isActivityTop(mContext, ".activity.chat.ChatMessageActivity")) {
				if (getCurrentChatId(mContext).equals(messageInfo.conversion.toid)) {
					ConversationHelper.saveToLastMsgListReaded(messageInfo, mContext);
				} else {
					ConversationHelper.saveToLastMsgList(messageInfo, mContext);
				}
				return;
			}
			ConversationHelper.saveToLastMsgList(messageInfo, mContext);
			if (poChat.getInt(SPConst.getTribeUserId(mContext, messageInfo.from), 0) == 1) {
				return;
			}
			notificationManager.notify(NOTIFYID_PRIVATE, builder.build());
		} else { // 部落

			if (isActivityTop(mContext, ".activity.chat.ChatTribeActivity")) {
				if (getCurrentChatId(mContext).equals(messageInfo.conversion.toid)) {
					ConversationHelper.saveToLastMsgListReaded(messageInfo, mContext);
				} else {
					ConversationHelper.saveToLastMsgList(messageInfo, mContext);
				}
				return;
			}

			if (!messageInfo.parentid.equals("0")) {
				return;
			}
			ConversationHelper.saveToLastMsgList(messageInfo, mContext);
			if (poChat.getInt(SPConst.getTribeUserId(mContext, messageInfo.to), 0) == 1) {
				return;
			}

			if (messageInfo.type == 200) {
				notificationManager.notify(NOTIFYID_TRIBE, builder.build());
			} else {
				notificationManager.notify(NOTIFYID_MEETING, builder.build());
			}
		}

	}

	public void notifySystemMessage(String msg, NotifiyVo notifiyVo) {
		init();
		if (!isNeedNotify()) {
			return;
		}
		if (isActivityTop(mContext, ".activity.NotifySystemActivity")) {
			ConversationHelper.saveToLastMsgList(notifiyVo, mContext, true);
			return;
		}
		ConversationHelper.saveToLastMsgList(notifiyVo, mContext, false);
		mContext.sendBroadcast(new Intent(NotificationFragment.ACTION_MSG_NOTIFY));

		NotificationCompat.Builder builder = getNotificationBuilder();
		builder.setContentTitle(mContext.getString(R.string.has_new_notification));
		builder.setContentText(msg);
		Intent intent = new Intent(mContext, NotifySystemActivity.class);
		intent.putExtra("notify", true);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, NOTIFYD_SYSTEM, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);
		notificationManager.notify(NOTIFYD_SYSTEM, builder.build());
	}

	private PendingIntent getChatIntent(MessageInfo messageInfo) {

		Intent intent;
		if (messageInfo.type == 100) {
			intent = new Intent(mContext, ChatMessageActivity.class);
			User user = new User();
			user.uid = messageInfo.from;
			user.realname = messageInfo.displayname;
			user.headsmall = messageInfo.headImgUrl;
			intent.putExtra(ChatMessageActivity.KEY_USER, user);
		} else {
			intent = new Intent(mContext, ChatTribeActivity.class);
			Tribe tribe = new Tribe();
			tribe.id = messageInfo.to;
			tribe.name = messageInfo.title;
			tribe.type = messageInfo.type;
			intent.putExtra(ChatTribeActivity.KEY_TRIBE, tribe);
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(ChatTribeActivity.KEY_CHAT_TYPE, messageInfo.type);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, messageInfo.to.hashCode(), intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return contentIntent;
	}

	private NotificationCompat.Builder getNotificationBuilder() {

		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
		builder.setSmallIcon(R.drawable.logo);
		builder.setWhen(System.currentTimeMillis());// 设置时间发生时间
		builder.setContentTitle(mContext.getString(R.string.has_new_notification));

		int notifyDefault = 0;
		notifyDefault |= Notification.DEFAULT_LIGHTS;
		if (System.currentTimeMillis() - NotifyHelper.getNotificationTime(mContext) > NotifyHelper.NOTIFICATION_INTERVAL) {
			Logger.d(this, "isVibrate=" + isVibrate());
			if (isVibrate()) {
				NotifyHelper.saveNotificationTime(mContext, System.currentTimeMillis());
				notifyDefault |= Notification.DEFAULT_VIBRATE;
			}
			Logger.d(this, "isPlayRingtone=" + isPlayRingtone());
			if (isPlayRingtone()) {
				notifyDefault |= Notification.DEFAULT_SOUND;
			}
		}

		builder.setAutoCancel(true);
		builder.setDefaults(notifyDefault);
		return builder;
	}

	// name = "MainActivity" e.t.
	public static boolean isActivityTop(Context context, String name) {
		try {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			Logger.d(NotifyHelper.class, cn.getClassName());
			if (cn.getClassName().equals(cn.getPackageName() + name)) {
				if (FeatureFunction.isAppOnForeground(context)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getCurrentChatId(Context context) {
		PreferenceOperateUtils po = new PreferenceOperateUtils(context);
		return po.getString(SPConst.KEY_CHAT_CURRENT_ID, "");
	}

	public static void setCurrentChatId(Context context, String id) {
		PreferenceOperateUtils po = new PreferenceOperateUtils(context);
		po.setString(SPConst.KEY_CHAT_CURRENT_ID, id);
	}

	public static void clearMsgNotification(Context mContext, int type) {
		switch (type) {
		case 100:
			clearNotification(mContext, NOTIFYID_PRIVATE);
			break;
		case 200:
			clearNotification(mContext, NOTIFYID_TRIBE);
			break;
		case 300:
			clearNotification(mContext, NOTIFYID_MEETING);
			break;
		default:
			break;
		}
	}

	public static void clearSysNotification(Context mContext) {
		clearNotification(mContext, NOTIFYD_SYSTEM);
	}

	public static void clearNotification(Context mContext, int id) {
		NotificationManager notificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(id);
	}
}
