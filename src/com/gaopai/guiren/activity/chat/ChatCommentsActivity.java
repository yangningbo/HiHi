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
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.PowerManager;
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
import com.gaopai.guiren.activity.ShowImagesActivity;
import com.gaopai.guiren.activity.TribeActivity;
import com.gaopai.guiren.adapter.BaseChatAdapter;
import com.gaopai.guiren.bean.Identity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageState;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.MsgZanListResult;
import com.gaopai.guiren.bean.MsgZanListResult.ZanBean;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.ChatMessageBean;
import com.gaopai.guiren.bean.net.IdentitityResult;
import com.gaopai.guiren.bean.net.SendMessageResult;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.IdentityTable;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.media.MediaUIHeper;
import com.gaopai.guiren.media.SpeexPlayerWrapper;
import com.gaopai.guiren.media.SpeexPlayerWrapper.OnDownLoadCallback;
import com.gaopai.guiren.media.SpeexRecorderWrapper;
import com.gaopai.guiren.receiver.NotifyChatMessage;
import com.gaopai.guiren.receiver.PushChatMessage;
import com.gaopai.guiren.support.CameralHelper;
import com.gaopai.guiren.support.chat.ChatBoxManager;
import com.gaopai.guiren.support.chat.ChatMsgDataHelper;
import com.gaopai.guiren.support.chat.ChatMsgDataHelper.Callback;
import com.gaopai.guiren.support.chat.ChatMsgHelper;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 消息详情界面，包括查看评论以及回复评论等功能
 */
public class ChatCommentsActivity extends BaseActivity implements OnClickListener {
	public static final String INTENT_MESSAGE_KEY = "message_key";
	public static final String INTENT_TRIBE_KEY = "tribe_key";
	public static final String INTENT_CHATTYPE_KEY = "chattype_key";
	public static final String INTENT_USER_KEY = "user_key";
	public static final String INTENT_IDENTITY_KEY = "identity_key";
	public static final String INTENT_SENCE_ONLOOK_KEY = "onlook_key";
	private MessageInfo messageInfo;
	private Tribe mTribe;
	protected User mLogin;
	protected Identity mIdentity;
	protected int mChatType = 0;

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
	private int reisanonymity = 0;

	public final static int VOICE_MODEL = 0;
	public final static int TEXT_MODEL = 1;
	private int mCurrentModel = VOICE_MODEL;

	private boolean isOnLooker = false;
	private DisplayImageOptions options;

	protected SpeexPlayerWrapper speexPlayerWrapper;

	protected MyAdapter mAdapter;
	protected PullToRefreshListView mListView;
	private View viewCoverTop;
	private ChatMsgDataHelper msgHelper;
	private List<ZanBean> zanList = new ArrayList<ZanBean>();

