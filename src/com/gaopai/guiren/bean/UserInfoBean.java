package com.gaopai.guiren.bean;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class UserInfoBean implements  Serializable,  GsonObj {
	@Expose
	public User data;
	@Expose
	public AppState state;
	@Expose
	public PageInfo pageInfo;

	int obj;
	@Override
	public String getInterface() {
		// TODO Auto-generated method stub
		return "user/user";
	}


	@Override
	public Type getTypeToken() {
		// TODO Auto-generated method stub
		return new TypeToken<UserInfoBean>() {
		}.getType();
	}

}
