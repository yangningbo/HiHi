/*
 * Copyright (C) 2013 www.418log.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gaopai.guiren.view;

import android.R.integer;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;

// TODO: Auto-generated Javadoc
/**
 * 描述：标题栏实现.
 * 
 * @author dean
 * @version v1.0
 */
public class TitleBar extends ViewGroup {

	private BaseActivity mActivity;

	public LinearLayout centerLayout = null;

	public Button titleTextBtn = null;

	public EditText etSearch = null;

	public TextView logoView = null;

	private LayoutParams generalLayoutParams = null;

	public LinearLayout rightLayout = null;
	public LinearLayout leftLayout = null;

	public int mAbTitleBarID = 1;

	public LayoutInflater mInflater;
	public LinearLayout.LayoutParams layoutParamsFF = null;
	public LinearLayout.LayoutParams layoutParamsFW = null;
	public LinearLayout.LayoutParams layoutParamsWF = null;
	public LinearLayout.LayoutParams layoutParamsWW = null;

	public LayoutParams layoutParamsBtnDefault;

	public final static int MATCH_PARENT = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
	public final static int WRAP_CONTENT = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

	private PopupWindow popupWindow;
	private Context mContext;
	private int titleBarHeight;

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		ininTitleBar(context);
	}

	public TitleBar(Context context) {
		super(context);
		this.mContext = context;
		ininTitleBar(context);

	}

	public void ininTitleBar(Context context) {

		mActivity = (BaseActivity) context;
		this.setId(mAbTitleBarID);
		this.setBackgroundColor(getResources().getColor(R.color.titlebar_background));
		titleBarHeight = getResources().getDimensionPixelSize(R.dimen.title_bar);
		mInflater = LayoutInflater.from(context);

		layoutParamsBtnDefault = new ViewGroup.LayoutParams(titleBarHeight, titleBarHeight);

		layoutParamsFF = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
		layoutParamsFW = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
		layoutParamsWF = new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT);
		layoutParamsWF.gravity = Gravity.CENTER_VERTICAL;
		layoutParamsWW = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
		layoutParamsWW.gravity = Gravity.CENTER_VERTICAL;

		generalLayoutParams = new ViewGroup.LayoutParams(WRAP_CONTENT, titleBarHeight);

		logoView = new TextView(context);
		logoView.setVisibility(View.GONE);
		leftLayout = getLinearLayout();
		this.addView(leftLayout, generalLayoutParams);

		centerLayout = getLinearLayout();
		titleTextBtn = new Button(context);
		titleTextBtn.setSingleLine();
		titleTextBtn.setEllipsize(TruncateAt.END);
		titleTextBtn.setPadding(0, 0, 0, 0);
		titleTextBtn.setTextSize(getResources().getDimension(R.dimen.title_text_size));
		titleTextBtn.setBackgroundDrawable(null);
		centerLayout.setGravity(Gravity.CENTER);
		centerLayout.addView(titleTextBtn, layoutParamsWW);
		this.addView(centerLayout, generalLayoutParams);

		rightLayout = getLinearLayout();
		this.addView(rightLayout, generalLayoutParams);

		logoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mActivity.finish();
			}
		});

