package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.UserList;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

/**
 * 
 * @author huake 处理各种申请
 */
public class DealApplyActivity extends BaseActivity {
	public static final int TYPE_APPLY_TRIBE = 0;
	public static final int TYPE_APPLY_MEETING = 1;
	public static final int TYPE_APPLY_HOST = 2;
	public static final int TYPE_APPLY_GUEST = 3;
	
	public static final String KEY_TRIBE = "key_tribe";
	public static final String KEY_TYPE = "key_type";

	private PullToRefreshListView mListView;
	private MyAdapter mAdapter;
	private SimpleResponseListener mUserListener;
	private SimpleResponseListener mDealListener;

	private int mType;
	private Tribe mTribe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.general_pulltorefresh_listview);
		mType = getIntent().getIntExtra(KEY_TYPE, 0);
		mTribe = (Tribe) getIntent().getSerializableExtra(KEY_TRIBE);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText("处理申请");

		mListView = (PullToRefreshListView) findViewById(R.id.listView);
		mListView.setPullRefreshEnabled(false);
		mListView.setPullLoadEnabled(false);
		mListView.setScrollLoadEnabled(false);

		mAdapter = new MyAdapter(mContext);
		mListView.setAdapter(mAdapter);
		mUserListener = new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final UserList data = (UserList) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						mAdapter.addAll(data.data);
					}
				} else {
					otherCondition(data.state, DealApplyActivity.this);
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				mListView.onPullComplete();
			}
		};
		mDealListener = new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					showToast("");
				} else {
					otherCondition(data.state, DealApplyActivity.this);
				}
			}
		};
		getUserList();
	}

	private void getUserList() {
		switch (mType) {
		case TYPE_APPLY_TRIBE:
			DamiInfo.getTribeApplyList(mTribe.id, mUserListener);
			break;
		case TYPE_APPLY_MEETING:
			DamiInfo.getMeetingApplyList(mTribe.id, mUserListener);
			break;
		case TYPE_APPLY_GUEST:
			DamiInfo.getGuestApplyList(mTribe.id, mUserListener);
			break;
		case TYPE_APPLY_HOST:
			DamiInfo.getHostgApplyList(mTribe.id, mUserListener);
			break;
		default:
			break;
		}
	}
	
	private void agreeJoin(String uid) {
		switch (mType) {
		case TYPE_APPLY_TRIBE:
			DamiInfo.agreeTribeJoin(mTribe.id, uid, mDealListener);
			break;
		case TYPE_APPLY_MEETING:
			DamiInfo.agreeMeetingJoin(mTribe.id, uid, mDealListener);
			break;
		case TYPE_APPLY_GUEST:
			DamiInfo.agreeGuestApply(mTribe.id, uid, mDealListener);
			break;
		case TYPE_APPLY_HOST:
			DamiInfo.agreeHostApply(mTribe.id, uid,  mUserListener);
			break;
		default:
			break;
		}
	}

	public class MyAdapter extends BaseAdapter {

		private final LayoutInflater mInflater;
		private List<User> list = new ArrayList<User>();

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
				convertView = mInflater.inflate(R.layout.item_general, null);
				holder.mUserNameTextView = (TextView) convertView.findViewById(R.id.tv_title);
				holder.mTimeTextView = (TextView) convertView.findViewById(R.id.tv_date);
				holder.mContentTextView = (TextView) convertView.findViewById(R.id.tv_info);
				holder.mMessageCount = (TextView) convertView.findViewById(R.id.tv_message_count);
				holder.mHeaderView = (ImageView) convertView.findViewById(R.id.iv_header);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			User user = list.get(position);
			if (!TextUtils.isEmpty(user.headsmall)) {
				ImageLoaderUtil.displayImage(user.headsmall, holder.mHeaderView);
			} else {
				holder.mHeaderView.setImageResource(R.drawable.default_tribe);
			}
			holder.mUserNameTextView.setText(user.displayName);
			holder.mTimeTextView.setVisibility(View.GONE);
			holder.mMessageCount.setVisibility(View.GONE);
			return convertView;
		}
	}

	private class ViewHolder {
		TextView mUserNameTextView;
		TextView mContentTextView;
		TextView mTimeTextView;
		TextView mMessageCount;
		ImageView mHeaderView;
	}

}
