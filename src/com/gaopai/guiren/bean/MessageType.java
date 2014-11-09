package com.gaopai.guiren.bean;


public final class MessageType {
	public static final int PICTURE = 2;
	public static final int VOICE = 3;
	public static final int TEXT = 1;
	public static final int MAP = 4;
	
	public static final int OTHER = 5;
	
	
	public static String timeUid(){
		return System.currentTimeMillis() + "";
	}
}
