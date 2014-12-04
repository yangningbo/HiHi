package com.gaopai.guiren.activity;

import java.util.List;

import android.content.BroadcastReceiver;
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
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.adapter.TribeAdapter;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.TribeList;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class TribeActivity extends BaseActivity implements OnClickListener {
	private PullToRefreshListView mListView;
	private TribeAdapter mAdapter;

	public final static String UPDATE_COUNT_ACTION = "com.gaopai.guiren.intent.action.UPDATE_COUNT_ACTION";
	public final static String ACTION_EXIT_TRIBE = "com.gaopai.guiren.intent.action.ACTION_EXIT_TRIBE";
	public final static String ACTION_KICK_TRIBE = "com.gaopai.guiren.intent.action.ACTION_KICK_TRIBE";

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.general_pulltorefresh_listview);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.my_tribe);
		View view = mTitleBar.addRightButtonView(R.drawable.selector_titlebar_add);
		view.setId(R.id.ab_add);
		view.setOnClickListener(this);
		mListView = (PullToRefreshListView) findViewById(R.id.listView);
		mListView.setPullRefreshEnabled(false);
		mListView.setPullLoadEnabled(false);
		mListView.setScrollLoadEnabled(false);

		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				getTribeList();
			}

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub

			}
		});

		mAdapter = new TribeAdapter(mContext);
		mListView.setAdapter(mAdapter);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Tribe tribe = (Tribe) mAdapter.getItem(position);
//				if (tribe.ispwd == 1) {
//					startActivity(TribeVierifyActivity.getIntent(mContext, tribe, 0));
//				} else {
					startActivity(ChatTribeActivity.getIntent(mContext, tribe, ChatTribeActivity.CHAT_TYPE_TRIBE));
//				}
			}
		});

		IntentFilter filter = new IntentFilter();
		filter.addAction(UPDATE_COUNT_ACTION);
		filter.addAction(ACTION_EXIT_TRIBE);
		filter.addAction(ACTION_KICK_TRIBE);
		filter.addAction(MainActivity.LOGIN_SUCCESS_ACTION);
		registerReceiver(mReceiver, filter);

		getTribeList();
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (!TextUtils.isEmpty(action)) {
				if (action.equals(ACTION_EXIT_TRIBE)) {
					String id = intent.getStringExtra("id");
					if (!TextUtils.isEmpty(id)) {
						for (int i = 0; i < mAdapter.list.size(); i++) {
							if (mAdapter.list.get(i).id.equals(id)) {
								mAdapter.list.remove(i);
								mAdapter.notifyDataSetChanged();
								break;
							}
						}
					}

					// mContext.sendBroadcast(new
					// Intent(MainActivity.ACTION_UPDATE_TRIBE_SESSION_COUNT));
				} else if (action.equals(ACTION_KICK_TRIBE)) {
					String id = intent.getStringExtra("id");
					if (!TextUtils.isEmpty(id)) {
						for (int i = 0; i < mAdapter.list.size(); i++) {
							if (mAdapter.list.get(i).id.equals(id)) {
								mAdapter.list.remove(i);
								if (mAdapter != null) {
									mAdapter.notifyDataSetChanged();
								}
								break;
							}
						}
					}

					// mContext.sendBroadcast(new
					// Intent(MainActivity.ACTION_UPDATE_TRIBE_SESSION_COUNT));
				} else if (action.equals(MainActivity.LOGIN_SUCCESS_ACTION)) {
					// mRightBtn.setVisibility(View.VISIBLE);
					// mContainer.clickrefresh();
				}
			}
		}
	};

	private int page = 1;
	private boolean isFull = false;

	private void getTribeList() {
		if (isFull) {
			mListView.setHasMoreData(!isFull);
			return;
		}
		DamiInfo.getTribeList(new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				final TribeList data = (TribeList) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						List<Tribe> tribeList = data.data;
						mAdapter.addAll(tribeList);
					}
				} else {
					otherCondition(data.state, TribeActivity.this);
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
		case R.id.ab_add:
			startActivity(CreatTribeActivity.class);
			break;

		default:
			break;
		}
	}

}
