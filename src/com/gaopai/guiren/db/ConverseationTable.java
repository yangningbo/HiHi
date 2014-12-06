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
import com.gaopai.guiren.bean.ConversationBean;
import com.gaopai.guiren.bean.MessageInfo;

public class ConverseationTable {
	public static final String TABLE_NAME = "ConverseationTable";// 数据表的名称

	public static final String COLUMN_HEAD_URL = "headurl";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_LAST_MSG_CONTENT = "lastmsgcontent";
	public static final String COLUMN_LAST_MSG_TIME = "lastmsgtime";
	public static final String COLUMN_UNREAD_COUNT = "unreadcount";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_LOCAL_TYPE = "local_type";
	public static final String COLUMN_TO_ID = "toid";
	public static final String COLUMN_ANONYMOUS = "anonymous";
	public static final String COLUMN_UNFINISH_INPUT = "unfinishinput";

	public static final String COLUMN_LOGIN_ID = "loginId";
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String PRIMARY_KEY_TYPE = "primary key(";

	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;

	public ConverseationTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}

	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {

			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_HEAD_URL, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_NAME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_LAST_MSG_CONTENT, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_LAST_MSG_TIME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_UNREAD_COUNT, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_TYPE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_LOCAL_TYPE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_TO_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_ANONYMOUS, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_UNFINISH_INPUT, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_LOGIN_ID, COLUMN_TEXT_TYPE);

			String primary_key = PRIMARY_KEY_TYPE + COLUMN_TO_ID + "," + COLUMN_LOGIN_ID + ")";

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

	public void insert(ConversationBean bean) {
		ContentValues allPromotionInfoValues = new ContentValues();

		allPromotionInfoValues.put(COLUMN_HEAD_URL, bean.headurl);
		allPromotionInfoValues.put(COLUMN_NAME, bean.name);
		allPromotionInfoValues.put(COLUMN_LAST_MSG_CONTENT, bean.lastmsgcontent);
		allPromotionInfoValues.put(COLUMN_LAST_MSG_TIME, bean.lastmsgtime);
		allPromotionInfoValues.put(COLUMN_UNREAD_COUNT, bean.unreadcount);
		allPromotionInfoValues.put(COLUMN_TYPE, bean.type);
		allPromotionInfoValues.put(COLUMN_TO_ID, bean.toid);
		allPromotionInfoValues.put(COLUMN_ANONYMOUS, bean.anonymous);
		allPromotionInfoValues.put(COLUMN_UNFINISH_INPUT, bean.unfinishinput);
		allPromotionInfoValues.put(COLUMN_LOCAL_TYPE, bean.localtype);
		allPromotionInfoValues.put(COLUMN_LOGIN_ID, DamiCommon.getUid(DamiApp.getInstance()));
		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
	}

	public boolean delete(String id) {
		try {
			mDBStore.delete(
					TABLE_NAME,
					COLUMN_TO_ID + " = '" + id + "' AND " + COLUMN_LOGIN_ID + "= '"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<ConversationBean> query() {
		List<ConversationBean> allInfo = new ArrayList<ConversationBean>();
		Cursor cursor = null;
		mDBStore.beginTransaction();
		try {
			String querySql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID + "='"
					+ DamiCommon.getUid(DamiApp.getInstance()) + "' ORDER BY " + COLUMN_LAST_MSG_TIME + " DESC ";
			cursor = mDBStore.rawQuery(querySql, null);
			while (cursor.moveToNext()) {
				ConversationBean bean = new ConversationBean();
				int columnIndex;
				columnIndex = cursor.getColumnIndex(COLUMN_HEAD_URL);
				bean.headurl = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_NAME);
				bean.name = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_LAST_MSG_CONTENT);
				bean.lastmsgcontent = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_LAST_MSG_TIME);
				bean.lastmsgtime = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_UNREAD_COUNT);
				bean.unreadcount = cursor.getInt(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_TYPE);
				bean.type = cursor.getInt(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_TO_ID);
				bean.toid = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_ANONYMOUS);
				bean.anonymous = cursor.getInt(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_UNFINISH_INPUT);
				bean.unfinishinput = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_LOCAL_TYPE);
				bean.localtype = cursor.getInt(columnIndex);

				allInfo.add(bean);
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

	public List<ConversationBean> queryPeople() {
		List<ConversationBean> allInfo = new ArrayList<ConversationBean>();
		Cursor cursor = null;
		mDBStore.beginTransaction();
		try {
			String querySql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID + "='"
					+ DamiCommon.getUid(DamiApp.getInstance()) + "' AND " + COLUMN_TYPE + " = " + 100 + " ORDER BY "
					+ COLUMN_LAST_MSG_TIME + " DESC ";
			cursor = mDBStore.rawQuery(querySql, null);
			while (cursor.moveToNext()) {
				ConversationBean bean = new ConversationBean();
				int columnIndex;
				columnIndex = cursor.getColumnIndex(COLUMN_HEAD_URL);
				bean.headurl = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_NAME);
				bean.name = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_LAST_MSG_CONTENT);
				bean.lastmsgcontent = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_LAST_MSG_TIME);
				bean.lastmsgtime = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_UNREAD_COUNT);
				bean.unreadcount = cursor.getInt(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_TYPE);
				bean.type = cursor.getInt(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_TO_ID);
				bean.toid = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_ANONYMOUS);
				bean.anonymous = cursor.getInt(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_UNFINISH_INPUT);
				bean.unfinishinput = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_LOCAL_TYPE);
				bean.localtype = cursor.getInt(columnIndex);

				allInfo.add(bean);
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

	public List<ConversationBean> queryShare() {
		List<ConversationBean> allInfo = new ArrayList<ConversationBean>();
		Cursor cursor = null;
		mDBStore.beginTransaction();
		try {
			String querySql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID + "='"
					+ DamiCommon.getUid(DamiApp.getInstance()) + "' AND " + COLUMN_TYPE + " = " + 100 + " ORDER BY "
					+ COLUMN_LAST_MSG_TIME + " DESC ";
			cursor = mDBStore.rawQuery(querySql, null);
			while (cursor.moveToNext()) {
				ConversationBean bean = new ConversationBean();
				int columnIndex;
				columnIndex = cursor.getColumnIndex(COLUMN_HEAD_URL);
				bean.headurl = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_NAME);
				bean.name = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_LAST_MSG_CONTENT);
				bean.lastmsgcontent = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_LAST_MSG_TIME);
				bean.lastmsgtime = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_UNREAD_COUNT);
				bean.unreadcount = cursor.getInt(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_TYPE);
				bean.type = cursor.getInt(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_TO_ID);
				bean.toid = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_ANONYMOUS);
				bean.anonymous = cursor.getInt(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_UNFINISH_INPUT);
				bean.unfinishinput = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_LOCAL_TYPE);
				bean.localtype = cursor.getInt(columnIndex);

				allInfo.add(bean);
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

	public ConversationBean queryByID(String id) {
		Cursor cursor = null;
		try {
			String querySql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TO_ID + "='" + id + "' AND "
					+ COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'";
			cursor = mDBStore.rawQuery(querySql, null);
			if (cursor != null && cursor.moveToFirst()) {
				ConversationBean bean = new ConversationBean();
				int columnIndex;
				columnIndex = cursor.getColumnIndex(COLUMN_HEAD_URL);
				bean.headurl = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_NAME);
				bean.name = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_LAST_MSG_CONTENT);
				bean.lastmsgcontent = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_LAST_MSG_TIME);
				bean.lastmsgtime = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_UNREAD_COUNT);
				bean.unreadcount = cursor.getInt(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_TYPE);
				bean.type = cursor.getInt(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_TO_ID);
				bean.toid = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_ANONYMOUS);
				bean.anonymous = cursor.getInt(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_UNFINISH_INPUT);
				bean.unfinishinput = cursor.getString(columnIndex);

				columnIndex = cursor.getColumnIndex(COLUMN_LOCAL_TYPE);
				bean.localtype = cursor.getInt(columnIndex);
				return bean;
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

	public boolean update(ConversationBean message) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_HEAD_URL, message.headurl);
		allPromotionInfoValues.put(COLUMN_LAST_MSG_CONTENT, message.lastmsgcontent);
		allPromotionInfoValues.put(COLUMN_LAST_MSG_TIME, message.lastmsgtime);
		allPromotionInfoValues.put(COLUMN_UNREAD_COUNT, message.unreadcount);
		allPromotionInfoValues.put(COLUMN_NAME, message.name);
		allPromotionInfoValues.put(COLUMN_TYPE, message.type);
		allPromotionInfoValues.put(COLUMN_UNFINISH_INPUT, message.unfinishinput);
		allPromotionInfoValues.put(COLUMN_LOCAL_TYPE, message.localtype);
		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_TO_ID + " = '" + message.toid + "' AND "
					+ COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean updateDraft(String draft, String toid) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_UNFINISH_INPUT, draft);
		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_TO_ID + " = '" + toid + "' AND "
					+ COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int queryUnreadCount() {
		Cursor cursor = null;
		try {

			String querySql = "";
			querySql = "SELECT toid, * FROM " + TABLE_NAME + " WHERE " + COLUMN_UNREAD_COUNT + "= 0'";

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

	public boolean resetCount(String id) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_UNREAD_COUNT, 0);
		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_TO_ID + " = '" + id + "'", null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
		return false;
	}

}
