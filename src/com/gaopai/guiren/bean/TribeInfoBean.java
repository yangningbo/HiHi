package com.gaopai.guiren.bean;

import java.io.Serializable;
import java.lang.reflect.Type;

import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class TribeInfoBean implements Serializable, GsonObj {
	@Expose
	public Tribe data;
	@Expose
	public AppState state;
	@Expose
	public PageInfo pageInfo;

	public final static int TYPE_TRIBE_INFO = 0;
	public final static int TYPE_MEETING_INFO = 1;
	int obj;

	@Override
	public String getInterface() {
		// TODO Auto-generated method stub
		if (obj == TYPE_TRIBE_INFO) {
			return "tribe/tribe";
		} else {
			return "meeting/detail";
		}
	}

	@Override
	public Type getTypeToken() {
		// TODO Auto-generated method stub
		return new TypeToken<TribeInfoBean>() {
		}.getType();
	}

}
