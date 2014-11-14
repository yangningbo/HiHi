package com.gaopai.guiren.bean;

import java.io.Serializable;
import java.util.List;

import com.gaopai.guiren.bean.net.BaseNetBean;
import com.google.gson.annotations.Expose;

public class FavoriteList extends BaseNetBean implements Serializable{

	private static final long serialVersionUID = -11545435143L;
	public List<Favorite> data;
	
	public static class Favorite implements Serializable {
		@Expose
		public String id;
		@Expose
		public User user;
		@Expose
		public MessageInfo message;
		@Expose
		public long createtime;
		@Expose
		public String roomid;
	}
	
}
