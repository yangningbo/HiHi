package com.gaopai.guiren.support;

import android.content.Context;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.gaopai.guiren.R;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;

public class ChatMsgHelper {

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

	public static RelativeLayout.LayoutParams getVoiceViewLengthParams(Context mContext, int length) {
		final int MAX_SECOND = 10;
		final int MIN_SECOND = 2;
		float max = mContext.getResources().getDimension(R.dimen.voice_max_length_comment);
		float min = mContext.getResources().getDimension(R.dimen.voice_min_length_comment);
		int width = (int) min;
		if (length >= MIN_SECOND && length <= MAX_SECOND) {
			width += (length - MIN_SECOND) * (int) ((max - min) / (MAX_SECOND - MIN_SECOND));
		} else if (length > MAX_SECOND) {
			width = (int) max;
		}
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		return lp;
	}

}
