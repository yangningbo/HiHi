package com.gaopai.guiren.bean.dynamic;

import java.io.Serializable;
import java.util.List;

import com.gaopai.guiren.bean.net.BaseNetBean;

public class NewDynamicBean extends BaseNetBean{
	
	public List<TypeHolder> data;
	
	public static class TypeHolder implements Serializable {
		public String id;
		public String uid;
		public int type;
		public String realname;
		public String headurl="s";//差这个字段
		public JsonContent jsoncontent;
	}
	
	public static class JsonContent implements Serializable {
		public String content;
		public String sid;
		public String name;
		public String image;
		public String recontent;
	}
	  
//	发表了一个动态11111111，然后评论555555555， uid是评论人的，realname是评论人的名称   
//	   {
//	            "id": "387",
//	            "uid": "13",
//	            "realname": "1221",
//	            "type": "2",
//	            "jsoncontent": {
//	                "sid": "413",
//	                "type": "1",
//	                "content": "1111111111111",
//	                "image": null,
//	                "recontent": "555555555555"
//	            }
//	        },
//
//
//	发表了一个动态11111111，然后扩散， uid是扩散人的，realname是扩散人的名称   
//	 {
//	            "id": "384",
//	            "uid": "13",
//	            "realname": "1221",
//	            "type": "1",
//	            "jsoncontent": {
//	                "sid": "275",
//	                "type": "7",
//	                "content": "1111111111111"
//	            }
//	        }
	
	
//	 {
//         "id": "388",
//         "uid": "13",
//         "realname": "1221",
//         "type": "1",
//         "jsoncontent": {
//             "sid": "277",
//             "type": "7",
//             "content": "222222222222"
//         }
//     }

}
