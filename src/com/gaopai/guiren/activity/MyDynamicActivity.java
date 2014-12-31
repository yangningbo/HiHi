package com.gaopai.guiren.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.DynamicAdapter;
import com.gaopai.guiren.bean.dynamic.DynamicBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.fragment.DynamicFragment;
import com.gaopai.guiren.support.DynamicHelper;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class MyDynamicActivity extends BaseActivity {

	@ViewInject(id = R.id.listview)
	private PullToRefreshListView mListView;
	private DynamicAdapter mAdapter;
	private String TAG = DynamicFragment.class.getName();

	private String fid;
	private boolean isMyself;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		fid = getIntent().getStringExtra("uid");
		isMyself = DamiCommon.getUid(mContext).equals(fid);
		initTitleBar();
		setAbContentView(R.layout.fragment_dynamic);
		FinalActivity.initInjectedView(this);
		initView();
	}

	private void initView() {
		if (isMyself) {
			mTitleBar.setTitleText("我的动态");
		} else {
			mTitleBar.setTitleText("TA的动态");
		}
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);

		mListView.setPullLoadEnabled(true);
		mListView.setPullRefreshEnabled(true);
		mListView.setScrollLoadEnabled(false);
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

		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				int pos = position - mListView.getRefreshableView().getHeaderViewsCount();
				mAdapter.viewDynamicDetail((TypeHolder) mAdapter.getItem(pos));
			}
		});

		mAdapter = new DynamicAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.doPullRefreshing(true, 0);
	}

	@Override
	protected void registerReceiver(IntentFilter intentFilter) {
		super.registerReceiver(intentFilter);
		intentFilter.addAction(DynamicHelper.ACTION_REFRESH_DYNAMIC);
	}
	
	public static Intent getIntent(Context context, String fid) {
		Intent intent = new Intent(context, MyDynamicActivity.class);
		intent.putExtra("uid", fid);
		return intent;
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
		Log.d(TAG, "page=" + page);

		DamiInfo.getDynamic(fid, page, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				final DynamicBean data = (DynamicBean) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						if (isRefresh) {
							mAdapter.clear();
						}
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
					otherCondition(data.state, MyDynamicActivity.this);
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
			if (action.equals(DynamicHelper.ACTION_REFRESH_DYNAMIC)) {
				String id = intent.getStringExtra("id");
				mAdapter.deleteItem(id);
			}
		}
	}
}
