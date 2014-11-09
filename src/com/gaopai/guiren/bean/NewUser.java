package com.gaopai.guiren.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class NewUser extends User implements Serializable {

	public String id; // 用户ID
	@Expose
	public String touid; // 
	@Expose
	public String request; // 
	@Expose
	public String response; // 
	@Expose
	public int status; // 
	@Expose
	public String requesttime; // 
	@Expose
	public String from; // 
	@Expose
	public String realname; // 
}
