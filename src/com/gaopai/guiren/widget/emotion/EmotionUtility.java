package com.gaopai.guiren.widget.emotion;

import com.gaopai.guiren.DamiApp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;

public class EmotionUtility {
	public static int getAppHeight(Activity paramActivity) {
		Rect localRect = new Rect();
		paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		return localRect.height();
	}

	public static int getScreenHeight(Activity paramActivity) {
		Display display = paramActivity.getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		return metrics.heightPixels;
	}

	public static int getStatusBarHeight(Activity paramActivity) {
		Rect localRect = new Rect();
		paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		return localRect.top;
	}

	public static int getKeyboardHeight(Activity paramActivity) {

		int height = EmotionUtility.getScreenHeight(paramActivity) - EmotionUtility.getStatusBarHeight(paramActivity)
				- EmotionUtility.getAppHeight(paramActivity);
		if (height == 0) {
			height = EmotionUtility.getDefaultSoftKeyBoardHeight();
		}
		EmotionUtility.setDefaultSoftKeyBoardHeight(height);
		return height;
	}

	public static final String KEYBOARD_HEIGHT = "keyboard_height";

	public static int getDefaultSoftKeyBoardHeight() {
		return DamiApp.getInstance().getPou().getInt(KEYBOARD_HEIGHT, 400);
	}

	public static void setDefaultSoftKeyBoardHeight(int height) {
		DamiApp.getInstance().getPou().setInt(KEYBOARD_HEIGHT, height);
	}

}
