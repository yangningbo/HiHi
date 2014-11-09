package com.gaopai.guiren.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageState;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.media.MediaUIHeper;
import com.gaopai.guiren.media.SpeexPlayerWrapper;
import com.gaopai.guiren.media.SpeexPlayerWrapper.OnDownLoadCallback;
import com.gaopai.guiren.receiver.NotifyChatMessage;
import com.gaopai.guiren.utils.ImageLoaderUtil;

/**
 * 顺序播放界面
 * 
 */
public class SequencePlayActivity extends BaseActivity implements OnClickListener {

	private TextView mCurrentNumsView, mTotalNumsView, mUsernameView;
	private ImageView mVoiceModeImage;
	private ImageView mBackwardBtn, mForwardBtn, mPlayBtn;
	private ImageView mHeaderView;
	private List<MessageInfo> mVoiceMsgList = new ArrayList<MessageInfo>();
	private int mCurrentNums = 0;
	private int mTotalNums = 0;
	private SpeexPlayerWrapper playerWrapper;
	private String mTitle;
	private String mID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitleBar();
		setAbContentView(R.layout.activity_sequenceplay);
		// registerFinishReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE);
		registerReceiver(mReceiver, filter);
		initComponent();
		initVoicePlayMode();
		updateVoicePlayModeState(isModeInCall);
	}

	public void updateVoicePlayModeState(boolean isModeInCall) {
		if (isModeInCall) {
			mVoiceModeImage.setVisibility(View.VISIBLE);
		} else {
			mVoiceModeImage.setVisibility(View.GONE);
		}
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				String action = intent.getAction();
				if (action.equals(NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE)) {
					final MessageInfo msg = (MessageInfo) intent
							.getSerializableExtra(NotifyChatMessage.EXTRAS_NOTIFY_CHAT_MESSAGE);
					if (!TextUtils.isEmpty(mID) && mID.equals(msg.to)
							&& (msg.fileType == MessageType.VOICE && msg.voiceTime > 0)) {
						mVoiceMsgList.add(msg);
						mTotalNums = mVoiceMsgList.size();
						bindView();
					}
				}
			}
		}
	};

	private void bindView() {
		mTotalNumsView.setText("/" + mTotalNums + "");
		mCurrentNumsView.setText((mCurrentNums + 1) + "");
		mUsernameView.setText(mVoiceMsgList.get(mCurrentNums).displayname);
		if (!TextUtils.isEmpty(mVoiceMsgList.get(mCurrentNums).headImgUrl)) {
			ImageLoaderUtil.displayImage(mVoiceMsgList.get(mCurrentNums).headImgUrl, mHeaderView);
		} else {
			mHeaderView.setImageResource(R.drawable.default_header);
		}
	}

	private void initComponent() {

		mTitle = getIntent().getStringExtra("title");
		mID = getIntent().getStringExtra("id");
		if (TextUtils.isEmpty(mTitle)) {
			mTitle = mContext.getString(R.string.play_by_sequence);
		}
		mTitleBar.setTitleText(mTitle);
		mTitleBar.setLogo(R.drawable.selector_titlebar_back);

		mVoiceModeImage = (ImageView) mTitleBar.addLeftImageView(R.drawable.voice_mode_in_call_icon);
		mVoiceModeImage.setVisibility(isModeInCall ? View.VISIBLE : View.GONE);

		mCurrentNumsView = (TextView) findViewById(R.id.currentNums);
		mTotalNumsView = (TextView) findViewById(R.id.totalNums);
		mUsernameView = (TextView) findViewById(R.id.username);
		mBackwardBtn = (ImageView) findViewById(R.id.backward);
		mBackwardBtn.setOnClickListener(this);
		mForwardBtn = (ImageView) findViewById(R.id.forward);
		mForwardBtn.setOnClickListener(this);
		mPlayBtn = (ImageView) findViewById(R.id.play);
		mPlayBtn.setOnClickListener(this);
		mHeaderView = (ImageView) findViewById(R.id.header);

		playerWrapper = new SpeexPlayerWrapper(mContext, new OnDownLoadCallback() {
			@Override
			public void onSuccess(MessageInfo msg) {
				if (playerWrapper.getMessageTag().equals(msg.tag)) {
					if (isLight) {
						playerWrapper.start(msg);
					}
					msg.isReadVoice = MessageState.VOICE_READED;
					msg.sendState = MessageState.STATE_SEND_SUCCESS;
				}
			}
		});
		playerWrapper.setPlayCallback(new PlayCallback());

		List<MessageInfo> msgList = (List<MessageInfo>) getIntent().getSerializableExtra("msgList");

		if (msgList != null) {
			for (int i = 0; i < msgList.size(); i++) {
				if (msgList.get(i).fileType == MessageType.VOICE && msgList.get(i).voiceTime > 0) {
					mVoiceMsgList.add(msgList.get(i));
				}
			}
		}
		mTotalNums = mVoiceMsgList.size();
		bindView();
		if (mTotalNums != 0) {
			playerWrapper.start(mVoiceMsgList.get(mCurrentNums));
		}
	}

	private void setStartPlayState() {
		mPlayBtn.setImageResource(R.drawable.voice_pause_icon);
	}

	private void setStopPlayState() {
		mPlayBtn.setImageResource(R.drawable.voice_play);
	}

	private class PlayCallback extends MediaUIHeper.PlayCallback {
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			setStartPlayState();
			DamiApp.getInstance().setPlayMode();
			// MyUtils.muteAudioFocus(mContext, true);
		}

		@Override
		public void onStop(boolean stopAutomatic) {
			if (stopAutomatic) {
				playNextVoice();
				return;
			} 
			setStopPlayState();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		playerWrapper.stop();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isLight = true;
		if (pauseVoice) {
			playerWrapper.start(mVoiceMsgList.get(mCurrentNums));
			pauseVoice = false;
		}
	}

	private boolean pauseVoice = false;

	@Override
	protected void onStop() {
		super.onStop();
		isLight = false;
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		if (pm.isScreenOn()) {
			if (playerWrapper.isPlay()) {
				pauseVoice = true;
				playerWrapper.stop();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.backward:
			playPreviousVoice();
			break;

		case R.id.play:
			playerWrapper.start(mVoiceMsgList.get(mCurrentNums));
			break;

		case R.id.forward:
			playNextVoice();
			break;

		default:
			break;
		}
	}

	private void playNextVoice() {
		if (mTotalNums == 0) {
			showToast(mContext.getString(R.string.voice_list_is_null));
			return;
		}
		if (mCurrentNums == mTotalNums - 1) {
			showToast(mContext.getString(R.string.the_last_voice));
			setStopPlayState();
			return;
		}
		mCurrentNums++;
		Log.d("play", "click mCurrentNums=" + mCurrentNums);
		bindView();
		playerWrapper.start(mVoiceMsgList.get(mCurrentNums));
	}

	private void playPreviousVoice() {
		if (mTotalNums == 0) {
			showToast(mContext.getString(R.string.voice_list_is_null));
			return;
		}
		if (mCurrentNums == 0) {
			showToast(mContext.getString(R.string.the_first_voice));
			return;
		}
		mCurrentNums--;
		bindView();
		playerWrapper.start(mVoiceMsgList.get(mCurrentNums));
	}

	private boolean isLight = true;
}
