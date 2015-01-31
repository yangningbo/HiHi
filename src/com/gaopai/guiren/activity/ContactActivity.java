package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.ContactAdapter;
import com.gaopai.guiren.adapter.CopyOfConnectionAdapter.Item;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.UserList;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshIndexableListView;
import com.gaopai.guiren.volley.SimpleResponseListener;
import com.gaopai.guiren.widget.indexlist.IndexableListView;
import com.gaopai.guiren.widget.indexlist.SingleIndexScroller;

public class ContactActivity extends BaseActivity implements OnClickListener {
	public final static int TYPE_FOLLOWERS = 0;
	public final static int TYPE_FANS = 1;
	public final static String KEY_TYPE = "type";
	public final static String KEY_UID = "uid";

	private int type;
	private String uid;
	private User mLogin;
	private boolean isMyself = false;
	private PullToRefreshIndexableListView mListView;
	private ContactAdapter mAdapter;
	private SingleIndexScroller indexScroller;
	private EditText etSearch;
	private boolean isSearchMode = false;
	public List<User> mSearchUserList = new ArrayList<User>();

	public final static String ACTION_UPDATE_LIST_ADD = "com.gaopai.guiren.ACTION_UPDATE_LIST_ADD";
	public final static String ACTION_UPDATE_LIST_DELETE = "com.gaopai.guiren.ACTION_UPDATE_LIST_DELETE";

	private Button btnSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		type = getIntent().getIntExtra(KEY_TYPE, TYPE_FANS);
		uid = getIntent().getStringExtra(KEY_UID);
		mLogin = DamiCommon.getLoginResult(mContext);
		isMyself = mLogin.uid.equals(uid);
		initTitleBar();
		setAbContentView(R.layout.activity_contact_list);

		btnSearch = (Button) mTitleBar.addRightButtonView(R.drawable.selector_titlebar_search);
		btnSearch.setLayoutParams(mTitleBar.layoutParamsWW);
		btnSearch.setId(R.id.ab_search);
		btnSearch.setOnClickListener(this);

