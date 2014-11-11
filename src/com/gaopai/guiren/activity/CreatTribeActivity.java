package com.gaopai.guiren.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.net.AddMeetingResult;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class CreatTribeActivity extends BaseActivity implements OnClickListener {
	
	private static final int MIN_LEN = 2;
	private static final int MAX_LEN = 15;
	private Button btnUploadPic;
	private Button btnCreat;
	private EditText etTitle;
	private EditText etInfo;
	private EditText etTags;
	private EditText etPassword;

	private ArrayAdapter spAdapter;
	private Spinner spPrivacy;

	private int mPrivacy = 1;

	private String mFilePath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_creat_tribe);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText("创建圈子");
		btnUploadPic = (Button) findViewById(R.id.btn_upload_pic);
		btnUploadPic.setOnClickListener(this);
		spAdapter = ArrayAdapter.createFromResource(mContext, R.array.spinner_creat_meeting_item,
				android.R.layout.simple_spinner_dropdown_item);
		spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		spPrivacy = (Spinner) findViewById(R.id.sp_privacy_setting);
//		spPrivacy.setAdapter(spAdapter);
//		spPrivacy.setOnItemSelectedListener(new SpItemSelectedListener());
		btnCreat = (Button) findViewById(R.id.btn_creat);
		btnCreat.setOnClickListener(this);

		etInfo = (EditText) findViewById(R.id.et_tribe_info);
		etTitle = (EditText) findViewById(R.id.et_tribe_title);
//		etPassword = (EditText) findViewById(R.id.et_tribe_password);
		etTags = (EditText) findViewById(R.id.et_tribe_tags);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_upload_pic:
			Intent intent = getIntent();
			intent.setClass(mContext, LocalPicPathActivity.class);
			intent.putExtra(LocalPicPathActivity.KEY_PIC_REQUIRE_TYPE, LocalPicPathActivity.PIC_REQUIRE_SINGLE);
			startActivityForResult(intent, LocalPicPathActivity.REQUEST_CODE_PIC);
			break;
		case R.id.btn_creat:
			creatTribe();
			break;
		default:
			break;
		}
	}

	private class SpItemSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			mPrivacy = position + 1;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LocalPicPathActivity.REQUEST_CODE_PIC) {
			if (resultCode == RESULT_OK) {
				List<String> pathList = data.getStringArrayListExtra(LocalPicActivity.KEY_PIC_SELECT_PATH_LIST);
				Drawable drawable = Drawable.createFromPath(pathList.get(0));
				btnUploadPic.setBackgroundDrawable(drawable);
			}
		}
	}

	private void creatTribe() {
		final String title = etTitle.getText().toString();
		final String info = etInfo.getText().toString();
		final String password = etPassword.getText().toString();
		final String tag = etTags.getText().toString();
		
		if (TextUtils.isEmpty(title)) {
			String prompt = mContext.getString(R.string.please_input) + mContext.getString(R.string.tribe_name);
			showToast(prompt);
			return;
		}

		if (title.length() < MIN_LEN) {
			String prompt = mContext.getString(R.string.tribe_name_too_short);
			showToast(prompt);
			return;
		}

		if (title.length() > MAX_LEN) {
			String prompt = mContext.getString(R.string.tribe_name_too_long);
			showToast(prompt);
			return;
		}


		if (TextUtils.isEmpty(info)) {
			String prompt = mContext.getString(R.string.please_input) + mContext.getString(R.string.tribe_content);
			showToast(prompt);
			return;
		}

		DamiInfo.addTribe(title, mFilePath, String.valueOf(mPrivacy), info, tag, password, new SimpleResponseListener(
				mContext, "正在请求") {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				AddMeetingResult data = (AddMeetingResult) o;
				if (data.state != null && data.state.code == 0) {
					showToast("创建成功");
					CreatTribeActivity.this.finish();
				} else {
					this.otherCondition(data.state, CreatTribeActivity.this);
				}
			}
		});
	}

}
