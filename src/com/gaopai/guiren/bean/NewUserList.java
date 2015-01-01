package com.gaopai.guiren.bean;

import java.lang.reflect.Type;
import java.util.List;

import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.annotations.Expose;

public class NewUserList implements GsonObj{
	public static final int TYPE_RECOMMEND_FRIEND = 0;//登录之后，connectionfragment中
	public static final int TYPE_NEW_FRIEND = 1;//登录之后，connectionfragment中
	@Expose
	public List<NewUser> data;
	@Expose
	public AppState state;
	@Expose
	public PageInfo pageInfo;

	int obj;
	@Override
	public String getInterface() {
		// TODO Auto-generated method stub
		if(obj == TYPE_NEW_FRIEND) {
			return "user/newFriend";
		} else if(obj == TYPE_RECOMMEND_FRIEND) {
			return "user/getSysRecFriend";
		}
		return "user/getSysRecFriend";
	}

	@Override
	public Type getTypeToken() {
		// TODO Auto-generated method stub
		return null;
	}

}
