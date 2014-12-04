package com.gaopai.guiren.bean.dynamic;

import java.io.Serializable;
import java.util.List;

import com.gaopai.guiren.bean.dynamic.DynamicBean.PicBean;
import com.gaopai.guiren.bean.net.BaseNetBean;

public class NewDynamicBean extends BaseNetBean{
	
	public List<TypeHolder> data;
	
	public static class TypeHolder implements Serializable {
		public String id;
		public String uid;
		public int type;//1=扩散 2=评论 3=赞
		public String realname;
		public String head;
		public JsonContent jsoncontent;
		public long addtime;
	}
	
	public static class JsonContent implements Serializable {
		public String content;
		public String sid;
		//如果外面的type=赞，此type为DynamicBean里面的type
		//如果外面的type=评论，此type为1：动态，实名评论 2：会议室 3：圈子 4：聊天室 5：人脉，
		//如果外面的type=扩散，此type为7？
		public int type;
		public String name;
		public List<PicBean> pic;
		public String recontent;
		public String image;
	}
	public static class PicBean implements Serializable {
		public String imgUrlS;
		public String imgUrlL;
		public int imgWidth;
		public int imgHeight;
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
