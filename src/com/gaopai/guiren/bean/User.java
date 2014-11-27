package com.gaopai.guiren.bean;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class User implements Serializable {

	private static final long serialVersionUID = -1945455564L;

	/**
	 * "uid": "4", "head": "7", "username": "", "password": "846768",
	 * "nickname": "Martinalily", "gender": "2", "thirdpartyid": "2451191967",
	 * "loginType": "sina", "phone": "", "realname": "", "company": "", "post":
	 * "", "auth": "0", "createtime": "1397636365", "s_path":
	 * "http:\/\/tp4.sinaimg.cn\/2451191967\/180\/5631324169\/0", "m_path":
	 * "http:\/\/tp4.sinaimg.cn\/2451191967\/180\/5631324169\/0", "authStage":
	 * "1"
	 */

	// 一般情况下显示realname， 在聊天室里显示displayname，发送消息时根据是否匿名设置displayname
	@Expose
	public int tuid; // 是否被邀请了 0否
	@Expose
	public String uid; // 用户ID
	@Expose
	public String username; // 用户名
	@Expose
	public String password; // 密码
	@Expose
	public String nickname; // 昵称
	@Expose
	public String gender; // 性别
	@Expose
	public String thirdpartyid; // 第三方登录ID
	@Expose
	public String loginType; // 登录方式
	@Expose
	public String phone; // 电话号码
	@Expose
	public String realname; // 真实姓名
	@Expose
	public String company; // 公司名称
	@Expose
	public String post; // 职业
	@Expose
	public int integral = 0; // 积分
	@Expose
	public int auth; // 是否是已认证的用户
	@Expose
	public long createtime; // 创建时间
	@Expose
	public String headsmall; // 小头像
	@Expose
	public String headlarge; // 中头像
	@Expose
	public String email = ""; // 邮箱
	@Expose
	public int authStage; // 认证阶段 1-内测阶段（贵人码阶段） 2-正常发展阶段
	@Expose
	public String token = ""; // 验证接口Token
	@Expose
	public long tokenExpiredTime = 0; // Token过期时间
	@Expose
	public int followers = 0; // 关注数
	@Expose
	public int fansers = 0; // 粉丝数
	@Expose
	public String device = ""; // 设备ID
	@Expose
	public String sign = ""; // 个性签名
	@Expose
	public int isfollow = 0; // 0-没有关系 1-关注 2-被关注 3-相互关注
	@Expose
	public String displayName = "";
	@Expose
	public int mProcessType = 0;
	@Expose
	public long addtime = 0;
	@Expose
	public String content; // 申请参会理由
	@Expose
	public Dynamic dynamic; // 最新动态
	@Expose
	public FavoriteList favorite; // 最新收藏
	@Expose
	public int favoriteCount = 0; // 收藏数
	@Expose
	public int dynamicCount = 0; // 动态数
	@Expose
	public RoomIds roomids;
	@Expose
	public int relation;// 和我有没有关系，有关系为1，没关系为0
	@Expose
	public int totalcomfriend;// 和我有多少共同好友

	public String reason;

	public String weibo;
	public String weixin;

	public List<SpreadBean> kuosanlist;
	public List<ZanBean> zantaglist;
	public List<CommentBean> commentlist;
	public List<TagBean> tag;
	public PrivacyConfig privacyconfig;

	public int kuosanlistnum;

	public class RoomIds implements Serializable {
		@Expose
		public String tribelist; // 用户所加入的部落id
		@Expose
		public String meetinglist;// 用户所在的会议ID
	}

	public static class SpreadBean implements Serializable {
		public String uid;
		public String realname;
	}

	public static class ZanBean implements Serializable {
		public String uid;
		public String realname;
		public String zantag;
	}

	public static class CommentBean implements Serializable {
		public String id;
		public String uid;
		public String uname;
		public String s_path;
		public String addtime;
		public CommentContent content;
	}

	public static class CommentContent implements Serializable {
		public String content;
	}

	public static class PrivacyConfig implements Serializable {
		public String id;
		public String uid;
		public int phone;
		public int mail;
		public int wechat;
		public int weibo;
		public int renmai;
	}
}
