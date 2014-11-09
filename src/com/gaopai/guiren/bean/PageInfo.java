package com.gaopai.guiren.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class PageInfo implements Serializable{

	private static final long serialVersionUID = 146435615132L;
	
	/**
	 *  "total": "2",
    "count": 20,
    "pageCount": 1,
    "page": 1
	 */
	@Expose
	public int page;
	@Expose
	public int pageCount;
	@Expose
	public int total;
	@Expose
	public int count;
	@Expose
	public boolean mHasMore;
	@Expose
	public int hasMore;

	
//	public PageInfo(JSONObject json){
//		try {
//			currentPage = json.getInt("page");
//			totalPage = json.getInt("pageCount");
//			totalCount = json.getInt("total");
//			pageSize = json.getInt("count");
//			int hasMore = json.getInt("hasMore");
//			mHasMore = hasMore == 1 ? true : false;
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
}
