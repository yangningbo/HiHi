package com.gaopai.guiren.service;

import xmpp.push.sns.Chat;
import xmpp.push.sns.ChatManagerListener;
import xmpp.push.sns.MessageListener;
import xmpp.push.sns.XMPPException;
import xmpp.push.sns.packet.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.receiver.NotifyChatMessage;
import com.gaopai.guiren.receiver.NotifyMessage;
import com.gaopai.guiren.receiver.NotifySystemMessage;
import com.gaopai.guiren.receiver.PushChatMessage;
import com.gaopai.guiren.receiver.PushMessage;

/**
 * 
 * 功能：聊天监听.监听服务端信息(聊天信息，系统消息等...) <br />
 * 日期：2013-5-5<br />
 * 地点：西竹科技<br />
 * 版本：ver 1.0<br />
 * 
 * @since
 */
public class SNSMessageManager implements ChatManagerListener {
	private static final String SYSTEM_USER = "beautyas";

	private XmppManager xmppManager;
	private MessageListener chatListener;

	// private LruMemoryCache<String, Chat> chatCache = new
	// LruMemoryCache<Strin  m 
	private NotifyChatMessage chatMessage;
	private NotifySystemMessage systemMessage;

	private PushChatMessage pushChatMessage;

	public SNSMessageManager(XmppManager xmppManager) {
		super();
		this.xmppManager = xmppManager;
		chatListener = new ChatListenerImpl();
		chatMessage = new NotifyChatMessage(xmppManager);
		systemMessage = new NotifySystemMessage(xmppManager);
		pushChatMessage = new PushChatMessage(xmppManager);
	}

	@Override
	public void chatCreated(Chat chat, boolean createdLocally) {
		if (!createdLocally) {
			chat.addMessageListener(chatListener);
		}
		// chatCache.put(chat.getParticipant().split("@")[0], chat);
	}

	/**
	 * 创建一个会话.
	 * 
	 * @param chatID
	 * @return 没有连接状态时,返回空. 作者:fighter <br />
	 *         创建时间:2013-5-5<br />
	 *         修改时间:<br />
	 */
	public Chat createChat(String chatID) {
		Chat chat = null;
		// chatCache.get(chatID);
		// if(chat == null){
		try {
			chat = xmppManager
					.getConnection()
					.getChatManager()
					.createChat(
							chatID
									+ "@"
									+ xmppManager.getConnection()
											.getServiceName(), chatListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// }

		// if(chat != null){
		// chatCache.put(chatID, chat);
		// }

		return chat;
	}

	/**
	 * 给指定的某人发送消息
	 * 
	 * @param uid
	 * @param info
	 *            作者:fighter <br />
	 *            创建时间:2013-3-16<br />
	 *            修改时间:<br />
	 */
	public boolean sendMessage(MessageInfo info, String group) {
		boolean flag = false;
		Chat chat = createChat(info.to);

		if (chat != null) {
			try {
				JSONObject json = (JSONObject) JSON.toJSON(info);
				json.remove("sendState");
				json.remove("readState");
				json.remove("sessionId");
				json.remove("pullTime");
				if (MessageType.MAP == info.type) {
					json.put("content", JSON.parseObject(info.content));
				}

				chat.sendMessage(json.toJSONString());
				flag = true;
			} catch (XMPPException e) {
				e.printStackTrace();
				flag = false;
			} catch (IllegalStateException e) {
				// 没连接上服务器
				e.printStackTrace();
				flag = false;
				xmppManager.startReconnectionThread();
			}
		}
		info.sendState = (flag ? 1 : 0);
		return flag;
	}

	/**
	 * 发送聊天信息
	 * 
	 * @param pushMessage
	 * @param messageInfo
	 *            作者:fighter <br />
	 *            创建时间:2013-5-6<br />
	 *            修改时间:<br />
	 */
	public void pushMessage(PushMessage pushMessage, MessageInfo msg,
			String group) {
		pushMessage.pushMessage(msg, group);
	}

	/**
	 * 接收到消息,通过广播发送发送.
	 * 
	 * @param notifyMessage
	 * @param content
	 *            作者:fighter <br />
	 *            创建时间:2013-5-6<br />
	 *            修改时间:<br />
	 */
	public void notityMessage(NotifyMessage notifyMessage, String msg) {
		notifyMessage.notifyMessage(msg);
	}

	public NotifySystemMessage getSystemMessage() {
		return systemMessage;
	}

	public PushChatMessage getPushChatMessage() {
		return pushChatMessage;
	}

	public NotifyChatMessage getNotifyChatMessage() {
		return chatMessage;
	}

	/**
	 * 
	 * 功能：聊天对象的单对单对话监听<br />
	 * 日期：2013-5-5<br />
	 * 地点：西竹科技<br />
	 * 版本：ver 1.0<br />
	 * 
	 * @since
	 */
	class ChatListenerImpl implements MessageListener {

		@Override
		public void processMessage(Chat chat, Message message) {
			// jid 为 chatId@domin/chat组成
			Log.e("是否有接收到消息？", message.getBody());
			String chatId = chat.getParticipant().split("@")[0]; // 发来消息的用户
			String content = message.getBody(); // 发送来的内容.
			if (SYSTEM_USER.equals(chatId)) {
				// Log.e("content", content);
				notityMessage(systemMessage, content);
			} else {
				if (!TextUtils.isEmpty(content) && content.startsWith("{")) {
				}
				notityMessage(chatMessage, content);
			}
		}

	}

}
