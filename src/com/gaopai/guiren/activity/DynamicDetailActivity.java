package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.DynamicAdapter.CommnetHolder;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.dynamic.DynamicBean.CommentBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.CommentContetnHolder;
import com.gaopai.guiren.bean.dynamic.DynamicBean.JsonContent;
import com.gaopai.guiren.bean.dynamic.DynamicBean.PicBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.bean.dynamic.DynamicBean.ZanBean;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.media.SpeexPlayerWrapper;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.FlowLayout;
import com.gaopai.guiren.view.MyGridLayout;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class DynamicDetailActivity extends BaseActivity {
	public static final int TYPE_SEND_DYNAMIC = 1;
	public final static int TYPE_SPREAD_MSG = 2;

	public final static int TYPE_SPREAD_MEETING = 3;

	public final static int TYPE_SPREAD_LINK = 6;
	public final static int TYPE_SPREAD_TRIBE = 4;
	public final static int TYPE_SPREAD_USER = 5;

	public final static int TYPE_SPREAD_OTHER_DYNAMIC = 7;

	private PullToRefreshListView mListView;
	private View headerView;
	private TextView tvZan;
	private EditText etContent;
	private View chatBox;
	private Button mSendTextBtn;

	public final static String KEY_TYPEHOLDER = "typeholder";
	private TypeHolder typeBean;

	private List<String> testUserList = new ArrayList<String>();

	private MyAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		typeBean = (TypeHolder) getIntent().getSerializableExtra(KEY_TYPEHOLDER);
		initTitleBar();
		setAbContentView(R.layout.fragment_dynamic);
		mTitleBar.setTitleText("动态详情");
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		initComponent();
	}

	private void initComponent() {
		// TODO Auto-generated method stub
		etContent = (EditText) findViewById(R.id.chat_box_edit_keyword);
		chatBox = findViewById(R.id.chat_box);
		mSendTextBtn = (Button) findViewById(R.id.send_text_btn);
		mListView = (PullToRefreshListView) findViewById(R.id.listview);
		mListView.getRefreshableView().setDivider(null);
		mListView.getRefreshableView().setSelector(mContext.getResources().getDrawable(R.color.transparent));

		headerView = getHeaderView();

		mListView.getRefreshableView().addHeaderView(headerView);
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
	
	private View getHeaderView() {
		// TODO Auto-generated method stub
		// DynamicBean.TypeHolder typeBean = mData.get(position);
		View convertView = null;
		switch (typeBean.type) {

		case TYPE_SPREAD_OTHER_DYNAMIC:
		case TYPE_SEND_DYNAMIC:
			convertView = inflateItemView(TYPE_SEND_DYNAMIC);
			buildDynamicView((ViewHolderSendDynamic) convertView.getTag());
			break;
		case TYPE_SPREAD_USER:
		case TYPE_SPREAD_TRIBE:
		case TYPE_SPREAD_LINK: {
			convertView = inflateItemView(TYPE_SPREAD_LINK);
			buildSpreadLinkView((ViewHolderSpreadLink) convertView.getTag());
			break;
		}
		case TYPE_SPREAD_MEETING: {
			convertView = inflateItemView(TYPE_SPREAD_MEETING);
			buildMeetingView((ViewHolderMeeting) convertView.getTag());
			break;
		}
		case TYPE_SPREAD_MSG:
			convertView = inflateItemView(TYPE_SPREAD_MSG);
			buildMsgView((ViewHolderSpreadMsg) convertView.getTag());
			break;
		}
		return convertView;
	}

	private View inflateItemView(int type) {
		View convertView = null;
		switch (type) {
		case TYPE_SEND_DYNAMIC:
		case TYPE_SPREAD_OTHER_DYNAMIC: {
			ViewHolderSendDynamic viewHolder;
			convertView = mInflater.inflate(R.layout.item_dynamic_detail_send, null);
			viewHolder = ViewHolderSendDynamic.getInstance(convertView);
			convertView.setTag(viewHolder);
		}
			break;

		case TYPE_SPREAD_MEETING: {
			ViewHolderMeeting viewHolder;
			convertView = mInflater.inflate(R.layout.item_dynamic_detail_meeting, null);
			viewHolder = ViewHolderMeeting.getInstance(convertView);
			convertView.setTag(viewHolder);
		}
			break;

		case TYPE_SPREAD_MSG: {
			ViewHolderSpreadMsg viewHolder;
			convertView = mInflater.inflate(R.layout.item_dynamic_detail_spread_msg, null);
			viewHolder = ViewHolderSpreadMsg.getInstance(convertView);
			convertView.setTag(viewHolder);
		}
			break;
		case TYPE_SPREAD_LINK:
		case TYPE_SPREAD_TRIBE:
		case TYPE_SPREAD_USER: {
			ViewHolderSpreadLink viewHolder;
			convertView = mInflater.inflate(R.layout.item_dynamic_detail_spread_link, null);
			viewHolder = ViewHolderSpreadLink.getInstance(convertView);
			convertView.setTag(viewHolder);
		}
			break;
		default:
			break;
		}
		return convertView;
	}

	private void buildCommonView(ViewHolderCommon viewHolder) {
		boolean isShowBottomLayout = false;
		int type = typeBean.type;
		if (!TextUtils.isEmpty(typeBean.s_path)) {
			ImageLoaderUtil.displayImage(typeBean.s_path, viewHolder.ivHeader);
		} else {
			viewHolder.ivHeader.setImageResource(R.drawable.default_header);
		}
		viewHolder.tvUserName.setText(typeBean.realname);
		viewHolder.tvUserInfo.setText(typeBean.post);

		if (type == TYPE_SEND_DYNAMIC) {
			viewHolder.tvAction.setVisibility(View.GONE);
		} else {
			viewHolder.tvAction.setVisibility(View.VISIBLE);
			viewHolder.tvAction.setText(typeBean.title);
		}

		viewHolder.tvDateInfo.setText(FeatureFunction.getCreateTime(Long.valueOf(typeBean.time)) + "     天山上的来客");
		if (typeBean.commentlist != null && typeBean.commentlist.size() > 0) {
			isShowBottomLayout = true;
		}

		if (typeBean.zanList.size() > 0) {
			isShowBottomLayout = true;
			viewHolder.layoutZan.setVisibility(View.VISIBLE);
			viewHolder.tvZan.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			viewHolder.tvZan.setText(MyTextUtils.addZanUserList(typeBean.zanList));
		} else {
			viewHolder.layoutZan.setVisibility(View.GONE);
		}
		viewHolder.layoutSpread.setVisibility(View.GONE);

		viewHolder.btnDynamicAction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showActionWindow(v);
			}
		});
	}

	private void buildMeetingView(ViewHolderMeeting viewHolder) {
		// TODO Auto-generated method stub
		buildCommonView(viewHolder);
		JsonContent jsonContent = typeBean.jsoncontent;

		viewHolder.tvMeetingTitle.setText(jsonContent.name);
		viewHolder.tvMeetingTime.setText(jsonContent.time);
		viewHolder.tvMeetingGuest.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
		viewHolder.tvMeetingGuest.setText(MyTextUtils.addGuestUserList(jsonContent.guest, "嘉宾："));

	}

	private SpeexPlayerWrapper mPlayerWrapper;

	private void buildMsgView(ViewHolderSpreadMsg viewHolder) {
		// TODO Auto-generated method stub
		buildCommonView(viewHolder);
		final JsonContent content = typeBean.jsoncontent;

		notHideViews(viewHolder, content.fileType);
		viewHolder.ivVoice.setLayoutParams(getVoiceViewLengthParams(content));
		viewHolder.msgHolder.setOnClickListener(null);
		switch (content.fileType) {
		case MessageType.TEXT:
			viewHolder.tvText.setText(MyTextUtils.addHttpLinks(content.content));
			viewHolder.tvText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			break;
		case MessageType.PICTURE:
			final String path = content.imgUrlS.trim();
			MessageInfo messageInfo = new MessageInfo();
			messageInfo.imgUrlS = content.imgUrlS;
			messageInfo.imgUrlL = content.imgUrlL;
			messageInfo.tag = content.sid;
			ImageLoaderUtil.displayImage(path, viewHolder.ivPic);
			if (path.startsWith("http://")) {
				ImageLoaderUtil.displayImage(path, viewHolder.ivPic);
			}
			viewHolder.ivPic.setTag(messageInfo);
			viewHolder.ivPic.setOnClickListener(photoClickListener);
			break;
		case MessageType.VOICE:
			viewHolder.tvVoiceLength.setText(content.voiceTime + "''");
			viewHolder.msgHolder.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// palyedPosition = position;
					MessageInfo messageInfo = new MessageInfo();
					messageInfo.voiceUrl = content.voiceUrl;
					messageInfo.tag = content.sid;
					mPlayerWrapper.start(messageInfo);
				}
			});

			AnimationDrawable drawable = (AnimationDrawable) viewHolder.ivVoice.getDrawable();
			// if (mPlayerWrapper.isPlay() && position == palyedPosition) {
			// drawable.start();
			// } else {
			// drawable.stop();
			// drawable.selectDrawable(0);
			// }
			break;
		}

	}

	private OnClickListener photoClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			List<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
			messageInfos.add((MessageInfo) v.getTag());
			Intent intent = new Intent(mContext, ShowImagesActivity.class);
			// intent.putExtra("msgList", (Serializable) mData);
			intent.putExtra("position", 0);
			mContext.startActivity(intent);
		}
	};

	private void notHideViews(ViewHolderSpreadMsg viewHolder, int which) {
		viewHolder.ivPic.setVisibility(View.GONE);
		viewHolder.tvText.setVisibility(View.GONE);
		viewHolder.tvVoiceLength.setVisibility(View.GONE);
		viewHolder.ivVoice.setVisibility(View.GONE);
		switch (which) {
		case MessageType.TEXT:
			viewHolder.tvText.setVisibility(View.VISIBLE);
			break;
		case MessageType.PICTURE:
			viewHolder.ivPic.setVisibility(View.VISIBLE);
			break;
		case MessageType.VOICE:
			viewHolder.ivVoice.setVisibility(View.VISIBLE);
			viewHolder.tvVoiceLength.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	private RelativeLayout.LayoutParams getVoiceViewLengthParams(JsonContent content) {
		final int MAX_SECOND = 10;
		final int MIN_SECOND = 2;
		int length = content.voiceTime;
		float max = mContext.getResources().getDimension(R.dimen.voice_max_length_comment);
		float min = mContext.getResources().getDimension(R.dimen.voice_min_length_comment);
		int width = (int) min;
		if (length >= MIN_SECOND && length <= MAX_SECOND) {
			width += (length - MIN_SECOND) * (int) ((max - min) / (MAX_SECOND - MIN_SECOND));
		} else if (length > MAX_SECOND) {
			width = (int) max;
		}
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		return lp;
	}

	private void buildSpreadLinkView(ViewHolderSpreadLink viewHolder) {
		// TODO Auto-generated method stub
		buildCommonView(viewHolder);
		JsonContent jsonContent = typeBean.jsoncontent;
		switch (typeBean.type) {
		case TYPE_SPREAD_LINK:
			// url
			ImageLoaderUtil.displayImage(jsonContent.image, viewHolder.ivHeader1);
			viewHolder.tvTitle1.setText(jsonContent.title);
			viewHolder.tvInfo1.setText(jsonContent.desc);
			break;
		case TYPE_SPREAD_TRIBE:
			ImageLoaderUtil.displayImage(jsonContent.headsmall, viewHolder.ivHeader1);
			viewHolder.tvTitle1.setText(jsonContent.name);
			viewHolder.tvInfo1.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			viewHolder.tvInfo1.setText(MyTextUtils.addGuestUserList(jsonContent.guest, "圈子知名人物："));
			break;
		case TYPE_SPREAD_USER:
			ImageLoaderUtil.displayImage(jsonContent.headsmall, viewHolder.ivHeader1);
			viewHolder.tvTitle1.setText(jsonContent.realname);
			viewHolder.tvInfo1.setText(jsonContent.post);
			break;
		default:
			break;
		}
	}

	private void buildDynamicView(ViewHolderSendDynamic viewHolder) {
		// TODO Auto-generated method stub
		JsonContent jsonContent = typeBean.jsoncontent;
		buildCommonView(viewHolder);
		viewHolder.tvContent.setText(jsonContent.content);

		if (jsonContent.pic != null) {
			buidImageViews(viewHolder.gridLayout, jsonContent.pic);
		}
		viewHolder.flTags.setVisibility(View.GONE);
	}

	private void buidImageViews(MyGridLayout gridLayout, List<PicBean> pics) {
		// TODO Auto-generated method stub
		for (PicBean bean : pics) {
			gridLayout.addView(getImageView(bean.imgUrlS));
		}
	}

	private ImageView getImageView(String url) {
		ImageView imageView = new ImageView(mContext);
		ImageLoaderUtil.displayImage(url, imageView);
		imageView.setImageResource(R.drawable.logo);
		android.view.ViewGroup.LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(lp);
		imageView.setScaleType(ScaleType.CENTER_INSIDE);
		int padding = MyUtils.dip2px(mContext, 5);
		imageView.setPadding(padding, padding, padding, padding);
		return imageView;
	}

	static class ViewHolderSendDynamic extends ViewHolderCommon {
		TextView tvContent;
		FlowLayout flTags;
		MyGridLayout gridLayout;

		public static ViewHolderSendDynamic getInstance(View view) {
			// TODO Auto-generated method stub
			ViewHolderSendDynamic viewHolder = new ViewHolderSendDynamic();
			viewHolder.initialBottom(viewHolder, view);

			viewHolder.tvContent = (TextView) view.findViewById(R.id.tv_content);
			viewHolder.flTags = (FlowLayout) view.findViewById(R.id.fl_tags);
			viewHolder.gridLayout = (MyGridLayout) view.findViewById(R.id.dynamic_pics_holder);

			return viewHolder;
		}
	}

	static class ViewHolderMeeting extends ViewHolderCommon {

		TextView tvMeetingTitle;
		TextView tvMeetingTime;
		TextView tvMeetingGuest;

		public static ViewHolderMeeting getInstance(View view) {
			// TODO Auto-generated method stub
			ViewHolderMeeting viewHolder = new ViewHolderMeeting();
			viewHolder.initialBottom(viewHolder, view);

			viewHolder.tvMeetingTime = (TextView) view.findViewById(R.id.tv_meeting_time);
			viewHolder.tvMeetingTitle = (TextView) view.findViewById(R.id.tv_meeting_title);
			viewHolder.tvMeetingGuest = (TextView) view.findViewById(R.id.tv_meeting_guest);
			return viewHolder;
		}
	}

	static class ViewHolderSpreadLink extends ViewHolderCommon {

		ImageView ivHeader1;
		TextView tvTitle1;
		TextView tvInfo1;

		public static ViewHolderSpreadLink getInstance(View view) {
			// TODO Auto-generated method stub
			ViewHolderSpreadLink viewHolder = new ViewHolderSpreadLink();
			viewHolder.initialBottom(viewHolder, view);

			viewHolder.ivHeader1 = (ImageView) view.findViewById(R.id.iv_header1);
			viewHolder.tvTitle1 = (TextView) view.findViewById(R.id.tv_title1);
			viewHolder.tvInfo1 = (TextView) view.findViewById(R.id.tv_info1);

			return viewHolder;
		}
	}

	static class ViewHolderCommon {
		ImageButton btnDynamicAction;
		LinearLayout rlDynamicInteractive;
		LinearLayout layoutSpread;
		TextView tvSpread;
		LinearLayout layoutZan;
		TextView tvZan;
		LinearLayout layoutComment;
		TextView tvDateInfo;

		ImageView ivHeader;
		TextView tvUserName;
		TextView tvUserInfo;
		TextView tvAction;

		public void initialBottom(ViewHolderCommon viewHolder, View view) {
			viewHolder.tvSpread = (TextView) view.findViewById(R.id.tv_spread);
			viewHolder.tvZan = (TextView) view.findViewById(R.id.tv_zan);
			viewHolder.tvDateInfo = (TextView) view.findViewById(R.id.tv_date_info);
			viewHolder.rlDynamicInteractive = (LinearLayout) view.findViewById(R.id.rl_dynamic_interactive);
			viewHolder.layoutComment = (LinearLayout) view.findViewById(R.id.ll_comment);
			viewHolder.layoutSpread = (LinearLayout) view.findViewById(R.id.ll_spread);
			viewHolder.layoutZan = (LinearLayout) view.findViewById(R.id.ll_zan);
			viewHolder.btnDynamicAction = (ImageButton) view.findViewById(R.id.btn_dynamic_ation);

			viewHolder.ivHeader = (ImageView) view.findViewById(R.id.iv_header);
			viewHolder.tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
			viewHolder.tvUserInfo = (TextView) view.findViewById(R.id.tv_user_info);
			viewHolder.tvAction = (TextView) view.findViewById(R.id.tv_spread_action);
		}
	}

	static class ViewHolderSpreadMsg extends ViewHolderCommon {

		ImageView ivHeader1;
		TextView tvUserName1;
		RelativeLayout msgHolder;
		ImageView ivVoice;
		ImageView ivPic;
		TextView tvText;
		TextView tvVoiceLength;

		public static ViewHolderSpreadMsg getInstance(View view) {
			// TODO Auto-generated method stub
			ViewHolderSpreadMsg viewHolder = new ViewHolderSpreadMsg();
			viewHolder.initialBottom(viewHolder, view);

			viewHolder.ivHeader1 = (ImageView) view.findViewById(R.id.iv_header1);
			viewHolder.tvUserName1 = (TextView) view.findViewById(R.id.tv_title1);

			viewHolder.msgHolder = (RelativeLayout) view.findViewById(R.id.ll_dynamic_msg_holder);
			viewHolder.ivVoice = (ImageView) view.findViewById(R.id.iv_chat_voice);
			viewHolder.ivPic = (ImageView) view.findViewById(R.id.iv_chat_photo);
			viewHolder.tvText = (TextView) view.findViewById(R.id.iv_chat_text);
			viewHolder.tvVoiceLength = (TextView) view.findViewById(R.id.tv_chat_voice_time_length);

			return viewHolder;
		}
	}

	private PopupWindow actionWindow;
	private int windowHeightPadding;
	private int windowWidthPadding;

	private void showActionWindow(View anchor) {
		View v = mInflater.inflate(R.layout.popup_dynamic_action, null);
		Button btnZan = (Button) v.findViewById(R.id.btn_zan);
		Button btnComment = (Button) v.findViewById(R.id.btn_comment);
		Button btnSpread = (Button) v.findViewById(R.id.btn_spread);
		if (typeBean.isZan == 0) {
			btnZan.setText("赞");
		} else {
			btnZan.setText("取消赞");
		}
		btnComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				commnetHolder.toid = typeBean.uid;
				commnetHolder.todisplayname = typeBean.realname;
				commnetHolder.displayname = DamiCommon.getLoginResult(mContext).realname;
				commnetHolder.dataid = typeBean.id;
				commnetHolder.type = 1;// ??????????????????
				showChatBox(commnetHolder.todisplayname);
				actionWindow.dismiss();
			}
		});
		btnZan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				zanMessage(typeBean);
				actionWindow.dismiss();
			}
		});
		actionWindow = new PopupWindow(v, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		actionWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		actionWindow.setAnimationStyle(R.style.window_slide_right);
		actionWindow.setOutsideTouchable(true);
		actionWindow.setFocusable(true);
		ViewUtil.measure(v);
		windowHeightPadding = (v.getMeasuredHeight() + anchor.getMeasuredHeight()) / 2;
		windowWidthPadding = v.getMeasuredWidth() + FeatureFunction.dip2px(mContext, 5);

		if (actionWindow.isShowing()) {
			actionWindow.dismiss();
			return;
		}
		actionWindow.showAsDropDown(anchor, -windowWidthPadding, -windowHeightPadding);
	}

	private void zanMessage(final TypeHolder typeBean) {
		final List<ZanBean> zanList = typeBean.zanList;
		DamiInfo.zanOperation(DamiCommon.getUid(mContext), 1, typeBean.id, 0, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub]
				if (typeBean.isZan == 0) {
					ZanBean zanBean = new ZanBean();
					zanBean.uid = DamiCommon.getUid(mContext);
					zanBean.uname = DamiCommon.getLoginResult(mContext).realname;
					typeBean.isZan = 1;
					zanList.add(zanBean);

				} else {
					for (ZanBean zanBean : zanList) {
						if (zanBean.uid.equals(DamiCommon.getLoginResult(mContext).uid)) {
							zanList.remove(zanBean);
							typeBean.isZan = 0;
							break;
						}
					}
				}
				buildCommonView((ViewHolderCommon) headerView.getTag());
			}
		});
	}

	private CommnetHolder commnetHolder = new CommnetHolder();

	public void commentMessage(final String content) {
		DamiInfo.addComment(commnetHolder.toid, commnetHolder.type, commnetHolder.dataid, content, 0,
				commnetHolder.displayname, commnetHolder.todisplayname, new SimpleResponseListener(mContext) {
					@Override
					public void onSuccess(Object o) {
						// TODO Auto-generated method stub
						BaseNetBean data = (BaseNetBean) o;
						if (data.state != null && data.state.code == 0) {
							CommentBean commentBean = new CommentBean();
							commentBean.content = new CommentContetnHolder();
							commentBean.content.content = content;
							commentBean.toid = commnetHolder.toid;
							commentBean.toname = commnetHolder.todisplayname;
							commentBean.uname = DamiCommon.getLoginResult(mContext).realname;
							commentBean.type = commnetHolder.type;
							if (typeBean.commentlist == null) {
								typeBean.commentlist = new ArrayList<CommentBean>();
							}
							Log.d("typeholder", "id===" + typeBean.id);
							typeBean.commentlist.add(commentBean);
							mAdapter.notifyDataSetChanged();
						}
					}
				});
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
			TextView tvComment;
			final CommentBean commentBean = typeBean.commentlist.get(position);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_dynamic_detail_comment, null);
				tvComment = (TextView) convertView.findViewById(R.id.tv_comment_item);
				convertView.setTag(tvComment);
			} else {
				tvComment = (TextView) convertView.getTag();
			}
			if (position == getCount() - 1) {
				tvComment.setBackgroundResource(R.drawable.fuck);
			} else {
				tvComment.setBackgroundResource(R.drawable.selector_gray_blue_btn);
			}
			tvComment.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			commentBean.uname = makeNameNotNull(commentBean.uname);
			commentBean.toname = makeNameNotNull(commentBean.toname);
			tvComment.setText(MyTextUtils.addUserHttpLinks(commentBean.uname + "回复" + commentBean.toname + "："
					+ commentBean.content.content, commentBean.uname, commentBean.toname, commentBean.uid,
					commentBean.toid));
			tvComment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					commnetHolder.toid = commentBean.uid;
					commnetHolder.todisplayname = commentBean.uname;
					commnetHolder.displayname = DamiCommon.getLoginResult(mContext).displayName;
					commnetHolder.dataid = commentBean.dataid;
					commnetHolder.type = commentBean.type;
					showChatBox(commnetHolder.todisplayname);
					showSoftKeyboard();
				}
			});
			return convertView;
		}

		private String makeNameNotNull(String name) {
			if (TextUtils.isEmpty(name)) {
				return "匿名";
			}
			return name;
		}

		public void showSoftKeyboard() {
			InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
		}
	}

	public void showChatBox(String text) {
		chatBox.setVisibility(View.VISIBLE);
		etContent.requestFocus();
		etContent.setHint("回复：" + text);
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
