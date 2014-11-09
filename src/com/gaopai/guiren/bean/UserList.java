package com.gaopai.guiren.bean;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class UserList implements GsonObj, Serializable{
	private static final long serialVersionUID = -1153454654654L;
	@Expose
	public List<User> data;
	@Expose
	public AppState state;
	@Expose
	public PageInfo pageInfo;
	
	public static final int TYPE_FRIEND = 0;
	public static final int TYPE_RECOMMEND = 1;//登录时推荐的
	

	int obj;

	@Override
	public String getInterface() {
		if(obj == TYPE_FRIEND) {
			return "user/getMyFriend";
		} else if (obj == TYPE_RECOMMEND) {
			return "user/recommendfriend";
		} 
		return "user/fanslist";
	}

	@Override
	public Type getTypeToken() {
		return new TypeToken<UserList>() {
		}.getType();
	}

}
