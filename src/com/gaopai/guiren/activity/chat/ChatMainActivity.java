package com.gaopai.guiren.activity.chat;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.BaseChatAdapter;
import com.gaopai.guiren.bean.ConversationBean;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.net.ChatMessageBean;
import com.gaopai.guiren.db.ConverseationTable;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.fragment.NotificationFragment;
import com.gaopai.guiren.media.MediaUIHeper;
import com.gaopai.guiren.media.SpeexRecorderWrapper;
import com.gaopai.guiren.support.ConversationHelper;
import com.gaopai.guiren.support.chat.ChatBoxManager;
import com.gaopai.guiren.utils.Logger;
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

public abstract class ChatMainActivity extends ChatBaseActivity implements OnClickListener {

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
	private ImageView mVoiceModeImage;
	private EditText mContentEdit;
	private Button mSwitchVoiceTextBtn;
	private ChatBoxManager boxManager;
	private View layoutChatbox;

	protected PreferenceOperateUtils spo;

	protected boolean mHasLocalData = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_chat_main);
		initComponent();
		initTitleBarLocal();
		initViewComponent();
	}

	public class GetMessageListener extends SimpleResponseListener {
		boolean isPullUp;

		public GetMessageListener(Context context, boolean isPullUp) {
			super(context);
			this.isPullUp = isPullUp;
		}

		@Override
		public void onSuccess(Object o) {
			// TODO Auto-generated method stub
			final ChatMessageBean data = (ChatMessageBean) o;
			if (data.state != null && data.state.code == 0) {
				if (data.data != null && data.data.size() > 0) {
					isFull = data.data.size() < 20;
					if (isPullUp) {// 上拉加载的加到列表后面
						mAdapter.addAllAppend(parseMessageList(data.data, 1));
						mListView.getRefreshableView().setSelection(messageInfos.size() - 1);
					} else {// 下拉刷新的加到列表前面
						mAdapter.addAll(parseMessageList(data.data, 1));
						mListView.getRefreshableView().setSelection(data.data.size() - 1);
					}

				} else {
					isFull = true;
				}
				mListView.setHasMoreData(!isFull);
			} else {
				otherCondition(data.state, ChatMainActivity.this);
			}
		}

		@Override
		public void onFinish() {
			mListView.onPullComplete();
		}
	}

	private void initComponent() {
		// TODO Auto-generated method stub
		mLogin = DamiCommon.getLoginResult(mContext);
		speexRecorder = new SpeexRecorderWrapper(this);
		speexRecorder.setRecordallback(recordCallback);
		recordDialog = new RecordDialog(this);
		recordDialog.getClass().getClass().getClass();
		spo = new PreferenceOperateUtils(mContext, SPConst.SP_AVOID_DISTURB);
	}

	protected void hideChatBox() {
		layoutChatbox.setVisibility(View.GONE);
	}

	protected void getMessageListLocal(boolean isFirstTime) {
		// implementation is in sub class
		// first query database with autoID=-1 (call initMessage)
		// then continue to query database with autoID=maxId to get more data
		// (call loadMessage)
		// if no more data then fetch data from internet
	}

	protected ImageView ivDisturb;

	protected void initAdapter(BaseChatAdapter chatAdapter) {
		super.initAdapter(chatAdapter);
		mListView.setAdapter(mAdapter);
		if (messageInfos == null || messageInfos.size() == 0) {
			// getMessageList(false);
			getMessageListLocal(true);
		}
	}

	protected void initMessage(String id, int type) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
		MessageTable messageTable = new MessageTable(db);
		List<MessageInfo> tempMessageInfos = messageTable.query(id, -1, type);
		if (tempMessageInfos.size() == 0) {
			mHasLocalData = false;
		} else {
			for (int i = 0; i < tempMessageInfos.size(); i++) {
				if (tempMessageInfos.get(i).readState == 0) {
					tempMessageInfos.get(i).readState = 1;
					updateMessage(tempMessageInfos.get(i));
				} else if (tempMessageInfos.get(i).sendState == 2) {
					tempMessageInfos.get(i).sendState = 0;
					updateMessage(tempMessageInfos.get(i));
				}
			}
			mAdapter.addAll(tempMessageInfos);
			if (tempMessageInfos.size() < 20) {
				mHasLocalData = false;
			}
			scrollToBottom();
		}
	}

	protected void loadMessage(String id, int chatType) {
		Logger.d(this, "haslocaldata=" + mHasLocalData);
		if (mHasLocalData) {
			if (mAdapter.getMessageInfos().size() == 0) {
				mHasLocalData = false;
				return;
			}
			SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
			MessageTable messageTable = new MessageTable(db);
			List<MessageInfo> tempList = messageTable.query(id, mAdapter.getMessageInfos().get(0).auto_id, chatType);
			if (tempList == null || tempList.size() < DamiCommon.LOAD_SIZE) {
				mHasLocalData = false;
			}
			if (tempList != null && tempList.size() != 0) {
				mListView.getRefreshableView().setSelection(tempList.size());
				mAdapter.addAll(tempList);
			}
			mListView.onPullComplete();
		} else {
			getMessageList(false);
		}
	}

	public void updateVoicePlayModeState(boolean isModeInCall) {
		if (isModeInCall) {
			mVoiceModeImage.setVisibility(View.VISIBLE);
		} else {
			mVoiceModeImage.setVisibility(View.GONE);
		}
	}

	public void showVoiceModeToastAnimation() {
		voiceModeToast.setVisibility(View.VISIBLE);
		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.voice_palymode_anim);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				voiceModeToast.setVisibility(View.GONE);
			}
		});
		voiceModeToast.startAnimation(animation);
	}

	public void checkHasDraft(String id) {
		SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getWritableDatabase();
		ConverseationTable table = new ConverseationTable(dbDatabase);
		ConversationBean conversationBean = table.queryByID(id);
		if (conversationBean != null && !TextUtils.isEmpty(conversationBean.unfinishinput)) {
			setDraft(conversationBean.unfinishinput);
		}
	}

	public void setDraft(String draft) {
		mContentEdit.setText(draft);
		boxManager.switchToText(false);
	}

	public void changePlayMode() {
		if (isModeInCall) {// 如果是听筒
			setPlayMode(false);// 那么就喇叭
			updateVoicePlayModeState(false);
			Toast.makeText(mContext, mContext.getString(R.string.switch_to_mode_in_speaker), Toast.LENGTH_SHORT).show();
		} else {
			setPlayMode(true);// 不然就听筒
			updateVoicePlayModeState(true);
			Toast.makeText(mContext, mContext.getString(R.string.switch_to_mode_in_call), Toast.LENGTH_SHORT).show();
		}
	}

	private void initTitleBarLocal() {
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		setTitleText();
		voiceModeToast = (LinearLayout) findViewById(R.id.voiceModeToast);
		ivDisturb = (ImageView) mTitleBar.addLeftImageViewWithDefaultSize(R.drawable.icon_chat_title_avoid_disturb_off);
		int imageId = R.drawable.icon_chat_title_ear_phone;
		mVoiceModeImage = (ImageView) mTitleBar.addLeftImageViewWithDefaultSize(R.drawable.icon_chat_title_ear_phone);
		initVoicePlayMode();
		if (isModeInCall) {
			mVoiceModeImage.setVisibility(View.VISIBLE);
		} else {
			mVoiceModeImage.setVisibility(View.GONE);
		}

		imageId = R.drawable.icon_chat_title_voice_mode;
		View view = mTitleBar.addRightImageView(imageId);
		view.setId(R.id.ab_chat_text);
		view.setOnClickListener(this);

		view = mTitleBar.addRightImageView(R.drawable.icon_chat_title_more);
		view.setId(R.id.ab_chat_more);
		view.setOnClickListener(this);
	}

	protected abstract boolean isAvoidDisturb();

	protected void switchAvoidDisturb() {
		if (isAvoidDisturb()) {
			ivDisturb.setVisibility(View.VISIBLE);
		} else {
			ivDisturb.setVisibility(View.GONE);
		}

	}

	protected void setTitleText() {
		// call before add ivDisturb
	};

	protected void initViewComponent() {
		mSwitchVoiceTextBtn = (Button) findViewById(R.id.chat_box_btn_switch_voice_text);
		mSwitchVoiceTextBtn.setOnClickListener(this);
		layoutChatbox = ViewUtil.findViewById(this, R.id.chat_box);

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
		mSendTextBtn.setVisibility(View.GONE);
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
		mAddBtn.setVisibility(View.VISIBLE);

		boxManager = new ChatBoxManager(this, mContentEdit, mSwitchVoiceTextBtn, mEmotionPicker, chatGridLayout,
				mEmotionBtn, mVoiceSendBtn);

		mListView = (PullToRefreshListView) findViewById(R.id.listview);
		mListView.getRefreshableView().setDivider(null);

		mListView.setPullRefreshEnabled(true); // 下拉刷新，启用
		mListView.setPullLoadEnabled(false);// 上拉刷新，禁止
		mListView.setScrollLoadEnabled(false);// 滑动到底部自动刷新，启用
		mListView.getRefreshableView().setSelector(mContext.getResources().getDrawable(R.color.transparent));
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// getMessageList(false);
				getMessageListLocal(false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			}
		});

	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		switchAvoidDisturb();
	}

	protected String getMessageMaxId() {
		String maxID = "";
		if (messageInfos != null && messageInfos.size() != 0) {
			maxID = messageInfos.get(0).id;
		}
		return maxID;
	}

	protected String getMessageMinId() {
		String minID = "";
		if (messageInfos != null && messageInfos.size() != 0) {
			minID = messageInfos.get(messageInfos.size() - 1).id;
		}
		return minID;
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

	public void showSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContentEdit.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}

	protected boolean isChangeVoice = true;

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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
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

		case R.id.ab_chat_text:
			int imgaeId = R.drawable.icon_chat_title_mode_text;
			if (mAdapter.getCurrentMode() == BaseChatAdapter.MODEL_VOICE) {
				mAdapter.setCurrentMode(BaseChatAdapter.MODE_TEXT);
			} else {
				mAdapter.setCurrentMode(BaseChatAdapter.MODEL_VOICE);
				imgaeId = R.drawable.icon_chat_title_voice_mode;
			}
			mAdapter.notifyDataSetChanged();
			((ImageView) v).setImageResource(imgaeId);
			break;

		case R.id.ab_chat_ear_phone:
			changePlayMode();
			break;

		default:
			break;
		}
	}

	private boolean isFull = false;

	protected void getMessageList(final boolean isPullUp) {
		if (isFull && (!isPullUp)) {
			mListView.setHasMoreData(!isFull);
			mListView.onPullComplete();
			return;
		}
	}

	private void afterSendChnageState() {
		mVoiceSendBtn.setText(mContext.getString(R.string.pressed_to_record));
		mContentEdit.setHint(mContext.getString(R.string.input_message_hint));
		mContentEdit.setText("");
	}

	// protected void changeToComment() {
	// mVoiceSendBtn.setText(mContext.getString(R.string.pressed_to_comment));
	// mContentEdit.setHint(mContext.getString(R.string.input_message_comment_hint));
	// }

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

	@Override
	protected void onOtherChatBroadCastAction(Intent intent) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void notifyMessage(MessageInfo msg) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		saveDraft(mContentEdit.getText().toString());

	}

	public void saveDraft(String draft) {
		MessageInfo msg = buildMessage();
		msg.fileType = MessageType.TEXT;
		msg.content = draft;
		if (ConversationHelper.saveDraft(mContext, msg)) {
			sendBroadcast(new Intent(NotificationFragment.ACTION_MSG_NOTIFY));
		}
	}
}