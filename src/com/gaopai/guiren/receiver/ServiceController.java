package com.gaopai.guiren.receiver;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gaopai.guiren.service.SnsService;

public class ServiceController extends BroadcastReceiver {

	private static Intent sMonitorServiceIntent = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		
//		Log.d("Service Controller", "Check Service");
		if (isServiceRunning(context, SnsService.class) == false) {
			String info = "starting service by AlarmManager";
//			Log.d("Service Controller", info);
			sMonitorServiceIntent = new Intent(context, SnsService.class);
			context.startService(sMonitorServiceIntent);
		}
	}

	private boolean isServiceRunning(Context context, Class<?> serviceClass) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo serviceInfo : am
				.getRunningServices(Integer.MAX_VALUE)) {
			String className1 = serviceInfo.service.getClassName();
			String className2 = serviceClass.getName();
			if (className1.equals(className2)) {
				return true;
			}
		}
		return false;
	}

	public static Intent getServiceIntent() {
		return sMonitorServiceIntent;
	}
}