	private PreferenceOperateUtils spoAnony;
	private CameralHelper cameralHelper;

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
		cameralHelper = new CameralHelper(this);
		cameralHelper.setCallback(picCallback);
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
		isChangeVoice = isAnony();
		setChangeVoiceView(isChangeVoice);
		if (mIdentity == null && !isOnLooker) {
			hasIdentity = false;
			getIdentity();
		}
	}

	private boolean hasIdentity = false;

	protected void getIdentity() {
		if ((mChatType == ChatTribeActivity.CHAT_TYPE_MEETING && (mTribe.role != 1))
				|| mChatType == ChatTribeActivity.CHAT_TYPE_TRIBE) {
			SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
			IdentityTable table = new IdentityTable(db);
			Identity identity = table.query(mTribe.id);
			if (identity == null || System.currentTimeMillis() - identity.updateTime > 24 * 60 * 60 * 1000) {
				getIndetityByNet();
			} else {
				hasIdentity = true;
				mIdentity = identity;
			}
		}
	}

	private void getIndetityByNet() {
		DamiInfo.getIndetity(mTribe.id, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				IdentitityResult data = (IdentitityResult) o;
				if (data.state != null && data.state.code == 0) {
					mIdentity = data.data;
					insertIdentity();
					hasIdentity = true;
				} else {
					hasIdentity = false;
					showGetIdentityDialog();
				}
			}
		});
	}

	private void showGetIdentityDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.identity_name);
		builder.setMessage(getString(R.string.refetch_nickname));
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				getIndetityByNet();
			}
		});
		builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				ChatCommentsActivity.this.finish();
			}
		});
		Dialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public void insertIdentity() {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
		IdentityTable table = new IdentityTable(db);
		Identity identity = table.query(mTribe.id);
		if (identity == null) {
			table.insert(mTribe.id, mIdentity);
		} else {
			table.update(mTribe.id, mIdentity);
		}
	}

	private boolean isAnony() {
		if (mChatType == ChatTribeActivity.CHAT_TYPE_MEETING && mTribe.role > 0) {
			return spoAnony.getInt(SPConst.getSingleSpId(mContext, mTribe.id), 0) == 1;
		} else {
			return spoAnony.getInt(SPConst.getSingleSpId(mContext, mTribe.id), 1) == 1;
		}
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

	@Override
	protected void registerReceiver(IntentFilter intentFilter) {
		intentFilter.addAction(NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE);
		intentFilter.addAction(NotifyChatMessage.ACTION_CHANGE_VOICE_CONTENT);
		intentFilter.addAction(ChatBaseActivity.ACTION_CHANGE_VOICE);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		super.registerReceiver(intentFilter);
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
		} else if (PushChatMessage.ACTION_SEND_STATE.equals(intent.getAction())) {
			Log.d(TAG, "receiver:" + PushChatMessage.ACTION_SEND_STATE);
			MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra(PushChatMessage.EXTRAS_MESSAGE);
			modifyMessageState(messageInfo);
		} else if (NotifyChatMessage.ACTION_CHANGE_VOICE_CONTENT.equals(intent.getAction())) {
			final MessageInfo temp = (MessageInfo) intent
					.getSerializableExtra(NotifyChatMessage.EXTRAS_NOTIFY_CHAT_MESSAGE);
			if (temp.parentid.equals("0")) {
				if (temp.id.equals(messageInfo.id)) {
					messageInfo.content = temp.content;
					bindView();
				}
			} else {
				for (MessageInfo comment : messageInfos) {
					if (comment.id.equals(temp.id)) {
						comment.content = temp.content;
						mAdapter.notifyDataSetChanged();
						break;
					}
				}
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

	View viewChatText;

	protected void initComponent() {
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mTitleBar.setTitleText(R.string.message_detail);
		int imageId = R.drawable.icon_chat_title_voice_mode;
		viewChatText = mTitleBar.addRightImageView(imageId);
		viewChatText.setId(R.id.ab_chat_text);
		viewChatText.setOnClickListener(this);
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
				reisanonymity = messageInfos.get(pos).isanonymity;
			}
		});
		options = new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnLoading(R.drawable.default_pic)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

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
			if (time > SpeexRecorderWrapper.MAX_TIME - 10) {
				recordDialog.setCountDownTime((int) (SpeexRecorderWrapper.MAX_TIME - time));
			}
			if (time > SpeexRecorderWrapper.MAX_TIME) {
				speexRecorder.stop();
				recordDialog.cancelDialog();
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

	private ZanBean buildZanMessage() {
		ZanBean zanBean = new ZanBean();
		zanBean.displayname = getName();
		zanBean.uid = mLogin.uid;
		return zanBean;
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

		progressbar = ViewUtil.findViewById(view, R.id.pb_chat_progress);

		headImageView = (ImageView) view.findViewById(R.id.iv_chat_talk_img_head);
		nameTextView = (TextView) view.findViewById(R.id.tv_user_name);
		layoutMsgContent = view.findViewById(R.id.layout_msg_text_voice_holder);

		commentCountText = (TextView) view.findViewById(R.id.chat_comment_count);
		likeCountText = (TextView) view.findViewById(R.id.chat_zan_count);
		likeCountText.setText(String.valueOf(messageInfo.agreeCount));

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

		bindCommentCountView();
		bindZanCommentBorderView();
		return view;
	}

	private void bindCommentCountView() {
		commentCountText.setText(String.valueOf(messageInfo.commentCount));
	}

	private void initialSendIdAndName() {
		commenterid = "";
		commenterName = "";
		reisanonymity = 0;
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
		progressbar.setVisibility(View.GONE);
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
		ImageLoaderUtil.displayImage(messageInfo.headImgUrl, headImageView, R.drawable.default_header);
		nameTextView.setText(messageInfo.displayname);
		tvText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
		notHideViews(messageInfo.fileType);
		switch (messageInfo.fileType) {
		case MessageType.TEXT:
			tvText.setText(MyTextUtils.addHttpLinks(messageInfo.content));
			tvText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			break;
		case MessageType.VOICE:
			if (mCurrentModel == MODE_TEXT) {
				notHideViews(MessageType.TEXT);
				tvText.setText(messageInfo.content);
				break;
			}
			tvVoiceLength.setText(messageInfo.voiceTime + "''");
			ivVoice.setLayoutParams(getVoiceViewLengthParams((ViewGroup.LayoutParams) ivVoice.getLayoutParams(),
					messageInfo));
			layoutMsgContent.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
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
			if (path.startsWith("http://")) {
				ImageLoaderUtil.displayImageByProgress(path, ivPhoto, null, progressbar);
			} else {
				ImageLoaderUtil.displayImageByProgress("file://" + path, ivPhoto, null, progressbar);
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
				viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.pb_chat_progress);
				viewHolder.resendImageView = (ImageView) convertView.findViewById(R.id.iv_chat_resend_icon);
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

			notHideViews(viewHolder, commentInfo.fileType);

			final boolean isMyself = commentInfo.from.equals(mLogin.uid) ? true : false;
			View resendView = viewHolder.resendImageView;
			if (isMyself) {
				if (MessageState.STATE_SEND_FAILED == commentInfo.sendState) {
					resendView.setVisibility(View.VISIBLE);
				} else {
					resendView.setVisibility(View.GONE);
				}
				resendView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						sendMessage(commentInfo);
					}
				});
			} else {
				resendView.setVisibility(View.GONE);
			}

			if (MessageState.STATE_SENDING == commentInfo.sendState) {
				viewHolder.progressBar.setVisibility(View.VISIBLE);
			} else {
				viewHolder.progressBar.setVisibility(View.GONE);
			}
			viewHolder.messageNameText.setTag(position);
			viewHolder.messageNameText.setOnTouchListener(MyTextUtils.mTextOnTouchListener);
			CharSequence replyFromToText;

			String fromId = commentInfo.isanonymity == 0 ? commentInfo.from : "-1";
			String commenterId = commentInfo.reisanonymity == 0 ? commentInfo.commenterid : "-1";
			if (!TextUtils.isEmpty(commentInfo.commenterid) && !TextUtils.isEmpty(commentInfo.commentername)) {
				replyFromToText = MyTextUtils.getSpannableString(
						MyTextUtils.addSingleUserSpan(commentInfo.displayname, fromId), "回复",
						MyTextUtils.addSingleUserSpan(commentInfo.commentername, commenterId), ":");
			} else {
				replyFromToText = MyTextUtils.getSpannableString(
						MyTextUtils.addSingleUserSpan(commentInfo.displayname, fromId), ":");
			}
			switch (commentInfo.fileType) {
			case MessageType.TEXT:
				viewHolder.messageNameText.setMaxWidth(FeatureFunction.dip2px(mContext, 2000));

				if (commentInfo.mIsShide == 0) {// not hide
					viewHolder.messageNameText.setText(MyTextUtils.getSpannableString(replyFromToText,
							MyTextUtils.addHttpLinks(commentInfo.content)));
				} else {
					viewHolder.messageNameText.setText(mContext.getString(R.string.shide_msg_prompt));
				}
				break;
			case MessageType.VOICE:
				viewHolder.messageNameText.setMaxWidth(FeatureFunction.dip2px(mContext, 150));
				viewHolder.messageNameText.setText(replyFromToText);
				if (mCurrentModel == TEXT_MODEL) {
					viewHolder.messageNameText.setText(MyTextUtils.getSpannableString(replyFromToText,
							commentInfo.content));
				}

				if (mCurrentModel == VOICE_MODEL) {
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
				break;
			case MessageType.PICTURE:
				viewHolder.messageNameText.setMaxWidth(FeatureFunction.dip2px(mContext, 150));

				int width = (int) (MyUtils.dip2px(mContext, commentInfo.imgWidth) * 0.7);
				int height = (int) (MyUtils.dip2px(mContext, commentInfo.imgHeight) * 0.7);

				viewHolder.picImageView.getLayoutParams().width = width;
				viewHolder.picImageView.getLayoutParams().height = height;
				viewHolder.messageNameText.setText(replyFromToText);
				final String path = commentInfo.imgUrlS;
				if (path.startsWith("http://")) {
					ImageLoaderUtil.displayImageByProgress(path, viewHolder.picImageView, options,
							viewHolder.progressBar);
				} else {
					ImageLoaderUtil.displayImageByProgress("file://" + path, viewHolder.picImageView, options,
							viewHolder.progressBar);
				}
				viewHolder.picImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext, ShowImagesActivity.class);
						intent.putExtra("position", position);
						intent.putExtra("msgList", (Serializable) messageInfos);
						mContext.startActivity(intent);
					}
				});
				break;

			default:
				break;
			}
			return convertView;
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

						for (MessageInfo msg : data.data) {
							msg.sendState = MessageState.STATE_SEND_SUCCESS;
						}
						messageInfos.addAll(data.data);
						notifyDataSetChanged();
						mListView.getRefreshableView().setSelection(data.data.size());

						if (data.pageInfo != null) {
							isFull = data.pageInfo.hasMore == 0;// true not has
																// more
							messageInfo.commentCount = data.pageInfo.total;
							updateCommentCountToDb();
							bindCommentCountView();
							sendNotify();
						}
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

	public final static int MODE_VOICE = 0;
	public final static int MODE_TEXT = 1;

	public void setCurrentModel(int model) {
		mCurrentModel = model;
	}

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
		case R.id.ab_chat_text:
			int imgaeId = R.drawable.icon_chat_title_mode_text;
			if (mCurrentModel == BaseChatAdapter.MODE_VOICE) {
				setCurrentModel(BaseChatAdapter.MODE_TEXT);
			} else {
				setCurrentModel(BaseChatAdapter.MODE_VOICE);
				imgaeId = R.drawable.icon_chat_title_voice_mode;
			}
			bindView();
			mAdapter.notifyDataSetChanged();
			((ImageView) v).setImageResource(imgaeId);
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
		cameralHelper.btnCameraAction();
	}

	protected void btnPhotoAction() {
		cameralHelper.btnPhotoAction();
	}

	private CameralHelper.GetImageCallback picCallback = new CameralHelper.SimpleCallback() {
		@Override
		public void receiveOriginPic(String path) {
			sendPicFile(MessageType.PICTURE, path);
		}

		@Override
		public void receiveOriginPicList(List<String> pathList) {
			for (String path : pathList) {
				if (!TextUtils.isEmpty(path)) {
					sendPicFile(MessageType.PICTURE, path);
				}
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		cameralHelper.onActivityResult(requestCode, resultCode, data);
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
			Point point = ChatMsgHelper.sizeOfPic(filePath);
			msg.imgWidth = point.x;
			msg.imgHeight = point.y;
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
		if (isAnony() && mIdentity != null) {
			msg.displayname = mIdentity.name;
			msg.headImgUrl = mIdentity.head;
			msg.heroid = mIdentity.id;
			msg.isanonymity = 1;
		} else {
			msg.displayname = User.getUserName(mLogin);
			msg.headImgUrl = mLogin.headsmall;
			msg.isanonymity = 0;
		}

		msg.type = mChatType;

		msg.commenterid = commenterid;
		msg.commentername = commenterName;
		msg.reisanonymity = reisanonymity;
		return msg;
	}

	private String getName() {
		if (isAnony() && mIdentity != null) {
			return mIdentity.name;
		} else {
			return User.getUserName(mLogin);
		}
	}

	// do not save comment to database
	protected void addSaveSendMessage(MessageInfo msg) {
		msg.sendState = MessageState.STATE_SENDING;
		// addSaveMessageInfo(msg);
		addMessageInfo(msg);
		sendMessage(msg);

	}

	// comment count has been updated in db before notify, so ignore it
	protected void addNotifyMessage(MessageInfo msg) {
		addMessageInfo(msg);
		messageInfo.commentCount = messageInfo.commentCount + 1;
		bindCommentCountView();
	}

	protected void addMessageInfo(MessageInfo info) {
		messageInfos.add(0, info);
		notifyDataSetChanged();
	}

	protected void addSaveMessageInfo(MessageInfo info) {
		addMessageInfo(info);
		updateCommentCountToDb();
	}

	protected void updateCommentCountToDb() {
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
						ChatCommentsActivity.this.messageInfo.commentCount++;
						updateCommentCountToDb();
						bindCommentCountView();
						sendNotify();
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
		Logger.d(this, "send failde===========");
		modifyMessageState(msg);
	}

	protected void modifyMessageState(MessageInfo messageInfo) {
		for (int i = 0, count = messageInfos.size(); i < count; i++) {
			Logger.d(this, "send failde=========" + i);
			if (messageInfo.tag.equals(messageInfos.get(i).tag)) {
				MessageInfo tempInfo = messageInfos.get(i);
				tempInfo.sendState = messageInfo.sendState;
				tempInfo.id = messageInfo.id;
				tempInfo.time = messageInfo.time;
				/**
				 * Avoid updating picture location after sending success, it may
				 * lead to a new http request. Just save the information into
				 * database and fetch from Internet next time.
				 */
				tempInfo.imgUrlS = messageInfo.imgUrlS;
				tempInfo.imgUrlL = messageInfo.imgUrlL;
				tempInfo.imgWidth = messageInfo.imgWidth;
				tempInfo.imgHeight = messageInfo.imgHeight;
				tempInfo.content = messageInfo.content;
				tempInfo.voiceUrl = messageInfo.voiceUrl;
				tempInfo.readState = messageInfo.readState;
				tempInfo.time = messageInfo.time;
				tempInfo.displayname = messageInfo.displayname;
				tempInfo.headImgUrl = messageInfo.headImgUrl;
				tempInfo.isReadVoice = messageInfo.isReadVoice;
				Logger.d(this, "send failde");
				mAdapter.notifyDataSetChanged();
				return;
			}
		}
	}

	private void getZanList() {
		DamiInfo.getMessageZanList(messageInfo.id, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				final MsgZanListResult data = (MsgZanListResult) o;
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
			for (ZanBean info : zanList) {
				if (info.uid.equals(mLogin.uid)) {
					zanCountBtn
							.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_msg_detail_zan_active));
					break;
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

	private List<SpanUser> getZanUserList(List<ZanBean> zanBeans) {
		List<SpanUser> spanUsers = new ArrayList<SpanUser>();
		for (ZanBean zanBean : zanBeans) {
			SpanUser spanUser = new SpanUser();
			spanUser.realname = zanBean.displayname;
			if (zanBean.isanonymity == 1) {
				spanUser.uid = "-1";
			} else {
				spanUser.uid = zanBean.uid;
			}
			spanUsers.add(spanUser);
		}
		return spanUsers;
	}

	private class PlayCallback extends MediaUIHeper.PlayCallback {

		@Override
		public void onStart() {
			bindView();
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void onStop(boolean stopAutomatic) {
			bindView();
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
			// msgHelper.goToRetrweet(msgInfo);
			msgHelper.spreadToDy(msgInfo);
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