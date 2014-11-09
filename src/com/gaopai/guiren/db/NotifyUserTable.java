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
import com.gaopai.guiren.bean.User;

public class NotifyUserTable {

	public static final String TABLE_NAME = "NotifyUserTable";//数据表的名称

	public static final String COLUMN_NOTIFY_ID = "notifyID";
	public static final String COLUMN_UID = "uid";
	public static final String COLUMN_LOGIN_ID = "loginId";
	public static final String COLUMN_SAMLL_FACE = "samllface";
	public static final String COLUMN_LARGE_FACE = "largeface";
	public static final String COLUMN_USER_NAME = "userName";
	public static final String COLUMN_REAL_NAME = "realName";
	
	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String PRIMARY_KEY_TYPE = "primary key(";
	
	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;
	
	public NotifyUserTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}
	
	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {

			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_NOTIFY_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_UID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_LOGIN_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_USER_NAME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_SAMLL_FACE, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_LARGE_FACE, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_REAL_NAME, COLUMN_TEXT_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_NOTIFY_ID + "," + COLUMN_UID + "," + COLUMN_LOGIN_ID + ")";

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
				if(user.uid == null || user.uid.equals("")){
					continue;
				}
				ContentValues allPromotionInfoValues = new ContentValues();
				
				allPromotionInfoValues.put(COLUMN_UID, user.uid);
				allPromotionInfoValues.put(COLUMN_LOGIN_ID, DamiCommon.getUid(DamiApp.getInstance()));
				allPromotionInfoValues.put(COLUMN_USER_NAME, user.nickname);
				allPromotionInfoValues.put(COLUMN_REAL_NAME, user.realname);
				allPromotionInfoValues.put(COLUMN_SAMLL_FACE, user.headsmall);
				allPromotionInfoValues.put(COLUMN_LARGE_FACE, user.headlarge);
				//delete(user);
				try {
					mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
				} catch (SQLiteConstraintException e) {
					e.printStackTrace();
				}
			}	
			mDBStore.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			mDBStore.endTransaction();
		}
		
	}
	
	public void update(String notifyID, User user) {
		if(user.uid == null || user.uid.equals("")){
			return;
		}
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_USER_NAME, user.nickname);
		allPromotionInfoValues.put(COLUMN_REAL_NAME, user.realname);
		allPromotionInfoValues.put(COLUMN_SAMLL_FACE, user.headsmall);
		allPromotionInfoValues.put(COLUMN_LARGE_FACE, user.headlarge);
		
		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_NOTIFY_ID + "='" + notifyID + "' AND " + COLUMN_UID + " = '" + user.uid + "' AND " + COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}						
	}
	
	public void insert(String notifyID, User user) {
		if(user.uid == null || user.uid.equals("")){
			return;
		}
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_NOTIFY_ID, notifyID);
		allPromotionInfoValues.put(COLUMN_UID, user.uid);
		allPromotionInfoValues.put(COLUMN_LOGIN_ID, DamiCommon.getUid(DamiApp.getInstance()));
		allPromotionInfoValues.put(COLUMN_USER_NAME, user.nickname);
		allPromotionInfoValues.put(COLUMN_REAL_NAME, user.realname);
		allPromotionInfoValues.put(COLUMN_SAMLL_FACE, user.headsmall);
		allPromotionInfoValues.put(COLUMN_LARGE_FACE, user.headlarge);
		delete(notifyID, user);
		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}					
	}
	
	public void delete(String notifyID, User user) {
		if(user.uid == null){
			return;
		}
		mDBStore.delete(TABLE_NAME, COLUMN_NOTIFY_ID + "='" + notifyID + "' AND " + COLUMN_UID + "='" + user.uid + "' AND " + COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
	}
	
	public User query(String notifyID, String uid){
		User user = new User();
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NOTIFY_ID + "='" + notifyID + "' AND " + COLUMN_UID + "='" + uid + "' AND " + COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
			if (cursor != null) {
				
				if (!cursor.moveToFirst()) {
					return null;
				}
				
				int indexUId = cursor.getColumnIndex(COLUMN_UID);
				int indexUserName = cursor.getColumnIndex(COLUMN_USER_NAME);
				int indexRealName = cursor.getColumnIndex(COLUMN_REAL_NAME);
				int indexSamllFace = cursor.getColumnIndex(COLUMN_SAMLL_FACE);
				int indexLargeFace = cursor.getColumnIndex(COLUMN_LARGE_FACE);
				
				user.uid = cursor.getString(indexUId);
				user.nickname = cursor.getString(indexUserName);
				user.realname = cursor.getString(indexRealName);
				
				if(!TextUtils.isEmpty(user.realname)){
					user.displayName = user.realname;
				}else {
					user.displayName = user.nickname;
				}
				
				user.headsmall = cursor.getString(indexSamllFace);
				user.headlarge = cursor.getString(indexLargeFace);
				
				return user;
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
	
	public List<User> queryList() {
		List<User> allInfo = new ArrayList<User>();
		Cursor cursor = null;
		mDBStore.beginTransaction();
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'" , null);
			if (cursor != null) {
				
				if (!cursor.moveToFirst()) {
					return null;
				}
				
				int indexUId = cursor.getColumnIndex(COLUMN_UID);
				int indexUserName = cursor.getColumnIndex(COLUMN_USER_NAME);
				int indexRealName = cursor.getColumnIndex(COLUMN_REAL_NAME);
				int indexSamllFace = cursor.getColumnIndex(COLUMN_SAMLL_FACE);
				int indexLargeFace = cursor.getColumnIndex(COLUMN_LARGE_FACE);
				
				do {
					User user = new User();
					user.uid = cursor.getString(indexUId);
					user.nickname = cursor.getString(indexUserName);
					user.realname = cursor.getString(indexRealName);
					
					if(!TextUtils.isEmpty(user.realname)){
						user.displayName = user.realname;
					}else {
						user.displayName = user.nickname;
					}
					
					user.headsmall = cursor.getString(indexSamllFace);
					user.headlarge = cursor.getString(indexLargeFace);
					
					allInfo.add(user);
				} while (cursor.moveToNext());
			}
			mDBStore.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
			mDBStore.endTransaction();
		}
		
		return allInfo;
	}
	
}
