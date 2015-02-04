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
import com.gaopai.guiren.bean.Tribe;

public class NotifyRoomTable {

	public static final String TABLE_NAME = "NotifyRoomTable";//数据表的名称

	public static final String COLUMN_NOTIFY_ID = "notifyID";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_LOGIN_ID = "loginId";
	public static final String COLUMN_SAMLL_FACE = "samlllogo";
	public static final String COLUMN_ATTACH_NAME = "attachName";
	public static final String COLUMN_ATTACH_CONTENT = "attachContent";
	public static final String COLUMN_ROLE = "role";
	
	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String PRIMARY_KEY_TYPE = "primary key(";
	
	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;
	
	public NotifyRoomTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}
	
	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {

			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_NOTIFY_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_LOGIN_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_ATTACH_NAME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_SAMLL_FACE, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_ATTACH_CONTENT, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_ROLE, COLUMN_INTEGER_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_NOTIFY_ID + "," + COLUMN_ID + "," + COLUMN_LOGIN_ID + ")";

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
	
	public void insert(List<Tribe> tribes) {
		List<Tribe> attachList = new ArrayList<Tribe>();
		attachList.addAll(tribes);
		mDBStore.beginTransaction();
		try {
			for (Tribe tribe : attachList) {
				if(TextUtils.isEmpty(tribe.id)){
					continue;
				}
				ContentValues allPromotionInfoValues = new ContentValues();
				
				allPromotionInfoValues.put(COLUMN_ID, tribe.id);
				allPromotionInfoValues.put(COLUMN_LOGIN_ID, DamiCommon.getUid(DamiApp.getInstance()));
				allPromotionInfoValues.put(COLUMN_ATTACH_NAME, tribe.name);
				allPromotionInfoValues.put(COLUMN_SAMLL_FACE, tribe.logosmall);
				allPromotionInfoValues.put(COLUMN_ATTACH_CONTENT, tribe.content);
				allPromotionInfoValues.put(COLUMN_ROLE, tribe.role);
				//delete(tribe);
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
	
	public void update(String notifyID, Tribe tribe) {
		if(TextUtils.isEmpty(tribe.id)){
			return;
		}
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_ATTACH_NAME, tribe.name);
		allPromotionInfoValues.put(COLUMN_SAMLL_FACE, tribe.logosmall);
		allPromotionInfoValues.put(COLUMN_ATTACH_CONTENT, tribe.content);
		allPromotionInfoValues.put(COLUMN_ROLE, tribe.role);
		
		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_NOTIFY_ID + "='" + notifyID + "' AND " + COLUMN_ID + " = '" + tribe.id + "' AND " + COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}						
	}
	
	public void insert(String notifyID, Tribe tribe) {
		if(TextUtils.isEmpty(tribe.id)){
			return;
		}
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_NOTIFY_ID, notifyID);
		allPromotionInfoValues.put(COLUMN_ID, tribe.id);
		allPromotionInfoValues.put(COLUMN_LOGIN_ID, DamiCommon.getUid(DamiApp.getInstance()));
		allPromotionInfoValues.put(COLUMN_ATTACH_NAME, tribe.name);
		allPromotionInfoValues.put(COLUMN_ATTACH_CONTENT, tribe.content);
		allPromotionInfoValues.put(COLUMN_ROLE, tribe.role);
		delete(notifyID, tribe);
		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}					
	}
	
	public void delete(String notifyID, Tribe tribe) {
		if(TextUtils.isEmpty(tribe.id)){
			return;
		}
		mDBStore.delete(TABLE_NAME, COLUMN_NOTIFY_ID + "='" + notifyID + "' AND " + COLUMN_ID + "='" + tribe.id + "' AND " + COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
	}
	
	public Tribe query(String notifyID, String id){
		Tribe tribe = new Tribe();
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NOTIFY_ID + "='" + notifyID + "' AND " + COLUMN_ID + "='" + id + "' AND " + COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
			if (cursor != null) {
				
				if (!cursor.moveToFirst()) {
					return null;
				}
				
				int indexId = cursor.getColumnIndex(COLUMN_ID);
				int indexName = cursor.getColumnIndex(COLUMN_ATTACH_NAME);
				int indexSamllFace = cursor.getColumnIndex(COLUMN_SAMLL_FACE);
				int indexContent = cursor.getColumnIndex(COLUMN_ATTACH_CONTENT);
				int indexRole = cursor.getColumnIndex(COLUMN_ROLE);
				
				tribe.id = cursor.getString(indexId);
				tribe.name = cursor.getString(indexName);
				tribe.logosmall = cursor.getString(indexSamllFace);
				tribe.content = cursor.getString(indexContent);
				tribe.role = cursor.getInt(indexRole);
				
				return tribe;
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
	
	public List<Tribe> queryList() {
		List<Tribe> allInfo = new ArrayList<Tribe>();
		Cursor cursor = null;
		mDBStore.beginTransaction();
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'" , null);
			if (cursor != null) {
				
				if (!cursor.moveToFirst()) {
					return null;
				}
				
				int indexId = cursor.getColumnIndex(COLUMN_ID);
				int indexName = cursor.getColumnIndex(COLUMN_ATTACH_NAME);
				int indexSamllFace = cursor.getColumnIndex(COLUMN_SAMLL_FACE);
				int indexContent = cursor.getColumnIndex(COLUMN_ATTACH_CONTENT);
				int indexRole = cursor.getColumnIndex(COLUMN_ROLE);
				
				do {
					Tribe tribe = new Tribe();
					tribe.id = cursor.getString(indexId);
					tribe.name = cursor.getString(indexName);
					tribe.logosmall = cursor.getString(indexSamllFace);
					tribe.content = cursor.getString(indexContent);
					tribe.role = cursor.getInt(indexRole);
					
					allInfo.add(tribe);
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
