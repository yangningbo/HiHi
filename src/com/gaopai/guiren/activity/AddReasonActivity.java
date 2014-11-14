package com.gaopai.guiren.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.bean.net.RecommendAddResult;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class AddReasonActivity extends BaseActivity {

	private EditText etAddReason;
	private Button btnAdd;

	public static final String KEY_REASON_TYPE = "reason_type";

	private User user = null;
	public static final String KEY_USER = "user";

	public static final String KEY_MEETING_ID = "meeting_id";
	public static final String KEY_APLLY_TYPE = "apply_type";
	public static final String KEY_MESSAGEINFO = "messageinfo";

	public static final int TYPE_TO_BE_HOST = 0;
	public static final int TYPE_TO_BE_GUEST = 1;
	public static final int TYPE_TO_BE_CUSTOM = 2;
	public static final int TYPE_DISMISS_MEETING = 3;
	public static final int TYPE_TO_JOIN_MEETING = 4;

	public static final int TYPE_WAHT_COMUNICATION = 10;
	public static final int TYPE_REFUSE_COMUNICATION = 11;

	public static final int TYPE_ADD_FRIEND = 20;

	public static final int TYPE_REFUSE_JOIN_TRIBE = 30;
	public static final int TYPE_REFUSE_JOIN_MEETING = 31;
	public static final int TYPE_REFUSE_BECOME_HOST = 32;
	public static final int TYPE_REFUSE_BECOME_GUEST = 33;
	
	public static final int TYPE_TO_JOIN_TRIBE = 40;

	private int type = -1;
	private String meetingId;
	private SimpleResponseListener resultListener;

	private MessageInfo messageInfo;

	private String title;
	private String hint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_add_reason);

		type = getIntent().getIntExtra(KEY_APLLY_TYPE, -1);
		hint = getString(R.string.please_input_reason);
		switch (type) {
		case TYPE_ADD_FRIEND:
			user = (User) getIntent().getSerializableExtra(KEY_USER);
			title = getString(R.string.add);
			break;

		case TYPE_TO_BE_CUSTOM:
			title = getString(R.string.back_to_normal);
		case TYPE_TO_BE_GUEST:
			title = getString(R.string.apply_to_jiabin);
		case TYPE_TO_BE_HOST:
			title = getString(R.string.apply_to_host);
		case TYPE_TO_JOIN_MEETING:
			title = getString(R.string.apply_add);
		case TYPE_DISMISS_MEETING:
			meetingId = getIntent().getStringExtra(KEY_MEETING_ID);
			break;

		case TYPE_WAHT_COMUNICATION:
			messageInfo = (MessageInfo) getIntent().getSerializableExtra(KEY_MESSAGEINFO);
			title = getString(R.string.seeking_contacts_reason);
			hint = getString(R.string.communication);
			break;

		case TYPE_REFUSE_JOIN_TRIBE:
			title = getString(R.string.refuse_add_into_tribe);
		case TYPE_REFUSE_JOIN_MEETING:
			title = getString(R.string.refuse_add_into_meeting);
		case TYPE_REFUSE_BECOME_GUEST:
			title = getString(R.string.refuse_become_guest);
		case TYPE_REFUSE_BECOME_HOST:
			title = getString(R.string.refuse_become_host);
			meetingId = getIntent().getStringExtra(KEY_MEETING_ID);
			user = (User) getIntent().getSerializableExtra(KEY_USER);
			break;
			
		case TYPE_TO_JOIN_TRIBE:
			title = getString(R.string.apply_into_tribe);
			meetingId = getIntent().getStringExtra(KEY_MEETING_ID);
			break;

		default:
			break;
		}
		mTitleBar.setLogo(R.drawable.selector_back_btn);
		etAddReason = (EditText) findViewById(R.id.et_reason);

		etAddReason.setHint(hint);
		mTitleBar.setTitleText(title);

		btnAdd = (Button) findViewById(R.id.btn_add);
		btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(etAddReason.getText().toString().trim())) {
					showToast(getString(R.string.input_can_not_be_empty));
					return;
				}
				forMeeting(type);
			}
		});
		resultListener = new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					setResult(RESULT_OK);
					AddReasonActivity.this.finish();
				} else {
					otherCondition(data.state, AddReasonActivity.this);
				}
			}
		};
	}

	private void addUser(final User user) {
		DamiInfo.requestAddFriend(user.uid, etAddReason.getText().toString(), "", new SimpleResponseListener(mContext,
				getString(R.string.request_internet_now)) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final RecommendAddResult data = (RecommendAddResult) o;
				if (data.state != null && data.state.code == 0) {
					Intent intent = new Intent();
					intent.putExtra(AddReasonActivity.KEY_USER, user);
					setResult(RESULT_OK, intent);
					showToast(getString(R.string.send_request_success));
					AddReasonActivity.this.finish();
				} else {
					this.otherCondition(data.state, AddReasonActivity.this);
				}
			}
		});
	}

	private void forMeeting(int type) {
		switch (type) {
		case TYPE_ADD_FRIEND:
			addUser(user);
			break;
		case TYPE_TO_BE_HOST:
			DamiInfo.applyhost(meetingId, etAddReason.getText().toString(), resultListener);
			break;
		case TYPE_TO_BE_GUEST:
			DamiInfo.applyguest(meetingId, etAddReason.getText().toString(), resultListener);
			break;
		case TYPE_TO_BE_CUSTOM:
			DamiInfo.resumeToMeetingJoiner(meetingId, etAddReason.getText().toString(), resultListener);
			break;
		case TYPE_DISMISS_MEETING:
			DamiInfo.cancelmeeting(meetingId, etAddReason.getText().toString(), resultListener);
			break;
		case TYPE_TO_JOIN_MEETING:
			DamiInfo.applyMeeting(meetingId, etAddReason.getText().toString(), resultListener);
			break;
		case TYPE_WAHT_COMUNICATION:
			DamiInfo.seekingContacts(messageInfo.from, messageInfo.id, etAddReason.getText().toString(), resultListener);
			break;
		case TYPE_REFUSE_COMUNICATION:
			DamiInfo.refuseSeekingContacts(messageInfo.from, messageInfo.id, etAddReason.getText().toString(),
					resultListener);
			break;

		case TYPE_REFUSE_JOIN_TRIBE:
			DamiInfo.refuseJoin(meetingId, user.uid, etAddReason.getText().toString(), resultListener);
			break;
		case TYPE_REFUSE_JOIN_MEETING:
			DamiInfo.refuseMeetingJoin(meetingId, user.uid, etAddReason.getText().toString(), resultListener);
			break;
		case TYPE_REFUSE_BECOME_GUEST:
			DamiInfo.refuseGuest(meetingId, user.uid, etAddReason.getText().toString(), resultListener);
			break;
		case TYPE_REFUSE_BECOME_HOST:
			DamiInfo.refuseHost(meetingId, user.uid, etAddReason.getText().toString(), resultListener);
			break;
			
		case TYPE_TO_JOIN_TRIBE:
			DamiInfo.applyTribe(meetingId, etAddReason.getText().toString(), resultListener);
			break;
			
		default:
			break;
		}
	}
}
