package com.gaopai.guiren.bean.net;

import java.lang.reflect.Type;

import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.reflect.TypeToken;

public class RecommendAddResult extends BaseNetBean implements GsonObj{
	public static final int TYPE_ADD_FRIEND = 0;
	public static final int TYPE_ADD_TRIBE = 1;
	
	int obj;

	@Override
	public String getInterface() {
		// TODO Auto-generated method stub
		if(obj == TYPE_ADD_FRIEND) {
			return "user/requestfriend";
		} else if(obj == TYPE_ADD_TRIBE) {
			return "tribe/requesttribe";
		}
		return null;
	}

	@Override
	public Type getTypeToken() {
		// TODO Auto-generated method stub
		return new TypeToken<RecommendAddResult>() {
		}.getType();
	}
}
