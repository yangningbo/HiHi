package com.gaopai.guiren.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.gaopai.guiren.BaseFragment;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.activity.ProfileActivity;
import com.gaopai.guiren.adapter.RecommendAdapter;
import com.gaopai.guiren.bean.BatFollowResult;
import com.gaopai.guiren.bean.BatFollowResult.BatFollowBean;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.UserList;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class RecommendFriendFragment extends BaseFragment implements OnClickListener {
	private PullToRefreshListView mListView;
	private Button btnAddAll;
	private RecommendAdapter<User> mAdapter;
	private String TAG = RecommendFriendFragment.class.getName();

	@Override
	public void addChildView(ViewGroup contentLayout) {
		// TODO Auto-generated method stub
		if (mView == null) {
			mView = mInflater.inflate(R.layout.fagment_recommend_friend, null);
			contentLayout.addView(mView, layoutParamsFF);
			mListView = (PullToRefreshListView) mView.findViewById(R.id.listView);
			ViewUtil.findViewById(mView, R.id.btn_follow).setOnClickListener(this);
			initView();
		}
	}

	private void initView() {

		mTitleBar.setTitleText(R.string.recommend_friend);

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

		mAdapter = new RecommendAdapter<User>(act, RecommendAdapter.RECOMMEND_FRIEND, new AddClickListener());
		mListView.setAdapter(mAdapter);
		// mListView.doPullRefreshing(true, 0);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				String uid = ((User) mAdapter.getItem(position)).uid;
				startActivity(ProfileActivity.getIntent(act, uid));
			}
		});
		getUserList();
	}

	private void getUserList() {

		DamiInfo.getRecommendFriendList(new SimpleResponseListener(getActivity()) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final UserList data = (UserList) o;
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
				super.onFinish();
				mListView.onPullComplete();
			}

		});

	}

	private class AddClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			User user = (User) v.getTag();
			mAdapter.addIdToChoseSet(user.uid);
		}
	}

	private void addUser(String id, int length) {

		DamiInfo.followInBat(id, new SimpleResponseListener(getActivity(), R.string.request_internet_now) {
			@Override
			public void onSuccess(Object o) {
				final BatFollowResult data = (BatFollowResult) o;
				if (data.state != null && data.state.code == 0) {
					BatFollowBean bean = data.data;
					User user = DamiCommon.getLoginResult(act);
					user.followers = user.followers + bean.complete;
					DamiCommon.saveLoginResult(act, user);
					act.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_PROFILE));
					getActivity().finish();
				} else {
					this.otherCondition(data.state, getActivity());
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_follow:
			if (mAdapter.choseSet.size() == 0) {
//				getBaseActivity().showToast(R.string.please_choose_add);
				getBaseActivity().finish();
				return;
			}
			addUser(mAdapter.getChoseIdString(), mAdapter.getCount());
			break;
		}
	}

}
