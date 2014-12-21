package com.gaopai.guiren.activity.chat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.LocalPicActivity;
import com.gaopai.guiren.activity.LocalPicPathActivity;
import com.gaopai.guiren.activity.RotateImageActivity;
import com.gaopai.guiren.adapter.BaseChatAdapter;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageState;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.SendMessageResult;
import com.gaopai.guiren.db.ConverseationTable;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.media.SpeexPlayerWrapper;
import com.gaopai.guiren.media.SpeexPlayerWrapper.OnDownLoadCallback;
import com.gaopai.guiren.receiver.NotifyChatMessage;
import com.gaopai.guiren.receiver.PushChatMessage;
import com.gaopai.guiren.service.SnsService;
import com.gaopai.guiren.service.type.XmppType;
import com.gaopai.guiren.support.ActionHolder;
import com.gaopai.guiren.support.ConversationHelper;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.view.pulltorefresh.PullToRefreshListView;
import com.gaopai.guiren.volley.SimpleResponseListener;
import com.tencent.a.b.i;

//处理聊天界面的逻辑
public abstract class ChatBaseActivity extends BaseActivity {
	protected List<MessageInfo> messageInfos = null;

	protected User mLogin;

	public final static int MSG_INIT_COMPONENT = 100;

	static final int REQUEST_GET_IMAGE_BY_CAMERA = 1002;
	static final int REQUEST_ROTATE_IMAGE = 1003;
	static final int REQUEST_GET_URI = 101;
	public static final int REQUEST_GET_BITMAP = 124;
	public static final int REQUEST_GET_BITMAP_LIST = 125;

	private String TEMP_FILE_NAME = "header.jpg";

	protected Handler mHandler;
	private List<String> downVoiceList = new ArrayList<String>();
	protected SpeexPlayerWrapper speexPlayerWrapper;

	protected BaseChatAdapter mAdapter;
	protected PullToRefreshListView mListView;

	protected String mId;// 部落会议id，或者对方用户id

