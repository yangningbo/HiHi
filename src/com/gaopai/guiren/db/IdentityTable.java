package com.gaopai.guiren.db;

import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.bean.Identity;

public class IdentityTable {

	public static final String TABLE_NAME = "IdentityTable";// 数据表的名称
	/**
	 * fromId, sendTime, unreadCount, currentUser, primary key(fromId,
	 * currentUser
	 */
	public static final String COLUMN_TRIBE_ID = "tribeid";
	public static final String COLUMN_IDENTITY_ID = "identity_id";
	public static final String COLUMN_IDENTITY_NAME = "identity_name";
	public static final String COLUMN_IDENTITY_HEAD = "identity_head";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_LOGIN_ID = "loginid";

	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String PRIMARY_KEY_TYPE = "primary key(";

	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;

	public IdentityTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}

	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {

			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_TRIBE_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_IDENTITY_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_IDENTITY_NAME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_IDENTITY_HEAD, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_TIME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_LOGIN_ID, COLUMN_TEXT_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_TRIBE_ID + ")";

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

	public void insert(String tribeID, Identity identity) {
		ContentValues allPromotionInfoValues = new ContentValues();

		allPromotionInfoValues.put(COLUMN_TRIBE_ID, tribeID);
		allPromotionInfoValues.put(COLUMN_IDENTITY_ID, identity.id);
		allPromotionInfoValues.put(COLUMN_IDENTITY_NAME, identity.name);
		allPromotionInfoValues.put(COLUMN_IDENTITY_HEAD, identity.head);
		allPromotionInfoValues.put(COLUMN_TIME, System.currentTimeMillis() + "");
		allPromotionInfoValues.put(COLUMN_LOGIN_ID, DamiCommon.getUid(DamiApp.getInstance()));

		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
	}

	public void update(String tribeID, Identity identity) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_IDENTITY_ID, identity.id);
		allPromotionInfoValues.put(COLUMN_IDENTITY_NAME, identity.name);
		allPromotionInfoValues.put(COLUMN_IDENTITY_HEAD, identity.head);
		allPromotionInfoValues.put(COLUMN_TIME, System.currentTimeMillis() + "");
		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_TRIBE_ID + " = '" + tribeID + "' AND "
					+ COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
	}

	public boolean delete(String id) {
		try {
			mDBStore.delete(
					TABLE_NAME,
					COLUMN_TRIBE_ID + " = '" + id + "' AND " + COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean delete() {
		try {
			mDBStore.delete(TABLE_NAME, COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public Identity query(String id) {
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery(
					"SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "' AND " + COLUMN_TRIBE_ID + "='" + id + "'",
					null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexIdentityID = cursor.getColumnIndex(COLUMN_IDENTITY_ID);
				int indexIdentityName = cursor.getColumnIndex(COLUMN_IDENTITY_NAME);
				int indexIdentityHead = cursor.getColumnIndex(COLUMN_IDENTITY_HEAD);
				int indexTime = cursor.getColumnIndex(COLUMN_TIME);
				Identity identity = new Identity();
				identity.id = cursor.getString(indexIdentityID);
				identity.name = cursor.getString(indexIdentityName);
				identity.head = cursor.getString(indexIdentityHead);
				identity.updateTime = Long.parseLong(cursor.getString(indexTime));
				return identity;
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

}
