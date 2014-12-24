package com.gaopai.guiren.bean;

import java.util.List;

import com.gaopai.guiren.bean.net.BaseNetBean;

public class MsgZanListResult extends BaseNetBean {
	public List<ZanBean> data;
	public static class ZanBean {
		public String id;
		public String uid;
		public String msgid;
		public String displayname;
		public long createtime;
		public int role;
		public int isanonymity;
	}
}