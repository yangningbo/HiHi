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

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;

// TODO: Auto-generated Javadoc
/**
 * 描述：标题栏实现.
 * 
 * @author dean
 * @version v1.0
 */
public class TitleBar extends LinearLayout {

	/** The m context. */
	private BaseActivity mActivity;

	/** 标题布局. */
	protected LinearLayout titleTextLayout = null;

	/** 显示标题文字的View. */
	protected Button titleTextBtn = null;

	protected EditText etSearch = null;

	/** 左侧的Logo图标View. */
	protected TextView logoView = null;

	/** 左侧的Logo图标View. */
	protected ImageView logoView2 = null;

	/** 左侧的Logo图标右边的分割线View. */
	protected ImageView logoLineView = null;

	/** 标题文本的对齐参数. */
	private LinearLayout.LayoutParams titleTextLayoutParams = null;

	/** 右边布局的的对齐参数. */
	private LinearLayout.LayoutParams rightViewLayoutParams = null;
	/** 左边布局的的对齐参数. */
	private LinearLayout.LayoutParams leftViewLayoutParams = null;

	/** 右边的View，可以自定义显示什么. */
	protected LinearLayout rightLayout = null;
	/** 左边的View，可以自定义显示什么. */
	protected LinearLayout leftLayout = null;

	/** 标题栏布局ID. */
	public int mAbTitleBarID = 1;

	/** 全局的LayoutInflater对象，已经完成初始化. */
	public LayoutInflater mInflater;

	/**
	 * LinearLayout.LayoutParams，已经初始化为FILL_PARENT, FILL_PARENT
	 */
	public LinearLayout.LayoutParams layoutParamsFF = null;

	/**
	 * LinearLayout.LayoutParams，已经初始化为FILL_PARENT, WRAP_CONTENT
	 */
	public LinearLayout.LayoutParams layoutParamsFW = null;

	/**
	 * LinearLayout.LayoutParams，已经初始化为WRAP_CONTENT, FILL_PARENT
	 */
	public LinearLayout.LayoutParams layoutParamsWF = null;

	/**
	 * LinearLayout.LayoutParams，已经初始化为WRAP_CONTENT, WRAP_CONTENT
	 */
	public LinearLayout.LayoutParams layoutParamsWW = null;
	
	public LinearLayout.LayoutParams layoutParamsIconDefault;

	/** 下拉选择. */
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
		// 水平排列
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setId(mAbTitleBarID);
		titleBarHeight = getResources().getDimensionPixelSize(R.dimen.title_bar);

		mInflater = LayoutInflater.from(context);
		
		layoutParamsIconDefault = new LayoutParams(titleBarHeight, titleBarHeight);

		layoutParamsFF = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParamsFW = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParamsWF = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParamsWF.gravity = Gravity.CENTER_VERTICAL;
		layoutParamsWW = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParamsWW.gravity = Gravity.CENTER_VERTICAL;

