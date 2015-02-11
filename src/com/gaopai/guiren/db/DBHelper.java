package com.gaopai.guiren.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private SQLiteDatabase mDB = null;
	private static DBHelper mInstance = null;
	public static final String DataBaseName = "Dami.db";
	public static final int DataBaseVersion = 12;

	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (null == mDB) {
			mDB = db;
		}
		db.execSQL(SessionTable.getCreateTableSQLString());
		db.execSQL(MessageTable.getCreateTableSQLString());
		db.execSQL(NotifyUserTable.getCreateTableSQLString());
		db.execSQL(NotifyTable.getCreateTableSQLString());
		db.execSQL(TribeTable.getCreateTableSQLString());
		db.execSQL(NotifyRoomTable.getCreateTableSQLString());
		db.execSQL(NotifyMessageTable.getCreateTableSQLString());
		db.execSQL(PromptTable.getCreateTableSQLString());
		db.execSQL(IdentityTable.getCreateTableSQLString());
		db.execSQL(ConverseationTable.getCreateTableSQLString());
		// db.execSQL(ContactUserTable.getCreateTableSQLString());
	}

	public synchronized static DBHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DBHelper(context, DataBaseName, null, DataBaseVersion);
		}

		return mInstance;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 11) {
			deleteAllTables(db);
			onCreate(db);
		} else {
			upgrade11to12(db);
		}

	}

	private void upgrade11to12(SQLiteDatabase db) {
		String addNotifyRoomRoleColumn = "ALTER TABLE " + NotifyRoomTable.TABLE_NAME + " ADD COLUMN "
				+ NotifyRoomTable.COLUMN_ROLE + " TEXT";
		db.execSQL(addNotifyRoomRoleColumn);

	}

	private void deleteAllTables(SQLiteDatabase db) {
		db.execSQL(SessionTable.getDeleteTableSQLString());
		db.execSQL(MessageTable.getDeleteTableSQLString());
		db.execSQL(NotifyTable.getDeleteTableSQLString());
		db.execSQL(NotifyUserTable.getDeleteTableSQLString());
		db.execSQL(TribeTable.getDeleteTableSQLString());
		db.execSQL(NotifyRoomTable.getDeleteTableSQLString());
		db.execSQL(NotifyMessageTable.getDeleteTableSQLString());
		db.execSQL(PromptTable.getDeleteTableSQLString());
		db.execSQL(IdentityTable.getDeleteTableSQLString());
		// db.execSQL(ContactUserTable.getCreateTableSQLString());
	}

	@Override
	public synchronized void close() {
		if (mDB != null) {
			mDB.close();
		}
		super.close();
	}
}
