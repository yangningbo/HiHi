package com.gaopai.guiren.activity.share;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Filter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.chat.ChatMessageActivity;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.activity.share.ShareActivity.CancelInterface;
import com.gaopai.guiren.adapter.CopyOfConnectionAdapter.Item;
import com.gaopai.guiren.adapter.CopyOfConnectionAdapter.Row;
import com.gaopai.guiren.adapter.CopyOfConnectionAdapter.Section;
import com.gaopai.guiren.adapter.ShareContactAdapter;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.UserList;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshIndexableListView;
import com.gaopai.guiren.volley.SimpleResponseListener;
import com.gaopai.guiren.widget.indexlist.SingleIndexScroller;

public abstract class BaseShareFragment extends Fragment implements CancelInterface {
	protected PullToRefreshIndexableListView mListView;
	protected ShareContactAdapter mAdapter;
	protected SingleIndexScroller indexScroller;

	private SimpleResponseListener listener;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_connection, null);
		initView(view);
		
		getUserList(false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		((ShareActivity)getActivity()).setCancelCallback(this);
		listener = new SimpleResponseListener(getActivity(), R.string.request_internet_now) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		};
	}

	private void initView(View mView) {
		// TODO Auto-generated method stub
		indexScroller = (SingleIndexScroller) mView.findViewById(R.id.scroller);
		mListView = (PullToRefreshIndexableListView) mView.findViewById(R.id.listView);

		mListView.getRefreshableView().addHeaderView(creatHeaderView());
		mListView.getRefreshableView().setVerticalScrollBarEnabled(false);
		mListView.setPullRefreshEnabled(false);
		mListView.setPullLoadEnabled(false);
		mListView.setScrollLoadEnabled(false);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				int pos = position - mListView.getRefreshableView().getHeaderViewsCount();
				if (mAdapter.getItem(pos) instanceof Item) {
					User user = ((Item) mAdapter.getItem(pos)).user;
					showDialog(user);
				}
			}
		});

		mAdapter = new ShareContactAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.getRefreshableView().setFastScrollEnabled(false);
		indexScroller.setListView(mListView.getRefreshableView());
	}

	protected View creatHeaderView() {
		return null;
	}



	protected void getUserList(final boolean isRefresh) {
		DamiInfo.getFriendsList(new SimpleResponseListener(getActivity()) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final UserList data = (UserList) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						if (isRefresh) {
							mAdapter.clear();
						}
						mAdapter.addAndSort(data.data);
					}
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

	void showDialog(final User user) {
		showDialog(null, user);
	}

	void showDialog(final Tribe tribe) {
		showDialog(tribe, null);
	}

	private void showDialog(final Tribe tribe, final User user) {
		String title = "确定转发";
		if (getType() != ShareActivity.TYPE_SHARE) {
			title = "确定邀请";
		}
		Dialog dialog = new AlertDialog.Builder(getActivity()).setTitle(title)
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (getType() != ShareActivity.TYPE_SHARE) {
							invite(user, getTribeId());
							return;
						}
						MessageInfo messageInfo = ((ShareActivity) getActivity()).messageInfo;
						Intent i = new Intent();
						i.putExtra(ChatMessageActivity.KEY_MESSAGE, messageInfo);
						if (tribe == null) {
							i.putExtra(ChatMessageActivity.KEY_USER, user);
							i.setClass(getActivity(), ChatMessageActivity.class);
						} else {
							i.putExtra(ChatTribeActivity.KEY_TRIBE, tribe);
							i.setClass(getActivity(), ChatTribeActivity.class);
						}
						getActivity().startActivity(i);
						getActivity().finish();
					}
				}).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).create();
		dialog.show();
	}

	private void invite(User user, String tribeid) {
		switch (getType()) {
		case ShareActivity.TYPE_INVITE_USER:
			DamiInfo.sendMeetingInvite(tribeid, user.uid, listener);
			break;
		case ShareActivity.TYPE_INVITE_HOST:
			DamiInfo.invitemeeting(tribeid, user.uid, 2, listener);
			break;
		case ShareActivity.TYPE_INVITE_GUEST:
			DamiInfo.invitemeeting(tribeid, user.uid, 3, listener);
			break;
		case ShareActivity.TYPE_INVITE_TRIBE:
			DamiInfo.sendInvite(tribeid, user.uid, listener);
			break;
		default:
			break;
		}
	}

	protected int getType() {
		return ((ShareActivity) getActivity()).type;
	}
	protected String getTribeId() {
		return ((ShareActivity) getActivity()).tribeId;
	}

	public String getCheckedId() {
		String result = "";
		StringBuffer sb = new StringBuffer();
		// for (int i = 0, len = checkedList.size(); i < len; i++) {
		// sb.append(mData.get(checkedList.get(i)).uid + ",");
		// }
		if (sb.length() > 0) {
			result = sb.substring(0, sb.length() - 1);
		}
		return result;
	}
	
	@Override
	public void cancel(String s) {
		// TODO Auto-generated method stub
		mAdapter.getFilter().filter(s);
	}
}
