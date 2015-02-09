package com.gaopai.guiren.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.TimePicker;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.MsgConfigResult;
import com.gaopai.guiren.bean.MsgConfigResult.MsgConfigBean;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.utils.DateUtil;
import com.gaopai.guiren.utils.PreferenceOperateUtils;
import com.gaopai.guiren.utils.SPConst;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class NotifySettingActivity extends BaseActivity implements OnClickListener {

	private PreferenceOperateUtils spo;

	private TextView tvAvoidDisturb;
	private TextView tvFromTime;
	private TextView tvToTime;
	private TextView tvPlayRingtone;
	private TextView tvVibrate;
	private TextView tvDamiNotify;

	private View layoutTimeSetting;

	private MsgConfigBean settingBean;

	private int switchOff;
	private int switchOn;

	private int mHourFrom;
	private int mHourTo;
	private int mMinuteFrom;
	private int mMinuteTo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_notification_setting);
		addLoadingView();
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.msg_notify);
		initComponent();
		spo = new PreferenceOperateUtils(mContext, SPConst.SP_SETTING);
		getSettings();
		showLoadingView();
	}

	private void getSettings() {
		// TODO Auto-generated method stub
		DamiInfo.getMessageConfig(new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				MsgConfigResult data = (MsgConfigResult) o;
				if (data.state != null && data.state.code == 0) {
					settingBean = data.data;
					showContent();
					bindView();
				} else {
					showErrorView();
					otherCondition(data.state, NotifySettingActivity.this);
				}
			}

			@Override
			public void onFailure(Object o) {
				showErrorView();
				super.onFailure(o);
			}
		});
	}

	private void showErrorView() {
		showErrorView(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showLoadingView();
				getSettings();
			}
		});
	}

	private void bindView() {
		// TODO Auto-generated method stub
		saveDataInSp();
		setSwitchState(tvAvoidDisturb, settingBean.dnd);
		setSwitchState(tvPlayRingtone, settingBean.ringtones);
		setSwitchState(tvVibrate, settingBean.shake);
		setSwitchState(tvDamiNotify, settingBean.dami);
		if (settingBean.dnd == 0) {
			layoutTimeSetting.setVisibility(View.GONE);
		} else {
			mHourFrom = settingBean.dndH_begin;
			mHourTo = settingBean.dndH_end;
			mMinuteFrom = settingBean.dndM_begin;
			mMinuteTo = settingBean.dndM_end;
			bindTimeView();
			layoutTimeSetting.setVisibility(View.VISIBLE);
		}
	}

	private void bindTimeView() {
		tvFromTime.setText(DateUtil.getReadableTime(mHourFrom, mMinuteFrom));
		tvToTime.setText(DateUtil.getReadableTime(mHourTo, mMinuteTo));
	}

	private void setSwitchState(TextView textView, int state) {
		textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, (state == 0) ? switchOff : switchOn, 0);
	}

	private void initComponent() {
		// TODO Auto-generated method stub
		tvAvoidDisturb = (TextView) findViewById(R.id.tv_avoid_disturb_time_segment);
		tvFromTime = (TextView) findViewById(R.id.tv_from_time);
		tvToTime = ViewUtil.findViewById(this, R.id.tv_to_time);
		tvPlayRingtone = (TextView) findViewById(R.id.tv_play_ringtone);
		tvVibrate = (TextView) findViewById(R.id.tv_vibrate);
		tvDamiNotify = (TextView) findViewById(R.id.tv_dami_paper_notify);

		layoutTimeSetting = findViewById(R.id.layout_set_time);

		tvAvoidDisturb.setOnClickListener(this);
		tvFromTime.setOnClickListener(this);
		tvToTime.setOnClickListener(this);
		tvPlayRingtone.setOnClickListener(this);
		tvVibrate.setOnClickListener(this);
		tvDamiNotify.setOnClickListener(this);

		switchOff = R.drawable.icon_switch_normal;
		switchOn = R.drawable.icon_switch_active;
	}

	private void saveDataInSp() {
		spo.setInt(SPConst.getNotifySettingKey(mContext, SPConst.KEY_AVOID_DISTURB_TIME_SEGMENT), settingBean.dnd);
		spo.setInt(SPConst.getNotifySettingKey(mContext, SPConst.KEY_AVOID_DISTURB_HOUR_FROM), settingBean.dndH_begin);
		spo.setInt(SPConst.getNotifySettingKey(mContext, SPConst.KEY_AVOID_DISTURB_MINUTE_FROM), settingBean.dndM_begin);
		spo.setInt(SPConst.getNotifySettingKey(mContext, SPConst.KEY_AVOID_DISTURB_HOUR_TO), settingBean.dndH_end);
		spo.setInt(SPConst.getNotifySettingKey(mContext, SPConst.KEY_AVOID_DISTURB_MINUTE_TO), settingBean.dndM_end);
		spo.setInt(SPConst.getNotifySettingKey(mContext, SPConst.KEY_NOTIFY_PLAY_RINGTONES), settingBean.ringtones);
		spo.setInt(SPConst.getNotifySettingKey(mContext, SPConst.KEY_NOTIFY_VIBRATE), settingBean.shake);
		spo.setInt(SPConst.getNotifySettingKey(mContext, SPConst.KEY_NOTIFY_DAMI), settingBean.dami);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_avoid_disturb_time_segment:
			setPrivacy(1 - settingBean.dnd, settingBean.dndH_begin, settingBean.dndM_begin, settingBean.dndH_end,
					settingBean.dndM_end, settingBean.ringtones, settingBean.shake, settingBean.dami);
			break;
		case R.id.tv_from_time:
			type = TYPE_TIME_FROM;
			new TimePickerDialog(this, mTimeSetListener, mHourFrom, mMinuteFrom, true).show();
			break;
		case R.id.tv_to_time:
			type = TYPE_TIME_TO;
			new TimePickerDialog(this, mTimeSetListener, mHourTo, mMinuteTo, true).show();
			break;
		case R.id.tv_play_ringtone:
			setPrivacy(settingBean.dnd, settingBean.dndH_begin, settingBean.dndM_begin, settingBean.dndH_end,
					settingBean.dndM_end, 1 - settingBean.ringtones, settingBean.shake, settingBean.dami);
			break;
		case R.id.tv_vibrate:
			setPrivacy(settingBean.dnd, settingBean.dndH_begin, settingBean.dndM_begin, settingBean.dndH_end,
					settingBean.dndM_end, settingBean.ringtones, 1 - settingBean.shake, settingBean.dami);
			break;
		case R.id.tv_dami_paper_notify:
			setPrivacy(settingBean.dnd, settingBean.dndH_begin, settingBean.dndM_begin, settingBean.dndH_end,
					settingBean.dndM_end, settingBean.ringtones, settingBean.shake, 1 - settingBean.dami);
			break;
		default:
			break;
		}
	}

	private void setPrivacy(final int dnd, final int bh, final int bm, final int eh, final int em, final int ring,
			final int shake, final int dami) {
		DamiInfo.setMessageConfig(dnd, bh, bm, eh, em, ring, shake, dami, new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					settingBean.dnd = dnd;
					settingBean.dndH_begin = bh;
					settingBean.dndM_begin = bm;
					settingBean.dndH_end = eh;
					settingBean.dndM_end = em;
					settingBean.ringtones = ring;
					settingBean.shake = shake;
					settingBean.dami = dami;
					bindView();
				} else {
					otherCondition(data.state, NotifySettingActivity.this);
				}
			}
		});
	}

	private static final int TYPE_TIME_FROM = 0;
	private static final int TYPE_TIME_TO = 1;
	private int type;

	@Override
	protected Dialog onCreateDialog(int id) {
		Log.d("tag", "dialog id=" + id);
		switch (id) {
		case TYPE_TIME_FROM:
			type = TYPE_TIME_FROM;
			return new TimePickerDialog(this, mTimeSetListener, mHourFrom, mMinuteFrom, true);

		case TYPE_TIME_TO:
			type = TYPE_TIME_TO;
			return new TimePickerDialog(this, mTimeSetListener, mHourTo, mMinuteTo, true);
		}
		return null;
	}

	private OnTimeSetListener mTimeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			if (type == TYPE_TIME_FROM) {
				mHourFrom = hourOfDay;
				mMinuteFrom = minute;
			} else {
				mHourTo = hourOfDay;
				mMinuteTo = minute;
			}
			bindTimeView();
			if (mHourFrom * 60 + mHourFrom >= mHourTo * 60 + mHourTo) {
				showToast(R.string.choose_time_error);
				return;
			}
			setPrivacy(settingBean.dnd, mHourFrom, mMinuteFrom, mHourTo, mMinuteTo, settingBean.ringtones,
					settingBean.shake, settingBean.dami);

		}
	};

}
