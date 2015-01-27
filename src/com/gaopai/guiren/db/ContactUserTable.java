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
import com.gaopai.guiren.bean.User;

public class ContactUserTable {

	public static final String TABLE_NAME = "ContactUserTable";// 数据表的名称

	public static final String COLUMN_UID = "uid";
	public static final String COLUMN_LOGIN_ID = "loginId";
	public static final String COLUMN_SAMLL_FACE = "samllface";
	public static final String COLUMN_LARGE_FACE = "largeface";
	public static final String COLUMN_REAL_NAME = "realName";
	public static final String COLUMN_POST = "post";
	public static final String COLUMN_BIG_V = "bigV";
	public static final String COLUMN_COMPANY = "company";

	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String PRIMARY_KEY_TYPE = "primary key(";

	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;

	public ContactUserTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}

	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {

			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_UID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_LOGIN_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_SAMLL_FACE, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_LARGE_FACE, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_REAL_NAME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_POST, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_COMPANY, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_BIG_V, COLUMN_INTEGER_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_UID + "," + COLUMN_LOGIN_ID + ")";

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

	public void insert(List<User> users) {
		List<User> userList = new ArrayList<User>();
		userList.addAll(users);
		mDBStore.beginTransaction();
		try {
			for (User user : userList) {
				if (user.uid == null || user.uid.equals("")) {
					continue;
				}
				ContentValues allPromotionInfoValues = new ContentValues();

				allPromotionInfoValues.put(COLUMN_UID, user.uid);
				allPromotionInfoValues.put(COLUMN_LOGIN_ID, DamiCommon.getUid(DamiApp.getInstance()));
				allPromotionInfoValues.put(COLUMN_REAL_NAME, user.realname);
				allPromotionInfoValues.put(COLUMN_SAMLL_FACE, user.headsmall);
				allPromotionInfoValues.put(COLUMN_LARGE_FACE, user.headlarge);
				allPromotionInfoValues.put(COLUMN_POST, user.post);
				allPromotionInfoValues.put(COLUMN_COMPANY, user.company);
				allPromotionInfoValues.put(COLUMN_BIG_V, user.bigv);
				delete(user);
				try {
					mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
				} catch (SQLiteConstraintException e) {
					e.printStackTrace();
				}
			}
			mDBStore.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			mDBStore.endTransaction();
		}

	}

	public void update(User user) {
		if (user.uid == null || user.uid.equals("")) {
			return;
		}
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_REAL_NAME, user.realname);
		allPromotionInfoValues.put(COLUMN_SAMLL_FACE, user.headsmall);
		allPromotionInfoValues.put(COLUMN_LARGE_FACE, user.headlarge);
		allPromotionInfoValues.put(COLUMN_POST, user.post);
		allPromotionInfoValues.put(COLUMN_COMPANY, user.company);
		allPromotionInfoValues.put(COLUMN_BIG_V, user.bigv);

		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_UID + " = '" + user.uid + "' AND "
					+ COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
	}

	public void insert(User user) {
		if (user.uid == null || user.uid.equals("")) {
			return;
		}
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_UID, user.uid);
		allPromotionInfoValues.put(COLUMN_LOGIN_ID, DamiCommon.getUid(DamiApp.getInstance()));
		allPromotionInfoValues.put(COLUMN_REAL_NAME, user.realname);
		allPromotionInfoValues.put(COLUMN_SAMLL_FACE, user.headsmall);
		allPromotionInfoValues.put(COLUMN_LARGE_FACE, user.headlarge);
		allPromotionInfoValues.put(COLUMN_POST, user.post);
		allPromotionInfoValues.put(COLUMN_COMPANY, user.company);
		allPromotionInfoValues.put(COLUMN_BIG_V, user.bigv);
		delete(user);
		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
	}

	public void delete(User user) {
		if (user.uid == null) {
			return;
		}
		mDBStore.delete(
				TABLE_NAME,
				COLUMN_UID + "='" + user.uid + "' AND " + COLUMN_LOGIN_ID + "='"
						+ DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
	}

	public User query(String uid) {
		User user = new User();
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_UID + "='" + uid + "' AND "
					+ COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
			while (cursor != null && cursor.moveToNext()) {
				user.uid = cursor.getString(cursor.getColumnIndex(COLUMN_UID));
				user.realname = cursor.getString(cursor.getColumnIndex(COLUMN_REAL_NAME));

				user.headsmall = cursor.getString(cursor.getColumnIndex(COLUMN_SAMLL_FACE));
				user.headlarge = cursor.getString(cursor.getColumnIndex(COLUMN_LARGE_FACE));

				user.post = cursor.getString(cursor.getColumnIndex(COLUMN_POST));
				user.company = cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY));
				user.bigv = cursor.getInt(cursor.getColumnIndex(COLUMN_BIG_V));

				return user;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public List<User> queryList() {
		List<User> allInfo = new ArrayList<User>();
		Cursor cursor = null;
		mDBStore.beginTransaction();
		try {
			cursor = mDBStore.rawQuery(
					"SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'", null);

			while (cursor != null && cursor.moveToNext()) {
				User user = new User();
				user.uid = cursor.getString(cursor.getColumnIndex(COLUMN_UID));
				user.realname = cursor.getString(cursor.getColumnIndex(COLUMN_REAL_NAME));

				user.headsmall = cursor.getString(cursor.getColumnIndex(COLUMN_SAMLL_FACE));
				user.headlarge = cursor.getString(cursor.getColumnIndex(COLUMN_LARGE_FACE));

				user.post = cursor.getString(cursor.getColumnIndex(COLUMN_POST));
				user.company = cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY));
				user.bigv = cursor.getInt(cursor.getColumnIndex(COLUMN_BIG_V));

				allInfo.add(user);
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
