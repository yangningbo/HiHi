package com.gaopai.guiren.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.text.InputFilter.LengthFilter;
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
import com.gaopai.guiren.adapter.RecommendUserAdapter;
import com.gaopai.guiren.bean.BatFollowResult;
import com.gaopai.guiren.bean.BatFollowResult.BatFollowBean;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.UserList;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class RecommendFriendFragment extends BaseFragment implements OnClickListener {
	private PullToRefreshListView mListView;
	private Button btnAddAll;
	private RecommendUserAdapter mAdapter;
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
				getUserList();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				getUserList();
			}
		});

		mAdapter = new RecommendUserAdapter(act, new AddClickListener());
		mListView.setAdapter(mAdapter);
//		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				String uid = ((User) mAdapter.getItem(position)).uid;
//				startActivity(ProfileActivity.getIntent(act, uid));
//			}
//		});
		mListView.doPullRefreshing(true, 0);
	}

	private void getUserList() {

		DamiInfo.getRecommendFriendList(new SimpleResponseListener(getActivity()) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final UserList data = (UserList) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						mAdapter.addAll(parseUserList(data.data));
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
	
	private List<User> parseUserList(List<User> sourceList) {
		List<User> resultList = new ArrayList<User>();
		int contactVipLen = 0;
		int contactNormalLen = 0;
		int vipNormalLen = 0;
		int normalLen = 0;
		for (int i = 0, len = sourceList.size(); i < len; i++) {
			User user = sourceList.get(i);
			if (user.iscontact == 1) {
				if (user.bigv == 1) {
					resultList.add(0, user);
					contactVipLen++;
				} else {
					resultList.add(contactVipLen, user);
					contactNormalLen++;
				}
				mAdapter.choseSet.add(user.uid);
			} else if (user.bigv == 1) {
				resultList.add(contactVipLen + contactNormalLen, user);
				vipNormalLen++;
				mAdapter.choseSet.add(user.uid);
			} else {
				resultList.add(user);
				normalLen++;
			}
		}
		int vipTotal = contactVipLen + contactNormalLen + vipNormalLen;
		if (vipTotal > 0 && normalLen > 0) {
			resultList.add(vipTotal, getBlankUser());
		}
		return resultList;
	}

	private User getBlankUser() {
		User user = new User();
		user.localType = 2;
		return user;
	}

	private class AddClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			User user = (User) v.getTag();
			if (user.iscontact == 1) {
				return;
			}
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
					act.sendBroadcast(new Intent(MainActivity.LOGIN_SUCCESS_ACTION));
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
				// getBaseActivity().showToast(R.string.please_choose_add);
				act.sendBroadcast(new Intent(MainActivity.LOGIN_SUCCESS_ACTION));
				getBaseActivity().finish();
				return;
			}
			addUser(mAdapter.getChoseIdString(), mAdapter.getCount());
			break;
		}
	}

}
