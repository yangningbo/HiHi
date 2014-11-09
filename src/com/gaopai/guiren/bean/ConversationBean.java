package com.gaopai.guiren.bean;

public class ConversationBean {
	public String headurl;
	public String name;
	public String lastmsgcontent;
	public String lastmsgtime;
	public int unreadcount;
	public int type;//100私聊，200会议，300圈子，102，202，302语音
	public String toid;
	
	//是否匿名 1——匿名 0——实名
	public int anonymous;
	public String unfinishinput;
	/*
	 * headurl name lastmsgcontent lastmsgtime unreadcount type toid anonymous
	 * unfinishinput
	 */

}
