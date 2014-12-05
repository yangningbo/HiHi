package com.gaopai.guiren.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.dynamic.DynamicBean.CommentBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.DySingleBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.support.DynamicHelper;
import com.gaopai.guiren.support.DynamicHelper.DyCallback;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class DynamicDetailActivity extends BaseActivity {
	private PullToRefreshListView mListView;
	private View headerView;
	private TextView tvZan;
	private EditText etContent;
	private View chatBox;
	private Button mSendTextBtn;

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

	private DynamicHelper.DyCallback callback = new DyCallback() {

		@Override
		public void onZanSuccess() {
			// TODO Auto-generated method stub
			dynamicHelper.buildCommonView((DynamicHelper.ViewHolderCommon) headerView.getTag(), typeBean);
		}

		@Override
		public void onVoicePlayStart() {
			// TODO Auto-generated method stub
			dynamicHelper.buildCommonView((DynamicHelper.ViewHolderCommon) headerView.getTag(), typeBean);
		}

		@Override
		public void onVoicePlayEnd() {
			// TODO Auto-generated method stub
			dynamicHelper.buildCommonView((DynamicHelper.ViewHolderCommon) headerView.getTag(), typeBean);
		}

		@Override
		public void onSpreadSuccess() {
			// TODO Auto-generated method stub
			dynamicHelper.buildCommonView((DynamicHelper.ViewHolderCommon) headerView.getTag(), typeBean);
		}

		@Override
		public void onDownVoiceSuccess() {
			// TODO Auto-generated method stub
			dynamicHelper.buildCommonView((DynamicHelper.ViewHolderCommon) headerView.getTag(), typeBean);
		}

		@Override
		public void onCommentSuccess() {
			// TODO Auto-generated method stub
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onCommentButtonClick(TypeHolder typeHolder, boolean isShowReply) {
			// TODO Auto-generated method stub
			showChatBox(typeHolder.realname, isShowReply);
			showSoftKeyboard();
		}

		@Override
		public void onBindComment(TypeHolder typeHolder, com.gaopai.guiren.support.DynamicHelper.ViewHolderCommon holder) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBindCommenViewBottom(TypeHolder typeBean,
				com.gaopai.guiren.support.DynamicHelper.ViewHolderCommon viewHolder, boolean isShowComment,
				boolean isShowSpread, boolean isShowZan) {
			// TODO Auto-generated method stub
			if (isShowSpread || isShowZan) {
				if (!isShowComment) {
					viewHolder.layoutCoverBottom.setVisibility(View.VISIBLE);
				} else {
					viewHolder.layoutCoverBottom.setVisibility(View.GONE);
				}
			}else {
				viewHolder.layoutCoverBottom.setVisibility(View.GONE);
			}
			if (isShowComment || isShowSpread || isShowZan) {
				viewHolder.layoutCoverTop.setVisibility(View.VISIBLE);
			} else {
				viewHolder.layoutCoverTop.setVisibility(View.GONE);
			}
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
//						getHeaderView();
						dynamicHelper.buildCommonView((com.gaopai.guiren.support.DynamicHelper.ViewHolderCommon) headerView.getTag(), typeBean);
						mAdapter.notifyDataSetChanged();
					}
				}
			}
		});
	}

	private void initComponent() {
		// TODO Auto-generated method stub
		etContent = (EditText) findViewById(R.id.chat_box_edit_keyword);
		chatBox = findViewById(R.id.chat_box);
		mSendTextBtn = (Button) findViewById(R.id.send_text_btn);


		mListView = (PullToRefreshListView) findViewById(R.id.listview);
		mListView.getRefreshableView().setDivider(null);
		mListView.getRefreshableView().setSelector(mContext.getResources().getDrawable(R.color.transparent));

		// headerView = getHeaderView();
		headerView = dynamicHelper.getView(convertView, typeBean);
		if (headerView != null) {
			mListView.getRefreshableView().addHeaderView(headerView);
		}
		mAdapter = new MyAdapter(this);
		mListView.setAdapter(mAdapter);
		mSendTextBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (etContent.getText().length() == 0) {
					showToast(R.string.input_can_not_be_empty);
					return;
				}
				String teString = etContent.getText().toString();
				commentMessage(teString);
			}
		});
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
			if (typeBean.commentlist == null || typeBean.commentlist.size() == 0) {
				return 0;
			}
			return typeBean.commentlist.size() + 1;
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
				viewHolder.layoutFake = convertView.findViewById(R.id.tv_comment_item_fake);
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
			if (position == getCount() - 1) {
				viewHolder.tvComment.setVisibility(View.INVISIBLE);
				viewHolder.layoutFake.setBackgroundResource(R.drawable.fuck);
				return convertView;
			} else {
				viewHolder.tvComment.setVisibility(View.VISIBLE);
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
					callback.onCommentButtonClick(typeBean, true);
					showSoftKeyboard();
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
		View layoutFake;
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
}
