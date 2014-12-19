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
package com.gaopai.guiren.utils;

import com.gaopai.guiren.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * The Class ViewUtil.
 */
public class ViewUtil {

	/**
	 * 描述：重置AbsListView的高度. item 的最外层布局要用
	 * RelativeLayout,如果计算的不准，就为RelativeLayout指定一个高度
	 * 
	 * @param absListView
	 *            the abs list view
	 * @param lineNumber
	 *            每行几个 ListView一行一个item
	 * @param verticalSpace
	 *            the vertical space
	 */
	public static void setAbsListViewHeight(AbsListView absListView, int lineNumber, int verticalSpace) {

		int totalHeight = getAbsListViewHeight(absListView, lineNumber, verticalSpace);
		ViewGroup.LayoutParams params = absListView.getLayoutParams();
		params.height = totalHeight;
		((MarginLayoutParams) params).setMargins(0, 0, 0, 0);
		absListView.setLayoutParams(params);
	}

	/**
	 * 描述：获取AbsListView的高度.
	 * 
	 * @param absListView
	 *            the abs list view
	 * @param lineNumber
	 *            每行几个 ListView一行一个item
	 * @param verticalSpace
	 *            the vertical space
	 */
	public static int getAbsListViewHeight(AbsListView absListView, int lineNumber, int verticalSpace) {
		int totalHeight = 0;
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		absListView.measure(w, h);
		ListAdapter mListAdapter = absListView.getAdapter();
		if (mListAdapter == null) {
			return totalHeight;
		}

		int count = mListAdapter.getCount();
		if (absListView instanceof ListView) {
			for (int i = 0; i < count; i++) {
				View listItem = mListAdapter.getView(i, null, absListView);
				listItem.measure(w, h);
				totalHeight += listItem.getMeasuredHeight();
			}
			if (count == 0) {
				totalHeight = verticalSpace;
			} else {
				totalHeight = totalHeight + (((ListView) absListView).getDividerHeight() * (count - 1));
			}

		} else if (absListView instanceof GridView) {
			int remain = count % lineNumber;
			if (remain > 0) {
				remain = 1;
			}
			if (mListAdapter.getCount() == 0) {
				totalHeight = verticalSpace;
			} else {
				View listItem = mListAdapter.getView(0, null, absListView);
				listItem.measure(w, h);
				int line = count / lineNumber + remain;
				totalHeight = line * listItem.getMeasuredHeight() + (line - 1) * verticalSpace;
			}

		}
		return totalHeight;

	}

	/**
	 * 测量这个view，最后通过getMeasuredWidth()获取宽度和高度.
	 * 
	 * @param v
	 *            要测量的view
	 * @return 测量过的view
	 */
	public static void measureView(View v) {
		if (v == null) {
			return;
		}
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		v.measure(w, h);
	}

	/**
	 * 描述：根据分辨率获得字体大小.
	 * 
	 * @param screenWidth
	 *            the screen width
	 * @param screenHeight
	 *            the screen height
	 * @param textSize
	 *            the text size
	 * @return the int
	 */
	public static int resizeTextSize(int screenWidth, int screenHeight, int textSize) {
		float ratio = 1;
		try {
			float ratioWidth = (float) screenWidth / 480;
			float ratioHeight = (float) screenHeight / 800;
			ratio = Math.min(ratioWidth, ratioHeight);
		} catch (Exception e) {
		}
		return Math.round(textSize * ratio);
	}

