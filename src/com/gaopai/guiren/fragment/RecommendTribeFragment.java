package com.gaopai.guiren.fragment;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.gaopai.guiren.BaseFragment;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.TribeDetailActivity;
import com.gaopai.guiren.adapter.RecommendAdapter;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.TribeList;
import com.gaopai.guiren.bean.net.RecommendAddResult;
import com.gaopai.guiren.support.FragmentHelper;
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
		btnBack = mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		btnBack.setOnClickListener(new BackClickListener());

		mListView.setPullLoadEnabled(false);
		mListView.setPullRefreshEnabled(false);
		mListView.setScrollLoadEnabled(false);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getTribeList();
				// TODO Auto-generated method stub
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
			}
		});

		mAdapter = new RecommendAdapter<Tribe>(act, RecommendAdapter.RECOMMEND_TRIBE, new AddClickListener());
		mListView.setAdapter(mAdapter);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				String tid = ((Tribe) mAdapter.getItem(position)).id;
				startActivity(TribeDetailActivity.getIntent(act, tid));
			}
		});
		mListView.doPullRefreshing(true, 0);
	}

	private class BackClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			FragmentHelper.replaceFragment(android.R.id.content, getFragmentManager(), RecommendFriendFragment.class);
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
			mAdapter.addIdToChoseSet(tribe.id);
		}
	}

	private void addTribe(String ids) {
		DamiInfo.requestAddTribe(ids, "", new SimpleResponseListener(getActivity(), R.string.request_internet_now) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final RecommendAddResult data = (RecommendAddResult) o;
				if (data.state != null && data.state.code == 0) {
					getActivity().finish();
					// changeAddInfo(tribeList);
				} else {
					otherCondition(data.state, getActivity());
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_add_all:
			if (mAdapter.choseSet.size() == 0) {
				getBaseActivity().showToast(R.string.please_choose_add);
				return;
			}
			addTribe(mAdapter.getChoseIdString());
			break;
		case R.id.btn_jump_over:
			getActivity().finish();
			break;
		}
	}
}
