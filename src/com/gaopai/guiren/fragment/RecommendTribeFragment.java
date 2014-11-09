package com.gaopai.guiren.fragment;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.gaopai.guiren.BaseFragment;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.RecommendAdapter;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.TribeList;
import com.gaopai.guiren.bean.net.RecommendAddResult;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class RecommendTribeFragment extends BaseFragment implements OnClickListener {
	private PullToRefreshListView mListView;
	private Button btAddAll;
	private Button btnJumpOver;
	private View btnBack;
	private RecommendAdapter<Tribe> mAdapter;
	private String TAG = RecommendFriendFragment.class.getName();

	@Override
	public void addChildView(ViewGroup contentLayout) {
		// TODO Auto-generated method stub
		if (mView == null) {
			mView = mInflater.inflate(R.layout.fragment_recommend_tribe, null);
			contentLayout.addView(mView, layoutParamsFF);
			mListView = (PullToRefreshListView) mView.findViewById(R.id.listView);
			btAddAll = (Button) mView.findViewById(R.id.btn_add_all);
			btAddAll.setOnClickListener(this);
			btnJumpOver = (Button) mView.findViewById(R.id.btn_jump_over);
			btnJumpOver.setOnClickListener(this);
			initView();
		}
	}

	private void initView() {
		mTitleBar.setTitleText(R.string.recommend_tribe);
		btnBack = mTitleBar.setLogo(R.drawable.selector_back_btn);
		btnBack.setOnClickListener(new BackClickListener());

		mListView.setPullLoadEnabled(false);
		mListView.setPullRefreshEnabled(false);
		mListView.setScrollLoadEnabled(false);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				Log.d(TAG, "pulldown");
				// TODO Auto-generated method stub
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				Log.d(TAG, "pull up to");
			}
		});

		mAdapter = new RecommendAdapter<Tribe>(act, RecommendAdapter.RECOMMEND_TRIBE, new AddClickListener());
		mListView.setAdapter(mAdapter);
		getTribeList();
	}

	private class BackClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			getFragmentManager().popBackStack();
		}
	}

	private void getTribeList() {
		DamiInfo.getRecommendTribeList(new SimpleResponseListener(getActivity()) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final TribeList data = (TribeList) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						mAdapter.addAll(data.data);
					}
				} else {
					otherCondition(data.state, getActivity());
				}
			}

			@Override
			public void onFinish() {
				mListView.onPullComplete();
				removeProgressDialog();
			}
		});

	}

	private class AddClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Tribe tribe = (Tribe) v.getTag();
			List<Tribe> tribeList = new ArrayList<Tribe>();
			tribeList.add(tribe);
			addTribe(tribeList);
		}
	}

	private void addTribe(final List<Tribe> tribeList) {
		StringBuilder builder = new StringBuilder();
		for (Tribe tribe : tribeList) {
			builder.append(tribe.id);
			builder.append(",");
		}
		String re = builder.toString();
		DamiInfo.requestAddTribe(re.substring(0, re.length() - 1), "",
				new SimpleResponseListener(getActivity(), "正在请求") {
					@Override
					public void onSuccess(Object o) {
						// TODO Auto-generated method stub
						final RecommendAddResult data = (RecommendAddResult) o;
						if (data.state != null && data.state.code == 0) {
							changeAddInfo(tribeList);
						} else {
							otherCondition(data.state, getActivity());
						}
					}
				});
	}

	private void changeAddInfo(List<Tribe> tribeList) {
		for (Tribe tribe : tribeList) {
			tribe.isInTribe = true;
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_add_all:
			addTribe(mAdapter.mData);
			break;
		case R.id.btn_jump_over:
			getActivity().finish();
			break;
		}
	}
}
