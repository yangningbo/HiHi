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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.R;

public class EmotionPicker extends LinearLayout {
	private int mPickerHeight;
	private EditText mEditText;
	private Activity activity;
	private LayoutInflater mInflater;
	private ViewPager viewPager;
	private EmotionParser emotionParser;


	public EmotionPicker(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
		// TODO Auto-generated constructor stub
        this.mInflater = LayoutInflater.from(paramContext);
        View view = this.mInflater.inflate(R.layout.chat_emotion_layout, null);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new SmileyPagerAdapter());
        emotionParser = new EmotionParser(paramContext);
//
//        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                switch (position) {
//                    case 0:
//                        leftPoint.getDrawable().setLevel(1);
//                        
//                        rightPoint.getDrawable().setLevel(0);
//                        break;
//                    case 1:
//                        leftPoint.getDrawable().setLevel(0);
//                        
//                        rightPoint.getDrawable().setLevel(1);
//                        break;
//                   
//
//                }
//            }
//        });
        addView(view);
	}
	
    public void setEditText(Activity activity, ViewGroup rootLayout, EditText paramEditText) {
        this.mEditText = paramEditText;
        this.activity = activity;
    }

    public void show(Activity paramActivity) {
//        this.mPickerHeight = FeatureFunction.dip2px(getContext(), 200);
        this.mPickerHeight = EmotionUtility.getKeyboardHeight(paramActivity);
        hideSoftInput(this.mEditText);
        getLayoutParams().height = this.mPickerHeight;
        setVisibility(View.VISIBLE);
    }
    
   public void hideSoftInput(View paramEditText) {
        ((InputMethodManager) DamiApp.getInstance().getSystemService("input_method"))
                .hideSoftInputFromWindow(paramEditText.getWindowToken(), 0);
    }

    public void hide(Activity paramActivity) {
        setVisibility(View.GONE);
        paramActivity.getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }
	 private class SmileyPagerAdapter extends PagerAdapter {

	        @Override
	        public void destroyItem(ViewGroup container, int position, Object object) {
	            View view = (View) object;
	            container.removeView(view);

	        }

	        @Override
	        public Object instantiateItem(ViewGroup container, int position) {

	            View view = activity.getLayoutInflater()
	                    .inflate(R.layout.chat_emotion_gridview, container, false);

	            GridView gridView = (GridView) view.findViewById(R.id.emotion_grid);

	            gridView.setAdapter(new SmileyAdapter(activity, position));
	            container.addView(view, 0,
	                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
	                            ViewGroup.LayoutParams.MATCH_PARENT));

	            return view;
	        }

	        @Override
	        public void setPrimaryItem(ViewGroup container, int position, Object object) {
	            super.setPrimaryItem(container, position, object);


	        }

	        @Override
	        public int getCount() {
	            return 1;
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

	        private int emotionPosition;

	        private int count;

	        public SmileyAdapter(Context context, int emotionPosition) {
	            this.emotionPosition = emotionPosition;
	            this.mInflater = LayoutInflater.from(context);
	            this.keys = new ArrayList<String>();
	            Set<String> keySet;
	            switch (emotionPosition) {
	                case EmotionMap.GENERAL_EMOTION_POSITION:
	                    keySet = EmotionManager.getInstance().getEmotionsPics().keySet();
	                    keys.addAll(keySet);
	                    bitmapMap = EmotionManager.getInstance().getEmotionsPics();
	                    count = bitmapMap.size();
	                    break;
	                case EmotionMap.HUAHUA_EMOTION_POSITION:
	                    keySet = EmotionManager.getInstance().getHuahuaPics().keySet();
	                    keys.addAll(keySet);
	                    bitmapMap = EmotionManager.getInstance().getHuahuaPics();
	                    count = bitmapMap.size();
	                    break;
	                default:
	                    throw new IllegalArgumentException("emotion position is invalid");
	            }
	        }

	        private void bindView(final int position, View contentView) {
	            ImageView imageView = ((ImageView) contentView.findViewById(R.id.smiley_item));
	            TextView textView = (TextView) contentView.findViewById(R.id.smiley_text_item);
	            if (emotionPosition != EmotionMap.EMOJI_EMOTION_POSITION) {
	                imageView.setVisibility(View.VISIBLE);
	                textView.setVisibility(View.INVISIBLE);
	                imageView.setImageBitmap(bitmapMap.get(keys.get(position)));

	            } else {
	                imageView.setVisibility(View.INVISIBLE);
	                textView.setVisibility(View.VISIBLE);
	                textView.setText(keys.get(position));
	            }

	            contentView.setOnClickListener(new OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                    String ori = mEditText.getText().toString();
	                    int index = mEditText.getSelectionStart();
	                    StringBuilder stringBuilder = new StringBuilder(ori);
	                    stringBuilder.insert(index, keys.get(position));
	                    mEditText.setText(EmotionParser.replaceContent(stringBuilder.toString()));
	                   // mEditText.setText(emotionParser.replaceEdit(mEditText, text))
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
	                paramView = this.mInflater
	                        .inflate(R.layout.chat_emotion_item, null);
	            }
	            bindView(paramInt, paramView);
	            return paramView;
	        }
	    }
}
