package com.gaopai.guiren.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.util.Log;
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
import com.gaopai.guiren.adapter.ConnectionAdapter;
import com.gaopai.guiren.bean.TribeList;
import com.gaopai.guiren.bean.dynamic.ConnectionBean;
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
	public void addChildView(ViewGroup contentLayout) {
		// TODO Auto-generated method stub
		if (mView == null) {
			mView = mInflater.inflate(R.layout.general_pulltorefresh_listview, null);
			contentLayout.addView(mView, layoutParamsFF);
			FinalActivity.initInjectedView(this, mView);
			initView();
		}
	}

	private void initView() {
		addButtonToTitleBar();
		mTitleBar.setTitleText("人脉");

		mListView.setPullLoadEnabled(true);
		mListView.setPullRefreshEnabled(true);
		mListView.setScrollLoadEnabled(false);

		mAdapter = new ConnectionAdapter(this);
		mListView.setAdapter(mAdapter);
		// mListView.doPullRefreshing(true, 0);
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
		getDynamicList(false);
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
}
