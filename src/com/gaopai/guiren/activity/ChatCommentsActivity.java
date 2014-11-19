package com.gaopai.guiren.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.chat.ChatMainActivity;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.activity.share.ShareActivity;
import com.gaopai.guiren.bean.Identity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageState;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.bean.net.ChatMessageBean;
import com.gaopai.guiren.bean.net.SendMessageResult;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.media.MediaUIHeper;
import com.gaopai.guiren.media.SpeexPlayerWrapper;
import com.gaopai.guiren.media.SpeexPlayerWrapper.OnDownLoadCallback;
import com.gaopai.guiren.media.SpeexRecorderWrapper;
import com.gaopai.guiren.utils.ChatBoxManager;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.view.ChatGridLayout;
import com.gaopai.guiren.view.RecordDialog;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;
import com.gaopai.guiren.widget.emotion.EmotionParser;
import com.gaopai.guiren.widget.emotion.EmotionPicker;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * 消息详情界面，包括查看评论以及回复评论等功能
 */
public class ChatCommentsActivity extends BaseActivity implements OnClickListener {
	public static final String INTENT_MESSAGE_KEY = "message_key";
	public static final String INTENT_TRIBE_KEY = "tribe_key";
	public static final String INTENT_CHATTYPE_KEY = "chattype_key";
	public static final String INTENT_USER_KEY = "user_key";
	public static final String INTENT_IDENTITY_KEY = "identity_key";
	public static final String INTENT_NEWURL_KEY = "newurl_key";
	public static final String INTENT_SENCE_ONLOOK_KEY = "onlook_key";
	private MessageInfo messageInfo;
	private Tribe mTribe;
	protected User mLogin;
	protected Identity mIdentity;
	protected int mChatType = 0;
	protected String mNewUrl = "";

	private ImageView ivVoice, ivPhoto, headImageView;
	private ImageView mVoiceModeImage;
	private ProgressBar progressbar;
	private TextView tvText, tvVoiceLength, commentCountText, likeCountText, favouriteCountText, nameTextView, zanText;
	private ImageView commentCountBtn, favoriteCountBtn, zanCountBtn;
	private View msgInfoLayout;

	private LinearLayout commentCountLayout, zanCountLayout, favoriteCountLayout;

	private RelativeLayout titleRightBtn;
	private RelativeLayout chatVoiceLayout;
	private RelativeLayout rootLayout;
	private LinearLayout chatCommentsLayout;

	private ViewGroup layoutZan;

	private boolean isRcording = false;
	private List<String> downVoiceList = new ArrayList<String>();

	private LinearLayout voiceModeToast;
	private Button mSendTextBtn;
	private Button mEmotionBtn;
	private EmotionPicker mEmotionPicker;
	private ChatGridLayout chatGridLayout;
	private LinearLayout chatAddCamereLayout;
	private LinearLayout chatAddGallaryLayout;
	private LinearLayout chatAddChangeVoiceLayout;
	private Button mVoiceSendBtn;
	private ImageView mAddBtn;
	private EditText mContentEdit;
	private Button mSwitchVoiceTextBtn;
	private ChatBoxManager boxManager;

	private boolean isLoad;

	private String commenterid;
	private String commenterName;

	private int commentCount = 0;
	public final static int VOICE_MODEL = 0;
	public final static int TEXT_MODEL = 1;
	private int mCurrentModel = VOICE_MODEL;

	public final static int MSG_DELETE_MSG = 11001;
	public final static int MSG_DOWNLOAD_MSG = 11002;
	public final static int MSG_SEND_MSG = 11003;
	public final static int MSG_FAVORITE_MSG = 11004;
	public final static int MSG_REPORT_MSG = 11005;
	public final static int MSG_REMOVE_MSG = 11006;
	public final static int MSG_VOICE_TEXT_MSG = 11007;

	public final static int MSG_TYPE = 11010;
	public final static int MSG_ZAN = 11011;
	private final static int FAVORITE_SUCCESS = 15454;
	private final static int REPORT_SUCCESS = 15455;

	protected SpeexPlayerWrapper speexPlayerWrapper;

	protected MyAdapter mAdapter;
	protected PullToRefreshListView mListView;

