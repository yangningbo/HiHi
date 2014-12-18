package com.gaopai.guiren.receiver;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.bean.ConversationBean;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.NotifyMessageBean.ConversationInnerBean;
import com.gaopai.guiren.bean.NotifyMessageBean.NotifyChatBean;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.db.ConverseationTable;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.db.MessageTable;
import com.gaopai.guiren.fragment.NotificationFragment;
import com.gaopai.guiren.service.XmppManager;
import com.gaopai.guiren.support.ConversationHelper;
import com.gaopai.guiren.utils.Logger;

/**
 * 
 * 功能： 接收到发送的消息.通过广播发送出去 <br />
 * 日期：2013-5-6<br />
 * 地点：西竹科技<br />
 * 版本：ver 1.0<br />
 * 
 * @since
 */
public class NotifyChatMessage implements NotifyMessage {
	private static final String TAG = "NotifyChatMessage";

	/**
	 * 聊天服务发来聊天信息, 广播包<br/>
	 * 附加参数: {@link NotifyChatMessage#EXTRAS_NOTIFY_CHAT_MESSAGE}
	 */
	public static final String ACTION_NOTIFY_CHAT_MESSAGE = "com.gaopai.guiren.sns.notify.ACTION_NOTIFY_CHAT_MESSAGE";
	/**
	 * 某消息列表有更新，注意查收 附加参数:
	 * {@link NotifyChatMessage#EXTRAS_NOTIFY_SESSION_MESSAGE}
	 */
	public static final String ACTION_NOTIFY_SESSION_MESSAGE = "com.gaopai.guiren.sns.notify.ACTION_NOTIFY_SESSION_MESSAGE";

	/**
	 * 更新语音转文字成功之后语音消息对应的文本信息通知
	 */
	public static final String ACTION_CHANGE_VOICE_CONTENT = "com.gaopai.guiren.intent.action.ACTION_CHANGE_VOICE_CONTENT";

	/**
	 * 附加信息<br/>
	 * {@link MessageInfo}
	 */
	public static final String EXTRAS_NOTIFY_CHAT_MESSAGE = "extras_message";
	/**
	 * 附加信息<br/>
	 * {@link SessionList}
	 */
	public static final String EXTRAS_NOTIFY_SESSION_MESSAGE = "extras_session";

	private ChatMessageNotifiy chatMessageNotifiy;
	public XmppManager xmppManager;
	public User userInfoVo;

	public NotifyChatMessage(XmppManager xmppManager) {
		super();
		this.xmppManager = xmppManager;
		this.userInfoVo = xmppManager.getSnsService().getUserInfoVo();
		chatMessageNotifiy = new ChatMessageNotifiy(xmppManager.getSnsService());
	}

	@Override
	public void notifyMessage(String msg) {
		try {

			if (msg.equals("This room is not anonymous.")) {
				return;
			}
			// Gson gson = new Gson();
			// MessageInfo info = gson.fromJson(msg, MessageInfo.class);
			// MessageInfo info = JSONObject.parseObject(msg,
			// MessageInfo.class);

			MessageInfo info = JSONObject.parseObject(msg, MessageInfo.class);
			// MessageInfo info = chatBean.messageInfo;

			Log.d(TAG, "save()");
			// ConversationHelper.saveToLastMsgList(info,
			// xmppManager.getSnsService());

			if (info.istranslate == 1) {
				SQLiteDatabase dbDatabase = DBHelper.getInstance(xmppManager.getSnsService()).getWritableDatabase();
				MessageTable table = new MessageTable(dbDatabase);
				table.updateVoiceContent(info.tag, info.content);

				Intent intent = new Intent(ACTION_CHANGE_VOICE_CONTENT);
				intent.putExtra(EXTRAS_NOTIFY_CHAT_MESSAGE, info);
				xmppManager.getSnsService().sendBroadcast(intent);
				return;
			}

			SQLiteDatabase dbDatabase = DBHelper.getInstance(xmppManager.getSnsService()).getReadableDatabase();
			MessageTable table = new MessageTable(dbDatabase);
			MessageInfo old = table.query(info.tag);
			if (old != null) {
				return;
			}

			if (info.type != 100 && info.from.equals(DamiCommon.getUid(xmppManager.getSnsService()))) {
				return;
			}
			info.sendState = 1;
			/*
			 * if(MessageType.MAP == info.type){
			 * info.setContent(json.getString("content")); }
			 */
			if (info != null) {
				saveMessageInfo(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveMessageInfo(MessageInfo info) {

		if (info.fileType == MessageType.VOICE) {
			info.sendState = 4;
		}

		SQLiteDatabase dbDatabase = DBHelper.getInstance(xmppManager.getSnsService()).getWritableDatabase();
		MessageTable table = new MessageTable(dbDatabase);
		table.insert(info);

		if (!info.parentid.equals("0")) {
			MessageInfo message = table.queryByID(info.parentid);
			if (message != null) {
				message.commentCount++;
				table.update(message);
			}
		}
		sendBroad(info);

	}

	private void sendBroad(MessageInfo info) {
		Log.d(TAG, "sendBroad()");

		/*
		 * Intent refreshIntent = new Intent(ChatsTab.ACTION_REFRESH_SESSION);
		 * refreshIntent.putExtra("message", info);
		 * xmppManager.getSnsService().sendBroadcast(refreshIntent);
		 */
		Intent intent = new Intent(ACTION_NOTIFY_CHAT_MESSAGE);
		intent.putExtra(EXTRAS_NOTIFY_CHAT_MESSAGE, info);
		// intent.putExtra(EXTRAS_NOTIFY_SESSION_MESSAGE, sessionList);
		chatMessageNotifiy.notifiy(info);
		xmppManager.getSnsService().sendBroadcast(new Intent(NotificationFragment.ACTION_MSG_NOTIFY));
		if (xmppManager != null && xmppManager.getSnsService() != null) {
			xmppManager.getSnsService().sendBroadcast(intent);
		}
	}
}
