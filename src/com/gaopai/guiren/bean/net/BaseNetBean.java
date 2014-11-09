package com.gaopai.guiren.bean.net;

import java.io.Serializable;

import com.gaopai.guiren.bean.AppState;
import com.gaopai.guiren.bean.PageInfo;
import com.google.gson.annotations.Expose;

public class BaseNetBean implements Serializable {
	@Expose
	public AppState state;
	@Expose
	public PageInfo pageInfo;
}
