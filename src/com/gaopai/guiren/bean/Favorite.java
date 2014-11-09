package com.gaopai.guiren.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class Favorite implements Serializable{

	private static final long serialVersionUID = -11545435143L;
	
	@Expose
	public String id;
	@Expose
	public User mUser;
	@Expose
	public MessageInfo mMessageInfo;
	@Expose
	public long createtime;
	@Expose
	public String roomid;
	

}
