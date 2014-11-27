package com.gaopai.guiren.support;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.gaopai.guiren.bean.ConversationBean;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.NotifyMessageBean.ConversationInnerBean;
import com.gaopai.guiren.db.ConverseationTable;
import com.gaopai.guiren.db.DBHelper;

public class ConversationHelper {
	
	public static void saveToLastMsgListReaded(MessageInfo messageInfo, Context context) {
		saveToLastMsgList(messageInfo, context, true);
	}
	
	public static void saveToLastMsgList(MessageInfo messageInfo, Context context) {
		saveToLastMsgList(messageInfo, context, false);
	}
	
	/**
	 * 
	 * @param messageInfo
	 * @param context
	 * @param isRead whether add 1 to unread count
	 */
	public static void saveToLastMsgList(MessageInfo messageInfo, Context context, boolean isRead) {
		SQLiteDatabase dbDatabase = DBHelper.getInstance(context).getWritableDatabase();
		ConversationInnerBean bean = messageInfo.conversion;
		ConverseationTable table = new ConverseationTable(dbDatabase);
		ConversationBean conversation = table.queryByID(bean.toid);
		if (conversation != null) {
			if (messageInfo.fileType == MessageType.VOICE) {
				conversation.type = bean.type + 2;
			} else {
				conversation.type = bean.type;
			}
			conversation.headurl = bean.headurl;
			if (!isRead) {
				conversation.unreadcount = conversation.unreadcount + 1;
			}
			conversation.name = bean.name;
			conversation.lastmsgcontent = messageInfo.content;
			conversation.lastmsgtime = String.valueOf(System.currentTimeMillis());
			table.update(conversation);
			return;
		}
		if (conversation == null) {
			conversation = new ConversationBean();
		}
		conversation.headurl = bean.headurl;
		if (isRead) {
			conversation.unreadcount = 0;
		} else {
			conversation.unreadcount = 1;
		}
		conversation.name = bean.name;
		conversation.lastmsgcontent = messageInfo.content;
		conversation.lastmsgtime = String.valueOf(System.currentTimeMillis());
		conversation.toid = bean.toid;
		if (messageInfo.fileType == MessageType.VOICE) {
			conversation.type = bean.type + 2;
		} else {
			conversation.type = bean.type;
		}
		conversation.anonymous = 0;
		table.insert(conversation);
	}

}
