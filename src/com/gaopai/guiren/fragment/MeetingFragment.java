package com.gaopai.guiren.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MeetingFragment extends BaseFragment implements OnClickListener {

	private String TAG = MeetingFragment.class.getName();
	@ViewInject(id = R.id.listView)
	private PullToRefreshListView mListView;

	private MeetingAdapter mAdapter;

	public static final int TYPE_ONGOING_MEETING = 0;
	public static final int TYPE_PAST_MEETING = 1;
	public static final int TYPE_MY_MEETING = 2;

	public final static String REFRESH_LIST_ACTION = "com.gaopai.guiren.intent.action.REFRESH_LIST_ACTION";

	private TextView tvOnGoingMeeting;
	private TextView tvPastMeeting;

	private ViewPager viewPager;
	private View pageIndicator;

	private DisplayMetrics displayMetrics;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mView == null) {
			mView = mInflater.inflate(R.layout.fragment_meeting, null);
			viewPager = (ViewPager) mView.findViewById(R.id.vp_meeting);
			initViewPager();
			initView(mView);
		}
		return mView;
	}

	private void initViewPager() {
		meetingFragments.add(getMeetingFragment(TYPE_ONGOING_MEETING));
		meetingFragments.add(getMeetingFragment(TYPE_PAST_MEETING));
		viewPager.setAdapter(new MeetingPagerAdapter(getFragmentManager()));
		viewPager.setOnPageChangeListener(pageChangeListener);
		viewPager.setCurrentItem(0);
	}

	private MeetingListFragment getMeetingFragment(int type) {
		MeetingListFragment fragment = new MeetingListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("type", type);
		fragment.setArguments(bundle);
		fragment.setViewPager(viewPager);
		return fragment;
	}

	@Override
	protected void onReceive(Intent intent) {
		String action = intent.getAction();
		if (!TextUtils.isEmpty(action)) {
			if (action.equals(MainActivity.LOGIN_SUCCESS_ACTION)) {
				meetingFragments.get(0).doPullRefresh();
				viewPager.setCurrentItem(0);
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initView(View mView) {
		pageIndicator = ViewUtil.findViewById(mView, R.id.meeting_page_indicator);
		displayMetrics = getActivity().getResources().getDisplayMetrics();

		tvPastMeeting = ViewUtil.findViewById(mView, R.id.tv_meeting_past);
		tvPastMeeting.setOnClickListener(this);
		tvOnGoingMeeting = ViewUtil.findViewById(mView, R.id.tv_meeting_ongoing);
		tvOnGoingMeeting.setOnClickListener(this);
		tvOnGoingMeeting.post(new Runnable() {
			@Override
			public void run() {
				pageIndicator.setTranslationX(displayMetrics.widthPixels / 4 - pageIndicator.getWidth() / 2);
			}
		});

		registerReceiver(REFRESH_LIST_ACTION, MainActivity.LOGIN_SUCCESS_ACTION);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ab_search: {
			Intent intent = new Intent();
			intent.setClass(act, SearchActivity.class);
			intent.putExtra(SearchActivity.KEY_SEARCH_ORDER, SearchActivity.SEARCH_MEETING);
			startActivity(intent);
			return;
		}
		case R.id.tv_meeting_ongoing:
			viewPager.setCurrentItem(0);
			break;
		case R.id.tv_meeting_past:
			viewPager.setCurrentItem(1);
			break;
		default:
			super.onClick(v);
		}
	}

	private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			int onGoingLeft = (int) (displayMetrics.widthPixels / 4f - pageIndicator.getWidth() / 2f);
			int pastLeft = (int) (displayMetrics.widthPixels * 0.75f - pageIndicator.getWidth() / 2f);
			if (position == 0) {
				pageIndicator.setTranslationX(onGoingLeft + (pastLeft - onGoingLeft) * positionOffset);
			} else {
				pageIndicator.setTranslationX(pastLeft - (pastLeft - onGoingLeft) * positionOffset);
			}
		}

		@Override
		public void onPageSelected(int position) {
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	};

	private class MeetingPagerAdapter extends FragmentPagerAdapter {
		public MeetingPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return meetingFragments.get(position);
		}

		@Override
		public int getCount() {
			return meetingFragments.size();
		}

	}

	private List<MeetingListFragment> meetingFragments = new ArrayList<MeetingListFragment>();

	public static class MeetingListFragment extends Fragment {
		private PullToRefreshListView mListView;
		private int type;
		private int page;
		private boolean isFull;
		private MeetingAdapter mAdapter;
		private boolean isInitial = true;
		private boolean isFirstTime = true;
		private ViewPager viewPager;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.general_pulltorefresh_listview, null);
			mListView = (PullToRefreshListView) view.findViewById(R.id.listView);
			type = getArguments().getInt("type");
			initListView();
			return view;
		}
		
		public void setViewPager(ViewPager viewPager) {
			this.viewPager = viewPager;
		}

		private void initListView() {
			mListView.setPullRefreshEnabled(true); // 下拉刷新
			mListView.setPullLoadEnabled(false);// 上拉刷新，禁止
			mListView.setScrollLoadEnabled(true);// 滑动到底部自动刷新，启用
			mListView.getRefreshableView().setDivider(null);
			mListView.getRefreshableView().setSelector(getActivity().getResources().getDrawable(R.color.transparent));
			mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
				@Override
				public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
					getMeetingList(true, type);
				}

				@Override
				public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
					getMeetingList(false, type);
				}
			});
			mAdapter = new MeetingAdapter(getActivity());
			mListView.setAdapter(mAdapter);

			mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent();
					intent.putExtra(MeetingDetailActivity.KEY_MEETING_ID, ((Tribe) mAdapter.getItem(position)).id);
					intent.setClass(getActivity(), MeetingDetailActivity.class);
					startActivity(intent);

				}
			});
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
			DamiInfo.getMeetingList(DamiCommon.getUid(getActivity()), meetingType, page, new MyResponseListener(
					getActivity()));
		}

		public void doPullRefresh() {
			if (mListView != null) {
				mListView.doPullRefreshing(true, 50);
			}
		}

		@Override
		public void onResume() {
			super.onResume();
			if (mListView != null && isInitial) {
				mListView.doPullRefreshing(true, 50);
				isInitial = false;
			}
		}

		public class MyResponseListener extends SimpleResponseListener {

			public MyResponseListener(Context context) {
				super(context);
			}

			@Override
			public void onSuccess(Object o) {
				final TribeList data = (TribeList) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						mAdapter.addAll(data.data);
					} else if (isFirstTime && type == TYPE_ONGOING_MEETING) {
						isFirstTime = false;
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								if (viewPager != null) {
									viewPager.setCurrentItem(1);
								}
							}
						}, 300);
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
		}
	}
}
