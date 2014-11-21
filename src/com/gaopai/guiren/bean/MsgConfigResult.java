package com.gaopai.guiren.bean;

import com.gaopai.guiren.bean.net.BaseNetBean;

public class MsgConfigResult extends BaseNetBean{
	public MsgConfigBean data;
	public static class MsgConfigBean {
		public String id;
		public String uid;
		public int dnd;
		public int dndH_begin;
		public int dndM_begin;
		public int dndH_end;
		public int dndM_end;
		public int ringtones;
		public int shake;
		public int dami;
		public int bh;
	}
}
