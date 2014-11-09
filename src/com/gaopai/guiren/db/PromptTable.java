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
import com.gaopai.guiren.bean.CalenderPrompt;

public class PromptTable {

	public static final String TABLE_NAME = "PromptTable";//数据表的名称

	public static final String COLUMN_EVENT_ID = "notifyID";
	public static final String COLUMN_LOGIN_ID = "loginId";
	public static final String COLUMN_START_TIME = "startTime";
	public static final String COLUMN_END_TIME = "endTime";
	public static final String COLUMN_PROMPT_TIME = "promptTime";
	public static final String COLUMN_PROMPT_TITLE = "promptTitle";
	
	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String PRIMARY_KEY_TYPE = "primary key(";
	
	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;
	
	public PromptTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}
	
	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {

			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_EVENT_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_LOGIN_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_START_TIME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_END_TIME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_PROMPT_TIME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_PROMPT_TITLE, COLUMN_TEXT_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_EVENT_ID + ")";

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
	
	public void insert(List<CalenderPrompt> prompts) {
		List<CalenderPrompt> promptList = new ArrayList<CalenderPrompt>();
		promptList.addAll(prompts);
		mDBStore.beginTransaction();
		try {
			for (CalenderPrompt prompt : promptList) {
				if(TextUtils.isEmpty(prompt.id)){
					continue;
				}
				ContentValues allPromotionInfoValues = new ContentValues();
				
				allPromotionInfoValues.put(COLUMN_EVENT_ID, prompt.id);
				allPromotionInfoValues.put(COLUMN_LOGIN_ID, DamiCommon.getUid(DamiApp.getInstance()));
				allPromotionInfoValues.put(COLUMN_START_TIME, String.valueOf(prompt.mStartTime));
				allPromotionInfoValues.put(COLUMN_END_TIME, String.valueOf(prompt.mEndTime));
				allPromotionInfoValues.put(COLUMN_PROMPT_TIME, String.valueOf(prompt.mTime));
				allPromotionInfoValues.put(COLUMN_PROMPT_TITLE, prompt.mTitle);
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
	
	public void update(CalenderPrompt prompt) {
		if(TextUtils.isEmpty(prompt.id)){
			return;
		}
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_PROMPT_TIME, String.valueOf(prompt.mTime));
		
		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_EVENT_ID + "='" + prompt.id + "' AND " + COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}						
	}
	
	public void insert(CalenderPrompt prompt) {
		if(TextUtils.isEmpty(prompt.id)){
			return;
		}
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_EVENT_ID, prompt.id);
		allPromotionInfoValues.put(COLUMN_LOGIN_ID, DamiCommon.getUid(DamiApp.getInstance()));
		allPromotionInfoValues.put(COLUMN_START_TIME, String.valueOf(prompt.mStartTime));
		allPromotionInfoValues.put(COLUMN_END_TIME, String.valueOf(prompt.mEndTime));
		allPromotionInfoValues.put(COLUMN_PROMPT_TIME, String.valueOf(prompt.mTime));
		allPromotionInfoValues.put(COLUMN_PROMPT_TITLE, prompt.mTitle);
		delete(prompt);
		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}					
	}
	
	public void delete(CalenderPrompt prompt) {
		if(TextUtils.isEmpty(prompt.id + "")){
			return;
		}
		mDBStore.delete(TABLE_NAME, COLUMN_EVENT_ID + "='" + prompt.id + "' AND " + COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
	}
	
	public CalenderPrompt query(String id){
		CalenderPrompt prompt = new CalenderPrompt();
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EVENT_ID + "='" + id + "' AND " + COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'", null);
			if (cursor != null) {
				
				if (!cursor.moveToFirst()) {
					return null;
				}
				
				int indexId = cursor.getColumnIndex(COLUMN_EVENT_ID);
				int indexName = cursor.getColumnIndex(COLUMN_PROMPT_TITLE);
				int indexStartTime = cursor.getColumnIndex(COLUMN_START_TIME);
				int indexEndTime = cursor.getColumnIndex(COLUMN_END_TIME);
				int indexPromptTime = cursor.getColumnIndex(COLUMN_PROMPT_TIME);
				
				prompt.id = cursor.getString(indexId);
				prompt.mTitle = cursor.getString(indexName);
				prompt.mStartTime = Long.parseLong(cursor.getString(indexStartTime));
				prompt.mEndTime = Long.parseLong(cursor.getString(indexEndTime));
				prompt.mTime = Long.parseLong(cursor.getString(indexPromptTime));
				
				return prompt;
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
	
	public List<CalenderPrompt> queryList() {
		List<CalenderPrompt> allInfo = new ArrayList<CalenderPrompt>();
		Cursor cursor = null;
		mDBStore.beginTransaction();
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID + "='" + DamiCommon.getUid(DamiApp.getInstance()) + "'" , null);
			if (cursor != null) {
				
				if (!cursor.moveToFirst()) {
					return null;
				}
				
				int indexId = cursor.getColumnIndex(COLUMN_EVENT_ID);
				int indexName = cursor.getColumnIndex(COLUMN_PROMPT_TITLE);
				int indexStartTime = cursor.getColumnIndex(COLUMN_START_TIME);
				int indexEndTime = cursor.getColumnIndex(COLUMN_END_TIME);
				int indexPromptTime = cursor.getColumnIndex(COLUMN_PROMPT_TIME);
				
				do {
					CalenderPrompt prompt = new CalenderPrompt();
					prompt.id = cursor.getString(indexId);
					prompt.mTitle = cursor.getString(indexName);
					prompt.mStartTime = Long.parseLong(cursor.getString(indexStartTime));
					prompt.mEndTime = Long.parseLong(cursor.getString(indexEndTime));
					prompt.mTime = Long.parseLong(cursor.getString(indexPromptTime));
					
					allInfo.add(prompt);
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
