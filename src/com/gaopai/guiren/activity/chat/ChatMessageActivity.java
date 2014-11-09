package com.gaopai.guiren.activity.chat;

import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;

import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.adapter.PrivateChatAdapter;
import com.gaopai.guiren.adapter.TribeChatAdapter;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.User;

//私信界面
public class ChatMessageActivity extends ChatMainActivity {
	private User user;
	public static final String KEY_USER= "user";
	private MessageInfo messageInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		user = (User) getIntent().getSerializableExtra(KEY_USER);
		messageInfo = (MessageInfo) getIntent().getSerializableExtra(KEY_MESSAGE);
		super.onCreate(savedInstanceState);

		mAdapter = new PrivateChatAdapter(mContext, speexPlayerWrapper, messageInfos);
		super.initAdapter(mAdapter);
		mTitleBar.setTitleText(user.displayName);
		

		if (messageInfo != null) {
			buildRetweetMessageInfo(messageInfo);
			addSaveSendMessage(messageInfo);
		}
	}

	protected void buildRetweetMessageInfo(MessageInfo messageInfo) {
		messageInfo.from = mLogin.uid;
		messageInfo.type = 100;
		messageInfo.displayname = mLogin.displayName;
		messageInfo.headImgUrl = mLogin.headsmall;
		messageInfo.heroid = "";
		messageInfo.tag = UUID.randomUUID().toString();
		messageInfo.title = "";
		messageInfo.time = System.currentTimeMillis();
		messageInfo.readState = 1;
		messageInfo.to = user.uid;
	}

	@Override
	protected void getMessageList(boolean isRefresh) {
		super.getMessageList(isRefresh);
		String maxID = getMessageMaxId();
		DamiInfo.getPrivateMessageList(user.uid, maxID, "", getMessageListListener);
	}

	@Override
	protected MessageInfo buildMessage() {
		MessageInfo msg = super.buildMessage();
		msg.title = "";
		msg.to = user.uid;
		msg.parentid = "0";
		msg.displayname = mLogin.displayName;
		msg.headImgUrl = mLogin.headsmall;
		msg.type = 100;
		return msg;
	}

	@Override
	protected void notifyMessage(MessageInfo msg) {
		// TODO Auto-generated method stub
		if (msg == null) {
			return;
		}
		try {
			if (msg.from.equals(DamiCommon.getUid(mContext))) {
				return;
			}
			addNewMessage(msg);
		} catch (Exception e) {

		}
	}

	@Override
	protected void onOtherChatBroadCastAction(Intent intent) {
		// TODO Auto-generated method stub

	}
}
