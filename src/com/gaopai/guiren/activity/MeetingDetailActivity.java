package com.gaopai.guiren.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.activity.share.ShareActivity;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.TribeInfoBean;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.bean.net.SimpleStateBean;
import com.gaopai.guiren.support.MessageHelper;
import com.gaopai.guiren.support.MessageHelper.DeleteCallback;
import com.gaopai.guiren.support.ShareManager;
import com.gaopai.guiren.support.ShareManager.CallDyback;
import com.gaopai.guiren.support.alarm.AlarmReceiver;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.PreferenceOperateUtils;
import com.gaopai.guiren.utils.SPConst;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class MeetingDetailActivity extends BaseActivity implements OnClickListener {

	private ViewGroup chatGridLayout;
	private Button btnSetting;
	private Button btnHideGrid;
	private Button btnEnterMeeting;

	private Button btnOnLook;
	private Button btnJoinMeeting;

	private View viewEnterMeeting;
	private TextView tvMeetingTitle;
	private TextView tvMeetingTime;
	private TextView tvMeetingTimeDiff;

	private TextView tvMeetingInfo;
	private TextView tvMeetingHost;
	private TextView tvMeetingGuest;
	private TextView tvMeetingJoinIn;
	private ImageView ivMeetingHeader;

	private View viewNotJoinIn;
	private View viewJoinIn;

	private View layoutBottom;

	public static final String KEY_MEETING_ID = "meeting_id";
	public static final String KEY_MEETING = "meeting";
	private String mMeetingID = "";// pass tribe entity only in preview mode
	private Tribe mMeeting;
	private boolean isPreview = false;

	public final static int REQUEST_NORMAL = 0;
	public final static int REQUEST_BACK_TO_NORMAL = 1;
	public final static int REQUEST_CANCEL_MEETING = 2;

	public static final String ACTION_AGREE_ADD_MEETING = "com.gaopai.guiren.ACTION_AGREE_ADD_MEETING";
	public static final String ACTION_MEETING_CANCEL = "com.gaopai.guiren.ACTION_MEETING_CANCEL";

	private PreferenceOperateUtils spo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_meeting_detail);
		spo = new PreferenceOperateUtils(mContext, SPConst.SP_AVOID_DISTURB);

		mMeetingID = getIntent().getStringExtra(KEY_MEETING_ID);
		mMeeting = (Tribe) getIntent().getSerializableExtra(KEY_MEETING);
		if (mMeeting != null) {
			isPreview = true;
		} else {
			if (TextUtils.isEmpty(mMeetingID)) {
				Uri data = getIntent().getData();
				mMeetingID = data.toString().substring(data.toString().indexOf("//") + 2);
			}
		}

		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(getString(R.string.meeting_detail_title));
		View view = mTitleBar.addRightButtonView(R.drawable.selector_titlebar_share);
		view.setId(R.id.ab_share);
		view.setOnClickListener(this);
		initComponent();
		if (isPreview) {
			bindBasicView();
			return;
		}
		getMeetingDetail();
		IntentFilter filter = new IntentFilter();
		filter.addAction(TribeActivity.ACTION_KICK_TRIBE);
		filter.addAction(ACTION_AGREE_ADD_MEETING);
		filter.addAction(ACTION_MEETING_CANCEL);
		registerReceiver(mReceiver, filter);
	}

	private void initComponent() {
		tvMeetingTitle = (TextView) findViewById(R.id.tv_meeting_title);
		tvMeetingTime = (TextView) findViewById(R.id.tv_meeting_time);
		tvMeetingTimeDiff = (TextView) findViewById(R.id.tv_meeting_time_difference);
		tvMeetingTitle = (TextView) findViewById(R.id.tv_meeting_title);
		tvMeetingInfo = (TextView) findViewById(R.id.tv_meeting_detail);
		tvMeetingHost = (TextView) findViewById(R.id.tv_meeting_host);
		tvMeetingGuest = (TextView) findViewById(R.id.tv_meeting_guest);
		tvMeetingJoinIn = (TextView) findViewById(R.id.tv_meeting_join_in);

		ivMeetingHeader = (ImageView) findViewById(R.id.iv_meeeting_header);

		btnEnterMeeting = (Button) findViewById(R.id.btn_enter_meeting);

		btnOnLook = (Button) findViewById(R.id.btn_on_look);
		btnJoinMeeting = (Button) findViewById(R.id.btn_want_in_meeting);

		btnSetting = (Button) findViewById(R.id.btn_more);
		viewNotJoinIn = findViewById(R.id.bottom_not_in_meeting);
		viewJoinIn = findViewById(R.id.bottom_in_meeting);

		layoutBottom = findViewById(R.id.layout_meeting_setting);
		layoutBottom.setVisibility(isPreview ? View.GONE : View.VISIBLE);
		if (!isPreview) {
			tvMeetingHost.setOnClickListener(this);
			tvMeetingGuest.setOnClickListener(this);
			tvMeetingJoinIn.setOnClickListener(this);
			btnEnterMeeting.setOnClickListener(this);
			btnOnLook.setOnClickListener(this);
			btnJoinMeeting.setOnClickListener(this);
			btnSetting.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showMoreWindow(mMeeting.role);
				}
			});
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (!TextUtils.isEmpty(action)) {
				if (action.equals(TribeActivity.ACTION_KICK_TRIBE)) {
					String id = intent.getStringExtra("id");
					if (!TextUtils.isEmpty(id) && id.equals(mMeetingID)) {
						/*
						 * mMeeting.isjoin = 0;
						 * mApplyBtn.setVisibility(View.VISIBLE);
						 * mExitBtn.setVisibility(View.GONE);
						 */
						getMeetingDetail();
					}
				} else if (action.equals(ACTION_AGREE_ADD_MEETING)) {
					String id = intent.getStringExtra("id");
					if (!TextUtils.isEmpty(id) && id.equals(mMeetingID)) {
						getMeetingDetail();
					}
				} else if (action.equals(ACTION_MEETING_CANCEL)) {
					finish();
					showToast(mContext.getString(R.string.meeting_cancel));
				}
			}
		}
	};

	private void getMeetingDetail() {
		DamiInfo.getMeetingDetail(mMeetingID, new SimpleResponseListener(mContext,
				getString(R.string.request_internet_now)) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final TribeInfoBean data = (TribeInfoBean) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null) {
						mMeeting = data.data;
						bindView();
					}
				} else {
					otherCondition(data.state, MeetingDetailActivity.this);
				}
			}
		});

	}

	private void bindView() {
		// TODO Auto-generated method stub
		bindBasicView();
		tvMeetingHost.setText(mMeeting.hosts);
		tvMeetingGuest.setText(mMeeting.guest);
		// tvMeetingInfo.setText(mMeeting.j)

		bindJoinInView(mMeeting.isjoin == 1);
	}

	private void bindBasicView() {
		tvMeetingTitle.setText(mMeeting.name);
		tvMeetingInfo.setText(mMeeting.content);
		tvMeetingTime.setText(FeatureFunction.getTime(mMeeting.start) + "~" + FeatureFunction.getTime(mMeeting.end));
		tvMeetingTimeDiff.setText(FeatureFunction.timeDifference(mMeeting.start));
		ImageLoaderUtil.displayImage(mMeeting.logosmall, ivMeetingHeader);
	}

	private void bindJoinInView(boolean isJoin) {
		if (isJoin) {
			viewNotJoinIn.setVisibility(View.GONE);
			viewJoinIn.setVisibility(View.VISIBLE);
		} else {
			viewNotJoinIn.setVisibility(View.VISIBLE);
			viewJoinIn.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_meeting_guest: {
			goToMemberActivity(TribeMemberActivity.TYPE_MEETING_GUEST);
			break;
		}

		case R.id.tv_meeting_host: {
			goToMemberActivity(TribeMemberActivity.TYPE_MEETING_HOST);
			break;
		}
		case R.id.tv_meeting_join_in: {
			goToMemberActivity(TribeMemberActivity.TYPE_MEETING_USER);
			break;
		}
		case R.id.btn_on_look:
		case R.id.btn_enter_meeting:
		case R.id.grid_enter_meeting: {
			Intent intent = new Intent();
			intent.setClass(mContext, ChatTribeActivity.class);
			intent.putExtra(ChatTribeActivity.KEY_TRIBE, mMeeting);
			intent.putExtra(ChatTribeActivity.KEY_CHAT_TYPE, ChatTribeActivity.CHAT_TYPE_MEETING);
			startActivity(intent);
			break;
		}

		case R.id.btn_want_in_meeting:
			applyWithReason(AddReasonActivity.TYPE_TO_JOIN_MEETING);
			break;
		case R.id.btn_hide_grid:
			hideMoreWindwo();
			break;
		case R.id.grid_notify_meeting_start: {
			setAlarmForMeeting();
			break;
		}
		case R.id.grid_avoid_disturb: {
			moreSetNotPush(getSwitchStateLevel(v), v);
			break;
		}
		case R.id.grid_user_real_name: {
			int level = ((ImageView) ((ViewGroup) v).getChildAt(0)).getDrawable().getLevel();
			((ImageView) ((ViewGroup) v).getChildAt(0)).setImageLevel(1 - level);
			break;
		}

		case R.id.grid_login_out:
			logOut();
			break;

		case R.id.grid_want_to_be_host:
			applyWithReason(AddReasonActivity.TYPE_TO_BE_HOST);
			break;

		case R.id.grid_want_to_be_guest:
			applyWithReason(AddReasonActivity.TYPE_TO_BE_GUEST);
			break;

		case R.id.grid_invite_to_meeting:
			invite(ShareActivity.TYPE_INVITE_USER);
			break;
		case R.id.grid_invite_host:
			invite(ShareActivity.TYPE_INVITE_HOST);
			break;
		case R.id.grid_invite_guest:
			invite(ShareActivity.TYPE_INVITE_GUEST);
			break;
		case R.id.grid_clear_local_msg:
			MessageHelper.clearChatCache(mContext, mMeetingID, 300, deleteCallback);
			break;
		case R.id.grid_restore_to_normal:
			break;
		case R.id.grid_deal_apply_guest:
			dealApply(3);
			break;
		case R.id.grid_deal_apply_meeting:
			dealApply(1);
			break;
		case R.id.grid_deal_apply_host:
			dealApply(2);
			break;
		case R.id.grid_cancel_meeting:
			applyWithReason(AddReasonActivity.TYPE_DISMISS_MEETING, REQUEST_CANCEL_MEETING);
			break;
		case R.id.grid_modify_meeting: {
			Intent intent = new Intent(mContext, CreatMeetingActivity.class);
			intent.putExtra(CreatMeetingActivity.KEY_MEETING, mMeeting);
			startActivity(intent);
			break;
		}
		case R.id.ab_share:
//			share();
			ShareManager shareManager = new ShareManager(this);
			shareManager.shareContentRecommend("aaaaaaa", "http://www.baidu.com");
			shareManager.setDyCallback(new CallDyback() {
				@Override
				public void spreadDy() {
					// TODO Auto-generated method stub
					spreadMeeting(mMeeting);
				}
			});
			break;
		default:
			break;
		}

	}
	
	
	
	public void spreadMeeting(Tribe meeting) {
		DamiInfo.spreadDynamic(3, meeting.id, "", "", "", "", new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					showToast(R.string.spread_success);
				} else {
					otherCondition(data.state, MeetingDetailActivity.this);
				}
			}
		});
		
	}

	private void setAlarmForMeeting() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MeetingDetailActivity.this, AlarmReceiver.class); // 创建Intent对象
		PendingIntent pi = PendingIntent.getBroadcast(MeetingDetailActivity.this, 0, intent, 0); // 创建PendingIntent
		// alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
		// //设置闹钟
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pi); // 设置闹钟，当前时间就唤醒
		showToast("success");
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

	private void goToMemberActivity(int type) {
		Intent intent = new Intent(MeetingDetailActivity.this, TribeMemberActivity.class);
		intent.putExtra(TribeMemberActivity.KEY_TRIBE_ID, mMeetingID);
		intent.putExtra(TribeMemberActivity.KEY_TYPE, type);
		startActivity(intent);
	}

	// 处理申请
	private void dealApply(int type) {
		Intent applyMeetingIntent = new Intent(mContext, ApplyListActivity.class);
		applyMeetingIntent = new Intent(mContext, ApplyListActivity.class);
		applyMeetingIntent.putExtra("id", mMeetingID);
		applyMeetingIntent.putExtra("type", type);
		startActivity(applyMeetingIntent);
	}

	private void invite(int type) {
		Intent intent = new Intent();
		intent.setClass(mContext, ShareActivity.class);
		intent.putExtra(ShareActivity.KEY_TRIBE_ID, mMeetingID);
		intent.putExtra(ShareActivity.KEY_TYPE, type);
		startActivity(intent);
	}

	private void moreSetNotPush(final int level, final View v) {
		DamiInfo.setNotPush(mMeetingID, 1 - level, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				SimpleStateBean data = (SimpleStateBean) o;
				if (data.state != null && data.state.code == 0) {
					changeSwitchState(v);
					spo.setInt(SPConst.getTribeUserId(mContext, mMeetingID), 1 - level);
				} else {
					otherCondition(data.state, MeetingDetailActivity.this);
				}
			}
		});
	}

	private void changeSwitchState(View v) {
		int level = ((ImageView) ((ViewGroup) v).getChildAt(0)).getDrawable().getLevel();
		setSwitchState(v, 1 - level);
	}

	private void setSwitchState(View v, int level) {
		((ImageView) ((ViewGroup) v).getChildAt(0)).setImageLevel(level);
	}

	private int getSwitchStateLevel(View v) {
		return ((ImageView) ((ViewGroup) v).getChildAt(0)).getDrawable().getLevel();
	}

	private void logOut() {
		DamiInfo.exitMeeting(mMeetingID, new SimpleResponseListener(mContext, "正在退出...") {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				dealWithSimpleResult(this, o);
			}
		});
	}

	private void dealWithSimpleResult(SimpleResponseListener listener, Object o) {
		BaseNetBean data = (BaseNetBean) o;
		if (data.state != null && data.state.code == 0) {
			getMeetingDetail();
		} else {
			listener.otherCondition(data.state, MeetingDetailActivity.this);
		}
	}

	private void applyWithReason(int type) {
		applyWithReason(type, REQUEST_NORMAL);
	}

	private void applyWithReason(int type, int request) {
		Intent intent = new Intent();
		intent.setClass(mContext, AddReasonActivity.class);
		intent.putExtra(AddReasonActivity.KEY_MEETING_ID, mMeetingID);
		intent.putExtra(AddReasonActivity.KEY_APLLY_TYPE, type);
		startActivityForResult(intent, request);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CANCEL_MEETING:
			if (resultCode == RESULT_OK) {
				MeetingDetailActivity.this.finish();
			}
			break;
		default:
			if (resultCode == RESULT_OK) {
				getMeetingDetail();
			}
			break;
		}
	}

	PopupWindow moreWindow;

	private void showMoreWindow(int role) {
		if (chatGridLayout == null) {
			chatGridLayout = (ViewGroup) getGridView(role);
		}
		if (moreWindow == null) {
			moreWindow = new PopupWindow(chatGridLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}
		// moreWindow.setBackgroundDrawable(new
		// ColorDrawable(android.R.color.transparent));
		moreWindow.setBackgroundDrawable(new BitmapDrawable());
		moreWindow.setOutsideTouchable(true);
		moreWindow.setFocusable(true);
		moreWindow.setAnimationStyle(R.style.window_bottom_animation);
		moreWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
	}

	private void hideMoreWindwo() {
		if (moreWindow != null && moreWindow.isShowing()) {
			moreWindow.dismiss();
		}
	}

	private ViewGroup getGridView(int type) {
		ViewGroup viewGroup = null;
		View view;
		switch (type) {
		case 0:// 普通用户
			viewGroup = (ViewGroup) mInflater.inflate(R.layout.grid_meeting_more_custom, null);
			view = viewGroup.findViewById(R.id.btn_hide_grid);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_notify_meeting_start);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_enter_meeting);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_want_to_be_host);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_want_to_be_guest);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_avoid_disturb);
			setSwitchState(view, spo.getInt(SPConst.getTribeUserId(mContext, mMeetingID), 0));
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_clear_local_msg);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_login_out);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_user_real_name);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_invite_to_meeting);
			view.setOnClickListener(this);
			break;
		case 1:// 会议发起人同时也是主持人
			viewGroup = (ViewGroup) mInflater.inflate(R.layout.grid_meeting_more_faqiren, null);
			view = viewGroup.findViewById(R.id.btn_hide_grid);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_enter_meeting);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_invite_to_meeting);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_invite_guest);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_invite_host);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_deal_apply_meeting);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_deal_apply_guest);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_deal_apply_host);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_modify_meeting);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_cancel_meeting);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_avoid_disturb);
			setSwitchState(view, spo.getInt(SPConst.getTribeUserId(mContext, mMeetingID), 0));
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_clear_local_msg);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_notify_meeting_start);
			view.setOnClickListener(this);
			break;
		case 2:// 会议嘉宾或者部落自发申请实名用户
			viewGroup = (ViewGroup) mInflater.inflate(R.layout.grid_meeting_more_jiabing, null);
			view = viewGroup.findViewById(R.id.btn_hide_grid);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_enter_meeting);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_restore_to_normal);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_want_to_be_host);

			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_notify_meeting_start);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_avoid_disturb);
			setSwitchState(view, spo.getInt(SPConst.getTribeUserId(mContext, mMeetingID), 0));
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_clear_local_msg);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_invite_to_meeting);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_login_out);
			view.setOnClickListener(this);
			break;
		case 3:// 会议主持人
			viewGroup = (ViewGroup) mInflater.inflate(R.layout.grid_meeting_more_zhuchiren, null);
			view = viewGroup.findViewById(R.id.btn_hide_grid);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_enter_meeting);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_restore_to_normal);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_invite_to_meeting);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_deal_apply_meeting);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_deal_apply_guest);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_invite_guest);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_notify_meeting_start);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_avoid_disturb);
			setSwitchState(view, spo.getInt(SPConst.getTribeUserId(mContext, mMeetingID), 0));
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_clear_local_msg);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_login_out);
			view.setOnClickListener(this);
			break;
		default:
			break;
		}
		return viewGroup;
	}

}
