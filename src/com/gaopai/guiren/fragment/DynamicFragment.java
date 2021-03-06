package com.gaopai.guiren.fragment;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.BaseFragment;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.DynamicDetailActivity;
import com.gaopai.guiren.activity.MainActivity;
import com.gaopai.guiren.activity.NewDynamicActivity;
import com.gaopai.guiren.adapter.DynamicAdapter;
import com.gaopai.guiren.bean.dynamic.DynamicBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.support.DynamicHelper;
import com.gaopai.guiren.support.chat.ChatBoxManager;
import com.gaopai.guiren.support.view.CustomEditText;
import com.gaopai.guiren.support.view.HandleTouchLinearLayout;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;
import com.gaopai.guiren.widget.emotion.EmotionPicker;

public class DynamicFragment extends BaseFragment implements OnClickListener {

	@ViewInject(id = R.id.listview)
	private PullToRefreshListView mListView;
	private DynamicAdapter mAdapter;
	private String TAG = DynamicFragment.class.getName();

	@ViewInject(id = R.id.chat_box)
	private View chatBox;
	@ViewInject(id = R.id.chat_box_edit_keyword)
	private CustomEditText etComment;
	private ChatBoxManager chaBoxManager;
	private EmotionPicker emotionPicker;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mView == null) {
			mView = mInflater.inflate(R.layout.fragment_dynamic, null);
			((MainActivity) getActivity()).setBackPressedListener(onBackPressedListener);
			FinalActivity.initInjectedView(this, mView);
			initView(mView);
		}
		return mView;
	}

	private OnTouchListener hideKeyboardListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			hideChatBox();
			return false;
		}
	};

	private void initView(View view) {
		((HandleTouchLinearLayout) mListView).setActionDownTouchListener(hideKeyboardListener);
		mListView.setPullLoadEnabled(true);
		mListView.setPullRefreshEnabled(true);
		mListView.setScrollLoadEnabled(false);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getDynamicList(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				getDynamicList(false);
			}
		});

		addNewDyHeader();
		mAdapter = new DynamicAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if (view instanceof TextView) { // the error view
					return;
				}
				if (position == 0) {
					tvDyCount.setVisibility(View.GONE);
					startActivity(NewDynamicActivity.class);
					return;
				}
				int pos = position - mListView.getRefreshableView().getHeaderViewsCount();
				mAdapter.viewDynamicDetail((TypeHolder) mAdapter.getItem(pos));
			}
		});
		mListView.getRefreshableView().setRecyclerListener(mAdapter.getRecyleListener());
		ViewUtil.findViewById(view, R.id.send_text_btn).setOnClickListener(this);
		Button emotionBtn = ViewUtil.findViewById(view, R.id.emotion_btn);
		emotionBtn.setOnClickListener(this);
		emotionBtn.setVisibility(View.VISIBLE);
		emotionPicker = ViewUtil.findViewById(view, R.id.emotion_picker);
		emotionPicker.setEditText(getActivity(), null, etComment);
		chaBoxManager = new ChatBoxManager(getActivity(), etComment, emotionPicker, emotionBtn);
		ViewUtil.findViewById(view, R.id.chat_box_btn_switch_voice_text).setOnClickListener(this);
		etComment.setBackPressedListener(onBackPressedListener);
		etComment.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
				} else {
					hideChatBox();
				}
			}
		});
		etComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chaBoxManager.editClick();
			}
		});
		registerReceiver(DynamicHelper.ACTION_REFRESH_DYNAMIC, Intent.ACTION_SCREEN_OFF,
				MainActivity.LOGIN_SUCCESS_ACTION, DynamicDetailActivity.ACTION_UPDATE_DYNAMIC_LIST,
				MainActivity.ACTION_UPDATE_PROFILE);
	}

	private boolean isInitialed = false;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (!isInitialed) {
				mListView.doPullRefreshing(true, 0);
				isInitialed = true;
			}
		}
	}

	private void lockListViewHeight() {
		LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) this.mListView.getLayoutParams();
		Logger.d(this, "listheight = " + mListView.getHeight());
		Logger.d(this, "diffheight = " + emotionPicker.getEmotionPickerKeyboardHeightDiff(getActivity()));
		localLayoutParams.height = mListView.getHeight()
				- emotionPicker.getEmotionPickerKeyboardHeightDiff(getActivity());
		localLayoutParams.weight = 0.0F;
	}

	private void unLockListViewHeight() {
		((LinearLayout.LayoutParams) this.mListView.getLayoutParams()).weight = 1.0f;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.emotion_btn:
			chaBoxManager.emotionClick();
			break;
		case R.id.send_text_btn:
			if (etComment.getText().length() == 0) {
				((BaseActivity) getActivity()).showToast(R.string.input_can_not_be_empty);
				return;
			}
			String teString = etComment.getText().toString();
			mAdapter.commentMessage(teString, (TypeHolder) etComment.getTag());
			break;
		case R.id.chat_box_btn_switch_voice_text:
			hideChatBox();
			break;

		default:
			break;
		}
	}

	private int page = 1;
	private boolean isFull = false;

	private void getDynamicList(final boolean isRefresh) {
		if (isRefresh) {
			page = 1;
			isFull = false;
		}
		if (isFull) {
			mListView.setHasMoreData(!isFull);
			return;
		}

		DamiInfo.getDynamic("0", page, new SimpleResponseListener(getActivity()) {
			@Override
			public void onSuccess(Object o) {
				final DynamicBean data = (DynamicBean) o;
				if (data.state != null && data.state.code == 0) {
					setHeaderCount(data.state.newalertcount);
					if (data.data != null && data.data.size() > 0) {
						if (isRefresh) {
							mAdapter.clear();
						}
						mAdapter.addAll(data.data);
					}
					if (data.pageInfo != null) {
						isFull = (data.pageInfo.hasMore == 0);
						if (!isFull) {
							page++;
						}
					}
					mListView.setHasMoreData(!isFull);
				} else {
					otherCondition(data.state, getActivity());
				}
			}

			@Override
			public void onFinish() {
				Logger.d(this, "onPullComplete");
				mListView.onPullComplete();
			}
		});
	}

	public void showChatBox(String name, TypeHolder typeHolder, boolean showReply) {
		chatBox.setVisibility(View.VISIBLE);
		etComment.requestFocus();
		etComment.setTag(typeHolder);
		if (showReply) {
			etComment.setHint("回复：" + name);
		} else {
			etComment.setHint("请输入评论");
		}
		((MainActivity) getActivity()).hideBottomTab();
	}

	public void hideChatBox() {
		if (chatBox == null || chaBoxManager == null) {
			return;
		}
		if (chatBox.getVisibility() == View.GONE) {
			return;
		}
		chaBoxManager.hideEmotion();
		chatBox.setVisibility(View.GONE);
		etComment.setText("");
		hideSoftKeyboard(etComment);
		((MainActivity) getActivity()).showBottomTab();
	}

	private BackPressedListener onBackPressedListener = new BackPressedListener() {
		@Override
		public boolean onBack() {
			// TODO Auto-generated method stub
			if (chatBox.getVisibility() == View.VISIBLE) {
				hideChatBox();
				return true;
			}
			return false;
		}
	};

	public static interface BackPressedListener {
		public boolean onBack();
	}

	private TextView tvDyCount;

	private void addNewDyHeader() {
		tvDyCount = new TextView(getActivity());

		tvDyCount.setTextColor(getResources().getColor(R.color.general_text_blue));
		tvDyCount.setBackgroundResource(R.drawable.selector_btn_round_gray);
		int padding = MyUtils.dip2px(getActivity(), 5);
		tvDyCount.setPadding(2 * padding, padding, 2 * padding, padding);
		tvDyCount.setGravity(Gravity.CENTER);
		tvDyCount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tvDyCount.setVisibility(View.GONE);
				startActivity(NewDynamicActivity.class);
			}
		});
		tvDyCount.setVisibility(View.GONE);
		LinearLayout layout = new LinearLayout(getActivity());
		LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(padding, padding, padding, padding);
		layout.setGravity(Gravity.CENTER);
		layout.addView(tvDyCount, lp);
		mListView.getRefreshableView().addHeaderView(layout);
	}

	private void setHeaderCount(int newalertcount) {
		if (newalertcount == 0) {
			tvDyCount.setVisibility(View.GONE);
			return;
		}
		tvDyCount.setVisibility(View.VISIBLE);
		tvDyCount.setText("您有" + newalertcount + "条新消息");
	}

	public void hideSoftKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	@Override
	protected void onReceive(Intent intent) {
		// TODO Auto-generated method stub
		if (intent == null) {
			return;
		}
		String action = intent.getAction();
		if (TextUtils.isEmpty(action)) {
			return;
		}
		if (action.equals(DynamicHelper.ACTION_REFRESH_DYNAMIC)) {
			String id = intent.getStringExtra("id");
			mAdapter.deleteItem(id);
		} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
			mAdapter.stopPlayVoice();
		} else if (action.equals(MainActivity.LOGIN_SUCCESS_ACTION)) {
			if (mAdapter != null) {
				mAdapter.clear();
				mAdapter.notifyDataSetChanged();
				/**
				 * when user swipe to this interface, force to refresh the data,
				 * see {@link #setUserVisibleHint}
				 */
				isInitialed = false;
				mAdapter.updateUser();
			}
		} else if (action.equals(DynamicDetailActivity.ACTION_UPDATE_DYNAMIC_LIST)) {
			TypeHolder bean = (TypeHolder) intent.getSerializableExtra(DynamicDetailActivity.KEY_TYPEHOLDER);
			if (bean != null) {
				mAdapter.replaceItem(bean);
			}
		} else if (action.equals(MainActivity.ACTION_UPDATE_PROFILE)) {
			mAdapter.updateUser();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		mAdapter.stopPlayVoice();
	}
}
