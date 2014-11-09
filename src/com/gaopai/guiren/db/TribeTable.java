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
import com.gaopai.guiren.bean.MessageInfo;
import com.gaopai.guiren.bean.Tribe;

public class TribeTable {

	public static final String TABLE_NAME = "TribeTable";// 数据表的名称
	/**
	 * fromId, sendTime, unreadCount, currentUser, primary key(fromId,
	 * currentUser
	 */
	public static final String COLUMN_TRIBE_NAME = "tribename";
	public static final String COLUMN_TRIBE_ID = "tribeid";
	public static final String COLUMN_TRIBE_LOGO_SMALL = "logosmall";
	public static final String COLUMN_TRIBE_LOGO_LARGE = "logolarge";
	public static final String COLUMN_TRIBE_INDUSTRY = "industry";
	public static final String COLUMN_TRIBE_TYPE = "type";
	public static final String COLUMN_TRIBE_CONTENT = "content";
	public static final String COLUMN_TRIBE_CREATETIME = "createtime";
	public static final String COLUMN_TRIBE_REALNAME = "realName";
	public static final String COLUMN_TRIBE_ISJOIN = "isjoin";
	public static final String COLUMN_TRIBE_ROLE = "role";
	public static final String COLUMN_TRIBE_COUNT = "count";
	public static final String COLUMN_LOGIN_ID = "loginid";
	public static final String COLUMN_GET_MSG_TYPE = "getMsgType";

	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String PRIMARY_KEY_TYPE = "primary key(";

	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;

