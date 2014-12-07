package com.gaopai.guiren.utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.dynamic.ConnectionBean.User;
import com.gaopai.guiren.bean.dynamic.DynamicBean.GuestBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.SpreadBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.ZanBean;
import com.gaopai.guiren.widget.emotion.EmotionManager;
import com.umeng.socialize.net.v;

public class MyTextUtils {
	private static final Pattern USER_PATTERN = Pattern.compile("[^、]*");
	private static final String USER_SPLIT_DOT = "、";
	private static final String USER_SCHEME = "guiren.user://";
	private static final String TRIBE_SCHEME = "guiren.tribe://";
	private static final String MEETING_SCHEME = "guiren.meeting://";
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

	public static Spannable addEmotions(String value) {
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

	public static Spannable getSpannableString(CharSequence... strings) {
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
}
