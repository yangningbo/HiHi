package com.gaopai.guiren.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * 部落
 * 
 * @Description
 * @author Dean
 * 
 */
public class Tribe implements Serializable {

	private static final long serialVersionUID = -11534545454L;

	/**
	 * "id": "1", //id "uid": "200300",
	 * 
	 * 
	 * "name": "为了部落", "logosmall": "", "logolarge": "", "industry": "电子商务",
	 * "type": "1", "content": "这是一个部落的说明", //部落说明 "check": "1", //1 审核通过的
	 * "createtime": "0" //创建时间
	 */
	@Expose
	public String id;
	@Expose
	public String uid; // 创建者
	@Expose
	public String name; // 名称
	@Expose
	public String logosmall; // 小图
	@Expose
	public String logolarge; // 大图
	@Expose
	public String industry; // 行业名称
	@Expose
	public String industryid; // 行业ID
	@Expose
	public int type; // 1 公开群 2-私密群
	@Expose
	public String content = ""; // 部落说明或会议大纲
	@Expose
	public int check = 0; // 1 审核通过的
	@Expose
	public long createtime; // 创建时间
	@Expose
	public String realname; // 创建者
	@Expose
	public int isjoin; // 0 -- 未加入 1--已加入
	/**
	 * role 0 普通用户 ;role 1 会议发起人同时也是主持人 ;role 2 会议嘉宾或者部落自发申请实名用户 ;role 3 会议主持人
	 */
	@Expose
	public int role = 0; // 0 -- 普通用户 1--创建者
	@Expose
	public int count = 0; // 群人数
	@Expose
	public long sendTime = 0;
	@Expose
	public int mMessageCount = 0;
	@Expose
	public long lastMessageTime = 0;
	@Expose
	public MessageInfo mMessageInfo;
	@Expose
	public int getmsg = 1; // 消息提醒类型 	1接受并提醒，2接受不提醒
	@Expose
	public int applyCount = 0; // 申请数
	@Expose
	public int jubaoCount = 0; // 举报数
	@Expose
	public long start; // 开始时间
	@Expose
	public long end; // 结束时间
	@Expose
	public int isexpired = 0; // 0--未过期 1--已过期
	@Expose
	public String users = "";
	@Expose
	public String codeurl = "";
	@Expose
	public String hosts = "";
	@Expose
	public String guest = "";
	@Expose
	public String importantuser = "";
	@Expose
	public String from = "";
	@Expose
	public boolean isInTribe = false;
	@Expose
	public List<Member> member;
	
	public boolean isTribeOrMeeting = true;

	public static class Member implements Serializable{
		@Expose
		public String uid;
		@Expose
		public String headsmall;
		@Expose
		public String realname;
		@Expose
		public String company;
		@Expose
		public String post;

	}

}
