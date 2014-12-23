package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.UserList;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.volley.SimpleResponseListener;

/**
 * 处理参会申请、嘉宾申请等，由会议详情界面或部落详情界面跳转过来
 * 
 */
public class ApplyListActivity extends BaseActivity implements OnClickListener {
	private ListView mListView;
	private List<User> mUserList = new ArrayList<User>();
	private TribePersonAdapter mAdapter;
	private String mTribeID;
	public final static int AGREE_SUCCESS = 4641;
	public final static int REFUSE_SUCCESS = 4642;
	private final static int HIDE_PROGRESS_DIALOG = 15453;
	private int mExcuteCode = 0;
	private int mPosition = -1;
	private int mType = 0;
	private final static int REFUSE_CODE = 1110;

	private SimpleResponseListener listener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.general_listview);
		mContext = this;

		listener = new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				UserList data = (UserList) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data!=null && data.data.size() > 0) {
						mUserList.clear();
						mUserList.addAll(data.data);
						mAdapter.notifyDataSetChanged();
					}
				}
			}
		};
		initComponent();
	}

	private void initComponent() {
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(mContext.getString(R.string.apply_list));

		mTribeID = getIntent().getStringExtra("id");
		mType = getIntent().getIntExtra("type", 0);

		mListView = (ListView) findViewById(R.id.general_list);
		mAdapter = new TribePersonAdapter(mContext, mUserList, false);
		mListView.setAdapter(mAdapter);
		getUserList();
	}


	class MyListener extends SimpleResponseListener {
		private int postion;

		public MyListener(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		public MyListener(Context context, String progressString) {
			super(context, progressString);
		}

		public MyListener(int pos) {
			super(mContext, getString(R.string.request_internet_now));
			postion = pos;
		}

		@Override
		public void onSuccess(Object o) {
			// TODO Auto-generated method stub
			Toast.makeText(mContext, R.string.add_block_success, Toast.LENGTH_LONG).show();
			mUserList.remove(postion);
			mAdapter.notifyDataSetChanged();
		}

	}
	

	private void agreeJoin(final int pos) {
		if (mType == 0) {
			DamiInfo.agreeTribeJoin(mTribeID, mUserList.get(pos).uid, new MyListener(pos));
		} else if (mType == 1) {
			DamiInfo.agreeMeetingJoin(mTribeID, mUserList.get(pos).uid, new MyListener(pos));
		} else if (mType == 2) {
			DamiInfo.agreeHostApply(mTribeID, mUserList.get(pos).uid, new MyListener(pos));
		} else if (mType == 3) {
			DamiInfo.agreeGuestApply(mTribeID, mUserList.get(pos).uid, new MyListener(pos));
		}
	}


	private void getUserList() {
		if (mType == 0) {
			DamiInfo.getTribeApplyList(mTribeID, listener);
		} else if (mType == 1) {
			DamiInfo.getMeetingApplyList(mTribeID, listener);
		} else if (mType == 2) { // 主持人
			DamiInfo.getHostgApplyList(mTribeID, listener);
		} else if (mType == 3) { // 嘉宾
			DamiInfo.getGuestApplyList(mTribeID, listener);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {

		case MainActivity.LOGIN_REQUEST:
			if (resultCode == RESULT_OK) {
			}
			break;
		case REFUSE_CODE:
			if (resultCode == RESULT_OK) {
				String uid = data.getStringExtra("uid");
				if (!TextUtils.isEmpty(uid)) {
					for (int i = 0; i < mUserList.size(); i++) {
						if (mUserList.get(i).uid.equals(uid)) {
							mUserList.remove(i);
							mAdapter.notifyDataSetChanged();
							break;
						}
					}
				}
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	public class TribePersonAdapter extends BaseAdapter {

		private final LayoutInflater mInflater;
		private List<User> mData;
		private Context mContext;
		// private ImageLoader mImageLoader;
		private int mType = 0;

		public TribePersonAdapter(Context context, List<User> data, boolean isShowKickBtn) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mContext = context;
			mData = data;
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_deal_apply, null);
				holder = new ViewHolder();
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_title);
				holder.ivHeader = (ImageView) convertView.findViewById(R.id.iv_header);
				holder.tvContent = ViewUtil.findViewById(convertView, R.id.tv_info);
				holder.btnAgree = ViewUtil.findViewById(convertView, R.id.btn_deal_agree);
				holder.btnRefuse = ViewUtil.findViewById(convertView, R.id.btn_deal_refuse);
				holder.layoutHolder = ViewUtil.findViewById(convertView, R.id.layout_user_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final User user = mData.get(position);

			if (!TextUtils.isEmpty(user.headsmall)) {
				ImageLoaderUtil.displayImage(user.headsmall, holder.ivHeader);
			} else {
				holder.ivHeader.setImageResource(R.drawable.default_header);
			}

			holder.tvName.setText(user.realname);
			String content = "";
			if (!TextUtils.isEmpty(user.company)) {
				content = user.company;
			} else {
				content = user.sign;
			}
			holder.tvContent.setText(content);
			holder.btnAgree.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					agreeJoin(position);
				}
			});
			holder.btnRefuse.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent refuseIntent = new Intent(mContext, AddReasonActivity.class);
					refuseIntent.putExtra("id", mTribeID);
					refuseIntent.putExtra("type", mType);
					refuseIntent.putExtra("refuseType", 1);
					refuseIntent.putExtra("uid", mUserList.get(position).uid);
					startActivityForResult(refuseIntent, REFUSE_CODE);
				}
			});
			holder.layoutHolder.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					startActivity(ProfileActivity.getIntent(mContext, user.uid));
				}
			});
			return convertView;
		}

		

	}
	static class ViewHolder {
		TextView tvName;
		TextView tvContent;
		ImageView ivHeader;
		View layoutHolder;
		Button btnAgree;
		Button btnRefuse;
	}
}
