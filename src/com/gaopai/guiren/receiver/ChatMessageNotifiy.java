package com.gaopai.guiren.receiver;

import android.content.Context;

import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.SNSMessage;
import com.gaopai.guiren.service.SnsService;
import com.gaopai.guiren.support.NotifyHelper;
import com.gaopai.guiren.utils.Logger;

public class ChatMessageNotifiy extends AbstractNotifiy {

	public static final int NOTIFYID_PRIVATE = 10000080;
	public static final int NOTIFYID_TRIBE = 10000081;
	public static final int NOTIFYID_MEETING = 10000082;

	private Context mContext;
	private NotifyHelper notifyHelper;

	public ChatMessageNotifiy(SnsService context) {
		super(context);
		mContext = context;
		notifyHelper = new NotifyHelper(mContext);
	}

	@Override
	public void notifiy(SNSMessage message) {
		Logger.d(this, "notifyyyyyyyyyyyyy()...");
		MessageInfo messageInfo = null;
		if (message instanceof MessageInfo) {
			messageInfo = (MessageInfo) message;
		} else {
			return;
		}
		notifyHelper.notifyChatMessage(messageInfo);
	}
}
