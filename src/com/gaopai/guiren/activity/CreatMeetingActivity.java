package com.gaopai.guiren.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.support.CameralHelper;
import com.gaopai.guiren.support.ImageCrop;
import com.gaopai.guiren.utils.DateUtil;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.utils.ViewUtil.OnTextChangedListener;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class CreatMeetingActivity extends BaseActivity implements OnClickListener {

	private static final int MIN_LEN = 2;
	private static final int MAX_LEN = 15;

	private int mYear = 0;
	private int mMonth = 0;
	private int mDay = 0;
	private int mHour = 0;
	private int mMinute = 0;
	int mYeaer1 = 0, mMonth1 = 0;

	private TextView tvStartTime;
	private TextView tvEndTime;
	private TextView tvNumLimit;

	private Button btnUploadPic;
	private Button btnCreat;
	private Button btnPreview;

	private EditText etTitle;
	private EditText etContent;

	private TextView tvSetPassword;
	private EditText etPassword;
	private EditText etPasswordAgain;
	private View layoutPrivacySetting;
	private TextView tvPrivacySetting;
	private View layoutPasswordSetting;

	private ImageView ivHeader;

	private int mPrivacy = 1; // 1公开 2私密

	private String mFilePath = "";

	private boolean isSetPassword = true;
	private Tribe mMeeting;
	public static String KEY_MEETING = "meeting";
	private boolean isEdit = false;

	private CameralHelper cameralHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_creat_meeting);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(getString(R.string.create_meeting));
		mMeeting = (Tribe) getIntent().getSerializableExtra(KEY_MEETING);
		if (mMeeting != null) {
			isEdit = true;
		}
		initComponent();
		bindView();
		cameralHelper = new CameralHelper(this, new CameralHelper.Option(1, true, ImageCrop.MEETING_WIDTH,
				ImageCrop.MEETING_HEIGHT));
		cameralHelper.setCallback(new CameralHelper.SimpleCallback() {
			@Override
			public void receiveCropBitmap(Bitmap bitmap) {
				setPic(bitmap);
			}

			@Override
			public void receiveCropPic(String path) {
				mFilePath = path;
			}
		});
	}

	private void bindView() {
		// TODO Auto-generated method stub
		if (!isEdit) {
			initialTimeView();
			return;
		}
		mTitleBar.setTitleText(R.string.edit_meeting);
		ImageLoaderUtil.displayImage(mMeeting.logolarge, ivHeader);
		etTitle.setText(mMeeting.name);
		if (mMeeting.type == 1) {
			mPrivacy = 1;
		} else {
			mPrivacy = 2;
		}
		tvPrivacySetting.setText(mPrivacy == 1 ? getString(R.string.privacy_setting_open)
				: getString(R.string.privacy_setting_close));
		tvStartTime.setText(FeatureFunction.getChatTime(mMeeting.start * 1000));
		tvEndTime.setText(FeatureFunction.getChatTime(mMeeting.end * 1000));
		etContent.setText(mMeeting.content);
		btnCreat.setText(R.string.edit_meeting);
	}

	private void initialTimeView() {
		Calendar calendar = Calendar.getInstance();
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
		mHour = calendar.get(Calendar.HOUR_OF_DAY);
		mMinute = calendar.get(Calendar.MINUTE);
		String time = DateUtil.showTimedate(mYear, mMonth, mDay, mHour, mMinute);
		tvStartTime.setText(time);
		tvEndTime.setText(time);
	}

	private void initComponent() {
		tvStartTime = (TextView) findViewById(R.id.tv_start_time);
		tvStartTime.setOnClickListener(this);
		tvEndTime = (TextView) findViewById(R.id.tv_end_time);
		tvEndTime.setOnClickListener(this);
		btnUploadPic = (Button) findViewById(R.id.btn_upload_pic);
		btnUploadPic.setOnClickListener(this);

		btnCreat = (Button) findViewById(R.id.btn_creat);
		btnPreview = (Button) findViewById(R.id.btn_preview);
		btnCreat.setOnClickListener(this);
		btnPreview.setOnClickListener(this);

		etContent = (EditText) findViewById(R.id.et_meeting_info);
		etTitle = (EditText) findViewById(R.id.et_meeting_title);
		tvSetPassword = ViewUtil.findViewById(this, R.id.tv_set_password);
		tvSetPassword.setOnClickListener(this);
		etPassword = ViewUtil.findViewById(this, R.id.et_enter_password);
		etPasswordAgain = ViewUtil.findViewById(this, R.id.et_enter_password_again);
		layoutPrivacySetting = ViewUtil.findViewById(this, R.id.layout_privacy_setting);
		layoutPrivacySetting.setOnClickListener(this);
		layoutPasswordSetting = ViewUtil.findViewById(this, R.id.layout_password);

		tvPrivacySetting = ViewUtil.findViewById(this, R.id.tv_privacy_setting);
		ivHeader = (ImageView) findViewById(R.id.iv_meeeting_header);

		tvNumLimit = ViewUtil.findViewById(this, R.id.tv_num_limit);

		etContent.addTextChangedListener(ViewUtil.creatNumLimitWatcher(etContent, 500, new OnTextChangedListener() {
			@Override
			public void onTextChanged(Editable s) {
				// TODO Auto-generated method stub
				tvNumLimit.setText("还能输入" + (500 - s.length()) + "字");
			}
		}));
		etContent.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (v.getId() == R.id.et_meeting_info) {
					v.getParent().requestDisallowInterceptTouchEvent(true);
					switch (event.getAction() & MotionEvent.ACTION_MASK) {
					case MotionEvent.ACTION_UP:
						v.getParent().requestDisallowInterceptTouchEvent(false);
						break;
					}
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_start_time:
			setTime(tvStartTime.getText().toString());
			showStartTimeDialog(mContext.getString(R.string.meeting_start_time), TYPE_START_TIME);
			break;
		case R.id.tv_end_time:
			setTime(tvEndTime.getText().toString());
			showStartTimeDialog(mContext.getString(R.string.meeting_end_time), TYPE_END_TIME);
			break;
		case R.id.iv_meeeting_header:
		case R.id.btn_upload_pic:
			cameralHelper.btnPhotoAction();
			break;
		case R.id.btn_creat:
			creatMeeting();
			break;
		case R.id.btn_preview:
			previewMeeting();
			break;
		case R.id.layout_privacy_setting:
			showPrivacyDialog();
			break;
		case R.id.tv_set_password:
			isSetPassword = !isSetPassword;
			showPasswordView();
			break;
		default:
			break;
		}
	}

	private void showPrivacyDialog() {
		// TODO Auto-generated method stub
		final String[] items = getResources().getStringArray(R.array.privacy_setting_choice);
		new AlertDialog.Builder(mContext).setTitle(getString(R.string.privacy_setting))
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mPrivacy = which + 1;
						tvPrivacySetting.setText(items[which]);
					}
				}).show();
	}

	private void showPasswordView() {
		// TODO Auto-generated method stub
		if (isSetPassword) {
			layoutPasswordSetting.setVisibility(View.VISIBLE);
			tvSetPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_switch_active, 0);
		} else {
			layoutPasswordSetting.setVisibility(View.GONE);
			tvSetPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_switch_normal, 0);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		cameralHelper.onActivityResult(requestCode, resultCode, data);
	}

	private void setPic(Bitmap bitmap) {
		ivHeader.setImageBitmap(bitmap);
		btnUploadPic.setVisibility(View.GONE);
		ViewUtil.findViewById(this, R.id.tv_add_pic_info).setVisibility(View.GONE);
		ivHeader.setOnClickListener(this);
	}

	private static final int TYPE_START_TIME = 0;
	private static final int TYPE_END_TIME = 1;

	private void showStartTimeDialog(String title, final int type) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.time_dialog, null);
		final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
		final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);
		timePicker.setAddStatesFromChildren(true);
		timePicker.setIs24HourView(true);
		if (mHour == 0) {
			Calendar c = Calendar.getInstance();
			mHour = c.get(Calendar.HOUR_OF_DAY);
			mMinute = c.get(Calendar.MINUTE);
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
		} else {
			timePicker.setCurrentHour(mHour);
			timePicker.setCurrentMinute(mMinute);
			datePicker.init(mYear, mMonth, mDay, new OnDateChangedListener() {
				@Override
				public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				}
			});
		}
		Dialog dialog = new AlertDialog.Builder(mContext).setTitle(title).setView(view)
				.setPositiveButton(title, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						datePicker.clearFocus();
						timePicker.clearFocus();
						mYear = datePicker.getYear();
						mMonth = datePicker.getMonth();
						mDay = datePicker.getDayOfMonth();
						mHour = timePicker.getCurrentHour();
						mMinute = timePicker.getCurrentMinute();
						String time = DateUtil.showTimedate(mYear, mMonth, mDay, mHour, mMinute);
						if (TextUtils.isEmpty(time)) {
							showToast(mContext.getString(R.string.choose_pass_time));
							return;
						}
						if (type == TYPE_START_TIME) {
							tvStartTime.setText(time);
						} else {
							tvEndTime.setText(time);
						}
					}
				}).create();
		dialog.show();
	}

	private void setTime(String text) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		try {
			Date date = sDateFormat.parse(text);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			mYear = calendar.get(Calendar.YEAR);
			mMonth = calendar.get(Calendar.MONTH);
			mDay = calendar.get(Calendar.DAY_OF_MONTH);
			mHour = calendar.get(Calendar.HOUR_OF_DAY);
			mMinute = calendar.get(Calendar.MINUTE);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			mYear = 0;
			mMonth = 0;
			mDay = 0;
			mHour = 0;
			mMinute = 0;
			e.printStackTrace();
		}
	}

	private void previewMeeting() {
		final String title = etTitle.getText().toString();
		final String info = etContent.getText().toString();
		final String start = tvStartTime.getText().toString();
		final String end = tvEndTime.getText().toString();
		Tribe tempTribe = new Tribe();
		tempTribe.content = info;
		tempTribe.start = DateUtil.getTimeStamp(start);
		tempTribe.end = DateUtil.getTimeStamp(end);
		tempTribe.name = title;
		tempTribe.logosmall = mFilePath;
		Intent intent = new Intent(mContext, MeetingDetailActivity.class);
		intent.putExtra(MeetingDetailActivity.KEY_MEETING, tempTribe);
		startActivity(intent);
	}

	private void creatMeeting() {
		final String title = etTitle.getText().toString();
		final String info = etContent.getText().toString();
		final String password = etPassword.getText().toString();
		final String passwordAgain = etPasswordAgain.getText().toString();
		final String start = tvStartTime.getText().toString();
		final String end = tvEndTime.getText().toString();

		if (TextUtils.isEmpty(title)) {
			String prompt = mContext.getString(R.string.please_input) + mContext.getString(R.string.meeting_name);
			showToast(prompt);
			return;
		}

		if (title.length() < MIN_LEN) {
			String prompt = mContext.getString(R.string.meeting_name_too_short);
			showToast(prompt);
			return;
		}

		if (title.length() > MAX_LEN) {
			String prompt = mContext.getString(R.string.meeting_name_too_long);
			showToast(prompt);
			return;
		}

		if (TextUtils.isEmpty(start)) {
			String prompt = mContext.getString(R.string.please_input) + mContext.getString(R.string.meeting_start_time);
			showToast(prompt);
			return;
		}

		if (TextUtils.isEmpty(end)) {
			String prompt = mContext.getString(R.string.please_input) + mContext.getString(R.string.meeting_end_time);
			showToast(prompt);
			return;
		}

		long startl = DateUtil.convertStringToSeconds(start);
		long endl = DateUtil.convertStringToSeconds(end);
		if (startl >= endl) {
			showToast(mContext.getString(R.string.choose_time_error));
			return;
		}

		if (TextUtils.isEmpty(info)) {
			String prompt = mContext.getString(R.string.please_input) + mContext.getString(R.string.meeting_content);
			showToast(prompt);
			return;
		}

		if (isSetPassword) {
			if (TextUtils.isEmpty(passwordAgain + password)) {
				showToast(R.string.password_can_not_be_empty);
				return;
			}
			if (!password.equals(passwordAgain)) {
				showToast(R.string.password_not_same);
				return;
			}
		}

		SimpleResponseListener listener = new SimpleResponseListener(mContext, R.string.now_creat) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					String str = getString(R.string.create_success_and_wait);
					if (isEdit) {
						str = getString(R.string.edit_success);
					}
					showToast(str);
					if (isEdit) {
						setResult(RESULT_OK);
					}
					CreatMeetingActivity.this.finish();
				} else {
					this.otherCondition(data.state, CreatMeetingActivity.this);
				}
			}
		};
		// php can only decode second, so let it be
		if (isEdit) {
			DamiInfo.editMeeting(mMeeting.id, title, mFilePath, String.valueOf(mPrivacy), info, startl, endl, password,
					listener);
		} else {
			DamiInfo.addMeeting(title, mFilePath, String.valueOf(mPrivacy), info, startl, endl, password, listener);
		}
	}

}
