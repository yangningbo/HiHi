package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.TagBean;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.bean.net.TagResult;
import com.gaopai.guiren.support.CameralHelper;
import com.gaopai.guiren.support.ImageCrop;
import com.gaopai.guiren.support.TagWindowManager;
import com.gaopai.guiren.support.TagWindowManager.TagCallback;
import com.gaopai.guiren.support.TextLimitWatcher;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.utils.ViewUtil.OnTextChangedListener;
import com.gaopai.guiren.view.FlowLayout;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class CreatTribeActivity extends BaseActivity implements OnClickListener {

	private static final int MIN_LEN = 2;
	private static final int MAX_LEN = 15;
	private ImageView btnUploadPic;
	private Button btnCreat;
	private EditText etTitle;
	private EditText etInfo;
	private TextView tvEditTags;
	private TextView tvNumLimit;
	private FlowLayout layoutTags;

	private TextView tvSetPassword;
	private EditText etPassword;
	private EditText etPasswordAgain;
	private View layoutPrivacySetting;
	private TextView tvPrivacySetting;
	private View layoutPasswordSetting;

	private boolean isSetPassword = false;
	private int mPrivacy = 1;
	private String mFilePath = "";
	private String mTags = "";

	private TagWindowManager tagWindowManager;
	private List<TagBean> recTagList = new ArrayList<TagBean>();

	public final static String KEY_TRIBE = "tribe";
	private boolean isEdit = false;
	private Tribe mTribe;

	private CameralHelper cameralHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_creat_tribe);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.create_tribe);
		mTribe = (Tribe) getIntent().getSerializableExtra(KEY_TRIBE);
		if (mTribe != null) {
			isEdit = true;
		}
		initComponent();
		tagWindowManager = new TagWindowManager(this, true, tagCallback);
		getTags();
		if (isEdit) {
			bindEditView();
		}
		cameralHelper = new CameralHelper(this, new CameralHelper.Option(1, true, ImageCrop.HEADER_WIDTH,
				ImageCrop.HEADER_HEIGHT));
		cameralHelper.setCallback(new CameralHelper.SimpleCallback() {
			@Override
			public void receiveCropPic(String path) {
				setPic(path);
			}
		});
	}

	private void bindEditView() {
		mTitleBar.setTitleText(R.string.edit_tribe);
		ImageLoaderUtil.displayImage(mTribe.logolarge, btnUploadPic, R.drawable.default_tribe);
		etTitle.setText(mTribe.name);
		if (mTribe.type == 1) {
			mPrivacy = 1;
		} else {
			mPrivacy = 2;
		}
		tvPrivacySetting.setText(mPrivacy == 1 ? getString(R.string.privacy_setting_open)
				: getString(R.string.privacy_setting_close));
		etInfo.setText(mTribe.content);
	}

	private void getTags() {
		DamiInfo.getTags("quanzi", new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				TagResult data = (TagResult) o;
				if (data.state != null && data.state.code == 0) {
					recTagList = data.data;
					tagWindowManager.setRecTagList(data.data);
				} else {
					this.otherCondition(data.state, CreatTribeActivity.this);
				}
			}
		});
	}

	private TagWindowManager.TagCallback tagCallback = new TagCallback() {

		@Override
		public void onSave(String tags) {
			// TODO Auto-generated method stub
			mTags = tags;
			tagWindowManager.bindTags(layoutTags, false);
		}
	};

	private void initComponent() {
		btnUploadPic = (ImageView) findViewById(R.id.btn_upload_pic);
		btnUploadPic.setOnClickListener(this);

		btnCreat = (Button) findViewById(R.id.btn_creat);
		btnCreat.setOnClickListener(this);
		if (isEdit) {
			btnCreat.setText(R.string.edit_tribe);
		}
		etInfo = (EditText) findViewById(R.id.et_tribe_info);
		etTitle = (EditText) findViewById(R.id.et_tribe_title);
		tvEditTags = ViewUtil.findViewById(this, R.id.tv_edit_tag);
		tvEditTags.setOnClickListener(this);
		tvSetPassword = ViewUtil.findViewById(this, R.id.tv_set_password);
		tvSetPassword.setOnClickListener(this);
		etPassword = ViewUtil.findViewById(this, R.id.et_enter_password);
		etPasswordAgain = ViewUtil.findViewById(this, R.id.et_enter_password_again);
		layoutPrivacySetting = ViewUtil.findViewById(this, R.id.layout_privacy_setting);
		layoutPrivacySetting.setOnClickListener(this);
		layoutPasswordSetting = ViewUtil.findViewById(this, R.id.layout_password);

		tvPrivacySetting = ViewUtil.findViewById(this, R.id.tv_privacy_setting);

		tvNumLimit = ViewUtil.findViewById(this, R.id.tv_num_limit);
		layoutTags = ViewUtil.findViewById(this, R.id.layout_tags);
		showPasswordView();

		etInfo.addTextChangedListener(new TextLimitWatcher(tvNumLimit, 500));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_upload_pic:
			cameralHelper.btnPhotoAction();
			break;
		case R.id.btn_creat:
			creatTribe();
			break;
		case R.id.layout_privacy_setting:
			showPrivacyDialog();
			break;
		case R.id.tv_set_password:
			isSetPassword = !isSetPassword;
			showPasswordView();
			break;
		case R.id.tv_edit_tag:
			tagWindowManager.showTagsWindow();
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

	private void setPic(String path) {
		mFilePath = path;
		Drawable drawable = Drawable.createFromPath(path);
		btnUploadPic.setImageDrawable(drawable);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		cameralHelper.onActivityResult(requestCode, resultCode, data);
	}

	private void creatTribe() {
		final String title = etTitle.getText().toString();
		final String info = etInfo.getText().toString();
		final String password = etPassword.getText().toString();
		final String passwordAgain = etPasswordAgain.getText().toString();

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
					CreatTribeActivity.this.finish();
				} else {
					this.otherCondition(data.state, CreatTribeActivity.this);
				}
			}
		};

		if (isEdit) {
			DamiInfo.editTribe(mTribe.id, title, mFilePath, String.valueOf(mPrivacy), info, mTags, password, listener);
		} else {
			DamiInfo.addTribe(title, mFilePath, String.valueOf(mPrivacy), info, mTags, password, listener);
		}
	}

}