//		 centerLayout.setBackgroundColor(getResources().getColor(R.color.red_dongtai_bg));
//		 leftLayout.setBackgroundColor(getResources().getColor(R.color.general_btn_green_active));
//		 rightLayout.setBackgroundColor(getResources().getColor(R.color.general_blue));

	}

	private LinearLayout getLinearLayout() {
		LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setPadding(0, 0, 0, 0);
		return layout;
	}

	public Button setTitleText(String text) {
		titleTextBtn.setText(text);
		titleTextBtn.setGravity(Gravity.CENTER);
		return titleTextBtn;
	}

	public View setTitleTextWithImage(String text, int id) {
		titleTextBtn.setText(text);
		titleTextBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, id, 0);
		titleTextBtn.setGravity(Gravity.CENTER);
		return titleTextBtn;
	}

	public void setTitleText(int resId) {
		titleTextBtn.setText(resId);
	}

	public View setLogo(int resId) {
		logoView = new TextView(mActivity);
		// leftLayout.removeAllViews();
		logoView.setBackgroundResource(resId);
		leftLayout.addView(logoView, layoutParamsBtnDefault);
		logoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mActivity.finish();
			}
		});
		return logoView;
	}

	public View addLeftImageViewWithDefaultSize(int resId) {
		int width = MyUtils.dip2px(mContext, 20);
		return addLeftImageView(resId, width, width);
	}

	public View addLeftImageView(int resId, int width, int height) {
		ImageView btn = new ImageView(mContext);
		btn.setImageResource(resId);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
		lp.gravity = Gravity.CENTER;
		int margin = MyUtils.dip2px(mContext, 5);
		lp.leftMargin = margin;
		lp.topMargin = margin;
		lp.bottomMargin = margin;
		leftLayout.addView(btn, lp);
		return btn;
	}

	public TextView addLeftTextView(String text) {
		TextView btn = new TextView(mContext);
		btn.setText(text);
		btn.setMaxEms(8);
		btn.setEllipsize(TruncateAt.END);
		btn.setSingleLine(true);
		btn.setGravity(Gravity.CENTER);
		btn.setTextSize(getResources().getDimension(R.dimen.title_text_size));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT);
		lp.gravity = Gravity.CENTER;
		int margin = MyUtils.dip2px(mContext, 5);
		lp.topMargin = margin;
		lp.bottomMargin = margin;
		leftLayout.addView(btn, lp);
		return btn;
	}

	public TextView addRightTextView(String text) {
		TextView btn = new TextView(mContext);
		btn.setText(text);
		btn.setGravity(Gravity.CENTER);
		btn.setTextColor(getResources().getColor(R.color.title_right_text_color));
		layoutParamsWF.rightMargin = MyUtils.dip2px(mContext, 10);
		rightLayout.addView(btn, layoutParamsWF);
		return btn;
	}

	public View addRightTextView(int textId) {
		return addRightTextView(getResources().getString(textId));
	}

	public View addRightButtonView(int bgId) {
		rightLayout.setVisibility(View.VISIBLE);
		Button btn = new Button(mContext);
		btn.setBackgroundResource(bgId);
		rightLayout.addView(btn, layoutParamsBtnDefault);
		btn.setTextColor(getResources().getColor(R.color.title_right_text_color));
		return btn;
	}

	public View addRightImageView(int drawableId) {
		rightLayout.setVisibility(View.VISIBLE);
		ImageView btn = new ImageView(mContext);
		btn.setImageResource(drawableId);
		layoutParamsWF.rightMargin = MyUtils.dip2px(mContext, 10);
		rightLayout.addView(btn, layoutParamsWF);
		return btn;
	}

	public void clearRightView() {
		rightLayout.removeAllViews();
	}

	public void setLogoOnClickListener(View.OnClickListener mOnClickListener) {
		logoView.setOnClickListener(mOnClickListener);
	}

	public void setTitleTextOnClickListener(View.OnClickListener mOnClickListener) {
		titleTextBtn.setOnClickListener(mOnClickListener);
	}

	public AutoCompleteTextView addSearchEditText() {
		titleTextBtn.setVisibility(View.GONE);
		AutoCompleteTextView view = (AutoCompleteTextView) mInflater.inflate(R.layout.title_search_edittext, null);
		centerLayout.addView(view);
		return view;
	}

	public EditText addContactSearchEditText() {
		View view = mInflater.inflate(R.layout.title_contact_search_edittext, null);
		centerLayout.addView(view, layoutParamsFW);
		return (EditText) view;
	}

	public void showWindow(View parent, View view) {
		ViewUtil.measureView(view);
		int popWidth = parent.getMeasuredWidth();
		int popMargin = (this.getMeasuredHeight() - parent.getMeasuredHeight()) / 2;
		if (view.getMeasuredWidth() > parent.getMeasuredWidth()) {
			popWidth = view.getMeasuredWidth();
		}
		popupWindow = new PopupWindow(view, popWidth, WRAP_CONTENT, true);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAsDropDown(parent, -MyUtils.dip2px(mContext, 10), popMargin + 2);
	}

	public void closeWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}

	public boolean isWindowShowing() {
		if (popupWindow != null && popupWindow.isShowing()) {
			return true;
		}
		return false;
	}

	public void setTitleTextDropDown(final View view) {
		if (view == null) {
			return;
		}
		setTitleTextOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showWindow(titleTextBtn, view);
			}
		});
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// make sure centerLayout always be in center of titlebar, doesn't have
		// to match parent
		int heightSpec = MeasureSpec.makeMeasureSpec(titleBarHeight, MeasureSpec.EXACTLY);
		int widthSpec = getChildMeasureSpec(widthMeasureSpec, 0, LayoutParams.WRAP_CONTENT);
		leftLayout.measure(widthSpec, heightSpec);
		rightLayout.measure(widthSpec, heightSpec);
		int leftWidth = leftLayout.getMeasuredWidth();
		int rightWidth = rightLayout.getMeasuredWidth();
		int centerXStart = Math.max(leftWidth, rightWidth);
		centerLayout.measure(
				getChildMeasureSpec(
						MeasureSpec.makeMeasureSpec(getMeasuredWidth() - 2 * centerXStart, MeasureSpec.EXACTLY), 0,
						LayoutParams.MATCH_PARENT), heightSpec);
		Logger.d(this, "center width=" + centerLayout.getMeasuredWidth());
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), heightSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		// super.onLayout(changed, l, t, r, b);
		View leftView = leftLayout;
		View centerView = centerLayout;
		View rightView = rightLayout;
		int leftWidth = leftView.getMeasuredWidth();
		int rightWidth = rightView.getMeasuredWidth();
		int centerXStart = Math.max(leftWidth, rightWidth);
		leftLayout.layout(0, t, leftWidth, b);
		int centerMarginLeft = ((getMeasuredWidth() - 2 * centerXStart) - centerLayout.getMeasuredWidth()) / 2;
		centerLayout.layout(centerXStart + centerMarginLeft, t, centerXStart + centerView.getMeasuredWidth()
				+ centerMarginLeft, b);
		rightLayout.layout(r - rightWidth, t, r, b);
	}
}
