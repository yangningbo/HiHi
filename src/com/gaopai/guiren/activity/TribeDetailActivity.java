package com.gaopai.guiren.activity;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.share.ShareActivity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.Tribe.Member;
import com.gaopai.guiren.bean.TribeInfoBean;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.support.MessageHelper;
import com.gaopai.guiren.support.MessageHelper.DeleteCallback;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.view.FlowLayout;
import com.gaopai.guiren.view.MyGridLayout;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class TribeDetailActivity extends BaseActivity implements OnClickListener {
	private String mTribeID = "";
	public static final String KEY_TRIBE_ID = "tribe_id";

	private boolean isCreator;

	private Tribe mTribe;

	private TextView tvTribeTitle;
	private TextView tvTribeInfo;
	private TextView tvTribeHost;
	private ImageView ivTribeLogo;

	private MyGridLayout mlUserLayout;

	private Button btnSpread;
	private Button btnOnLook;
	private Button btnApplyJoin;
	private Button btnExitTribe;
	public static final String ACTION_AGREE_ADD_TRIBE = "com.gaopai.guiren.ACTION_AGREE_ADD_TRIBE";

	private TextView tvAvoidDisturb;
	private TextView tvClearCache;
	private TextView tvUseRealIdentity;

	private TextView tvDealApply;
	private TextView tvDealJubaoApply;
	private TextView tvChangeTribe;
	private TextView tvCancelTribe;

	private FlowLayout layoutTags;
	private TextView tvMoreTags;

	private View layoutAdmin;
	private View layoutSetting;

	public final static int REQUEST_JOIN_TRIBE = 0;
	public final static int REQUEST_ALL_TRIBE_USERS = 1;

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
		mTitleBar.setTitleText(R.string.tribe_detail);

		tvTribeTitle = (TextView) findViewById(R.id.tv_tibe_title);
		tvTribeInfo = (TextView) findViewById(R.id.tv_tribe_detail);
		tvTribeHost = (TextView) findViewById(R.id.tv_tribe_host);

		ivTribeLogo = (ImageView) findViewById(R.id.iv_tribe_logo);
		mlUserLayout = (MyGridLayout) findViewById(R.id.ml_tribe_users);

		btnSpread = (Button) findViewById(R.id.btn_spread);
		btnSpread.setOnClickListener(this);
		btnOnLook = (Button) findViewById(R.id.btn_on_look);
		btnOnLook.setOnClickListener(this);
		btnApplyJoin = (Button) findViewById(R.id.btn_want_in_tribe);
		btnApplyJoin.setOnClickListener(this);
		btnExitTribe = (Button) findViewById(R.id.btn_exit_tribe);
		btnExitTribe.setOnClickListener(this);

		tvAvoidDisturb = (TextView) findViewById(R.id.tv_avoid_disturb);
		tvAvoidDisturb.setOnClickListener(this);
		tvClearCache = (TextView) findViewById(R.id.tv_clear_cache);
		tvClearCache.setOnClickListener(this);
		tvUseRealIdentity = (TextView) findViewById(R.id.tv_user_real_identity);
		tvUseRealIdentity.setOnClickListener(this);

		tvDealApply = (TextView) findViewById(R.id.tv_deal_apply);
		tvDealApply.setOnClickListener(this);
		tvDealJubaoApply = (TextView) findViewById(R.id.tv_deal_jubao_apply);
		tvDealJubaoApply.setOnClickListener(this);
		tvChangeTribe = (TextView) findViewById(R.id.tv_change_tribe);
		tvChangeTribe.setOnClickListener(this);
		tvCancelTribe = (TextView) findViewById(R.id.tv_cancel_tribe);
		tvCancelTribe.setOnClickListener(this);

		layoutTags = (FlowLayout) findViewById(R.id.tribe_tags);
		tvMoreTags = (TextView) findViewById(R.id.tv_more_tags);
		tvMoreTags.setOnClickListener(this);

		layoutAdmin = findViewById(R.id.layout_tribe_detail_admin);
		layoutSetting = findViewById(R.id.layout_tribe_detail_setting);

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
		DamiInfo.getTribeDetail(mTribeID, new SimpleResponseListener(mContext, R.string.request_internet_now) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				TribeInfoBean data = (TribeInfoBean) o;
				if (data.state != null && data.state.code == 0) {
					mTribe = data.data;
					isCreator = mTribe.uid.equals(DamiCommon.getUid(mContext));
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
		tvTribeHost.setText(mTribe.realname);

		changeSwitch(tvAvoidDisturb, mTribe.getmsg == 1);

		ImageLoaderUtil.displayImage(mTribe.logosmall, ivTribeLogo);
		bindBottomButtons();
		bindMemberView();
	}

	private void bindBottomButtons() {
		if (isCreator) {
			btnExitTribe.setVisibility(View.GONE);
			btnApplyJoin.setVisibility(View.GONE);
			layoutAdmin.setVisibility(View.VISIBLE);
			btnOnLook.setText(getString(R.string.invite));
		} else {
			layoutAdmin.setVisibility(View.GONE);
			if (mTribe.isjoin == 1) {
				layoutSetting.setVisibility(View.VISIBLE);
				btnOnLook.setText(getString(R.string.invite));
				btnApplyJoin.setVisibility(View.GONE);
				btnExitTribe.setVisibility(View.VISIBLE);
			} else {
				layoutSetting.setVisibility(View.GONE);
				btnOnLook.setText(getString(R.string.onlooker));
				btnApplyJoin.setVisibility(View.VISIBLE);
				btnExitTribe.setVisibility(View.GONE);
			}
		}
	}

	private void bindMemberView() {
		List<Member> members = mTribe.member;
		LayoutInflater inflater = LayoutInflater.from(mContext);

		if (members != null && members.size() > 0) {
			int size = members.size();
			for (int i = 0; i < size + 1; i++) {

				ViewGroup gridView = (ViewGroup) inflater.inflate(R.layout.tribe_grid_item, null);
				UserViewHolder holder = new UserViewHolder();
				holder.tvUserName = (TextView) gridView.findViewById(R.id.tv_user_name);
				holder.ivHeader = (ImageView) gridView.findViewById(R.id.iv_header);
				if (i == size) {
					gridView.removeViewAt(0);
					holder.tvUserName.setText("查看其他" + (size - i) + "人");
					gridView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(TribeDetailActivity.this, TribeMemberActivity.class);
							intent.putExtra(TribeMemberActivity.KEY_TRIBE_ID, mTribeID);
							startActivityForResult(intent, REQUEST_ALL_TRIBE_USERS);
						}
					});
					mlUserLayout.addView(gridView);
					return;
				}

				gridView.setOnClickListener(gridClickListener);
				gridView.setOnLongClickListener(gridLongClickListener);
				Member member = members.get(i);
				holder.tvUserName.setText(member.realname);
				ImageLoaderUtil.displayImage(member.headsmall, holder.ivHeader);
				gridView.setTag(member);
				mlUserLayout.addView(gridView);
			}
		}
	}

	private OnLongClickListener gridLongClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			showDialog(getString(R.string.kick_out_tribe), 0, (Member) v.getTag());
			return false;
		}
	};

	private OnClickListener gridClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String uid = ((Member) v.getTag()).uid;
			Intent intent = new Intent();
			intent.putExtra(ProfileActivity.KEY_USER_ID, uid);
			intent.setClass(mContext, ProfileActivity.class);
			startActivity(intent);
		}
	};

	private void showDialog(String title, final int type, final Member member) {// 0提出部落
																				// 1退出部落
		Dialog dialog = new AlertDialog.Builder(mContext).setTitle(title)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (type == 0) {
							kikOutTribe(member);
						} else {
							exitTribe();
						}
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

	static class UserViewHolder {
		TextView tvUserName;
		ImageView ivHeader;
	}

	private void spreadTribe() {
		DamiInfo.spreadDynamic(4, mTribe.id, "", "", "", "", new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					showToast(R.string.spread_success);
				} else {
					otherCondition(data.state, TribeDetailActivity.this);
				}
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
		// case R.id.btn_enter_tribe:
		// Intent intent = new Intent();
		// intent.setClass(mContext, ChatTribeActivity.class);
		// intent.putExtra(ChatTribeActivity.KEY_TRIBE, mTribe);
		// intent.putExtra(ChatTribeActivity.KEY_CHAT_TYPE,
		// ChatTribeActivity.CHAT_TYPE_TRIBE);
		// startActivity(intent);
		// break;
		case R.id.btn_on_look:
			if (mTribe.isjoin == 1) {
				invite();
			}
			break;
		case R.id.btn_want_in_tribe:
			applyJoinTribe();
			break;
		case R.id.btn_exit_tribe:
			showDialog(getString(R.string.exit_tribe), 1, null);
			break;
		case R.id.tv_avoid_disturb:
			avoidDisturb();
			break;
		case R.id.tv_clear_cache:
			MessageHelper.clearChatCache(mContext, mTribeID, 200, deleteCallback);
			break;
		case R.id.tv_user_real_identity:
			changeSwitch(tvUseRealIdentity, true);
			break;
		case R.id.tv_deal_apply: {
			Intent dealapplyIntent = new Intent(mContext, ApplyListActivity.class);
			dealapplyIntent.putExtra("id", mTribeID);
			startActivity(dealapplyIntent);
			break;
		}
		case R.id.tv_deal_jubao_apply: {
			Intent reportIntent = new Intent(mContext, ReportMsgActivity.class);
			reportIntent.putExtra(ReportMsgActivity.KEY_TID, mTribeID);
			reportIntent.putExtra(ReportMsgActivity.KEY_TYPE, ReportMsgActivity.TYPE_TRIBE);
			startActivity(reportIntent);
			break;
		}
		case R.id.tv_change_tribe: {
			Intent intent = new Intent(mContext, CreatTribeActivity.class);
			intent.putExtra(CreatTribeActivity.KEY_TRIBE, mTribe);
			startActivity(intent);
			break;
		}
		case R.id.tv_more_tags:
			break;
		default:
			break;
		}
	}
	
	private DeleteCallback deleteCallback = new DeleteCallback() {

		@Override
		public void onStart() {
			showProgressDialog(R.string.clear_cache_now);
		}

		@Override
		public void onEnd() {
			removeProgressDialog();
			showToast(R.string.clear_cache_success);
		}
	};

	private void invite() {
		Intent intent = new Intent();
		intent.setClass(mContext, ShareActivity.class);
		intent.putExtra(ShareActivity.KEY_TRIBE_ID, mTribeID);
		intent.putExtra(ShareActivity.KEY_TYPE, ShareActivity.TYPE_INVITE_TRIBE);
		startActivity(intent);
	}

	private void applyJoinTribe() {
		Intent intent = new Intent();
		intent.setClass(mContext, AddReasonActivity.class);
		intent.putExtra(AddReasonActivity.KEY_MEETING_ID, mTribeID);
		intent.putExtra(AddReasonActivity.KEY_APLLY_TYPE, AddReasonActivity.TYPE_TO_JOIN_TRIBE);
		startActivityForResult(intent, REQUEST_JOIN_TRIBE);
	}

	private void exitTribe() {
		DamiInfo.exitTribe(mTribeID, new SimpleResponseListener(mContext, R.string.request_internet_now) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					showToast(R.string.operate_success);
					bindBottomButtons();
				} else {
					otherCondition(data.state, TribeDetailActivity.this);
				}
			}
		});
	}

	private void kikOutTribe(final Member member) {
		DamiInfo.kickTribePerson(mTribeID, member.uid, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					showToast(R.string.operate_success);
					mTribe.member.remove(member);
				} else {
					otherCondition(data.state, TribeDetailActivity.this);
				}
			}
		});
	}

	private void avoidDisturb() {
		final String type = mTribe.getmsg == 1 ? "2" : "1";
		DamiInfo.setMsgType(mTribeID, type, new SimpleResponseListener(mContext, R.string.request_internet_now) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					showToast(R.string.operate_success);
					mTribe.getmsg = Integer.parseInt(type);
					changeSwitch(tvAvoidDisturb, mTribe.getmsg == 1);
				}
			}
		});
	}

	private void changeSwitch(TextView textView, boolean isOn) {
		if (isOn) {
			textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_switch_active, 0);
		} else {
			textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_switch_normal, 0);
		}
	}

	@Override
	protected void onActivityResult(int request, int result, Intent intent) {
		// TODO Auto-generated method stub
		if (result == RESULT_OK) {
			if (request == REQUEST_ALL_TRIBE_USERS) {
				getTribeDetail();
			}
		}
	}

}
