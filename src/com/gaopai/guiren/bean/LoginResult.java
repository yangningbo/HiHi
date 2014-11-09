package com.gaopai.guiren.bean;

import java.io.Serializable;
import java.lang.reflect.Type;

import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class LoginResult implements GsonObj, Serializable {

	private static final long serialVersionUID = 113454353454L;

	@Expose
	public User data;
	@Expose
	public AppState state;

	int obj;

	@Override
	public String getInterface() {
		// TODO Auto-generated method stub
		if (obj == 1) { // 验证贵人码
			return "user/codeCheck";
		} else if (obj == 2) { // 隐式登录
			return "index/hiddenLogin";
		}
		return "index/login";
	}

	@Override
	public Type getTypeToken() {
		return new TypeToken<LoginResult>() {
		}.getType();
	}
}
