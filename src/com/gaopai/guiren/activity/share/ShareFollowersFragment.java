package com.gaopai.guiren.activity.share;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.gaopai.guiren.R;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class ShareFollowersFragment extends BaseShareFragment {
	private SimpleResponseListener listener;

	@Override
	protected void creatHeaderView(ListView listView) {

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

		listView.addHeaderView(view);

		if (((ShareActivity) getActivity()).type == ShareActivity.TYPE_SHARE) {
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
			listView.addHeaderView(view);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getShareActivity().setTitleText(R.string.follow);
	}

	private TextView creatHeaderTextView(String text) {
		int padding = MyUtils.dip2px(getActivity(), 10);
		TextView view = new TextView(getActivity());
		view.setText(text);
		view.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_text_btn));
		view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.profile_tab_arrow, 0);
		view.setPadding(padding, padding, padding + padding, padding);
		return view;
	}

}