		mListView = (PullToRefreshIndexableListView) findViewById(R.id.listView);
		if (type == TYPE_FANS) {
			if (isMyself) {
				mTitleBar.setTitleText(getString(R.string.my_fans));
			} else {
				mTitleBar.setTitleText(getString(R.string.his_fans));
			}
		} else {
			if (isMyself) {
				mTitleBar.setTitleText(getString(R.string.my_follow));
			} else {
				mTitleBar.setTitleText(getString(R.string.his_follow));
			}
			mListView.getRefreshableView().addHeaderView(creatHeaderView());

		}
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);

		indexScroller = (SingleIndexScroller) findViewById(R.id.scroller);
		etSearch = mTitleBar.addContactSearchEditText();
		etSearch.setVisibility(View.GONE);
		etSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (isSearchMode && s.length() > 0) {
					return;
				}
				isSearchMode = false;
				mAdapter.getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND
						|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						if (etSearch.getText().length() == 0) {
							ContactActivity.this.showToast(getString(R.string.input_can_not_be_empty));
							return true;
						}
						isSearchMode = true;
						searchListPage = 1;
						isFullSearch = false;
						mSearchUserList.clear();
						getUserList();
						return true;
					}
				}
				return false;
			}
		});

		mListView.getRefreshableView().addHeaderView(new View(mContext));
		mListView.getRefreshableView().setVerticalScrollBarEnabled(false);
		mListView.setPullRefreshEnabled(false);
		mListView.setPullLoadEnabled(false);
		mListView.setScrollLoadEnabled(true);
		mListView.setOnRefreshListener(new OnRefreshListener<IndexableListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<IndexableListView> refreshView) {
				getUserList();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<IndexableListView> refreshView) {
				// TODO Auto-generated method stub
				if (isSearchMode) {
					if (isFullSearch) {
						mListView.onPullComplete();
						return;
					}
				} else {
					if (isFullList) {
						mListView.onPullComplete();
						return;
					}
				}
				getUserList();
			}
		});
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int pos = position - mListView.getRefreshableView().getHeaderViewsCount();
				if (!(mAdapter.getItem(pos) instanceof Item)) {
					return;
				}
				User user = ((Item) mAdapter.getItem(pos)).user;
				if (user.isguirenuser == 0) {
					startActivity(FakeProfileActivity.getIntent(mContext, user));
				} else {
					startActivity(ProfileActivity.getIntent(mContext, user.uid));
				}
			}
		});

		mAdapter = new ContactAdapter(inviteClickListener);
		mListView.setAdapter(mAdapter);
		mListView.getRefreshableView().setFastScrollEnabled(false);
		indexScroller.setListView(mListView.getRefreshableView());
		mListView.doPullRefreshing(true, 0);
	}

	private TextView tvFansCount;

	private View creatHeaderView() {
		View view = mInflater.inflate(R.layout.layout_contact_header, null);
		tvFansCount = ViewUtil.findViewById(view, R.id.tv_fans_count);
		tvFansCount.setText(String.valueOf(mLogin.fansers));
		TextView tvFans = (TextView) ViewUtil.findViewById(view, R.id.tv_fans_title);
		if (isMyself) {
			tvFans.setText(R.string.my_fans);
		} else {
			tvFans.setText(R.string.his_fans);
		}
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(ContactActivity.getIntent(mContext, TYPE_FANS, uid));
			}
		});
		return view;
	}

	@Override
	protected void registerReceiver(IntentFilter intentFilter) {
		super.registerReceiver(intentFilter);
		intentFilter.addAction(ACTION_UPDATE_LIST_ADD);
		intentFilter.addAction(ACTION_UPDATE_LIST_DELETE);
	}

	// send broadcast in ProfileActivity
	@Override
	protected void onReceive(Intent intent) {
		if (intent.getAction().equals(ACTION_UPDATE_LIST_ADD)) {
			User user = (User) intent.getSerializableExtra("user");
			if (user != null) {
				if (type == TYPE_FOLLOWERS) {
					mAdapter.addUser(user);
				}
			}
		} else if (intent.getAction().equals(ACTION_UPDATE_LIST_DELETE)) {
			String uid = intent.getStringExtra("uid");
			if (uid != null) {
				if (type == TYPE_FOLLOWERS) {
					mAdapter.removeUser(uid);
				}
			}
		}
	}

	public static Intent getDeleteBroadcastIntent(String uid) {
		Intent intent = new Intent(ACTION_UPDATE_LIST_DELETE);
		intent.putExtra("uid", uid);
		return intent;
	}

	public static Intent getAddBroadcastIntent(User user) {
		Intent intent = new Intent(ACTION_UPDATE_LIST_ADD);
		intent.putExtra("user", user);
		return intent;
	}

	public static Intent getIntent(Context context, int type, String uid) {
		Intent intent = new Intent(context, ContactActivity.class);
		intent.putExtra(KEY_TYPE, type);
		intent.putExtra(KEY_UID, uid);
		return intent;
	}

	private int page = 1;

	private void getUserList() {
		page = listPage;
		if (isSearchMode) {
			page = searchListPage;
		}
		if (type == TYPE_FANS) {
			DamiInfo.getFansList(uid, page, etSearch.getText().toString(), new MyListener(mContext));
		} else {
			DamiInfo.getFollowerList(uid, page, etSearch.getText().toString(), new MyListener(mContext));
		}
	}

	public void hideIndexedTextWhenChangePage() {
		indexScroller.hide();
	}

	private int searchListPage = 1;
	private int listPage = 1;
	private boolean isFullSearch = false;
	private boolean isFullList = false;

	class MyListener extends SimpleResponseListener {

		public MyListener(Context context) {
			super(context);
		}

		@Override
		public void onSuccess(Object o) {
			// TODO Auto-generated method stub
			final UserList data = (UserList) o;
			if (data.state != null && data.state.code == 0) {
				if (data.data != null) {
					// sortData(data.data);
					if (isSearchMode) {
						mSearchUserList.addAll(data.data);
						mAdapter.sortData(mSearchUserList);
					} else {
						mAdapter.addAndSort(data.data);
					}
				}

				if (data.pageInfo != null) {
					if (isSearchMode) {
						isFullSearch = (data.pageInfo.hasMore == 0);
						if (!isFullSearch) {
							searchListPage++;
						}
						mListView.setHasMoreData(!isFullSearch);
					} else {
						isFullList = (data.pageInfo.hasMore == 0);
						if (!isFullList) {
							listPage++;
						}
						mListView.setHasMoreData(!isFullList);
					}
				}

			} else {
				otherCondition(data.state, ContactActivity.this);
			}
		}

		@Override
		public void onFinish() {
			mListView.onPullComplete();
			mListView.setHasMoreData(!isFullList);
		}
	}

	private boolean isOpenSearch = false;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ab_search:
			if (isOpenSearch) {
				btnSearch.setBackgroundResource(R.drawable.selector_titlebar_search);
				btnSearch.setText("");
				etSearch.setVisibility(View.GONE);
				mTitleBar.titleTextBtn.setVisibility(View.VISIBLE);
			} else {
				btnSearch.setBackgroundResource(R.drawable.transparent);
				btnSearch.setText(R.string.cancel);
				etSearch.setVisibility(View.VISIBLE);
				mTitleBar.titleTextBtn.setVisibility(View.GONE);
				etSearch.requestFocus();
			}
			isOpenSearch = !isOpenSearch;
			break;
		default:
			break;
		}
	}

	private OnClickListener inviteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!User.checkCanInvite(mLogin, ContactActivity.this)) {
				return;
			}
			String phone = (String) v.getTag();
			if (TextUtils.isEmpty(phone)) {
				return;
			}
			FakeProfileActivity.getInviteUrl(mContext, phone);
		}
	};

}
