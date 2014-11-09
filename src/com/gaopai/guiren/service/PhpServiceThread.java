package com.gaopai.guiren.service;

import android.util.Log;

import com.gaopai.guiren.bean.User;
import com.gaopai.guiren.service.type.XmppTypeManager;


public class PhpServiceThread implements Runnable{
	private static final String TAG = "php_content_thread";
	private XmppTypeManager manager;
	private User userInfoVo;
	public static final long TIME = 300000;
	
	public boolean runState = true;
	
	public PhpServiceThread(XmppTypeManager manager, User userInfoVo) {
		super();
		this.manager = manager;
		this.userInfoVo = userInfoVo;
	}

	@Override
	public void run() {
		while(runState){
			connect();
			try {
				Thread.sleep(TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void connect(){
		Log.d(TAG, "connect()");
		try {
			//String result = userInfoApi.online(userInfoVo.getUid());
			//Log.d(TAG, "connect:online:" + result);
		} catch (Exception e) {
			Log.i(TAG, "connect()", e);
		}
	}

}
