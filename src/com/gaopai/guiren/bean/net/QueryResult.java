package com.gaopai.guiren.bean.net;

import java.lang.reflect.Type;
import java.util.List;

import com.gaopai.guiren.bean.Tribe;
import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.bean.dynamic.DynamicBean;
import com.gaopai.guiren.bean.dynamic.DynamicBean.TypeHolder;
import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class QueryResult extends BaseNetBean implements GsonObj {
	public DataHolder data;
	int obj;
	@Override
	public String getInterface() {
		// TODO Auto-generated method stub
		return "index/query/";
	}
	
	@Override
	public Type getTypeToken() {
		// TODO Auto-generated method stub
		return new TypeToken<QueryResult>() {
		}.getType();
	}
	
	public static class DataHolder {
		public List<User> user;
		public List<Tribe> tribe;
		public List<Tribe> meeting;
		public List<TypeHolder> dynamic;
	}

}
