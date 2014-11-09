package com.gaopai.guiren.bean.net;

import java.lang.reflect.Type;

import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.reflect.TypeToken;

public class CreatMeetingResult extends BaseNetBean implements GsonObj{
	int obj;
	@Override
	public String getInterface() {
		// TODO Auto-generated method stub
		return "/meeting/addMeeting";
	}

	@Override
	public Type getTypeToken() {
		// TODO Auto-generated method stub
		return new TypeToken<CreatMeetingResult>() {
		}.getType();
	}
}
