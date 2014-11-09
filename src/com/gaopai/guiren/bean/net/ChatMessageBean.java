package com.gaopai.guiren.bean.net;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import com.gaopai.guiren.activity.chat.ChatBaseActivity;
import com.gaopai.guiren.activity.chat.ChatTribeActivity;
import com.gaopai.guiren.bean.AppState;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.PageInfo;
import com.gaopai.guiren.bean.TestMessageInfo;
import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class ChatMessageBean implements Serializable, GsonObj{
	
	@Expose
	public List<MessageInfo> data;
	@Expose
	public AppState state;
	@Expose
	public PageInfo pageInfo;

	int obj;

	@Override
	public String getInterface() {
		// TODO Auto-generated method stub
		if(obj == ChatTribeActivity.CHAT_TYPE_TRIBE) {
			return "tribe/messageList";
		} else if(obj ==  ChatTribeActivity.CHAT_TYPE_MEETING) {
			return "meeting/messageList";
		}
		return "meeting/messageList";
	}

	@Override
	public Type getTypeToken() {
		// TODO Auto-generated method stub
		return new TypeToken<ChatMessageBean>() {
		}.getType();
	}

}
