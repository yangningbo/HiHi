package com.gaopai.guiren.receiver;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.bean.ConversationBean;
import com.gaopai.guiren.bean.NotifiyType;
import com.gaopai.guiren.bean.NotifiyVo;
import com.gaopai.guiren.bean.NotifyMessageBean.ConversationInnerBean;
import com.gaopai.guiren.bean.NotifyMessageBean.NotifyChatBean;
import com.gaopai.guiren.db.ConverseationTable;
import com.gaopai.guiren.db.DBHelper;
import com.gaopai.guiren.service.XmppManager;

public class NotifySystemMessage implements NotifyMessage {
	private static final String TAG = NotifySystemMessage.class
			.getCanonicalName();
	/**
	 * 聊天服务发送系统消息广播包<br/>
	 * 附加参数: {@link NotifySystemMessage#EXTRAS_NOTIFY_SYSTEM_MESSAGE} 附加参数:
	 * {@link NotifySystemMessage#EXTRAS_NOTIFY_SYSTEM_TAG}
	 */
	public static final String ACTION_NOTIFY_SYSTEM_MESSAGE = "com.gaopai.guiren.sns.notify.ACTION_NOTIFY_SYSTEM_MESSAGE";

	/**
	 * 附加标识<br/>
	 * {@link NotifiyType}
	 */
	public static final String EXTRAS_NOTIFY_SYSTEM_TAG = "extra_tag";

	/**
	 * 附加信息<br/>
	 * {@link NotifiyVo}
	 */
	public static final String EXTRAS_NOTIFY_SYSTEM_MESSAGE = "extras_message";

	/**
	 * VIP 状态发生变化 附加参数: {@link NotifySystemMessage#EXTRAS_VIP}
	 * */
	public static final String ACTION_VIP_STATE = "com.gaopai.guiren.sns.notify.ACTION_VIP_STATE";

	/**
	 * {@link CustomerVo}
	 * */
	public static final String EXTRAS_VIP = "extra_vip";

	private XmppManager xmppManager;
	private SystemNotifiy systemNotifiy;

	public NotifySystemMessage(XmppManager xmppManager) {
		super();
		this.xmppManager = xmppManager;
		this.systemNotifiy = new SystemNotifiy(xmppManager.getSnsService());
	}

	@Override
	public void notifyMessage(String msg) {
		Log.d(TAG, "notitySystemMessage()：" + msg);
		try {
			NotifiyVo notifiyVo = JSONObject.parseObject(msg, NotifiyVo.class);
			this.systemNotifiy.notifiy(notifiyVo);
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "notityMessage()", e);
		}
	}

	// 如果发送的是通过邀请，则不再显示通知，直接发送信息
	private boolean isInvitePassed(NotifiyVo notifiyVo) {
		if (notifiyVo.type == NotifiyType.PASS_INVITE_CODE) {
			// Intent i = new
			// Intent(ApplyInvitationActivity.SEND_MESSAGE_ACTION);
			// i.putExtra(ApplyInvitationActivity.GUI_REN_CODE_KEY,
			// notifiyVo.mContent);
			// DamiApp.getInstance().sendBroadcast(i);
			return true;
		}
		return false;
	}
	


}
