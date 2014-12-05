package com.gaopai.guiren.activity.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.AddReasonActivity;
import com.gaopai.guiren.activity.ChatCommentsActivity;
import com.gaopai.guiren.activity.MeetingDetailActivity;
import com.gaopai.guiren.activity.TribeDetailActivity;
import com.gaopai.guiren.activity.share.ShareActivity;
import com.gaopai.guiren.adapter.TribeChatAdapter;
import com.gaopai.guiren.bean.ConversationBean;
import com.gaopai.guiren.bean.Identity;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageState;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.NotifyMessageBean.ConversationInnerBean;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.TribeInfoBean;
import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.bean.net.IdentitityResult;
import com.gaopai.guiren.bean.net.SendMessageResult;
import com.gaopai.guiren.db.ConverseationTable;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.IdentityTable;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.receiver.NotifyChatMessage;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.SPConst;
import com.gaopai.guiren.volley.SimpleResponseListener;

public class ChatTribeActivity extends ChatMainActivity implements OnClickListener {
	public static final int CHAT_TYPE_MEETING = 300;
	public static final int CHAT_TYPE_TRIBE = 200;

	public static final String KEY_CHAT_TYPE = "chat_type";
	public static final String KEY_TRIBE = "tribe";
	public static final String KEY_TRIBE_ID = "tribe_id";
	public static final String KEY_IS_ONLOOKER = "onlooker";

	private int mSceneType = 0;
	public final static int IS_SCENE_ONLOOK = 1;

	private String mTribeId;

	protected int mChatType = 0;
	protected Tribe mTribe;
	protected Identity mIdentity;

