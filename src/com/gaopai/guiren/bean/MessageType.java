package com.gaopai.guiren.bean;


public final class MessageType {
	public static final int PICTURE = 2;
	public static final int VOICE = 3;
	public static final int TEXT = 1;
	public static final int MAP = 4;
	public static final int OTHER = 5;
	
	
	//just for local information
	public static final int LOCAL_ANONY_TRUE = 20;//use anony identity
	public static final int LOCAL_ANONY_FALSE = 21;//use real identity
	
	
	
	public static String timeUid(){
		return System.currentTimeMillis() + "";
	}
}
