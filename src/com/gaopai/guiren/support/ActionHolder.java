package com.gaopai.guiren.support;

import android.content.Intent;

public class ActionHolder {
	public final static String ACTION_CANCEL_TRIBE = "com.gaopai.guiren.intent.action.ACTION_CANCEL_TRIBE";
	public final static String ACTION_QUIT_TRIBE = "com.gaopai.guiren.intent.action.ACTION_QUIT_TRIBE";
	public final static String ACTION_CANCEL_MEETING = "com.gaopai.guiren.intent.action.ACTION_CANCEL_MEETING";
	public final static String ACTION_QUIT_MEETING = "com.gaopai.guiren.intent.action.ACTION_QUIT_MEETING";
	public static Intent getExitIntent(String tid, String action) {
		Intent intent = new Intent(action);
		intent.putExtra("tid", tid);
		return intent;
	}
	

}
