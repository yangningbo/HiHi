package com.gaopai.guiren.activity.chat;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import com.gaopai.guiren.activity.LocalPicActivity;
import com.gaopai.guiren.activity.LocalPicPathActivity;
import com.gaopai.guiren.activity.RotateImageActivity;
import com.gaopai.guiren.activity.ShowImagesActivity;
import com.gaopai.guiren.activity.TribeActivity;
import com.gaopai.guiren.bean.Identity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageState;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.ChatMessageBean;
import com.gaopai.guiren.bean.net.SendMessageResult;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.media.MediaUIHeper;
import com.gaopai.guiren.media.SpeexPlayerWrapper;
import com.gaopai.guiren.media.SpeexPlayerWrapper.OnDownLoadCallback;
import com.gaopai.guiren.media.SpeexRecorderWrapper;
import com.gaopai.guiren.receiver.NotifyChatMessage;
import com.gaopai.guiren.support.chat.ChatBoxManager;
import com.gaopai.guiren.support.chat.ChatMsgDataHelper;
import com.gaopai.guiren.support.chat.ChatMsgDataHelper.Callback;
import com.gaopai.guiren.utils.ImageLoaderUtil;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.MyTextUtils;
import com.gaopai.guiren.utils.MyTextUtils.SpanUser;
import com.gaopai.guiren.utils.MyUtils;
import com.gaopai.guiren.utils.PreferenceOperateUtils;
import com.gaopai.guiren.utils.SPConst;
import com.gaopai.guiren.utils.ViewUtil;
import com.gaopai.guiren.view.ChatGridLayout;
import com.gaopai.guiren.view.RecordDialog;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;
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

	private ImageView ivVoice, ivPhoto, ivPhotoCover, headImageView;
	private View layoutPic;
	private ImageView mVoiceModeImage;
	private ProgressBar progressbar;
	private TextView tvText, tvVoiceLength, commentCountText, likeCountText, favouriteCountText, nameTextView, zanText;
	private ImageView commentCountBtn, favoriteCountBtn, zanCountBtn;
	private View layoutMsgContent;

	private LinearLayout commentCountLayout, zanCountLayout, favoriteCountLayout;

	private ViewGroup layoutZan;

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

	private String commenterid;
	private String commenterName;

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

	private boolean isOnLooker = false;

	protected SpeexPlayerWrapper speexPlayerWrapper;

	protected MyAdapter mAdapter;
	protected PullToRefreshListView mListView;
	private View viewCoverTop;
	private ChatMsgDataHelper msgHelper;
	private List<MessageInfo> zanList = new ArrayList<MessageInfo>();

	private PreferenceOperateUtils spoAnony;

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
		isOnLooker = getIntent().getBooleanExtra(INTENT_SENCE_ONLOOK_KEY, false);
		msgHelper = new ChatMsgDataHelper(mContext, callback, mTribe, mChatType);
		initComponent();
		mListView.getRefreshableView().addHeaderView(creatHeaderView());
		bindView();

		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
		getmessageInfos();
		mLogin = DamiCommon.getLoginResult(mContext);
		getZanList();
		initVoicePlayMode();
		spoAnony = new PreferenceOperateUtils(mContext, SPConst.SP_ANONY);
		// updateVoicePlayModeState(isModeInCall);
		registerReceiver(getIntentFilter());
		isChangeVoice = isAnony();
		setChangeVoiceView(isChangeVoice);
	}

	private boolean isAnony() {
		return spoAnony.getInt(SPConst.getSingleSpId(mContext, mTribe.id), 0) == 1;
	}

	protected void setChangeVoiceView(boolean isChangeVoice) {
		TextView view = (TextView) chatAddChangeVoiceLayout.getChildAt(1);
		ImageView ivVoice = (ImageView) chatAddChangeVoiceLayout.getChildAt(0);
		if (isChangeVoice) {
			ivVoice.setImageResource(R.drawable.icon_chat_grid_noraml_voice);
			view.setText(getString(R.string.not_change_voice));
		} else {
			view.setText(getString(R.string.change_voice));
			ivVoice.setImageResource(R.drawable.icon_chat_grid_unnoraml_voice);
		}
	}

	private IntentFilter getIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE);
		intentFilter.addAction(ChatBaseActivity.ACTION_CHANGE_VOICE);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		return intentFilter;
	}

	@Override
	protected void onReceive(Intent intent) {
		super.onReceive(intent);
		if (intent.getAction().equals(NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE)) {
			final MessageInfo msg = (MessageInfo) intent
					.getSerializableExtra(NotifyChatMessage.EXTRAS_NOTIFY_CHAT_MESSAGE);
			if (msg != null && msg.parentid.equals(messageInfo.id)) {
				addNotifyMessage(msg);
			}
		} else if (intent.getAction().equals(ChatBaseActivity.ACTION_CHANGE_VOICE)) {
			isChangeVoice = isAnony();
			setChangeVoiceView(isChangeVoice);
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			stopPlayVoice();
		}
	}

	private void stopPlayVoice() {
		if (speexPlayerWrapper != null) {
			speexPlayerWrapper.stop();
		}
	}

	protected void initComponent() {
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.message_detail);

		if (isOnLooker) {
			ViewUtil.findViewById(this, R.id.chat_box).setVisibility(View.GONE);
		} else {
			View view = mTitleBar.addRightImageView(R.drawable.icon_chat_title_more);
			view.setId(R.id.ab_chat_more);
			view.setOnClickListener(this);
		}
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
			}

			@Override
			public void afterTextChanged(Editable s) {
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

		mSendTextBtn.setVisibility(View.GONE);
		mAddBtn.setVisibility(View.VISIBLE);

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
				int pos = position - mListView.getRefreshableView().getHeaderViewsCount();
				if (pos < 0) {
					return;
				}
				mContentEdit.setHint(getString(R.string.comment_reply_info_colon) + messageInfos.get(pos).displayname);
				mVoiceSendBtn.setText(getString(R.string.comment_reply_info_colon) + messageInfos.get(pos).displayname);
				commenterid = messageInfos.get(pos).from;
				commenterName = messageInfos.get(pos).displayname;
			}
		});
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
		}

		@Override
		public void onStop(float recordingTime, String path) {
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

	private ChatMsgDataHelper.Callback callback = new Callback() {

		@Override
		public void zanMessage(MessageInfo msg) {
			updateZanCount(ChatCommentsActivity.this.messageInfo);
			zanList.add(buildZanMessage());
			bindZanView();
			sendNotify();
		}

		@Override
		public void unZanMessage(MessageInfo msg) {
			updateZanCount(ChatCommentsActivity.this.messageInfo);
			removeZanMessage();
			bindZanView();
			sendNotify();
		}

		@Override
		public void unFavoriteMessage(MessageInfo msg) {
			favoriteCountBtn.setImageResource(R.drawable.icon_msg_detail_favorite_normal);
			bindFavoriteView();
			sendNotify();
		}

		@Override
		public void favoriteMessage(MessageInfo msg) {
			favoriteCountBtn.setImageResource(R.drawable.icon_msg_detail_favorite_active);
			bindFavoriteView();
			sendNotify();
		}

		@Override
		public void commentMessage(MessageInfo msg) {
		}
	};

	private MessageInfo buildZanMessage() {
		MessageInfo messageInfo = new MessageInfo();
		messageInfo.displayname = getName();
		messageInfo.uid = mLogin.uid;
		return messageInfo;
	}

	private void removeZanMessage() {
		for (int i = 0; i < zanList.size(); i++) {
			if (zanList.get(i).uid.equals(mLogin.uid)) {
				zanList.remove(i);
				return;
			}

		}
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
		zanText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
		tvText = (TextView) view.findViewById(R.id.iv_chat_text);
		tvVoiceLength = (TextView) view.findViewById(R.id.tv_chat_voice_time_length);
		ivVoice = (ImageView) view.findViewById(R.id.iv_chat_voice);
		ivPhoto = (ImageView) view.findViewById(R.id.iv_chat_photo);
		ivPhotoCover = (ImageView) view.findViewById(R.id.iv_chat_photo_cover);
		layoutPic = view.findViewById(R.id.layout_msg_pic_holder);

		headImageView = (ImageView) view.findViewById(R.id.iv_chat_talk_img_head);
		nameTextView = (TextView) view.findViewById(R.id.tv_user_name);
		layoutMsgContent = view.findViewById(R.id.layout_msg_text_voice_holder);

		commentCountText = (TextView) view.findViewById(R.id.chat_comment_count);
		likeCountText = (TextView) view.findViewById(R.id.chat_zan_count);
		favouriteCountText = (TextView) view.findViewById(R.id.chat_favourite_count);
		favouriteCountText.setText(String.valueOf(messageInfo.favoriteCount));

		zanCountLayout = (LinearLayout) view.findViewById(R.id.zan_count_layout);
		zanCountBtn = (ImageView) view.findViewById(R.id.zan_count_btn);

		commentCountLayout = (LinearLayout) view.findViewById(R.id.comment_count_layout);
		commentCountBtn = (ImageView) view.findViewById(R.id.comment_count_btn);

		favoriteCountLayout = (LinearLayout) view.findViewById(R.id.favourite_count_layout);
		favoriteCountBtn = (ImageView) view.findViewById(R.id.favorite_count_btn);

		if (!isOnLooker) {
			zanCountLayout.setOnClickListener(this);
			zanCountBtn.setOnClickListener(this);
			commentCountLayout.setOnClickListener(this);
			commentCountBtn.setOnClickListener(this);
			favoriteCountLayout.setOnClickListener(this);
			favoriteCountBtn.setOnClickListener(this);
		}

		layoutZan = (ViewGroup) view.findViewById(R.id.ll_zan);
		viewCoverTop = view.findViewById(R.id.view_cover_top);

		bindCommentCount();
		bindZanCommentBorderView();
		return view;
	}

	private void bindCommentCount() {
		commentCountText.setText(String.valueOf(messageInfo.commentCount));
	}

	private void initialSendIdAndName() {
		commenterid = "";
		commenterName = "";
		mVoiceSendBtn.setText(mContext.getString(R.string.pressed_to_record));
		mContentEdit.setHint(mContext.getString(R.string.input_message_hint));
	}

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
		layoutPic.setVisibility(View.GONE);
		layoutMsgContent.setVisibility(View.GONE);
		tvText.setVisibility(View.GONE);
		tvVoiceLength.setVisibility(View.GONE);
		ivVoice.setVisibility(View.GONE);
		switch (which) {
		case MessageType.TEXT:
			layoutMsgContent.setVisibility(View.VISIBLE);
			tvText.setVisibility(View.VISIBLE);
			break;
		case MessageType.PICTURE:
			layoutPic.setVisibility(View.VISIBLE);
			break;
		case MessageType.VOICE:
			layoutMsgContent.setVisibility(View.VISIBLE);
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
		tvText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
		notHideViews(messageInfo.fileType);
		switch (messageInfo.fileType) {
		case MessageType.TEXT:
			tvText.setText(MyTextUtils.addHttpLinks(messageInfo.content));
			tvText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			break;
		case MessageType.VOICE:
			tvVoiceLength.setText(messageInfo.voiceTime + "''");
			ivVoice.setLayoutParams(getVoiceViewLengthParams((ViewGroup.LayoutParams) ivVoice.getLayoutParams(),
					messageInfo));
			layoutMsgContent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					bindView();
					speexPlayerWrapper.start(messageInfo);
				}
			});

			AnimationDrawable drawable = (AnimationDrawable) ivVoice.getDrawable();
			if (speexPlayerWrapper.isPlay() && speexPlayerWrapper.getMessageTag().equals(messageInfo.tag)) {
				drawable.start();
			} else {
				drawable.stop();
				drawable.selectDrawable(0);
			}
			break;
		case MessageType.PICTURE:
			final String path = messageInfo.imgUrlS.trim();
			int width = (int) (MyUtils.dip2px(mContext, messageInfo.imgWidth) * 0.7);
			int height = (int) (MyUtils.dip2px(mContext, messageInfo.imgHeight) * 0.7);
			ivPhoto.getLayoutParams().height = height;
			ivPhoto.getLayoutParams().width = width;
			ivPhotoCover.getLayoutParams().height = height;
			ivPhotoCover.getLayoutParams().width = width;
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
		bindFavoriteView();
	}

	public ViewGroup.LayoutParams getVoiceViewLengthParams(ViewGroup.LayoutParams lp, MessageInfo commentInfo) {
		final int MAX_SECOND = 20;
		final int MIN_SECOND = 2;
		int length = commentInfo.voiceTime;
		float max = mContext.getResources().getDimension(R.dimen.voice_max_length_comment);
		float min = mContext.getResources().getDimension(R.dimen.voice_min_length_comment);
		int width = (int) min;
		if (length >= MIN_SECOND && length <= MAX_SECOND) {
			width += (length - MIN_SECOND) * (int) ((max - min) / (MAX_SECOND - MIN_SECOND));
		} else if (length > MAX_SECOND) {
			width = (int) max;
		}
		if (lp == null) {
			lp = new ViewGroup.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		} else {
			lp.width = width;
		}
		return lp;
	}

	private void bindZanCommentBorderView() {
		if (zanList.size() == 0) {
			layoutZan.setVisibility(View.GONE);
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
			} else {
				layoutZan.getChildAt(1).setVisibility(View.GONE);
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

	private final static int MAX_SECOND = 10;
	private final static int MIN_SECOND = 2;

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return messageInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return messageInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
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
				viewHolder.picImageView = (ImageView) convertView.findViewById(R.id.iv_chat_photo);
				viewHolder.picImageCover = (ImageView) convertView.findViewById(R.id.iv_chat_photo_cover);
				viewHolder.rootLayout = (RelativeLayout) convertView.findViewById(R.id.chat_talk_msg_info);
				viewHolder.voiceLayout = (RelativeLayout) convertView.findViewById(R.id.chat_voice_layout);
				viewHolder.voiceTimeText = (TextView) convertView.findViewById(R.id.chat_talk_voice_num);
				viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.chat_talk_msg_progressBar);
				viewHolder.resendImageView = (ImageView) convertView.findViewById(R.id.chat_talk_msg_sendsate);
				viewHolder.layoutPicHolder = convertView.findViewById(R.id.layout_msg_pic_holder);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.messageNameText.setCompoundDrawablePadding(MyUtils.dip2px(mContext, 5));
			if (position == 0) {
				viewHolder.messageNameText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_dynamic_comment, 0,
						0, 0);
			} else {
				viewHolder.messageNameText.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.icon_dynamic_comment_transparent, 0, 0, 0);
			}

			final MessageInfo commentInfo = messageInfos.get(position);
			viewHolder.messageNameText.setTag(position);
			viewHolder.messageNameText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			CharSequence replyFromToText;

			if (!TextUtils.isEmpty(commentInfo.commenterid) && (Integer.valueOf(commentInfo.commenterid)) > 0) {
				replyFromToText = MyTextUtils.getSpannableString(
						MyTextUtils.addSingleUserSpan(commentInfo.displayname, commentInfo.from), "回复",
						MyTextUtils.addSingleUserSpan(commentInfo.commentername, commentInfo.commenterid), ":");
			} else {
				replyFromToText = MyTextUtils.getSpannableString(
						MyTextUtils.addSingleUserSpan(commentInfo.displayname, commentInfo.from), ":");
			}

			if (commentInfo.sendState == 0) {
				viewHolder.resendImageView.setVisibility(View.GONE);
				viewHolder.resendImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

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
					viewHolder.messageNameText.setText(MyTextUtils.getSpannableString(replyFromToText,
							MyTextUtils.addHttpLinks(commentInfo.content)));
				} else {
					viewHolder.messageNameText.setText(mContext.getString(R.string.shide_msg_prompt));
				}
				break;
			case MessageType.VOICE:
				viewHolder.messageNameText.setMaxWidth(FeatureFunction.dip2px(mContext, 150));
				if (mCurrentModel == TEXT_MODEL) {
					replyFromToText = replyFromToText + commentInfo.content;
				}
				viewHolder.messageNameText.setText(replyFromToText);

				if (commentInfo.sendState == 2) {// now sending
					notHideViews(viewHolder, MessageType.MAP);
				} else {
					if (mCurrentModel == VOICE_MODEL) {
						notHideViews(viewHolder, MessageType.VOICE);
						viewHolder.voiceImageView.setLayoutParams(getVoiceViewLengthParams(
								viewHolder.voiceImageView.getLayoutParams(), commentInfo));
						viewHolder.voiceTimeText.setText(commentInfo.voiceTime + "''");
						viewHolder.voiceLayout.setTag(commentInfo);
						viewHolder.voiceLayout.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								speexPlayerWrapper.start(commentInfo);
							}
						});
						AnimationDrawable drawable = (AnimationDrawable) viewHolder.voiceImageView.getDrawable();
						if (speexPlayerWrapper.isPlay() && commentInfo.tag.equals(speexPlayerWrapper.getMessageTag())) {
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
				viewHolder.messageNameText.setText(replyFromToText);
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
			viewHolder.layoutPicHolder.setVisibility(View.GONE);
			viewHolder.voiceLayout.setVisibility(View.GONE);
			viewHolder.progressBar.setVisibility(View.GONE);
			viewHolder.messageNameText.setVisibility(View.VISIBLE);
			switch (which) {
			case MessageType.TEXT:
				viewHolder.messageNameText.setVisibility(View.VISIBLE);
				break;
			case MessageType.PICTURE:
				viewHolder.layoutPicHolder.setVisibility(View.VISIBLE);
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

	}

	static class ViewHolder {
		TextView messageNameText;
		ImageView picImageView, voiceImageView, resendImageView, picImageCover;
		TextView voiceTimeText;
		RelativeLayout rootLayout;
		RelativeLayout voiceLayout;
		View layoutPicHolder;
		ProgressBar progressBar;
	}

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

	private void updateFavoriteCount(MessageInfo favoriteMessage, boolean isFavorite) {
		if (isFavorite) {
			messageInfo.favoriteCount = favoriteMessage.favoriteCount;
		} else {
			messageInfo.favoriteCount++;
			messageInfo.isfavorite = 1;
		}
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
			animator.start();
			msgHelper.zanMessage(messageInfo, isAnony() ? 1 : 0);
			break;

		case R.id.favorite_count_btn:
		case R.id.favourite_count_layout:
			animator = ObjectAnimator.ofFloat(favoriteCountBtn, "rotationY", 0, 360, 0).setDuration(1500);
			animator.start();
			if (messageInfo.isfavorite == 0) {
				msgHelper.favoriteMessage(messageInfo);
			} else {
				msgHelper.unFavoriteMessage(messageInfo);
			}
			break;
		case R.id.chat_add_camera:
			btnCameraAction();
			boxManager.hideAddGrid();
			break;
		case R.id.chat_add_gallary:
			btnPhotoAction();
			boxManager.hideAddGrid();
			break;
		case R.id.chat_add_change_voice:
			if (isChangeVoice) {
				showToast(R.string.change_normal_voice_mode);
			} else {
				showToast(R.string.change_weired_voice_mode);
			}
			isChangeVoice = !isChangeVoice;
			setChangeVoiceView(isChangeVoice);
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
//		intent.putExtra(LocalPicPathActivity.KEY_PIC_REQUIRE_TYPE, LocalPicPathActivity.PIC_REQUIRE_MUTI);
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
			msg.displayname = mLogin.realname;
			msg.headImgUrl = mLogin.headsmall;
		} else {
			if (spoAnony.getInt(SPConst.getSingleSpId(mContext, mTribe.id), 0) == 0) {
				Logger.d(this, "id=" + spoAnony.getInt(SPConst.getSingleSpId(mContext, mTribe.id), 0));
				msg.displayname = mLogin.realname;
				msg.headImgUrl = mLogin.headsmall;
			} else {
				msg.displayname = mIdentity.name;
				msg.headImgUrl = mIdentity.head;
				msg.heroid = mIdentity.id;
			}
		}
		msg.type = mChatType;

		msg.commenterid = commenterid;
		msg.commentername = commenterName;

		return msg;
	}

	private String getName() {
		if (mChatType == ChatTribeActivity.CHAT_TYPE_MEETING && mTribe.role != 0) {
			return User.getUserName(mLogin);
		} else {
			if (spoAnony.getInt(SPConst.getSingleSpId(mContext, mTribe.id), 0) == 0) {
				return User.getUserName(mLogin);
			} else {
				return mIdentity.name;
			}
		}
	}

	// do not save comment to database
	protected void addSaveSendMessage(MessageInfo msg) {
		msg.sendState = MessageState.STATE_SENDING;
		addSaveMessageInfo(msg);
		bindCommentCount();
		sendMessage(msg);
		sendNotify();
	}

	// comment count has been updated in db before notify, so ignore it
	protected void addNotifyMessage(MessageInfo msg) {
		addMessageInfo(msg);
		bindCommentCount();
	}

	protected void addMessageInfo(MessageInfo info) {
		messageInfos.add(0, info);
		messageInfo.commentCount++;
		notifyDataSetChanged();
	}

	protected void addSaveMessageInfo(MessageInfo info) {
		addMessageInfo(info);
		msgHelper.updateCommentCountToDb(messageInfo);
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
						// updateNewMessage(messageInfo);
						modifyMessageState(messageInfo);

						return;
					} else if (data.state.code == 1) {
						sendFailed(msg);
					} else {
						this.otherCondition(data.state, ChatCommentsActivity.this);
					}
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

	public final static int ZAN_SUCCESS = 17454;
	public final static int ZAN_FAILED = 17455;
	public final static int ZAN_CANCEL_SUCCESS = 17456;

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
					bindZanView();
				} else {
					otherCondition(data.state, ChatCommentsActivity.this);
				}

			}
		});
	}

	private void bindZanView() {
		zanCountBtn.setEnabled(true);
		messageInfo.agreeCount = zanList.size();
		likeCountText.setText(String.valueOf(zanList.size()));
		zanCountBtn.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_msg_detail_zan_normal));
		if (zanList.size() > 0) {
			zanText.setText(MyTextUtils.addUserSpans(getZanUserList(zanList)));
			for (MessageInfo info : zanList) {
				if (info.uid.equals(mLogin.uid)) {
					zanCountBtn
							.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_msg_detail_zan_active));
				}
			}
		}
		bindZanCommentBorderView();
	}

	private void bindFavoriteView() {
		if (messageInfo.isfavorite == 0) {
			favoriteCountBtn.setImageResource(R.drawable.icon_msg_detail_favorite_normal);
		} else {
			favoriteCountBtn.setImageResource(R.drawable.icon_msg_detail_favorite_active);
		}
		favouriteCountText.setText(String.valueOf(messageInfo.favoriteCount));
	}

	private List<SpanUser> getZanUserList(List<MessageInfo> messageInfos) {
		List<SpanUser> spanUsers = new ArrayList<SpanUser>();
		for (MessageInfo messageInfo : messageInfos) {
			SpanUser spanUser = new SpanUser();
			spanUser.realname = messageInfo.displayname;
			spanUser.uid = messageInfo.uid;
			spanUsers.add(spanUser);
		}
		return spanUsers;
	}

	private class PlayCallback extends MediaUIHeper.PlayCallback {

		@Override
		public void onStart() {
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onStop(boolean stopAutomatic) {
			mAdapter.notifyDataSetChanged();
		}
	}

	private void sendNotify() {
		Intent favoriteIntent = new Intent(ChatMainActivity.ACTION_COMMENT_OR_ZAN_OR_FAVOURITE);
		favoriteIntent.putExtra("message", ChatCommentsActivity.this.messageInfo);
		mContext.sendBroadcast(favoriteIntent);
	}

	public void showItemLongClickDialog(final MessageInfo messageInfo) {
		final List<String> strList = new ArrayList<String>();
		strList.add(getString(R.string.comment));
		if (messageInfo.isfavorite == 1) {
			strList.add(getString(R.string.cancel_favorite));
		} else {
			strList.add(getString(R.string.favorite));
		}
		strList.add(getString(R.string.report));
		strList.add(getString(R.string.delete));

		if (messageInfo.isAgree == 1) {
			strList.add(1, getString(R.string.cancel_zan));
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
		} else if (result.equals(getString(R.string.zan)) || result.equals(getString(R.string.cancel_zan))) {
			msgHelper.zanMessage(msgInfo, isAnony() ? 1 : 0);
		} else if (result.equals(getString(R.string.retrweet))) {
			msgHelper.goToRetrweet(msgInfo);
		} else if (result.equals(getString(R.string.report))) {
			msgHelper.showReportDialog(msgInfo);
		} else if (result.equals(getString(R.string.favorite))) {
			msgHelper.favoriteMessage(msgInfo);
		} else if (result.equals(getString(R.string.cancel_favorite))) {
			msgHelper.unFavoriteMessage(msgInfo);
		} else if (result.equals(getString(R.string.delete))) {
			removeMessage(msgInfo);
		} else if (result.equals(getString(R.string.communication))) {
			msgHelper.communicatePeople(msgInfo);
		}
	}

	public void changePlayMode() {
		if (isModeInCall) {// 如果是听筒
			setPlayMode(false);// 那么就喇叭
			Toast.makeText(mContext, mContext.getString(R.string.switch_to_mode_in_speaker), Toast.LENGTH_SHORT).show();
		} else {
			setPlayMode(true);// 不然就听筒
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

	private boolean isLight = true;

	@Override
	protected void onStop() {
		super.onStop();
		isLight = false;
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		if (pm.isScreenOn()) {
			if (speexPlayerWrapper != null) {
				speexPlayerWrapper.stop();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		isLight = true;
	}
}
