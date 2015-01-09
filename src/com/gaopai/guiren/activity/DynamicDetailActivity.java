package com.gaopai.guiren.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.dynamic.DynamicBean.CommentBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.DySingleBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.fragment.DynamicFragment.BackPressedListener;
import com.gaopai.guiren.support.DynamicHelper;
import com.gaopai.guiren.support.DynamicHelper.DyCallback;
import com.gaopai.guiren.support.chat.ChatBoxManager;
import com.gaopai.guiren.support.view.CustomEditText;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;
import com.gaopai.guiren.widget.emotion.EmotionPicker;

public class DynamicDetailActivity extends BaseActivity implements OnClickListener {
	private PullToRefreshListView mListView;
	private View headerView;
	private TextView tvZan;
	private CustomEditText etContent;
	private View chatBox;
	private ChatBoxManager chaBoxManager;

	public final static String KEY_TYPEHOLDER = "typeholder";
	public final static String KEY_SID = "sid";
	private TypeHolder typeBean;

	private String sid;
	private MyAdapter mAdapter;
	private DynamicHelper dynamicHelper;

	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.fragment_dynamic);
		dynamicHelper = new DynamicHelper(mContext, DynamicHelper.DY_DETAIL);
		dynamicHelper.setCallback(callback);
		mTitleBar.setTitleText("动态详情");
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		user = DamiCommon.getLoginResult(mContext);
		typeBean = (TypeHolder) getIntent().getSerializableExtra(KEY_TYPEHOLDER);
		if (typeBean == null) {
			sid = getIntent().getStringExtra(KEY_SID);
		} else {
			sid = typeBean.id;
			initComponent();
		}
		getDynamicDetail();
	}

	@Override
	protected void registerReceiver(IntentFilter intentFilter) {
		super.registerReceiver(intentFilter);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
	}

	public static Intent getIntent(Context context, String sid) {
		Intent intent = new Intent(context, DynamicDetailActivity.class);
		intent.putExtra(KEY_SID, sid);
		return intent;
	}

	private DynamicHelper.DyCallback callback = new DyCallback() {

		@Override
		public void onCommentSuccess() {
			// TODO Auto-generated method stub
			dynamicHelper.buildCommonView((DynamicHelper.ViewHolderCommon) headerView.getTag(), typeBean);
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onBindComment(TypeHolder typeHolder, com.gaopai.guiren.support.DynamicHelper.ViewHolderCommon holder) {
			// TODO Auto-generated method stub
			dynamicHelper.buildCommonView((DynamicHelper.ViewHolderCommon) headerView.getTag(), typeBean);
		}

		@Override
		public void onBindCommenViewBottom(TypeHolder typeBean,
				com.gaopai.guiren.support.DynamicHelper.ViewHolderCommon viewHolder, boolean isShowComment,
				boolean isShowSpread, boolean isShowZan) {
			// TODO Auto-generated method stub
			if (isShowComment || isShowSpread || isShowZan) {
				viewHolder.layoutCoverTop.setVisibility(View.VISIBLE);
			} else {
				viewHolder.layoutCoverTop.setVisibility(View.GONE);
			}
		}

		@Override
		public void onCommentButtonClick(TypeHolder typeHolder, String name, boolean isShowReply) {
			// TODO Auto-generated method stub
			showChatBox(name, isShowReply);
			showSoftKeyboard();
		}

		@Override
		public void notifyUpdateView() {
			dynamicHelper.getView(headerView, typeBean);
		}

		@Override
		public void onDeleteItem(String dataid) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDeleteItemSuccess(TypeHolder typeHolder) {
			mContext.sendBroadcast(DynamicHelper.getDeleteIntent(typeHolder.id));
			DynamicDetailActivity.this.finish();
		}

		@Override
		public void onVoiceStart() {
			dynamicHelper.getView(headerView, typeBean);
		}

		@Override
		public void onVoiceStop() {
			dynamicHelper.getView(headerView, typeBean);
		}
	};

	private void getDynamicDetail() {
		DamiInfo.getDynamicDetails(sid, new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				DySingleBean data = (DySingleBean) o;
				if (data.state != null && data.state.code == 0) {
					if (typeBean == null) {
						typeBean = data.data;
						initComponent();
					} else {
						typeBean = data.data;
						// getHeaderView();
						dynamicHelper.buildCommonView(
								(com.gaopai.guiren.support.DynamicHelper.ViewHolderCommon) headerView.getTag(),
								typeBean);
						mAdapter.notifyDataSetChanged();
					}
				} else {
					otherCondition(data.state, DynamicDetailActivity.this);
				}
				mListView.onPullComplete();
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				mListView.onPullComplete();
			}

		});
	}

	private void initComponent() {
		// TODO Auto-generated method stub
		etContent = (CustomEditText) findViewById(R.id.chat_box_edit_keyword);
		etContent.setBackPressedListener(new BackPressedListener() {
			@Override
			public boolean onBack() {
				if (chatBox.getVisibility() == View.VISIBLE) {
					hideChatBox();
					return true;
				}
				return false;
			}
		});
		chatBox = findViewById(R.id.chat_box);
		ViewUtil.findViewById(this, R.id.send_text_btn).setOnClickListener(this);
		Button emotionBtn = ViewUtil.findViewById(this, R.id.emotion_btn);
		emotionBtn.setOnClickListener(this);
		emotionBtn.setVisibility(View.VISIBLE);
		EmotionPicker emotionPicker = ViewUtil.findViewById(this, R.id.emotion_picker);
		emotionPicker.setEditText(this, null, etContent);
		chaBoxManager = new ChatBoxManager(this, etContent, emotionPicker, emotionBtn);
		ViewUtil.findViewById(this, R.id.send_text_btn).setOnClickListener(this);

		mListView = (PullToRefreshListView) findViewById(R.id.listview);
		mListView.setPullRefreshEnabled(true);
		mListView.getRefreshableView().setDivider(null);
		mListView.getRefreshableView().setSelector(mContext.getResources().getDrawable(R.color.transparent));
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getDynamicDetail();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			}
		});
		headerView = dynamicHelper.getView(convertView, typeBean);
		headerView.setClickable(true);
		if (headerView != null) {
			mListView.getRefreshableView().addHeaderView(headerView);
		}
		mAdapter = new MyAdapter(this);
		mListView.setAdapter(mAdapter);
	}

	View convertView = null;

	public void commentMessage(final String content) {
		hideChatBox();
		hideSoftKeyboard(etContent);
		etContent.setText("");
		dynamicHelper.commentMessage(content, typeBean);
	}

	class MyAdapter extends BaseAdapter {

		public MyAdapter(Context context) {
			mContext = context;
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return typeBean.commentlist.size();
		}

		@Override
		public Object getItem(int position) {
			return typeBean.commentlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_dynamic_detail_comment, null);
				viewHolder = new ViewHolder();
				viewHolder.tvComment = (TextView) convertView.findViewById(R.id.tv_comment_item);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.tvComment.setCompoundDrawablePadding(MyUtils.dip2px(mContext, 5));
			if (position == 0) {
				viewHolder.tvComment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_dynamic_comment, 0, 0, 0);
			} else {
				viewHolder.tvComment.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.icon_dynamic_comment_transparent, 0, 0, 0);
			}

			final CommentBean commentBean = typeBean.commentlist.get(position);
			viewHolder.tvComment.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			viewHolder.tvComment.setText(dynamicHelper.getCommentString(commentBean));
			viewHolder.tvComment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (commentBean.uid.equals(user.uid)) {
						return;
					}
					dynamicHelper.setCommentHolderForReply(typeBean, commentBean);
					callback.onCommentButtonClick(typeBean, commentBean.uname, true);
				}
			});
			return convertView;
		}
	}

	public void showSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}

	public void hideSoftKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	static class ViewHolder {
		TextView tvComment;
	}

	public void showChatBox(String name, boolean showReply) {
		chatBox.setVisibility(View.VISIBLE);
		etContent.requestFocus();
		if (showReply) {
			etContent.setHint("回复：" + name);
		} else {
			etContent.setHint(getString(R.string.please_input_comment));
		}
	}

	public void hideChatBox() {
		chaBoxManager.hideEmotion();
		chatBox.setVisibility(View.GONE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (chatBox.getVisibility() == View.VISIBLE) {
				hideChatBox();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.emotion_btn:
			chaBoxManager.emotionClick();
			break;
		case R.id.send_text_btn:
			if (etContent.getText().length() == 0) {
				showToast(R.string.input_can_not_be_empty);
				return;
			}
			String teString = etContent.getText().toString();
			commentMessage(teString);
			break;
		case R.id.chat_box_btn_switch_voice_text:
			hideChatBox();
			break;

		default:
			break;
		}

	}

	@Override
	protected void onReceive(Intent intent) {
		// TODO Auto-generated method stub
		if (intent != null) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				dynamicHelper.stopPlayVoice();
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		dynamicHelper.stopPlayVoice();
	}
}
