package com.gaopai.guiren.support.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;

public class ChatMsgHelper {
	public static final int MAX_SECOND = 30;
	public static final int MIN_SECOND = 2;

	public static MessageInfo creatVoiceMsg(String url, String tag) {
		MessageInfo messageInfo = new MessageInfo();
		messageInfo.voiceUrl = url;
		messageInfo.tag = tag;
		return messageInfo;
	}

	public static MessageInfo creatPicMsg(String imgUrlS, String imgUrlL, String tag) {
		MessageInfo messageInfo = new MessageInfo();
		messageInfo.imgUrlS = imgUrlS;
		messageInfo.imgUrlL = imgUrlL;
		messageInfo.fileType = MessageType.PICTURE;
		messageInfo.tag = tag;
		return messageInfo;
	}

	public static ViewGroup.LayoutParams getVoiceViewLengthParams(ViewGroup.LayoutParams lp, Context mContext,
			int length) {

		float max = mContext.getResources().getDimension(R.dimen.voice_max_length_comment);
		float min = mContext.getResources().getDimension(R.dimen.voice_min_length_comment);
		int width = (int) min;
		if (length >= MIN_SECOND && length <= MAX_SECOND) {
			width += (length - MIN_SECOND) * (int) ((max - min) / (MAX_SECOND - MIN_SECOND));
		} else if (length > MAX_SECOND) {
			width = (int) max;
		}
		if (lp == null) {
			lp = new ViewGroup.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		} else {
			lp.width = width;
		}
		return lp;
	}
	
	// caculate the dimen of pic until receive size from server
	// Maximum of sizes is smaller than 200
	public static Point sizeOfPic(String path) {
		Point point = new Point();
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float ratio = Math.max(width / 200f, height / 200f);
		if (ratio > 1) {
			width = (int) (width / ratio);
			height = (int) (height / ratio);
		}
		point.x = width;
		point.y = height;
		return point;
	}
}
