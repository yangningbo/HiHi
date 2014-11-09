package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.TribePersonAdapter;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.UserList;
import com.gaopai.guiren.volley.SimpleResponseListener;

/**
 * 处理参会申请、嘉宾申请等，由会议详情界面或部落详情界面跳转过来
 * 
 */
public class ApplyListActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	private ListView mListView;
	private List<User> mUserList = new ArrayList<User>();
	private TribePersonAdapter mAdapter;
	private String mTribeID;
	private TextView mNoDataView;
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
		setAbContentView(R.layout.industry_page);
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

		mListView = (ListView) findViewById(R.id.industry_list);
		mListView.setCacheColorHint(0);
		mListView.setDivider(null);
		mListView.setOnItemClickListener(this);

		mNoDataView = (TextView) findViewById(R.id.no_data);
		mNoDataView.setText(mContext.getString(R.string.no_more_data));
		
		mAdapter = new TribePersonAdapter(mContext, mUserList, false);
		mListView.setAdapter(mAdapter);
		getUserList();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (mUserList.get(arg2).mProcessType == 0) {
			showDealDialog(arg2);
		}
	}

	private void showDealDialog(final int pos) {

		final Dialog dlg = new Dialog(mContext, R.style.MMThem_DataSheet);
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.chat_deal_menu_dialog, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);

		final Button agreeBtn = (Button) layout.findViewById(R.id.sendType);
		final Button refuseBtn = (Button) layout.findViewById(R.id.camera);
		final Button viewInfoBtn = (Button) layout.findViewById(R.id.gallery);
		final Button cancelBtn = (Button) layout.findViewById(R.id.cancelbtn);

		agreeBtn.setText(mContext.getString(R.string.agree));
		refuseBtn.setText(mContext.getString(R.string.refuse));
		viewInfoBtn.setText(mContext.getString(R.string.view_user_info));
		cancelBtn.setText(mContext.getString(R.string.cancel));

		agreeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				agreeJoin(pos);
			}
		});

		refuseBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				Intent refuseIntent = new Intent(mContext, AddReasonActivity.class);
				refuseIntent.putExtra("id", mTribeID);
				refuseIntent.putExtra("type", mType);
				refuseIntent.putExtra("refuseType", 1);
				refuseIntent.putExtra("uid", mUserList.get(pos).uid);
				startActivityForResult(refuseIntent, REFUSE_CODE);
			}
		});

		viewInfoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				Intent intent = new Intent(mContext, UserInfoActivity.class);
				intent.putExtra(UserInfoActivity.KEY_UID, mUserList.get(pos).uid);
				startActivity(intent);
			}
		});

		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});

		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setCancelable(true);

		dlg.setContentView(layout);
		dlg.show();
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


	private void updateListView() {
		if (mUserList != null && mUserList.size() != 0) {
			mListView.setVisibility(View.VISIBLE);
			mNoDataView.setVisibility(View.GONE);
			mAdapter = new TribePersonAdapter(mContext, mUserList, false);
			mListView.setAdapter(mAdapter);
		} else {
			mListView.setVisibility(View.GONE);
			mNoDataView.setVisibility(View.VISIBLE);
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

}
