package com.gaopai.guiren.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;

public class TestMessageInfo implements Serializable {
	private static final long serialVersionUID = -4274108350647182194L;
	@Expose
	public String id = ""; // 消息ID
	@Expose
	public String tag = ""; // 消息标识符
	@Expose
	public int type; // 单聊100/部落200/会议300(客户端给定) 通知若干(服务器给定)
	@Expose
	public String to = ""; // 本消息发送给谁
	@Expose
	public String from = ""; // 本消息来自 谁
	@Expose
	public int fileType; // 该消息的类型为什么.
	@Expose
	public int imgWidth; // 小图宽度

}
