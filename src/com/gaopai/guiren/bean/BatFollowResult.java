package com.gaopai.guiren.bean;

import com.gaopai.guiren.bean.net.BaseNetBean;

public class BatFollowResult extends BaseNetBean{
	public BatFollowBean data;
	//批量关注结果
	public static class BatFollowBean {
		public int total;
		public int complete;
		public int follownum;
	}
}
