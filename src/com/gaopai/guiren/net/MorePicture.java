package com.gaopai.guiren.net;

public class MorePicture {
	public String key;
	public String filePath;
	public int mType = 0;
	
	public MorePicture(String key, String filePath) {
		super();
		this.key = key;
		this.filePath = filePath;
	}
	
	public MorePicture(String key, String filePath, int type) {
		super();
		this.key = key;
		this.filePath = filePath;
		this.mType = type;
	}
	
	public MorePicture() {
		super();
	}
	
}
