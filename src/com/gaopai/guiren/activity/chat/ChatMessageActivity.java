package com.gaopai.guiren.activity.chat;

import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.DamiInfo;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.PrivacyChatSettingActivity;
import com.gaopai.guiren.adapter.PrivateChatAdapter;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.NotifyMessageBean.ConversationInnerBean;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.support.ConversationHelper;
import com.gaopai.guiren.support.NotifyHelper;
import com.gaopai.guiren.utils.Logger;
import com.gaopai.guiren.utils.SPConst;

//私信界面
public class ChatMessageActivity extends ChatMainActivity implements OnClickListener {
	private User user;
	public static final String KEY_USER = "user";
	private MessageInfo messageInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		user = (User) getIntent().getSerializableExtra(KEY_USER);
		messageInfo = (MessageInfo) getIntent().getSerializableExtra(KEY_MESSAGE);
		super.onCreate(savedInstanceState);

		mAdapter = new PrivateChatAdapter(mContext, speexPlayerWrapper, messageInfos);
		super.initAdapter(mAdapter);
//		mTitleBar.setTitleText(user.realname);

		ivDisturb.setImageLevel(spo.getInt(SPConst.getTribeUserId(mContext, user.uid), 0));

		if (messageInfo != null) {
			buildRetweetMessageInfo(messageInfo);
			addSaveSendMessage(messageInfo);
		}
	}
	
	public static Intent getIntent(Context context, String uid, String name, String headsmall) {
		User user = new User();
		user.uid = uid;
		user.realname = name;
		user.headsmall = headsmall;
		Intent intent = new Intent(context, ChatMessageActivity.class);
		intent.putExtra(ChatMessageActivity.KEY_USER, user);
		return intent;
	}
	
	public static Intent getIntent(Context context, User user) {
		Intent intent = new Intent(context, ChatMessageActivity.class);
		intent.putExtra(ChatMessageActivity.KEY_USER, user);
		return intent;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Logger.d(this, "onResume to referesh");
		checkHasDraft(user.uid);
		NotifyHelper.setCurrentChatId(mContext, user.uid);
		NotifyHelper.clearMsgNotification(mContext, 100);
		ConversationHelper.resetCountAndRefresh(mContext, user.uid);
	}
	


	@Override
	protected void setTitleText() {
		// TODO Auto-generated method stub
		TextView textView = mTitleBar.addLeftTextView(user.realname);
	}

	@Override
	protected void getMessageListLocal(boolean isFirstTime) {
		// TODO Auto-generated method stub
		if (isFirstTime) {
			initMessage(user.uid, 100);
		} else {
			loadMessage(user.uid, 100);
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
		buildConversation(messageInfo);
	}

	@Override
	protected void getMessageList(boolean isRefresh) {
		super.getMessageList(isRefresh);
		String maxID = getMessageMaxId();
		DamiInfo.getPrivateMessageList(user.uid, maxID, "", new GetMessageListener(mContext, false));
	}

	@Override
	protected MessageInfo buildMessage() {
		MessageInfo msg = super.buildMessage();
		msg.title = "";
		msg.to = user.uid;
		msg.parentid = "0";
		msg.displayname = User.getUserName(mLogin);
		msg.headImgUrl = mLogin.headsmall;
		msg.type = 100;
		buildConversation(msg);
		return msg;
	}

	private void buildConversation(MessageInfo msg) {
		ConversationInnerBean bean = new ConversationInnerBean();
		bean.headurl = user.headsmall;
		bean.toid = user.uid;
		bean.name = user.realname;
		bean.type = 100;
		if (msg.fileType == MessageType.VOICE) {
			bean.localtype = 1;
		} else {
			bean.localtype = 0;
		}
		msg.conversion = bean;
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ab_chat_more:
			Intent intent = new Intent(this, PrivacyChatSettingActivity.class);
			intent.putExtra(PrivacyChatSettingActivity.KEY_UID, user.uid);
			startActivity(intent);
			break;

		}

		super.onClick(v);
	}


	@Override
	protected boolean isAvoidDisturb() {
		return spo.getInt(SPConst.getTribeUserId(mContext, user.uid), 0) == 1;
	}
}
