package com.gaopai.guiren.activity.share;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import u.aly.ba;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.BaseFragment;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.ContactActivity;
import com.gaopai.guiren.activity.share.BaseShareFragment.OnBackListener;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.support.FragmentHelper;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.volley.SimpleResponseListener;

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

	private BaseShareFragment currentFragment;

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
		FragmentHelper.replaceFragment(R.id.fl_fragment_holder, getSupportFragmentManager(),
				ShareFollowersFragment.class);
	}

	public void setCurrentFragment(BaseShareFragment baseFragment) {
		this.currentFragment = baseFragment;
	}

	public boolean isShare() {
		return !(messageInfo == null);
	}

	private void initComponent() {
		// TODO Auto-generated method stub
		fragmentHodler = (FrameLayout) findViewById(R.id.fl_fragment_holder);
		etSearch = (EditText) findViewById(R.id.et_share_search);
		listener = new SimpleResponseListener(this, R.string.request_internet_now) {
			@Override
			public void onSuccess(Object o) {
				ShareActivity.this.finish();
			}
		};
		etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					if (etSearch.getText().length() == 0) {
						ShareActivity.this.showToast(getString(R.string.input_can_not_be_empty));
						return true;
					}
					BaseShareFragment fragment = (BaseShareFragment) getSupportFragmentManager().findFragmentById(
							R.id.fl_fragment_holder);
					if (fragment != null) {
						fragment.searchUser(etSearch.getText().toString());
					}
					return true;
				}
				return false;
			}
		});
	}

	public void setTitleText(int text) {
		mTitleBar.setTitleText(text);
	}

	public void toggleUser(User user) {
		if (userMap.containsKey(user.uid)) {
			userMap.remove(user.uid);
		} else {
			userMap.put(user.uid, user);
		}
		addRightTitleButton();
	}

	private TextView rightButtonView;

	public void addRightTitleButton() {
		if (rightButtonView == null) {
			rightButtonView = mTitleBar.addRightTextView("确定(" + userMap.size() + ")");
			rightButtonView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialog("确认邀请", "", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							invite(getCheckedId(), tribeId);
						}
					});
				}
			});
		}
		if (userMap.size() == 0) {
			rightButtonView.setVisibility(View.GONE);
		} else {
			rightButtonView.setVisibility(View.VISIBLE);
		}
		rightButtonView.setText("确定(" + userMap.size() + ")");
	}

	public String getCheckedId() {
		String result = "";
		StringBuffer sb = new StringBuffer();
		Set<String> keyset = userMap.keySet();
		for (String key : keyset) {
			sb.append(key + ",");
		}
		if (sb.length() > 0) {
			result = sb.substring(0, sb.length() - 1);
		}
		return result;
	}

	private SimpleResponseListener listener;

	private void invite(String uid, String tribeid) {
		switch (type) {
		case ShareActivity.TYPE_INVITE_USER:
			DamiInfo.sendMeetingInvite(tribeid, uid, listener);
			break;
		case ShareActivity.TYPE_INVITE_HOST:
			DamiInfo.invitemeeting(tribeid, uid, 2, listener);
			break;
		case ShareActivity.TYPE_INVITE_GUEST:
			DamiInfo.invitemeeting(tribeid, uid, 3, listener);
			break;
		case ShareActivity.TYPE_INVITE_TRIBE:
			DamiInfo.sendInvite(tribeid, uid, listener);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		default:
			break;
		}
	}

	public static interface CancelInterface {
		public void cancel(String s);
	}

	public OnBackListener backListener;

	public void setBackListener(OnBackListener backListener) {
		this.backListener = backListener;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (backListener != null && backListener.onBack()) {
			return;
		}
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_fragment_holder);
		if (fragment instanceof ShareFollowersFragment) {
			this.finish();
			return;
		}
		super.onBackPressed();
	}
}