	public TribeTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}

	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {

			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_TRIBE_NAME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_TRIBE_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_TRIBE_LOGO_SMALL, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_TRIBE_LOGO_LARGE, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_TRIBE_INDUSTRY, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_TRIBE_TYPE, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_TRIBE_CONTENT, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_TRIBE_CREATETIME, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_TRIBE_REALNAME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_TRIBE_ISJOIN, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_TRIBE_ROLE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_TRIBE_COUNT, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_LOGIN_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_GET_MSG_TYPE, COLUMN_INTEGER_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_TRIBE_ID + ","
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

	public void insert(List<Tribe> tribes) {
		List<Tribe> tribeList = new ArrayList<Tribe>();
		tribeList.addAll(tribes);
		mDBStore.beginTransaction();
		try {
			for (Tribe tribe : tribeList) {
				ContentValues allPromotionInfoValues = new ContentValues();

				allPromotionInfoValues.put(COLUMN_TRIBE_NAME, tribe.name);
				allPromotionInfoValues.put(COLUMN_TRIBE_ID, tribe.id);
				allPromotionInfoValues.put(COLUMN_TRIBE_LOGO_SMALL,
						tribe.logosmall);
				allPromotionInfoValues.put(COLUMN_TRIBE_LOGO_LARGE,
						tribe.logolarge);
				allPromotionInfoValues.put(COLUMN_TRIBE_INDUSTRY,
						tribe.industry);
				allPromotionInfoValues.put(COLUMN_TRIBE_TYPE, tribe.type);
				allPromotionInfoValues.put(COLUMN_TRIBE_CONTENT, tribe.content);
				allPromotionInfoValues.put(COLUMN_TRIBE_CREATETIME,
						tribe.createtime);
				allPromotionInfoValues.put(COLUMN_TRIBE_REALNAME,
						tribe.realname);
				allPromotionInfoValues.put(COLUMN_TRIBE_ISJOIN, tribe.isjoin);
				allPromotionInfoValues.put(COLUMN_TRIBE_ROLE, tribe.role);
				allPromotionInfoValues.put(COLUMN_TRIBE_COUNT, tribe.count);
				allPromotionInfoValues.put(COLUMN_GET_MSG_TYPE,
						tribe.getmsg);
				allPromotionInfoValues.put(COLUMN_LOGIN_ID,
						DamiCommon.getUid(DamiApp.getInstance()));

				// delete(tribe.id);
				mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
			}
			mDBStore.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {

			mDBStore.endTransaction();
		}

	}

	public void insert(Tribe tribe) {
		ContentValues allPromotionInfoValues = new ContentValues();

		allPromotionInfoValues.put(COLUMN_TRIBE_NAME, tribe.name);
		allPromotionInfoValues.put(COLUMN_TRIBE_ID, tribe.id);
		allPromotionInfoValues.put(COLUMN_TRIBE_LOGO_SMALL, tribe.logosmall);
		allPromotionInfoValues.put(COLUMN_TRIBE_LOGO_LARGE, tribe.logolarge);
		allPromotionInfoValues.put(COLUMN_TRIBE_INDUSTRY, tribe.industry);
		allPromotionInfoValues.put(COLUMN_TRIBE_TYPE, tribe.type);
		allPromotionInfoValues.put(COLUMN_TRIBE_CONTENT, tribe.content);
		allPromotionInfoValues.put(COLUMN_TRIBE_CREATETIME, tribe.createtime);
		allPromotionInfoValues.put(COLUMN_TRIBE_REALNAME, tribe.realname);
		allPromotionInfoValues.put(COLUMN_TRIBE_ISJOIN, tribe.isjoin);
		allPromotionInfoValues.put(COLUMN_TRIBE_ROLE, tribe.role);
		allPromotionInfoValues.put(COLUMN_TRIBE_COUNT, tribe.count);
		allPromotionInfoValues.put(COLUMN_GET_MSG_TYPE, tribe.getmsg);
		allPromotionInfoValues.put(COLUMN_LOGIN_ID,
				DamiCommon.getUid(DamiApp.getInstance()));
		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
	}

	public void update(Tribe tribe) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_GET_MSG_TYPE, tribe.getmsg);
		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_TRIBE_ID
					+ " = '" + tribe.id + "' AND " + COLUMN_LOGIN_ID + "='"
					+ DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
	}

	public boolean delete(String id) {
		try {
			mDBStore.delete(TABLE_NAME,
					COLUMN_TRIBE_ID + " = '" + id + "' AND " + COLUMN_LOGIN_ID
							+ "='" + DamiCommon.getUid(DamiApp.getInstance())
							+ "'", null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean delete() {
		try {
			mDBStore.delete(
					TABLE_NAME,
					COLUMN_LOGIN_ID + "='"
							+ DamiCommon.getUid(DamiApp.getInstance()) + "'",
					null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public Tribe query(String id) {
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery(
					"SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID
							+ "='" + DamiCommon.getUid(DamiApp.getInstance())
							+ "' AND " + COLUMN_TRIBE_ID + "='" + id + "'",
					null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexTribeName = cursor.getColumnIndex(COLUMN_TRIBE_NAME);
				int indexTribeID = cursor.getColumnIndex(COLUMN_TRIBE_ID);
				int indexTribeSmallLogo = cursor
						.getColumnIndex(COLUMN_TRIBE_LOGO_SMALL);
				int indexTribeLargeLogo = cursor
						.getColumnIndex(COLUMN_TRIBE_LOGO_LARGE);
				int indexTribeIndustry = cursor
						.getColumnIndex(COLUMN_TRIBE_INDUSTRY);
				int indexTribeType = cursor.getColumnIndex(COLUMN_TRIBE_TYPE);
				int indexTribeContent = cursor
						.getColumnIndex(COLUMN_TRIBE_CONTENT);
				int indexTribeCreateTime = cursor
						.getColumnIndex(COLUMN_TRIBE_CREATETIME);
				int indexTribeRealname = cursor
						.getColumnIndex(COLUMN_TRIBE_REALNAME);
				int indexIsJoin = cursor.getColumnIndex(COLUMN_TRIBE_ISJOIN);
				int indexRole = cursor.getColumnIndex(COLUMN_TRIBE_ROLE);
				int indexCount = cursor.getColumnIndex(COLUMN_TRIBE_COUNT);
				int indexGetMsgType = cursor
						.getColumnIndex(COLUMN_GET_MSG_TYPE);
				Tribe tribe = new Tribe();
				tribe.name = cursor.getString(indexTribeName);
				tribe.id = cursor.getString(indexTribeID);
				tribe.logosmall = cursor.getString(indexTribeSmallLogo);
				tribe.logolarge = cursor.getString(indexTribeLargeLogo);
				tribe.industry = cursor.getString(indexTribeIndustry);
				tribe.type = cursor.getInt(indexTribeType);
				tribe.content = cursor.getString(indexTribeContent);
				tribe.createtime = cursor.getInt(indexTribeCreateTime);
				tribe.realname = cursor.getString(indexTribeRealname);
				tribe.isjoin = cursor.getInt(indexIsJoin);
				tribe.role = cursor.getInt(indexRole);
				tribe.count = cursor.getInt(indexCount);
				tribe.getmsg = cursor.getInt(indexGetMsgType);
				return tribe;
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

	public List<Tribe> query() {
		List<Tribe> allInfo = new ArrayList<Tribe>();
		Cursor cursor = null;
		mDBStore.beginTransaction();
		try {
			cursor = mDBStore.rawQuery(
					"SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID
							+ "='" + DamiCommon.getUid(DamiApp.getInstance())
							+ "'", null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexTribeName = cursor.getColumnIndex(COLUMN_TRIBE_NAME);
				int indexTribeID = cursor.getColumnIndex(COLUMN_TRIBE_ID);
				int indexTribeSmallLogo = cursor
						.getColumnIndex(COLUMN_TRIBE_LOGO_SMALL);
				int indexTribeLargeLogo = cursor
						.getColumnIndex(COLUMN_TRIBE_LOGO_LARGE);
				int indexTribeIndustry = cursor
						.getColumnIndex(COLUMN_TRIBE_INDUSTRY);
				int indexTribeType = cursor.getColumnIndex(COLUMN_TRIBE_TYPE);
				int indexTribeContent = cursor
						.getColumnIndex(COLUMN_TRIBE_CONTENT);
				int indexTribeCreateTime = cursor
						.getColumnIndex(COLUMN_TRIBE_CREATETIME);
				int indexTribeRealname = cursor
						.getColumnIndex(COLUMN_TRIBE_REALNAME);
				int indexIsJoin = cursor.getColumnIndex(COLUMN_TRIBE_ISJOIN);
				int indexRole = cursor.getColumnIndex(COLUMN_TRIBE_ROLE);
				int indexCount = cursor.getColumnIndex(COLUMN_TRIBE_COUNT);
				int indexGetMsgType = cursor
						.getColumnIndex(COLUMN_GET_MSG_TYPE);

				do {
					Tribe tribe = new Tribe();
					tribe.name = cursor.getString(indexTribeName);
					tribe.id = cursor.getString(indexTribeID);
					tribe.logosmall = cursor.getString(indexTribeSmallLogo);
					tribe.logolarge = cursor.getString(indexTribeLargeLogo);
					tribe.industry = cursor.getString(indexTribeIndustry);
					tribe.type = cursor.getInt(indexTribeType);
					tribe.content = cursor.getString(indexTribeContent);
					tribe.createtime = cursor.getInt(indexTribeCreateTime);
					tribe.realname = cursor.getString(indexTribeRealname);
					tribe.isjoin = cursor.getInt(indexIsJoin);
					tribe.role = cursor.getInt(indexRole);
					tribe.count = cursor.getInt(indexCount);
					tribe.getmsg = cursor.getInt(indexGetMsgType);

					MessageTable messageTable = new MessageTable(mDBStore);
					List<MessageInfo> messageList = messageTable.query(
							tribe.id, -1, 200);
					if (messageList != null) {
						tribe.mMessageInfo = messageList
								.get(messageList.size() - 1);
						tribe.lastMessageTime = messageList.get(messageList
								.size() - 1).time;
					} else {
						tribe.lastMessageTime = tribe.createtime * 1000;
					}
					tribe.mMessageCount = messageTable
							.queryUnreadCount(tribe.id);

					allInfo.add(tribe);
				} while (cursor.moveToNext());
			}
			mDBStore.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDBStore.endTransaction();
			if (cursor != null) {
				cursor.close();
			}
		}

		return allInfo;
	}

}
