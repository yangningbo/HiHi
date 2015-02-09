package com.gaopai.guiren.support.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.utils.Logger;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context mContext, Intent intent) {
		Logger.d(this, "action=" + intent.getAction() + "  id=" + intent.getStringExtra("id"));
		String action = intent.getAction();
		String name = intent.getStringExtra("name");
		if (action.contains("meeting")) {
			int start = action.indexOf("meeting") + 8;
			String id = intent.getAction().substring(start, action.length());

			NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
			builder.setSmallIcon(R.drawable.logo_help);
			builder.setWhen(System.currentTimeMillis());// 设置时间发生时间

			int notifyDefault = 0;
			notifyDefault |= Notification.DEFAULT_LIGHTS;
			notifyDefault |= Notification.DEFAULT_VIBRATE;
			notifyDefault |= Notification.DEFAULT_SOUND;

			builder.setAutoCancel(true);
			builder.setDefaults(notifyDefault);

			builder.setContentTitle(mContext.getString(R.string.has_new_notification));
			if (TextUtils.isEmpty(name)) {
				builder.setContentText(mContext.getString(R.string.alarm_meeting_is_on_going));
			} else {
				builder.setContentText("会议【" + name + "】即将开始,请速速就座！");
			}

			Intent i = new Intent(mContext, MainActivity.class);
			i.setAction(MainActivity.ACTION_NOTIFY_MEETING_START);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra("id", id);

			PendingIntent contentIntent = PendingIntent.getActivity(mContext, 121212, i,
					PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(contentIntent);
			NotificationManager notificationManager = (NotificationManager) mContext
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(121212, builder.build());
		}
	}
}
