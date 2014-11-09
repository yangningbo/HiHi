package com.gaopai.guiren.bean.net;

import java.io.Serializable;
import java.lang.reflect.Type;

import com.gaopai.guiren.bean.AppState;
import com.gaopai.guiren.bean.PageInfo;
import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.TribeInfoBean;
import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class VerificationResult extends BaseNetBean implements GsonObj{
	@Expose
	public SmsCode data;

	int obj;
	@Override
	public String getInterface() {
		// TODO Auto-generated method stub
		return "index/getSmsCode/";
	}


	@Override
	public Type getTypeToken() {
		// TODO Auto-generated method stub
		return new TypeToken<VerificationResult>() {
		}.getType();
	}
	
	public static class SmsCode implements Serializable{
		@Expose
		public String code;
		@Expose
		public String phone;
	}

}
