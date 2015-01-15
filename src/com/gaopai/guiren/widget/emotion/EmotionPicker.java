package com.gaopai.guiren.widget.emotion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyUtils;

public class EmotionPicker extends LinearLayout {
	private int mPickerHeight;
	private EditText mEditText;
	private Activity activity;
	private LayoutInflater mInflater;
	private ViewPager viewPager;
	private EmotionParser emotionParser;

	private ImageView centerPoint;
	private ImageView leftPoint;
	private ImageView rightPoint;

	public EmotionPicker(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		// TODO Auto-generated constructor stub
		this.mInflater = LayoutInflater.from(paramContext);
		View view = this.mInflater.inflate(R.layout.chat_emotion_layout, null);
		viewPager = (ViewPager) view.findViewById(R.id.viewpager);
		viewPager.setAdapter(new SmileyPagerAdapter());
		emotionParser = new EmotionParser(paramContext);
		leftPoint = (ImageView) view.findViewById(R.id.left_point);
		centerPoint = (ImageView) view.findViewById(R.id.center_point);
		rightPoint = (ImageView) view.findViewById(R.id.right_point);
		leftPoint.getDrawable().setLevel(1);
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				switch (position) {
				case 0:
					leftPoint.getDrawable().setLevel(1);
					centerPoint.getDrawable().setLevel(0);
					rightPoint.getDrawable().setLevel(0);
					break;
				case 1:
					leftPoint.getDrawable().setLevel(0);
					centerPoint.getDrawable().setLevel(1);
					rightPoint.getDrawable().setLevel(0);
					break;
				case 2:
					leftPoint.getDrawable().setLevel(0);
					centerPoint.getDrawable().setLevel(0);
					rightPoint.getDrawable().setLevel(1);
					break;
				}
			}
		});
		addView(view);
	}

	public void setEditText(Activity activity, ViewGroup rootLayout, EditText paramEditText) {
		this.mEditText = paramEditText;
		this.activity = activity;
	}

	public void show(Activity paramActivity) {
		this.mPickerHeight = MyUtils.dip2px(getContext(), 236);
		// this.mPickerHeight = EmotionUtility.getKeyboardHeight(paramActivity);
//		hideSoftInput(this.mEditText);
		getLayoutParams().height = this.mPickerHeight;
		setVisibility(View.VISIBLE);
	}

	public int getEmotionPickerKeyboardHeightDiff(Activity paramActivity) {
		Logger.d(this, "keyboard height = " + EmotionUtility.getKeyboardHeight(paramActivity));
		return EmotionUtility.getKeyboardHeight(paramActivity) - MyUtils.dip2px(getContext(), 236);
	}

	public void hideSoftInput(View paramEditText) {
		((InputMethodManager) DamiApp.getInstance().getSystemService("input_method")).hideSoftInputFromWindow(
				paramEditText.getWindowToken(), 0);
	}

	public void hide(Activity paramActivity) {
		setVisibility(View.GONE);
	}

	private class SmileyPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			container.removeView(view);

		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			View view = activity.getLayoutInflater().inflate(R.layout.chat_emotion_gridview, container, false);
			GridView gridView = (GridView) view.findViewById(R.id.emotion_grid);
			gridView.setAdapter(new SmileyAdapter(activity, position));
			container.addView(view, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));

			return view;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			super.setPrimaryItem(container, position, object);

		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}
	}

	private final class SmileyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		private List<String> keys;

		private Map<String, Bitmap> bitmapMap;

		private int count;

		public SmileyAdapter(Context context, int emotionPosition) {
			this.mInflater = LayoutInflater.from(context);
			this.keys = new ArrayList<String>();
			Set<String> keySet = EmotionManager.getInstance().getEmotionsPics(emotionPosition).keySet();
			keys.addAll(keySet);
			bitmapMap = EmotionManager.getInstance().getEmotionsPics(emotionPosition);
			count = bitmapMap.size();
		}

		private void bindView(final int position, View contentView) {
			ImageView imageView = ((ImageView) contentView.findViewById(R.id.smiley_item));
			TextView textView = (TextView) contentView.findViewById(R.id.smiley_text_item);
			imageView.setVisibility(View.VISIBLE);
			textView.setVisibility(View.INVISIBLE);
			imageView.setImageBitmap(bitmapMap.get(keys.get(position)));

			contentView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int index = mEditText.getSelectionStart();
					if (keys.get(position).equals("[删除]")) {
						if (index == 0) {
							return;
						}
						SpannableString spannableString = new SpannableString(mEditText.getText());
						ImageSpan[] imageSpans = spannableString.getSpans(0, spannableString.length(), ImageSpan.class);

						for (int i = 0, len = imageSpans.length; i < len; i++) {
							ImageSpan imageSpan = imageSpans[i];
							int start = spannableString.getSpanStart(imageSpan);
							int end = spannableString.getSpanEnd(imageSpan);
							if (index == end) {
								spannableString.removeSpan(imageSpan);
								mEditText.setText(mEditText.getText().delete(start, end));
								mEditText.setSelection(start);
								return;
							}
						}

						mEditText.setText(mEditText.getText().delete(index - 1, index));
						mEditText.setSelection(index - 1);
						return;
					}
					String ori = mEditText.getText().toString();
					StringBuilder stringBuilder = new StringBuilder(ori);
					stringBuilder.insert(index, keys.get(position));
					mEditText.setText(EmotionParser.replaceContent(stringBuilder.toString()));
					mEditText.setSelection(index + keys.get(position).length());
				}
			});
		}

		@Override
		public int getCount() {
			return count;
		}

		@Override
		public Object getItem(int paramInt) {
			return null;
		}

		@Override
		public long getItemId(int paramInt) {
			return 0L;
		}

		@Override
		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			if (paramView == null) {
				paramView = this.mInflater.inflate(R.layout.chat_emotion_item, null);
			}
			bindView(paramInt, paramView);
			return paramView;
		}
	}
}
