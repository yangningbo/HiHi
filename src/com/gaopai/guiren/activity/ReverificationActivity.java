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

//认证界面
public class ReverificationActivity extends BaseActivity {

	private EditText etName;
	private EditText etCompany;
	private EditText etPartment;
	private EditText etJob;
	private Button btnVerificaion;

	private User mUser = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_verification_profile);
		mUser = DamiCommon.getLoginResult(this);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.click_verify);

		etName = (EditText) findViewById(R.id.et_real_name);
		etCompany = (EditText) findViewById(R.id.et_company);
		etPartment = (EditText) findViewById(R.id.et_partment);
		etJob = (EditText) findViewById(R.id.et_job);

		btnVerificaion = (Button) findViewById(R.id.btn_verificaition);
		btnVerificaion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(etName.getText().toString()) || TextUtils.isEmpty(etCompany.getText().toString())
						|| TextUtils.isEmpty(etPartment.getText().toString())
						|| TextUtils.isEmpty(etJob.getText().toString())) {
					showToast(R.string.input_can_not_be_empty);
					return;
				}
				DamiInfo.reAuth(etPartment.getText().toString(), etName.getText().toString(), etCompany.getText()
						.toString(), etJob.getText().toString(), new SimpleResponseListener(mContext,
						R.string.request_internet_now) {
					@Override
					public void onSuccess(Object o) {
						// TODO Auto-generated method stub
						BaseNetBean data = (BaseNetBean) o;
						if (data.state != null && data.state.code == 0) {
							ReverificationActivity.this.showToast("申请成功，正在接受审核");
							ReverificationActivity.this.finish();
						} else {
							otherCondition(data.state, ReverificationActivity.this);
						}
					}
				});
			}
		});

		etName.setText(mUser.realname);
		etCompany.setText(mUser.company);
		etPartment.setText(mUser.depa);
		etJob.setText(mUser.post);
	}
}
