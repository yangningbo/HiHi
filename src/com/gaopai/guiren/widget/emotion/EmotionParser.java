package com.gaopai.guiren.widget.emotion;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gaopai.guiren.DamiApp;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

public class EmotionParser {
	private Context mContext;
	private Pattern mPattern;

	public EmotionParser(Context context) {
		mContext = context;
		mPattern = Pattern.compile("\\[(\\S+?)\\]");
	}

	// 构建正则表达式
	private Pattern buildPattern(String[] mSmileyTexts) {
		StringBuilder patternString = new StringBuilder(mSmileyTexts.length * 3);
		patternString.append('(');
		for (String s : mSmileyTexts) {
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		patternString.replace(patternString.length() - 1,
				patternString.length(), ")");

		return Pattern.compile(patternString.toString());
	}

	public static CharSequence replaceContent(String text) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		Matcher matcher = Pattern.compile("\\[(\\S+?)\\]").matcher(text);
		while (matcher.find()) {
			int k = matcher.start();
			int m = matcher.end();
			if (m - k < 8) {
				Map<String, Bitmap> map = EmotionManager.getInstance().getAllEmotionsPics();
				Bitmap bitmap = map.get(matcher.group());
				if (bitmap != null) {
					bitmap = Bitmap.createScaledBitmap(bitmap,
							EmotionManager.dip2px(20),
							EmotionManager.dip2px(20), true);
					builder.setSpan(new ImageSpan(DamiApp.getInstance(),
							bitmap, DynamicDrawableSpan.ALIGN_BOTTOM), k, m,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return builder;
	}
}
