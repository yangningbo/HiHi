package com.gaopai.guiren.activity.share;

import com.gaopai.guiren.R;

import android.view.View;
import android.widget.ListView;

public class ShareFansFragment extends BaseShareFragment {
	
	@Override
	protected void creatHeaderView(ListView listView) {
		listView.addHeaderView(new View(getActivity()));
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getShareActivity().setTitleText(R.string.fans);
	}
}
