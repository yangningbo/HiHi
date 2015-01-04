package com.gaopai.guiren.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.chat.ChatBaseActivity;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.activity.share.ShareActivity;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.TribeInfoBean;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.fragment.NotificationFragment;
import com.gaopai.guiren.support.ActionHolder;
import com.gaopai.guiren.support.ConversationHelper;
import com.gaopai.guiren.support.MessageHelper;
import com.gaopai.guiren.support.MessageHelper.DeleteCallback;
import com.gaopai.guiren.support.ShareManager;
import com.gaopai.guiren.support.ShareManager.CallDyback;
import com.gaopai.guiren.support.alarm.AlarmReceiver;
import com.gaopai.guiren.utils.DateUtil;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.Logger;
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

	public static final String KEY_MEETING_ID = "id";
	public static final String KEY_MEETING = "meeting";
	private String mMeetingID = "";// pass tribe entity only in preview mode
	private Tribe mMeeting;
	private boolean isPreview = false;

	public final static int REQUEST_NORMAL = 0;
	public final static int REQUEST_BACK_TO_NORMAL = 1;
	public final static int REQUEST_CANCEL_MEETING = 2;
	public final static int REQUEST_EDIT_MEETING = 3;

	public static final String ACTION_AGREE_ADD_MEETING = "com.gaopai.guiren.ACTION_AGREE_ADD_MEETING";
	public static final String ACTION_MEETING_CANCEL = "com.gaopai.guiren.ACTION_MEETING_CANCEL";

	private PreferenceOperateUtils spo;
	private PreferenceOperateUtils spoAnony;

	private User loginUser;

	private View shareView;

	private boolean isFromAlarm = false;// if alarm service start this activity,
										// true show some toasts

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_meeting_detail);

		spo = new PreferenceOperateUtils(mContext, SPConst.SP_AVOID_DISTURB);
		spoAnony = new PreferenceOperateUtils(mContext, SPConst.SP_ANONY);

		loginUser = DamiCommon.getLoginResult(mContext);

		mMeetingID = getIntent().getStringExtra(KEY_MEETING_ID);
		isFromAlarm = getIntent().getBooleanExtra("isalarm", false);
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
		shareView = mTitleBar.addRightButtonView(R.drawable.selector_titlebar_share);
		shareView.setId(R.id.ab_share);
		shareView.setOnClickListener(this);

		initComponent();
		if (isPreview) {
			bindPreviewView();
			return;
		}

		addLoadingView();

		getMeetingDetail();
		if (isFromAlarm) {
			showToast(R.string.alarm_meeting_is_on_going);
		}
	}

	@Override
	protected void registerReceiver(IntentFilter filter) {
		// TODO Auto-generated method stub
		filter.addAction(TribeActivity.ACTION_KICK_TRIBE);
		filter.addAction(ACTION_AGREE_ADD_MEETING);
		filter.addAction(ACTION_MEETING_CANCEL);
		super.registerReceiver(filter);
	}

	@Override
	protected void onReceive(Intent intent) {
		String action = intent.getAction();
		if (TextUtils.isEmpty(action)) {
			return;
		}
		if (action.equals(TribeActivity.ACTION_KICK_TRIBE)) {
			String id = intent.getStringExtra("id");
			if (!TextUtils.isEmpty(id) && id.equals(mMeetingID)) {
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
		super.onReceive(intent);
	}

	public static Intent getIntent(Context context, String tid) {
		Intent intent = new Intent(context, MeetingDetailActivity.class);
		intent.putExtra(KEY_MEETING_ID, tid);
		return intent;
	}

	public static Intent getAlarmIntent(Context context, String tid) {
		Intent intent = new Intent(context, MeetingDetailActivity.class);
		intent.putExtra("isalarm", true);
		intent.putExtra(KEY_MEETING_ID, tid);
		return intent;
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
		if (isPreview) {
			return;
		}
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

	private void getMeetingDetail() {
		showLoadingView();
		DamiInfo.getMeetingDetail(mMeetingID, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final TribeInfoBean data = (TribeInfoBean) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null) {
						showContent();
						mMeeting = data.data;
						bindView();
					}
				} else {
					showErrorView();
					otherCondition(data.state, MeetingDetailActivity.this);
				}
			}

			@Override
			public void onFailure(Object o) {
				showErrorView();
			}
		});
	}

	private void showErrorView() {
		showErrorView(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getMeetingDetail();
			}
		});
	}

	private void bindPreviewView() {
		bindBasicView();
		if (!TextUtils.isEmpty(mMeeting.logosmall)) {
			Drawable drawable = Drawable.createFromPath(mMeeting.logosmall);
			ivMeetingHeader.setImageDrawable(drawable);
		} else {
			Drawable drawable = Drawable.createFromPath(mMeeting.logolarge);
			ivMeetingHeader.setImageDrawable(drawable);
		}
	}

	private void bindView() {
		bindBasicView();
		ImageLoaderUtil.displayImage(mMeeting.logolarge, ivMeetingHeader, R.drawable.icon_default_meeting);
		tvMeetingHost.setText(mMeeting.hosts);
		tvMeetingGuest.setText(mMeeting.guest);
		tvMeetingJoinIn.setText(mMeeting.user);
		bindJoinInView();

		if (mMeeting.check == 1) {
			shareView.setVisibility(View.VISIBLE);
		} else {
			shareView.setVisibility(View.GONE);
		}
	}

	private void bindBasicView() {
		tvMeetingTitle.setText(mMeeting.name);
		tvMeetingInfo.setText(mMeeting.content);
		tvMeetingTime.setText(DateUtil.getCreatTimeFromSeconds(mMeeting.start, mMeeting.end));
		tvMeetingTimeDiff.setText(DateUtil.getMeetingDiffStrFromSeconds(mMeeting.start, mMeeting.end));
	}

	private void bindJoinInView() {
		if (mMeeting.check == 0) {// now checking
			tvMeetingTimeDiff.setText(R.string.meeting_is_checking);
		} else if (mMeeting.check == 2) {
			tvMeetingTimeDiff.setText(R.string.meeting_not_pass_checking);
		}
		if (mMeeting.check == 0 || mMeeting.check == 2) {
			if (mMeeting.uid.equals(loginUser.uid)) {// I am the creator
				viewJoinIn.setVisibility(View.VISIBLE);
				viewNotJoinIn.setVisibility(View.GONE);
				btnEnterMeeting.setText(R.string.edit_meeting);
				btnSetting.setEnabled(false);
			} else {
				viewNotJoinIn.setVisibility(View.VISIBLE);
				viewJoinIn.setVisibility(View.GONE);
				btnOnLook.setEnabled(false);
			}

			tvMeetingGuest.setEnabled(false);
			tvMeetingHost.setEnabled(false);
			tvMeetingJoinIn.setEnabled(false);
			tvMeetingJoinIn.setText("");
			tvMeetingHost.setText(mMeeting.realname);
			return;
		}

		tvMeetingGuest.setEnabled(true);
		tvMeetingHost.setEnabled(true);
		tvMeetingJoinIn.setEnabled(true);
		btnSetting.setEnabled(true);
		btnOnLook.setEnabled(true);
		btnEnterMeeting.setText(R.string.enter_meeting);

		boolean isJoin = (mMeeting.isjoin == 1);
		if (isJoin) {
			viewNotJoinIn.setVisibility(View.GONE);
			viewJoinIn.setVisibility(View.VISIBLE);
		} else {
			viewNotJoinIn.setVisibility(View.VISIBLE);
			viewJoinIn.setVisibility(View.GONE);
			if (mMeeting.type == 2) {
				btnOnLook.setEnabled(false);
			} else {
				btnOnLook.setEnabled(true);
			}
		}
	}

	private boolean isMeetingStart() {
		return mMeeting.start * 1000 < System.currentTimeMillis();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_meeting_guest: {
			if (mMeeting.type == 2 && mMeeting.isjoin == 0) {
				showToast(getString(R.string.no_right_see_members));
				return;
			}
			goToMemberActivity(TribeMemberActivity.TYPE_MEETING_GUEST);
			break;
		}

		case R.id.tv_meeting_host: {
			if (mMeeting.type == 2 && mMeeting.isjoin == 0) {
				showToast(getString(R.string.no_right_see_members));
				return;
			}
			goToMemberActivity(TribeMemberActivity.TYPE_MEETING_HOST);
			break;
		}
		case R.id.tv_meeting_join_in: {
			if (mMeeting.type == 2 && mMeeting.isjoin == 0) {
				showToast(getString(R.string.no_right_see_members));
				return;
			}
			goToMemberActivity(TribeMemberActivity.TYPE_MEETING_USER);
			break;
		}
		case R.id.btn_on_look:
			startActivity(ChatTribeActivity.getIntent(mContext, mMeeting, ChatTribeActivity.CHAT_TYPE_MEETING, true));
			break;
		case R.id.btn_enter_meeting:
			if (mMeeting.check == 0 || mMeeting.check == 2) {
				modifyMeeting();
				break;
			}
		case R.id.grid_enter_meeting:
			if (!isMeetingStart()) {
				showToast(R.string.meeting_is_not_begin);
				return;
			}
			startActivity(ChatTribeActivity.getIntent(mContext, mMeeting, ChatTribeActivity.CHAT_TYPE_MEETING));
			break;

		case R.id.btn_want_in_meeting:
			if (mMeeting.ispwd == 0) {
				wantJoinMeeting();
			} else {
				startActivity(TribeVierifyActivity.getIntent(mContext, mMeeting, 1));
			}
			break;
		case R.id.btn_hide_grid:
			hideMoreWindow();
			break;
		case R.id.grid_notify_meeting_start: {
			if (isAlarm()) {
				cancelMeetingAlarm();
			} else {
				setAlarmForMeeting();
			}
			changeAlarm(v);
			break;
		}
		case R.id.grid_avoid_disturb: {
			changeAvoidDisturb(v);
			break;
		}
		case R.id.grid_user_real_name: {
			changeUseRealName(v);
			break;
		}

		case R.id.grid_login_out:
			showExitDialog();
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
			applyWithReason(AddReasonActivity.TYPE_TO_BE_NORMAL, REQUEST_BACK_TO_NORMAL);
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
			modifyMeeting();
			break;
		}
		case R.id.ab_share:
			if (mMeeting == null) {
				return;
			}

			String strUrl = DamiInfo.HOST + DamiInfo.SHARE_MEETING + mMeeting.id;
			ShareManager shareManager = new ShareManager(this);
			shareManager.shareTribeLink(DamiCommon.getLoginResult(mContext).realname + " 扩散了一个会议[" + mMeeting.name
					+ "]",
					"老友，我已经加入了会议[" + mMeeting.name + "],会议里边采用了讲后即焚机制保护您的安全，甩掉名缰利锁，讲真话，讲干货，真的很不错，您也来吧 " + strUrl,
					strUrl);
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

		hideMoreWindow();
	}

	private void modifyMeeting() {
		Intent intent = new Intent(mContext, CreatMeetingActivity.class);
		intent.putExtra(CreatMeetingActivity.KEY_MEETING, mMeeting);
		startActivityForResult(intent, REQUEST_EDIT_MEETING);
	}

	private void showExitDialog() {
		showDialog(getString(R.string.confirm_exit_meeting), null, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				exitMeeting();
			}
		});
	}

	public void wantJoinMeeting() {
		DamiInfo.applyMeeting(mMeetingID, "", new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					showToast(R.string.send_request_success);
				} else {
					otherCondition(data.state, MeetingDetailActivity.this);
				}
			}
		});
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
		if (isMeetingStart()) {
			showToast(R.string.meeting_is_start);
			return;
		}
		Intent intent = new Intent(MeetingDetailActivity.this, AlarmReceiver.class); // 创建Intent对象
		intent.putExtra("id", mMeeting.id);
		intent.setAction(this.getPackageName() + ".meeting." + mMeeting.id);
		PendingIntent pi = PendingIntent.getBroadcast(MeetingDetailActivity.this, 199823, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Logger.d(this, "current=" + System.currentTimeMillis() + "   diff="
				+ (System.currentTimeMillis() - mMeeting.start * 1000) / 1000);
		alarmManager.set(AlarmManager.RTC_WAKEUP, mMeeting.start * 1000, pi); // 设置闹钟，当前时间就唤醒
	}

	private void cancelMeetingAlarm() {
		if (isMeetingStart()) {
			showToast(R.string.meeting_is_start);
			return;
		}
		Intent intent = new Intent(MeetingDetailActivity.this, AlarmReceiver.class); // 创建Intent对象
		intent.setAction(this.getPackageName() + ".meeting." + mMeetingID);
		PendingIntent pi = PendingIntent.getBroadcast(MeetingDetailActivity.this, 199823, intent,
				PendingIntent.FLAG_UPDATE_CURRENT); // 创建PendingIntent
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pi);
	}

	private boolean isAlarm() {
		PreferenceOperateUtils po = new PreferenceOperateUtils(mContext, SPConst.SP_ALARM);
		return po.getBoolean(SPConst.getSingleSpId(mContext, mMeetingID), false);
	}

	private void setAlarm(boolean isAlarm) {
		PreferenceOperateUtils po = new PreferenceOperateUtils(mContext, SPConst.SP_ALARM);
		po.setBoolean(SPConst.getSingleSpId(mContext, mMeetingID), isAlarm);
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

	private void exitMeeting() {
		DamiInfo.exitMeeting(mMeetingID, new SimpleResponseListener(mContext, R.string.request_internet_now) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					getMeetingDetail();
					sendBroadcast(ActionHolder.getExitIntent(mMeetingID, ActionHolder.ACTION_QUIT_MEETING));
					MainActivity.minusMeeting(mContext);
					deleteConverstion();
				} else {
					otherCondition(data.state, MeetingDetailActivity.this);
				}
			}
		});
	}

	private void deleteConverstion() {
		ConversationHelper.deleteItem(mContext, mMeetingID);
		sendBroadcast(new Intent(NotificationFragment.ACTION_MSG_NOTIFY));
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
		case REQUEST_EDIT_MEETING:
			return;
		case REQUEST_CANCEL_MEETING:
			if (resultCode == RESULT_OK) {
				MeetingDetailActivity.this.finish();
				sendBroadcast(ActionHolder.getExitIntent(mMeetingID, ActionHolder.ACTION_CANCEL_MEETING));
				MainActivity.minusMeeting(mContext);
				deleteConverstion();
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
		chatGridLayout = (ViewGroup) getGridView(role);
		moreWindow = new PopupWindow(chatGridLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		moreWindow.setBackgroundDrawable(new BitmapDrawable());
		moreWindow.setOutsideTouchable(true);
		moreWindow.setFocusable(true);
		moreWindow.setAnimationStyle(R.style.window_bottom_animation);
		moreWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
	}

	private void hideMoreWindow() {
		if (moreWindow != null && moreWindow.isShowing()) {
			moreWindow.dismiss();
		}
	}

	private void changeUseRealName(View v) {
		int level;
		if (mMeeting.role != 0) {
			level = spoAnony.getInt(SPConst.getSingleSpId(mContext, mMeeting.id), 0);
		} else {
			level = spoAnony.getInt(SPConst.getSingleSpId(mContext, mMeeting.id), 1);
		}
		spoAnony.setInt(SPConst.getSingleSpId(mContext, mMeetingID), 1 - level);
		if (level == 0) {
			showToast(R.string.switch_use_anony_name_mode);
		} else {
			showToast(R.string.switch_use_real_name_mode);
		}
		sendBroadcast(new Intent(ChatBaseActivity.ACTION_CHANGE_VOICE));
		setUseRealName(v);
	}

	private void setUseRealName(View v) {
		int anony;
		if (mMeeting.role != 0) {
			anony = spoAnony.getInt(SPConst.getSingleSpId(mContext, mMeeting.id), 0);
		} else {
			anony = spoAnony.getInt(SPConst.getSingleSpId(mContext, mMeeting.id), 1);
		}
		setSwitchState(v, anony);
		if (anony == 0) {
			setViewText(v, R.string.user_anony_name);
		} else {
			setViewText(v, R.string.user_real_name);
		}
	}

	private void changeAvoidDisturb(View v) {
		int anony = spo.getInt(SPConst.getSingleSpId(mContext, mMeetingID), 0);
		if (anony == 0) {
			showToast(R.string.switch_avoid_disturb_on);// yes we did remove
														// notification
		} else {
			showToast(R.string.switch_avoid_disturb_off);
		}
		spo.setInt(SPConst.getSingleSpId(mContext, mMeetingID), 1 - anony);
		setAvoidDisturb(v);
	}

	private void setAvoidDisturb(View v) {
		int anony = spo.getInt(SPConst.getSingleSpId(mContext, mMeetingID), 0);// 0=now
																				// we
																				// have
																				// notification
		setSwitchState(v, 1 - anony);
		if (anony == 0) {
			setViewText(v, R.string.avoid_disturb_on);// want to remove
														// notification
		} else {
			setViewText(v, R.string.avoid_disturb_off);
		}
	}

	private void changeAlarm(View v) {
		boolean isAlarm = isAlarm();
		setAlarm(!isAlarm);
		if (isAlarm) {
			showToast(R.string.switch_meeting_start_alarm_off);
		} else {
			showToast(R.string.switch_meeting_start_alarm_on);
		}
		setAlarm(v);
	}

	private void setAlarm(View v) {
		boolean isAlarm = isAlarm();
		setSwitchState(v, isAlarm ? 1 : 0);
		if (isAlarm) {
			setViewText(v, R.string.meeting_start_alarm_off);
		} else {
			setViewText(v, R.string.meeting_start_alarm_on);
		}
	}

	private void setViewText(View v, int resid) {
		((TextView) ((ViewGroup) v).getChildAt(1)).setText(resid);
	}

	private void changeSwitchState(View v) {
		int level = getSwitchStateLevel(v);
		setSwitchState(v, 1 - level);
	}

	private void setSwitchState(View v, int level) {
		((ImageView) ((ViewGroup) v).getChildAt(0)).setImageLevel(level);
	}

	private int getSwitchStateLevel(View v) {
		return ((ImageView) ((ViewGroup) v).getChildAt(0)).getDrawable().getLevel();
	}

	private ViewGroup getGridView(int type) {
		ViewGroup viewGroup = null;
		View view;
		switch (type) {
		case 0:// 普通用户
			viewGroup = (ViewGroup) mInflater.inflate(R.layout.grid_meeting_more_custom, null);
			view = viewGroup.findViewById(R.id.btn_hide_grid);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_enter_meeting);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_want_to_be_host);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_want_to_be_guest);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_clear_local_msg);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_login_out);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_invite_to_meeting);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_avoid_disturb);
			setAvoidDisturb(view);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_user_real_name);
			setUseRealName(view);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_notify_meeting_start);
			setAlarm(view);
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
			view = viewGroup.findViewById(R.id.grid_clear_local_msg);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_avoid_disturb);
			setAvoidDisturb(view);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_notify_meeting_start);
			setAlarm(view);
			view.setOnClickListener(this);
			break;
		case 3:// 会议嘉宾或者部落自发申请实名用户
			viewGroup = (ViewGroup) mInflater.inflate(R.layout.grid_meeting_more_jiabing, null);
			view = viewGroup.findViewById(R.id.btn_hide_grid);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_enter_meeting);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_restore_to_normal);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_want_to_be_host);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_clear_local_msg);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_invite_to_meeting);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_login_out);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_notify_meeting_start);
			setAlarm(view);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_avoid_disturb);
			setAvoidDisturb(view);
			view.setOnClickListener(this);
			break;
		case 2:// 会议主持人
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
			view = viewGroup.findViewById(R.id.grid_login_out);
			view.setOnClickListener(this);
			view = viewGroup.findViewById(R.id.grid_clear_local_msg);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_notify_meeting_start);
			setAlarm(view);
			view.setOnClickListener(this);

			view = viewGroup.findViewById(R.id.grid_avoid_disturb);
			setAvoidDisturb(view);
			view.setOnClickListener(this);
			break;
		default:
			break;
		}

		return viewGroup;
	}

}
