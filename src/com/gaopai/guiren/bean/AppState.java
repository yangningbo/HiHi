package com.gaopai.guiren.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class AppState implements Serializable{

	private static final long serialVersionUID = 149681634654564865L;
	@Expose
	public int code;
	@Expose
	public String msg = "";
	@Expose
	public String debugMsg = "";

}