	/**
	 * 
	 * 描述：dip转换为px
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 * @throws
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		System.out.println("SCALE======================" + "scale");
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 
	 * 描述：px转换为dip
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 * @throws
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取一个LinearLayout
	 * 
	 * @param context
	 *            上下文
	 * @param orientation
	 *            流向
	 * @param width
	 *            宽
	 * @param height
	 *            高
	 * @return LinearLayout
	 */
	public static LinearLayout createLinearLayout(Context context, int orientation, int width, int height) {
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(orientation);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(width, height));
		return linearLayout;
	}

	/**
	 * 
	 * 获取一个LinearLayout
	 * 
	 * @param context
	 *            上下文
	 * @param orientation
	 *            流向
	 * @param width
	 *            宽
	 * @param height
	 *            高
	 * @param weight
	 *            权重
	 * @return LinearLayout
	 */
	public static LinearLayout createLinearLayout(Context context, int orientation, int width, int height, int weight) {
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(orientation);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(width, height, weight));
		return linearLayout;
	}

	/**
	 * 根据ListView的所有子项的高度设置其高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeightByAllChildrenViewHeight(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter != null) {
			int totalHeight = 0;
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
			listView.setLayoutParams(params);
		}
	}

	/**
	 * 给给定的视图设置长按提示
	 * 
	 * @param context
	 *            上下文
	 * @param view
	 *            给定的视图
	 * @param hintContent
	 *            提示内容
	 */
	public static void setLongClickHint(final Context context, View view, final String hintContent) {
		view.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(context, hintContent, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
	}

	/**
	 * 给给定的视图设置长按提示
	 * 
	 * @param context
	 *            上下文
	 * @param view
	 *            给定的视图
	 * @param hintContentId
	 *            提示内容的ID
	 */
	public static void setLongClickHint(final Context context, View view, final int hintContentId) {
		setLongClickHint(context, view, context.getString(hintContentId));
	}

	/**
	 * 设置给定视图的高度
	 * 
	 * @param view
	 *            给定的视图
	 * @param newHeight
	 *            新的高度
	 */
	public static void setViewHeight(View view, int newHeight) {
		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		layoutParams.height = newHeight;
		view.setLayoutParams(layoutParams);
	}

	/**
	 * 将给定视图的高度增加一点
	 * 
	 * @param view
	 *            给定的视图
	 * @param increasedAmount
	 *            增加多少
	 */
	public static void addViewHeight(View view, int increasedAmount) {
		ViewGroup.LayoutParams headerLayoutParams = view.getLayoutParams();
		headerLayoutParams.height += increasedAmount;
		view.setLayoutParams(headerLayoutParams);
	}

	/**
	 * 设置给定视图的宽度
	 * 
	 * @param view
	 *            给定的视图
	 * @param newWidth
	 *            新的宽度
	 */
	public static void setViewWidth(View view, int newWidth) {
		ViewGroup.LayoutParams headerLayoutParams = view.getLayoutParams();
		headerLayoutParams.width = newWidth;
		view.setLayoutParams(headerLayoutParams);
	}

	/**
	 * 将给定视图的宽度增加一点
	 * 
	 * @param view
	 *            给定的视图
	 * @param increasedAmount
	 *            增加多少
	 */
	public static void addViewWidth(View view, int increasedAmount) {
		ViewGroup.LayoutParams headerLayoutParams = view.getLayoutParams();
		headerLayoutParams.width += increasedAmount;
		view.setLayoutParams(headerLayoutParams);
	}

	/**
	 * 获取流布局的底部外边距
	 * 
	 * @param linearLayout
	 * @return
	 */
	public static int getLinearLayoutBottomMargin(LinearLayout linearLayout) {
		return ((LinearLayout.LayoutParams) linearLayout.getLayoutParams()).bottomMargin;
	}

	/**
	 * 设置流布局的底部外边距
	 * 
	 * @param linearLayout
	 * @param newBottomMargin
	 */
	public static void setLinearLayoutBottomMargin(LinearLayout linearLayout, int newBottomMargin) {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
		lp.bottomMargin = newBottomMargin;
		linearLayout.setLayoutParams(lp);
	}

	/**
	 * 获取流布局的高度
	 * 
	 * @param linearLayout
	 * @return
	 */
	public static int getLinearLayoutHiehgt(LinearLayout linearLayout) {
		return ((LinearLayout.LayoutParams) linearLayout.getLayoutParams()).height;
	}

	/**
	 * 设置输入框的光标到末尾
	 * 
	 * @param editText
	 */
	public static final void setEditTextSelectionToEnd(EditText editText) {
		Editable editable = editText.getEditableText();
		Selection.setSelection(editable, editable.toString().length());
	}

	/**
	 * 执行测量，执行完成之后只需调用View的getMeasuredXXX()方法即可获取测量结果
	 * 
	 * @param view
	 * @return
	 */
	public static final View measure(View view) {
		ViewGroup.LayoutParams p = view.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		view.measure(childWidthSpec, childHeightSpec);
		Logger.d(ViewUtil.class, "width=" + view.getMeasuredWidth() + "   height=" + view.getMeasuredHeight());
		return view;
	}

	/**
	 * 获取给定视图的测量高度
	 * 
	 * @param view
	 * @return
	 */
	public static final int getMeasuredHeight(View view) {
		return measure(view).getMeasuredHeight();
	}

	/**
	 * 获取给定视图的测量宽度
	 * 
	 * @param view
	 * @return
	 */
	public static final int getMeasuredWidth(View view) {
		return measure(view).getMeasuredWidth();
	}

	/**
	 * 获取视图1相对于视图2的位置，注意在屏幕上看起来视图1应该被视图2包含，但是视图1和视图并不一定是绝对的父子关系也可以是兄弟关系，
	 * 只是一个大一个小而已
	 * 
	 * @param view1
	 * @param view2
	 * @return
	 */
	public static final Rect getRelativeRect(View view1, View view2) {
		Rect childViewGlobalRect = new Rect();
		Rect parentViewGlobalRect = new Rect();
		view1.getGlobalVisibleRect(childViewGlobalRect);
		view2.getGlobalVisibleRect(parentViewGlobalRect);
		return new Rect(childViewGlobalRect.left - parentViewGlobalRect.left, childViewGlobalRect.top
				- parentViewGlobalRect.top, childViewGlobalRect.right - parentViewGlobalRect.left,
				childViewGlobalRect.bottom - parentViewGlobalRect.top);
	}

	/**
	 * 删除监听器
	 * 
	 * @param viewTreeObserver
	 * @param onGlobalLayoutListener
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static final void removeOnGlobalLayoutListener(ViewTreeObserver viewTreeObserver,
			ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			viewTreeObserver.removeGlobalOnLayoutListener(onGlobalLayoutListener);
		} else {
			viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener);
		}
	}

	@SuppressWarnings({ "unchecked" })
	public static <T extends View> T findViewById(View view, int id) {
		return (T) view.findViewById(id);
	}

	@SuppressWarnings({ "unchecked" })
	public static <T extends View> T findViewById(Activity activity, int id) {
		return (T) activity.findViewById(id);
	}

	public static interface OnTextChangedListener {
		public void onTextChanged(Editable s);
	}

	public static TextWatcher creatNumLimitWatcher(final EditText etDynamicMsg, final int numLimit,
			final OnTextChangedListener changedListener) {
		return new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
//				int nSelStart = 0;
//				int nSelEnd = 0;
//				boolean nOverMaxLength = false;
//
//				nSelStart = etDynamicMsg.getSelectionStart();
//				nSelEnd = etDynamicMsg.getSelectionEnd();
//				nOverMaxLength = (s.length() > numLimit) ? true : false;
//				if (nOverMaxLength) {
//					s.delete(nSelStart - 1, nSelEnd);
//					etDynamicMsg.setTextKeepState(s);
//				}
				changedListener.onTextChanged(s);
			}
		};
	}

	public static View creatTitleBarLineView(Context context) {
		View view = new View(context);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(
				R.dimen.general_line_narrow)));
		view.setBackgroundColor(context.getResources().getColor(R.color.titlebar_divider));
		return view;
	}
}