	private boolean isShowTopCover = false;
	private View viewCoverTop;
	private View viewCoverBottom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_chat_main);
		mLogin = DamiCommon.getLoginResult(mContext);
		messageInfo = (MessageInfo) getIntent().getSerializableExtra(INTENT_MESSAGE_KEY);
		mTribe = (Tribe) getIntent().getSerializableExtra(INTENT_TRIBE_KEY);
		mIdentity = (Identity) getIntent().getSerializableExtra(INTENT_IDENTITY_KEY);
		mChatType = getIntent().getIntExtra(INTENT_CHATTYPE_KEY, 0);
		initComponent();
		mListView.getRefreshableView().addHeaderView(creatHeaderView());
		bindView();

		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
		getmessageInfos();
		mLogin = DamiCommon.getLoginResult(mContext);
		// getZanList();
		// initVoicePlayMode();
		// updateVoicePlayModeState(isModeInCall);
	}

	protected void initComponent() {
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.message_detail);
		View view = mTitleBar.addRightImageButtonView(R.drawable.icon_chat_title_more);
		view.setId(R.id.ab_chat_more);
		view.setOnClickListener(this);

		mSwitchVoiceTextBtn = (Button) findViewById(R.id.chat_box_btn_switch_voice_text);
		mSwitchVoiceTextBtn.setOnClickListener(this);

		mContentEdit = (EditText) findViewById(R.id.chat_box_edit_keyword);
		mContentEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boxManager.editClick();
			}
		});

		mSendTextBtn = (Button) findViewById(R.id.send_text_btn);
		mSendTextBtn.setOnClickListener(this);
		mContentEdit.setOnEditorActionListener(mEditActionLister);
		mContentEdit.setVisibility(View.GONE);
		mContentEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (s.length() != 0) {
					mSendTextBtn.setVisibility(View.VISIBLE);
					mAddBtn.setVisibility(View.INVISIBLE);
				} else {
					mSendTextBtn.setVisibility(View.GONE);
					mAddBtn.setVisibility(View.VISIBLE);
				}
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
		mEmotionPicker = (EmotionPicker) findViewById(R.id.emotion_picker);
		mEmotionPicker.setEditText(this, null, mContentEdit);
		mEmotionBtn = (Button) findViewById(R.id.emotion_btn);
		mEmotionBtn.setOnClickListener(this);

		chatGridLayout = (ChatGridLayout) findViewById(R.id.chat_grid_layout);
		chatGridLayout.setEditText(mContentEdit);

		chatAddCamereLayout = (LinearLayout) findViewById(R.id.chat_add_camera);
		chatAddCamereLayout.setOnClickListener(this);
		chatAddGallaryLayout = (LinearLayout) findViewById(R.id.chat_add_gallary);
		chatAddGallaryLayout.setOnClickListener(this);
		chatAddChangeVoiceLayout = (LinearLayout) findViewById(R.id.chat_add_change_voice);
		chatAddChangeVoiceLayout.setOnClickListener(this);

		mVoiceSendBtn = (Button) findViewById(R.id.chat_box_btn_voice);
		mVoiceSendBtn.setText(mContext.getString(R.string.pressed_to_record));
		mVoiceSendBtn.setVisibility(View.VISIBLE);
		mVoiceSendBtn.setOnTouchListener(new OnVoice());

		mAddBtn = (ImageView) findViewById(R.id.chat_box_btn_add);
		mAddBtn.setOnClickListener(this);

		boxManager = new ChatBoxManager(this, mContentEdit, mSwitchVoiceTextBtn, mEmotionPicker, chatGridLayout,
				mEmotionBtn, mVoiceSendBtn);

		mListView = (PullToRefreshListView) findViewById(R.id.listview);
		mListView.getRefreshableView().setDivider(null);
		mListView.getRefreshableView().setSelector(mContext.getResources().getDrawable(R.color.transparent));

		mListView.setPullRefreshEnabled(false); // 下拉刷新，启用
		mListView.setPullLoadEnabled(false);// 上拉刷新，禁止
		mListView.setScrollLoadEnabled(true);// 滑动到底部自动刷新，启用
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getmessageInfos();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				getmessageInfos();
			}
		});

		speexPlayerWrapper = new SpeexPlayerWrapper(mContext, new OnDownLoadCallback() {
			@Override
			public void onSuccess(MessageInfo messageInfo) {
				// TODO Auto-generated method stub
				downVoiceSuccess(messageInfo);
			}
		});

		speexPlayerWrapper.setPlayCallback(new PlayCallback());

		speexRecorder = new SpeexRecorderWrapper(this);
		speexRecorder.setRecordallback(recordCallback);
		recordDialog = new RecordDialog(this);

		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				mContentEdit.setHint(getString(R.string.comment_reply_info_colon)
						+ messageInfos.get(position).displayname);
				mVoiceSendBtn.setText(getString(R.string.comment_reply_info_colon)
						+ messageInfos.get(position).displayname);
				commenterid = messageInfos.get(position).from;
				commenterName = messageInfos.get(position).displayname;
			}
		});
		getZanList();
	}

	/**
	 * 下载成功后修改消息状态，更新数据库并播放声音
	 * 
	 * @param msg
	 * @param type
	 */
	private void downVoiceSuccess(final MessageInfo msg) {
		SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable messageTable = new MessageTable(dbDatabase);
		messageTable.update(msg);
		if (speexPlayerWrapper.getMessageTag().equals(msg.tag)) {
			speexPlayerWrapper.start(msg);
			msg.isReadVoice = 1;
			messageTable.update(msg);
			mAdapter.notifyDataSetChanged();
		}
	}

	private boolean isChangeVoice = true;
	private SpeexRecorderWrapper speexRecorder;
	private MediaUIHeper.RecordCallback recordCallback = new MediaUIHeper.RecordCallback() {
		@Override
		public void onStart() {
			Log.d(TAG, "call back start");
		}

		@Override
		public void onStop(float recordingTime, String path) {
			Log.d(TAG, "call back stop " + recordingTime + "  " + path);
			if (recordingTime < SpeexRecorderWrapper.MIN_TIME) {
				Toast.makeText(mContext, getString(R.string.record_time_too_short), Toast.LENGTH_SHORT).show();
				return;
			}
			File file = new File(path);
			if (file.exists()) {
				sendVoiceFile(MessageType.VOICE, path, (int) speexRecorder.getRecordTime(), isChangeVoice);
				afterSendChnageState();
			}
		}

		@Override
		public void onRecording(int volume, float time) {
			Log.d(TAG, "call back recording" + volume + "  " + time);
			if (recordDialog.isShowing()) {
				recordDialog.setDialogImg(volume);
			}
		}
	};

	private void afterSendChnageState() {
		mVoiceSendBtn.setText(mContext.getString(R.string.pressed_to_record));
		mContentEdit.setHint(mContext.getString(R.string.input_message_hint));
		mContentEdit.setText("");
	}

	private RecordDialog recordDialog;

	class OnVoice implements OnTouchListener {
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				speexPlayerWrapper.stop();
				speexRecorder.start();
				recordDialog.showDialog();
				break;
			case MotionEvent.ACTION_UP:
				if (recordDialog.isPositionInDialog(x, y, mVoiceSendBtn)) {
					speexRecorder.setCancelByUser(true);
				}
				speexRecorder.stop();
				recordDialog.cancelDialog();
				break;
			case MotionEvent.ACTION_MOVE:
				if (recordDialog.isPositionInDialog(x, y, mVoiceSendBtn)) {
					recordDialog.showCancalView();
				} else {
					recordDialog.showRecordView();
				}
				break;
			}
			return false;
		}
	}

	protected EditText.OnEditorActionListener mEditActionLister = new EditText.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEND && mContentEdit.getVisibility() == View.VISIBLE) {
				boxManager.hideAll();
				return true;
			}
			return false;
		}
	};

	private View creatHeaderView() {
		// mVoiceModeImage = (ImageView) findViewById(R.id.voice_mode_image);
		View view = mInflater.inflate(R.layout.activity_chat_comment_layout, null);
		zanText = (TextView) view.findViewById(R.id.zan_text);
		zanText.setOnTouchListener(new BlockTouchListener());
		tvText = (TextView) view.findViewById(R.id.iv_chat_text);
		tvVoiceLength = (TextView) view.findViewById(R.id.tv_chat_voice_time_length);
		ivVoice = (ImageView) view.findViewById(R.id.iv_chat_voice);
		ivPhoto = (ImageView) view.findViewById(R.id.iv_chat_photo);
		headImageView = (ImageView) view.findViewById(R.id.iv_chat_talk_img_head);
		nameTextView = (TextView) view.findViewById(R.id.tv_user_name);
		msgInfoLayout = view.findViewById(R.id.rl_msg_info_holder);

		commentCountText = (TextView) view.findViewById(R.id.chat_comment_count);
		likeCountText = (TextView) view.findViewById(R.id.chat_zan_count);
		favouriteCountText = (TextView) view.findViewById(R.id.chat_favourite_count);
		favouriteCountText.setText(String.valueOf(messageInfo.favoriteCount));

		commentCountText.setText(String.valueOf(messageInfo.commentCount));

		zanCountLayout = (LinearLayout) view.findViewById(R.id.zan_count_layout);
		zanCountLayout.setOnClickListener(this);
		zanCountBtn = (ImageView) view.findViewById(R.id.zan_count_btn);
		zanCountBtn.setOnClickListener(this);

		commentCountLayout = (LinearLayout) view.findViewById(R.id.comment_count_layout);
		commentCountLayout.setOnClickListener(this);
		commentCountBtn = (ImageView) view.findViewById(R.id.comment_count_btn);
		commentCountBtn.setOnClickListener(this);

		favoriteCountLayout = (LinearLayout) view.findViewById(R.id.favourite_count_layout);
		favoriteCountLayout.setOnClickListener(this);
		favoriteCountBtn = (ImageView) view.findViewById(R.id.favorite_count_btn);
		favoriteCountBtn.setOnClickListener(this);

		layoutZan = (ViewGroup) view.findViewById(R.id.ll_zan);
		viewCoverTop = view.findViewById(R.id.view_cover_top);
		viewCoverBottom = view.findViewById(R.id.view_cover_bottom);
		
		bindZanCommentBorderView();
		return view;

	}

	private View creatFooterView() {
		View view = new View(mContext);
		view.setBackgroundResource(R.drawable.icon_dynamic_detail_cover_bottom);
		view.setPadding(MyUtils.dip2px(mContext, 60), 0, MyUtils.dip2px(mContext, 10), 0);
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(lp);
		return view;
	}

	private void initialSendIdAndName() {
		commenterid = "";
		commenterName = "";
		mVoiceSendBtn.setText(mContext.getString(R.string.pressed_to_record));
		mContentEdit.setHint(mContext.getString(R.string.input_message_hint));
	}

	// @Override
	// protected void sendFilePath(MessageInfo messageInfo, int isResend) {
	// // sendMessage(messageInfo, isResend);
	// }

	public void hideSoftKeyboard() {
		hideSoftKeyboard(getCurrentFocus());
	}

	public void hideSoftKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public void showSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContentEdit.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}

	private void notHideViews(int which) {
		ivPhoto.setVisibility(View.GONE);
		tvText.setVisibility(View.GONE);
		tvVoiceLength.setVisibility(View.GONE);
		ivVoice.setVisibility(View.GONE);
		switch (which) {
		case MessageType.TEXT:
			tvText.setVisibility(View.VISIBLE);
			break;
		case MessageType.PICTURE:
			ivPhoto.setVisibility(View.VISIBLE);
			break;
		case MessageType.VOICE:
			ivVoice.setVisibility(View.VISIBLE);
			tvVoiceLength.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	private void bindView() {

		if (!TextUtils.isEmpty(messageInfo.headImgUrl)) {
			headImageView.setTag(messageInfo.headImgUrl);
			ImageLoaderUtil.displayImage(messageInfo.headImgUrl, headImageView);
		}
		nameTextView.setText(messageInfo.displayname);
		tvText.setOnTouchListener(blockTouchListener);
		notHideViews(messageInfo.fileType);
		switch (messageInfo.fileType) {
		case MessageType.TEXT:
			tvText.setText(MyTextUtils.addHttpLinks(messageInfo.content));
			tvText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			break;
		case MessageType.VOICE:
			tvVoiceLength.setText(messageInfo.voiceTime + "''");
			msgInfoLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					speexPlayerWrapper.start(messageInfo);
				}
			});

			AnimationDrawable drawable = (AnimationDrawable) ivVoice.getDrawable();
			if (speexPlayerWrapper.isPlay()) {
				drawable.start();
			} else {
				drawable.stop();
				drawable.selectDrawable(0);
			}
			break;
		case MessageType.PICTURE:
			final String path = messageInfo.imgUrlS.trim();

			ImageLoaderUtil.displayImage(path, ivPhoto);
			if (path.startsWith("http://")) {
				ImageLoaderUtil.displayImage(path, ivPhoto);
			}
			ivPhoto.setTag(messageInfo);
			ivPhoto.setOnClickListener(photoClickListener);
			break;
		default:
			break;
		}

		if (messageInfo.isAgree == 0) {
			zanCountBtn.setImageResource(R.drawable.icon_msg_detail_zan_normal);
		} else {
			zanCountBtn.setImageResource(R.drawable.icon_msg_detail_zan_active);
		}
		if (messageInfo.isfavorite == 0) {
			favoriteCountBtn.setImageResource(R.drawable.icon_msg_detail_favorite_normal);
		} else {
			favoriteCountBtn.setImageResource(R.drawable.icon_msg_detail_favorite_active);
		}
	}
	
	private void bindZanCommentBorderView() {
		if(zanList.size() == 0) {
			layoutZan.setVisibility(View.GONE);
			viewCoverBottom.setVisibility(View.GONE);
			if (messageInfos.size() == 0) {
				viewCoverTop.setVisibility(View.GONE);
			} else {
				viewCoverTop.setVisibility(View.VISIBLE);
			}
		} else {
			layoutZan.setVisibility(View.VISIBLE);
			viewCoverTop.setVisibility(View.VISIBLE);
			if (messageInfos.size() != 0) {
				layoutZan.getChildAt(1).setVisibility(View.VISIBLE);
				viewCoverBottom.setVisibility(View.GONE);
			} else {
				layoutZan.getChildAt(1).setVisibility(View.GONE);
				viewCoverBottom.setVisibility(View.VISIBLE);
			}
		}
	}
	

	private OnClickListener photoClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			List<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
			messageInfos.add((MessageInfo) v.getTag());
			Intent intent = new Intent(mContext, ShowImagesActivity.class);
			intent.putExtra("msgList", (Serializable) messageInfos);
			intent.putExtra("position", 0);
			mContext.startActivity(intent);
		}
	};

	private CharSequence setLinkOfSpannable(CharSequence charSequence) {
		SpannableString spannable = new SpannableString(charSequence);
		Matcher matcher = Patterns.WEB_URL.matcher(spannable);
		while (matcher.find()) {
			int s = matcher.start();
			int e = matcher.end();
			spannable.setSpan(urlClickSpan, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return spannable;
	}

	//
	// private void updateZanView() {
	// // if (animator != null) {
	// // animator.end();
	// // }
	// // likeCountBtn.clearAnimation();
	// likeCountBtn.setEnabled(true);
	// messageInfo.agreeCount = zanList.size();
	// likeCountText.setText(String.valueOf(zanList.size()));
	// likeCountBtn.setImageDrawable(this.getResources().getDrawable(R.drawable.zan_btn));
	// if (zanList.size() > 0) {
	// chatLikeLayout.setVisibility(View.VISIBLE);
	// zanText.setText(getColorfulClickText());
	// for (MessageInfo info : zanList) {
	// if (info.uid.equals(mLogin.uid)) {
	// likeCountBtn.setImageDrawable(this.getResources().getDrawable(R.drawable.zan_btn_on));
	// }
	// }
	// } else {
	// chatLikeLayout.setVisibility(View.GONE);
	// }
	// }
	//
	private Spannable getColorfulClickText() {
		int dotlen = "，".length();
		StringBuilder sb = new StringBuilder();
		for (MessageInfo info : zanList) {
			sb.append(info.displayname);
			sb.append("，");
		}
		SpannableString s = new SpannableString(sb.subSequence(0, sb.length() - 1));// 去掉最后一个逗号
		int offset = 0;
		int namelen;
		for (int i = 0, len = zanList.size(); i < len; i++) {
			final int index = i;
			final MessageInfo info = zanList.get(i);
			namelen = info.displayname.length();
			s.setSpan(new ForegroundColorSpan(Color.BLUE), offset, offset + namelen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			s.setSpan(new ClickableSpan() {
				@Override
				public void updateDrawState(TextPaint ds) {
					super.updateDrawState(ds);
					ds.setUnderlineText(false);
				}

				@Override
				public void onClick(View widget) {
					// TODO Auto-generated method stub
					if (!TextUtils.isEmpty(info.role) && !info.role.equals("null") && Integer.valueOf(info.role) > 0) {
						goToUserActivity(info.uid);
					}
				}

			}, offset, offset + namelen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			offset = offset + namelen + dotlen;
		}
		return s;
	}

	private ImageView chatCommentIcon;
	private boolean onBottom = false;

	private void bindCommentsView() {
		// chatCommentIcon = (ImageView) findViewById(R.id.chat_comment_icon);
		mAdapter = new MyAdapter();
		// mListView = (MyListView) findViewById(R.id.chat_comment_list);
		mListView.setAdapter(mAdapter);
		// mListView.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		// mContentEdit.setHint(getString(R.string.comment_reply_info_colon)
		// + messageInfos.get(position).displayname);
		// mVoiceSendBtn.setText(getString(R.string.comment_reply_info_colon)
		// + messageInfos.get(position).displayname);
		// commenterid = messageInfos.get(position).from;
		// commenterName = messageInfos.get(position).displayname;
		// }
		// });

	}

	private final static int MAX_SECOND = 10;
	private final static int MIN_SECOND = 2;
	private BlockTouchListener blockTouchListener = new BlockTouchListener();
	private int palyedPosition;

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// if (messageInfos.size() == 0) {
			// mListView.setBackgroundColor(Color.TRANSPARENT);
			// } else {
			// mListView.setBackgroundColor(Color.parseColor("#f0f0f0"));
			// }
			if (messageInfos.size() == 0) {
				return 0;
			}
			return messageInfos.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return messageInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_chat_detail, null, false);
				viewHolder.messageNameText = (TextView) convertView.findViewById(R.id.chat_comment_not_text_name);
				viewHolder.voiceImageView = (ImageView) convertView.findViewById(R.id.chat_talk_msg_info_msg_voice);
				viewHolder.picImageView = (ImageView) convertView.findViewById(R.id.chat_talk_msg_info_msg_photo);
				viewHolder.rootLayout = (RelativeLayout) convertView.findViewById(R.id.chat_talk_msg_info);
				viewHolder.rootLayoutFake = (RelativeLayout) convertView.findViewById(R.id.chat_talk_msg_info_fake);
				viewHolder.voiceLayout = (RelativeLayout) convertView.findViewById(R.id.chat_voice_layout);
				viewHolder.voiceTimeText = (TextView) convertView.findViewById(R.id.chat_talk_voice_num);
				viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.chat_talk_msg_progressBar);
				viewHolder.resendImageView = (ImageView) convertView.findViewById(R.id.chat_talk_msg_sendsate);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			// viewHolder.messageNameText.setMovementMethod(LinkMovementMethod
			// .getInstance());
			// viewHolder.messageNameText.setAutoLinkMask(Linkify.ALL);

			viewHolder.messageNameText.setCompoundDrawablePadding(MyUtils.dip2px(mContext, 5));
			if (position == 0) {
				viewHolder.messageNameText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_dynamic_comment, 0,
						0, 0);
			} else {
				viewHolder.messageNameText.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.icon_dynamic_comment_transparent, 0, 0, 0);
			}
			if (position == getCount() - 1) {
				viewHolder.rootLayout.setVisibility(View.INVISIBLE);
				viewHolder.rootLayoutFake.setBackgroundResource(R.drawable.fuck);
				return convertView;
			} else {
				viewHolder.rootLayout.setVisibility(View.VISIBLE);
			}

			final MessageInfo commentInfo = messageInfos.get(position);
			viewHolder.messageNameText.setTag(position);
			viewHolder.messageNameText.setOnTouchListener(blockTouchListener);
			String replyFromToText;
			if (!TextUtils.isEmpty(commentInfo.commenterid) && (Integer.valueOf(commentInfo.commenterid)) > 0) {
				replyFromToText = commentInfo.displayname + getString(R.string.comment_reply_info_no_colon)
						+ commentInfo.commentername + ":";
			} else {
				replyFromToText = commentInfo.displayname + ":";
			}

			if (commentInfo.sendState == 0) {
				viewHolder.resendImageView.setVisibility(View.GONE);
				viewHolder.resendImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// showResendDialog(commentInfo);
					}
				});
			} else {
				viewHolder.resendImageView.setVisibility(View.GONE);
			}
			switch (commentInfo.fileType) {
			case MessageType.TEXT:
				viewHolder.messageNameText.setMaxWidth(FeatureFunction.dip2px(mContext, 2000));
				notHideViews(viewHolder, MessageType.TEXT);
				if (commentInfo.mIsShide == 0) {// not hide
					if (!TextUtils.isEmpty(commentInfo.commenterid) && (Integer.valueOf(commentInfo.commenterid)) > 0) {
						viewHolder.messageNameText.setText(buildColorSpannable(commentInfo.displayname,
								commentInfo.commentername,
								EmotionParser.replaceContent(replyFromToText + commentInfo.content)));
					} else {
						viewHolder.messageNameText.setText(buildColorSpannable(commentInfo.displayname,
								EmotionParser.replaceContent(replyFromToText + commentInfo.content)));
					}

				} else {
					viewHolder.messageNameText.setText(mContext.getString(R.string.shide_msg_prompt));
				}
				break;
			case MessageType.VOICE:
				viewHolder.messageNameText.setMaxWidth(FeatureFunction.dip2px(mContext, 150));
				if (mCurrentModel == TEXT_MODEL) {
					replyFromToText = replyFromToText + commentInfo.content;
				}
				if (!TextUtils.isEmpty(commentInfo.commenterid) && (Integer.valueOf(commentInfo.commenterid)) > 0) {
					viewHolder.messageNameText.setText(buildColorSpannable(commentInfo.displayname,
							commentInfo.commentername, replyFromToText));
				} else {
					viewHolder.messageNameText.setText(buildColorSpannable(commentInfo.displayname, replyFromToText));
				}

				if (commentInfo.sendState == 2) {// now sending
					notHideViews(viewHolder, MessageType.MAP);
				} else {
					if (mCurrentModel == VOICE_MODEL) {
						notHideViews(viewHolder, MessageType.VOICE);
						viewHolder.voiceImageView.setLayoutParams(getLayoutParamsOfVoiceView(commentInfo));
						viewHolder.voiceTimeText.setText(commentInfo.voiceTime + "''");
						viewHolder.voiceLayout.setTag(commentInfo);
						viewHolder.voiceLayout.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								palyedPosition = position;
								speexPlayerWrapper.start(commentInfo);
							}
						});
						AnimationDrawable drawable = (AnimationDrawable) viewHolder.voiceImageView.getDrawable();
						if (speexPlayerWrapper.isPlay() && position == palyedPosition) {
							drawable.start();
						} else {
							drawable.stop();
							drawable.selectDrawable(0);
						}
					} else {
						viewHolder.messageNameText.setMaxWidth(FeatureFunction.dip2px(mContext, 1000));
						notHideViews(viewHolder, MessageType.TEXT);
					}
				}
				break;
			case MessageType.PICTURE:
				viewHolder.messageNameText.setMaxWidth(FeatureFunction.dip2px(mContext, 150));
				notHideViews(viewHolder, MessageType.PICTURE);
				viewHolder.picImageView.getLayoutParams().width = commentInfo.imgWidth;
				viewHolder.picImageView.getLayoutParams().height = commentInfo.imgHeight;
				if (!TextUtils.isEmpty(commentInfo.commenterid) && (Integer.valueOf(commentInfo.commenterid)) > 0) {
					viewHolder.messageNameText.setText(buildColorSpannable(commentInfo.displayname,
							commentInfo.commentername, replyFromToText));
				} else {
					viewHolder.messageNameText.setText(buildColorSpannable(commentInfo.displayname, replyFromToText));
				}
				final String path = commentInfo.imgUrlS;
				if (path.startsWith("http://")) {
					viewHolder.progressBar.setVisibility(View.VISIBLE);
					ImageLoaderUtil.displayImageByProgress(path, viewHolder.picImageView, null, viewHolder.progressBar);
				} else {
					ImageLoaderUtil.displayImageByProgress("file://" + path, viewHolder.picImageView, null,
							viewHolder.progressBar);
					viewHolder.progressBar.setVisibility(View.GONE);
				}
				viewHolder.picImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d(TAG, "the sendstate is " + commentInfo.sendState);
						// if (commentInfo.sendState == 1) {
						Intent intent = new Intent(mContext, ShowImagesActivity.class);
						intent.putExtra("position", position);
						intent.putExtra("msgList", (Serializable) messageInfos);
						mContext.startActivity(intent);
						// }
					}
				});
				break;

			default:
				break;
			}
			return convertView;
		}

		public void setCurrentModel(int model) {
			mCurrentModel = model;
			// playListener.setCurrentModel(model);
		}

		private void notHideViews(ViewHolder viewHolder, int which) {
			viewHolder.picImageView.setVisibility(View.GONE);
			viewHolder.voiceLayout.setVisibility(View.GONE);
			viewHolder.progressBar.setVisibility(View.GONE);
			viewHolder.messageNameText.setVisibility(View.VISIBLE);
			switch (which) {
			case MessageType.TEXT:
				viewHolder.messageNameText.setVisibility(View.VISIBLE);
				break;
			case MessageType.PICTURE:
				viewHolder.picImageView.setVisibility(View.VISIBLE);
				break;
			case MessageType.VOICE:
				viewHolder.voiceLayout.setVisibility(View.VISIBLE);
				break;
			case MessageType.MAP:
				viewHolder.progressBar.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}

		private RelativeLayout.LayoutParams getLayoutParamsOfVoiceView(MessageInfo commentInfo) {
			int length = commentInfo.voiceTime;
			float max = mContext.getResources().getDimension(R.dimen.voice_max_length_comment);
			float min = mContext.getResources().getDimension(R.dimen.voice_min_length_comment);
			int width = (int) min;
			if (length >= MIN_SECOND && length <= MAX_SECOND) {
				width += (length - MIN_SECOND) * (int) ((max - min) / (MAX_SECOND - MIN_SECOND));
			} else if (length > MAX_SECOND) {
				width = (int) max;
			}
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
			return lp;
		}

	}

	static class ViewHolder {
		TextView messageNameText;
		ImageView picImageView, voiceImageView, resendImageView;
		TextView voiceTimeText;
		RelativeLayout rootLayout;
		RelativeLayout rootLayoutFake;
		RelativeLayout voiceLayout;
		ProgressBar progressBar;
	}

	// private void showResendDialog(final MessageInfo messageInfo) {
	// final Dialog dlg = new Dialog(mContext, R.style.MMThem_DataSheet);
	// LayoutInflater inflater = (LayoutInflater)
	// mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// // LinearLayout layout = (LinearLayout)
	// // inflater.inflate(R.layout.message_delete_dialog, null);
	// LinearLayout layout = null;
	// final int cFullFillWidth = 10000;
	// layout.setMinimumWidth(cFullFillWidth);
	//
	// final Button deleteBtn = (Button) layout.findViewById(R.id.deletebtn);
	// deleteBtn.setText(mContext.getString(R.string.send));
	// final Button cancelBtn = (Button) layout.findViewById(R.id.cancelbtn);
	//
	// deleteBtn.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// dlg.dismiss();
	// Message message = new Message();
	// // message.what = ChatAdapter.MSG_SEND_MSG;
	// message.obj = messageInfo;
	// mHandler.sendMessage(message);
	// }
	// });
	//
	// cancelBtn.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// dlg.dismiss();
	// }
	// });
	//
	// // set a large value put it in bottom
	// Window w = dlg.getWindow();
	// WindowManager.LayoutParams lp = w.getAttributes();
	// lp.x = 0;
	// final int cMakeBottom = -1000;
	// lp.y = cMakeBottom;
	// lp.gravity = Gravity.BOTTOM;
	// dlg.onWindowAttributesChanged(lp);
	// dlg.setCanceledOnTouchOutside(true);
	// dlg.setCancelable(true);
	//
	// dlg.setContentView(layout);
	// dlg.show();
	// }

	/* 重发信息 */
	private void btnResendAction(MessageInfo messageInfo) {
		if (messageInfo != null) {
			switch (messageInfo.fileType) {
			case MessageType.PICTURE:
			case MessageType.VOICE:
				resendFile(messageInfo);
				break;
			case MessageType.TEXT:
				// sendMessage(messageInfo, 1);
				break;

			default:
				break;
			}
		}
	};

	private void resendFile(MessageInfo messageInfo) {
		try {
			// sendFilePath(messageInfo, 1);
		} catch (Exception e) {
			showToast(mContext.getString(R.string.resend_failed));
		}
	}

	private TextView touchedTextView;

	private class BlockTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			boolean ret = false;
			CharSequence text = ((TextView) v).getText();
			Spannable stext = Spannable.Factory.getInstance().newSpannable(text);
			TextView widget = (TextView) v;
			touchedTextView = widget;
			int action = event.getAction();
			try {
				if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
					int x = (int) event.getX();
					int y = (int) event.getY();

					x -= widget.getTotalPaddingLeft();
					y -= widget.getTotalPaddingTop();

					x += widget.getScrollX();
					y += widget.getScrollY();

					Layout layout = widget.getLayout();
					int line = layout.getLineForVertical(y);
					int off = layout.getOffsetForHorizontal(line, x);

					ClickableSpan[] link = stext.getSpans(off, off, ClickableSpan.class);
					if (link.length != 0) {
						if (action == MotionEvent.ACTION_UP) {
							link[0].onClick(widget);
							stext.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), 0, stext.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							widget.setText(stext);
						} else if (action == MotionEvent.ACTION_DOWN) {
							stext.setSpan(new BackgroundColorSpan(Color.GRAY), stext.getSpanStart(link[0]),
									stext.getSpanEnd(link[0]), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							widget.setText(stext);
						}
						ret = true;
					}
				}
			} catch (NullPointerException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return ret;
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		boolean re = super.dispatchTouchEvent(ev);
		if (ev.getAction() == MotionEvent.ACTION_UP) {

			if (touchedTextView != null) {
				Spannable stext = Spannable.Factory.getInstance().newSpannable(touchedTextView.getText());
				stext.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), 0, stext.length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				touchedTextView.setText(stext);
			}
		}
		return re;
	}

	private class UserClickSpan extends ClickableSpan {

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
		}

		@Override
		public void onClick(View widget) {
			TextView tv = (TextView) widget;
			Spanned s = (Spanned) tv.getText();
			int start = s.getSpanStart(this);
			int end = s.getSpanEnd(this);
			int pos = (Integer) tv.getTag();
			MessageInfo info = messageInfos.get(pos);
			if (start == 0) {
				if (!TextUtils.isEmpty(info.fromrole) && !info.fromrole.equals("null")
						&& Integer.valueOf(info.fromrole) > 0) {
					goToUserActivity(info.from);
				}
			} else {
				if (!TextUtils.isEmpty(info.commenterrole) && !info.commenterrole.equals("null")
						&& Integer.valueOf(info.commenterrole) > 0) {
					goToUserActivity(info.commenterid);
				}
			}

		}
	}

	private UrlClickSpan urlClickSpan = new UrlClickSpan();

	private CharSequence buildColorSpannable(String name1, String name2, CharSequence charSequence) {
		SpannableString spannable = new SpannableString(charSequence);

		spannable.setSpan(new UserClickSpan(), 0, name1.length(), 0);
		int start = name1.length() + getString(R.string.comment_reply_info_no_colon).length();
		spannable.setSpan(new UserClickSpan(), start, start + name2.length(), 0);

		Matcher matcher = Patterns.WEB_URL.matcher(spannable);
		while (matcher.find()) {
			int s = matcher.start();
			int e = matcher.end();
			spannable.setSpan(urlClickSpan, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return spannable;
	}

	private CharSequence buildColorSpannable(String name1, CharSequence charSequence) {
		SpannableString spannable = new SpannableString(charSequence);
		spannable.setSpan(new UserClickSpan(), 0, name1.length(), 0);

		Matcher matcher = Patterns.WEB_URL.matcher(spannable);
		while (matcher.find()) {
			int s = matcher.start();
			int e = matcher.end();
			spannable.setSpan(urlClickSpan, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return spannable;
	}

	private class UrlClickSpan extends ClickableSpan {

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
			ds.setColor(Color.parseColor("#226666"));
		}

		@Override
		public void onClick(View widget) {
			TextView tv = (TextView) widget;
			Spanned s = (Spanned) tv.getText();
			int start = s.getSpanStart(this);
			int end = s.getSpanEnd(this);
			String url = s.subSequence(start, end).toString();
			// Intent intent = new Intent(mContext, ReportDetailActivity.class);
			// intent.putExtra("title", url);
			// intent.putExtra("url", url);
			// startActivity(intent);
		}
	}

	private void goToUserActivity(String uid) {
		Intent i = new Intent();
		i.setClass(ChatCommentsActivity.this, UserInfoActivity.class);
		i.putExtra("uid", uid);
		startActivity(i);
	}

	// private Handler mHandler = new Handler() {
	//
	// @Override
	// public void handleMessage(Message msg) {
	// // TODO Auto-generated method stub
	// switch (msg.what) {
	// case MainActivity.LIST_LOAD_REFERSH:
	// break;
	// case MainActivity.LIST_LOAD_FIRST:
	// isLoad = false;
	// hideProgressDialog();
	// if (messageInfos.size() == 0) {
	// chatCommentIcon.setVisibility(View.GONE);
	// } else {
	// chatCommentIcon.setVisibility(View.VISIBLE);
	// }
	// mAdapter.notifyDataSetChanged();
	// break;
	// case MainActivity.LIST_LOAD_MORE:
	// isLoad = false;
	// mAdapter.notifyDataSetChanged();
	// break;
	// case MainActivity.SHOW_PROGRESS_DIALOG:
	// showProgressDialog(getString(R.string.xlistview_header_hint_loading));
	// break;
	// case MainActivity.HIDE_PROGRESS_DIALOG:
	// likeCountBtn.setEnabled(true);
	// hideProgressDialog();
	// break;
	// case ChatMainActivity.SEND_SUCCESS:
	// MessageInfo commentInfo = (MessageInfo) msg.obj;
	// if (!TextUtils.isEmpty(mNewUrl)) {
	// shareNews(commentInfo.content + " " + mNewUrl);
	// mNewUrl = "";
	// }
	// updateCommentInfoState(commentInfo);
	// updateNewMessage(commentInfo);
	// modifyMessageState();
	// break;
	// case ZAN_SUCCESS:
	// Toast.makeText(mContext, getString(R.string.zan_success),
	// Toast.LENGTH_SHORT).show();
	// hideProgressDialog();
	// getZanList();
	// break;
	// case ZAN_CANCEL_SUCCESS:
	// Toast.makeText(mContext, getString(R.string.zan_cancel_success),
	// Toast.LENGTH_SHORT).show();
	// getZanList();
	// break;
	// case ZAN_FAILED:
	// Toast.makeText(mContext, getString(R.string.zan_failed),
	// Toast.LENGTH_SHORT).show();
	// break;
	// case GET_ZAN_LIST_SUCCESS:
	// updateZanView();
	// break;
	//
	// case ChatMainActivity.SHOW_SENSTIVE_WORD_ERROR: {
	// String control_detail = (String) msg.obj;
	// Toast.makeText(mContext, control_detail, Toast.LENGTH_LONG).show();
	// }
	// break;
	// case ChatMainActivity.SHOW_MEETING_TIME_ERROR: {
	// messageInfos.remove(0);
	// mAdapter.notifyDataSetChanged();
	// String control_detail = (String) msg.obj;
	// Toast.makeText(mContext, control_detail, Toast.LENGTH_LONG).show();
	// }
	// break;
	// case MainActivity.MSG_NETWORK_ERROR:
	// likeCountBtn.setEnabled(true);
	// hideProgressDialog();
	// Toast.makeText(mContext, R.string.network_error,
	// Toast.LENGTH_LONG).show();
	// break;
	//
	// case ChatAdapter.MSG_SEND_MSG:
	// MessageInfo msgInfo = (MessageInfo) msg.obj;
	// msgInfo.sendState = 2;
	// mAdapter.notifyDataSetChanged();
	// msgInfo.title = mTribe.name;
	// if (mChatType == MEETING_CHAT_TYPE && mTribe.role != 0) {
	// msgInfo.displayname = mLogin.displayname;
	// msgInfo.headImgUrl = mLogin.mSmallHead;
	// } else {
	// msgInfo.displayname = mIdentity.name;
	// msgInfo.headImgUrl = mIdentity.heading;
	// msgInfo.heroid = mIdentity.id;
	// }
	// btnResendAction(msgInfo);
	// break;
	//
	// // from menu dialog
	// case ChatAdapter.MSG_DELETE_MSG:
	//
	// break;
	// case ChatAdapter.MSG_FAVORITE_MSG: {// 在adapter点击收藏
	// MessageInfo msgInfo1 = (MessageInfo) msg.obj;
	// Message msgF = new Message();
	// msgF.what = MainActivity.SHOW_PROGRESS_DIALOG;
	// msgF.obj = mContext.getString(R.string.send_loading);
	// mHandler.sendMessage(msgF);
	// favorite(msgInfo1);
	// }
	// break;
	// case ChatAdapter.MSG_REPORT_MSG: {
	// MessageInfo msgInfo1 = (MessageInfo) msg.obj;
	// Message msgR = new Message();
	// msgR.obj = mContext.getString(R.string.send_loading);
	// msgR.what = MainActivity.SHOW_PROGRESS_DIALOG;
	// mHandler.sendMessage(msgR);
	//
	// final String[] levelArray =
	// mContext.getResources().getStringArray(R.array.report_message_cause);
	// report(msgInfo1, levelArray[msg.arg1]);
	// }
	// break;
	// case ChatAdapter.MSG_TYPE: {// 评论
	// initialSendIdAndName();
	// }
	// break;
	// case ChatAdapter.MSG_ZAN:
	// zanMessage(messageInfo);
	// break;
	// case MSG_VOICE_TEXT_MSG:
	// if (mCurrentModel == VOICE_MODEL) {
	// mCurrentModel = TEXT_MODEL;
	// if (mAdapter != null) {
	// mAdapter.setCurrentModel(mCurrentModel);
	// mAdapter.notifyDataSetChanged();
	// }
	// } else if (mCurrentModel == TEXT_MODEL) {
	// mCurrentModel = VOICE_MODEL;
	// if (mAdapter != null) {
	// mAdapter.setCurrentModel(mCurrentModel);
	// mAdapter.notifyDataSetChanged();
	// }
	// }
	// bindView();
	// break;
	// case MSG_REMOVE_MSG:
	// removeMessage(messageInfo);
	// break;
	//
	// // ===result
	// case FAVORITE_SUCCESS:
	// hideProgressDialog();
	// MessageInfo favoriteMessage = (MessageInfo) msg.obj;
	// Toast.makeText(mContext, R.string.favorite_success,
	// Toast.LENGTH_SHORT).show();
	// updateFavoriteCount(favoriteMessage, false);
	// favouriteCountText.setText(String.valueOf(messageInfo.favoriteCount));
	// Intent favoriteIntent = new
	// Intent(ChatMainActivity.ACTION_COMMENT_OR_ZAN_OR_FAVOURITE);
	// favoriteIntent.putExtra("message",
	// ChatCommentsActivity.this.messageInfo);
	// mContext.sendBroadcast(favoriteIntent);
	// break;
	//
	// case REPORT_SUCCESS:
	// hideProgressDialog();
	// Toast.makeText(mContext, R.string.report_success,
	// Toast.LENGTH_SHORT).show();
	// break;
	// case MainActivity.MSG_LOAD_ERROR:
	// hideProgressDialog();
	// String error_Detail = (String) msg.obj;
	// if (error_Detail != null && !error_Detail.equals("")) {
	// Toast.makeText(mContext, error_Detail, Toast.LENGTH_LONG).show();
	// } else {
	// Toast.makeText(mContext, R.string.load_error, Toast.LENGTH_LONG).show();
	// }
	// break;
	// default:
	// break;
	// }
	// }
	//
	// };

	private void removeMessage(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		MessageInfo message = table.query(messageInfo.tag);
		if (message != null) {
			table.delete(messageInfo);
			Intent intent = new Intent(ChatMainActivity.ACTION_MESSAGE_DELETE);
			intent.putExtra("tag", message.tag);
			mContext.sendBroadcast(intent);
			mContext.sendBroadcast(new Intent(TribeActivity.UPDATE_COUNT_ACTION));
			ChatCommentsActivity.this.finish();
		}
	}

	private void updateFavoriteCount(MessageInfo favoriteMessage, boolean isFavorite) {
		if (isFavorite) {
			messageInfo.favoriteCount = favoriteMessage.favoriteCount;
		} else {
			messageInfo.favoriteCount++;
			messageInfo.isfavorite = 1;
			// updateMessage(messageInfo);
		}
	}

	// private void shareNews(final String content) {
	// new Thread() {
	// @Override
	// public void run() {
	// try {
	// DamiCommon.getDamiInfo().shareContent(content);
	// } catch (DamiException e) {
	// e.printStackTrace();
	// }
	// }
	// }.start();
	// }

	private void updateCommentInfoState(MessageInfo messageInfo) {
		MessageInfo tempInfo = this.messageInfo;
		updateCommentCount(tempInfo);
		if (messageInfos == null || messageInfos.size() == 0) {
			return;
		}
		for (int j = 0; j < messageInfos.size(); j++) {
			if (messageInfo.tag.equals(messageInfos.get(j).tag)) {
				MessageInfo comment = messageInfos.get(j);
				comment.sendState = messageInfo.sendState;
				comment.id = messageInfo.id;
				comment.imgUrlS = messageInfo.imgUrlS;
				comment.imgUrlL = messageInfo.imgUrlL;
				comment.imgWidth = messageInfo.imgWidth;
				comment.imgHeight = messageInfo.imgHeight;
				comment.voiceUrl = messageInfo.voiceUrl;
				Log.d(TAG, "voice url=" + messageInfo.voiceUrl);
				comment.readState = messageInfo.readState;
				comment.time = messageInfo.time;
				comment.displayname = messageInfo.displayname;
				comment.headImgUrl = messageInfo.headImgUrl;
				mAdapter.notifyDataSetChanged();
				break;
			}
		}
	}

	private void modifyMessageState() {
		Intent favoriteIntent = new Intent(ChatMainActivity.ACTION_COMMENT_OR_ZAN_OR_FAVOURITE);
		favoriteIntent.putExtra("message", messageInfo);
		mContext.sendBroadcast(favoriteIntent);
	}

	private void updateCommentCount(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.updateCommentCount(messageInfo);
	}

	private void updateZanCount(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.updateAgreeCount(messageInfo);
	}

	private List<MessageInfo> messageInfos = new ArrayList<MessageInfo>();

	protected String getMessageMaxId() {
		String maxID = "";
		if (messageInfos != null && messageInfos.size() != 0) {
			// maxID = messageInfos.get(0).id;
			maxID = messageInfos.get(messageInfos.size() - 1).id;
		}
		return maxID;
	}

	private boolean isFull = false;
	private int page = 0;

	private void getmessageInfos() {

		String maxID = getMessageMaxId();
		DamiInfo.getCommentList(messageInfo.id, maxID, "", new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final ChatMessageBean data = (ChatMessageBean) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						isFull = data.pageInfo.hasMore == 0;// true not has more
															// page
						messageInfos.addAll(data.data);
						notifyDataSetChanged();
						mListView.getRefreshableView().setSelection(data.data.size());
					} else {
						isFull = true;
					}

				} else {
					otherCondition(data.state, ChatCommentsActivity.this);
				}
			}

			@Override
			public void onFinish() {
				mListView.onPullComplete();
				mListView.setHasMoreData(!isFull);
			}
		});
	}

	private boolean emotionShowFlag = false;
	private AlphaAnimation alphaAnim = null;
	private ObjectAnimator animator;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// case R.id.rightlayout:
		// showMenuDialog(messageInfo);
		// break;
		case R.id.comment_count_layout:
		case R.id.comment_count_btn:
			initialSendIdAndName();
			break;
		case R.id.zan_count_layout:
		case R.id.zan_count_btn:
			animator = ObjectAnimator.ofFloat(zanCountBtn, "rotationY", 0, 360, 0).setDuration(1500);
			// animator.setRepeatCount(-1);
			animator.start();
			zanMessage(messageInfo);
			break;

		case R.id.favorite_count_btn:
		case R.id.favourite_count_layout:
			animator = ObjectAnimator.ofFloat(favoriteCountBtn, "rotationY", 0, 360, 0).setDuration(1500);
			animator.start();
			favoriteMessage(messageInfo);
			break;
		// case R.id.send_text_btn:
		// mEmotionPicker.hide(ChatCommentsActivity.this);
		// mEmotionBtn.setBackgroundResource(R.drawable.chatting_biaoqing_btn_normal);
		// emotionShowFlag = false;
		// hideSoftKeyboard();
		// sendText();

		// break;
		// case R.id.emotion_btn:
		// if (!emotionShowFlag) {
		// hideSoftKeyboard();
		// mEmotionPicker.show(ChatCommentsActivity.this);
		// mEmotionBtn.setBackgroundResource(R.drawable.chatting_biaoqing_btn_enable);
		// emotionShowFlag = true;
		// } else {
		// mEmotionPicker.hide(ChatCommentsActivity.this);
		// mEmotionBtn.setBackgroundResource(R.drawable.chatting_biaoqing_btn_normal);
		// emotionShowFlag = false;
		// }
		// break;
		// case R.id.chat_box_btn_text:
		// switchTextVoice();
		// break;
		// case R.id.chat_box_btn_add:
		// showTypeDialog();
		// break;
		// case R.id.left_btn:
		// hideSoftKeyboard();
		// this.finish();
		// break;
		case R.id.chat_add_camera:
			btnCameraAction();
			boxManager.hideAddGrid();
			break;
		case R.id.chat_add_gallary:
			btnPhotoAction();
			boxManager.hideAddGrid();
			break;
		case R.id.chat_add_change_voice:
			TextView view = (TextView) chatAddChangeVoiceLayout.getChildAt(0);
			if (!isChangeVoice) {
				view.setText(getString(R.string.change_voice));
				isChangeVoice = true;
			} else {
				view.setText(getString(R.string.not_change_voice));
				isChangeVoice = false;
			}
			boxManager.hideAddGrid();
			break;
		case R.id.send_text_btn:
			boxManager.hideAll();
			sendText(mContentEdit.getText().toString());
			afterSendChnageState();
			break;
		case R.id.emotion_btn:
			boxManager.emotionClick();
			break;
		case R.id.chat_box_btn_switch_voice_text:
			boxManager.switchTextVoice();
			break;
		case R.id.chat_box_btn_add:
			boxManager.addGridClick();
			break;

		case R.id.ab_chat_more:
			showItemLongClickDialog(messageInfo);
			break;
		default:
			break;
		}
	}

	protected void btnCameraAction() {
		getImageFromCamera();
	}

	protected void btnPhotoAction() {
		getImageFromGallery();
	}

	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		TEMP_FILE_NAME = FeatureFunction.getPhotoFileName();
		if (FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY)) {
			File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY,
					FeatureFunction.getPhotoFileName());
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, REQUEST_GET_IMAGE_BY_CAMERA);
		}
	}

	private void getImageFromGallery() {
		Intent intent = new Intent();
		intent.putExtra(LocalPicPathActivity.KEY_PIC_REQUIRE_TYPE, LocalPicPathActivity.PIC_REQUIRE_MUTI);
		intent.setClass(mContext, LocalPicPathActivity.class);
		startActivityForResult(intent, REQUEST_GET_BITMAP_LIST);
	}

	static final int REQUEST_GET_IMAGE_BY_CAMERA = 1002;
	static final int REQUEST_ROTATE_IMAGE = 1003;
	static final int REQUEST_GET_URI = 101;
	public static final int REQUEST_GET_BITMAP = 124;
	public static final int REQUEST_GET_BITMAP_LIST = 125;

	private String TEMP_FILE_NAME = "header.jpg";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_GET_IMAGE_BY_CAMERA:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					Uri uri = data.getData();
					if (!TextUtils.isEmpty(uri.getAuthority())) {
						Cursor cursor = getContentResolver().query(uri, new String[] { MediaColumns.DATA }, null, null,
								null);
						if (null == cursor) {
							return;
						}
						cursor.moveToFirst();
						String path = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));
						String extension = path.substring(path.lastIndexOf("."), path.length());
						if (FeatureFunction.isPic(extension)) {
							sendPicFile(MessageType.PICTURE, path);
						}
					}
					return;
				} else {
					// Here if we give the uri, we need to read it
					String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY
							+ TEMP_FILE_NAME;
					String extension = path.substring(path.indexOf("."), path.length());
					if (FeatureFunction.isPic(extension)) {
						Intent intent = new Intent();
						intent.putExtra(RotateImageActivity.KEY_IMAGE_PATH, path);
						intent.setClass(ChatCommentsActivity.this, RotateImageActivity.class);
						startActivityForResult(intent, REQUEST_ROTATE_IMAGE);
					}
				}

			}
			break;
		case REQUEST_ROTATE_IMAGE:
			if (resultCode == RESULT_OK) {
				String path = data.getStringExtra(RotateImageActivity.KEY_IMAGE_PATH);
				if (!TextUtils.isEmpty(path)) {
					sendPicFile(MessageType.PICTURE, path);
				}
			}
			break;

		case REQUEST_GET_BITMAP_LIST:
			if (resultCode == RESULT_OK) {
				List<String> pathList = data.getStringArrayListExtra(LocalPicActivity.KEY_PIC_SELECT_PATH_LIST);
				for (String path : pathList) {
					if (!TextUtils.isEmpty(path)) {
						sendPicFile(MessageType.PICTURE, path);
					}
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private boolean isTextEmpty(String str) {
		return (str != null)
				&& (str.trim().replaceAll("\r", "").replaceAll("\t", "").replaceAll("\n", "").replaceAll("\f", "")) != "";
	}

	void sendPicFile(int type, String filePath) {
		sendVoiceFile(type, filePath, 0, false);
	}

	void sendText(String str) {
		Log.e("SEND MESSAGE", str);
		if (isTextEmpty(str)) {
			if (str.length() > DamiCommon.MESSAGE_CONTENT_LEN) {
				showToast(mContext.getString(R.string.message_limit_count));
				return;
			}
			MessageInfo msg = buildMessage();
			msg.fileType = MessageType.TEXT;
			msg.content = str;

			addSaveSendMessage(msg);
			initialSendIdAndName();
		}
	}

	void sendVoiceFile(int type, String filePath, int recordTime, boolean isChangeVoice) {
		MessageInfo msg = buildMessage();
		if (type == MessageType.VOICE) {
			msg.voiceUrl = filePath;
			msg.voiceTime = (int) recordTime;
			msg.content = "[" + mContext.getString(R.string.voice) + "]";
			if (isChangeVoice) {
				msg.samplerate = DamiCommon.getRandomSampleRate();
			} else {
				msg.samplerate = 8000;
			}
		} else if (type == MessageType.PICTURE) {
			msg.imgUrlS = filePath;
		}
		msg.isReadVoice = 1;// 自己的语音标记为已读
		msg.fileType = type;
		addSaveSendMessage(msg);
	}

	protected MessageInfo buildMessage() {
		MessageInfo msg = new MessageInfo();
		msg.from = mLogin.uid;
		msg.tag = UUID.randomUUID().toString();
		msg.time = System.currentTimeMillis();
		msg.readState = 1;

		msg.title = mTribe.name;
		msg.to = mTribe.id;
		msg.parentid = messageInfo.id;
		if (mChatType == ChatTribeActivity.CHAT_TYPE_MEETING && mTribe.role != 0) {
			msg.displayname = mLogin.displayName;
			msg.headImgUrl = mLogin.headsmall;
		} else {
			msg.displayname = mIdentity.name;
			msg.headImgUrl = mIdentity.head;
			msg.heroid = mIdentity.id;
		}
		msg.type = mChatType;

		msg.commenterid = commenterid;
		msg.commentername = commenterName;

		return msg;
	}

	/**
	 * 发送并将消息加到消息列表中
	 * 
	 * @param msg
	 */
	protected void addSaveSendMessage(MessageInfo msg) {
		msg.sendState = MessageState.STATE_SENDING;
		addMessageInfo(msg);
		insertMessage(msg);
		sendMessage(msg);
	}

	protected void addMessageInfo(MessageInfo info) {
		Log.d(TAG, "add message");
		messageInfos.add(info);
		notifyDataSetChanged();
		if (messageInfos != null && messageInfos.size() != 0) {
			mListView.getRefreshableView().setSelection(messageInfos.size() - 1);
		}
	}

	protected void insertMessage(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.insert(messageInfo);
	}

	protected void updateNewMessage(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.updateMessage(messageInfo);
	}

	private void sendMessage(final MessageInfo msg) {
		Log.d(TAG, "send voice change file name" + msg.voiceUrl);
		DamiInfo.sendMessage(msg, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				SendMessageResult data = (SendMessageResult) o;
				if (data.state != null) {
					if (data.state.code == 0) {
						MessageInfo messageInfo = data.data;
						messageInfo.sendState = MessageState.STATE_SEND_SUCCESS;
						if (msg.fileType == MessageType.VOICE) {
							String voice = FeatureFunction.generator(messageInfo.voiceUrl);
							FeatureFunction.reNameFile(new File(msg.voiceUrl), voice);
						}
						updateNewMessage(messageInfo);
						modifyMessageState(messageInfo);
						return;
					} else if (data.state.code == 1) {
						sendFailed(msg);
					} else {
						this.otherCondition(data.state, ChatCommentsActivity.this);
					}
					// handleExtralSendSuccessConditon(data, msg);
				}
			}

			@Override
			public void onFailure(Object o) {
				// TODO Auto-generated method stub
				super.onFailure(o);
				sendFailed(msg);
			}

			@Override
			public void onTimeOut() {
				// TODO Auto-generated method stub
				super.onTimeOut();
				sendFailed(msg);
			}
		});

	}

	private void sendFailed(MessageInfo msg) {
		msg.sendState = MessageState.STATE_SEND_FAILED;
		modifyMessageState(msg);
	}

	protected void modifyMessageState(MessageInfo messageInfo) {
		for (int i = 0; i < messageInfos.size(); i++) {
			if (messageInfo.tag.equals(messageInfos.get(i).tag)) {
				MessageInfo tempInfo = messageInfos.get(i);
				tempInfo.sendState = messageInfo.sendState;
				tempInfo.id = messageInfo.id;
				tempInfo.imgUrlS = messageInfo.imgUrlS;
				tempInfo.imgUrlL = messageInfo.imgUrlL;
				tempInfo.imgWidth = messageInfo.imgWidth;
				tempInfo.imgHeight = messageInfo.imgHeight;
				tempInfo.voiceUrl = messageInfo.voiceUrl;
				tempInfo.readState = messageInfo.readState;
				tempInfo.time = messageInfo.time;
				tempInfo.displayname = messageInfo.displayname;
				tempInfo.headImgUrl = messageInfo.headImgUrl;
				tempInfo.isReadVoice = messageInfo.isReadVoice;
				mAdapter.notifyDataSetChanged();
				return;
			}
		}
	}

	private void switchTextVoice() {
		boolean sendVoice = (mContentEdit.getVisibility() == View.VISIBLE);
		if (sendVoice) {
			mSwitchVoiceTextBtn.setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn_normal);
			mEmotionBtn.setVisibility(View.GONE);
			mContentEdit.setVisibility(View.GONE);
			mVoiceSendBtn.setVisibility(View.VISIBLE);
			mEmotionPicker.hide(ChatCommentsActivity.this);
			mEmotionBtn.setBackgroundResource(R.drawable.chatting_biaoqing_btn_normal);
			hideSoftKeyboard();
		} else {
			mSwitchVoiceTextBtn.setBackgroundResource(R.drawable.chatting_setmode_voice_btn_normal);
			mEmotionBtn.setVisibility(View.VISIBLE);
			mVoiceSendBtn.setVisibility(View.GONE);
			mContentEdit.setVisibility(View.VISIBLE);
			mContentEdit.setFocusable(true);
			mContentEdit.setFocusableInTouchMode(true);
			mContentEdit.requestFocus();
			// hideSoftKeyboard();
			showSoftKeyboard();
		}
	}

	private void showTypeDialog() {
		final Dialog dlg = new Dialog(mContext, R.style.MMThem_DataSheet);
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.chat_add_menu_dialog, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);

		final Button tpyeBtn = (Button) layout.findViewById(R.id.sendType);
		tpyeBtn.setVisibility(View.GONE);
		final Button cameraBtn = (Button) layout.findViewById(R.id.camera);
		final Button galleryBtn = (Button) layout.findViewById(R.id.gallery);
		final Button cancelBtn = (Button) layout.findViewById(R.id.cancelbtn);

		cameraBtn.setText(mContext.getString(R.string.camera));
		galleryBtn.setText(mContext.getString(R.string.gallery));
		cancelBtn.setText(mContext.getString(R.string.cancel));

		final boolean sendVoice;
		if (mContentEdit.getVisibility() == View.VISIBLE) {
			sendVoice = true;
		} else {
			sendVoice = false;
		}

		cameraBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				// btnCameraAction();
			}
		});

		galleryBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				// btnPhotoAction();
			}
		});

		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});

		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setCancelable(true);

		dlg.setContentView(layout);
		dlg.show();
	}

	public final static int ZAN_SUCCESS = 17454;
	public final static int ZAN_FAILED = 17455;
	public final static int ZAN_CANCEL_SUCCESS = 17456;

	private final static int HIDE_PROGRESS_DIALOG = 15453;

	// private void sendText() {
	// String str = mContentEdit.getText().toString();
	// Log.e("SEND MESSAGE", str);
	// if (str != null
	// && (str.trim().replaceAll("\r", "").replaceAll("\t", "").replaceAll("\n",
	// "").replaceAll("\f", "")) != "") {
	// if (str.length() > DamiCommon.MESSAGE_CONTENT_LEN) {
	// showToast(mContext.getString(R.string.message_limit_count));
	// return;
	// }
	// mContentEdit.setText("");
	//
	// MessageInfo msg = new MessageInfo();
	// msg.from = DamiCommon.getUid(mContext);// 来自自己
	// msg.tag = UUID.randomUUID().toString() + "-" + msg.from + "-" +
	// System.currentTimeMillis();
	// msg.title = mTribe.name;
	// msg.to = mTribe.id;// 发送给会议
	// msg.parentid = messageInfo.id;// 给谁发评论，要么是0，要么是其他消息
	// if (mChatType == ChatTribeActivity.CHAT_TYPE_MEETING && mTribe.role != 0)
	// {// 在会议室并且是创建者
	// msg.displayname = mLogin.displayName;// 发送者的名字
	// msg.headImgUrl = mLogin.headsmall;// 发送者的小头像
	// } else {// 临时？
	// msg.displayname = mIdentity.name;
	// msg.headImgUrl = mIdentity.head;
	// msg.heroid = mIdentity.id;
	// }
	//
	// msg.fileType = MessageType.TEXT;
	// msg.content = str;
	// msg.type = mChatType;
	// if (mNewUrl == null || mNewUrl.equals("")) {
	// Pattern p = Pattern
	// .compile("(\\bhttps?://[a-zA-Z0-9\\-.]+(?::(\\d+))?(?:(?:/[a-zA-Z0-9\\-._?,'+\\&%$=~*!():@\\\\]*)+)?)");
	// Matcher m = p.matcher(str);
	// if (m.find()) {
	// msg.url = m.group(1);
	// }
	// } else {
	// msg.url = mNewUrl;
	// }
	//
	// msg.time = System.currentTimeMillis();
	// msg.readState = 1;
	//
	// msg.commenterid = commenterid;
	// msg.commentername = commenterName;
	// initialSendIdAndName();
	// msg.sendState = 1;
	// addMessageInfo(msg);
	// sendMessage(msg, 0);
	// }
	// }

	//
	// @Override
	// protected void addMessageInfo(MessageInfo info) {
	// messageInfos.add(0, info);
	// messageInfo.commentCount++;
	// commentCountText.setText(String.valueOf(messageInfo.commentCount));
	// if (messageInfos.size() > 0) {
	// chatCommentIcon.setVisibility(View.VISIBLE);
	// }
	// Log.d(TAG, "add message messageInfos length is:" + messageInfos.size());
	// mAdapter.notifyDataSetChanged();
	// insertMessage(info);
	// }
	//
	// private void sendMessage(final MessageInfo msg, final int isResend) {
	// new Thread() {
	// @Override
	// public void run() {
	// if (DamiCommon.verifyNetwork(mContext)) {
	// msg.sendState = 2;
	// Message stateMessage = new Message();
	// stateMessage.obj = msg;
	// stateMessage.what = ChatMainActivity.CHANGE_STATE;
	// mHandler.sendMessage(stateMessage);
	// try {
	// MessageResult result = DamiCommon.getDamiInfo().sendMessage(msg);
	// if (result != null && result.mState != null && result.mState.code == 0)
	// {// 发送成功
	// result.mMessageInfo.sendState = 1;
	// if (msg.msgType == MessageType.VOICE) {
	// String voice = FeatureFunction.generator(result.mMessageInfo.voiceUrl);
	// FeatureFunction.reNameFile(new File(msg.voiceUrl), voice);
	// }
	// result.mMessageInfo.readState = 1;
	// Message message = new Message();
	// message.what = ChatMainActivity.SEND_SUCCESS;
	// message.arg1 = isResend;
	// message.obj = result.mMessageInfo;
	// mHandler.sendMessage(message);
	// return;
	// } else if (result != null && result.mState != null && result.mState.code
	// == DamiCommon.EXPIRED_CODE) {// 账户过期
	// DamiCommon.saveLoginResult(mContext, null);
	// DamiCommon.setUid("");
	// DamiCommon.setToken("");
	// Intent intent = new Intent(mContext, LoginActivity.class);
	// startActivityForResult(intent, MainActivity.LOGIN_REQUEST);
	// } else if (result != null && result.mState != null && result.mState.code
	// == DamiCommon.SENSITIVE_WORD_CODE) {// 敏感词过滤
	// Message message = new Message();
	// message.what = ChatMainActivity.SHOW_SENSTIVE_WORD_ERROR;
	// message.obj = result.mState.errorMsg;
	// mHandler.sendMessage(message);
	// } else if (result != null
	// && result.mState != null
	// && (result.mState.code == DamiCommon.MEETING_NO_START_CODE// 会议结束等问题
	// || result.mState.code == DamiCommon.MEETING_IS_OVER_CODE ||
	// result.mState.code == DamiCommon.MEETING_EXPIRED_CODE ||
	// result.mState.code == DamiCommon.SENSITIVE_WORD_CODE)) {
	// Message message = new Message();
	// message.what = ChatMainActivity.SHOW_MEETING_TIME_ERROR;
	// message.obj = result.mState.errorMsg;
	// mHandler.sendMessage(message);
	// } else if (result != null && result.mState != null && result.mState.code
	// == DamiCommon.IDENTITY_INVALID_CODE) {// 身份失效
	// mIdentity = result.mIdentity;
	// SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
	// IdentityTable table = new IdentityTable(db);
	// Identity identity = table.query(mTribe.id);
	// if (identity == null) {
	// table.insert(mTribe.id, result.mIdentity);
	// } else {
	// table.update(mTribe.id, result.mIdentity);
	// }
	// msg.displayName = result.mIdentity.name;
	// msg.heroid = result.mIdentity.id;
	// msg.headImgUrl = result.mIdentity.heading;
	// }
	// } catch (DamiException e) {
	// e.printStackTrace();
	// }
	//
	// } else {
	// mHandler.sendEmptyMessage(MainActivity.MSG_NETWORK_ERROR);
	// }
	//
	// msg.displayName = messageInfo.displayName;
	// msg.sendState = 0;
	// Message message = new Message();
	// message.what = ChatMainActivity.SEND_FAILED;
	// message.arg1 = isResend;
	// message.obj = msg;
	// mHandler.sendMessage(message);
	// }
	// }.start();
	// }
	//
	private List<MessageInfo> zanList = new ArrayList<MessageInfo>();

	private void getZanList() {
		DamiInfo.getMessageZanList(messageInfo.id, new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				final ChatMessageBean data = (ChatMessageBean) o;
				if (data.state != null && data.state.code == 0) {
					zanList.clear();
					if (data.data != null && data.data.size() > 0) {
						zanList.addAll(data.data);
					}
					updateZanView();
				} else {
					otherCondition(data.state, ChatCommentsActivity.this);
				}

			}
		});
	}

	private void updateZanView() {
		zanCountBtn.setEnabled(true);
		messageInfo.agreeCount = zanList.size();
		likeCountText.setText(String.valueOf(zanList.size()));
		zanCountBtn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_msg_detail_zan_normal));
		if (zanList.size() > 0) {
			zanText.setText(getColorfulClickText());
			for (MessageInfo info : zanList) {
				if (info.uid.equals(mLogin.uid)) {
					zanCountBtn
							.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_msg_detail_zan_active));
				}
			}
		} 
		bindZanCommentBorderView();
	}

	//
	// /** 聊天广播 */
	// private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// String action = intent.getAction();
	// if (ChatMainActivity.ACTION_ZAN_MESSAGE.equals(action)) {
	// MessageInfo messageInfo = (MessageInfo)
	// intent.getSerializableExtra("message");
	// if (messageInfo != null) {
	// updateZanCount(messageInfo, true);
	// }
	// } else if (ChatMainActivity.ACTION_UNZAN_MESSAGE.equals(action)) {
	// MessageInfo messageInfo = (MessageInfo)
	// intent.getSerializableExtra("message");
	// if (messageInfo != null) {
	// updateZanCount(messageInfo, true);
	// }
	// } else if (NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE.equals(action)) {
	// final MessageInfo messageInfo = (MessageInfo)
	// intent.getSerializableExtra(NotifyChatMessage.EXTRAS_NOTIFY_CHAT_MESSAGE);
	// mHandler.post(new Runnable() {
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	//
	// modifyMessageState(messageInfo);
	// }
	// });
	//
	// } else if (ChatMainActivity.ACTION_FAVORITE_MESSAGE.equals(action)) {
	// updateFavoriteCount(messageInfo, false);
	// favouriteCountText.setText(String.valueOf(messageInfo.favoriteCount));
	// } else if (ChatMainActivity.ACTION_SHIED_MESSAGE.equals(action)) {
	// String tag = intent.getStringExtra("tag");
	// if (!TextUtils.isEmpty(tag)) {
	// if (messageInfo.tag.equals(tag)) {
	// messageInfo.mIsShide = 1;
	// bindView();
	// } else {
	// for (int i = 0; i < messageInfos.size(); i++) {
	// if (messageInfos.get(i).tag.equals(tag)) {
	// messageInfos.get(i).mIsShide = 1;
	// mAdapter.notifyDataSetChanged();
	// break;
	// }
	// }
	// }
	// }
	// }
	// }
	// };
	//
	// private void registerReceiver() {
	// IntentFilter filter = new IntentFilter();
	// filter.addAction(ChatMainActivity.ACTION_ZAN_MESSAGE);
	// filter.addAction(ChatMainActivity.ACTION_UNZAN_MESSAGE);
	// filter.addAction(NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE);
	// filter.addAction(ChatMainActivity.ACTION_SHIED_MESSAGE);
	// registerReceiver(chatReceiver, filter);
	// }
	//
	// private void unregisterReceiver() {
	// unregisterReceiver(chatReceiver);
	// }
	//
	// private void updateZanCount(MessageInfo msgInfo, boolean isAgree) {
	// if (msgInfo.parentId.equals("0")) {
	// if (msgInfo.tag.equals(messageInfo.tag)) {
	// if (isAgree) {
	// messageInfo.agreeCount = msgInfo.agreeCount;
	// } else {
	// messageInfo.agreeCount++;
	// updateMessage(msgInfo);
	// }
	// }
	// }
	// }
	//
	// private void modifyMessageState(MessageInfo messageInfo) {
	// if (messageInfo == null) {
	// return;
	// }
	// updateMessage(messageInfo);
	// if (messageInfo.parentId.equals("0")) {
	// return;
	// } else {
	// if (mChatType == TRIBE_CHAT_TYPE) {
	// mContext.sendBroadcast(new Intent(TribeTab.UPDATE_COUNT_ACTION));
	// mContext.sendBroadcast(new
	// Intent(MainActivity.ACTION_UPDATE_TRIBE_SESSION_COUNT));
	// } else if (mChatType == MEETING_CHAT_TYPE) {
	// mContext.sendBroadcast(new
	// Intent(MeatingTab.REFRESH_UNREAD_COUNT_ACTION));
	// mContext.sendBroadcast(new
	// Intent(MainActivity.ACTION_UPDATE_MEETING_SESSION_COUNT));
	// }
	// if
	// (messageInfo.parentId.equals(ChatCommentsActivity.this.messageInfo.id)) {
	// ChatCommentsActivity.this.messageInfo.commentCount++;
	// commentCountText.setText(String.valueOf(ChatCommentsActivity.this.messageInfo.commentCount));
	// messageInfos.add(0, messageInfo);
	// mAdapter.notifyDataSetChanged();
	//
	// }
	// }
	// }

	// private int mSceneType = 0;
	// public final static int SCENE_ONLOOK = 101;
	//
	// public void showMenuDialog(final MessageInfo messageInfo) {
	//
	// final Dialog dlg = new Dialog(mContext, R.style.MMThem_DataSheet);
	// LayoutInflater inflater = (LayoutInflater)
	// mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// LinearLayout layout = (LinearLayout)
	// inflater.inflate(R.layout.chat_comment_add_menu_dialog, null);
	// final int cFullFillWidth = 10000;
	// layout.setMinimumWidth(cFullFillWidth);
	//
	// final Button tpyeBtn = (Button) layout.findViewById(R.id.sendType);
	// final Button zanButn = (Button) layout.findViewById(R.id.zanButton);
	// final Button cameraBtn = (Button) layout.findViewById(R.id.camera);
	// final Button galleryBtn = (Button) layout.findViewById(R.id.gallery);
	// final Button cancelBtn = (Button) layout.findViewById(R.id.cancelbtn);
	// LinearLayout firstline = (LinearLayout)
	// layout.findViewById(R.id.firstline);
	// LinearLayout line = (LinearLayout) layout.findViewById(R.id.lastline);
	// LinearLayout voiceLayout = (LinearLayout)
	// layout.findViewById(R.id.voiceLayout);
	// LinearLayout retrweetLayout = (LinearLayout)
	// layout.findViewById(R.id.forwardLayout);
	// LinearLayout commnicationLayout = (LinearLayout)
	// layout.findViewById(R.id.communicationLayout);
	// LinearLayout zanLayout = (LinearLayout)
	// layout.findViewById(R.id.zanLayout);
	// LinearLayout deleteLayout = (LinearLayout)
	// layout.findViewById(R.id.deleteLayout);
	// LinearLayout voiceModeLayout = (LinearLayout)
	// layout.findViewById(R.id.voiceModeLayout);
	//
	// final Button voiceModeBtn = (Button)
	// layout.findViewById(R.id.voice_mode_btn);
	// final Button deleteBtn = (Button) layout.findViewById(R.id.deleteBtn);
	// final Button voiceBtn = (Button) layout.findViewById(R.id.voiceBtn);
	// final Button forwardBtn = (Button) layout.findViewById(R.id.forwardBtn);
	// final Button commnicationBtn = (Button)
	// layout.findViewById(R.id.communicationBtn);
	//
	// tpyeBtn.setText(mContext.getString(R.string.comment));
	// cameraBtn.setText(mContext.getString(R.string.favorite));
	// galleryBtn.setText(mContext.getString(R.string.report));
	// cancelBtn.setText(mContext.getString(R.string.cancel));
	//
	// zanLayout.setVisibility(View.VISIBLE);
	// deleteLayout.setVisibility(View.VISIBLE);
	// if (messageInfo.parentId.equals("0")) {
	// tpyeBtn.setVisibility(View.VISIBLE);
	// firstline.setVisibility(View.VISIBLE);
	// retrweetLayout.setVisibility(View.VISIBLE);
	// voiceLayout.setVisibility(View.VISIBLE);
	// if (messageInfo.fromId.equals(DamiCommon.getUid(mContext))) {//
	// 如果来自自己，则不交往，转发等
	// commnicationLayout.setVisibility(View.GONE);
	// galleryBtn.setVisibility(View.GONE);
	// line.setVisibility(View.GONE);
	// } else {
	// commnicationLayout.setVisibility(View.VISIBLE);
	// galleryBtn.setVisibility(View.VISIBLE);
	// line.setVisibility(View.VISIBLE);
	// }
	//
	// retrweetLayout.setBackgroundResource(R.drawable.bottom_half_transparent_btn);
	// } else {
	// commnicationLayout.setVisibility(View.GONE);
	// tpyeBtn.setVisibility(View.GONE);
	// firstline.setVisibility(View.GONE);
	// retrweetLayout.setVisibility(View.GONE);
	// voiceLayout.setVisibility(View.GONE);
	// if (messageInfo.fromId.equals(DamiCommon.getUid(mContext))) {
	// galleryBtn.setVisibility(View.GONE);
	// line.setVisibility(View.GONE);
	// cameraBtn.setBackgroundResource(R.drawable.round_half_transparent_btn);
	// } else {
	// galleryBtn.setVisibility(View.VISIBLE);
	// line.setVisibility(View.VISIBLE);
	// cameraBtn.setBackgroundResource(R.drawable.top_half_transparent_btn);
	// }
	// }
	// if (mCurrentModel == VOICE_MODEL) {
	// voiceBtn.setText(getString(R.string.view_text_version));
	// } else {
	// voiceBtn.setText(getString(R.string.view_voice_version));
	// }
	//
	// if (messageInfo.mIsAgree == 1) {
	// zanButn.setText(getString(R.string.zan_cancel));
	// } else {
	// zanButn.setText(getString(R.string.zan));
	// }
	//
	// if (mSceneType == SCENE_ONLOOK) {
	// deleteBtn.setVisibility(View.GONE);
	// commnicationBtn.setVisibility(View.GONE);
	// forwardBtn.setVisibility(View.GONE);
	// } else {
	// deleteBtn.setVisibility(View.VISIBLE);
	// }
	//
	// voiceModeLayout.setVisibility(View.VISIBLE);
	// if (((BaseActivity) mContext).isModeInCall) {
	// voiceModeBtn.setText(mContext.getString(R.string.mode_in_speaker));
	// } else {
	// voiceModeBtn.setText(mContext.getString(R.string.mode_in_call));
	// }
	//
	// voiceModeBtn.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// dlg.dismiss();
	// if (((BaseActivity) mContext).isModeInCall) {// 如果是听筒
	// ((BaseActivity) mContext).setPlayMode(false);// 那么就喇叭
	// updateVoicePlayModeState(false);
	// Toast.makeText(mContext,
	// mContext.getString(R.string.switch_to_mode_in_speaker),
	// Toast.LENGTH_SHORT).show();
	// } else {
	// ((BaseActivity) mContext).setPlayMode(true);// 不然就听筒
	// updateVoicePlayModeState(true);
	// Toast.makeText(mContext,
	// mContext.getString(R.string.switch_to_mode_in_call),
	// Toast.LENGTH_SHORT).show();
	// }
	// if (playListener.isPalying()) {
	// DamiApp.getInstance().setPlayMode();
	// }
	// }
	// });
	//
	// deleteBtn.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// dlg.dismiss();
	// sendMessage(MSG_REMOVE_MSG, messageInfo);
	// }
	// });
	//
	// tpyeBtn.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// dlg.dismiss();
	// sendMessage(MSG_TYPE, messageInfo);
	// }
	// });
	//
	// zanButn.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// dlg.dismiss();
	// sendMessage(MSG_ZAN, messageInfo);
	// }
	// });
	//
	// cameraBtn.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// dlg.dismiss();
	// sendMessage(MSG_FAVORITE_MSG, messageInfo);
	// }
	// });
	//
	// galleryBtn.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// dlg.dismiss();
	// showReportDialog(messageInfo);
	// }
	// });
	//
	// voiceBtn.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// dlg.dismiss();
	// sendMessage(MSG_VOICE_TEXT_MSG, messageInfo);
	// }
	// });
	//
	// commnicationBtn.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// dlg.dismiss();
	// Intent intent = new Intent(mContext, ApplyMeetingActivity.class);
	// intent.putExtra("uid", messageInfo.fromId);
	// intent.putExtra("msgid", messageInfo.id);
	// intent.putExtra("refuseType", 2);
	// mContext.startActivity(intent);
	// }
	// });
	//
	// forwardBtn.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// dlg.dismiss();
	// Intent intent = new Intent(mContext, InviteUserActivity.class);
	// intent.putExtra("id", mTribe.id);
	// intent.putExtra("isforward", 1);
	// intent.putExtra("message", messageInfo);
	// mContext.startActivity(intent);
	// }
	// });
	//
	// cancelBtn.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// dlg.dismiss();
	// }
	// });
	//
	// Window w = dlg.getWindow();
	// WindowManager.LayoutParams lp = w.getAttributes();
	// lp.x = 0;
	// final int cMakeBottom = -1000;
	// lp.y = cMakeBottom;
	// lp.gravity = Gravity.BOTTOM;
	// dlg.onWindowAttributesChanged(lp);
	// dlg.setCanceledOnTouchOutside(true);
	// dlg.setCancelable(true);
	//
	// dlg.setContentView(layout);
	// dlg.show();
	// }
	//
	// public void updateVoicePlayModeState(boolean isModeInCall) {
	// if (isModeInCall) {
	// mVoiceModeImage.setVisibility(View.VISIBLE);
	// } else {
	// mVoiceModeImage.setVisibility(View.GONE);
	// }
	// }
	//
	// private void showReportDialog(final MessageInfo messageInfo) {
	// final Dialog dlg = new Dialog(mContext, R.style.MMThem_DataSheet);
	// LayoutInflater inflater = (LayoutInflater)
	// mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// LinearLayout layout = (LinearLayout)
	// inflater.inflate(R.layout.chat_report_dialog, null);
	// final int cFullFillWidth = 10000;
	// layout.setMinimumWidth(cFullFillWidth);
	//
	// ListView listView = (ListView) layout.findViewById(R.id.cause_list);
	// listView.setCacheColorHint(0);
	// final String[] levelArray =
	// mContext.getResources().getStringArray(R.array.report_message_cause);
	// listView.setOnItemClickListener(new OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long
	// arg3) {
	// dlg.dismiss();
	// sendMessage(MSG_REPORT_MSG, messageInfo, arg2);
	// }
	// });
	// ReportCauseAdapter adapter = new ReportCauseAdapter(mContext,
	// levelArray);
	// listView.setAdapter(adapter);
	// final Button cancelBtn = (Button) layout.findViewById(R.id.cancelbtn);
	// cancelBtn.setText(mContext.getString(R.string.cancel));
	//
	// cancelBtn.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// dlg.dismiss();
	// }
	// });
	//
	// // set a large value put it in bottom
	// Window w = dlg.getWindow();
	// WindowManager.LayoutParams lp = w.getAttributes();
	// lp.x = 0;
	// final int cMakeBottom = -1000;
	// lp.y = cMakeBottom;
	// lp.gravity = Gravity.BOTTOM;
	// dlg.onWindowAttributesChanged(lp);
	// dlg.setCanceledOnTouchOutside(true);
	// dlg.setCancelable(true);
	//
	// dlg.setContentView(layout);
	// dlg.show();
	// }

	private void sendMessage(int msgID, MessageInfo msg) {
		sendMessage(msgID, msg, 0);
	}

	private void sendMessage(int msgID, MessageInfo msg, int arg) {
		Message msgMessage = new Message();
		msgMessage.what = msgID;
		msgMessage.arg1 = arg;
		msgMessage.obj = msg;
		// mHandler.sendMessage(msgMessage);
	}

	private class PlayCallback extends MediaUIHeper.PlayCallback {

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			// if (((ChatBaseActivity)mContext).isModeInCall) {
			// ((ChatTribeActivity) mContext).showVoiceModeToastAnimation();
			// }
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onStop(boolean stopAutomatic) {
			// TODO Auto-generated method stub
			mAdapter.notifyDataSetChanged();
		}
	}

	public void zanMessage(final MessageInfo messageInfo) {
		DamiInfo.agreeMessage(mTribe.id, messageInfo.id, new SimpleResponseListener(mContext,
				getString(R.string.request_internet_now)) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					if (data.state.msg.equals("赞成功")) {
						Toast.makeText(mContext, "赞同成功", Toast.LENGTH_SHORT).show();
						messageInfo.isAgree = 1;
						messageInfo.agreeCount++;
					} else if (data.state.msg.equals("取消赞成功")) {
						Toast.makeText(mContext, "您已取消赞同", Toast.LENGTH_SHORT).show();
						messageInfo.isAgree = 0;
						messageInfo.agreeCount--;
					}
					updateZanCount(ChatCommentsActivity.this.messageInfo);
					getZanList();
				} else {
					otherCondition(data.state, ChatCommentsActivity.this);
				}
			}
		});
	}

	public void favoriteMessage(final MessageInfo messageInfo) {
		SimpleResponseListener listener = new SimpleResponseListener(mContext, getString(R.string.request_internet_now)) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, R.string.favorite_success, Toast.LENGTH_SHORT).show();
				updateFavoriteCount(messageInfo, false);
				favoriteCountBtn.setImageResource(R.drawable.icon_msg_detail_favorite_active);
			}
		};
		if (mChatType == ChatTribeActivity.CHAT_TYPE_TRIBE) {
			DamiInfo.favoriteMessage(mTribe.id, messageInfo.id, listener);
		} else if (mChatType == ChatTribeActivity.CHAT_TYPE_MEETING) {
			DamiInfo.favoriteMeetingMessage(mTribe.id, messageInfo.id, listener);
		}
	}

	private void report(final MessageInfo messageInfo, final String content) {
		SimpleResponseListener listener = new SimpleResponseListener(mContext, getString(R.string.request_internet_now)) {
			@Override
			public void onSuccess(Object o) {
				Toast.makeText(mContext, R.string.report_success, Toast.LENGTH_SHORT).show();
			}
		};
		if (mChatType == ChatTribeActivity.CHAT_TYPE_TRIBE) {
			DamiInfo.reportMessage(mTribe.id, messageInfo.id, content, listener);
		} else if (mChatType == ChatTribeActivity.CHAT_TYPE_MEETING) {
			DamiInfo.reportMeetingMessage(mTribe.id, messageInfo.id, content, listener);
		}
	}

	public void showItemLongClickDialog(final MessageInfo messageInfo) {
		final List<String> strList = new ArrayList<String>();
		strList.add(getString(R.string.comment));
		strList.add(getString(R.string.favorite));
		strList.add(getString(R.string.report));
		strList.add(getString(R.string.delete));

		if (messageInfo.isAgree == 1) {
			strList.add(1, getString(R.string.zan_cancel));
		} else {
			strList.add(1, getString(R.string.zan));
		}
		if (messageInfo.fileType == MessageType.VOICE) {
			if (isModeInCall) {
				strList.add(0, getString(R.string.mode_in_speaker));
			} else {
				strList.add(0, getString(R.string.mode_in_call));
			}
		}
		if (!messageInfo.from.equals(DamiCommon.getUid(mContext))) {// 如果来自自己，则不交往，转发等
			strList.add(getString(R.string.retrweet));
			strList.add(getString(R.string.communication));
		}
		String[] array = new String[1];
		Dialog dialog = new AlertDialog.Builder(this)
				.setItems((String[]) strList.toArray(array), new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						parseItemLongClickAction(strList.get(which), messageInfo);
					}
				}).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).create();
		dialog.show();
	}

	private void parseItemLongClickAction(String result, MessageInfo msgInfo) {
		if (result.equals(getString(R.string.comment))) {

		} else if (result.equals(getString(R.string.mode_in_speaker))
				|| result.equals(getString(R.string.mode_in_call))) {
			changePlayMode();
		} else if (result.equals(getString(R.string.zan)) || result.equals(getString(R.string.zan_cancel))) {
			zanMessage(msgInfo);
		} else if (result.equals(getString(R.string.retrweet))) {
			goToRetrweet(msgInfo);
		} else if (result.equals(getString(R.string.report))) {
			showReportDialog(msgInfo);
		} else if (result.equals(getString(R.string.favorite))) {
			favoriteMessage(msgInfo);
		} else if (result.equals(getString(R.string.delete))) {
			removeMessage(msgInfo);
		} else if (result.equals(getString(R.string.communication))) {
			communicatePeople(msgInfo);
		}
	}

	private void goToRetrweet(MessageInfo msgInfo) {
		Intent intent = new Intent(mContext, ShareActivity.class);
		intent.putExtra(ShareActivity.KEY_TYPE, ShareActivity.TYPE_SHARE);
		intent.putExtra(ShareActivity.KEY_MESSAGE, msgInfo);
		startActivity(intent);
	}

	private void showReportDialog(final MessageInfo msgInfo) {
		final String[] levelArray = mContext.getResources().getStringArray(R.array.report_message_cause);
		Dialog dialog = new AlertDialog.Builder(this).setItems(levelArray, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				report(msgInfo, levelArray[which]);
			}
		}).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		}).create();
		dialog.show();
	}

	private void communicatePeople(MessageInfo messageInfo) {
		Intent intent = new Intent(mContext, AddReasonActivity.class);
		intent.putExtra(AddReasonActivity.KEY_MESSAGEINFO, messageInfo);
		intent.putExtra(AddReasonActivity.KEY_APLLY_TYPE, AddReasonActivity.TYPE_WAHT_COMUNICATION);
		mContext.startActivity(intent);
	}

	public void changePlayMode() {
		if (isModeInCall) {// 如果是听筒
			setPlayMode(false);// 那么就喇叭
			// updateVoicePlayModeState(false);
			Toast.makeText(mContext, mContext.getString(R.string.switch_to_mode_in_speaker), Toast.LENGTH_SHORT).show();
		} else {
			setPlayMode(true);// 不然就听筒
			// updateVoicePlayModeState(true);
			Toast.makeText(mContext, mContext.getString(R.string.switch_to_mode_in_call), Toast.LENGTH_SHORT).show();
		}
		if (speexPlayerWrapper.isPlay()) {
			DamiApp.getInstance().setPlayMode();
		}
	}
	
	private void notifyDataSetChanged() {
		bindZanCommentBorderView();
		mAdapter.notifyDataSetChanged();
	}
}
