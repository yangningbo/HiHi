package com.gaopai.guiren.bean;

import java.io.Serializable;

public class CalenderPrompt implements Serializable{

	private static final long serialVersionUID = -115454545L;
	
	public long mTime = 0;
	public long mStartTime = 0;
	public long mEndTime = 0;
	public String mTitle = "";
	public String id;
	
	public CalenderPrompt(){}
	
	public CalenderPrompt(String id, long start, long end, String title, long time){
		this.id = id;
		this.mStartTime = start;
		this.mEndTime = end;
		this.mTitle = title;
		this.mTime = time;
	}

}
