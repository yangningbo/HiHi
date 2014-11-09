package com.gaopai.guiren.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.bean.Session;

public class SessionTable {

	public static final String TABLE_NAME = "SessionTable";// 数据表的名称
	/**
	 * fromId, sendTime, unreadCount, currentUser, primary key(fromId,
	 * currentUser
	 */
	public static final String COLUMN_SESSION_ID = "sessionID";
	public static final String COLUMN_LOGIN_ID = "loginId";

	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String PRIMARY_KEY_TYPE = "primary key(";

	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;

	public SessionTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}

	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {

			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_SESSION_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_LOGIN_ID, COLUMN_TEXT_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_SESSION_ID + ","
					+ COLUMN_LOGIN_ID + ")";

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

	public void insert(List<Session> sessions) {
		List<Session> sessionList = new ArrayList<Session>();
		sessionList.addAll(sessions);
		mDBStore.beginTransaction();
		try {
			for (Session session : sessionList) {
				ContentValues allPromotionInfoValues = new ContentValues();

				allPromotionInfoValues.put(COLUMN_SESSION_ID,
						session.getFromId());
				allPromotionInfoValues.put(COLUMN_LOGIN_ID,
						DamiCommon.getUid(DamiApp.getInstance()));

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

	public boolean insert(Session session) {
		ContentValues allPromotionInfoValues = new ContentValues();

		allPromotionInfoValues.put(COLUMN_SESSION_ID, session.getFromId());
		allPromotionInfoValues.put(COLUMN_LOGIN_ID,
				DamiCommon.getUid(DamiApp.getInstance()));

		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean update(Session session) {
		ContentValues allPromotionInfoValues = new ContentValues();

		try {
			mDBStore.update(
					TABLE_NAME,
					allPromotionInfoValues,
					COLUMN_SESSION_ID + " = '" + session.getFromId() + "' AND "
							+ COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'",
					null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean delete(String fromId) {
		try {
			mDBStore.delete(
					TABLE_NAME,
					COLUMN_SESSION_ID + " = '" + fromId + "' AND "
							+ COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'",
					null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public Session query(String fromId) {
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery(
					"SELECT * FROM " + TABLE_NAME + " WHERE "
							+ COLUMN_SESSION_ID + "='" + fromId + "' AND "
							+ COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'",
					null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexFromId = cursor.getColumnIndex(COLUMN_SESSION_ID);

				Session session = new Session();
				session.setFromId(cursor.getString(indexFromId));
				return session;
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

	public List<Session> query() {
		List<Session> allInfo = new ArrayList<Session>();
		Cursor cursor = null;
		mDBStore.beginTransaction();
		try {
			cursor = mDBStore.rawQuery(
					"SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID
							+ "='" + DamiCommon.getUid(DamiApp.getInstance())
							+ "' ", null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexFromId = cursor.getColumnIndex(COLUMN_SESSION_ID);
				int indexLoginId = cursor.getColumnIndex(COLUMN_LOGIN_ID);

				do {
					Session session = new Session();

					session.setFromId(cursor.getString(indexFromId));
					session.setLoginId(cursor.getString(indexLoginId));

					allInfo.add(session);
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

}
