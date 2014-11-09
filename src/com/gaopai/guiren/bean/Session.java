package com.gaopai.guiren.bean;

import java.io.Serializable;


public class Session implements Serializable{
	private static final long serialVersionUID = 5389219102904727377L;
	private String loginId;		//  当前登录用户ID
	private String fromId;		// 会话来源用户ID
	private int unreadCount;	// 列表中包含未读条数
	private int listType;		// 列表的类型。
	private long sendTime;		// 发送时间
	private int isRoom = 0;
	public int isOwner = 0;
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromId == null) ? 0 : fromId.hashCode());
		result = prime * result;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Session other = (Session) obj;
		if (fromId == null) {
			if (other.fromId != null)
				return false;
		} else if (!fromId.equals(other.fromId))
			return false;
		return true;
	}
	
	/**
	 * @return the id
	 */
	public String getLoginId() {
		return loginId;
	}
	/**
	 * @return the fuid
	 */
	public String getFromId() {
		return fromId;
	}
	/**
	 * @return the notReadNum
	 */
	public int getUnreadNum() {
		return unreadCount;
	}
	/**
	 * @return the listType
	 */
	public int getListType() {
		return listType;
	}
	/**
	 * @return the createTime
	 */
	public long getSendTime() {
		return sendTime;
	}
	
	/**
	 * @param fuid the fromId to set
	 */
	public void setFromId(String fromId) {
		this.fromId = fromId;
	}
	
	/**
	 * @param loginId the loginId to set
	 */
	
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	/**
	 * @param notReadNum the notReadNum to set
	 */
	public void setUnreadNum(int unreadCount) {
		this.unreadCount = unreadCount;
	}
	/**
	 * @param listType the listType to set
	 */
	public void setListType(int listType) {
		this.listType = listType;
	}
	/**
	 * @param createTime the createTime to set
	 */
	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}
	
	public void setIsRoom(int isRoom){
		this.isRoom = isRoom;
	}
	
	public int getIsRoom(){
		return isRoom;
	}
	
}
