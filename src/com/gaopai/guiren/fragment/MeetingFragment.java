package com.gaopai.guiren.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.gaopai.guiren.BaseFragment;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.activity.MeetingDetailActivity;
import com.gaopai.guiren.activity.SearchActivity;
import com.gaopai.guiren.activity.TribeActivity;
import com.gaopai.guiren.adapter.MeetingAdapter;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.TribeList;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class MeetingFragment extends BaseFragment implements OnClickListener {

	private String TAG = MeetingFragment.class.getName();
	@ViewInject(id = R.id.listView)
	private PullToRefreshListView mListView;

	private MeetingAdapter mAdapter;

	public static final int TYPE_ONGOING_MEETING = 1;
	public static final int TYPE_PAST_MEETING = 2;
	public static final int TYPE_MY_MEETING = 3;

	private int meetingType;

	public final static String REFRESH_LIST_ACTION = "com.gaopai.guiren.intent.action.REFRESH_LIST_ACTION";

	private TextView tvOnGoingMeeting;
	private TextView tvPastMeeting;
	
	private boolean intialFlag = true;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mView == null) {
			mView = mInflater.inflate(R.layout.fragment_meeting, null);
			FinalActivity.initInjectedView(this, mView);
			initView(mView);
		}
		return mView;
	}

	@Override
	protected void onReceive(Intent intent) {
		String action = intent.getAction();
		if (!TextUtils.isEmpty(action)) {
			if (action.equals(REFRESH_LIST_ACTION)) {
				getMeetingList(true, meetingType);
			} else if (action.equals(TribeActivity.ACTION_KICK_TRIBE)) {
				String id = intent.getStringExtra("id");
				if (!TextUtils.isEmpty(id)) {
					for (int i = 0; i < mAdapter.mData.size(); i++) {
						if (mAdapter.mData.get(i).id.equals(id)) {
							mAdapter.mData.remove(i);
							mAdapter.notifyDataSetChanged();
							break;
						}
					}
				}
			} else if (action.equals(MainActivity.LOGIN_SUCCESS_ACTION)) {
				getMeetingList(true, meetingType);
			}
		}
	}

	private void initView(View mView) {

		meetingType = TYPE_ONGOING_MEETING;

		mListView.setPullRefreshEnabled(true); // 下拉刷新
		mListView.setPullLoadEnabled(false);// 上拉刷新，禁止
		mListView.setScrollLoadEnabled(true);// 滑动到底部自动刷新，启用
		mListView.getRefreshableView().setDivider(null);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getMeetingList(true, meetingType);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				getMeetingList(false, meetingType);
			}
		});
		mAdapter = new MeetingAdapter(act);
		mListView.setAdapter(mAdapter);

		mListView.doPullRefreshing(true, 50);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra(MeetingDetailActivity.KEY_MEETING_ID, ((Tribe) mAdapter.getItem(position)).id);
				intent.setClass(getActivity(), MeetingDetailActivity.class);
				startActivity(intent);

			}
		});

		ViewGroup viewGroup = ViewUtil.findViewById(mView, R.id.layout_meeting_past);
		tvPastMeeting = (TextView) viewGroup.getChildAt(0);
		viewGroup.setOnClickListener(this);
		viewGroup = ViewUtil.findViewById(mView, R.id.layout_meeting_faxian);
		tvOnGoingMeeting = (TextView) viewGroup.getChildAt(0);
		viewGroup.setOnClickListener(this);

		registerReceiver(REFRESH_LIST_ACTION, MainActivity.LOGIN_SUCCESS_ACTION);
	}

	private int page = 1;
	private boolean isFull = false;

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
		DamiInfo.getMeetingList(DamiCommon.getUid(act), meetingType, page, new MyResponseListener(getActivity(),
				meetingType));
	}

	public class MyResponseListener extends SimpleResponseListener {
		private int type;

		public MyResponseListener(Context context, int type) {
			super(context);
			this.type = type;
		}

		@Override
		public void onSuccess(Object o) {
			final TribeList data = (TribeList) o;
			if (data.state != null && data.state.code == 0) {
				if (type != meetingType) {
					return;
				}
				if (data.data != null && data.data.size() > 0) {
					mAdapter.addAll(data.data);
				} else {
					if (intialFlag) {
						mListView.onPullComplete();
						mListView.postDelayed(new Runnable() {
							@Override
							public void run() {
								switchPage(TYPE_PAST_MEETING);
							}
						}, 800);
					}
				}
				if (data.pageInfo != null) {
					isFull = (data.pageInfo.hasMore == 0);
					if (!isFull) {
						page++;
					}
				}
				mListView.setHasMoreData(!isFull);
				
			
				intialFlag = false;
			} else {
				otherCondition(data.state, getActivity());
			}
		}

		@Override
		public void onFinish() {
			mListView.onPullComplete();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ab_search: {
			Intent intent = new Intent();
			intent.setClass(act, SearchActivity.class);
			intent.putExtra(SearchActivity.KEY_SEARCH_ORDER, SearchActivity.SEARCH_MEETING);
			startActivity(intent);
			return;
		}
		case R.id.layout_meeting_faxian:
			switchPage(TYPE_ONGOING_MEETING);
			break;
		case R.id.layout_meeting_past:
			switchPage(TYPE_PAST_MEETING);
			break;
		default:
			super.onClick(v);
		}
	}
	
	private void switchPage(int type) {
		if (type == TYPE_ONGOING_MEETING) {
			meetingType = TYPE_ONGOING_MEETING;
			tvPastMeeting.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvOnGoingMeeting.setBackgroundResource(R.drawable.shape_bottom_blue_border);
			mListView.doPullRefreshing(true, 0);
		} else {
			meetingType = TYPE_PAST_MEETING;
			tvOnGoingMeeting.setBackgroundColor(getResources().getColor(R.color.transparent));
			tvPastMeeting.setBackgroundResource(R.drawable.shape_bottom_blue_border);
			mListView.doPullRefreshing(true, 0);
		}
	}
	
	private class MeetingPagerAdapter extends FragmentPagerAdapter {
		public MeetingPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}
	}

}
