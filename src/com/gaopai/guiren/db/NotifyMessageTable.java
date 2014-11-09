package com.gaopai.guiren.db;

import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.bean.MessageInfo;

public class NotifyMessageTable {

	public static final String TABLE_NAME = "NotifyMessageTable";//数据表的名称

	public static final String COLUMN_NOTIFY_ID = "notifyID";
	public static final String COLUMN_FROM_UID = "fromId";
	public static final String COLUMN_TO_ID = "toId";
	public static final String COLUMN_LOGIN_ID = "loginId";
	public static final String COLUMN_ID = "messageID";
	public static final String COLUMN_TAG = "messageTag";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_IMAGE_URLS = "imageUrlS";
	public static final String COLUMN_IMAGE_URLL = "imageUrlL";
	public static final String COLUMN_VOICE_URL = "voiceUrl";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_MESSAGE_TYPE = "msgtype";
	public static final String COLUMN_IMAGE_WIDTH = "imageWith";
	public static final String COLUMN_IMAGE_HEIGHT = "imageHeight";
	public static final String COLUMN_DISPLAYNAME = "displayName";
	public static final String COLUMN_HEADIMGURL = "headImgUrl";
	public static final String COLUMN_PARENT_ID = "parentId";
	public static final String COLUMN_SEND_TIME = "sendTime";
	public static final String COLUMN_VOICE_TIME = "voiceTime";
	public static final String COLUMN_READ_STATE = "readState";
	public static final String COLUMN_SEND_STATE = "sendState";
	public static final String COLUMN_IS_READ_VOICE = "readVoiceState";
	public static final String COLUMN_FAVORITE_COUNT = "favoriteCount";
	public static final String COLUMN_COMMENT_COUNT = "commentCount";
	public static final String COLUMN_IS_FAVORITE = "isFavorite";
	public static final String COLUMN_IS_SHIDE = "isShide";
	
	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String PRIMARY_KEY_TYPE = "primary key(";
	
	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;
	
