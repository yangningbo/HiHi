package com.gaopai.guiren.bean;

import java.io.Serializable;

import com.gaopai.guiren.bean.net.BaseNetBean;

public class QrCordBean extends BaseNetBean {
	public QrCodeResult data;

	public static class QrCodeResult implements Serializable{
		public String uid;
		public String realname;
		public int score;
		public String company;
		public String depa;
		public String post;
		public String s_path;
		public String codeurl;
	}

}