	private MessageInfo messageInfo;
	private boolean isOnLooker = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mChatType = getIntent().getIntExtra(KEY_CHAT_TYPE, CHAT_TYPE_MEETING);
		mTribe = (Tribe) getIntent().getSerializableExtra(KEY_TRIBE);// before
		isOnLooker = getIntent().getBooleanExtra(KEY_IS_ONLOOKER, false);
		super.onCreate(savedInstanceState);
		// mTribeId = getIntent().getStringExtra(KEY_TRIBE_ID)
		updateTribe();
		getIdentity();
		initTribeComponent();
	}
	
	private void hideOnLookerView() {
		hideChatBox();
	}

	protected void initTribeComponent() {
		if (isOnLooker) {
			hideOnLookerView();
		}
		mAdapter = new TribeChatAdapter(mContext, speexPlayerWrapper, messageInfos);
		super.initAdapter(mAdapter);
		ivDisturb.setImageLevel(spo.getInt(SPConst.getTribeUserId(mContext, mTribe.id), 0));
		mListView.getRefreshableView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				showItemLongClickDialog(messageInfos.get(position));
				return false;
			}
		});
		mListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ChatTribeActivity.this, ChatCommentsActivity.class);
				intent.putExtra(ChatCommentsActivity.INTENT_CHATTYPE_KEY, mChatType);
				intent.putExtra(ChatCommentsActivity.INTENT_TRIBE_KEY, mTribe);
				intent.putExtra(ChatCommentsActivity.INTENT_MESSAGE_KEY, messageInfos.get(position));
				intent.putExtra(ChatCommentsActivity.INTENT_IDENTITY_KEY, mIdentity);
				startActivity(intent);
			}
		});

		messageInfo = (MessageInfo) getIntent().getSerializableExtra(KEY_MESSAGE);
		if (messageInfo != null) {
			buildRetweetMessageInfo(messageInfo);
			addSaveSendMessage(messageInfo);
		}
	}

	public static Intent getIntent(Context context, Tribe tribe, int type) {
		Intent intent = new Intent(context, ChatTribeActivity.class);
		intent.putExtra(KEY_TRIBE, tribe);
		intent.putExtra(KEY_CHAT_TYPE, type);
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
		// TODO Auto-generated method stub
		Logger.d(this, "tribe id=" + mTribe.id);
		if (isFirstTime) {
			initMessage(mTribe.id, mChatType);
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
			// goToRetrweet(msgInfo);
			spreadToDy(msgInfo);
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

	private void spreadToDy(MessageInfo messageInfo) {
		DamiInfo.spreadDynamic(2, messageInfo.id, "", "", "", "", new SimpleResponseListener(mContext) {

			@Override
			public void onSuccess(Object o) {
				// TODO Auto-generated method stub
				BaseNetBean data = (BaseNetBean) o;
				if (data.state != null && data.state.code == 0) {
					showToast(R.string.spread_success);
				} else {
					otherCondition(data.state, ChatTribeActivity.this);
				}
			}
		});
	}

	private void goToRetrweet(MessageInfo msgInfo) {
		Intent intent = new Intent(mContext, ShareActivity.class);
		intent.putExtra(ShareActivity.KEY_TYPE, ShareActivity.TYPE_SHARE);
		intent.putExtra(ShareActivity.KEY_MESSAGE, msgInfo);
		startActivity(intent);
	}

	private void communicatePeople(MessageInfo messageInfo) {
		Intent intent = new Intent(mContext, AddReasonActivity.class);
		intent.putExtra(AddReasonActivity.KEY_MESSAGEINFO, messageInfo);
		intent.putExtra(AddReasonActivity.KEY_APLLY_TYPE, AddReasonActivity.TYPE_WAHT_COMUNICATION);
		mContext.startActivity(intent);
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
						updateZanForLocal(messageInfo);
					} else if (data.state.msg.equals("取消赞成功")) {
						Toast.makeText(mContext, "您已取消赞同", Toast.LENGTH_SHORT).show();
						messageInfo.isAgree = 0;
						messageInfo.agreeCount--;
						updateZanForLocal(messageInfo);
					}
				} else {
					otherCondition(data.state, ChatTribeActivity.this);
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
			}
		};
		if (mChatType == CHAT_TYPE_TRIBE) {
			DamiInfo.favoriteMessage(mTribe.id, messageInfo.id, listener);
		} else if (mChatType == CHAT_TYPE_MEETING) {
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
		if (mChatType == CHAT_TYPE_TRIBE) {
			DamiInfo.reportMessage(mTribe.id, messageInfo.id, content, listener);
		} else if (mChatType == CHAT_TYPE_MEETING) {
			DamiInfo.reportMeetingMessage(mTribe.id, messageInfo.id, content, listener);
		}
	}

	@Override
	protected MessageInfo buildMessage() {
		MessageInfo msg = super.buildMessage();
		msg.title = mTribe.name;
		msg.to = mTribe.id;
		msg.parentid = "0";
		if (mChatType == CHAT_TYPE_MEETING && mTribe.role != 0) {
			msg.displayname = mLogin.displayName;
			msg.headImgUrl = mLogin.headsmall;
		} else {
			msg.displayname = mIdentity.name;
			msg.headImgUrl = mIdentity.head;
			msg.heroid = mIdentity.id;
		}
		msg.type = mChatType;
		buildConversation(msg);
		return msg;
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
	protected void handleExtralSendSuccessConditon(SendMessageResult data, MessageInfo msg) {
		if (data.state.code == DamiCommon.IDENTITY_INVALID_CODE) {
			mIdentity = data.identity;
			updateIdentity(mIdentity);
			msg.displayname = mIdentity.name;
			msg.heroid = mIdentity.id;
			msg.headImgUrl = mIdentity.head;
			modifyMessageState(msg);
		}
	}

	protected void getIdentity() {
		if (!(mChatType == CHAT_TYPE_MEETING && mTribe.role != 0)) {
			SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
			IdentityTable table = new IdentityTable(db);
			Identity identity = table.query(mTribe.id);
			if (identity == null || System.currentTimeMillis() - identity.updateTime > 24 * 60 * 60 * 1000) {
				getIndetityByNet();
			} else {
				mIdentity = identity;
			}
		}
	}

	@Override
	protected void getMessageList(boolean isRefresh) {
		super.getMessageList(isRefresh);
		String maxID = getMessageMaxId();
		DamiInfo.getMessageList(mChatType, mTribe.id, maxID, "", getMessageListListener);
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

	private void getIndetityByNet() {
		DamiInfo.getIndetity(mTribe.id, new SimpleResponseListener(mContext) {
			@Override
			public void onSuccess(Object o) {
				IdentitityResult data = (IdentitityResult) o;
				if (data.state != null && data.state.code == 0) {
					mIdentity = data.data;
				}
			}
		});
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
			}
		}
	}

	@Override
	protected void onOtherChatBroadCastAction(Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
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
				}
			}

		} else if (ACTION_SHIED_MESSAGE.equals(action)) {
			String tag = intent.getStringExtra("tag");
			if (!TextUtils.isEmpty(tag)) {
				for (int i = 0; i < messageInfos.size(); i++) {
					if (messageInfos.get(i).tag.equals(tag)) {
						messageInfos.get(i).mIsShide = 1;
						if (mAdapter != null) {
							mAdapter.notifyDataSetChanged();
						}
						break;
					}
				}
			}

		} else if (ACTION_FAVORITE_MESSAGE.equals(action)) {
			MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("message");
			if (messageInfo != null) {
				updateFavoriteCount(messageInfo, true);
			}
		} else if (ACTION_UNFAVORITE_MESSAGE.equals(action)) {
			MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("message");
			if (messageInfo != null) {
				updateFavoriteCount(messageInfo, true);
			}
		} else if (ACTION_ZAN_MESSAGE.equals(action)) {
			Log.e("CHEN", "notitySystemMessage4");
			MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("message");
			if (messageInfo != null) {
				updateZanCount(messageInfo, true);
			}
		} else if (ACTION_UNZAN_MESSAGE.equals(action)) {
			MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("message");
			if (messageInfo != null) {
				updateZanCount(messageInfo, true);
			}
		} else if (ACTION_COMMENT_OR_ZAN_OR_FAVOURITE.equals(action)) {
			MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("message");
			updateMessgaeItem(messageInfo);
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
		}

	}

	private void updateFavoriteCount(MessageInfo messageInfo, boolean isFavorite) {
		if (messageInfo.parentid.equals("0")) {
			for (int i = 0; i < messageInfos.size(); i++) {
				if (messageInfo.tag.equals(messageInfos.get(i).tag)) {
					if (isFavorite) {
						messageInfos.get(i).favoriteCount = messageInfo.favoriteCount;
					} else {
						messageInfos.get(i).favoriteCount++;
						messageInfos.get(i).isfavorite = 1;
						updateMessage(messageInfo);
					}
					mAdapter.notifyDataSetChanged();
					break;
				}
			}
		}
	}

	private void updateZanCount(MessageInfo messageInfo, boolean isAgree) {
		if (messageInfo.parentid.equals("0")) {
			for (int i = 0; i < messageInfos.size(); i++) {
				if (messageInfo.tag.equals(messageInfos.get(i).tag)) {
					if (isAgree) {
						messageInfos.get(i).agreeCount = messageInfo.agreeCount;
					} else {
						messageInfos.get(i).agreeCount++;
						updateMessage(messageInfo);
					}
					mAdapter.notifyDataSetChanged();
					break;
				}
			}
		}
	}

	private void updateZanForLocal(MessageInfo messageInfo) {
		if (messageInfo.parentid.equals("0")) {
			for (int i = 0; i < messageInfos.size(); i++) {
				if (messageInfo.tag.equals(messageInfos.get(i).tag)) {
					messageInfos.get(i).agreeCount = messageInfo.agreeCount;
					messageInfos.get(i).isAgree = messageInfo.isAgree;
					updateZanCount(messageInfo);
					mAdapter.notifyDataSetChanged();
					break;
				}
			}
		}
	}

	private void updateZanCount(MessageInfo messageInfo) {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.updateAgreeCount(messageInfo);
	}

	private void updateMessgaeItem(MessageInfo messageInfo) {
		if (messageInfo.parentid.equals("0")) {
			for (int i = 0; i < messageInfos.size(); i++) {
				if (messageInfo.tag.equals(messageInfos.get(i).tag)) {
					messageInfos.get(i).agreeCount = messageInfo.agreeCount;
					messageInfos.get(i).commentCount = messageInfo.commentCount;
					messageInfos.get(i).isAgree = messageInfo.isAgree;
					messageInfos.get(i).isfavorite = messageInfo.isfavorite;
					messageInfos.get(i).favoriteCount = messageInfo.favoriteCount;
					mAdapter.notifyDataSetChanged();
					break;
				}
			}
		}
	}

	private void updateCommentCount(MessageInfo messageInfo) {
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

	// public void updateVoicePlayModeState(boolean isModeInCall) {
	// if (isModeInCall) {
	// mVoiceModeImage.setImageResource(R.drawable.icon_chat_title_avoid_disturb_on);
	// } else {
	// mVoiceModeImage.setImageResource(R.drawable.icon_chat_title_avoid_disturb_off);
	// }
	// }

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
				startActivity(intent);
			}

			break;

		}

		super.onClick(v);
	}

}
