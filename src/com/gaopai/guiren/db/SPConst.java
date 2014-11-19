package com.gaopai.guiren.db;

import android.content.Context;

import com.gaopai.guiren.DamiCommon;

public class SPConst {
	
	//用来储存免打扰的会议或部落
	public final static String SP_AVOID_DISTURB = "sp_avoid_disturb";
	public static String getTribeUserId(Context context, String tribeId) {
		return DamiCommon.getUid(context)+"&"+tribeId;
	}
}
