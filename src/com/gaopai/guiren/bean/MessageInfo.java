package com.gaopai.guiren.bean;

import java.util.List;

import com.gaopai.guiren.bean.NotifyMessageBean.ConversationInnerBean;
import com.google.gson.annotations.Expose;

public class MessageInfo extends SNSMessage {
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
	public String content = ""; // 内容()
	@Expose
	public String imgUrlS = ""; // 小图URL
	@Expose
	public String imgUrlL = ""; // 大图URL
	@Expose
	public int voiceTime = 0; // 录音的时间
	@Expose
	public int imgWidth; // 小图宽度
	@Expose
	public int imgHeight; // 小图高度
	@Expose
	public String voiceUrl = ""; // 音频URL
	@Expose
	public String displayname = ""; // 显示名字
	@Expose
	public String commentername = ""; // 被评论者的名字
	@Expose
	public String commenterid = ""; // 被评论者的id
	@Expose
	public String fromrole = "";
	@Expose
	public String commenterrole = "";
	@Expose
	public String headImgUrl = ""; // 显示头像
	@Expose
	public int sendState = 0; // 消息发送成功与否的状态 1 成功, 2 正在发送， 4， 正在下载。0 失败 5失败
	@Expose
	public int readState = 0; // 读取消息的状态. 1--已读 0--未读
	@Expose
	public long time; // 对方发送的时间
	@Expose
	public String parentid = "0";
	@Expose
	public int isReadVoice = 0;
	@Expose
	public int auto_id;
	@Expose
	public String title = ""; // 部落会议显示标题
	@Expose
	public String heroid = ""; // 临时身份ID
	@Expose
	public List<MessageInfo> comment;
	@Expose
	public User mUser;
	@Expose
	public int favoriteCount = 0; // 消息收藏数
	@Expose
	public int commentCount = 0; // 消息评论数
	@Expose
	public int agreeCount = 0; // 消息赞输
	@Expose
	public int mIsShide = 0; // 消息是否被屏蔽
	@Expose
	public int isfavorite = 0;
	@Expose
	public int isAgree = 0;
	@Expose	
	public String url = ""; // 分享URL;
	@Expose
	public int istranslate = 0; // 是否转文字
	@Expose
	public int samplerate = 8000; // 播放音频采样率
	@Expose
	public String uid;
	@Expose
	public String role;
	
	public int isanonymity;//0实名  1匿名
	public int reisanonymity;//0实名  1匿名
	

	public ConversationInnerBean conversion; 
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + tag.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageInfo other = (MessageInfo) obj;
		if (tag != other.tag)
			return false;
		return true;
	}

}
