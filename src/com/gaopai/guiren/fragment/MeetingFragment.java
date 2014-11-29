package com.gaopai.guiren.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gaopai.guiren.BaseFragment;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.activity.MeetingDetailActivity;
import com.gaopai.guiren.activity.SearchActivity;
import com.gaopai.guiren.activity.TribeActivity;
import com.gaopai.guiren.adapter.MeetingAdapter;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.TribeList;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class MeetingFragment extends BaseFragment implements OnClickListener {

	private String TAG = MeetingFragment.class.getName();
	@ViewInject(id = R.id.listView)
	private PullToRefreshListView mListView;

	private MeetingAdapter mAdapter;

	private View onGoingMeetingBtn;
	private View pastMeetingBtn;
	private View myMeetingBtn;

	public static final int TYPE_ONGOING_MEETING = 1;
	public static final int TYPE_PAST_MEETING = 2;
	public static final int TYPE_MY_MEETING = 3;

	private int meetingType;

	public final static String REFRESH_LIST_ACTION = "com.gaopai.guiren.intent.action.REFRESH_LIST_ACTION";

	@Override
	public void addChildView(ViewGroup contentLayout) {

		if (mView == null) {
			mView = mInflater.inflate(R.layout.fragment_meeting, null);
			contentLayout.addView(mView, layoutParamsFF);
			FinalActivity.initInjectedView(this, mView);
			initView();
		} else {
			((ViewGroup) mView.getParent()).removeView(mView);
		}
	}

	private void registerFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(REFRESH_LIST_ACTION);
		filter.addAction(TribeActivity.ACTION_KICK_TRIBE);
		filter.addAction(MainActivity.LOGIN_SUCCESS_ACTION);
		getActivity().registerReceiver(mReceiver, filter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
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
//					mLaunchBtn.setVisibility(View.VISIBLE);
//					if (mCurrentType.equals(MY_MEETING)) {
//						mContainer.clickrefresh();
//					}
				}
			}
		}
	};

	private void initView() {
		addButtonToTitleBar();
		mTitleBar.setTitleTextWithImage(getString(R.string.meeting_title), android.R.drawable.ic_menu_more);

		LayoutInflater layoutInflater = getActivity().getLayoutInflater();
		ViewGroup dropDownView = (ViewGroup) layoutInflater.inflate(R.layout.titlebar_popup_window, null);
		PopupWindowItemClickListener clickListener = new PopupWindowItemClickListener();
		myMeetingBtn = dropDownView.findViewById(R.id.title_popup_text_my_meeting);
		onGoingMeetingBtn = dropDownView.findViewById(R.id.title_popup_text_ongoing_meeting);
		pastMeetingBtn = dropDownView.findViewById(R.id.title_popup_text_past_meeting);
		myMeetingBtn.setOnClickListener(clickListener);
		onGoingMeetingBtn.setOnClickListener(clickListener);
		pastMeetingBtn.setOnClickListener(clickListener);

		mTitleBar.setTitleTextDropDown(dropDownView);

		meetingType = TYPE_ONGOING_MEETING;

		mListView.setPullRefreshEnabled(false); // 下拉刷新
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
	}

	private class PopupWindowItemClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mTitleBar.closeWindow();
			switch (v.getId()) {
			case R.id.title_popup_text_ongoing_meeting:
				meetingType = TYPE_ONGOING_MEETING;
				break;
			case R.id.title_popup_text_my_meeting:
				meetingType = TYPE_MY_MEETING;
				break;
			case R.id.title_popup_text_past_meeting:
				meetingType = TYPE_PAST_MEETING;
				break;
			default:
				break;
			}
			getMeetingList(true, meetingType);
		}

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
		DamiInfo.getMeetingList(meetingType, page, new SimpleResponseListener(getActivity()) {
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
					otherCondition(data.state, getActivity());
				}
			}

			@Override
			public void onFinish() {
				mListView.onPullComplete();
			}
		});
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
		default:
			super.onClick(v);
		}

	}

}
