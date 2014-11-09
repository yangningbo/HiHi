package com.gaopai.guiren.activity.share;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.MyUtils;

public class ShareActivity extends BaseActivity implements OnClickListener {

	private FrameLayout fragmentHodler;
	private EditText etSearch;
	private HorizontalScrollView hsImageHolder;
	private LinearLayout layoutImageHolder;

	private List<User> userList = new ArrayList<User>();

	public HashMap<String, User> userMap = new HashMap<String, User>();

	private boolean isEditEmpty = true;

	public final static String KEY_TYPE = "type";

	public final static int TYPE_SHARE = 0;
	public final static int TYPE_INVITE_USER = 1;
	public final static int TYPE_INVITE_GUEST = 2;
	public final static int TYPE_INVITE_HOST = 3;
	public final static int TYPE_INVITE_TRIBE = 4;

	public int type;

	public static final String KEY_MESSAGE = "message";
	public static final String KEY_TRIBE_ID = "tribe_id";
	public MessageInfo messageInfo;
	public String tribeId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_share);

		type = getIntent().getIntExtra(KEY_TYPE, TYPE_SHARE);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		initComponent();
		messageInfo = (MessageInfo) getIntent().getSerializableExtra(KEY_MESSAGE);
		tribeId = (String) getIntent().getSerializableExtra(KEY_TRIBE_ID);
		ShareFollowersFragment shareFollowersFragment = new ShareFollowersFragment();

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fl_fragment_holder, shareFollowersFragment, ShareFollowersFragment.class.getName())
				.addToBackStack(null).commit();
	}

	private void initComponent() {
		// TODO Auto-generated method stub
		fragmentHodler = (FrameLayout) findViewById(R.id.fl_fragment_holder);
		etSearch = (EditText) findViewById(R.id.et_share_search);
		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				cancelCallback.cancel(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		default:
			break;
		}
	}

	private CancelInterface cancelCallback;

	public void setCancelCallback(CancelInterface callback) {
		cancelCallback = callback;
	}

	public static interface CancelInterface {
		public void cancel(String s);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
			this.finish();
			return;
		}
		super.onBackPressed();
	}
}
