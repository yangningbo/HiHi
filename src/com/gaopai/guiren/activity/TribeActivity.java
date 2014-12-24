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
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.chat.ChatBaseActivity;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.adapter.TribeAdapter;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.TribeList;
import com.gaopai.guiren.support.ActionHolder;
import com.gaopai.guiren.utils.Logger;
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

	private String fid;// the id of a user who own this tribe list
	private boolean isMyself;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		fid = getIntent().getStringExtra("fid");
		isMyself = DamiCommon.getUid(mContext).equals(fid);
		initTitleBar();
		setAbContentView(R.layout.general_pulltorefresh_listview);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		if (isMyself) {
			mTitleBar.setTitleText(R.string.my_tribe);
		} else {
			mTitleBar.setTitleText(R.string.his_tribe);
		}
		View view = mTitleBar.addRightButtonView(R.drawable.selector_titlebar_add);
		view.setId(R.id.ab_add);
		view.setOnClickListener(this);
		mListView = (PullToRefreshListView) findViewById(R.id.listView);
		mListView.setPullRefreshEnabled(true);
		mListView.setPullLoadEnabled(false);
		mListView.setScrollLoadEnabled(false);

		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				getTribeList(false);
			}

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getTribeList(true);
			}
		});

		mAdapter = new TribeAdapter(mContext);
		mListView.setAdapter(mAdapter);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Tribe tribe = (Tribe) mAdapter.getItem(position);
				if (!isMyself) {
					startActivity(TribeDetailActivity.getIntent(mContext, tribe.id));
					return;
				}
				if (tribe.isjoin == 1) {
					TribeActivity.this.startActivityForResult(
							ChatTribeActivity.getIntent(mContext, tribe, ChatTribeActivity.CHAT_TYPE_TRIBE), 1);
					return;
				}
				if (tribe.ispwd == 1) {
					startActivity(TribeVierifyActivity.getIntent(mContext, tribe, 0));
				}
			}
		});

		mListView.doPullRefreshing(true, 0);

	}

	public static Intent getIntent(Context context, String fid) {
		Intent intent = new Intent(context, TribeActivity.class);
		intent.putExtra("fid", fid);
		return intent;
	}

	
	@Override
	protected void registerReceiver(IntentFilter filter) {
		filter.addAction(UPDATE_COUNT_ACTION);
		filter.addAction(ACTION_EXIT_TRIBE);
		filter.addAction(ACTION_KICK_TRIBE);
		filter.addAction(MainActivity.LOGIN_SUCCESS_ACTION);
		filter.addAction(ActionHolder.ACTION_CANCEL_TRIBE);
		filter.addAction(ActionHolder.ACTION_QUIT_TRIBE);
		super.registerReceiver(filter);
	}

	@Override
	protected void onReceive(Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(intent);
		String action = intent.getAction();
		if (TextUtils.isEmpty(action)) {
			return;
		}
		if (action.equals(ACTION_EXIT_TRIBE)) {
			String id = intent.getStringExtra("id");
			if (!TextUtils.isEmpty(id)) {
				removeItem(id);
			}
		} else if (action.equals(ACTION_KICK_TRIBE)) {
			String id = intent.getStringExtra("id");
			if (!TextUtils.isEmpty(id)) {
				removeItem(id);
			}
		} else if (action.equals(MainActivity.LOGIN_SUCCESS_ACTION)) {
		} else if (action.equals(ActionHolder.ACTION_CANCEL_TRIBE) || action.equals(ActionHolder.ACTION_QUIT_TRIBE)) {
			String id = intent.getStringExtra("tid");
			if (!TextUtils.isEmpty(id)) {
				removeItem(id);
			}
		}
	}

	private void removeItem(String id) {
		for (int i = 0; i < mAdapter.list.size(); i++) {
			if (mAdapter.list.get(i).id.equals(id)) {
				mAdapter.list.remove(i);
				mAdapter.notifyDataSetChanged();
				break;
			}
		}
	}

	private int page = 1;
	private boolean isFull = false;

	private void getTribeList(boolean isRefresh) {
		if (isRefresh) {
			page = 1;
			mAdapter.clear();
			isFull = false;
		}
		if (isFull) {
			mListView.setHasMoreData(!isFull);
			return;
		}
		DamiInfo.getTribeList(fid, page, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				final TribeList data = (TribeList) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						List<Tribe> tribeList = data.data;
						mAdapter.addAll(tribeList);
					}
					if (data.pageInfo != null) {
						isFull = (data.pageInfo.hasMore == 0);
						if (!isFull) {
							page++;
						}
					}
					mListView.setHasMoreData(!isFull);
				} else {
					otherCondition(data.state, TribeActivity.this);
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
			startActivity(CreatTribeActivity.class);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int request, int result, Intent arg2) {
		Logger.d(this, result + "  ==" + request);
		if (result == TribeDetailActivity.RESULT_CANCEL_TRIBE) {
			mListView.doPullRefreshing(true, 0);
		}
	}
}
