package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.CopyOfConnectionAdapter;
import com.gaopai.guiren.adapter.CopyOfConnectionAdapter.Item;
import com.gaopai.guiren.adapter.CopyOfConnectionAdapter.Row;
import com.gaopai.guiren.adapter.CopyOfConnectionAdapter.Section;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.UserList;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshIndexableListView;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.volley.SimpleResponseListener;
import com.gaopai.guiren.widget.indexlist.IndexableListView;
import com.gaopai.guiren.widget.indexlist.SingleIndexScroller;

public class ContactActivity extends BaseActivity {
	public final static int KEY_FOLLOWERS = 0;
	public final static int KEY_FANS = 1;
	public final static String KEY_TYPE = "type";

	private int type;

	private PullToRefreshIndexableListView mListView;
	private CopyOfConnectionAdapter mAdapter;
	private SingleIndexScroller indexScroller;

	private ArrayList<Item> mItems;
	private ArrayList<Row> mRows;

	private SimpleResponseListener listener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		type = getIntent().getIntExtra(KEY_TYPE, KEY_FANS);
		initTitleBar();
		setAbContentView(R.layout.activity_contact_list);

		if (type == KEY_FANS) {
			mTitleBar.setTitleText(getString(R.string.fans));
		} else {
			mTitleBar.setTitleText(getString(R.string.follow));
		}
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);

		mListView = (PullToRefreshIndexableListView) findViewById(R.id.listView);
		indexScroller = (SingleIndexScroller) findViewById(R.id.scroller);

		mListView.getRefreshableView().addHeaderView(new View(mContext));
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
				intent.setClass(mContext, UserInfoActivity.class);
				startActivity(intent);
			}
		});

		mAdapter = new CopyOfConnectionAdapter();
		mListView.setAdapter(mAdapter);
		mListView.getRefreshableView().setFastScrollEnabled(false);
		indexScroller.setListView(mListView.getRefreshableView());

		mItems = new ArrayList<Item>();
		mRows = new ArrayList<Row>();

		mListView.doPullRefreshing(true, 0);
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
		if (type == KEY_FANS) {
			DamiInfo.getFriendsList(new MyListener(mContext, isRefresh));
		} else {
			DamiInfo.getFansList(new MyListener(mContext, isRefresh));
		}
	}

	public void hideIndexedTextWhenChangePage() {
		indexScroller.hide();
	}

	class MyListener extends SimpleResponseListener {

		private boolean isRefresh = false;

		public MyListener(Context context, boolean isRefresh) {
			super(context);
			// TODO Auto-generated constructor stub
		}

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
				otherCondition(data.state, ContactActivity.this);
			}

		}

		@Override
		public void onFinish() {
			mListView.onPullComplete();
		}
	}

}
