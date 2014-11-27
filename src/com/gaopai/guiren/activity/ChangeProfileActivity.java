package com.gaopai.guiren.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class ChangeProfileActivity extends BaseActivity {

	public final static String KEY_TYPE = "type";
	public final static int TYPE_EMAIL = 0;
	public final static int TYPE_PHONE = 1;
	public final static int TYPE_WEIXIN = 2;
	public final static int TYPE_WEIBO = 3;
	private int type;
	public final static String KEY_TEXT = "text";
	private String text;

	private EditText etText;
	private ImageButton btnDelete;

	private User mUser = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_change_profile);
		mUser = DamiCommon.getLoginResult(mContext);

		text = getIntent().getStringExtra(KEY_TEXT);
		type = getIntent().getIntExtra(KEY_TYPE, TYPE_EMAIL);

		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		View rightBtnView = mTitleBar.addRightTextView(R.string.save);
		rightBtnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(etText.getText().toString())) {
					showToast(R.string.input_can_not_be_empty);
					return;
				}
				String email = "", weibo = "", weixin = "", phone = "";
				switch (type) {
				case TYPE_EMAIL:
					email = etText.getText().toString();
					mUser.email = email;
					break;
				case TYPE_PHONE:
					phone = etText.getText().toString();
					mUser.phone = phone;
					break;
				case TYPE_WEIBO:
					weibo = etText.getText().toString();
					mUser.weibo = weibo;
					break;
				case TYPE_WEIXIN:
					weixin = etText.getText().toString();
					mUser.weixin = weixin;
					break;

				default:
					break;
				}
				DamiInfo.editProfile("", "", email, weibo, weixin, phone, new SimpleResponseListener(mContext,
						R.string.request_internet_now) {
					@Override
					public void onSuccess(Object o) {
						// TODO Auto-generated method stub
						BaseNetBean data = (BaseNetBean) o;
						if (data.state != null && data.state.code == 0) {
							ChangeProfileActivity.this.showToast("修改成功");
							DamiCommon.saveLoginResult(mContext, mUser);
							ChangeProfileActivity.this.setResult(RESULT_OK);
							ChangeProfileActivity.this.finish();
						} else {
							otherCondition(data.state, ChangeProfileActivity.this);
						}
					}
				});
			}
		});
		mTitleBar.setTitleText("资料修改");

		etText = (EditText) findViewById(R.id.et_change_profile);
		etText.setText(text);
		
		if (etText != null) {
			etText.setSelection(text.length());
		}
		btnDelete = (ImageButton) findViewById(R.id.btn_delete);
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etText.setText("");
			}
		});

	}
}