	public NotifyMessageTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}
	
	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {

			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_NOTIFY_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_FROM_UID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_TO_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_LOGIN_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_TAG, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_CONTENT, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_IMAGE_URLS, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_IMAGE_URLL, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_VOICE_URL, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_TYPE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_MESSAGE_TYPE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_IMAGE_WIDTH, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_IMAGE_HEIGHT, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_DISPLAYNAME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_HEADIMGURL, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_PARENT_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_SEND_TIME, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_VOICE_TIME, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_READ_STATE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_SEND_STATE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_IS_READ_VOICE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_FAVORITE_COUNT, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_COMMENT_COUNT, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_IS_FAVORITE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_IS_SHIDE, COLUMN_INTEGER_TYPE);
			
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_NOTIFY_ID + "," + COLUMN_FROM_UID + "," + COLUMN_TO_ID + "," + COLUMN_LOGIN_ID + "," + COLUMN_TAG + ")";

			mSQLCreateWeiboInfoTable = SqlHelper.formCreateTableSqlString(TABLE_NAME, columnNameAndType, primary_key);
		}
		return mSQLCreateWeiboInfoTable;

	}

	public static String getDeleteTableSQLString() {
		if (null == mSQLDeleteWeiboInfoTable) {
			mSQLDeleteWeiboInfoTable = SqlHelper.formDeleteTableSqlString(TABLE_NAME);
		}  
		return mSQLDeleteWeiboInfoTable;
	}
	
	public boolean update(String notifyID, MessageInfo message){
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_ID, message.id);
		allPromotionInfoValues.put(COLUMN_IMAGE_URLS, message.imgUrlS);
		allPromotionInfoValues.put(COLUMN_IMAGE_URLL, message.imgUrlL);
		allPromotionInfoValues.put(COLUMN_VOICE_URL, message.voiceUrl);
		allPromotionInfoValues.put(COLUMN_IMAGE_WIDTH, message.imgWidth);
		allPromotionInfoValues.put(COLUMN_IMAGE_HEIGHT, message.imgHeight);
		allPromotionInfoValues.put(COLUMN_SEND_TIME, message.time);
		allPromotionInfoValues.put(COLUMN_VOICE_TIME, message.voiceTime);
		allPromotionInfoValues.put(COLUMN_SEND_STATE, message.sendState);
		allPromotionInfoValues.put(COLUMN_READ_STATE, message.readState);
		allPromotionInfoValues.put(COLUMN_PARENT_ID, message.parentid);
		allPromotionInfoValues.put(COLUMN_IS_READ_VOICE, message.isReadVoice);
		allPromotionInfoValues.put(COLUMN_FAVORITE_COUNT, message.favoriteCount);
		allPromotionInfoValues.put(COLUMN_COMMENT_COUNT, message.commentCount);
		allPromotionInfoValues.put(COLUMN_IS_FAVORITE, message.isfavorite);
		allPromotionInfoValues.put(COLUMN_IS_SHIDE, message.mIsShide);
		
		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_NOTIFY_ID + "='" + notifyID + "' AND " + COLUMN_FROM_UID + " = '" + message.from + "' AND " + COLUMN_TO_ID + "='" + message.to + "' AND " + COLUMN_TAG + "='" + message.tag + "' AND " + COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}	
		
		return false;
	}
	
	public void insert(String notifyID, MessageInfo message) {
		if(message.id == null || message.id.equals("")){
			return;
		}
		Cursor cursor = null;
		
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_NOTIFY_ID, notifyID);
		allPromotionInfoValues.put(COLUMN_FROM_UID, message.from);
		allPromotionInfoValues.put(COLUMN_LOGIN_ID, DamiCommon.getUid(DamiApp.getInstance()));
		allPromotionInfoValues.put(COLUMN_TO_ID, message.to);
		allPromotionInfoValues.put(COLUMN_ID, message.id);
		allPromotionInfoValues.put(COLUMN_TAG, message.tag);
		allPromotionInfoValues.put(COLUMN_CONTENT, message.content);
		allPromotionInfoValues.put(COLUMN_IMAGE_URLS, message.imgUrlS);
		allPromotionInfoValues.put(COLUMN_IMAGE_URLL, message.imgUrlL);
		allPromotionInfoValues.put(COLUMN_VOICE_URL, message.voiceUrl);
		allPromotionInfoValues.put(COLUMN_TYPE, message.type);
		allPromotionInfoValues.put(COLUMN_MESSAGE_TYPE, message.type);
		allPromotionInfoValues.put(COLUMN_IMAGE_WIDTH, message.imgWidth);
		allPromotionInfoValues.put(COLUMN_IMAGE_HEIGHT, message.imgHeight);
		allPromotionInfoValues.put(COLUMN_DISPLAYNAME, message.displayname);
		allPromotionInfoValues.put(COLUMN_HEADIMGURL, message.headImgUrl);
		allPromotionInfoValues.put(COLUMN_PARENT_ID, message.parentid);
		allPromotionInfoValues.put(COLUMN_SEND_TIME, message.time);
		allPromotionInfoValues.put(COLUMN_VOICE_TIME, message.voiceTime);
		allPromotionInfoValues.put(COLUMN_READ_STATE, message.readState);
		allPromotionInfoValues.put(COLUMN_SEND_STATE, message.sendState);
		allPromotionInfoValues.put(COLUMN_IS_READ_VOICE, message.isReadVoice);
		allPromotionInfoValues.put(COLUMN_FAVORITE_COUNT, message.favoriteCount);
		allPromotionInfoValues.put(COLUMN_COMMENT_COUNT, message.commentCount);
		allPromotionInfoValues.put(COLUMN_IS_FAVORITE, message.isfavorite);
		allPromotionInfoValues.put(COLUMN_IS_SHIDE, message.mIsShide);
		
		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}	
	}
	
	public void delete(String notifyID, MessageInfo message) {
		if(message.id == null){
			return;
		}
		mDBStore.delete(TABLE_NAME, COLUMN_NOTIFY_ID + "='" + notifyID + "' AND " + COLUMN_ID + "='" + message.id + "' AND " + COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
	}
	
	public MessageInfo query(String notifyID, String id){
		MessageInfo message = new MessageInfo();
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NOTIFY_ID + "='" + notifyID + "' AND " + COLUMN_ID + "='" + id + "' AND " + COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
			if (cursor != null) {
				
				if (!cursor.moveToFirst()) {
					return null;
				}
				
				int indexFromId = cursor.getColumnIndex(COLUMN_FROM_UID);
				int indexToID = cursor.getColumnIndex(COLUMN_TO_ID);
				int indexMessageId = cursor.getColumnIndex(COLUMN_ID);
				int indexMessageTag = cursor.getColumnIndex(COLUMN_TAG);
				int indexContent = cursor.getColumnIndex(COLUMN_CONTENT);
				int indexImgUrls = cursor.getColumnIndex(COLUMN_IMAGE_URLS);
				int indexImgUrlL = cursor.getColumnIndex(COLUMN_IMAGE_URLL);
				int indexVoiceUrl = cursor.getColumnIndex(COLUMN_VOICE_URL);
				int indexType = cursor.getColumnIndex(COLUMN_TYPE);
				int indexMessageType = cursor.getColumnIndex(COLUMN_MESSAGE_TYPE);
				int indexImgWidth = cursor.getColumnIndex(COLUMN_IMAGE_WIDTH);
				int indexImgHeight = cursor.getColumnIndex(COLUMN_IMAGE_HEIGHT);
				int indexDisplayName = cursor.getColumnIndex(COLUMN_DISPLAYNAME);
				int indexHeadImgUrl = cursor.getColumnIndex(COLUMN_HEADIMGURL);
				int indexParentId = cursor.getColumnIndex(COLUMN_PARENT_ID);
				int indexSendTime = cursor.getColumnIndex(COLUMN_SEND_TIME);
				int indexVoiceTime = cursor.getColumnIndex(COLUMN_VOICE_TIME);
				int indexReadState = cursor.getColumnIndex(COLUMN_READ_STATE);
				int indexSendState = cursor.getColumnIndex(COLUMN_SEND_STATE);
				int indexVoiceReadState = cursor.getColumnIndex(COLUMN_IS_READ_VOICE);
				int indexFavoriteCount = cursor.getColumnIndex(COLUMN_FAVORITE_COUNT);
				int indexCommentCount = cursor.getColumnIndex(COLUMN_COMMENT_COUNT);
				int indexIsFavorite = cursor.getColumnIndex(COLUMN_IS_FAVORITE);
				int indexIsShide = cursor.getColumnIndex(COLUMN_IS_SHIDE);
				
				message.from = cursor.getString(indexFromId);
				message.to = cursor.getString(indexToID);
				message.id = cursor.getString(indexMessageId);
				message.tag = cursor.getString(indexMessageTag);
				message.content = cursor.getString(indexContent);
				message.imgUrlS = cursor.getString(indexImgUrls);
				message.imgUrlL = cursor.getString(indexImgUrlL);
				message.voiceUrl = cursor.getString(indexVoiceUrl);
				message.type = cursor.getInt(indexType);
				message.type = cursor.getInt(indexMessageType);
				message.imgWidth = cursor.getInt(indexImgWidth);
				message.imgHeight = cursor.getInt(indexImgHeight);
				message.displayname = cursor.getString(indexDisplayName);
				message.headImgUrl = cursor.getString(indexHeadImgUrl);
				message.parentid = cursor.getString(indexParentId);
				
				message.time = cursor.getLong(indexSendTime);
				message.voiceTime = cursor.getInt(indexVoiceTime);
				message.readState = cursor.getInt(indexReadState);
				message.sendState = cursor.getInt(indexSendState);
				message.isReadVoice = cursor.getInt(indexVoiceReadState);
				message.favoriteCount = cursor.getInt(indexFavoriteCount);
				message.commentCount = cursor.getInt(indexCommentCount);
				message.isfavorite = cursor.getInt(indexIsFavorite);
				message.mIsShide = cursor.getInt(indexIsShide);
				
				return message;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}
	
}
