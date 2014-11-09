package com.gaopai.guiren.service.type;
/***
 * 
 * 功能： XMPP管理类型 <br />
 * 日期：2013-6-1<br />
 * 地点：西竹科技<br />
 * 版本：ver 1.0<br />
 * 
 * @since
 */
public class XmppType {
	/** XMPP对应的键 */
	public static final String XMPP_KEY = "xskey";
	/** 登录成功了的 */
	public static final String XMPP_STATE_AUTHENTICATION = "xsauth";
	/** 未登录的 */
	public static final String XMPP_STATE_REAUTH = "xsreauth";
	/** 登录失败了的 */
	public static final String XMPP_STATE_AUTHERR = "xserauth";
	/** 服务启动的 */
	public static final String XMPP_STATE_START = "xsstart";
	/** 服务没有启动 */
	public static final String XMPP_STATE_STOP = "xsstop";
}
