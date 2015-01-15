package com.gaopai.guiren.widget.emotion;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;

import com.gaopai.guiren.DamiApp;

public class EmotionManager {
	private static EmotionManager instance;

	public static EmotionManager getInstance() {
		if (instance == null) {
			instance = new EmotionManager();
			return instance;
		} else {
			return instance;
		}
	}

	private LinkedHashMap<Integer, LinkedHashMap<String, Bitmap>> emotionsPic = new LinkedHashMap<Integer, LinkedHashMap<String, Bitmap>>();
	private static LinkedHashMap<String, Bitmap> imageMap = new LinkedHashMap<String, Bitmap>();

	public static void initMap() {
		Map<String, String> general = EmotionMap.getInstance().getGeneral();
		imageMap = getEmotionsTask(general);
	}

	public static Bitmap getBitmap(String str) {
		return imageMap.get(str);
	}

	public Map<String, Bitmap> getEmotionsPics(int pos) {
		if (emotionsPic != null && emotionsPic.size() > 0) {
			return emotionsPic.get(pos);
		} else {
			getEmotionsTask();
			return emotionsPic.get(pos);
		}
	}
	public LinkedHashMap<String, Bitmap> getAllEmotionsPics() {
		if (allEmotions.size() > 0) {
			return allEmotions;
		} else {
			getEmotionsTask();
			return allEmotions;
		}
	}
	
	LinkedHashMap<String, Bitmap> allEmotions =  new LinkedHashMap<String, Bitmap>();
	private void getEmotionsTask() {
		LinkedHashMap<String, Bitmap> partEmotions;
		partEmotions = getEmotionsTask(EmotionMap.getInstance().getGeneral());
		emotionsPic.put(0, partEmotions);
		allEmotions.putAll(partEmotions);
		partEmotions = getEmotionsTask(EmotionMap.getInstance().getGeneral1());
		emotionsPic.put(1, partEmotions);
		allEmotions.putAll(partEmotions);
		partEmotions = getEmotionsTask(EmotionMap.getInstance().getGeneral2());
		emotionsPic.put(2, partEmotions);
		allEmotions.putAll(partEmotions);
	}

	private static LinkedHashMap<String, Bitmap> getEmotionsTask(Map<String, String> emotionMap) {
		List<String> index = new ArrayList<String>();
		index.addAll(emotionMap.keySet());
		LinkedHashMap<String, Bitmap> bitmapMap = new LinkedHashMap<String, Bitmap>();
		for (String str : index) {
			String name = emotionMap.get(str);
			AssetManager assetManager = DamiApp.getInstance().getAssets();
			InputStream inputStream;
			try {
				inputStream = assetManager.open(name);
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				if (bitmap != null) {
					Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, dip2px(30), dip2px(30), true);
					if (bitmap != scaledBitmap) {
						bitmap.recycle();
						bitmap = scaledBitmap;
					}
					bitmapMap.put(str, bitmap);
				}
			} catch (IOException ignored) {

			}
		}

		return bitmapMap;
	}

	public static int dip2px(int dipValue) {
		float reSize = DamiApp.getInstance().getResources().getDisplayMetrics().density;
		return (int) ((dipValue * reSize) + 0.5);
	}

	public static int px2dip(int pxValue) {
		float reSize = DamiApp.getInstance().getResources().getDisplayMetrics().density;
		return (int) ((pxValue / reSize) + 0.5);
	}

	public static float sp2px(int spValue) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, DamiApp.getInstance().getResources()
				.getDisplayMetrics());
	}
}
