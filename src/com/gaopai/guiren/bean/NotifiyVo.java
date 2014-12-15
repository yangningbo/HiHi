package com.gaopai.guiren.bean;

import com.gaopai.guiren.bean.NotifyMessageBean.ConversationInnerBean;
import com.google.gson.annotations.Expose;

/**
 * 
 * 功能： 系统通知信息 <br />
 * 客户端收到的通知格式 {"type":"1","content":"0","sent":"{xxxx}"} type 信息类型：
 * 1-系统消息，2-好友申请，3-申请查看资料，4-被评论,5-查看资料,6-删除好友 content 消息内容：针对系统消息 其余5种类型没有值 sent
 * 发送人 用户信息. 日期：2013-5-29<br />
 * 地点：西竹科技<br />
 * 版本：ver 1.0<br />
 * 
 * @since
 */
public class NotifiyVo extends SNSMessage {

	/** 拒绝 */
	public static final int STATE_REFUSED = 2;
	/** 同意 */
	public static final int STATE_ADDED = 1;
	/** 消息为完成的状态 */
	public static final int STATE_NO_FINISH = 0;
	public static final int STATE_FINISH = 5;
	private static final long serialVersionUID = -5731925495114017054L;
	public int processed = STATE_NO_FINISH;
	public int mReadState = 0;
	public String mID;
	
	public NotifiyVo() {
		intial();
	}
	
	@Expose
	public int type;
	@Expose
	public String content;
	@Expose
	public String sent;
	@Expose
	public long time;
	@Expose
	public String code = "";
	@Expose
	public String phone = "";
	@Expose
	public String msgcontent = "";
	@Expose
	public Tribe room;
	@Expose
	public User user;
	@Expose
	public MessageInfo message;
	@Expose
	public Identity roomuser;
	public ConversationInnerBean conversion;
	
	//not from json
	public String roomid;
	
	public void intial() {
		message = new MessageInfo();
		user = new User();
		roomuser = new Identity();
		room = new Tribe();
	}
}
