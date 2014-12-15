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
import com.gaopai.guiren.bean.NotifiyVo;

public class NotifyTable {

	public static final String TABLE_NAME = "NotifyTable";// 数据表的名称
	/**
	 * CREATE TABLE IF NOT EXISTS tb_notify (type, content, userID, time,
	 * processed, currentUser, primary key(userID, time, type, currentUser))
	 */
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_USERID = "userID";
	public static final String COLUMN_ROOMID = "roomID";
	public static final String COLUMN_MESSAGEID = "messageID";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_READ_STATE = "read_state";
	public static final String COLUMN_PROCESSED = "processed";
	public static final String COLUMN_LOGINID = "loginID";
	public static final String COLUMN_CODE = "invite_code";
	public static final String COLUMN_PHONE = "phone";
	public static final String COLUMN_IDENTITYID = "identityID";

	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String PRIMARY_KEY_TYPE = "primary key(";

	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;

	public NotifyTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}

	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {

			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_TYPE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_CONTENT, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_USERID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_ROOMID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_MESSAGEID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_IDENTITYID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_USERID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_TIME, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_CODE, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_PHONE, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_READ_STATE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_PROCESSED, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_LOGINID, COLUMN_TEXT_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_ID + ")";

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

	public void insert(List<NotifiyVo> notifyVos) {
		List<NotifiyVo> notifyList = new ArrayList<NotifiyVo>();
		notifyList.addAll(notifyVos);
		mDBStore.beginTransaction();
		try {
			for (NotifiyVo notify : notifyList) {
				ContentValues allPromotionInfoValues = new ContentValues();

				allPromotionInfoValues.put(COLUMN_TYPE, notify.type);
				allPromotionInfoValues.put(COLUMN_CONTENT, notify.content);
				allPromotionInfoValues.put(COLUMN_USERID, notify.user.uid);
				allPromotionInfoValues.put(COLUMN_TIME, notify.time);
				allPromotionInfoValues.put(COLUMN_PROCESSED, notify.processed);
				allPromotionInfoValues.put(COLUMN_LOGINID, DamiCommon.getUid(DamiApp.getInstance()));

				try {
					mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
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

	public boolean insert(NotifiyVo notify) {
		ContentValues allPromotionInfoValues = new ContentValues();

		allPromotionInfoValues.put(COLUMN_ID, notify.mID);
		allPromotionInfoValues.put(COLUMN_TYPE, notify.type);
		allPromotionInfoValues.put(COLUMN_CONTENT, notify.content);
		
		if (notify.user != null) {
			allPromotionInfoValues.put(COLUMN_USERID, notify.user.uid);
		}
		if (notify.message != null) {
			allPromotionInfoValues.put(COLUMN_MESSAGEID, notify.message.id);
		}
		if (notify.room != null) {
			allPromotionInfoValues.put(COLUMN_ROOMID, notify.room.id);
		}
		if (notify.roomuser != null) {
			allPromotionInfoValues.put(COLUMN_ROOMID, notify.roomuser.id);
		}
		allPromotionInfoValues.put(COLUMN_CODE, notify.code);
		allPromotionInfoValues.put(COLUMN_PHONE, notify.phone);
		allPromotionInfoValues.put(COLUMN_TIME, notify.time);
		allPromotionInfoValues.put(COLUMN_PROCESSED, notify.processed);
		allPromotionInfoValues.put(COLUMN_READ_STATE, notify.mReadState);
		allPromotionInfoValues.put(COLUMN_LOGINID, DamiCommon.getUid(DamiApp.getInstance()));

		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean update(NotifiyVo notify) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_PROCESSED, notify.processed);
		allPromotionInfoValues.put(COLUMN_READ_STATE, notify.mReadState);

		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_ID + " = '" + notify.mID + "' AND "
					+ COLUMN_LOGINID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean delete(NotifiyVo notify) {
		try {
			mDBStore.delete(TABLE_NAME, COLUMN_TYPE + " = '" + notify.type + "' AND " + COLUMN_LOGINID + "='"
					+ DamiCommon.getUid(DamiApp.getInstance()) + "' AND " + COLUMN_USERID + "='" + notify.user.uid
					+ "' AND " + COLUMN_TIME + "='" + notify.time + "'", null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean deleteByID(NotifiyVo notify) {
		try {
			mDBStore.delete(
					TABLE_NAME,
					COLUMN_ID + " = '" + notify.mID + "' AND " + COLUMN_LOGINID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean deleteSame(NotifiyVo notify) {
		try {
			mDBStore.delete(TABLE_NAME, COLUMN_LOGINID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "' AND "
					+ COLUMN_USERID + "='" + notify.user.uid + "' AND " + COLUMN_TYPE + "=" + notify.type + " AND "
					+ COLUMN_PROCESSED + "=0", null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public NotifiyVo query(NotifiyVo notify) {
		Cursor cursor = null;
		List<NotifiyVo> allInfo = new ArrayList<NotifiyVo>();
		mDBStore.beginTransaction();
		try {
			cursor = mDBStore.rawQuery(
					"SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGINID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "' AND " + COLUMN_ROOMID + "='"
							+ notify.room.id + "' AND " + COLUMN_TYPE + "=" + notify.type + " ORDER BY " + COLUMN_TIME
							+ " DESC ", null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexID = cursor.getColumnIndex(COLUMN_ID);
				int indexType = cursor.getColumnIndex(COLUMN_TYPE);
				int indexContent = cursor.getColumnIndex(COLUMN_CONTENT);
				int indexUid = cursor.getColumnIndex(COLUMN_USERID);
				int indexTime = cursor.getColumnIndex(COLUMN_TIME);
				int indexProccessed = cursor.getColumnIndex(COLUMN_PROCESSED);
				int indexRoomID = cursor.getColumnIndex(COLUMN_ROOMID);
				int indexMessageID = cursor.getColumnIndex(COLUMN_MESSAGEID);
				int indexIdentityID = cursor.getColumnIndex(COLUMN_IDENTITYID);
				int indexReadState = cursor.getColumnIndex(COLUMN_READ_STATE);
				int indexCode = cursor.getColumnIndex(COLUMN_CODE);
				int indexPhone = cursor.getColumnIndex(COLUMN_PHONE);

				do {
					NotifiyVo notifiyVo = new NotifiyVo();
					notifiyVo.type = cursor.getInt(indexType);
					notifiyVo.content = cursor.getString(indexContent);
					notifiyVo.user.uid = cursor.getString(indexUid);
					notifiyVo.processed = cursor.getInt(indexProccessed);
					notifiyVo.time = cursor.getLong(indexTime);
					notifiyVo.mID = cursor.getString(indexID);
					notifiyVo.room.id = cursor.getString(indexRoomID);
					notifiyVo.message.id = cursor.getString(indexMessageID);
					notifiyVo.roomuser.id = cursor.getString(indexIdentityID);
					notifiyVo.mReadState = cursor.getInt(indexReadState);
					notifiyVo.code = cursor.getString(indexCode);
					notifiyVo.phone = cursor.getString(indexPhone);
					if (!TextUtils.isEmpty(notifiyVo.user.uid)) {
						NotifyUserTable userTable = new NotifyUserTable(mDBStore);
						notifiyVo.user = userTable.query(notifiyVo.mID, notifiyVo.user.uid);
					}

					if (!TextUtils.isEmpty(notifiyVo.room.id)) {
						NotifyRoomTable table = new NotifyRoomTable(mDBStore);
						notifiyVo.room = table.query(notifiyVo.mID, notifiyVo.room.id);
					}

					if (!TextUtils.isEmpty(notifiyVo.message.id)) {
						NotifyMessageTable table = new NotifyMessageTable(mDBStore);
						notifiyVo.message = table.query(notifiyVo.mID, notifiyVo.message.id);
					}
					if (!TextUtils.isEmpty(notifiyVo.roomuser.id)) {
						IdentityTable table = new IdentityTable(mDBStore);
						notifiyVo.roomuser = table.query(notifiyVo.mID);
					}
					allInfo.add(notifiyVo);
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
		if (allInfo.size() != 0) {
			return allInfo.get(0);
		}
		return null;
	}

	public NotifiyVo queryComment(NotifiyVo notify) {
		Cursor cursor = null;
		List<NotifiyVo> allInfo = new ArrayList<NotifiyVo>();
		mDBStore.beginTransaction();
		try {
			cursor = mDBStore.rawQuery(
					"SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGINID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "' AND " + COLUMN_MESSAGEID + "='"
							+ notify.message.id + "' AND " + COLUMN_TYPE + "=" + notify.type + " ORDER BY "
							+ COLUMN_TIME + " DESC ", null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexID = cursor.getColumnIndex(COLUMN_ID);
				int indexType = cursor.getColumnIndex(COLUMN_TYPE);
				int indexContent = cursor.getColumnIndex(COLUMN_CONTENT);
				int indexUid = cursor.getColumnIndex(COLUMN_USERID);
				int indexTime = cursor.getColumnIndex(COLUMN_TIME);
				int indexProccessed = cursor.getColumnIndex(COLUMN_PROCESSED);
				int indexRoomID = cursor.getColumnIndex(COLUMN_ROOMID);
				int indexMessageID = cursor.getColumnIndex(COLUMN_MESSAGEID);
				int indexIdentityID = cursor.getColumnIndex(COLUMN_IDENTITYID);
				int indexReadState = cursor.getColumnIndex(COLUMN_READ_STATE);
				int indexCode = cursor.getColumnIndex(COLUMN_CODE);
				int indexPhone = cursor.getColumnIndex(COLUMN_PHONE);

				do {
					NotifiyVo notifiyVo = new NotifiyVo();
					notifiyVo.type = (cursor.getInt(indexType));
					notifiyVo.content = (cursor.getString(indexContent));
					notifiyVo.user.uid = (cursor.getString(indexUid));
					notifiyVo.processed = (cursor.getInt(indexProccessed));
					notifiyVo.time = (cursor.getLong(indexTime));
					notifiyVo.mID = cursor.getString(indexID);
					notifiyVo.room.id = cursor.getString(indexRoomID);
					notifiyVo.message.id = cursor.getString(indexMessageID);
					notifiyVo.roomuser.id = cursor.getString(indexIdentityID);
					notifiyVo.mReadState = cursor.getInt(indexReadState);
					notifiyVo.code = cursor.getString(indexCode);
					notifiyVo.phone = cursor.getString(indexPhone);
					if (!TextUtils.isEmpty(notifiyVo.user.uid)) {
						NotifyUserTable userTable = new NotifyUserTable(mDBStore);
						notifiyVo.user = userTable.query(notifiyVo.mID, notifiyVo.user.uid);
					}

					if (!TextUtils.isEmpty(notifiyVo.room.id)) {
						NotifyRoomTable table = new NotifyRoomTable(mDBStore);
//						notifiyVo.room = table.query(notifiyVo.mID, notifiyVo.room.id);
					}

					if (!TextUtils.isEmpty(notifiyVo.message.id)) {
						NotifyMessageTable table = new NotifyMessageTable(mDBStore);
						notifiyVo.message = table.query(notifiyVo.mID, notifiyVo.message.id);
					}
					if (!TextUtils.isEmpty(notifiyVo.roomuser.id)) {
						IdentityTable table = new IdentityTable(mDBStore);
						notifiyVo.roomuser = table.query(notifiyVo.mID);
					}
					allInfo.add(notifiyVo);
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
		if (allInfo.size() != 0) {
			return allInfo.get(0);
		}
		return null;
	}

	public List<NotifiyVo> query() {
		List<NotifiyVo> allInfo = new ArrayList<NotifiyVo>();
		Cursor cursor = null;
		mDBStore.beginTransaction();
		try {
			cursor = mDBStore.rawQuery(
					"SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGINID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "' ORDER BY " + COLUMN_TIME + " DESC ", null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return allInfo;
				}

				int indexID = cursor.getColumnIndex(COLUMN_ID);
				int indexType = cursor.getColumnIndex(COLUMN_TYPE);
				int indexContent = cursor.getColumnIndex(COLUMN_CONTENT);
				int indexUid = cursor.getColumnIndex(COLUMN_USERID);
				int indexTime = cursor.getColumnIndex(COLUMN_TIME);
				int indexProccessed = cursor.getColumnIndex(COLUMN_PROCESSED);
				int indexRoomID = cursor.getColumnIndex(COLUMN_ROOMID);
				int indexMessageID = cursor.getColumnIndex(COLUMN_MESSAGEID);
				int indexIdentityID = cursor.getColumnIndex(COLUMN_IDENTITYID);
				int indexReadState = cursor.getColumnIndex(COLUMN_READ_STATE);
				int indexCode = cursor.getColumnIndex(COLUMN_CODE);
				int indexPhone = cursor.getColumnIndex(COLUMN_PHONE);

				do {
					NotifiyVo notifiyVo = new NotifiyVo();
					notifiyVo.intial();
					notifiyVo.type = (cursor.getInt(indexType));
					notifiyVo.content = (cursor.getString(indexContent));
					notifiyVo.user.uid = (cursor.getString(indexUid));
					notifiyVo.processed = (cursor.getInt(indexProccessed));
					notifiyVo.time = (cursor.getLong(indexTime));
					notifiyVo.mID = cursor.getString(indexID);
					notifiyVo.room.id = cursor.getString(indexRoomID);
					notifiyVo.message.id = cursor.getString(indexMessageID);
					notifiyVo.roomuser.id = cursor.getString(indexIdentityID);
					notifiyVo.mReadState = cursor.getInt(indexReadState);
					notifiyVo.code = cursor.getString(indexCode);
					notifiyVo.phone = cursor.getString(indexPhone);
//					if (!TextUtils.isEmpty(notifiyVo.user.uid)) {
//						NotifyUserTable userTable = new NotifyUserTable(mDBStore);
//						notifiyVo.user = (userTable.query(notifiyVo.mID, notifiyVo.user.uid));
//					}

//					if (!TextUtils.isEmpty(notifiyVo.room.id)) {
//						NotifyRoomTable table = new NotifyRoomTable(mDBStore);
//						notifiyVo.room = table.query(notifiyVo.mID, notifiyVo.room.id);
//					}

//					if (!TextUtils.isEmpty(notifiyVo.message.id)) {
//						NotifyMessageTable table = new NotifyMessageTable(mDBStore);
//						notifiyVo.message = table.query(notifiyVo.mID, notifiyVo.message.id);
//					}
//					if (!TextUtils.isEmpty(notifiyVo.roomuser.id)) {
//						IdentityTable table = new IdentityTable(mDBStore);
//						notifiyVo.roomuser = table.query(notifiyVo.roomuser.id);
//					}

					allInfo.add(notifiyVo);
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

	public int queryUnread() {
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery(
					"SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGINID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "' AND " + COLUMN_READ_STATE + "=0", null);
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
