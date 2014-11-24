package com.gaopai.guiren.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.MessageType;
import com.gaopai.guiren.utils.Logger;

public class MessageTable {

	public static final String TABLE_NAME = "MessageTable";// 数据表的名称

	/**
	 * content, fromId, toId, sessionId, pullTime, sendTime, voiceTime, type,
	 * readState, sendState, sendType, currentUser, primary key(fromId, toId,
	 * sendTime, currentUser)
	 */
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
	public static final String COLUMN_AGREE_COUNT = "agreeCount";
	public static final String COLUMN_IS_FAVORITE = "isFavorite";
	public static final String COLUMN_IS_AGREE = "isAgree";
	public static final String COLUMN_IS_SHIDE = "isShide";
	public static final String COLUMN_URL = "url";
	public static final String COLUMN_SAMPLE_RATE = "sampleRate";

	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String PRIMARY_KEY_TYPE = "primary key(";

	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;

	public MessageTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}

	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {

			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
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
			columnNameAndType.put(COLUMN_AGREE_COUNT, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_IS_FAVORITE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_IS_AGREE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_IS_SHIDE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_URL, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_SAMPLE_RATE, COLUMN_INTEGER_TYPE);

			String primary_key = PRIMARY_KEY_TYPE + COLUMN_FROM_UID + ","
					+ COLUMN_TO_ID + "," + COLUMN_LOGIN_ID + "," + COLUMN_TAG
					+ ")";

			mSQLCreateWeiboInfoTable = SqlHelper.formCreateTableSqlString(
					TABLE_NAME, columnNameAndType, primary_key);
		}
		return mSQLCreateWeiboInfoTable;

	}

	public static String getDeleteTableSQLString() {
		if (null == mSQLDeleteWeiboInfoTable) {
			mSQLDeleteWeiboInfoTable = SqlHelper
					.formDeleteTableSqlString(TABLE_NAME);
		}
		return mSQLDeleteWeiboInfoTable;
	}

	public void insert(List<MessageInfo> messages) {
		List<MessageInfo> messageList = new ArrayList<MessageInfo>();
		messageList.addAll(messages);
		mDBStore.beginTransaction();
		try {
			for (MessageInfo message : messageList) {
				ContentValues allPromotionInfoValues = new ContentValues();

				allPromotionInfoValues.put(COLUMN_FROM_UID, message.from);
				allPromotionInfoValues.put(COLUMN_LOGIN_ID,
						DamiCommon.getUid(DamiApp.getInstance()));
				allPromotionInfoValues.put(COLUMN_TO_ID, message.to);
				allPromotionInfoValues.put(COLUMN_ID, message.id);
				allPromotionInfoValues.put(COLUMN_TAG, message.tag);
				allPromotionInfoValues.put(COLUMN_CONTENT, message.content);
				allPromotionInfoValues.put(COLUMN_IMAGE_URLS, message.imgUrlS);
				allPromotionInfoValues.put(COLUMN_IMAGE_URLL, message.imgUrlL);
				allPromotionInfoValues.put(COLUMN_VOICE_URL, message.voiceUrl);
				allPromotionInfoValues.put(COLUMN_TYPE, message.type);
				allPromotionInfoValues.put(COLUMN_MESSAGE_TYPE,
						message.fileType);
				allPromotionInfoValues
						.put(COLUMN_IMAGE_WIDTH, message.imgWidth);
				allPromotionInfoValues.put(COLUMN_IMAGE_HEIGHT,
						message.imgHeight);
				allPromotionInfoValues.put(COLUMN_DISPLAYNAME,
						message.displayname);
				allPromotionInfoValues.put(COLUMN_HEADIMGURL,
						message.headImgUrl);
				allPromotionInfoValues.put(COLUMN_PARENT_ID, message.parentid);
				allPromotionInfoValues.put(COLUMN_SEND_TIME, message.time);
				allPromotionInfoValues
						.put(COLUMN_VOICE_TIME, message.voiceTime);
				allPromotionInfoValues
						.put(COLUMN_READ_STATE, message.readState);
				allPromotionInfoValues
						.put(COLUMN_SEND_STATE, message.sendState);
				allPromotionInfoValues.put(COLUMN_IS_READ_VOICE,
						message.isReadVoice);
				allPromotionInfoValues.put(COLUMN_FAVORITE_COUNT,
						message.favoriteCount);
				allPromotionInfoValues.put(COLUMN_COMMENT_COUNT,
						message.commentCount);
				allPromotionInfoValues.put(COLUMN_AGREE_COUNT,
						message.agreeCount);
				allPromotionInfoValues.put(COLUMN_IS_FAVORITE,
						message.isfavorite);
				allPromotionInfoValues.put(COLUMN_IS_AGREE, message.isAgree);
				allPromotionInfoValues.put(COLUMN_IS_SHIDE, message.mIsShide);
				allPromotionInfoValues.put(COLUMN_URL, message.url);
				allPromotionInfoValues.put(COLUMN_SAMPLE_RATE,
						message.samplerate);

				try {
					mDBStore.insertOrThrow(TABLE_NAME, null,
							allPromotionInfoValues);
				} catch (SQLiteConstraintException e) {
					e.printStackTrace();
				}
			}
			mDBStore.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			mDBStore.endTransaction();
		}

	}

	public void insert(MessageInfo message) {

		Cursor cursor = null;

		ContentValues allPromotionInfoValues = new ContentValues();

		allPromotionInfoValues.put(COLUMN_FROM_UID, message.from);
		allPromotionInfoValues.put(COLUMN_LOGIN_ID,
				DamiCommon.getUid(DamiApp.getInstance()));
		allPromotionInfoValues.put(COLUMN_TO_ID, message.to);
		allPromotionInfoValues.put(COLUMN_ID, message.id);
		allPromotionInfoValues.put(COLUMN_TAG, message.tag);
		allPromotionInfoValues.put(COLUMN_CONTENT, message.content);
		allPromotionInfoValues.put(COLUMN_IMAGE_URLS, message.imgUrlS);
		allPromotionInfoValues.put(COLUMN_IMAGE_URLL, message.imgUrlL);
		allPromotionInfoValues.put(COLUMN_VOICE_URL, message.voiceUrl);
		allPromotionInfoValues.put(COLUMN_TYPE, message.type);
		allPromotionInfoValues.put(COLUMN_MESSAGE_TYPE, message.fileType);
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
		allPromotionInfoValues
				.put(COLUMN_FAVORITE_COUNT, message.favoriteCount);
		allPromotionInfoValues.put(COLUMN_COMMENT_COUNT, message.commentCount);
		allPromotionInfoValues.put(COLUMN_AGREE_COUNT, message.agreeCount);
		allPromotionInfoValues.put(COLUMN_IS_FAVORITE, message.favoriteCount);
		allPromotionInfoValues.put(COLUMN_IS_AGREE, message.isAgree);
		allPromotionInfoValues.put(COLUMN_IS_SHIDE, message.mIsShide);
		allPromotionInfoValues.put(COLUMN_URL, message.url);
		allPromotionInfoValues.put(COLUMN_SAMPLE_RATE, message.samplerate);

		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

	}

	public void delete(MessageInfo message) {
		if (!TextUtils.isEmpty(message.id)) {
			deleteComment(message.id);
		}
		mDBStore.delete(
				TABLE_NAME,
				COLUMN_TAG + "='" + message.tag + "' AND " + COLUMN_LOGIN_ID
						+ "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'",
				null);
	}

	public void deleteMore(String tribeIDs, String meetingIDs) {
		try {
			String execSql = "";
			String execSql2 = "";
			if (!TextUtils.isEmpty(tribeIDs) && !TextUtils.isEmpty(meetingIDs)) {
				execSql = "DELETE FROM " + TABLE_NAME + " WHERE ("
						+ COLUMN_TO_ID + " NOT IN (" + tribeIDs + ") AND "
						+ COLUMN_TYPE + "=200)" + " OR (" + COLUMN_TO_ID
						+ " NOT IN (" + meetingIDs + ") AND " + COLUMN_TYPE
						+ "=300)" + " AND " + COLUMN_LOGIN_ID + "='"
						+ DamiCommon.getUid(DamiApp.getInstance()) + "'";
			} else if (!TextUtils.isEmpty(tribeIDs)) {
				execSql = "DELETE FROM " + TABLE_NAME + " WHERE "
						+ COLUMN_TO_ID + " NOT IN (" + tribeIDs + ") AND "
						+ COLUMN_TYPE + "=200" + " AND " + COLUMN_LOGIN_ID
						+ "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'";
				execSql2 = "DELETE FROM " + TABLE_NAME + "WHERE " + COLUMN_TYPE
						+ "=300 AND " + COLUMN_LOGIN_ID + "='"
						+ DamiCommon.getUid(DamiApp.getInstance()) + "'";
			} else if (!TextUtils.isEmpty(meetingIDs)) {
				execSql = "DELETE FROM " + TABLE_NAME + " WHERE "
						+ COLUMN_TO_ID + " NOT IN (" + meetingIDs + ") AND "
						+ COLUMN_TYPE + "=300" + " AND " + COLUMN_LOGIN_ID
						+ "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'";
				execSql2 = "DELETE FROM " + TABLE_NAME + "WHERE " + COLUMN_TYPE
						+ "=200 AND " + COLUMN_LOGIN_ID + "='"
						+ DamiCommon.getUid(DamiApp.getInstance()) + "'";
			} else {
				execSql = "DELETE FROM " + TABLE_NAME + "WHERE " + COLUMN_TYPE
						+ "=200 AND " + COLUMN_LOGIN_ID + "='"
						+ DamiCommon.getUid(DamiApp.getInstance()) + "'";
				execSql2 = "DELETE FROM " + TABLE_NAME + "WHERE " + COLUMN_TYPE
						+ "=300 AND " + COLUMN_LOGIN_ID + "='"
						+ DamiCommon.getUid(DamiApp.getInstance()) + "'";
			}

			if (!TextUtils.isEmpty(execSql)) {
				mDBStore.execSQL(execSql);
			}

			if (!TextUtils.isEmpty(execSql2)) {
				mDBStore.execSQL(execSql2);
			}
		} catch (Exception e) {
		}

	}

	public void deleteRecord(String toID) {
		try {
			String execSql = "DELETE FROM " + TABLE_NAME + " WHERE "
					+ COLUMN_TO_ID + "='" + toID + "' AND " + COLUMN_LOGIN_ID
					+ "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'";
			mDBStore.execSQL(execSql);
		} catch (Exception e) {
		}

	}

	public void deleteComment(String id) {
		try {
			String execSql = "DELETE FROM " + TABLE_NAME + " WHERE "
					+ COLUMN_PARENT_ID + "='" + id + "' AND " + COLUMN_LOGIN_ID
					+ "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'";
			mDBStore.execSQL(execSql);
		} catch (Exception e) {
		}
	}
	
	//delete meeting or tribe
	public boolean deleteTribe(String toId) {
		if (delete(toId, 1)) {
			return true;
		}
		return false;
	}

	public boolean delete(String toId, int isRoom) {
		try {
			if (isRoom == 1) {
				mDBStore.delete(
						TABLE_NAME,
						COLUMN_TO_ID + "='" + toId + "'" + " AND "
								+ COLUMN_LOGIN_ID + "='"
								+ DamiCommon.getUid(DamiApp.getInstance())
								+ "'", null);
			} else {
				mDBStore.delete(
						TABLE_NAME,
						COLUMN_FROM_UID + "='" + toId + "' or " + COLUMN_TO_ID
								+ "='" + toId + "'" + " AND " + COLUMN_LOGIN_ID
								+ "='"
								+ DamiCommon.getUid(DamiApp.getInstance())
								+ "'", null);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean updateMessage(MessageInfo message) {
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
		allPromotionInfoValues
				.put(COLUMN_FAVORITE_COUNT, message.favoriteCount);
		allPromotionInfoValues.put(COLUMN_COMMENT_COUNT, message.commentCount);
		allPromotionInfoValues.put(COLUMN_AGREE_COUNT, message.agreeCount);
		allPromotionInfoValues.put(COLUMN_IS_FAVORITE, message.isfavorite);
		allPromotionInfoValues.put(COLUMN_IS_AGREE, message.isAgree);
		allPromotionInfoValues.put(COLUMN_IS_SHIDE, message.mIsShide);
		allPromotionInfoValues.put(COLUMN_DISPLAYNAME, message.displayname);
		allPromotionInfoValues.put(COLUMN_HEADIMGURL, message.headImgUrl);

		try {
			mDBStore.update(
					TABLE_NAME,
					allPromotionInfoValues,
					COLUMN_FROM_UID + " = '" + message.from + "' AND "
							+ COLUMN_TO_ID + "='" + message.to + "' AND "
							+ COLUMN_TAG + "='" + message.tag + "' AND "
							+ COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'",
					null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean update(MessageInfo message) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_SEND_STATE, message.sendState);
		allPromotionInfoValues.put(COLUMN_READ_STATE, message.readState);
		allPromotionInfoValues.put(COLUMN_IS_READ_VOICE, message.isReadVoice);
		allPromotionInfoValues
				.put(COLUMN_FAVORITE_COUNT, message.favoriteCount);
		allPromotionInfoValues.put(COLUMN_COMMENT_COUNT, message.commentCount);
		allPromotionInfoValues.put(COLUMN_AGREE_COUNT, message.agreeCount);
		allPromotionInfoValues.put(COLUMN_IS_AGREE, message.isAgree);
		allPromotionInfoValues.put(COLUMN_IS_FAVORITE, message.isfavorite);
		allPromotionInfoValues.put(COLUMN_IS_SHIDE, message.mIsShide);
		allPromotionInfoValues.put(COLUMN_DISPLAYNAME, message.displayname);
		allPromotionInfoValues.put(COLUMN_HEADIMGURL, message.headImgUrl);

		try {
			mDBStore.update(
					TABLE_NAME,
					allPromotionInfoValues,
					COLUMN_FROM_UID + " = '" + message.from + "' AND "
							+ COLUMN_TO_ID + "='" + message.to + "' AND "
							+ COLUMN_TAG + "='" + message.tag + "' AND "
							+ COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'",
					null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean updateVoiceContent(String tag, String content) {

		try {
			String sql = "UPDATE " + TABLE_NAME + " SET " + COLUMN_CONTENT
					+ "='" + content + "' WHERE " + COLUMN_TAG + "='" + tag
					+ "' AND " + COLUMN_LOGIN_ID + "='"
					+ DamiCommon.getUid(DamiApp.getInstance()) + "'";
			mDBStore.execSQL(sql);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean updateReadState(String id) {

		try {
			String sql = "UPDATE " + TABLE_NAME + " SET " + COLUMN_READ_STATE
					+ "=1 WHERE " + COLUMN_TO_ID + "='" + id + "' AND "
					+ COLUMN_LOGIN_ID + "='"
					+ DamiCommon.getUid(DamiApp.getInstance()) + "'";
			mDBStore.execSQL(sql);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean updatePrivateReadState(String id) {

		try {
			String sql = "UPDATE "
					+ TABLE_NAME
					+ " SET "
					+ COLUMN_READ_STATE
					+ "=1 WHERE "
					+ (COLUMN_FROM_UID + "='" + id + "' OR " + COLUMN_TO_ID
							+ "='" + id) + "' AND " + COLUMN_LOGIN_ID + "='"
					+ DamiCommon.getUid(DamiApp.getInstance()) + "'";
			mDBStore.execSQL(sql);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean updateFavoriteCount(MessageInfo message) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues
				.put(COLUMN_FAVORITE_COUNT, message.favoriteCount);

		try {
			mDBStore.update(
					TABLE_NAME,
					allPromotionInfoValues,
					COLUMN_FROM_UID + " = '" + message.from + "' AND "
							+ COLUMN_TO_ID + "='" + message.to + "' AND "
							+ COLUMN_TAG + "='" + message.tag + "' AND "
							+ COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'",
					null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean updateCommentCount(MessageInfo message) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_COMMENT_COUNT, message.commentCount);

		try {
			mDBStore.update(
					TABLE_NAME,
					allPromotionInfoValues,
					COLUMN_FROM_UID + " = '" + message.from + "' AND "
							+ COLUMN_TO_ID + "='" + message.to + "' AND "
							+ COLUMN_TAG + "='" + message.tag + "' AND "
							+ COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'",
					null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean updateAgreeCount(MessageInfo message) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_AGREE_COUNT, message.agreeCount);
		allPromotionInfoValues.put(COLUMN_IS_AGREE, message.isAgree);
		try {
			mDBStore.update(
					TABLE_NAME,
					allPromotionInfoValues,
					COLUMN_FROM_UID + " = '" + message.from + "' AND "
							+ COLUMN_TO_ID + "='" + message.to + "' AND "
							+ COLUMN_TAG + "='" + message.tag + "' AND "
							+ COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'",
					null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean updateShide(MessageInfo message) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_IS_SHIDE, message.mIsShide);
		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_TAG
					+ "='" + message.tag + "' AND " + COLUMN_LOGIN_ID + "='"
					+ DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}

		return false;
	}

	public MessageInfo query(String tag) {
		MessageInfo message = new MessageInfo();
		Cursor cursor = null;
		try {
			String querySql = "";
			querySql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TAG
					+ "='" + tag + "'";
			cursor = mDBStore.rawQuery(querySql, null);
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
				int indexMessageType = cursor
						.getColumnIndex(COLUMN_MESSAGE_TYPE);
				int indexImgWidth = cursor.getColumnIndex(COLUMN_IMAGE_WIDTH);
				int indexImgHeight = cursor.getColumnIndex(COLUMN_IMAGE_HEIGHT);
				int indexDisplayName = cursor
						.getColumnIndex(COLUMN_DISPLAYNAME);
				int indexHeadImgUrl = cursor.getColumnIndex(COLUMN_HEADIMGURL);
				int indexParentId = cursor.getColumnIndex(COLUMN_PARENT_ID);
				int indexSendTime = cursor.getColumnIndex(COLUMN_SEND_TIME);
				int indexVoiceTime = cursor.getColumnIndex(COLUMN_VOICE_TIME);
				int indexReadState = cursor.getColumnIndex(COLUMN_READ_STATE);
				int indexSendState = cursor.getColumnIndex(COLUMN_SEND_STATE);
				int indexVoiceReadState = cursor
						.getColumnIndex(COLUMN_IS_READ_VOICE);
				int indexFavoriteCount = cursor
						.getColumnIndex(COLUMN_FAVORITE_COUNT);
				int indexCommentCount = cursor
						.getColumnIndex(COLUMN_COMMENT_COUNT);
				int indexAgreeCount = cursor.getColumnIndex(COLUMN_AGREE_COUNT);
				int indexIsAgree = cursor.getColumnIndex(COLUMN_IS_AGREE);
				int indexIsFavorite = cursor.getColumnIndex(COLUMN_IS_FAVORITE);
				int indexIsShide = cursor.getColumnIndex(COLUMN_IS_SHIDE);
				int indexURL = cursor.getColumnIndex(COLUMN_URL);
				int indexSampleRate = cursor.getColumnIndex(COLUMN_SAMPLE_RATE);

				message.from = cursor.getString(indexFromId);
				message.to = cursor.getString(indexToID);
				message.id = cursor.getString(indexMessageId);
				message.tag = cursor.getString(indexMessageTag);
				message.content = cursor.getString(indexContent);
				message.imgUrlS = cursor.getString(indexImgUrls);
				message.imgUrlL = cursor.getString(indexImgUrlL);
				message.voiceUrl = cursor.getString(indexVoiceUrl);
				message.type = cursor.getInt(indexType);
				message.fileType = cursor.getInt(indexMessageType);
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
				message.agreeCount = cursor.getInt(indexAgreeCount);
				message.isfavorite = cursor.getInt(indexIsFavorite);
				message.isAgree = cursor.getInt(indexIsAgree);
				message.mIsShide = cursor.getInt(indexIsShide);
				message.url = cursor.getString(indexURL);
				message.samplerate = cursor.getInt(indexSampleRate);
				if (message.samplerate == 0) {
					message.samplerate = 8000;
				}
				return message;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;

	}

	public MessageInfo queryByID(String id) {
		MessageInfo message = new MessageInfo();
		Cursor cursor = null;
		try {
			String querySql = "";
			querySql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID
					+ "='" + id + "'";
			cursor = mDBStore.rawQuery(querySql, null);
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
				int indexMessageType = cursor
						.getColumnIndex(COLUMN_MESSAGE_TYPE);
				int indexImgWidth = cursor.getColumnIndex(COLUMN_IMAGE_WIDTH);
				int indexImgHeight = cursor.getColumnIndex(COLUMN_IMAGE_HEIGHT);
				int indexDisplayName = cursor
						.getColumnIndex(COLUMN_DISPLAYNAME);
				int indexHeadImgUrl = cursor.getColumnIndex(COLUMN_HEADIMGURL);
				int indexParentId = cursor.getColumnIndex(COLUMN_PARENT_ID);
				int indexSendTime = cursor.getColumnIndex(COLUMN_SEND_TIME);
				int indexVoiceTime = cursor.getColumnIndex(COLUMN_VOICE_TIME);
				int indexReadState = cursor.getColumnIndex(COLUMN_READ_STATE);
				int indexSendState = cursor.getColumnIndex(COLUMN_SEND_STATE);
				int indexVoiceReadState = cursor
						.getColumnIndex(COLUMN_IS_READ_VOICE);
				int indexFavoriteCount = cursor
						.getColumnIndex(COLUMN_FAVORITE_COUNT);
				int indexCommentCount = cursor
						.getColumnIndex(COLUMN_COMMENT_COUNT);
				int indexAgreeCount = cursor.getColumnIndex(COLUMN_AGREE_COUNT);
				int indexIsAgree = cursor.getColumnIndex(COLUMN_IS_AGREE);
				int indexIsFavorite = cursor.getColumnIndex(COLUMN_IS_FAVORITE);
				int indexIsShide = cursor.getColumnIndex(COLUMN_IS_SHIDE);
				int indexURL = cursor.getColumnIndex(COLUMN_URL);
				int indexSampleRate = cursor.getColumnIndex(COLUMN_SAMPLE_RATE);

				message.from = cursor.getString(indexFromId);
				message.to = cursor.getString(indexToID);
				message.id = cursor.getString(indexMessageId);
				message.tag = cursor.getString(indexMessageTag);
				message.content = cursor.getString(indexContent);
				message.imgUrlS = cursor.getString(indexImgUrls);
				message.imgUrlL = cursor.getString(indexImgUrlL);
				message.voiceUrl = cursor.getString(indexVoiceUrl);
				message.type = cursor.getInt(indexType);
				message.fileType = cursor.getInt(indexMessageType);
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
				message.agreeCount = cursor.getInt(indexAgreeCount);
				message.isfavorite = cursor.getInt(indexIsFavorite);
				message.isAgree = cursor.getInt(indexIsAgree);
				message.mIsShide = cursor.getInt(indexIsShide);
				message.url = cursor.getString(indexURL);
				message.samplerate = cursor.getInt(indexSampleRate);
				if (message.samplerate == 0) {
					message.samplerate = 8000;
				}
				return message;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;

	}

	public List<MessageInfo> query(String toId, int autoID, int type) {
		List<MessageInfo> allInfo = new ArrayList<MessageInfo>();
		Cursor cursor = null;

		mDBStore.beginTransaction();
		try {

			String querySql = "";

			if (type == 100) {
				if (autoID == -1) {
					querySql = "SELECT rowid, * FROM " + TABLE_NAME
							+ " WHERE (" + COLUMN_FROM_UID + "='" + toId
							+ "' or " + COLUMN_TO_ID + "='" + toId + "')"
							+ " AND " + COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'"
							+ " AND " + COLUMN_TYPE + "=" + type
							+ " ORDER BY sendTime" + " DESC LIMIT 0,"
							+ DamiCommon.LOAD_SIZE;
				} else {
					querySql = "SELECT rowid, * FROM " + TABLE_NAME
							+ " WHERE (" + COLUMN_FROM_UID + "='" + toId
							+ "' or " + COLUMN_TO_ID + "='" + toId + "')"
							+ " AND " + COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'"
							+ " AND " + " rowid<" + autoID + " AND "
							+ COLUMN_TYPE + "=" + type + " ORDER BY sendTime"
							+ " DESC LIMIT 0," + DamiCommon.LOAD_SIZE;
				}

			} else {
				if (autoID == -1) {
					querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE "
							+ COLUMN_TO_ID + "='" + toId + "' AND "
							+ COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'"
							+ " AND " + COLUMN_PARENT_ID + "='0'" + " AND "
							+ COLUMN_TYPE + "=" + type + " ORDER BY sendTime"
							+ " DESC LIMIT 0," + DamiCommon.LOAD_SIZE;
				} else {
					querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE "
							+ COLUMN_TO_ID + "='" + toId + "' AND "
							+ COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'"
							+ " AND " + COLUMN_PARENT_ID + "='0'" + " AND "
							+ " rowid<" + autoID + " AND " + COLUMN_TYPE + "="
							+ type + " ORDER BY sendTime" + " DESC LIMIT 0,"
							+ DamiCommon.LOAD_SIZE;
				}

			}

			cursor = mDBStore.rawQuery(querySql, null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return allInfo;
				}

				int indexRowId = cursor.getColumnIndex("rowid");
				int indexFromId = cursor.getColumnIndex(COLUMN_FROM_UID);
				int indexToID = cursor.getColumnIndex(COLUMN_TO_ID);
				int indexMessageId = cursor.getColumnIndex(COLUMN_ID);
				int indexMessageTag = cursor.getColumnIndex(COLUMN_TAG);
				int indexContent = cursor.getColumnIndex(COLUMN_CONTENT);
				int indexImgUrls = cursor.getColumnIndex(COLUMN_IMAGE_URLS);
				int indexImgUrlL = cursor.getColumnIndex(COLUMN_IMAGE_URLL);
				int indexVoiceUrl = cursor.getColumnIndex(COLUMN_VOICE_URL);
				int indexType = cursor.getColumnIndex(COLUMN_TYPE);
				int indexMessageType = cursor
						.getColumnIndex(COLUMN_MESSAGE_TYPE);
				int indexImgWidth = cursor.getColumnIndex(COLUMN_IMAGE_WIDTH);
				int indexImgHeight = cursor.getColumnIndex(COLUMN_IMAGE_HEIGHT);
				int indexDisplayName = cursor
						.getColumnIndex(COLUMN_DISPLAYNAME);
				int indexHeadImgUrl = cursor.getColumnIndex(COLUMN_HEADIMGURL);
				int indexParentId = cursor.getColumnIndex(COLUMN_PARENT_ID);
				int indexSendTime = cursor.getColumnIndex(COLUMN_SEND_TIME);
				int indexVoiceTime = cursor.getColumnIndex(COLUMN_VOICE_TIME);
				int indexReadState = cursor.getColumnIndex(COLUMN_READ_STATE);
				int indexSendState = cursor.getColumnIndex(COLUMN_SEND_STATE);
				int indexVoiceReadState = cursor
						.getColumnIndex(COLUMN_IS_READ_VOICE);
				int indexFavoriteCount = cursor
						.getColumnIndex(COLUMN_FAVORITE_COUNT);
				int indexCommentCount = cursor
						.getColumnIndex(COLUMN_COMMENT_COUNT);
				int indexAgreeCount = cursor.getColumnIndex(COLUMN_AGREE_COUNT);
				int indexIsAgree = cursor.getColumnIndex(COLUMN_IS_AGREE);
				int indexIsFavorite = cursor.getColumnIndex(COLUMN_IS_FAVORITE);
				int indexIsShide = cursor.getColumnIndex(COLUMN_IS_SHIDE);
				int indexURL = cursor.getColumnIndex(COLUMN_URL);
				int indexSampleRate = cursor.getColumnIndex(COLUMN_SAMPLE_RATE);
				do {
					MessageInfo message = new MessageInfo();
					message.from = cursor.getString(indexFromId);
					message.to = cursor.getString(indexToID);
					message.id = cursor.getString(indexMessageId);
					message.tag = cursor.getString(indexMessageTag);
					message.content = cursor.getString(indexContent);
					message.imgUrlS = cursor.getString(indexImgUrls);
					message.imgUrlL = cursor.getString(indexImgUrlL);
					message.voiceUrl = cursor.getString(indexVoiceUrl);
					message.type = cursor.getInt(indexType);
					message.fileType = cursor.getInt(indexMessageType);
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
					message.agreeCount = cursor.getInt(indexAgreeCount);
					message.isfavorite = cursor.getInt(indexIsFavorite);
					message.isAgree = cursor.getInt(indexIsAgree);
					message.mIsShide = cursor.getInt(indexIsShide);
					message.url = cursor.getString(indexURL);

					message.auto_id = cursor.getInt(indexRowId);
					if (!TextUtils.isEmpty(message.id)) {
						message.comment = queryCommentList(message.id);
					}
					message.samplerate = cursor.getInt(indexSampleRate);
					if (message.samplerate == 0) {
						message.samplerate = 8000;
					}
					allInfo.add(0, message);
				} while (cursor.moveToNext());

			}
			mDBStore.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			mDBStore.endTransaction();
		}
		return allInfo;
	}
	
	//only delete voice 
	//type = 200部落  300会议
	public List<MessageInfo> queryDeleteMessageInfos(String toId, int type) {
		List<MessageInfo> allInfo = new ArrayList<MessageInfo>();
		Cursor cursor = null;
		
		mDBStore.beginTransaction();
		try {
			
			String querySql = "";
			
			if (type == 100) {
				querySql = "SELECT rowid, * FROM " + TABLE_NAME
						+ " WHERE (" + COLUMN_FROM_UID + "='" + toId
						+ "' or " + COLUMN_TO_ID + "='" + toId + "')"
						+ " AND " + COLUMN_LOGIN_ID + "='"
						+ DamiCommon.getUid(DamiApp.getInstance()) + "'"
						+ " AND " + COLUMN_TYPE + "=" + type
						+ " ORDER BY sendTime" + " DESC LIMIT 0,"
						+ DamiCommon.LOAD_SIZE;
				
			} else {
				querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE "
						+ COLUMN_TO_ID + "='" + toId + "' AND "
						+ COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'" + " AND " 
						+ COLUMN_TYPE + "=" + type +  " AND "
						+ COLUMN_MESSAGE_TYPE + "=" + MessageType.VOICE;
			}
			cursor = mDBStore.rawQuery(querySql, null);
			if (!cursor.moveToFirst()) {
				Logger.d(this, "4=" + cursor.getCount()+"");
			}
			if (cursor != null) {
				
				if (!cursor.moveToFirst()) {
					return allInfo;
				}
				
				int indexImgUrls = cursor.getColumnIndex(COLUMN_IMAGE_URLS);
				int indexImgUrlL = cursor.getColumnIndex(COLUMN_IMAGE_URLL);
				int indexVoiceUrl = cursor.getColumnIndex(COLUMN_VOICE_URL);
				int indexMessageType = cursor
						.getColumnIndex(COLUMN_MESSAGE_TYPE);
				do {
					MessageInfo message = new MessageInfo();
					message.imgUrlS = cursor.getString(indexImgUrls);
					message.imgUrlL = cursor.getString(indexImgUrlL);
					message.voiceUrl = cursor.getString(indexVoiceUrl);
					message.fileType = cursor.getInt(indexMessageType);
					allInfo.add(0, message);
				} while (cursor.moveToNext());
				
			}
			mDBStore.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			mDBStore.endTransaction();
		}
		return allInfo;
	}

	public List<MessageInfo> queryPictureMessageList(String toId, int autoID,
			int type) {
		List<MessageInfo> allInfo = new ArrayList<MessageInfo>();
		Cursor cursor = null;
		mDBStore.beginTransaction();
		try {
			String querySql = "";
			if (type == 100) {
				if (autoID == -1) {
					querySql = "SELECT rowid, * FROM " + TABLE_NAME
							+ " WHERE (" + COLUMN_FROM_UID + "='" + toId
							+ "' or " + COLUMN_TO_ID + "='" + toId + "')"
							+ " AND " + COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'"
							+ " AND " + COLUMN_TYPE + "=" + type + " AND "
							+ COLUMN_MESSAGE_TYPE + "=" + 2
							+ " ORDER BY sendTime";
				} else {
					querySql = "SELECT rowid, * FROM " + TABLE_NAME
							+ " WHERE (" + COLUMN_FROM_UID + "='" + toId
							+ "' or " + COLUMN_TO_ID + "='" + toId + "')"
							+ " AND " + COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'"
							+ " AND " + " rowid<" + autoID + " AND "
							+ COLUMN_TYPE + "=" + type + " ORDER BY sendTime"
							+ " DESC LIMIT 0," + DamiCommon.LOAD_SIZE;
				}

			} else {
				if (autoID == -1) {
					querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE "
							+ COLUMN_TO_ID + "='" + toId + "' AND "
							+ COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'"
							+ " AND " + COLUMN_PARENT_ID + "='0'" + " AND "
							+ COLUMN_TYPE + "=" + type + " ORDER BY sendTime"
							+ " DESC LIMIT 0," + DamiCommon.LOAD_SIZE;
				} else {
					querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE "
							+ COLUMN_TO_ID + "='" + toId + "' AND "
							+ COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'"
							+ " AND " + COLUMN_PARENT_ID + "='0'" + " AND "
							+ " rowid<" + autoID + " AND " + COLUMN_TYPE + "="
							+ type + " ORDER BY sendTime" + " DESC LIMIT 0,"
							+ DamiCommon.LOAD_SIZE;
				}

			}

			cursor = mDBStore.rawQuery(querySql, null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexRowId = cursor.getColumnIndex("rowid");
				int indexFromId = cursor.getColumnIndex(COLUMN_FROM_UID);
				int indexToID = cursor.getColumnIndex(COLUMN_TO_ID);
				int indexMessageId = cursor.getColumnIndex(COLUMN_ID);
				int indexMessageTag = cursor.getColumnIndex(COLUMN_TAG);
				int indexContent = cursor.getColumnIndex(COLUMN_CONTENT);
				int indexImgUrls = cursor.getColumnIndex(COLUMN_IMAGE_URLS);
				int indexImgUrlL = cursor.getColumnIndex(COLUMN_IMAGE_URLL);
				int indexVoiceUrl = cursor.getColumnIndex(COLUMN_VOICE_URL);
				int indexType = cursor.getColumnIndex(COLUMN_TYPE);
				int indexMessageType = cursor
						.getColumnIndex(COLUMN_MESSAGE_TYPE);
				int indexImgWidth = cursor.getColumnIndex(COLUMN_IMAGE_WIDTH);
				int indexImgHeight = cursor.getColumnIndex(COLUMN_IMAGE_HEIGHT);
				int indexDisplayName = cursor
						.getColumnIndex(COLUMN_DISPLAYNAME);
				int indexHeadImgUrl = cursor.getColumnIndex(COLUMN_HEADIMGURL);
				int indexParentId = cursor.getColumnIndex(COLUMN_PARENT_ID);
				int indexSendTime = cursor.getColumnIndex(COLUMN_SEND_TIME);
				int indexVoiceTime = cursor.getColumnIndex(COLUMN_VOICE_TIME);
				int indexReadState = cursor.getColumnIndex(COLUMN_READ_STATE);
				int indexSendState = cursor.getColumnIndex(COLUMN_SEND_STATE);
				int indexVoiceReadState = cursor
						.getColumnIndex(COLUMN_IS_READ_VOICE);
				int indexFavoriteCount = cursor
						.getColumnIndex(COLUMN_FAVORITE_COUNT);
				int indexCommentCount = cursor
						.getColumnIndex(COLUMN_COMMENT_COUNT);
				int indexAgreeCount = cursor.getColumnIndex(COLUMN_AGREE_COUNT);
				int indexIsAgree = cursor.getColumnIndex(COLUMN_IS_AGREE);
				int indexIsFavorite = cursor.getColumnIndex(COLUMN_IS_FAVORITE);
				int indexIsShide = cursor.getColumnIndex(COLUMN_IS_SHIDE);
				int indexURL = cursor.getColumnIndex(COLUMN_URL);
				int indexSampleRate = cursor.getColumnIndex(COLUMN_SAMPLE_RATE);
				do {
					MessageInfo message = new MessageInfo();
					message.from = cursor.getString(indexFromId);
					message.to = cursor.getString(indexToID);
					message.id = cursor.getString(indexMessageId);
					message.tag = cursor.getString(indexMessageTag);
					message.content = cursor.getString(indexContent);
					message.imgUrlS = cursor.getString(indexImgUrls);
					message.imgUrlL = cursor.getString(indexImgUrlL);
					message.voiceUrl = cursor.getString(indexVoiceUrl);
					message.type = cursor.getInt(indexType);
					message.fileType = cursor.getInt(indexMessageType);
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
					message.agreeCount = cursor.getInt(indexAgreeCount);
					message.isfavorite = cursor.getInt(indexIsFavorite);
					message.isAgree = cursor.getInt(indexIsAgree);
					message.mIsShide = cursor.getInt(indexIsShide);
					message.url = cursor.getString(indexURL);

					message.auto_id = cursor.getInt(indexRowId);
					if (!TextUtils.isEmpty(message.id)) {
						message.comment = queryCommentList(message.id);
					}
					message.samplerate = cursor.getInt(indexSampleRate);
					if (message.samplerate == 0) {
						message.samplerate = 8000;
					}
					allInfo.add(0, message);
				} while (cursor.moveToNext());

			}
			mDBStore.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			mDBStore.endTransaction();
		}
		return allInfo;
	}

	public List<MessageInfo> queryCommentList(String msgID) {
		List<MessageInfo> allInfo = new ArrayList<MessageInfo>();
		Cursor cursor = null;
		mDBStore.beginTransaction();
		try {

			String querySql = "";
			querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE "
					+ COLUMN_PARENT_ID + "='" + msgID + "' AND "
					+ COLUMN_LOGIN_ID + "='"
					+ DamiCommon.getUid(DamiApp.getInstance()) + "'"
					+ " ORDER BY rowid" + " DESC LIMIT 0,3";

			cursor = mDBStore.rawQuery(querySql, null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexRowId = cursor.getColumnIndex("rowid");
				int indexFromId = cursor.getColumnIndex(COLUMN_FROM_UID);
				int indexToID = cursor.getColumnIndex(COLUMN_TO_ID);
				int indexMessageId = cursor.getColumnIndex(COLUMN_ID);
				int indexMessageTag = cursor.getColumnIndex(COLUMN_TAG);
				int indexContent = cursor.getColumnIndex(COLUMN_CONTENT);
				int indexImgUrls = cursor.getColumnIndex(COLUMN_IMAGE_URLS);
				int indexImgUrlL = cursor.getColumnIndex(COLUMN_IMAGE_URLL);
				int indexVoiceUrl = cursor.getColumnIndex(COLUMN_VOICE_URL);
				int indexType = cursor.getColumnIndex(COLUMN_TYPE);
				int indexMessageType = cursor
						.getColumnIndex(COLUMN_MESSAGE_TYPE);
				int indexImgWidth = cursor.getColumnIndex(COLUMN_IMAGE_WIDTH);
				int indexImgHeight = cursor.getColumnIndex(COLUMN_IMAGE_HEIGHT);
				int indexDisplayName = cursor
						.getColumnIndex(COLUMN_DISPLAYNAME);
				int indexHeadImgUrl = cursor.getColumnIndex(COLUMN_HEADIMGURL);
				int indexParentId = cursor.getColumnIndex(COLUMN_PARENT_ID);
				int indexSendTime = cursor.getColumnIndex(COLUMN_SEND_TIME);
				int indexVoiceTime = cursor.getColumnIndex(COLUMN_VOICE_TIME);
				int indexReadState = cursor.getColumnIndex(COLUMN_READ_STATE);
				int indexSendState = cursor.getColumnIndex(COLUMN_SEND_STATE);
				int indexVoiceReadState = cursor
						.getColumnIndex(COLUMN_IS_READ_VOICE);
				int indexFavoriteCount = cursor
						.getColumnIndex(COLUMN_FAVORITE_COUNT);
				int indexCommentCount = cursor
						.getColumnIndex(COLUMN_COMMENT_COUNT);
				int indexAgreeCount = cursor.getColumnIndex(COLUMN_AGREE_COUNT);
				int indexIsAgree = cursor.getColumnIndex(COLUMN_IS_AGREE);
				int indexIsFavorite = cursor.getColumnIndex(COLUMN_IS_FAVORITE);
				int indexIsShide = cursor.getColumnIndex(COLUMN_IS_SHIDE);
				int indexSampleRate = cursor.getColumnIndex(COLUMN_SAMPLE_RATE);

				do {
					MessageInfo message = new MessageInfo();
					message.from = cursor.getString(indexFromId);
					message.to = cursor.getString(indexToID);
					message.id = cursor.getString(indexMessageId);
					message.tag = cursor.getString(indexMessageTag);
					message.content = cursor.getString(indexContent);
					message.imgUrlS = cursor.getString(indexImgUrls);
					message.imgUrlL = cursor.getString(indexImgUrlL);
					message.voiceUrl = cursor.getString(indexVoiceUrl);
					message.type = cursor.getInt(indexType);
					message.fileType = cursor.getInt(indexMessageType);
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
					message.agreeCount = cursor.getInt(indexAgreeCount);
					message.isfavorite = cursor.getInt(indexIsFavorite);
					message.isAgree = cursor.getInt(indexIsAgree);
					message.mIsShide = cursor.getInt(indexIsShide);
					message.samplerate = cursor.getInt(indexSampleRate);
					if (message.samplerate == 0) {
						message.samplerate = 8000;
					}
					message.auto_id = cursor.getInt(indexRowId);
					allInfo.add(message);
				} while (cursor.moveToNext());
			}
			mDBStore.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			mDBStore.endTransaction();
		}
		return allInfo;
	}

	public int queryUnreadCount(String tribeID) {
		Cursor cursor = null;
		try {

			String querySql = "";
			querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE "
					+ COLUMN_TO_ID + "='" + tribeID + "' AND "
					+ COLUMN_PARENT_ID + "='0'" + " AND " + COLUMN_READ_STATE
					+ "=0" + " AND " + COLUMN_LOGIN_ID + "='"
					+ DamiCommon.getUid(DamiApp.getInstance()) + "'"
					+ " ORDER BY " + COLUMN_SEND_TIME + " DESC ";

			cursor = mDBStore.rawQuery(querySql, null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return 0;
				}

				return cursor.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}

	public int queryUnreadTribeCount() {
		Cursor cursor = null;
		try {

			String querySql = "";
			querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE "
					+ COLUMN_TYPE + "=200" + " AND " + COLUMN_PARENT_ID
					+ "='0'" + " AND " + COLUMN_READ_STATE + "=0" + " AND "
					+ COLUMN_LOGIN_ID + "='"
					+ DamiCommon.getUid(DamiApp.getInstance()) + "'";

			cursor = mDBStore.rawQuery(querySql, null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return 0;
				}

				return cursor.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}

	public int queryUnreadMeetingCount() {
		Cursor cursor = null;
		try {

			String querySql = "";
			querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE "
					+ COLUMN_TYPE + "=300" + " AND " + COLUMN_PARENT_ID
					+ "='0'" + " AND " + COLUMN_READ_STATE + "=0" + " AND "
					+ COLUMN_LOGIN_ID + "='"
					+ DamiCommon.getUid(DamiApp.getInstance()) + "'";

			cursor = mDBStore.rawQuery(querySql, null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return 0;
				}

				return cursor.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}

	public int queryPrivateUnreadCount() {
		Cursor cursor = null;
		try {

			String querySql = "";
			querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE "
					+ COLUMN_TYPE + "=100" + " AND " + COLUMN_READ_STATE + "=0"
					+ " AND " + COLUMN_LOGIN_ID + "='"
					+ DamiCommon.getUid(DamiApp.getInstance()) + "'";

			cursor = mDBStore.rawQuery(querySql, null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return 0;
				}

				return cursor.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}

	public int queryUnreadUserCount(String fuid) {
		Cursor cursor = null;
		try {

			String querySql = "";
			querySql = "SELECT rowid, * FROM " + TABLE_NAME + " WHERE "
					+ COLUMN_FROM_UID + "='" + fuid + "' AND " + COLUMN_TYPE
					+ "=100 AND " + COLUMN_READ_STATE + "=0" + " AND "
					+ COLUMN_LOGIN_ID + "='"
					+ DamiCommon.getUid(DamiApp.getInstance()) + "'"
					+ " ORDER BY " + COLUMN_SEND_TIME + " DESC ";

			cursor = mDBStore.rawQuery(querySql, null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return 0;
				}

				return cursor.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}
}
