package com.gaopai.guiren.utils;

import android.util.Log;

public class Logger {
	public static void d(Object obj, String info) {
		Log.d(obj.getClass().getName(), info);
	}
	public static void d(Class clazz, String info) {
		Log.d(clazz.getName(), info);
	}
}
