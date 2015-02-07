package com.gaopai.guiren.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.MeetingAdapter;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.TribeList;
import com.gaopai.guiren.fragment.MeetingFragment;
import com.gaopai.guiren.support.ActionHolder;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class MyMeetingActivity extends BaseActivity implements OnClickListener {
	private PullToRefreshListView mListView;
	private MeetingAdapter mAdapter;

	private int page = 1;
	private boolean isFull = false;
	private String fid;
	private boolean isMyself = false;

	public final static int REQUEST_CREAT_MEETING = 69;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		fid = getIntent().getStringExtra("fid");
		isMyself = DamiCommon.getUid(mContext).equals(fid);
		initTitleBar();
		setAbContentView(R.layout.general_pulltorefresh_listview);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		if (isMyself) {
			mTitleBar.setTitleText(R.string.my_meeting);
			View view = mTitleBar.addRightButtonView(R.drawable.selector_titlebar_add);
			view.setId(R.id.ab_add);
			view.setOnClickListener(this);
		} else {
			mTitleBar.setTitleText(R.string.his_meeting);
		}
		mListView = (PullToRefreshListView) findViewById(R.id.listView);
		mListView.setPullRefreshEnabled(false); // 下拉刷新
		mListView.setPullLoadEnabled(false);// 上拉刷新，禁止
		mListView.setScrollLoadEnabled(true);// 滑动到底部自动刷新，启用
		mListView.getRefreshableView().setDivider(null);
		mListView.getRefreshableView().setSelector(mContext.getResources().getDrawable(R.color.transparent));
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getMeetingList(true, MeetingFragment.TYPE_MY_MEETING);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				getMeetingList(false, MeetingFragment.TYPE_MY_MEETING);
			}
		});
		mAdapter = new MeetingAdapter(mContext);
		mListView.setAdapter(mAdapter);
		mListView.doPullRefreshing(true, 50);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra(MeetingDetailActivity.KEY_MEETING_ID, ((Tribe) mAdapter.getItem(position)).id);
				intent.setClass(mContext, MeetingDetailActivity.class);
				startActivity(intent);

			}
		});
		IntentFilter filter = new IntentFilter();
		filter.addAction(ActionHolder.ACTION_CANCEL_MEETING);
		filter.addAction(ActionHolder.ACTION_QUIT_MEETING);
		registerReceiver(filter);
	}

	@Override
	protected void onReceive(Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(intent);
		String action = intent.getAction();
		if (action.equals(ActionHolder.ACTION_CANCEL_TRIBE) || action.equals(ActionHolder.ACTION_QUIT_TRIBE)) {
			String id = intent.getStringExtra("tid");
			if (!TextUtils.isEmpty(id)) {
				removeItem(id);
			}
		}
	}

	private void removeItem(String id) {
		for (int i = 0; i < mAdapter.getCount(); i++) {
			if (mAdapter.mData.get(i).id.equals(id)) {
				mAdapter.mData.remove(i);
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
				break;
			}
		}
	}

	public static Intent getIntent(Context context, String fid) {
		Intent intent = new Intent(context, MyMeetingActivity.class);
		intent.putExtra("fid", fid);
		return intent;
	}

	private void getMeetingList(final boolean isRefresh, int meetingType) {
		if (isRefresh) {
			page = 1;
			mAdapter.clear();
			isFull = false;
		}
		if (isFull) {
			mListView.setHasMoreData(!isFull);
			return;
		}
		DamiInfo.getMeetingList(fid, meetingType, page, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				final TribeList data = (TribeList) o;
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
					otherCondition(data.state, MyMeetingActivity.this);
				}
			}

			@Override
			public void onFinish() {
				mListView.onPullComplete();
				mListView.setHasMoreData(!isFull);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ab_add:
			startActivityForResult(CreatMeetingActivity.class, REQUEST_CREAT_MEETING);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CREAT_MEETING:
			if (resultCode == RESULT_OK) {
				mListView.doPullRefreshing(true, 200);
			}
			break;
		}
	}

}
