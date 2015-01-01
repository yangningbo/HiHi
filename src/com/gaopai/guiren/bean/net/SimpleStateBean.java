package com.gaopai.guiren.bean.net;

import java.io.Serializable;
import java.lang.reflect.Type;

import com.gaopai.guiren.bean.AppState;
import com.gaopai.guiren.volley.GsonObj;

public class SimpleStateBean implements Serializable, GsonObj {
	public static int TYPE_JOIN_MEETING_BY_PASSWORD = 0;
	public static int TYPE_DEAL_ADD_USER = 1;
	public static int TYPE_SET_NOT_PUSH = 2;

	public AppState state;

	int obj;

	@Override
	public String getInterface() {
		// TODO Auto-generated method stub
		if (obj == TYPE_JOIN_MEETING_BY_PASSWORD) {
			return "meeting/joinMeetingByPasswd";
		} else if (obj == TYPE_DEAL_ADD_USER) {
			return "user/chargeFriendRequest";
		} else if (obj == TYPE_SET_NOT_PUSH) {
			return "user/acceptPush";
		}
		return null;
	}

	@Override
	public Type getTypeToken() {
		// TODO Auto-generated method stub
		return null;
	}

}
