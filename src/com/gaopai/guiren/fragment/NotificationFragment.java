package com.gaopai.guiren.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.gaopai.guiren.BaseFragment;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.activity.NotifySystemActivity;
import com.gaopai.guiren.activity.WebActivity;
import com.gaopai.guiren.activity.chat.ChatMessageActivity;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.adapter.NotificationAdapter;
import com.gaopai.guiren.bean.ConversationBean;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.db.ConverseationTable;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.support.ConversationHelper;
import com.gaopai.guiren.support.NotifyHelper;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.PreferenceOperateUtils;
import com.gaopai.guiren.utils.SPConst;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;

public class NotificationFragment extends BaseFragment {
	private PullToRefreshListView mListView;
	private NotificationAdapter mAdapter;
	private List<ConversationBean> conversationBeans = new ArrayList<ConversationBean>();
	public final static String ACTION_MSG_NOTIFY = "com.gaopai.guiren.intent.action.ACTION_MSG_NOTIFY";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mView == null) {
			mView = mInflater.inflate(R.layout.general_pulltorefresh_listview, null);
			initView();
		}
		return mView;
	}

	private void initView() {
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
				if (position == 0) {
					Intent intent = new Intent(getActivity(), WebActivity.class);
					intent.putExtra(WebActivity.KEY_URL, getString(R.string.share_dami_url));
					intent.putExtra(WebActivity.KEY_TITLE, getString(R.string.dige));
					startActivity(intent);
					return;
				}
				ConversationBean conversationBean = (ConversationBean) mAdapter.getItem(position);
				// resetCount(conversationBean.toid);
				switch (conversationBean.type) {
				case -1:
					startActivity(NotifySystemActivity.class);
					break;
				case 100: {
					startActivity(ChatMessageActivity.getIntent(act, conversationBean.toid, conversationBean.name,
							conversationBean.headurl));
					break;
				}
				case 200:
				case 300: {
					Intent intent = new Intent();
					Tribe tribe = new Tribe();
					tribe.name = conversationBean.name;
					tribe.id = conversationBean.toid;
					tribe.role = -1;
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
		mListView.getRefreshableView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final ConversationBean conversationBean = (ConversationBean) mAdapter.getItem(position);
				// TODO Auto-generated method stub
				getBaseActivity().showMutiDialog("", new String[] { getString(R.string.delete) },
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ConversationHelper.deleteItem(act, conversationBean.toid);
								getDataFromDb();
							}
						});
				return true;
			}
		});
		registerReceiver(ACTION_MSG_NOTIFY, MainActivity.LOGIN_SUCCESS_ACTION);
	}

	private boolean isInitialed = false;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (!isInitialed) {
				getDataFromDb();
				isInitialed = true;
			}
		}
	}

	private void getDataFromDb() {
		SQLiteDatabase dbDatabase = DBHelper.getInstance(getActivity()).getWritableDatabase();
		ConverseationTable table = new ConverseationTable(dbDatabase);
		conversationBeans = table.query();
		mAdapter.addAll(conversationBeans, initialData(conversationBeans));
	}

	// if contains dami
	private boolean initialData(List<ConversationBean> conversationBeans) {
		int count = 0;
		int length = conversationBeans.size();
		int damiPosition = -1;
		for (int i = 0; i < length; i++) {
			ConversationBean conversationBean = conversationBeans.get(i);
			count += conversationBean.unreadcount;
			if (conversationBean.toid.equals("-2")) {
				damiPosition = i;
			}
		}
		if (damiPosition > 0) {
			conversationBeans.add(0, conversationBeans.remove(damiPosition));
		}
		setNotification(count);
		return damiPosition >= 0;
	}

	private void setNotification(int count) {
		PreferenceOperateUtils spo = new PreferenceOperateUtils(getActivity());
		spo.setBoolean(SPConst.KEY_HAS_NOTIFICATION, count > 0);
		showNotificationDot();
	}

	private void showNotificationDot() {
		PreferenceOperateUtils operateUtils = new PreferenceOperateUtils(getActivity());
		boolean hasNotification = operateUtils.getBoolean(SPConst.KEY_HAS_NOTIFICATION, false);
		View viewDot = ViewUtil.findViewById(getActivity(), R.id.iv_count_4);
		if (hasNotification) {
			viewDot.setVisibility(View.VISIBLE);
		} else {
			viewDot.setVisibility(View.GONE);
		}
	}

	// private void resetCount(String id) {
	// SQLiteDatabase dbDatabase =
	// DBHelper.getInstance(getActivity()).getWritableDatabase();
	// ConverseationTable table = new ConverseationTable(dbDatabase);
	// if (table.resetCount(id)) {
	// conversationBeans = table.query();
	// setNotificationSp(conversationBeans);
	// mAdapter.addAll(conversationBeans);
	// }
	// }

	@Override
	protected void onReceive(Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(intent);
		if (intent != null) {
			String action = intent.getAction();
			if (action.equals(ACTION_MSG_NOTIFY)) {
				Logger.d(this, "receive notify...");
				getDataFromDb();
			} else if (action.equals(MainActivity.LOGIN_SUCCESS_ACTION)) {
				if (mAdapter != null) {
					mAdapter.clear();
					isInitialed = false;
				}
			}
		}
	}

}
