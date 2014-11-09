package com.gaopai.guiren.activity.chat;

import java.io.File;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.R;
import com.gaopai.guiren.adapter.BaseChatAdapter;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.net.ChatMessageBean;
import com.gaopai.guiren.media.MediaUIHeper;
import com.gaopai.guiren.media.SpeexRecorderWrapper;
import com.gaopai.guiren.utils.ChatBoxManager;
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

	protected SimpleResponseListener getMessageListListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_chat_main);
		mLogin = DamiCommon.getLoginResult(mContext);

		speexRecorder = new SpeexRecorderWrapper(this);
		speexRecorder.setRecordallback(recordCallback);
		recordDialog = new RecordDialog(this);

		getMessageListListener = new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final ChatMessageBean data = (ChatMessageBean) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && data.data.size() > 0) {
						isFull = data.data.size() < 20;// true not has more page
						mAdapter.addAll(parseMessageList(data.data, 1));
						mListView.getRefreshableView().setSelection(data.data.size());
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
		};

		if (messageInfos == null || messageInfos.size() == 0) {
			getMessageList(false);
		}
	}

	protected void initAdapter(BaseChatAdapter chatAdapter) {
		super.initAdapter(chatAdapter);
		initComponent();
	}

	private void initComponent() {
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);
		mSwitchVoiceTextBtn = (Button) findViewById(R.id.chat_box_btn_text);
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
		mListView.setAdapter(mAdapter);
		mListView.setPullRefreshEnabled(true); // 下拉刷新，启用
		mListView.setPullLoadEnabled(false);// 上拉刷新，禁止
		mListView.setScrollLoadEnabled(true);// 滑动到底部自动刷新，启用
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getMessageList(false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			}
		});

	}

	protected String getMessageMaxId() {
		String maxID = "";
		if (messageInfos != null && messageInfos.size() != 0) {
			maxID = messageInfos.get(0).id;
		}
		return maxID;
	}

	private EditText.OnEditorActionListener mEditActionLister = new EditText.OnEditorActionListener() {
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

	private boolean isChangeVoice = true;



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
		case R.id.chat_box_btn_text:
			boxManager.switchTextVoice();
			break;
		case R.id.chat_box_btn_add:
			boxManager.addGridClick();
			break;
		
		default:
			break;
		}
	}

	

	private boolean isFull = false;

	protected void getMessageList(final boolean isRefresh) {
		if (isFull) {
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
}
