package com.gaopai.guiren.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class Dynamic implements Serializable{

	/**
	 * "id": "44",
      "uid": "4",
      "type": "1",
      "tid": "1399530815815",
      "createtime": "1401196076",
      "realname": "Lily",
      "phone": "15888888888",
      "post": "\u8f6f\u4ef6\u5de5\u7a0b\u5e08",
      "company": "\u6210\u90fd\u897f\u7af9\u79d1\u6280",
      "name": "\u970d\u683c\u6c83\u5179",
	 */
	private static final long serialVersionUID = -11563564654L;
	@Expose
	public String id;
	@Expose
	public String uid;
	@Expose
	public int type = 0;
	@Expose
	public String tid;
	@Expose
	public long createtime;
	@Expose
	public String realname;
	@Expose
	public String post;
	@Expose
	public String company;
	@Expose
	public String name;
	@Expose
	public String headsmall;
	
}
