package com.gaopai.guiren.utils;

import android.util.Log;

public class Logger {
	public static boolean debug = false;
	public static long startTime;

	public static void d(Object obj, String info) {
		if (debug) {
			Log.d(obj.getClass().getName(), info);
		}
	}

	public static void d(Class clazz, String info) {
		if (debug) {
			Log.d(clazz.getName(), info);
		}
	}

	public static void d(String tag, String info) {
		if (debug) {
			Log.d(tag, info);
		}
	}

	public static void e(String tag, String info) {
		if (debug) {
			Log.e(tag, info);
		}
	}

	public static void time(Object obj, String info) {
		if (debug) {
			Log.d(obj.getClass().getName(), info + ":" + (System.currentTimeMillis() - startTime));
		}
	}

	public static void startCountTime() {
		startTime = System.currentTimeMillis();
	}
}
