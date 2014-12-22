package com.gaopai.guiren.support.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.MyUtils;

public class AgreeAnimWindow {
	public static void showAnim(View parent) {
		final PopupWindow popupWindow;
		Context context = parent.getContext();
		int width = MyUtils.dip2px(context, 40);
		int height = MyUtils.dip2px(context, 40);
		View view = creatAgreeView(parent.getContext());
		popupWindow = new PopupWindow(view, height, width, true);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(R.style.agree_popwin_anim_style);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		parent.postDelayed(new Runnable() {
			@Override
			public void run() {
				popupWindow.dismiss();
			}
		}, 740);
		popupWindow.showAsDropDown(parent, (parent.getWidth() - width) / 2, -parent.getHeight() - height);
	}

	public static View creatAgreeView(Context context) {
		TextView textView = new TextView(context);
		textView.setText("+1");
		textView.setTextSize(17);
		textView.setTextColor(Color.WHITE);
		textView.setBackgroundColor(context.getResources().getColor(R.color.blue_dongtai_name));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MyUtils.dip2px(context, 40), MyUtils.dip2px(
				context, 40));
		textView.setGravity(Gravity.CENTER);
		textView.setLayoutParams(lp);
		return textView;
	}
}
