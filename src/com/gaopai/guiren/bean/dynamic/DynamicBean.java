package com.gaopai.guiren.bean.dynamic;

import java.io.Serializable;
import java.util.List;

import com.gaopai.guiren.bean.AppState;
import com.gaopai.guiren.bean.PageInfo;
import com.google.gson.annotations.Expose;

public class DynamicBean implements Serializable{
	public List<TypeHolder> data;
	@Expose
	public DyState state;
	@Expose
	public PageInfo pageInfo;

	public static class DyState extends AppState {
		public int newalertcount; 
	}

	public static class TypeHolder implements Serializable {
		public String id;
		public String uid;
		public int type;
		public String title;
		public JsonContent jsoncontent;

		public String tag;
		public String time;
		public int isanonymous;
		public int totalkuosan;
		public int totalzan;
		public int totalcomment;
		public String from;
		public int isdelete;
		public List<CommentBean> commentlist;
		public List<ZanBean> zanList;
		public List<SpreadBean> spread;

		public String nickname;
		public String realname;
		public String m_path;
		public String s_path;
		public String company;
		public String post;

		public int isZan = 0;
	}

	public static class JsonContent implements Serializable {

		// type=3 4扩散了会议
		public List<PicBean> pic;
		public String content;
		public String tid;
		public String name;
		public List<GuestBean> guest;
		public String time;

		// type==2扩散了聊天室（会议、圈子、私聊）中的消息
		public String displayname;
		public String headsmall;
		public String messageid;
		public int fileType;
		public String voiceUrl;
		public int voiceTime;
		public String imgUrlS;
		public String imgUrlL;
		public int type;
		public String to;

		// type=5扩散了人脉
		public String company;
		public String post;
		public int integral;

		// type = 6扩散了一个链接(类似微信的扩散了链接)
		public String image;
		public String title;
		public String url;
		public String desc;

		// type=7 扩散的其他人发布的动态
		public String uid;
		public String sid;
		public String realname;
	
	}

	public static class GuestBean implements Serializable {
		public String realname;
		public String uid;
	}

	public static class PicBean implements Serializable {
		public String imgUrlS;
		public String imgUrlL;
		public int imgWidth;
		public int imgHeight;
	}

	public static class CommentBean implements Serializable {
		public String id;
		public String uid;
		public String uname;
		public int type;
		public String dataid;
		public CommentContetnHolder content;
		public int isanonymous;
		public String toid;
		public String toname;
		public String time;
	}

	public static class CommentContetnHolder implements Serializable {
		public String content;
	}

	public static class ZanBean implements Serializable {
		public String uid;
		public String uname;
	}
	
	public static class SpreadBean implements Serializable {
		public String uid;
		public String nickname;
		public String realname;
	}

}
