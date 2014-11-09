package com.gaopai.guiren.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.chat.ChatMessageActivity;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.UserInfoBean;
import com.gaopai.guiren.bean.UserList;
import com.gaopai.guiren.utils.StringUtils;
import com.gaopai.guiren.volley.IResponseListener;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class UserInfoActivity extends BaseActivity implements OnClickListener {

	public static final String KEY_UID = "uid";
	private String uid;
	private TextView tvUserName;
	private TextView tvUserInfo;
	private TextView tvPhone;

	private User mUser;

	private Button btnSendMsgButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_user_info);
		initView();

		uid = getIntent().getStringExtra(KEY_UID);
		if (TextUtils.isEmpty(uid)) {
			Uri data = getIntent().getData();
			uid = data.toString().substring(data.toString().indexOf("//") + 2);
		}
		mTitleBar.setTitleText("详细资料");
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		DamiInfo.getUserInfo(uid, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final UserInfoBean data = (UserInfoBean) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null) {
						mUser = data.data;
						bindView(mUser);
					}
				} else {
					otherCondition(data.state, UserInfoActivity.this);
				}
			}
		});

	}

	private void initView() {
		// TODO Auto-generated method stub
		tvUserName = (TextView) findViewById(R.id.tv_user_name);
		tvUserInfo = (TextView) findViewById(R.id.tv_user_info);
		tvPhone = (TextView) findViewById(R.id.tv_user_phone);
		btnSendMsgButton = (Button) findViewById(R.id.btn_send_msg);
		btnSendMsgButton.setOnClickListener(this);
	}

	private void bindView(User user) {
		tvUserName.setText(user.nickname);
		tvUserInfo.setText(user.nickname);
		tvPhone.setText("手机号码：" + user.phone);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_send_msg:
			Intent intent = new Intent();
			intent.putExtra(ChatMessageActivity.KEY_USER, mUser);
			intent.setClass(mContext, ChatMessageActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

}
