package com.gaopai.guiren.activity.share;

import com.gaopai.guiren.R;

import android.view.View;

public class ShareFansFragment extends BaseShareFragment {
	
	@Override
	protected View creatHeaderView() {
		
		return new View(getActivity());
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getShareActivity().setTitleText(R.string.fans);
	}
}
