package com.gaopai.guiren.bean;

import java.io.Serializable;
import java.util.List;

import com.gaopai.guiren.bean.net.BaseNetBean;

public class ReportMsgResult extends BaseNetBean {

	public List<ReportMsgBean> data;

	public static class ReportMsgBean implements Serializable {
		public String id;
		public UserBean user;
		public MessageInfo message;
		public String roomid;
		public String content = "";
		public long createtime = 0;
	}

	public static class UserBean implements Serializable {
		public String uid;
		public String realname;
		public String headsmall;

	}
}
