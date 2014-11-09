package com.gaopai.guiren.bean;

import java.io.Serializable;
import java.lang.reflect.Type;

import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class BaseBean implements GsonObj,Serializable{

	private static final long serialVersionUID = 113454353454L;
	
	@Expose
	public Object data;
	@Expose
	public AppState state;
	
	int obj;

	@Override
	public String getInterface() {
		if(obj ==1){ //实名认证
			return "user/realnameAuth";
		}
		return "user/realnameAuth";
	}

	@Override
	public Type getTypeToken() {
		return new TypeToken<BaseBean>() {
		}.getType();
	}
}
