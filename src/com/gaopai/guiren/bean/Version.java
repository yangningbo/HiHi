package com.gaopai.guiren.bean;

import java.io.Serializable;
import java.lang.reflect.Type;

import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class Version implements GsonObj, Serializable {

	private static final long serialVersionUID = 19874543545465L;
	/**
	 * "hasNewVersion": 1, "currVersion": "1.3", "url":
	 * "112.124.14.169/qichuang/Data/app/app_menu.apk", "description":
	 * "1.不错\r\n2.要得\r\n3.可以\r\n4.恩。", "updateType": "0"
	 */
	@Expose
	public String currVersion;
	@Expose
	public String url;
	@Expose
	public String description;
	@Expose
	public int hasNewVersion;
	@Expose
	public int updateType;
	@Expose
	public long updateTime = 0;

	@Override
	public String getInterface() {
		// TODO Auto-generated method stub
		return "index/update";
	}

	@Override
	public Type getTypeToken() {
		// TODO Auto-generated method stub
		return new TypeToken<Version>() {
		}.getType();
	}

}