	public static final String KEY_MESSAGE = "message";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		speexPlayerWrapper = new SpeexPlayerWrapper(mContext, new OnDownLoadCallback() {
			@Override
			public void onSuccess(MessageInfo messageInfo) {
				// TODO Auto-generated method stub
				downVoiceSuccess(messageInfo);
			}
		});
		if (messageInfos == null) {
			messageInfos = new ArrayList<MessageInfo>();
		}
	}

	protected void initAdapter(BaseChatAdapter chatAdapter) {
		// mHandler = handler;
		mAdapter = chatAdapter;
		mAdapter.setResendClickListener(resendClickListener);
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

	// 从网络下载下来后首先设置message初始状态， order = 1倒叙插入
	protected List<MessageInfo> parseMessageList(List<MessageInfo> list, int order) {
		List<MessageInfo> mList = new ArrayList<MessageInfo>();
		for (MessageInfo messageInfo : list) {
			messageInfo.readState = 1;
			messageInfo.sendState = 1;
			messageInfo.isReadVoice = 1;
			if (messageInfo.fileType == MessageType.VOICE) {
				messageInfo.sendState = 4;
			}
			if (order == 1) {
				mList.add(0, messageInfo);
			} else {
				mList.add(messageInfo);
			}
		}
		return mList;
	}

	protected void addMessageInfo(MessageInfo info) {
		Log.d(TAG, "add message");
		messageInfos.add(info);
		mAdapter.notifyDataSetChanged();
		scrollToBottom();
	}

	protected void scrollToBottom() {
		if (messageInfos != null && messageInfos.size() != 0) {
			mListView.getRefreshableView().setSelection(messageInfos.size() - 1);
		}
	}

	protected void insertMessage(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.insert(messageInfo);
	}

	protected void insertMessages(List<MessageInfo> messageInfos) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.insert(messageInfos);
	}

	protected void updateNewMessage(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.updateMessage(messageInfo);
	}

	protected void updateMessage(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.update(messageInfo);
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
		saveMsgToLastConversation(msg);
		sendMessage(msg);
	}

	private void saveMsgToLastConversation(MessageInfo msg) {
		ConversationHelper.saveToLastMsgList(msg, mContext, true);
	}

	protected void sendFilePath(MessageInfo messageInfo, int isResend) {
		sendMessage(messageInfo);
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

			Logger.d(this, "name=" + msg.displayname);
			addSaveSendMessage(msg);
		}
	}

	protected MessageInfo buildMessage() {
		MessageInfo msg = new MessageInfo();
		msg.from = mLogin.uid;
		msg.tag = UUID.randomUUID().toString();
		msg.time = System.currentTimeMillis();
		msg.readState = 1;
		return msg;
	}

	private boolean isTextEmpty(String str) {
		return (str != null)
				&& (str.trim().replaceAll("\r", "").replaceAll("\t", "").replaceAll("\n", "").replaceAll("\f", "")) != "";
	}

	void sendPicFile(int type, String filePath) {
		sendVoiceFile(type, filePath, 0, false);
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

	private void sendMessage(final MessageInfo msg) {
		Log.d(TAG, "send voice change file name" + msg.time);

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
						messageInfo.time = msg.time;
						updateNewMessage(messageInfo);
						modifyMessageState(messageInfo);
						return;
					} else if (data.state.code == 1) {
						sendFailed(msg);
					} else {
						this.otherCondition(data.state, ChatBaseActivity.this);
					}
					handleExtralSendSuccessConditon(data, msg);
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

	// override by tribe chat activity
	protected void handleExtralSendSuccessConditon(SendMessageResult data, MessageInfo msg) {
	}

	private void sendFailed(MessageInfo msg) {
		msg.sendState = MessageState.STATE_SEND_FAILED;
		modifyMessageState(msg);
	}

	protected void modifyMessageState(MessageInfo messageInfo) {
		if (messageInfo.parentid.equals("0")) {
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
	}

	protected void btnCameraAction() {
		getImageFromCamera();
	}

	protected void btnPhotoAction() {
		getImageFromGallery();
	}

	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// TEMP_FILE_NAME = FeatureFunction.getPhotoFileName();

		if (FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY)) {
			File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY,
					TEMP_FILE_NAME);
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
					Logger.d(this, "path=" + path);
					String extension = path.substring(path.indexOf("."), path.length());
					if (FeatureFunction.isPic(extension)) {

						// if (!TextUtils.isEmpty(path)) {
						// sendPicFile(MessageType.PICTURE, path);
						// }
						Intent intent = new Intent();
						intent.putExtra(RotateImageActivity.KEY_IMAGE_PATH, path);
						intent.setClass(ChatBaseActivity.this, RotateImageActivity.class);
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
						Logger.d(this, path);
						sendPicFile(MessageType.PICTURE, path);
					}
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private boolean isLight = true;

	@Override
	protected void onStop() {
		super.onStop();
		isLight = false;
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		if (pm.isScreenOn()) {
			stopPlayVoice();
		}
	}

	private void stopPlayVoice() {
		if (speexPlayerWrapper != null) {
			speexPlayerWrapper.stop();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		isLight = true;
	}

	protected OnClickListener resendClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MessageInfo messageInfo = (MessageInfo) v.getTag();
			sendMessage(messageInfo);
		}
	};

	public static final String DESTORY_ACTION = "com.gaopai.guiren.intent.action.DESTORY_ACTION";
	public static final String REFRESH_ADAPTER = "com.gaopai.guiren.intent.action.REFRESH_ADAPTER";
	public static final String ACTION_READ_VOICE_STATE = "com.gaopai.guiren.sns.push.ACTION_READ_VOICE_STATE";
	public static final String ACTION_CHANGE_FRIEND = "com.gaopai.guiren.intent.action.ACTION_CHANGE_FRIEND";
	public static final String ACTION_RECORD_AUTH = "com.gaopai.guiren.intent.action.ACTION_RECORD_AUTH";

	public static final String ACTION_MEETING_DESTROY_LIST = "com.gaopai.guiren.intent.action.ACTION_MEETING_DESTROY_LIST";
	public final static String ACTION_DESTROY_MESSAGE_LIST = "com.gaopai.guiren.intent.action.ACTION_DESTROY_MESSAGE_LIST";

	public final static String ACTION_SHIED_MESSAGE = "com.gaopai.guiren.intent.action.ACTION_SHIED_MESSAGE";
	public final static String ACTION_FAVORITE_MESSAGE = "com.gaopai.guiren.intent.action.ACTION_FAVORITE_MESSAGE";
	public final static String ACTION_UNFAVORITE_MESSAGE = "com.gaopai.guiren.intent.action.ACTION_UNFAVORITE_MESSAGE";
	public final static String ACTION_ZAN_MESSAGE = "com.gaopai.guiren.intent.action.ACTION_ZAN_MESSAGE";
	public final static String ACTION_UNZAN_MESSAGE = "com.gaopai.guiren.intent.action.ACTION_UNZAN_MESSAGE";
	public final static String ACTION_COMMENT_OR_ZAN_OR_FAVOURITE = "com.gaopai.guiren.intent.action.ACTION_COMMENT_OR_ZAN_OR_FAVOURITE";
	public final static String ACTION_MESSAGE_DELETE = "com.gaopai.guiren.intent.action.ACTION_MESSAGE_DELETE";

	public final static String UPDATE_COUNT_ACTION = "com.gaopai.guiren.intent.action.UPDATE_COUNT_ACTION";
	public final static String ACTION_EXIT_TRIBE = "com.gaopai.guiren.intent.action.ACTION_EXIT_TRIBE";
	public final static String ACTION_KICK_TRIBE = "com.gaopai.guiren.intent.action.ACTION_KICK_TRIBE";

	public final static String ACTION_CHANGE_VOICE = "com.gaopai.guiren.intent.action.ACTION_CHANGE_VOICE";

	@Override
	protected void registerReceiver(IntentFilter filter) {
		super.registerReceiver(filter);
		filter.addAction(SnsService.ACTION_CONNECT_CHANGE);
		filter.addAction(PushChatMessage.ACTION_SEND_STATE);
		filter.addAction(NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE);
		filter.addAction(NotifyChatMessage.ACTION_CHANGE_VOICE_CONTENT);
		filter.addAction(DESTORY_ACTION);
		filter.addAction(ACTION_MEETING_DESTROY_LIST);
		filter.addAction(REFRESH_ADAPTER);
		filter.addAction(ACTION_READ_VOICE_STATE);
		filter.addAction(ACTION_KICK_TRIBE);
		filter.addAction(ACTION_EXIT_TRIBE);
		filter.addAction(ACTION_CHANGE_FRIEND);
		filter.addAction(ACTION_SHIED_MESSAGE);
		filter.addAction(ACTION_FAVORITE_MESSAGE);
		filter.addAction(ACTION_ZAN_MESSAGE);
		filter.addAction(ACTION_UNFAVORITE_MESSAGE);
		filter.addAction(ACTION_UNZAN_MESSAGE);
		filter.addAction(ACTION_RECORD_AUTH);
		filter.addAction(ACTION_COMMENT_OR_ZAN_OR_FAVOURITE);
		filter.addAction(ACTION_MESSAGE_DELETE);
		filter.addAction(ACTION_CHANGE_VOICE);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(ActionHolder.ACTION_CANCEL_TRIBE);
		filter.addAction(ActionHolder.ACTION_QUIT_TRIBE);
		filter.addAction(ActionHolder.ACTION_CANCEL_MEETING);
		filter.addAction(ActionHolder.ACTION_QUIT_MEETING);
	}

	private boolean opconnectState = false;

	/** 聊天广播 */

	@Override
	protected void onReceive(Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (SnsService.ACTION_CONNECT_CHANGE.equals(action)) {
			Log.d(TAG, "receiver:" + action);
			String type = intent.getExtras().getString(SnsService.EXTRAS_CHANGE);
			Log.d(TAG, "receiver:Exper" + type);
			if (XmppType.XMPP_STATE_AUTHENTICATION.equals(type)) {
				// 认证成功
				opconnectState = true;
			} else if (XmppType.XMPP_STATE_AUTHERR.equals(type)) {
				// 认证失败
				opconnectState = false;
				showToast(mContext.getString(R.string.login_user_auth_error));
			} else if (XmppType.XMPP_STATE_REAUTH.equals(type)) {
				// 未认证
				opconnectState = false;
			} else if (XmppType.XMPP_STATE_START.equals(type)) {
				// 开始登录
				opconnectState = false;
			} else if (XmppType.XMPP_STATE_STOP.equals(type)) {
				// 没开启登录
				opconnectState = false;
			}
		} else if (PushChatMessage.ACTION_SEND_STATE.equals(action)) {
			Log.d(TAG, "receiver:" + PushChatMessage.ACTION_SEND_STATE);
			MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra(PushChatMessage.EXTRAS_MESSAGE);
			updateMessage(messageInfo);
			modifyMessageState(messageInfo);
		} else if (NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE.equals(action)) {// 接受新的消息广播
			final MessageInfo msg = (MessageInfo) intent
					.getSerializableExtra(NotifyChatMessage.EXTRAS_NOTIFY_CHAT_MESSAGE);
			Log.d("message", "from" + ChatBaseActivity.class.getName());
			notifyMessage(msg);
		} else if (action.equals(DESTORY_ACTION)) {
			ChatBaseActivity.this.finish();
		} else if (action.equals(ACTION_MEETING_DESTROY_LIST)) {
			ChatBaseActivity.this.finish();
		} else if (ACTION_READ_VOICE_STATE.equals(action)) {
			Log.d(TAG, "receive change voice state");
			final MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra(PushChatMessage.EXTRAS_MESSAGE);
			updateMessage(messageInfo);
			// changeVoiceState(messageInfo);

		} else if (action.equals(ACTION_RECORD_AUTH)) {
			Toast.makeText(mContext, mContext.getString(R.string.record_auth_control), Toast.LENGTH_LONG).show();
		} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
			stopPlayVoice();
		}
		onOtherChatBroadCastAction(intent);
	}

	protected abstract void onOtherChatBroadCastAction(Intent intent);

	protected abstract void notifyMessage(final MessageInfo msg);

	protected void addNewMessage(MessageInfo msg) {
		messageInfos.add(msg);
		mAdapter.notifyDataSetChanged();
		Logger.d(this, "size=" + messageInfos.size() + "   visible="
				+ mListView.getRefreshableView().getLastVisiblePosition());
		if (messageInfos.size() - mListView.getRefreshableView().getLastVisiblePosition() <= 2) {
			mListView.getRefreshableView().setSelection(messageInfos.size());// 定位到最后一行
		}
	}

	protected void resetCount(String id) {
		SQLiteDatabase dbDatabase = DBHelper.getInstance(this).getWritableDatabase();
		ConverseationTable table = new ConverseationTable(dbDatabase);
		table.resetCount(id);
	}

}
