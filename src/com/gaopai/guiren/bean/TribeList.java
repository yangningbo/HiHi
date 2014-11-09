package com.gaopai.guiren.bean;

import java.lang.reflect.Type;
import java.util.List;

import com.gaopai.guiren.bean.net.BaseNetBean;
import com.gaopai.guiren.volley.GsonObj;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

public class TribeList extends BaseNetBean implements GsonObj {

	@Expose
	public List<Tribe> data;
	public PageInfo pageInfo;

	int obj;
	
	public final static int TRIBE_RECOMMEND = 1;
	public final static int MEETING_LIST= 2;
	public final static int TRIBE_LIST= 3;

	@Override
	public String getInterface() {
		if (obj == TRIBE_RECOMMEND) { // 获取推荐部落
			return "user/recommend/";
		}else if(obj == MEETING_LIST){
			return "index/meetinglist/";
		} else if(obj == TRIBE_LIST) {
			return "user/mytribe/";
		}
		return "user/recommend";
	}

	@Override
	public Type getTypeToken() {
		return new TypeToken<TribeList>() {
		}.getType();
	}

}
