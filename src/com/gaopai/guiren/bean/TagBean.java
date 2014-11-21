package com.gaopai.guiren.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class TagBean implements Serializable{
	@Expose
	public String id;
	@Expose
	public String tag;
	@Expose
	public String times;
	
	public String uid;
	public int num;
}
