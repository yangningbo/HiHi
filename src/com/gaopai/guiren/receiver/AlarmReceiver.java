package com.gaopai.guiren.receiver;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.service.SnsService;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("arui.alarm.action")) {
			boolean isServiceRunning = false;
			ActivityManager manager = (ActivityManager) DamiApp.getInstance()
					.getSystemService(Context.ACTIVITY_SERVICE);
			for (RunningServiceInfo service : manager
					.getRunningServices(Integer.MAX_VALUE)) {
				if ("com.gaopai.guiren.service.SnsService"
						.equals(service.service.getClassName())) {
					isServiceRunning = true;
				}
			}
			if (!isServiceRunning) {
				Intent i = new Intent(context, SnsService.class);
				context.startService(i);
			}

		}
	}

}
