package com.gaopai.guiren.bean;

import java.io.Serializable;
import java.lang.reflect.Type;

import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class Identity implements GsonObj,Serializable {

	private static final long serialVersionUID = -154545454545L;
	@Expose
	public String id;
	@Expose
	public String name;
	@Expose
	public String head;
	@Expose
	public String role;

	public long updateTime = 0;

	
	@Override
	public String getInterface() {
		return "user/getIdentity";
	}

	@Override
	public Type getTypeToken() {
		return new TypeToken<Identity>() {
		}.getType();
	}
	
}
