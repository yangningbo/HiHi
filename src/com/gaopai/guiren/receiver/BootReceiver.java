package com.gaopai.guiren.receiver;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.service.SnsService;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {

		// Intent intent = new Intent(context, AlarmReceiver.class);
		// intent.setAction("arui.alarm.action");
		// PendingIntent sender = PendingIntent.getBroadcast(context, 0,
		// intent, 0);
		// long firstime = SystemClock.elapsedRealtime();
		// AlarmManager am = (AlarmManager) context
		// .getSystemService(Context.ALARM_SERVICE);
		// // 10秒一个周期，不停的发送广播
		// am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,
		// 10 * 1000, sender);

		boolean isServiceRunning = false;
		ActivityManager manager = (ActivityManager) DamiApp.getInstance()
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.gaopai.guiren.service.SnsService".equals(service.service
					.getClassName())) {
				isServiceRunning = true;
			}
		}
		if (!isServiceRunning) {
			Intent i = new Intent(context, SnsService.class);
			context.startService(i);
		}

	}

}
