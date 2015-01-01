package com.gaopai.guiren.bean.net;

import java.lang.reflect.Type;
import java.util.List;

import com.gaopai.guiren.bean.TagBean;
import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class TagResult extends BaseNetBean implements GsonObj {
	@Expose
	public List<TagBean> data;

	int obj;

	@Override
	public String getInterface() {
		return "index/getTag";
	}

	@Override
	public Type getTypeToken() {
		return new TypeToken<TagResult>() {
		}.getType();
	}
}
