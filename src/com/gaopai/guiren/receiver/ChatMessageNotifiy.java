package com.gaopai.guiren.receiver;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.SNSMessage;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.service.SnsService;

public class ChatMessageNotifiy extends AbstractNotifiy {
	private static final String LOGTAG = "ChatMessageNotifiy";

	public static final int NOTIFYID_PRIVATE = 10000080;
	public static final int NOTIFYID_TRIBE = 10000081;
	public static final int NOTIFYID_MEETING = 10000082;

	private Context mContext;

	public ChatMessageNotifiy(SnsService context) {
		super(context);
		mContext = context;
	}

	@Override
	public void notifiy(SNSMessage message) {
		Log.d(LOGTAG, "notifyyyyyyyyyyyyy()...");
		MessageInfo messageInfo = null;
		if (message instanceof MessageInfo) {
			messageInfo = (MessageInfo) message;
		} else {
			return;
		}
		String fuid = messageInfo.from;

		String msg = null;

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

		// Notification
		Notification notification = new Notification();
		notification.icon = R.drawable.logo; // 设置通知的图标
		// notification.defaults |= Notification.DEFAULT_SOUND;
		if (System.currentTimeMillis() - DamiCommon.getNotificationTime(mContext) > DamiCommon.NOTIFICATION_INTERVAL) {
			DamiCommon.saveNotificationTime(mContext, System.currentTimeMillis());
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}

		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// 音频将被重复直到通知取消或通知窗口打开。
		// notification.flags |= Notification.FLAG_INSISTENT;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.when = System.currentTimeMillis();

		String notifyMsg = "";
		if (messageInfo.type == 100) {
			notifyMsg = messageInfo.displayname;
		} else if (messageInfo.type == 200) {
			notifyMsg = messageInfo.title;
		} else if (messageInfo.type == 300) {
			notifyMsg = messageInfo.title;
		}

		boolean isReceive = DamiCommon.getAcceptMsgAuth(mContext);

		/*
		 * try { Context context =
		 * mContext.createPackageContext("com.gaopai.guiren",
		 * Context.CONTEXT_IGNORE_SECURITY); SharedPreferences preferences =
		 * context.getSharedPreferences(DamiCommon.MESSAGE_NOTIFY_SHARED,
		 * Context.MODE_WORLD_READABLE); isReceive =
		 * preferences.getBoolean(DamiCommon.MESSAGE_NOTIFY, true); } catch
		 * (NameNotFoundException e1) { e1.printStackTrace(); }
		 */

		if (messageInfo.type == 100) {// 单聊

			User user = new User();
			user.uid = messageInfo.from;
			user.displayName = messageInfo.displayname;
			user.headsmall = messageInfo.headImgUrl;
			// Intent sessionIntent = new Intent(
			// PrivateMsgListActivity.UPDATE_PRIVATE_USER_SESSION_ACTION);
			// sessionIntent.putExtra("user", user);
			// sessionIntent.putExtra("message", messageInfo);
			// mContext.sendBroadcast(sessionIntent);

			try {
				ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
				ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
				if (cn.getClassName().equals(cn.getPackageName() + ".MessageListActivity")) {
					if (FeatureFunction.isAppOnForeground(mContext)) {
						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// mContext.sendBroadcast(new
			// Intent(ProfileTab.UPDATE_MESSAGE_ACTION));
			// mContext.sendBroadcast(new Intent(
			// MainActivity.ACTION_UPDATE_MESSAGE_SESSION_COUNT));

			if (!FeatureFunction.isAppOnForeground(mContext) && !isReceive) {
				return;
			}

			Intent intent = new Intent(mContext, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("data", user);
			intent.putExtra("chatnotify", true);
			intent.putExtra("type", messageInfo.type);
			PendingIntent contentIntent = PendingIntent.getActivity(mContext, messageInfo.to.hashCode(), intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(mContext, notifyMsg, msg, contentIntent);
			// getNotificationManager().notify(messageInfo.getFromId().hashCode(),
			// notification);
			getNotificationManager().notify(NOTIFYID_PRIVATE, notification);
		} else {
			if (messageInfo.type == 200) { // 部落
				try {
					ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
					ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
					if (cn.getClassName().equals(cn.getPackageName() + ".ChatMainActivity")
							&& DamiCommon.getChatType(mContext) == 200) {
						if (FeatureFunction.isAppOnForeground(mContext)) {
							return;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// mContext.sendBroadcast(new
				// Intent(TribeTab.UPDATE_COUNT_ACTION));
				// mContext.sendBroadcast(new Intent(
				// MainActivity.ACTION_UPDATE_TRIBE_SESSION_COUNT));
				// SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext)
				// .getReadableDatabase();
				// TribeTable table = new TribeTable(dbDatabase);
				// Tribe tribeSetting = table.query(messageInfo.toId);
				if (!FeatureFunction.isAppOnForeground(mContext) && !isReceive) {
					return;
				}
				// if (tribeSetting != null && tribeSetting.type == 2) {
				// return;
				// }

				if (!messageInfo.parentid.equals("0")) {
					return;
				}

				Intent intent = new Intent(mContext, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Tribe tribe = new Tribe();
				tribe.id = messageInfo.to;
				tribe.name = messageInfo.title;
				// intent.putExtra(ChatMainActivity.TRIBE_EXTRAS, tribe);
				intent.putExtra("type", messageInfo.type);
				intent.putExtra("chatnotify", true);
				PendingIntent contentIntent = PendingIntent.getActivity(mContext, messageInfo.to.hashCode(), intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				notification.setLatestEventInfo(mContext, notifyMsg, msg, contentIntent);
				// getNotificationManager().notify(
				// messageInfo.getToId().hashCode(), notification);
				getNotificationManager().notify(NOTIFYID_TRIBE, notification);
			} else if (messageInfo.type == 300) {// 会议
				try {
					ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
					ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
					if (cn.getClassName().equals(cn.getPackageName() + ".ChatMainActivity")
							&& DamiCommon.getChatType(mContext) == 300) {// UPDATE
						if (FeatureFunction.isAppOnForeground(mContext)) {
							return;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				// mContext.sendBroadcast(new Intent(
				// MeatingTab.REFRESH_UNREAD_COUNT_ACTION));
				// mContext.sendBroadcast(new Intent(
				// MainActivity.ACTION_UPDATE_MEETING_SESSION_COUNT));

				if (!FeatureFunction.isAppOnForeground(mContext) && !isReceive) {
					return;
				}
				if (!messageInfo.parentid.equals("0")) {
					return;
				}

				Intent intent = new Intent(mContext, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Tribe tribe = new Tribe();
				tribe.id = messageInfo.to;
				tribe.name = messageInfo.title;
				if (messageInfo.uid.equals(DamiCommon.getUid(mContext))) {
					tribe.role = 1;
				}
				// intent.putExtra(ChatMainActivity.TRIBE_EXTRAS, tribe);
				intent.putExtra("type", messageInfo.type);
				intent.putExtra("chatnotify", true);
				PendingIntent contentIntent = PendingIntent.getActivity(mContext, messageInfo.to.hashCode(), intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				notification.setLatestEventInfo(mContext, notifyMsg, msg, contentIntent);
				getNotificationManager().notify(NOTIFYID_MEETING, notification);
			}

		}

	}
}
