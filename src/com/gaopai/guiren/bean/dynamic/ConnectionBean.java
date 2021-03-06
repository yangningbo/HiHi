package com.gaopai.guiren.bean.dynamic;

import java.io.Serializable;
import java.util.List;

import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.utils.MyTextUtils.SpanUser;

public class ConnectionBean extends BaseNetBean {
	public List<TypeHolder> data;

	public static class TypeHolder implements Serializable{
		public String id;
		public String uid;
		public int type;
		


		public JsonContent jsoncontent;

		public String tips;
		public String addtime;

		public String nickname;
		public String realname;
		public String head;
		public String headsmall;
	}

	public static class JsonContent implements Serializable {
		public String speak;
		// type=1,2,3
		public String uid;
		public String realname;
		public String headsmall;
		public String company;
		public String post;
		public int bigv;

		// type=2,7,4,5,6
		public String roomid;
		public String roomname;
		public List<User> user;
		public List<User> content;
	}

	public static class User extends SpanUser {
		public String headsmall;
		public String company;
		public String post;
		public int bigv;
	}

	public static class ZanBean implements Serializable {
		public String uid;
		public String uname;
	}

}
