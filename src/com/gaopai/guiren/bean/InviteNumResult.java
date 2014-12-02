package com.gaopai.guiren.bean;

import com.gaopai.guiren.bean.net.BaseNetBean;

public class InviteNumResult extends BaseNetBean {
	public InviteNumberBean data;
	public static class InviteNumberBean {
		public int total;
		public int complete;
	}
}
