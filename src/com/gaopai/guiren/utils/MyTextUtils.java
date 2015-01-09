package com.gaopai.guiren.utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.net.rtp.RtpStream;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.widget.emotion.EmotionManager;

public class MyTextUtils {
	private static final Pattern USER_PATTERN = Pattern.compile("[^、]*");
	private static final String USER_SPLIT_DOT = "、";
	public static final String USER_SCHEME = "guiren.user://";
	public static final String TRIBE_SCHEME = "guiren.tribe://";
	public static final String MEETING_SCHEME = "guiren.meeting://";
	private static final Pattern URL_PATTERN = Pattern
			.compile("http://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]");;
	private static final String URL_SCHEME = "guiren.web://";
	private static final Pattern EMOTION_PATTERN = Pattern.compile("\\[(\\S+?)\\]");

	public static SpannableString addHttpLinks(String text) {
		SpannableString result = SpannableString.valueOf(text);
		Linkify.addLinks(result, URL_PATTERN, URL_SCHEME);
		URLSpan[] urlSpans = result.getSpans(0, result.length(), URLSpan.class);
		WeiboTextUrlSpan weiboTextUrlSpan = null;
		for (int i = 0; i < urlSpans.length; i++) {
			URLSpan urlSpan = urlSpans[i];
			int start = result.getSpanStart(urlSpan);
			int end = result.getSpanEnd(urlSpan);
			weiboTextUrlSpan = new WeiboTextUrlSpan(URL_SCHEME + result.subSequence(start, end),
					WeiboTextUrlSpan.TYPE_LINK);
			result.removeSpan(urlSpan);
			result.setSpan(weiboTextUrlSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		addEmotions(result);
		return result;
	}

	public static Spannable addEmotions(CharSequence value) {
		return addEmotions(new SpannableString(value));
	}

	public static Spannable addEmotions(Spannable value) {
		Matcher matcher = EMOTION_PATTERN.matcher(value);
		while (matcher.find()) {
			int k = matcher.start();
			int m = matcher.end();
			if (m - k < 8) {
				Map<String, Bitmap> map = EmotionManager.getInstance().getEmotionsPics();
				Bitmap bitmap = map.get(matcher.group());
				if (bitmap != null) {
					bitmap = Bitmap.createScaledBitmap(bitmap, EmotionManager.dip2px(20), EmotionManager.dip2px(20),
							true);
					value.setSpan(new ImageSpan(DamiApp.getInstance(), bitmap, DynamicDrawableSpan.ALIGN_BOTTOM), k, m,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return value;
	}

	public static View.OnTouchListener mTextOnTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			try {
				TextView textView = (TextView) v;

				int x = (int) event.getX();
				int y = (int) event.getY();
				x -= textView.getTotalPaddingLeft();
				y -= textView.getTotalPaddingTop();

				x += textView.getScrollX();
				y += textView.getScrollY();

				Layout layout = textView.getLayout();
				int offset = 0;
				if (layout != null) {
					int line = layout.getLineForVertical(y);
					offset = layout.getOffsetForHorizontal(line, x);
				}
				SpannableString text = SpannableString.valueOf(textView.getText());
				LongClickableLinkMovementMethod.getInstance().onTouchEvent(textView, text, event);
				switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					WeiboTextUrlSpan[] spans = text.getSpans(0, text.length(), WeiboTextUrlSpan.class);
					boolean found = false;
					int foundStart = 0;
					int foundEnd = 0;
					for (WeiboTextUrlSpan span : spans) {
						int start = text.getSpanStart(span);
						int end = text.getSpanEnd(span);
						if (start <= offset && offset <= end) {
							found = true;
							foundStart = start;
							foundEnd = end;
							break;
						}
					}
					boolean consumeEvent = false;
					if (found && true) {
						consumeEvent = true;
					}
					if (found && !consumeEvent) {
						clearBackgroundColorSpans(text, textView);
					}
					if (consumeEvent) {
						BackgroundColorSpan span = new BackgroundColorSpan(DamiApp.getInstance().getResources()
								.getColor(R.color.holo_blue_light));
						text.setSpan(span, foundStart, foundEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
						textView.setText(text);
					}
					return consumeEvent;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					LongClickableLinkMovementMethod.getInstance().removeLongClickCallback();
					clearBackgroundColorSpans(text, textView);
					break;
				}
				return false;
			} catch (NullPointerException e) {
				// TODO: handle exception
			}
			return false;

		}

		private void clearBackgroundColorSpans(SpannableString text, TextView textView) {
			BackgroundColorSpan[] spans = text.getSpans(0, text.length(), BackgroundColorSpan.class);
			for (BackgroundColorSpan span : spans) {
				text.removeSpan(span);
				textView.setText(text);
			}
		}
	};

	public static void changeToBold(TextView textView) {
		TextPaint tp = textView.getPaint();
		tp.setFakeBoldText(true);
	}

	public static SpannableString addSingleMeetingSpan(String text, String uid) {
		return addSingleSpanGeneral(text, uid, MEETING_SCHEME);
	}

	public static SpannableString addSingleTribeSpan(String text, String uid) {
		return addSingleSpanGeneral(text, uid, TRIBE_SCHEME);
	}

	public static SpannableString addSingleUserSpan(String text, String uid) {
		return addSingleSpanGeneral(text, uid, USER_SCHEME);
	}

	public static SpannableString setTextSize(SpannableString str, int size) {
		str.setSpan(new AbsoluteSizeSpan(size, true), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return str;
	}

	public static SpannableString setTextColor(SpannableString str, int color) {
		str.setSpan(new ForegroundColorSpan(color), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return str;
	}

	public static SpannableString setTextColor(String str, int color) {
		return setTextColor(new SpannableString(str), color);
	}

	public static SpannableString addSingleSpanGeneral(String text, String uid, String scheme) {
		if (TextUtils.isEmpty(text)) {
			return new SpannableString("");
		}
		SpannableString result = new SpannableString(text);
		result.setSpan(new WeiboTextUrlSpan(scheme + uid, WeiboTextUrlSpan.TYPE_CONNECTION), 0, text.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return result;
	}

	public static <T extends SpanUser> SpannableString addUserSpans(List<T> spanUsers) {
		if (spanUsers.size() == 0) {
			return new SpannableString("");
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (SpanUser user : spanUsers) {
			stringBuilder.append(user.realname);
			stringBuilder.append(USER_SPLIT_DOT);
		}
		String text = stringBuilder.substring(0, stringBuilder.length() - 1).toString();
		SpannableString result = SpannableString.valueOf(text);
		Linkify.addLinks(result, USER_PATTERN, USER_SCHEME);
		URLSpan[] urlSpans = result.getSpans(0, result.length(), URLSpan.class);
		WeiboTextUrlSpan weiboTextUrlSpan = null;
		for (int i = 0; i < urlSpans.length; i++) {
			URLSpan urlSpan = urlSpans[i];
			int start = result.getSpanStart(urlSpan);
			int end = result.getSpanEnd(urlSpan);
			weiboTextUrlSpan = new WeiboTextUrlSpan(USER_SCHEME + spanUsers.get(i / 2).uid,
					WeiboTextUrlSpan.TYPE_CONNECTION);// USER_SCHEME+
			result.removeSpan(urlSpan);
			result.setSpan(weiboTextUrlSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return result;
	}

	public static CharSequence getSpannableString(CharSequence... strings) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		for (CharSequence spannableString : strings) {
			builder.append(spannableString);
		}
		return builder;
	}

	public static class SpanUser implements Serializable {
		public String uid;
		public String realname;
	}

	public static InputFilter[] tagTextFilters = { new MyTextUtils.NameLengthFilter(NameLengthFilter.TAG_LENGTH) };
	public static InputFilter[] tagEditFilters = { new MyTextUtils.NameLengthFilter(NameLengthFilter.TAG_LENGTH, true) };

	public static class NameLengthFilter implements InputFilter {
		public static final int TAG_LENGTH = 10;
		int MAX_EN;
		String regEx = "[\\u4e00-\\u9fa5]";
		boolean isEdit = false;

		public NameLengthFilter(int mAX_EN) {
			super();
			MAX_EN = mAX_EN;
		}

		public NameLengthFilter(int mAX_EN, boolean isEdit) {
			super();
			MAX_EN = mAX_EN;
			this.isEdit = isEdit;
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			if (isEdit) {
				int destCount = dest.toString().length() + getChineseCount(dest.toString());
//				int sourceCount = source.toString().length() + getChineseCount(source.toString());
				return source.toString().subSequence(0, getLimitStr(source.toString(), MAX_EN - destCount));
			} else {
				return source.toString().subSequence(0, getLimitStr(source.toString(), MAX_EN));
			}
		}

		private int getChineseCount(String str) {
			int count = 0;
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(str);
			while (m.find()) {
				for (int i = 0; i <= m.groupCount(); i++) {
					count = count + 1;
				}
			}
			return count;
		}
	}
	
	public static boolean isChinese(char c) {
		return '\u4e00' <= c && c <= '\u9fa5';
	}
	
	public static int getLimitStr(String str, int desiredLen) {
		int length = str.length();
		int resultLen = 0;
		int tempLen = 0;
		for (int i = 0; i < length; i++) {
			char c = str.charAt(i);
			if (isChinese(c)) {
				if (tempLen + 2 > desiredLen) {
					return resultLen;
				}
				tempLen += 2;
				resultLen += 1;
			} else {
				if (tempLen + 1 > desiredLen) {
					return resultLen;
				}
				tempLen += 1;
				resultLen += 1;
			}
		}
		return resultLen;
	}

	public static String getSubString(String paramString, int len) {
		return paramString.substring(0, getLimitStr(paramString, 2*len));
	}

	public static int length(String paramString) {
		int i = 0;
		for (int j = 0; j < paramString.length(); j++) {
			if (!paramString.substring(j, j + 1).matches("[Α-￥]")) {
				i += 2;
			} else {
				i++;
			}
		}
		if (i % 2 > 0) {
			i = 1 + i / 2;
		} else {
			i = i / 2;
		}
		return i;
	}

	public static boolean checkIsEmail(String email) {
		return Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	public static boolean checkIsPhone(String phone) {
		return PhoneNumberUtils.isGlobalPhoneNumber(phone);
	}
}
