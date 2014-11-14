package com.gaopai.guiren.bean.net;

import java.util.List;

import com.gaopai.guiren.bean.AppState;

public class SendDynamicResult extends BaseNetBean {
	public DResult data;
	
    
  public static class DResult {
  	public DContetnResult jsoncontent;
  	public String uid;
  	public String type;
  	public String title;
  	public String tag;
  	public String time;
  	public int isanonymous;
  	public String totalkuosan;
  	public String totalzan;
  	public String totalcomment;
  	public String from;
  	public String id;
  }
    
  public static class DContetnResult {
//	public List<DImage> pic;
	public String content;
}
    
//    public class DResult {
//    	public DContetnResult jsoncontent;
//    	public String uid;
//    	public String type;
//    	public String title;
//    	public String tag;
//    	public String time;
//    	public int isanonymous;
//    	public String totalkuosan;
//    	public String totalzan;
//    	public String totalcomment;
//    	public String from;
//    	public String id;
//    }
//    
//    public class DContetnResult {
//    	public List<DImage> pic;
//    	public String content;
//    }
//	
//	public class DImage {
//		public String imgUrlS;
//		public String imgUrlL;
//		public String imgWidth;
//		public String imgHeight;
//	}

}
