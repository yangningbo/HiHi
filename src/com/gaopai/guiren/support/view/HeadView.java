package com.gaopai.guiren.support.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyUtils;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class HeadView extends ViewGroup {

	private ImageView ivHeader;
	private ImageView ivMvp;

	public HeadView(Context context) {
		super(context);
	}

	public HeadView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HeadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();

		ivHeader = (ImageView) getChildAt(0);
		ivMvp = (ImageView) getChildAt(1);
	}

	public void setImage(String url) {
		ImageLoaderUtil.displayImage(url, ivHeader, R.drawable.default_header);
	}

	public void setMVP(boolean isMvp) {
		ivMvp.setVisibility(isMvp ? View.VISIBLE : View.GONE);
		invalidate();
	}

	public static String MVP_NAME_STR = " [**]";

	public static Spannable getMvpName(Context context, String name) {
		return getMvpName(context, new SpannableString(name));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int dimen = ivHeader.getLayoutParams().width;
		int measureSpec = MeasureSpec.makeMeasureSpec(dimen, MeasureSpec.EXACTLY);
		// ivHeader.measure(measureSpec, measureSpec);
		measureChild(ivHeader, widthMeasureSpec, heightMeasureSpec);
		if (ivMvp.getVisibility() == VISIBLE) {
			measureChild(ivMvp, widthMeasureSpec, heightMeasureSpec);
			int marginBottom = ((MarginLayoutParams) ivMvp.getLayoutParams()).bottomMargin;
			int marginRight = ((MarginLayoutParams) ivMvp.getLayoutParams()).rightMargin;
			setMeasuredDimension(dimen - marginRight, dimen - marginBottom);
			return;
		}
		setMeasuredDimension(dimen, dimen);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		// TODO Auto-generated method stub
		return new MarginLayoutParams(getContext(), attrs);
	}

	@Override
	protected LayoutParams generateLayoutParams(LayoutParams p) {
		// TODO Auto-generated method stub
		return new MarginLayoutParams(p);
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		// TODO Auto-generated method stub
		return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public static Spannable getMvpName(Context context, Spannable name) {
		Spannable spannableString = name;
		Matcher matcher = Pattern.compile("\\[\\*\\*\\]").matcher(name);
		if (matcher.find()) {
			int k = matcher.start();
			int m = matcher.end();
			spannableString.setSpan(
					new ImageSpan(context, R.drawable.icon_mvp_text, DynamicDrawableSpan.ALIGN_BASELINE), k, m,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return spannableString;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		ivHeader.layout(0, 0, ivHeader.getMeasuredWidth(), ivHeader.getMeasuredHeight());
		if (ivMvp.getVisibility() == VISIBLE) {
			ivMvp.layout(getMeasuredWidth() - ivMvp.getMeasuredWidth(), getMeasuredHeight() - ivMvp.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
		}
	}
}
