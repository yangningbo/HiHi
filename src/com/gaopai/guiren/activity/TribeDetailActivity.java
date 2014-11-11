package com.gaopai.guiren.activity;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.Tribe.Member;
import com.gaopai.guiren.bean.TribeInfoBean;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.view.FlowLayout;
import com.gaopai.guiren.view.MyGridLayout;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class TribeDetailActivity extends BaseActivity implements OnClickListener {
	private String mTribeID = "";
	public static final String KEY_TRIBE_ID = "tribe_id";

	private Tribe mTribe;

	private TextView tvTribeTitle;
	private TextView tvTribeInfo;
	private TextView tvTribeHost;
	private TextView tvTribeGuest;
	private ImageView ivTribeLogo;

	private FlowLayout fyTribeTags;
	private MyGridLayout mlUserLayout;

	private Button btnSpread;
	private Button btnEnter;
	public static final String ACTION_AGREE_ADD_TRIBE = "com.gaopai.guiren.ACTION_AGREE_ADD_TRIBE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mTribeID = getIntent().getStringExtra(KEY_TRIBE_ID);
		if (TextUtils.isEmpty(mTribeID)) {
			Uri data = getIntent().getData();
			mTribeID = data.toString().substring(data.toString().indexOf("//") + 2);
		}
		initTitleBar();
		setAbContentView(R.layout.activity_tribe_detail);

		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText("圈子详情");

		tvTribeTitle = (TextView) findViewById(R.id.tv_tibe_title);
		tvTribeInfo = (TextView) findViewById(R.id.tv_tribe_detail);
		tvTribeHost = (TextView) findViewById(R.id.tv_tribe_host);
//		tvTribeGuest = (TextView) findViewById(R.id.tv_tribe_guest);
		fyTribeTags = (FlowLayout) findViewById(R.id.tribe_tags);

		ivTribeLogo = (ImageView) findViewById(R.id.iv_tribe_logo);
		mlUserLayout = (MyGridLayout) findViewById(R.id.ml_tribe_users);
		mlUserLayout.setOnItemClickListener(gridClickListener);

		btnSpread = (Button) findViewById(R.id.btn_spread);
		btnSpread.setOnClickListener(this);

//		btnEnter = (Button) findViewById(R.id.btn_enter_tribe);
//		btnEnter.setOnClickListener(this);

		IntentFilter filter = new IntentFilter();
		filter.addAction(TribeActivity.ACTION_KICK_TRIBE);
		filter.addAction(ACTION_AGREE_ADD_TRIBE);
		registerReceiver(mReceiver, filter);

		getTribeDetail();
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (!TextUtils.isEmpty(action)) {
				if (action.equals(TribeActivity.ACTION_KICK_TRIBE)) {
					String id = intent.getStringExtra("id");
					if (!TextUtils.isEmpty(id) && id.equals(mTribeID)) {
						getTribeDetail();
					}
				} else if (action.equals(ACTION_AGREE_ADD_TRIBE)) {
					String id = intent.getStringExtra("id");
					if (!TextUtils.isEmpty(id) && id.equals(mTribeID)) {
						getTribeDetail();
					}
				}
			}
		}
	};

	private void getTribeDetail() {
		DamiInfo.getTribeDetail(mTribeID, new SimpleResponseListener(mContext, "正在请求数据") {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				TribeInfoBean data = (TribeInfoBean) o;
				if (data.state != null && data.state.code == 0) {
					mTribe = data.data;
					bindView();
				} else {
					this.otherCondition(data.state, TribeDetailActivity.this);
				}

			}
		});
	}

	private void bindView() {
		// TODO Auto-generated method stub
		tvTribeTitle.setText(mTribe.name);
		tvTribeInfo.setText(mTribe.content);
		tvTribeHost.setText("圈子群主：" + mTribe.realname);
//		tvTribeGuest.setText("嘉宾: " + mTribe.guest);
		ImageLoaderUtil.displayImage(mTribe.logosmall, ivTribeLogo);
		bindMemberView();
	}

	private void bindMemberView() {
		List<Member> members = mTribe.member;
		LayoutInflater inflater = LayoutInflater.from(mContext);

		if (members != null && members.size() > 0) {
			int size = members.size();
			for (int i = 0; i < size; i++) {

				Member member = members.get(i);
				ViewGroup gridView = (ViewGroup) inflater.inflate(R.layout.tribe_grid_item, null);
				UserViewHolder holder = new UserViewHolder();
				holder.tvUserName = (TextView) gridView.findViewById(R.id.tv_user_name);
				holder.ivHeader = (ImageView) gridView.findViewById(R.id.iv_header);
				holder.tvUserName.setText(member.realname);
				ImageLoaderUtil.displayImage(member.headsmall, holder.ivHeader);
				if (i > 6) {
					gridView.removeViewAt(0);
					holder.tvUserName.setText("查看其他" + (size - i) + "人");
					return;
				}
				gridView.setTag(member.uid);
				mlUserLayout.addView(gridView);
			}
		}
	}

	private OnClickListener gridClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String uid = (String) v.getTag();
			Intent intent = new Intent();
			intent.putExtra(UserInfoActivity.KEY_UID, uid);
			intent.setClass(mContext, UserInfoActivity.class);
			startActivity(intent);
		}
	};

	static class UserViewHolder {
		TextView tvUserName;
		ImageView ivHeader;
	}

	private void spreadTribe() {
		DamiInfo.spreadDynamic(4, mTribe.id, "", "", "", "", new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_spread:
			spreadTribe();
			break;
//		case R.id.btn_enter_tribe:
//			Intent intent = new Intent();
//			intent.setClass(mContext, ChatTribeActivity.class);
//			intent.putExtra(ChatTribeActivity.KEY_TRIBE, mTribe);
//			intent.putExtra(ChatTribeActivity.KEY_CHAT_TYPE, ChatTribeActivity.CHAT_TYPE_TRIBE);
//			startActivity(intent);
//			break;

		default:
			break;
		}
	}

}
