package com.gaopai.guiren.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ListView;

import com.gaopai.guiren.BaseFragment;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.adapter.ConnectionAdapter;
import com.gaopai.guiren.bean.TribeList;
import com.gaopai.guiren.bean.dynamic.ConnectionBean;
import com.gaopai.guiren.support.DynamicHelper;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

//I don't know how to spell 人脉 in English
public class ConnectionFragment extends BaseFragment implements OnClickListener {
	@ViewInject(id = R.id.listView)
	private PullToRefreshListView mListView;
	private ConnectionAdapter mAdapter;
	private String TAG = DynamicFragment.class.getName();

	@ViewInject(id = R.id.chat_box)
	private View chatBox;
	@ViewInject(id = R.id.chat_box_edit_keyword)
	private EditText etComment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mView == null) {
			mView = mInflater.inflate(R.layout.general_pulltorefresh_listview, null);
			FinalActivity.initInjectedView(this, mView);
			initView();
		}
		return mView;
	}

	private void initView() {
		mListView.setPullLoadEnabled(true);
		mListView.setPullRefreshEnabled(true);
		mListView.setScrollLoadEnabled(false);

		mAdapter = new ConnectionAdapter(this);
		mListView.setAdapter(mAdapter);
		// mListView.doPullRefreshing(true, 0);
		mListView.getRefreshableView().setSelector(getActivity().getResources().getDrawable(R.color.transparent));
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				Log.d(TAG, "pulldown");
				getDynamicList(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				Log.d(TAG, "pull up to");
				getDynamicList(false);
			}
		});
		registerReceiver(MainActivity.LOGIN_SUCCESS_ACTION);
	}

	private boolean isInitialed = false;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (!isInitialed) {
				mListView.doPullRefreshing(true, 0);
				isInitialed = true;
			}
		}
	}

	private int page = 1;
	private boolean isFull = false;

	private void getDynamicList(final boolean isRefresh) {
		if (isRefresh) {
			page = 1;
			isFull = false;
		}
		if (isFull) {
			mListView.setHasMoreData(!isFull);
			return;
		}
		DamiInfo.getRenMainList(page, new SimpleResponseListener(getActivity()) {
			@Override
			public void onSuccess(Object o) {
				final ConnectionBean data = (ConnectionBean) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						if (isRefresh) {
							mAdapter.clear();
						}
						mAdapter.addAll(data.data);
						page++;
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
	protected void onReceive(Intent intent) {
		// TODO Auto-generated method stub
		if (intent != null) {
			String action = intent.getAction();
			if (action.equals(MainActivity.LOGIN_SUCCESS_ACTION)) {
				if (mAdapter != null) {
					mAdapter.clear();
					mAdapter.notifyDataSetChanged();
					isInitialed = false;
				}
			}
		}
	}
}
