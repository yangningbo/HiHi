package com.gaopai.guiren.bean;

/**
 * 
 * 功能： 系统通知信息 <br />
 * 客户端收到的通知格式 {"type":"1","content":"0","sent":"{xxxx}"} type 信息类型：
 * 1-系统消息，2-好友申请，3-申请查看资料，4-被评论,5-查看资料,6-删除好友 
 * 	content 消息内容：针对系统消息
	其余5种类型没有值
 *  sent
 * 发送人 用户信息. 
 * 日期：2013-5-29<br />
 * 地点：西竹科技<br />
 * 版本：ver 1.0<br />
 * 
 * @since
 */
public class NotifiyType {
	
	/** 系统消息 */
	public static final int SYSTEM_MSG = 1;
	
	/** 实名认证通过*/
	public static final int REAL_VERIFY_PASS = 11;
	/** 实名认证未通过*/
	public static final int REFUSE_REAL_VERIFY = 12;
	/** 贵人码通过审核*/
	public static final int PASS_INVITE_CODE = 13;
	/** 贵人码未通过审核*/
	public static final int REFUSE_INVITE_CODE = 14;
	/** 积分控制通知*/
	public static final int INTEGRAL_CONTROL = 17;
	
	/** 部落通过审核*/
	public static final int PASS_CREATE_TRIBE = 20;
	/** 部落未通过审核*/
	public static final int REFUSE_CREATE_TRIBE = 21;
	/** 申请加入部落*/
	public static final int APPLY_ADD_TRIBE = 22;
	/** 同意加入部落 */
	public static final int AGREE_ADD_TRIBE = 23;
	/** 不同意加入部落 */
	public static final int DISAGREE_ADD_TRIBE = 24;
	/** 邀请加入部落 */
	public static final int INVITE_ADD_TRIBE = 25;
	/** 同意邀请加入部落*/
	public static final int AGREE_INVITE_ADD_TRIBE = 26;
	/** 不同意邀请加入部落*/
	public static final int DISAGREE_INVITE_ADD_TRIBE = 27;
	/** 被踢出部落*/
	public static final int TRIBE_KICK_OUT = 28;
	
	/** 会议通过审核*/
	public static final int PASS_CREATE_MEETING = 30;
	/** 会议未通过审核*/
	public static final int REFUSE_CREATE_MEETING = 31;
	/** 申请加入会议*/
	public static final int APPLY_ADD_MEETING = 32;
	/** 同意加入会议*/
	public static final int AGREE_ADD_MEETING = 33;
	/** 不同意加入会议*/
	public static final int REFUSE_ADD_MEETING = 34;
	/** 邀请加入会议*/
	public static final int INVITE_ADD_MEETING = 35;
	/** someone 同意 my 邀请加入会议*/
	public static final int AGREE_INVITE_ADD_MEETING = 36;
	/** 不同意邀请加入会议*/
	public static final int REFUSE_INVITE_ADD_MEETING = 37;
	/** 被踢出会议*/
	public static final int MEETING_KICK_OUT = 38;
	
	/** 收到评论消息*/
	public static final int COMMENT_MESSAGE = 40;
	/** 收到消息举报*/
	public static final int RECEIVE_REPORT_MSG = 41;
	/** 同意举报，房间人收到通知*/
	public static final int ROOM_RECEIVE_REPORT_MSG = 42;
	/** 同意举报，举报人收到通知*/
	public static final int REPORTED_PERSON_RECEIVE_REPORT_MSG = 43;
	/** 同意举报，被举报人收到通知*/
	public static final int BEEN_REPORTED_AGREE_REPORT_MSG = 44;
	/** 不同意举报*/
	public static final int REFUSE_REPORT_MSG = 45;
	/** 收藏消息*/
	public static final int FAVORITE_MESSAGE = 47;
	/** 取消收藏消息*/
	public static final int UNFAVORITE_MESSAGE = 48;
	/** 加关注通知*/
	public static final int FOLLOW_NOTIFY = 49;
	/** 求交往*/
	public static final int SEEKING_CONTACTS = 50;
	/** 同意求交往*/
	public static final int AGREE_SEEKING_CONTACTS = 51;
	/** 拒绝求交往*/
	public static final int REFUSE_SEEKING_CONTACTS = 52;
	
	
	/** 有人申请成为会议主持人*/
	public static final int APPLY_BECOME_HOST = 53;
	/** 会议发起人已经同意了你的主持人申请*/
	public static final int AGREE_BECOME_HOST = 54;
	/** 会议发起人拒绝了你的主持人申请*/
	public static final int REFUSE_BECOME_HOST = 55;
	/** 会议主持人身份已经降为嘉宾*/
	public static final int HOST_TO_GUEST = 56;
	/** 有人申请成为会议嘉宾*/
	public static final int APPLY_BECOME_GUEST = 57;
	/** 会议主持人同意了你的嘉宾申请*/
	public static final int AGREE_BECOME_GUEST = 58;
	/** 会议主持人拒绝了你的嘉宾申请*/
	public static final int REFUSE_BECOME_GUEST = 59;
	/** 其他主持人已经处理了某用户的嘉宾或者参会申请*/
	public static final int OTHER_DEAL_APPLY = 60;
	/** 某用户身份从会议主持人或者嘉宾回复至普通用户*/
	public static final int BACK_TO_NORMAL = 61;
	/** 你被邀请担任会议嘉宾*/
	public static final int INVITE_TO_GUEST = 62;
	/** 你被邀请担任会议主持人*/
	public static final int INVITE_TO_HOST = 63;
	/** 某用户已经同意（或者拒绝）担任主持人*/
	public static final int AGREE_OR_REFUSE_HOST = 64;
	/** 某用户已经同意（或者拒绝）担任嘉宾*/
	public static final int AGREE_OR_REFUSE_GUEST = 65;
	/** 会议主持人修改了会议时间*/
	public static final int HOSTOR_CHANGE_TIME = 66;
	/** 会议主持人取消了会议*/
	public static final int HOSTOR_CANCEL_MEETING = 67;
	/** 某条消息的赞被取消了一次 */
	public static final int MESSAGE_ZAN_CANCEL = 68;
	/** 某条消息的赞被加一次 */
	public static final int MESSAGE_ZAN_ADD = 69;
	/** 你的消息被赞了 */
	public static final int MESSAGE_ZAN_YOURS = 70;
	
}