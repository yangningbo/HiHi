package com.gaopai.guiren.activity.share;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.TribeAdapter;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.TribeList;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class ShareTribeFragment extends BaseShareFragment {

	private TribeAdapter mAdapter;
	private PullToRefreshListView mListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.general_pulltorefresh_listview, null);
		initView(view);
		getTribeList();
		
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getShareActivity().setTitleText(R.string.tribe);
	}
	private void initView(View mView) {
		// TODO Auto-generated method stub
		mListView = (PullToRefreshListView) mView.findViewById(R.id.listView);
		mListView.getRefreshableView().setVerticalScrollBarEnabled(false);
		mListView.setPullRefreshEnabled(false);
		mListView.setPullLoadEnabled(false);
		mListView.setScrollLoadEnabled(false);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				int pos = position - mListView.getRefreshableView().getHeaderViewsCount();
				Tribe tribe = (Tribe) mAdapter.getItem(pos);
				showDialog(tribe);
			}
		});
		mAdapter = new TribeAdapter(getActivity());
		mListView.setAdapter(mAdapter);
	}

	private int page = 1;
	private boolean isFull = false;

	private void getTribeList() {
		if (isFull) {
			mListView.setHasMoreData(!isFull);
			return;
		}
		DamiInfo.getTribeList(new SimpleResponseListener(getActivity()) {
			@Override
			public void onSuccess(Object o) {
				final TribeList data = (TribeList) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						List<Tribe> tribeList = data.data;
						mAdapter.addAll(tribeList);
					}
				} else {
					otherCondition(data.state, getActivity());
				}
			}

			@Override
			public void onFinish() {
				mListView.onPullComplete();
			}

		});
	}
}
