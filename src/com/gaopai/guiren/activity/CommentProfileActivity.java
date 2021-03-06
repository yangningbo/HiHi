package com.gaopai.guiren.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class CommentProfileActivity extends BaseActivity {

	public final static String KEY_USER = "user";

	private EditText etText;
	private Button btnComment;

	private User mUser = null;
	private User tUser = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stubs
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_comment_profile);
		mUser = DamiCommon.getLoginResult(mContext);

		tUser = (User) getIntent().getSerializableExtra(KEY_USER);

		mTitleBar.setTitleText("评论");
		mTitleBar.setLogo(R.drawable.selector_back_btn);

		etText = (EditText) findViewById(R.id.et_change_profile);
		btnComment = (Button) findViewById(R.id.btn_send);
		btnComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(etText.getText())) {
					showToast(R.string.input_can_not_be_empty);
					return;
				}
				DamiInfo.addProfileComment("0", 5, tUser.uid, etText.getText().toString(), 0, mUser.displayName,
						tUser.displayName, "2", new SimpleResponseListener(mContext, R.string.request_internet_now) {

							@Override
							public void onSuccess(Object o) {
								// TODO Auto-generated method stub
								BaseNetBean data = (BaseNetBean) o;
								if (data.state!=null && data.state.code == 0) {
									showToast(R.string.comment_success);
								} else {
									otherCondition(data.state, CommentProfileActivity.this);
								}
							}
						});
			}
		});
	}
}
