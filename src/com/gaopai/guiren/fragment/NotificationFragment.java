package com.gaopai.guiren.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.gaopai.guiren.BaseFragment;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.NotifySystemActivity;
import com.gaopai.guiren.activity.chat.ChatMessageActivity;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.adapter.NotificationAdapter;
import com.gaopai.guiren.bean.ConversationBean;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.db.ConverseationTable;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;

public class NotificationFragment extends BaseFragment {
	private PullToRefreshListView mListView;
	private NotificationAdapter mAdapter;
	private List<ConversationBean> conversationBeans = new ArrayList<ConversationBean>();
	public final static String ACTION_MSG_NOTIFY = "com.gaopai.guiren.intent.action.ACTION_MSG_NOTIFY";

	@Override
	public void addChildView(ViewGroup contentLayout) {
		if (mView == null) {
			mView = mInflater.inflate(R.layout.general_pulltorefresh_listview, null);
			contentLayout.addView(mView, layoutParamsFF);
			initView();
		} else {
			((ViewGroup) mView.getParent()).removeView(mView);
		}
	}

	private void initView() {
		addButtonToTitleBar();
		mTitleBar.setTitleText(R.string.message);
		mListView = (PullToRefreshListView) mView.findViewById(R.id.listView);
		mListView.setPullLoadEnabled(false);
		mListView.setScrollLoadEnabled(false);
		mListView.setPullRefreshEnabled(false);

		mAdapter = new NotificationAdapter(getActivity());
		mListView.setAdapter(mAdapter);
		mListView.getRefreshableView().setDivider(null);
		mListView.getRefreshableView().setFastScrollEnabled(false);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				
				ConversationBean conversationBean = (ConversationBean) mAdapter.getItem(position);
				resetCount(conversationBean.toid);
				switch (conversationBean.type) {
				case -1:
					startActivity(NotifySystemActivity.class);
					break;
				case 100: {
					Intent intent = new Intent();
					com.gaopai.guiren.bean.User user = new com.gaopai.guiren.bean.User();
					user.uid = conversationBean.toid;
					user.realname = conversationBean.name;
					user.headsmall = conversationBean.headurl;
					intent.putExtra(ChatMessageActivity.KEY_USER, user);
					intent.setClass(getActivity(), ChatMessageActivity.class);
					startActivity(intent);
					break;
				}
				case 200:
				case 300: {
					Intent intent = new Intent();
					Tribe tribe = new Tribe();
					tribe.name = conversationBean.name;
					tribe.id = conversationBean.toid;
					tribe.role = 0;
					intent.setClass(getActivity(), ChatTribeActivity.class);
					intent.putExtra(ChatTribeActivity.KEY_TRIBE, tribe);
					intent.putExtra(ChatTribeActivity.KEY_CHAT_TYPE, conversationBean.type);
					startActivity(intent);
					break;
				}

				default:
					break;
				}
			}
		});
		registerReceiver();
	}
	
	

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getDataFromDb();
	}

	private void getDataFromDb() {
		SQLiteDatabase dbDatabase = DBHelper.getInstance(getActivity()).getWritableDatabase();
		ConverseationTable table = new ConverseationTable(dbDatabase);
		conversationBeans = table.query();
		mAdapter.addAll(conversationBeans);
		mAdapter.notifyDataSetChanged();
	}
	private void resetCount(String id) {
		SQLiteDatabase dbDatabase = DBHelper.getInstance(getActivity()).getWritableDatabase();
		ConverseationTable table = new ConverseationTable(dbDatabase);
		if (table.resetCount(id)) {
			conversationBeans = table.query();
			mAdapter.addAll(conversationBeans);
		}
	}

	private boolean mIsRegisterReceiver = false;

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_MSG_NOTIFY);
		getActivity().registerReceiver(mReceiver, filter);
		mIsRegisterReceiver = true;
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				String action = intent.getAction();
				if (action.equals(ACTION_MSG_NOTIFY)) {
					getDataFromDb();
				}
			}
		}
	};

}
