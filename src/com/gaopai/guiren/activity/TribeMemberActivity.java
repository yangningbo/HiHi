package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.UserList;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class TribeMemberActivity extends BaseActivity {
	private PullToRefreshListView mListView;
	private MyAdapter mAdapter;

	private String mTribeID = "";
	public static final String KEY_TRIBE_ID = "tribe_id";

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.layout_general_refresh_listview);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTribeID = getIntent().getStringExtra(KEY_TRIBE_ID);
		mTitleBar.setTitleText("圈子成员");
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

		mAdapter = new MyAdapter(mContext);
		mListView.setAdapter(mAdapter);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra(ProfileActivity.KEY_USER_ID, ((User) mAdapter.getItem(position)).uid);
				intent.setClass(mContext, ProfileActivity.class);
				startActivity(intent);
			}
		});
		getTribeList();
	}

	private int page = 1;
	private boolean isFull = false;

	private void getTribeList() {
		if (isFull) {
			mListView.setHasMoreData(!isFull);
			return;
		}
		DamiInfo.getTribeUserList(mTribeID, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				final UserList data = (UserList) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						mAdapter.addAll(data.data);
					}
				} else {
					otherCondition(data.state, TribeMemberActivity.this);
				}
			}

			@Override
			public void onFinish() {
				mListView.onPullComplete();
			}

		});
	}

	public class MyAdapter extends BaseAdapter {

		private final LayoutInflater mInflater;
		public List<User> list = new ArrayList<User>();

		public MyAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public void addAll(List<User> o) {
			list.addAll(o);
			notifyDataSetChanged();
		}

		public void clear() {
			list.clear();
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_tribe_user, null);
				holder.mUserNameTextView = (TextView) convertView.findViewById(R.id.tv_user_name);
				holder.mContentTextView = (TextView) convertView.findViewById(R.id.tv_info);
				holder.mHeaderView = (ImageView) convertView.findViewById(R.id.iv_header);
				holder.btnAction = (Button) convertView.findViewById(R.id.btn_action);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			User user = list.get(position);
			holder.mUserNameTextView.setText(user.realname);
			holder.mContentTextView.setText(user.post);
			if (!TextUtils.isEmpty(user.headsmall)) {
				ImageLoaderUtil.displayImage(user.headsmall, holder.mHeaderView);
			} else {
				holder.mHeaderView.setImageResource(R.drawable.default_header);
			}

			holder.btnAction.setText(getString(R.string.kick_out_tribe));
			holder.btnAction.setTag(user);
			holder.btnAction.setOnClickListener(kickClickListener);
			return convertView;
		}

		private class ViewHolder {
			TextView mUserNameTextView;
			TextView mContentTextView;
			ImageView mHeaderView;

			Button btnAction;
		}

	}

	private OnClickListener kickClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			showDialog(getString(R.string.kick_out_tribe), (User) v.getTag());
		}
	};

	private void showDialog(String title, final User user) {// 0提出部落
		// 1退出部落
		Dialog dialog = new AlertDialog.Builder(mContext).setTitle(title)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						kikOutTribe(user);
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				}).create();
		dialog.show();
	}

	private void kikOutTribe(final User user) {
		DamiInfo.kickTribePerson(mTribeID, user.uid, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					showToast(R.string.operate_success);
				} else {
					otherCondition(data.state, TribeMemberActivity.this);
				}
			}
		});
	}

}