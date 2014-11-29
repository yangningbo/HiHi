package com.gaopai.guiren.bean.net;

import java.io.Serializable;
import java.lang.reflect.Type;

import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class RegisterResult extends BaseNetBean implements GsonObj {
	@Expose
	public RegisterBean data;

	int obj;
	@Override
	public String getInterface() {
		// TODO Auto-generated method stub
		if (obj == 0) {
			return "index/reg";
		} else {
			return "index/resetpassword";
		}
	}


	@Override
	public Type getTypeToken() {
		// TODO Auto-generated method stub
		return new TypeToken<VerificationResult>() {
		}.getType();
	}
	
	public static class RegisterBean implements Serializable{
		@Expose
		public String code;
		@Expose
		public String phone;
	}
}
