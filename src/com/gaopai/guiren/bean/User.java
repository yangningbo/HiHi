package com.gaopai.guiren.bean;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.utils.MyTextUtils;
import com.google.gson.annotations.Expose;

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
	public String depa; // 部门 换成了行业
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
	public int isfollow = 0; // 0-没有关系 1-关注 2-被关注 3-相互关注 关注指的是我关注别人
	@Expose
	public String displayName = "";
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
	public int tribeCount = 0;
	public int meetingCount = 0;
	@Expose
	public RoomIds roomids;

	public String weibo;
	public String weixin;

	public int bigv;
	public int iscontact;// 1是通讯录 0不是通讯录
	public int localType = 0;//

	public List<SpreadBean> kuosanlist;
	public List<ZanBean> zantaglist;
	public List<CommentBean> commentlist;
	public List<TagBean> tag;
	public PrivacyConfig privacyconfig;

	public int kuosanlistnum;

	public TypeHolder newdyna;

	public String codeurl;// QRcode

	public String nextpage;
	public String url;
	public String alertmessage;

	public int isguirenuser;

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
		public long addtime;
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

	// "newdyna": {
	// "id": "440",
	// "uid": "59",
	// "type": "7",
	// "title": "扩散了一个动态",
	// "jsoncontent": {
	// "pic": [
	// {
	// "imgUrlS":
	// "http://192.168.1.239:8081/Data/upload/dynamic/59/s_547fbbbea5f6b.jpg",
	// "imgUrlL":
	// "http://192.168.1.239:8081/Data/upload/dynamic/59/547fbbbea5f6b.jpg",
	// "imgWidth": 200,
	// "imgHeight": 200
	// }
	// ],
	// "content": "寂寞吗",
	// "sid": "439",
	// "uid": "59",
	// "realname": "Android4.1.2"
	// },
	// "tag": "厄尔,万科,",
	// "time": "1417657871",
	// "isanonymous": "0",
	// "totalkuosan": "0",
	// "totalzan": "0",
	// "totalcomment": "0",
	// "from": "439",
	// "isdelete": "0"
	// }
	// },

	public static String getUserName(User user) {
		if (!TextUtils.isEmpty(user.realname)) {
			return user.realname;
		} else if (!TextUtils.isEmpty(user.nickname)) {
			return user.nickname;
		}
		return "";
	}

	public static String getSubUserName(User user, Context context, int len) {
		int maxLen = len;
		String name = User.getUserName(user);
		if (MyTextUtils.length(name) > maxLen) {
			return MyTextUtils.getSubString(name, maxLen) + context.getString(R.string.ellipsize);
		}
		return name;
	}

	public static String getUserInfo(User tUser) {
		return TextUtils.isEmpty(tUser.company) ? (TextUtils.isEmpty(tUser.post) ? "" : tUser.post) : (TextUtils
				.isEmpty(tUser.post) ? tUser.company : tUser.company + "/" + tUser.post);
	}

	public static String getUserInfo(String company, String post) {
		return TextUtils.isEmpty(company) ? (TextUtils.isEmpty(post) ? "" : post) : (TextUtils.isEmpty(post) ? company
				: company + "/" + post);
	}

	public static boolean checkCanInvite(User mUser, BaseActivity activity) {
		if (mUser == null) {
			return false;
		}
		if (TextUtils.isEmpty(mUser.realname) || TextUtils.isEmpty(mUser.company) || TextUtils.isEmpty(mUser.post)
				|| TextUtils.isEmpty(mUser.depa) || TextUtils.isEmpty(mUser.email) || TextUtils.isEmpty(mUser.phone)) {
			activity.showToast(R.string.please_finish_profile);
			return false;
		}
		return true;
	}
}