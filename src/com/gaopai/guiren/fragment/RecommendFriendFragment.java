package com.gaopai.guiren.fragment;

import java.util.ArrayList;
import java.util.List;

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
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.UserInfoActivity;
import com.gaopai.guiren.adapter.RecommendAdapter;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.UserList;
import com.gaopai.guiren.bean.net.RecommendAddResult;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class RecommendFriendFragment extends BaseFragment implements OnClickListener {
	private PullToRefreshListView mListView;
	private Button btnAddAll;
	private View btnJumpOver;
	private RecommendAdapter<User> mAdapter;
	private String TAG = RecommendFriendFragment.class.getName();

	@Override
	public void addChildView(ViewGroup contentLayout) {
		// TODO Auto-generated method stub
		if (mView == null) {
			mView = mInflater.inflate(R.layout.fagment_recommend_friend, null);
			contentLayout.addView(mView, layoutParamsFF);
			mListView = (PullToRefreshListView) mView.findViewById(R.id.listView);
			btnAddAll = (Button) mView.findViewById(R.id.btn_add_all);
			btnAddAll.setOnClickListener(this);
			initView();
		}
	}

	private void initView() {
		mTitleBar.setTitleText(R.string.recommend_friend);

		btnJumpOver = mTitleBar.addRightTextView("跳过");
		btnJumpOver.setOnClickListener(new JumpOverClickListener());

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
				Intent intent = new Intent();
				intent.putExtra(UserInfoActivity.KEY_UID, uid);
				intent.setClass(getActivity(), UserInfoActivity.class);
				startActivity(intent);
			}
		});
		getUserList();
	}

	private class JumpOverClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			RecommendTribeFragment tribeFragment = (RecommendTribeFragment) getFragmentManager().findFragmentByTag(
					"tribe");
			if (tribeFragment == null) {
				tribeFragment = new RecommendTribeFragment();
			}
			getFragmentManager().beginTransaction().replace(android.R.id.content, tribeFragment, "tribe")
					.addToBackStack(null).commit();

		}
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
			List<User> userList = new ArrayList<User>();
			userList.add(user);
			addUser(userList);
		}
	}

	private void addUser(final List<User> userList) {
		StringBuilder builder = new StringBuilder();
		for (User user : userList) {
			builder.append(user.uid);
			builder.append(",");
		}
		String re = builder.toString();
		DamiInfo.requestAddFriend(re.substring(0, re.length() - 1), "", "", new SimpleResponseListener(getActivity(),
				"正在请求") {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final RecommendAddResult data = (RecommendAddResult) o;
				if (data.state != null && data.state.code == 0) {
					changeAddInfo(userList);
				} else {
					this.otherCondition(data.state, getActivity());
				}
			}
		});
	}

	private void changeAddInfo(List<User> userList) {
		for (User user : userList) {
			user.relation = 1;
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_add_all:
			addUser(mAdapter.mData);
			break;
		}
	}

}
