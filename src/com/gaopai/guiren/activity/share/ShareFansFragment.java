package com.gaopai.guiren.activity.share;

import android.view.View;
import android.widget.ListView;

import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.share.BaseShareFragment.MyListener;

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
		getShareActivity().setBackListener(backToShareFollower);
	}

	@Override
	protected void getUserList(boolean isRefresh) {
		// TODO Auto-generated method stub
		super.getUserList(isRefresh);
		DamiInfo.getFansList(mLogin.uid, searchHolder.getPage(), searchHolder.searchText, new MyListener(getActivity(),
				isRefresh));
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		getShareActivity().setCurrentFragment(this);
	}

}
