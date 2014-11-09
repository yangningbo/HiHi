package com.gaopai.guiren.bean;

import java.io.Serializable;

public class NotifyMessageBean {
	// 推送消息时，增加一个conversion
	// 对象，对象中字段有：type(类型：圈子消息、会议消息、私聊消息、系统通知、大蜜汇报),toid(和type保持一致
	// ，圈子id，会议id，私聊对方id，系统通知id（默认为-1），大蜜汇报id（默认为-2）)，name（聊天室名称），headurl（聊天室icon图片地址）

	public static class NotifyChatBean {
		public MessageInfo messageInfo;
		public ConversationInnerBean conversation;
	}

	// notitySystemMessage()：{"user":{"uid":0,"name":"admin","url":""},"content":"asdf","type":1,"room":{},"time":1415149238682,
	//"conversion":{"type":-1,"toid":-1,"name":"\u7cfb\u7edf\u901a\u77e5","headurl":"http:\/\/192.168.1.239:8081\/Public\/img\/icon7.png"}}

	public static class ConversationInnerBean implements Serializable{
		public int type;
		public String toid;
		public String name;
		public String headurl;
	}

}
