package com.gaopai.guiren.activity.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.MeetingDetailActivity;
import com.gaopai.guiren.activity.TribeDetailActivity;
import com.gaopai.guiren.adapter.TribeChatAdapter;
import com.gaopai.guiren.bean.Identity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageState;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.NotifiyVo;
import com.gaopai.guiren.bean.NotifyMessageBean.ConversationInnerBean;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.TribeInfoBean;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.net.IdentitityResult;
import com.gaopai.guiren.bean.net.SendMessageResult;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.IdentityTable;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.receiver.NotifyChatMessage;
import com.gaopai.guiren.support.ActionHolder;
import com.gaopai.guiren.support.ConversationHelper;
import com.gaopai.guiren.support.NotifyHelper;
import com.gaopai.guiren.support.chat.ChatMsgDataHelper;
import com.gaopai.guiren.support.chat.ChatMsgDataHelper.Callback;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.PreferenceOperateUtils;
import com.gaopai.guiren.utils.SPConst;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class ChatTribeActivity extends ChatMainActivity implements OnClickListener {
	public static final int CHAT_TYPE_MEETING = 300;
	public static final int CHAT_TYPE_TRIBE = 200;

	public static final String KEY_CHAT_TYPE = "chat_type";
	public static final String KEY_TRIBE = "tribe";
	public static final String KEY_IS_ONLOOKER = "onlooker";

	private String mTribeId;

	protected int mChatType = 0;
	protected Tribe mTribe;
	protected Identity mIdentity;

	private MessageInfo messageInfo;
	private boolean isOnLooker = false;

	private ChatMsgDataHelper msgHelper;

	private PreferenceOperateUtils spoAnony;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mChatType = getIntent().getIntExtra(KEY_CHAT_TYPE, CHAT_TYPE_MEETING);
		mTribe = (Tribe) getIntent().getSerializableExtra(KEY_TRIBE);// before
		isOnLooker = getIntent().getBooleanExtra(KEY_IS_ONLOOKER, false);
		super.onCreate(savedInstanceState);
		msgHelper = new ChatMsgDataHelper(mContext, callback, mTribe, mChatType);
		spoAnony = new PreferenceOperateUtils(mContext, SPConst.SP_ANONY);
		if (mTribe.role == -1) {
			updateTribe();
		}
		if (!isOnLooker) {
			getIdentity();
		}
		initTribeComponent();
	}

	private void setUpForOnLooker() {
		hideChatBox();
		startTimer();
	}

	private Timer mTimer;
	private TimerTask mTask;
	private final int mRefreshTime = 10 * 1000;

	private void startTimer() {
		mTimer = new Timer();
		mTask = new TimerTask() {
			@Override
			public void run() {
				// want to get message with id larger than current sinceId
				getMessageList(true);
			}
		};
		mTimer.schedule(mTask, 0, mRefreshTime);
	}

	private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
			mTimer = null;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopTimer();
	}

	protected void initTribeComponent() {
		if (isOnLooker) {
			setUpForOnLooker();
		}
		mAdapter = new TribeChatAdapter(mContext, speexPlayerWrapper, messageInfos, isOnLooker);
		super.initAdapter(mAdapter);
		ivDisturb.setImageLevel(spo.getInt(SPConst.getTribeUserId(mContext, mTribe.id), 0));

		// if (!isOnLooker) {
		mListView.getRefreshableView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				showItemLongClickDialog(messageInfos.get(position));
				return true;
			}
		});
		// }

		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				MessageInfo messageInfo = messageInfos.get(position);
				if (messageInfo.fileType == MessageType.LOCAL_ANONY_FALSE
						|| messageInfo.fileType == MessageType.LOCAL_ANONY_TRUE) {
					return;
				}
				if (messageInfo.mIsShide == MessageState.MESSAGE_SHIDE) {
					return;
				}
				if (messageInfo.sendState == MessageState.STATE_SENDING
						|| messageInfo.sendState == MessageState.STATE_SEND_FAILED) {
					return;
				}
				Intent intent = new Intent(ChatTribeActivity.this, ChatCommentsActivity.class);
				intent.putExtra(ChatCommentsActivity.INTENT_CHATTYPE_KEY, mChatType);
				intent.putExtra(ChatCommentsActivity.INTENT_TRIBE_KEY, mTribe);
				intent.putExtra(ChatCommentsActivity.INTENT_MESSAGE_KEY, messageInfos.get(position));
				intent.putExtra(ChatCommentsActivity.INTENT_IDENTITY_KEY, mIdentity);
				intent.putExtra(ChatCommentsActivity.INTENT_SENCE_ONLOOK_KEY, isOnLooker);
				startActivity(intent);
			}
		});

		messageInfo = (MessageInfo) getIntent().getSerializableExtra(KEY_MESSAGE);
		if (messageInfo != null) {
			buildRetweetMessageInfo(messageInfo);
			addSaveSendMessage(messageInfo);
		}
		isChangeVoice = isAnony();
		setChangeVoiceView(isChangeVoice);
	}

	public static Intent getIntent(Context context, Tribe tribe, int type) {
		Intent intent = new Intent(context, ChatTribeActivity.class);
		intent.putExtra(KEY_TRIBE, tribe);
		intent.putExtra(KEY_CHAT_TYPE, type);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

	public static Intent getIntent(Context context, Tribe tribe, int type, boolean isOnLooker) {
		Intent intent = new Intent(context, ChatTribeActivity.class);
		intent.putExtra(KEY_TRIBE, tribe);
		intent.putExtra(KEY_CHAT_TYPE, type);
		intent.putExtra(KEY_IS_ONLOOKER, isOnLooker);
		return intent;
	}

	@Override
	protected boolean isAvoidDisturb() {
		// TODO Auto-generated method stub
		return spo.getInt(SPConst.getTribeUserId(mContext, mTribe.id), 0) == 1;
	}

	@Override
	protected void setTitleText() {
		mTitleBar.addLeftTextView(mTribe.name);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkHasDraft(mTribe.id);
		NotifyHelper.setCurrentChatId(mContext, mTribe.id);
		Logger.d(this, mTribe.id + "  " + NotifyHelper.getCurrentChatId(mContext));
		NotifyHelper.clearMsgNotification(mContext, mChatType);
		ConversationHelper.resetCountAndRefresh(mContext, mTribe.id);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (NotifyHelper.getCurrentChatId(mContext).equals(mTribe.id)) {
			NotifyHelper.setCurrentChatId(mContext, "");
		}
	}

	// 通知过来的tribe没有role，发送消息时需要用到，所以这里尽快更新呀
	private void updateTribe() {
		SimpleResponseListener listener = new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				final TribeInfoBean data = (TribeInfoBean) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null) {
						mTribe = data.data;
						msgHelper.setTribe(mTribe);
					}
				} else {
					otherCondition(data.state, ChatTribeActivity.this);
				}
			}
		};
		if (mChatType == CHAT_TYPE_MEETING) {
			DamiInfo.getMeetingDetail(mTribe.id, listener);
		} else {
			DamiInfo.getTribeDetail(mTribe.id, listener);
		}
	}

	@Override
	protected void getMessageListLocal(boolean isFirstTime) {
		if (isFirstTime) {
			if (isOnLooker) {
				return;
			}
			initMessage(mTribe.id, mChatType);
			if (mAdapter.getCount() == 0) {
				addAndInsertMessage(isAnony());
				loadMessage(mTribe.id, mChatType);
			}
		} else {
			loadMessage(mTribe.id, mChatType);
		}
	}

	protected void buildRetweetMessageInfo(MessageInfo messageInfo) {
		messageInfo.from = mLogin.uid;
		if (mChatType == CHAT_TYPE_MEETING) {
			messageInfo.type = 300;
		} else {
			messageInfo.type = 200;
		}
		messageInfo.displayname = mLogin.displayName;
		messageInfo.headImgUrl = mLogin.headsmall;
		messageInfo.heroid = "";
		messageInfo.tag = UUID.randomUUID().toString();
		messageInfo.title = "";
		messageInfo.time = System.currentTimeMillis();
		messageInfo.readState = 1;
		messageInfo.to = mTribe.id;
		buildConversation(messageInfo);
	}

	public void showItemLongClickDialog(final MessageInfo messageInfo) {
		final List<String> strList = new ArrayList<String>();
		// strList.add(getString(R.string.comment));
		if (!isOnLooker) {
			strList.add(getString(R.string.delete));
		}
		if (messageInfo.isfavorite == 1) {
			strList.add(getString(R.string.cancel_favorite));
		} else {
			strList.add(getString(R.string.favorite));
		}

		if (messageInfo.fileType == MessageType.TEXT
				|| (messageInfo.fileType == MessageType.VOICE && mAdapter.isInTextMode())) {
			strList.add(getString(R.string.copy));
		}
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
		if (!messageInfo.from.equals(DamiCommon.getUid(mContext))) {// 如果来自自己，则不交往，举报
			strList.add(getString(R.string.communication));
			strList.add(getString(R.string.report));

		}
		strList.add(getString(R.string.retrweet));
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
			zanMessage(msgInfo);
		} else if (result.equals(getString(R.string.retrweet))) {
			// goToRetrweet(msgInfo);
			msgHelper.spreadToDy(msgInfo);
		} else if (result.equals(getString(R.string.report))) {
			msgHelper.showReportDialog(msgInfo);
		} else if (result.equals(getString(R.string.favorite))) {
			favoriteMessage(msgInfo);
		} else if (result.equals(getString(R.string.cancel_favorite))) {
			unFavoriteMessage(msgInfo);
		} else if (result.equals(getString(R.string.delete))) {
			removeMessage(msgInfo);
		} else if (result.equals(getString(R.string.communication))) {
			msgHelper.communicatePeople(msgInfo);
		} else if (result.equals(getString(R.string.copy))) {
			ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Activity.CLIPBOARD_SERVICE);
			cmb.setText(msgInfo.content);
			showToast(R.string.copy_successfull);
		}
	}

	private ChatMsgDataHelper.Callback callback = new Callback() {

		@Override
		public void zanMessage(MessageInfo msg) {
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void unZanMessage(MessageInfo msg) {
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void unFavoriteMessage(MessageInfo msg) {
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void favoriteMessage(MessageInfo msg) {
			mAdapter.notifyDataSetChanged();
		}

		@Override
		public void commentMessage(MessageInfo msg) {
			mAdapter.notifyDataSetChanged();
		}
	};

	public void zanMessage(final MessageInfo messageInfo) {
		msgHelper.zanMessage(messageInfo, isAnony() ? 1 : 0);
	}

	public void favoriteMessage(final MessageInfo messageInfo) {
		msgHelper.favoriteMessage(messageInfo);
	}

	public void unFavoriteMessage(final MessageInfo messageInfo) {
		msgHelper.unFavoriteMessage(messageInfo);
	}

	@Override
	protected MessageInfo buildMessage() {
		MessageInfo msg = super.buildMessage();
		msg.title = mTribe.name;
		msg.to = mTribe.id;
		msg.parentid = "0";
		// if (mChatType == CHAT_TYPE_MEETING && mTribe.role > 0) {// 必须实名
		// msg.displayname = mLogin.realname;
		// msg.headImgUrl = mLogin.headsmall;
		// } else {
		if ((!isAnony()) || (!hasIdentity)) {
			Logger.d(this, "id=" + spoAnony.getInt(SPConst.getSingleSpId(mContext, mTribe.id), 0));
			msg.displayname = User.getUserName(mLogin);
			msg.headImgUrl = mLogin.headsmall;
		} else {
			msg.displayname = mIdentity.name;
			msg.headImgUrl = mIdentity.head;
			msg.heroid = mIdentity.id;
		}
		// }
		msg.type = mChatType;
		buildConversation(msg);
		return msg;
	}

	private boolean isAnony() {
		if (isOnLooker) {
			return true;
		}
		if (mChatType == CHAT_TYPE_MEETING && mTribe.role > 0) {
			return spoAnony.getInt(SPConst.getSingleSpId(mContext, mTribe.id), 0) == 1;
		} else {
			return spoAnony.getInt(SPConst.getSingleSpId(mContext, mTribe.id), 1) == 1;
		}
	}

	private void buildConversation(MessageInfo msg) {
		ConversationInnerBean bean = new ConversationInnerBean();
		bean.headurl = mTribe.logosmall;
		bean.toid = mTribe.id;
		bean.name = mTribe.name;
		bean.type = mChatType;
		msg.conversion = bean;
	}

	@Override
	protected void handleExtralSendResultConditon(SendMessageResult data, MessageInfo msg) {
		if (data.state.code == DamiCommon.IDENTITY_INVALID_CODE) {
			if (data.identity != null) {
				mIdentity = data.identity;
				updateIdentity(mIdentity);
				msg.displayname = mIdentity.name;
				msg.heroid = mIdentity.id;
				msg.headImgUrl = mIdentity.head;
				modifyMessageState(msg);
			}
		}
	}

	protected void getIdentity() {
		if ((mChatType == CHAT_TYPE_MEETING && (mTribe.role != 1)) || mChatType == CHAT_TYPE_TRIBE) {
			SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
			IdentityTable table = new IdentityTable(db);
			Identity identity = table.query(mTribe.id);
			if (identity == null || System.currentTimeMillis() - identity.updateTime > 24 * 60 * 60 * 1000
					|| TextUtils.isEmpty(identity.name)) {
				getIndetityByNet();
			} else {
				if (identity != null && TextUtils.isEmpty(identity.name)) {
					getIndetityByNet();
					return;
				}
				hasIdentity = true;
				mIdentity = identity;
			}
		}
	}

	/**
	 * pull up indicate to get messages with larger id, so we should pass
	 * sinceId to fetch data.
	 */
	@Override
	protected void getMessageList(boolean isPullUp) {
		super.getMessageList(isPullUp);
		String minID = getMessageMinId();
		String maxID = getMessageMaxId();
		Logger.d(this, "minId=" + minID + "  maxId=" + maxID);
		if (isPullUp) {
			DamiInfo.getMessageList(mChatType, mTribe.id, "", minID, new GetMessageListener(mContext, true));
		} else {
			DamiInfo.getMessageList(mChatType, mTribe.id, maxID, "", new GetMessageListener(mContext, false));
		}
	}

	protected void updateIdentity(Identity mIdentity) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
		IdentityTable table = new IdentityTable(db);
		Identity identity = table.query(mTribe.id);
		if (identity == null) {
			table.insert(mTribe.id, mIdentity);
		} else {
			table.update(mTribe.id, mIdentity);
		}
	}

	private boolean hasIdentity = false;

	/**
	 * @update
	 */
	private void getIndetityByNet() {
		DamiInfo.getIndetity(mTribe.id, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				IdentitityResult data = (IdentitityResult) o;
				if (data.state != null && data.state.code == 0) {
					if (data.data != null && !TextUtils.isEmpty(data.data.name)) {
						mIdentity = data.data;
						insertIdentity();
						hasIdentity = true;
						return;
					}
				}
				hasIdentity = false;
				showGetIdentityDialog();
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
				ChatTribeActivity.this.finish();
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

	@Override
	protected void notifyMessage(MessageInfo msg) {
		if (msg == null) {
			return;
		}
		if (msg.from.equals(DamiCommon.getUid(mContext))) {
			return;
		}
		if (msg.to.equals(mTribe.id)) {
			msg.isReadVoice = MessageState.VOICE_NOT_READED; // 语音未读
			updateMessage(msg);// 先存入数据库, 在downVoiceSuccess修改播放信息
			if (msg.parentid.equals("0")) {
				addNewMessage(msg);
			} else {
				MessageInfo messageInfo = getTargetMessageInfoById(msg.parentid);
				if (messageInfo != null) {
					messageInfo.commentCount++;
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	private void deleteConverstion() {
		// ConversationHelper.deleteItemAndUpadte(mContext, mTribe.id);
		ConversationHelper.deleteChatItemAndUpadte(mContext, mTribe.id, false);
	}

	@Override
	protected void onOtherChatBroadCastAction(Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		Logger.d(this, "action=" + action);
		if (action.equals(REFRESH_ADAPTER)) {
			SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
			MessageTable messageTable = new MessageTable(db);
			messageInfos = messageTable.query(mTribe.id, -1, mChatType);
			if (messageInfos == null) {
				messageInfos = new ArrayList<MessageInfo>();
			}
			mAdapter.notifyDataSetChanged();
		} else if (action.equals(ACTION_EXIT_TRIBE)) {
			String id = intent.getStringExtra("id");
			if (!TextUtils.isEmpty(id)) {
				if (id.equals(mTribe.id)) {
					if (mChatType == CHAT_TYPE_TRIBE) {
						destoryDialog(mContext.getString(R.string.you_have_exit_tribe));
					} else if (mChatType == CHAT_TYPE_MEETING) {
						destoryDialog(mContext.getString(R.string.you_have_exit_meeting));
					}
					deleteConverstion();
				}
			}

		} else if (action.equals(ACTION_KICK_TRIBE)) {
			String id = intent.getStringExtra("id");
			if (!TextUtils.isEmpty(id)) {
				if (id.equals(mTribe.id)) {
					if (mChatType == CHAT_TYPE_TRIBE) {
						destoryDialog(mContext.getString(R.string.you_have_been_removed_from_tribe));
					} else if (mChatType == CHAT_TYPE_MEETING) {
						destoryDialog(mContext.getString(R.string.you_have_been_removed_from_meeting));
					}
					deleteConverstion();
				}
			}

		} else if (ACTION_SHIED_MESSAGE.equals(action)) {
			MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("message");
			if (messageInfo == null) {
				return;
			}
			MessageInfo target = getTargetMessageInfo(messageInfo);
			if (target != null) {
				target.mIsShide = 1;
				mAdapter.notifyDataSetChanged();
			}
		} else if (ACTION_FAVORITE_MESSAGE.equals(action)) {
			MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("message");
			if (messageInfo != null) {
				updateFavoriteCount(messageInfo);
			}
		} else if (ACTION_UNFAVORITE_MESSAGE.equals(action)) {
			MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("message");
			if (messageInfo != null) {
				updateFavoriteCount(messageInfo);
			}
		} else if (ACTION_ZAN_MESSAGE.equals(action)) {
			NotifiyVo notifiyVo = (NotifiyVo) intent.getSerializableExtra("notifiyVo");
			if (notifiyVo == null) {
				return;
			}
			MessageInfo messageInfo = notifiyVo.message;
			if (messageInfo != null) {
				updateZanCountFromNotify(messageInfo, true);
			}
		} else if (ACTION_UNZAN_MESSAGE.equals(action)) {
			NotifiyVo notifiyVo = (NotifiyVo) intent.getSerializableExtra("notifiyVo");
			if (notifiyVo == null) {
				return;
			}
			MessageInfo messageInfo = notifiyVo.message;
			if (messageInfo != null) {
				updateZanCountFromNotify(messageInfo, false);
			}
		} else if (ACTION_COMMENT_OR_ZAN_OR_FAVOURITE.equals(action)) {
			MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("message");
			if (messageInfo != null) {
				updateMessgaeItem(messageInfo);
			}
		} else if (NotifyChatMessage.ACTION_CHANGE_VOICE_CONTENT.equals(action)) {
			final MessageInfo messageInfo = (MessageInfo) intent
					.getSerializableExtra(NotifyChatMessage.EXTRAS_NOTIFY_CHAT_MESSAGE);
			if (messageInfo.to.equals(String.valueOf(mTribe.id))) {
				if (messageInfo.parentid.equals("0")) {
					for (int i = 0; i < messageInfos.size(); i++) {
						if (messageInfo.tag.equals(messageInfos.get(i).tag)) {
							messageInfos.get(i).content = messageInfo.content;
							mAdapter.notifyDataSetChanged();
							break;
						}
					}
				} else {
					for (int i = 0; i < messageInfos.size(); i++) {
						if (messageInfo.parentid.equals(messageInfos.get(i).id)) {
							MessageInfo tempInfo = messageInfos.get(i);
							if (tempInfo.comment != null && tempInfo.comment.size() != 0) {
								for (int j = 0; j < tempInfo.comment.size(); j++) {
									if (tempInfo.comment.get(j).tag.equals(messageInfo.tag)) {
										tempInfo.comment.get(j).content = messageInfo.content;
										break;
									}
								}
								mAdapter.notifyDataSetChanged();
							}
							break;
						}
					}
				}
			}
		} else if (action.equals(ACTION_MESSAGE_DELETE)) {
			String tag = intent.getStringExtra("tag");
			for (int i = 0; i < messageInfos.size(); i++) {
				if (messageInfos.get(i).tag.equals(tag)) {
					messageInfos.remove(i);
					mAdapter.notifyDataSetChanged();
					break;
				}
			}
		} else if (action.equals(ACTION_CHANGE_VOICE)) {
			isChangeVoice = isAnony();
			setChangeVoiceView(isChangeVoice);
			// have inserted msg to db when sending broadcast, so just add it to
			// list
			addTipMessageToList(isChangeVoice);
		} else if (action.equals(ActionHolder.ACTION_QUIT_MEETING)) {
			String id = intent.getStringExtra("tid");
			if (id.equals(mTribe.id)) {
				this.finish();
			}
		} else if (action.equals(ActionHolder.ACTION_QUIT_TRIBE)) {
			String id = intent.getStringExtra("tid");
			if (id.equals(mTribe.id)) {
				this.finish();
			}
		} else if (action.equals(ActionHolder.ACTION_CANCEL_MEETING)) {
			String id = intent.getStringExtra("tid");
			if (id.equals(mTribe.id)) {
				this.finish();
			}
		} else if (action.equals(ActionHolder.ACTION_CANCEL_TRIBE)) {
			String id = intent.getStringExtra("tid");
			if (id.equals(mTribe.id)) {
				this.finish();
			}
		}
	}

	public static void insertTipMessageToDb(Context context, boolean isAnony, User user, Tribe tribe, int type) {
		MessageInfo messageInfo = buidTipMessage(user, tribe, type);
		if (isAnony) {
			messageInfo.fileType = MessageType.LOCAL_ANONY_TRUE;
		} else {
			messageInfo.fileType = MessageType.LOCAL_ANONY_FALSE;
		}
		SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.insert(messageInfo);
	}

	public static MessageInfo buidTipMessage(User user, Tribe tribe, int type) {
		MessageInfo msg = new MessageInfo();
		msg.from = user.uid;
		msg.tag = UUID.randomUUID().toString();
		msg.time = System.currentTimeMillis();
		msg.to = tribe.id;
		msg.parentid = "0";
		msg.type = type;
		return msg;
	}

	private void addAndInsertMessage(boolean isAnony) {
		MessageInfo msgInfo = addTipMessageToList(isAnony);
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.insert(msgInfo);
	}

	private MessageInfo addTipMessageToList(boolean isAnony) {
		MessageInfo messageInfo = buildMessage();
		if (isAnony) {
			messageInfo.fileType = MessageType.LOCAL_ANONY_TRUE;
		} else {
			messageInfo.fileType = MessageType.LOCAL_ANONY_FALSE;
		}
		messageInfos.add(messageInfo);
		mAdapter.notifyDataSetChanged();
		scrollToBottom();
		return messageInfo;
	}

	private void updateMessgaeItem(MessageInfo messageInfo2) {
		MessageInfo target = getTargetMessageInfo(messageInfo2);
		if (target != null) {
			target.favoriteCount = messageInfo2.favoriteCount;
			target.commentCount = messageInfo2.commentCount;
			target.agreeCount = messageInfo2.agreeCount;
			target.isAgree = messageInfo2.isAgree;
			target.isfavorite = messageInfo2.isfavorite;
			target.isReadVoice = messageInfo2.isReadVoice;
			mAdapter.notifyDataSetChanged();
		}
	}

	private void updateFavoriteCount(MessageInfo messageInfo) {
		MessageInfo target = getTargetMessageInfo(messageInfo);
		if (target != null) {
			target.favoriteCount = messageInfo.favoriteCount;
			mAdapter.notifyDataSetChanged();
		}
	}

	// count has been add one in SystemNotify, so need not update database
	private void updateZanCountFromNotify(MessageInfo messageInfo, boolean isAdd) {
		MessageInfo target = getTargetMessageInfo(messageInfo);
		if (target != null) {
			if (isAdd) {
				target.agreeCount = target.agreeCount + 1;
			} else {
				target.agreeCount = target.agreeCount - 1;
			}
			if (target.agreeCount < 0) {
				target.agreeCount = 0;
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	private void updateCommentCount(MessageInfo messageInfo) {
		MessageInfo target = getTargetMessageInfo(messageInfo);
		if (target != null) {
			target.commentCount = messageInfo.commentCount;
			mAdapter.notifyDataSetChanged();
		}
	}

	private MessageInfo getTargetMessageInfo(MessageInfo messageInfo) {
		if (messageInfo.parentid.equals("0")) {
			for (int i = 0; i < messageInfos.size(); i++) {
				if (messageInfo.tag.equals(messageInfos.get(i).tag)) {
					return messageInfos.get(i);
				}
			}
		}
		return null;
	}

	private MessageInfo getTargetMessageInfoById(String id) {
		for (int i = 0; i < messageInfos.size(); i++) {
			if (id.equals(messageInfos.get(i).id)) {
				return messageInfos.get(i);
			}
		}
		return null;
	}

	private void updateZanCountToDb(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.updateAgreeCount(messageInfo);
	}

	private void updateFavoriteCountToDb(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.updateFavoriteCount(messageInfo);
	}

	private void updateCommentCountToDb(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.updateCommentCount(messageInfo);
	}

	/** 从消息列表中移除消息，并发送更新数量通知 */
	private void removeMessage(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		MessageInfo message = table.query(messageInfo.tag);
		if (message != null) {
			table.delete(messageInfo);
			for (int i = 0; i < messageInfos.size(); i++) {
				if (messageInfos.get(i).tag.equals(messageInfo.tag)) {
					messageInfos.remove(i);
					mAdapter.notifyDataSetChanged();
					break;
				}
			}
			// mContext.sendBroadcast(new Intent(TribeTab.UPDATE_COUNT_ACTION));
		}
	}

	// 被轰出去了
	private void destoryDialog(String title) {
		AlertDialog builder = new AlertDialog.Builder(this).create();
		builder.setTitle(title);
		builder.setButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				ChatTribeActivity.this.finish();
			}
		});
		builder.setCancelable(false);
		builder.setCanceledOnTouchOutside(false);
		builder.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.ab_chat_more:
			if (mChatType == CHAT_TYPE_MEETING) {
				Intent intent = new Intent(this, MeetingDetailActivity.class);
				intent.putExtra(MeetingDetailActivity.KEY_MEETING_ID, mTribe.id);
				startActivity(intent);
			} else {
				Intent intent = new Intent(this, TribeDetailActivity.class);
				intent.putExtra(TribeDetailActivity.KEY_TRIBE_ID, mTribe.id);
				startActivityForResult(intent, 12);
			}
			break;

		}

		super.onClick(v);
	}

	@Override
	protected void onActivityResult(int request, int result, Intent arg2) {
		if (result == TribeDetailActivity.RESULT_CANCEL_TRIBE) {
			setResult(TribeDetailActivity.RESULT_CANCEL_TRIBE);
			ChatTribeActivity.this.finish();
		}
		super.onActivityResult(request, result, arg2);
	}
}