		titleTextLayoutParams = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		titleTextLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		rightViewLayoutParams = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 0);
		rightViewLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		leftViewLayoutParams = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 0);
		leftViewLayoutParams.gravity = Gravity.CENTER_VERTICAL;

		titleTextLayout = new LinearLayout(context);
		titleTextLayout.setOrientation(LinearLayout.HORIZONTAL);
		titleTextLayout.setGravity(Gravity.CENTER_VERTICAL);
		titleTextLayout.setPadding(0, 0, 0, 0);
		titleTextLayout.setMinimumHeight(titleBarHeight);

		titleTextBtn = new Button(context);
		titleTextBtn.setTextColor(context.getResources().getColor(R.color.title_color));
		titleTextBtn.setTextSize(20);
		titleTextBtn.setSingleLine();
		titleTextBtn.setEllipsize(TruncateAt.END);
		titleTextBtn.setPadding(5, 10, 5, 10);
		titleTextBtn.setGravity(Gravity.CENTER_VERTICAL);
		titleTextBtn.setBackgroundDrawable(null);

		titleTextLayout.addView(titleTextBtn, layoutParamsWF);

		logoView = new TextView(context);
		logoView.setVisibility(View.GONE);
		logoLineView = new ImageView(context);
		logoLineView.setVisibility(View.GONE);

		logoView2 = new ImageView(context);
		logoView2.setVisibility(View.GONE);

		// this.addView(logoView, layoutParamsWW);
		// this.addView(logoLineView, layoutParamsWW);
		// this.addView(logoView2, layoutParamsWW);

		// 加左边的布局
		leftLayout = new LinearLayout(context);
		leftLayout.setOrientation(LinearLayout.HORIZONTAL);
		leftLayout.setGravity(Gravity.LEFT);
		leftLayout.setPadding(0, 0, 0, 0);
		leftLayout.setHorizontalGravity(Gravity.LEFT);
		leftLayout.setGravity(Gravity.CENTER_VERTICAL);
		this.addView(leftLayout, leftViewLayoutParams);

		this.addView(titleTextLayout, titleTextLayoutParams);

		// 加右边的布局
		rightLayout = new LinearLayout(context);
		rightLayout.setOrientation(LinearLayout.HORIZONTAL);
		rightLayout.setGravity(Gravity.RIGHT);
		rightLayout.setPadding(0, 0, 0, 0);
		rightLayout.setHorizontalGravity(Gravity.RIGHT);
		rightLayout.setGravity(Gravity.CENTER_VERTICAL);
		rightLayout.setVisibility(View.GONE);
		this.addView(rightLayout, rightViewLayoutParams);

		logoView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mActivity.finish();
			}
		});
	}

	/**
	 * 描述：标题栏的背景图.
	 * 
	 * @param res
	 *            背景图资源ID
	 */
	public void setTitleBarBackground(int res) {
		this.setBackgroundResource(res);
	}

	/**
	 * 描述：设置标题背景.
	 * 
	 * @param d
	 *            背景图
	 */
	public void setTitleBarBackgroundDrawable(Drawable d) {
		this.setBackgroundDrawable(d);
	}

	/**
	 * 描述：标题栏的背景图.
	 * 
	 * @param color
	 *            背景颜色值
	 */
	public void setTitleBarBackgroundColor(int color) {
		this.setBackgroundColor(color);
	}

	/**
	 * 描述：标题文字的对齐,需要在setTitleBarGravity之后设置才生效.
	 * 
	 * @param left
	 *            the left
	 * @param top
	 *            the top
	 * @param right
	 *            the right
	 * @param bottom
	 *            the bottom
	 */
	public void setTitleTextMargin(int left, int top, int right, int bottom) {
		titleTextLayoutParams.setMargins(left, top, right, bottom);
	}

	/**
	 * 描述：标题文字字号.
	 * 
	 * @param titleTextSize
	 *            文字字号
	 */
	public void setTitleTextSize(int titleTextSize) {
		this.titleTextBtn.setTextSize(titleTextSize);
	}

	/**
	 * 描述：设置标题文字对齐方式 根据右边的具体情况判定方向： （1）中间靠近 Gravity.CENTER,Gravity.CENTER
	 * （2）左边居左 右边居右Gravity.LEFT,Gravity.RIGHT
	 * （3）左边居中，右边居右Gravity.CENTER,Gravity.RIGHT
	 * （4）左边居右，右边居右Gravity.RIGHT,Gravity.RIGHT 必须在addRightView(view)方法后设置
	 * 
	 * @param gravity1
	 *            标题对齐方式
	 * @param gravity2
	 *            右边布局对齐方式
	 */
	public void setTitleBarGravity(int gravity1, int gravity2) {
		ViewUtil.measureView(this.rightLayout);
		ViewUtil.measureView(this.logoView);
		int leftWidth = this.logoView.getMeasuredWidth() + layoutParamsWW.leftMargin;
		int rightWidth = this.rightLayout.getMeasuredWidth() + layoutParamsWF.rightMargin;
		this.titleTextLayoutParams.rightMargin = 0;
		this.titleTextLayoutParams.leftMargin = 0;
		// 全部中间靠
		if ((gravity1 == Gravity.CENTER_HORIZONTAL || gravity1 == Gravity.CENTER)) {
			if (leftWidth == 0 && rightWidth == 0) {
				// this.titleTextLayout.setGravity(Gravity.CENTER_HORIZONTAL);
				this.titleTextLayout.setGravity(Gravity.CENTER);
				this.titleTextBtn.setGravity(Gravity.CENTER);
			} else {
				if (gravity2 == Gravity.RIGHT) {
					if (rightWidth == 0) {
					} else {
						this.titleTextBtn.setPadding(rightWidth / 3 * 2, 0, 0, 0);
					}
					this.titleTextBtn.setGravity(Gravity.CENTER);
					this.rightLayout.setHorizontalGravity(Gravity.RIGHT);
				}
				if (gravity2 == Gravity.CENTER || gravity2 == Gravity.CENTER_HORIZONTAL) {
					this.titleTextLayout.setGravity(Gravity.CENTER);
					this.rightLayout.setHorizontalGravity(Gravity.LEFT);
					this.titleTextBtn.setGravity(Gravity.CENTER);
					int offset = leftWidth - rightWidth;
					if (offset > 0) {
						this.titleTextLayoutParams.rightMargin = offset;
					} else {
						this.titleTextLayoutParams.leftMargin = Math.abs(offset);
					}
				}
			}
			// 左右
		} else if (gravity1 == Gravity.LEFT && gravity2 == Gravity.RIGHT) {
			this.titleTextLayout.setGravity(Gravity.LEFT);
			this.rightLayout.setHorizontalGravity(Gravity.RIGHT);
			// 全部右靠
		} else if (gravity1 == Gravity.RIGHT && gravity2 == Gravity.RIGHT) {
			this.titleTextLayout.setGravity(Gravity.RIGHT);
			this.rightLayout.setHorizontalGravity(Gravity.RIGHT);
		} else if (gravity1 == Gravity.LEFT && gravity2 == Gravity.LEFT) {
			this.titleTextLayout.setGravity(Gravity.LEFT);
			this.rightLayout.setHorizontalGravity(Gravity.LEFT);
		}

	}

	/**
	 * 描述：获取标题文本的Button.
	 * 
	 * @return the title Button view
	 */
	public Button getTitleTextButton() {
		return titleTextBtn;
	}

	/**
	 * 描述：获取标题Logo的View.
	 * 
	 * @return the logo view
	 */
	public TextView getLogoView() {
		return logoView;
	}

	/**
	 * 描述：获取标题Logo的View.
	 * 
	 * @return the logo view
	 */
	public ImageView getLogoView2() {
		return logoView2;
	}

	/**
	 * 描述：设置标题字体粗体.
	 * 
	 * @param bold
	 *            the new title text bold
	 */
	public void setTitleTextBold(boolean bold) {
		TextPaint paint = titleTextBtn.getPaint();
		if (bold) {
			// 粗体
			paint.setFakeBoldText(true);
		} else {
			paint.setFakeBoldText(false);
		}

	}

	/**
	 * 描述：设置标题背景.
	 * 
	 * @param resId
	 *            the new title text background resource
	 */
	public void setTitleTextBackgroundResource(int resId) {
		titleTextBtn.setBackgroundResource(resId);
	}

	/**
	 * 描述：设置标题背景.
	 * 
	 * @param drawable
	 *            the new title text background drawable
	 */
	public void setTitleTextBackgroundDrawable(Drawable drawable) {
		titleTextBtn.setBackgroundDrawable(drawable);
	}

	/**
	 * 描述：设置标题文本.
	 * 
	 * @param text
	 *            文本
	 */
	public void setTitleText(String text) {
		titleTextBtn.setText(text);
		titleTextBtn.setGravity(Gravity.CENTER_HORIZONTAL);
	}

	public View setTitleTextWithImage(String text, int id) {
		titleTextBtn.setText(text);
		titleTextBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, id, 0);
		titleTextBtn.setGravity(Gravity.CENTER_HORIZONTAL);
		return titleTextBtn;
	}

	public void setTitleSize(int size) {
		titleTextBtn.setTextSize(size);
	}

	/**
	 * 描述：设置标题文本.
	 * 
	 * @param resId
	 *            文本的资源ID
	 */
	public void setTitleText(int resId) {
		titleTextBtn.setText(resId);
	}

	/**
	 * 描述：设置Logo的背景图.
	 * 
	 * @param drawable
	 *            Logo资源Drawable
	 */
	public void setLogo(Drawable drawable) {
		logoView.setVisibility(View.VISIBLE);
		logoView.setBackgroundDrawable(drawable);
		layoutParamsWW.width = MyUtils.dip2px(mContext, 30);
		layoutParamsWW.height = MyUtils.dip2px(mContext, 30);
		layoutParamsWW.leftMargin = MyUtils.dip2px(mContext, 5);
		layoutParamsWW.topMargin = MyUtils.dip2px(mContext, 5);
		layoutParamsWW.bottomMargin = MyUtils.dip2px(mContext, 5);
		leftLayout.addView(logoView, layoutParamsWW);
		logoView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mActivity.finish();
			}
		});
	}

	/**
	 * 描述：设置Logo的背景资源.
	 * 
	 * @param resId
	 *            Logo资源ID
	 */
	public View setLogo(int resId) {
		logoView = new TextView(mActivity);
		leftLayout.removeAllViews();
		logoView.setVisibility(View.VISIBLE);
		logoView.setBackgroundResource(resId);
		leftLayout.addView(logoView, layoutParamsIconDefault);
		logoView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mActivity.finish();
			}
		});
		return logoView;
	}

	public void setLogoTextView(int resId, String text) {
		logoView = new TextView(mActivity);
		leftLayout.removeAllViews();
		logoView.setVisibility(View.VISIBLE);
		// Drawable drawable =
		// getResources().getDrawable(R.drawable.icon_address);
		// / 这一步必须要做,否则不会显示.
		// drawable.setBounds(0, 0, drawable.getMinimumWidth(),
		// drawable.getMinimumHeight());
		// logoView.setCompoundDrawables(drawable, null, null, null);
		logoView.setText(text);
		logoView.setTextColor(getResources().getColor(R.color.white));
		logoView.setGravity(Gravity.CENTER_VERTICAL);
		layoutParamsWW.leftMargin = MyUtils.dip2px(mContext, 10);
		leftLayout.addView(logoView, layoutParamsWW);
		logoView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mActivity.finish();
			}
		});
	}

	/**
	 * 描述：设置Logo的背景图.
	 * 
	 * @param drawable
	 *            Logo资源Drawable
	 */
	public void setLogo2(Drawable drawable) {
		logoView2.setVisibility(View.VISIBLE);
		logoView2.setBackgroundDrawable(drawable);
		leftLayout.addView(logoView2, layoutParamsWW);
	}

	/**
	 * 描述：设置Logo的背景资源.
	 * 
	 * @param resId
	 *            Logo资源ID
	 */
	public void setLogo2(int resId) {
		logoView2.setVisibility(View.VISIBLE);
		logoView2.setBackgroundResource(resId);
		leftLayout.addView(logoView2, layoutParamsWW);
	}

	/**
	 * 描述：设置Logo分隔线的背景资源.
	 * 
	 * @param resId
	 *            Logo资源ID
	 */
	public void setLogoLine(int resId) {
		logoLineView.setVisibility(View.VISIBLE);
		logoLineView.setBackgroundResource(resId);
		leftLayout.addView(logoLineView, layoutParamsWW);
	}

	/**
	 * 描述：设置Logo分隔线的背景图.
	 * 
	 * @param drawable
	 *            Logo资源Drawable
	 */
	public void setLogoLine(Drawable drawable) {
		logoLineView.setVisibility(View.VISIBLE);
		logoLineView.setBackgroundDrawable(drawable);
		leftLayout.addView(logoLineView, layoutParamsWW);
	}
	
	public View addLeftImageView(int resId) {
		ImageView btn = new ImageView(mContext);
		btn.setImageResource(resId);
		layoutParamsWW.width = MyUtils.dip2px(mContext, 23);
		layoutParamsWW.height = MyUtils.dip2px(mContext, 23);
		layoutParamsWW.leftMargin = MyUtils.dip2px(mContext, 5);
		layoutParamsWW.topMargin = MyUtils.dip2px(mContext, 5);
		layoutParamsWW.bottomMargin = MyUtils.dip2px(mContext, 5);
		leftLayout.addView(btn, layoutParamsWW);
		return btn;
	}

	/**
	 * 描述：把指定的View填加到标题栏右边.
	 * 
	 * @param rightView
	 *            指定的View
	 */
	public void addRightView(View rightView) {
		rightLayout.setVisibility(View.VISIBLE);
		rightLayout.addView(rightView, layoutParamsWF);
	}

	/**
	 * 描述：把指定资源ID表示的View填加到标题栏右边.
	 * 
	 * @param resId
	 *            指定的View的资源ID
	 */
	public void addRightView(int resId) {
		rightLayout.setVisibility(View.VISIBLE);
		rightLayout.addView(mInflater.inflate(resId, null), layoutParamsWF);
	}

	public View addRightImageView(int resId) {
		rightLayout.setVisibility(View.VISIBLE);
		ImageView btn = new ImageView(mContext);
		btn.setImageResource(resId);
		rightLayout.addView(btn, layoutParamsIconDefault);
		return btn;
	}

	public View addRightButtonView(int resId, String text) {
		rightLayout.setVisibility(View.VISIBLE);
		Button btn = new Button(mContext);
		btn.setMinHeight(0);
		btn.setMinimumHeight(0);
		btn.setMinWidth(0);
		btn.setMinimumWidth(0);
		btn.setTextSize(15);
		btn.setText(text);
		btn.setTextColor(getResources().getColor(R.color.black));
		btn.setBackgroundResource(resId);
		btn.setGravity(Gravity.CENTER);
		layoutParamsWF.rightMargin = MyUtils.dip2px(mContext, 10);
		rightLayout.addView(btn, layoutParamsWF);
		return btn;
	}

	public TextView addRightButtonView(String text) {
		rightLayout.setVisibility(View.VISIBLE);
		TextView btn = new TextView(mContext);
		btn.setText(text);
		btn.setGravity(Gravity.CENTER);
		btn.setTextColor(getResources().getColor(R.color.title_right_text_color));
		layoutParamsWF.rightMargin = MyUtils.dip2px(mContext, 10);
		rightLayout.addView(btn, layoutParamsWF);
		return btn;
	}

	public View addRightButtonView(int textId) {
		rightLayout.setVisibility(View.VISIBLE);
		TextView btn = new TextView(mContext);
		btn.setText(textId);
		btn.setGravity(Gravity.CENTER);
		btn.setTextColor(getResources().getColor(R.color.title_right_text_color));
		layoutParamsWF.rightMargin = MyUtils.dip2px(mContext, 10);
		rightLayout.addView(btn, layoutParamsWF);
		return btn;
	}

	public View addRightImageButtonView(int drawableId) {
		rightLayout.setVisibility(View.VISIBLE);
		ImageView btn = new ImageView(mContext);
		btn.setImageResource(drawableId);
		layoutParamsWF.rightMargin = MyUtils.dip2px(mContext, 10);
		rightLayout.addView(btn, layoutParamsWF);
		return btn;
	}

	public View addMiddleView(int resId) {
		titleTextBtn.setVisibility(View.GONE);
		ImageView btn = new ImageView(mContext);
		btn.setImageResource(resId);
		titleTextLayout.addView(btn, layoutParamsWF);
		return btn;
	}

	/**
	 * 描述：清除标题栏右边的View.
	 */
	public void clearRightView() {
		rightLayout.removeAllViews();
	}

	/**
	 * 描述：设置Logo按钮的点击事件.
	 * 
	 * @param mOnClickListener
	 *            指定的返回事件
	 */
	public void setLogoOnClickListener(View.OnClickListener mOnClickListener) {
		logoView.setOnClickListener(mOnClickListener);
	}

	/**
	 * 描述：设置Logo按钮的点击事件.
	 * 
	 * @param mOnClickListener
	 *            指定的返回事件
	 */
	public void setLogo2OnClickListener(View.OnClickListener mOnClickListener) {
		logoView2.setOnClickListener(mOnClickListener);
	}

	/**
	 * 描述：设置标题的点击事件.
	 * 
	 * @param mOnClickListener
	 *            指定的返回事件
	 */
	public void setTitleTextOnClickListener(View.OnClickListener mOnClickListener) {
		titleTextBtn.setOnClickListener(mOnClickListener);
	}

	public AutoCompleteTextView addSearchEditText() {
		titleTextBtn.setVisibility(View.GONE);
		AutoCompleteTextView view = (AutoCompleteTextView) mInflater.inflate(R.layout.title_search_edittext, null);
		titleTextLayout.addView(view);
		return view;
	}

	/**
	 * 描述：下拉菜单的的实现方法
	 * 
	 * @param parent
	 * @param view
	 *            要显示的View
	 */
	public void showWindow(View parent, View view) {
		ViewUtil.measureView(view);
		int popWidth = parent.getMeasuredWidth();
		int popMargin = (this.getMeasuredHeight() - parent.getMeasuredHeight()) / 2;
		if (view.getMeasuredWidth() > parent.getMeasuredWidth()) {
			popWidth = view.getMeasuredWidth();
		}
		// if (popupWindow == null ) {
		popupWindow = new PopupWindow(view, popWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
		// }
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景

		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// popupWindow.setBackgroundDrawable(new
		// ColorDrawable(android.R.color.transparent));
		popupWindow.showAsDropDown(parent, 0, popMargin + 2);
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

	/**
	 * 
	 * 描述：设置标题下拉的View
	 * 
	 * @param view
	 * @throws
	 */
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

}
