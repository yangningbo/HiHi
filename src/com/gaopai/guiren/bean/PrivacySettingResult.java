package com.gaopai.guiren.bean;

import java.io.Serializable;

import com.gaopai.guiren.bean.net.BaseNetBean;

public class PrivacySettingResult extends BaseNetBean{
	public PrivacySettingBean data;
	
	public static class PrivacySettingBean implements Serializable {
		public String id;
		public String uid;
		public int phone;
		public int mail;
		public int wechat;
		public int weibo;
		public int renmai;
	}
}
