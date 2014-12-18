package com.gaopai.guiren.support;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.gaopai.guiren.bean.ConversationBean;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.bean.NotifiyVo;
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
	 * @param isRead
	 *            whether add 1 to unread count
	 */
	public static void saveToLastMsgList(MessageInfo messageInfo, Context context, boolean isRead) {
		SQLiteDatabase dbDatabase = DBHelper.getInstance(context).getWritableDatabase();
		ConversationInnerBean bean = messageInfo.conversion;
		ConverseationTable table = new ConverseationTable(dbDatabase);
		ConversationBean conversation = table.queryByID(bean.toid);
		if (conversation != null) {
			if (messageInfo.fileType == MessageType.VOICE) {
				conversation.localtype = 1;
			} else if (messageInfo.fileType == MessageType.PICTURE) {
				conversation.localtype = 2;
			} else {
				conversation.localtype = 0;
			}
			if (!TextUtils.isEmpty(bean.headurl)) {
				conversation.headurl = bean.headurl;
			}
			conversation.type = bean.type;
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
		if (!TextUtils.isEmpty(bean.headurl)) {
			conversation.headurl = bean.headurl;
		}
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
			conversation.localtype = 1;
		} else if (messageInfo.fileType == MessageType.PICTURE) {
			conversation.localtype = 2;
		} else {
			conversation.localtype = 0;
		}
		conversation.type = bean.type;
		conversation.anonymous = 0;
		table.insert(conversation);
	}

	public static void creatNewItem() {
	}

	public static boolean saveDraft(Context context, MessageInfo messageInfo) {
		SQLiteDatabase dbDatabase = DBHelper.getInstance(context).getWritableDatabase();
		ConverseationTable table = new ConverseationTable(dbDatabase);
		ConversationInnerBean bean = messageInfo.conversion;
		ConversationBean conversation = table.queryByID(bean.toid);
		if (conversation == null) {
			if (TextUtils.isEmpty(messageInfo.content)) {
				return false;
			}
			conversation = new ConversationBean();
			conversation.name = bean.name;
			conversation.lastmsgtime = String.valueOf(System.currentTimeMillis());
			conversation.toid = bean.toid;
			conversation.unfinishinput = messageInfo.content;
			if (messageInfo.fileType == MessageType.VOICE) {
				conversation.localtype = 1;
			} else if (messageInfo.fileType == MessageType.PICTURE) {
				conversation.localtype = 2;
			} else {
				conversation.localtype = 0;
			}
			conversation.type = bean.type;
			conversation.unreadcount = 0;
			conversation.anonymous = 0;
			table.insert(conversation);
		} else {
			conversation.unfinishinput = messageInfo.content;
			table.update(conversation);
		}
		return true;
	}

	// for system
	public static void saveToLastMsgList(NotifiyVo notifiyVo, Context mContext, boolean isRead) {
		SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getWritableDatabase();
		ConversationInnerBean bean = notifiyVo.conversion;
		ConverseationTable table = new ConverseationTable(dbDatabase);
		ConversationBean conversation = table.queryByID(bean.toid);
		if (conversation != null) {
			conversation.headurl = bean.headurl;
			if (!isRead) {
				conversation.unreadcount = conversation.unreadcount + 1;
			}
			conversation.name = bean.name;
			conversation.lastmsgcontent = notifiyVo.content;
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
		conversation.lastmsgcontent = notifiyVo.content;
		conversation.lastmsgtime = String.valueOf(System.currentTimeMillis());
		conversation.toid = bean.toid;
		conversation.type = bean.type;
		conversation.anonymous = 0;
		table.insert(conversation);
	}

	public static void deleteItem(Context context, String id) {
		SQLiteDatabase dbDatabase = DBHelper.getInstance(context).getWritableDatabase();
		ConverseationTable table = new ConverseationTable(dbDatabase);
		table.delete(id);
	}

}
