package com.gaopai.guiren.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.RecommendAdapter;
import com.gaopai.guiren.bean.NewUser;
import com.gaopai.guiren.bean.NewUserList;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.SimpleStateBean;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class FriendsActivity extends BaseActivity {
	public static final String KEY_NEW_OR_REC = "new_or_rec";
	public static final int NEW_FRIEND = 0;
	public static final int REC_FRIEND = 1;

	private int mType;
	private PullToRefreshListView mListView;

	private RecommendAdapter<NewUser> mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.single_pulltorefresh_listview);
		mType = getIntent().getIntExtra(KEY_NEW_OR_REC, NEW_FRIEND);
		String title = (mType == NEW_FRIEND ? getString(R.string.new_friends)
				: getString(R.string.new_friends_recommend));
		mTitleBar.setTitleText(title);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mListView = (PullToRefreshListView) findViewById(R.id.listview);

		mListView.setPullLoadEnabled(false);
		mListView.setPullRefreshEnabled(false);
		mListView.setScrollLoadEnabled(true);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				getUserList();
			}
		});

		int type = (mType == REC_FRIEND ? RecommendAdapter.REC_FRIEND : RecommendAdapter.NEW_FRIEND);
		mAdapter = new RecommendAdapter<NewUser>(mContext, type, new AddClickListener());
		mListView.setAdapter(mAdapter);
		// mListView.doPullRefreshing(true, 0);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				String uid = ((User) mAdapter.getItem(position)).uid;
				Intent intent = new Intent();
				intent.putExtra(UserInfoActivity.KEY_UID, uid);
				intent.setClass(mContext, UserInfoActivity.class);
				startActivity(intent);
			}
		});
		getUserList();
	}

	private class AddClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (mType == REC_FRIEND) {
				User user = (User) v.getTag();
				Intent intent = new Intent();
				intent.setClass(mContext, AddReasonActivity.class);
				intent.putExtra(AddReasonActivity.KEY_APLLY_TYPE, AddReasonActivity.TYPE_ADD_FRIEND);
				intent.putExtra(AddReasonActivity.KEY_USER, user);
				startActivityForResult(intent, 0);
			} else {
				NewUser user = (NewUser) v.getTag();
				addUser(user);
			}
		}
	}

	private int page = 1;
	private boolean isFull = false;

	private void getUserList() {
		if (isFull) {
			mListView.setHasMoreData(!isFull);
			return;
		}
		int obj = (mType == REC_FRIEND ? NewUserList.TYPE_RECOMMEND_FRIEND : NewUserList.TYPE_NEW_FRIEND);
		DamiInfo.getNewRecFriendsList(page, obj, new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final NewUserList data = (NewUserList) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						mAdapter.addAll(data.data);
					}
					if (data.pageInfo != null) {
						isFull = (data.pageInfo.hasMore == 0);
						if (!isFull) {
							page++;
						}
					}
					mListView.setHasMoreData(!isFull);
				} else {
					otherCondition(data.state, FriendsActivity.this);
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				mListView.onPullComplete();
			}
		});
	}

	private void addUser(final NewUser user) {
		DamiInfo.dealAddUser(user.uid, 1, "", new SimpleResponseListener(mContext,
				getString(R.string.request_internet_now)) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final SimpleStateBean data = (SimpleStateBean) o;
				if (data.state != null && data.state.code == 0) {
					changeAddInfo(user);
				} else {
					this.otherCondition(data.state, FriendsActivity.this);
				}
			}
		});
	}

	private void changeAddInfo(NewUser user) {
		user.status = 1;
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			User user = (User) data.getSerializableExtra(AddReasonActivity.KEY_USER);
			for (User muser : mAdapter.mData) {
				if (muser.uid.equals(user.uid)) {
					muser.relation = 1;
					mAdapter.notifyDataSetChanged();
				}
			}
			break;
		}
	}
}
