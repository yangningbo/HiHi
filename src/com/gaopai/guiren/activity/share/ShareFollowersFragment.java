package com.gaopai.guiren.activity.share;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.volley.SimpleResponseListener;


public class ShareFollowersFragment extends BaseShareFragment {
	private SimpleResponseListener listener;

	@Override
	protected View creatHeaderView() {
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new android.widget.AbsListView.LayoutParams(
				android.widget.AbsListView.LayoutParams.MATCH_PARENT,
				android.widget.AbsListView.LayoutParams.WRAP_CONTENT));
		layout.setOrientation(LinearLayout.VERTICAL);

		LayoutParams textLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		View view = creatHeaderTextView("我的粉丝");
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ShareFansFragment shareFansFragment = new ShareFansFragment();
				getFragmentManager().beginTransaction()
						.replace(R.id.fl_fragment_holder, shareFansFragment, ShareFansFragment.class.getName())
						.addToBackStack(null).commit();

			}
		});
		layout.addView(view, textLp);
		
		if (((ShareActivity)getActivity()).type != ShareActivity.TYPE_SHARE) {
			return layout;
		}
		if (((ShareActivity) getActivity()).type == ShareActivity.TYPE_SHARE) {
			view = new View(getActivity());
			view.setBackgroundColor(getResources().getColor(R.color.black));
			layout.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, 1));

			view = creatHeaderTextView("我的圈子");
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ShareTribeFragment shareTribeFragment = new ShareTribeFragment();
					getFragmentManager().beginTransaction()
							.replace(R.id.fl_fragment_holder, shareTribeFragment, ShareTribeFragment.class.getName())
							.addToBackStack(null).commit();
				}
			});
			layout.addView(view, textLp);
		}
		return layout;
	}

	private TextView creatHeaderTextView(String text) {
		int padding = MyUtils.dip2px(getActivity(), 10);
		TextView view = new TextView(getActivity());
		view.setText(text);
		view.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_text_btn));
		view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.profile_tab_arrow, 0);
		view.setPadding(padding, padding, padding, padding);
		return view;
	}
	
}
