package com.gaopai.guiren.receiver;

import com.gaopai.guiren.bean.MessageInfo;

/**
 * 
 * 功能：推送信息,好友之间的信息推送. <br />
 * 日期：2013-4-27<br />
 * 地点：西竹科技<br />
 * 版本：ver 1.0<br />
 * 
 * @since
 */
public interface PushMessage {
	void pushMessage(MessageInfo msg, String group);
}
