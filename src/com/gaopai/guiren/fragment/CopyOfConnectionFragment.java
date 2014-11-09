package com.gaopai.guiren.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.gaopai.guiren.BaseFragment;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.FriendsActivity;
import com.gaopai.guiren.activity.SearchActivity;
import com.gaopai.guiren.activity.TribeActivity;
import com.gaopai.guiren.activity.UserInfoActivity;
import com.gaopai.guiren.adapter.ConnectionAdapter;
import com.gaopai.guiren.adapter.CopyOfConnectionAdapter;
import com.gaopai.guiren.adapter.CopyOfConnectionAdapter.Item;
import com.gaopai.guiren.adapter.CopyOfConnectionAdapter.Row;
import com.gaopai.guiren.adapter.CopyOfConnectionAdapter.Section;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.UserList;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshIndexableListView;
import com.gaopai.guiren.volley.SimpleResponseListener;
import com.gaopai.guiren.widget.indexlist.IndexableListView;
import com.gaopai.guiren.widget.indexlist.SingleIndexScroller;

//I don't know how to spell 人脉 in English
public class CopyOfConnectionFragment extends BaseFragment implements OnClickListener {
	private PullToRefreshIndexableListView mListView;
	private CopyOfConnectionAdapter mAdapter;
	private SingleIndexScroller indexScroller;
	private TextView tvNewFriends;
	private TextView tvRecommendFriends;
	private TextView tvMyTribe;
	private View headerView;

	private ArrayList<Item> mItems;
	private ArrayList<Row> mRows;

	@Override
	public void addChildView(ViewGroup contentLayout) {
		if (mView == null) {
			mView = mInflater.inflate(R.layout.fragment_connection, null);
			contentLayout.addView(mView, layoutParamsFF);
			FinalActivity.initInjectedView(this, mView);
			headerView = mInflater.inflate(R.layout.fragment_connection_header, null);
			initView();
		} else {
			((ViewGroup) mView.getParent()).removeView(mView);
		}
	}

	private void initView() {
		addButtonToTitleBar();
		mTitleBar.setTitleText(getString(R.string.connection_title));
		mListView = (PullToRefreshIndexableListView) mView.findViewById(R.id.listView);
		indexScroller = (SingleIndexScroller) mView.findViewById(R.id.scroller);
		tvMyTribe = (TextView) headerView.findViewById(R.id.tv_my_tribe);
		tvMyTribe.setOnClickListener(this);
		tvNewFriends = (TextView) headerView.findViewById(R.id.tv_my_new_friends);
		tvNewFriends.setOnClickListener(this);
		tvRecommendFriends = (TextView) headerView.findViewById(R.id.tv_my_recommended_friends);
		tvRecommendFriends.setOnClickListener(this);

		mListView.getRefreshableView().addHeaderView(headerView);
		mListView.getRefreshableView().setVerticalScrollBarEnabled(false);
		mListView.setPullRefreshEnabled(true);
		mListView.setPullLoadEnabled(false);
		mListView.setScrollLoadEnabled(false);
		mListView.setOnRefreshListener(new OnRefreshListener<IndexableListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<IndexableListView> refreshView) {
				// TODO Auto-generated method stub
				getUserList(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<IndexableListView> refreshView) {
				// TODO Auto-generated method stub
				getUserList(false);
			}
		});
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				int pos = position - mListView.getRefreshableView().getHeaderViewsCount();
				String uid = ((Item) mAdapter.getItem(pos)).user.uid;
				Intent intent = new Intent();
				intent.putExtra(UserInfoActivity.KEY_UID, uid);
				intent.setClass(getActivity(), UserInfoActivity.class);
				startActivity(intent);
			}
		});

		mAdapter = new CopyOfConnectionAdapter();
		mListView.setAdapter(mAdapter);
		mListView.getRefreshableView().setFastScrollEnabled(false);
		indexScroller.setListView(mListView.getRefreshableView());

		mListView.doPullRefreshing(true, 0);
		mItems = new ArrayList<Item>();
		mRows = new ArrayList<Row>();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_my_tribe:
			startActivity(TribeActivity.class);
			break;
		case R.id.tv_my_new_friends: {
			Intent intent = new Intent();
			intent.setClass(act, FriendsActivity.class);
			intent.putExtra(FriendsActivity.KEY_NEW_OR_REC, FriendsActivity.NEW_FRIEND);
			startActivity(intent);
			break;
		}
		case R.id.tv_my_recommended_friends: {
			Intent intent = new Intent();
			intent.setClass(act, FriendsActivity.class);
			intent.putExtra(FriendsActivity.KEY_NEW_OR_REC, FriendsActivity.REC_FRIEND);
			startActivity(intent);
			break;
		}
		case R.id.ab_search: {
			Intent intent = new Intent();
			intent.setClass(act, SearchActivity.class);
			intent.putExtra(SearchActivity.KEY_SEARCH_ORDER, SearchActivity.SEARCH_FRIEND);
			startActivity(intent);
			return;
		}
		default:
			break;
		}
		super.onClick(v);
	}

	private int page = 1;
	private boolean isFull = false;

	private void sortData(List<User> userList) {
		int size = userList.size();
		mItems.clear();
		for (int i = 0; i < size; i++) {
			mItems.add(new Item(userList.get(i)));
		}
		Collections.sort(mItems);
		char character = '0';
		for (int i = 0; i < mItems.size(); i++) {
			Item item = mItems.get(i);
			char first = item.pingYinText.charAt(0);
			if (i == 0 && (first < 'A' || first > 'Z')) {
				mRows.add(new Section("#"));
			}
			if (first >= 'A' && first <= 'Z') {
				if (character != first) {
					mRows.add(new Section(String.valueOf(first)));
					character = first;
				}
			}
			mRows.add(item);
		}
		mAdapter.addAll(mRows);
	}

	private void getUserList(final boolean isRefresh) {
		DamiInfo.getFriendsList(new SimpleResponseListener(getActivity()) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final UserList data = (UserList) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						if (isRefresh) {
							mAdapter.clear();
						}
						sortData(data.data);
					}
					mListView.setHasMoreData(!isFull);
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

	public void hideIndexedTextWhenChangePage() {
		indexScroller.hide();
	}
}
