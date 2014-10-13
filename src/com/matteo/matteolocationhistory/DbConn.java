package com.matteo.matteolocationhistory;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class DbConn {
	private SQLiteDatabase db;
	private Cursor cursor;
	public int Count;
	private static final String CreateLocationHistoryTableSQL = "create table location_history(guid integer primary key autoincrement,"
			+ "createtime TIMESTAMP default(datetime('now', 'localtime')),"
			+ "longitude double,latitude double,altitude double,"
			+ "accuracy double,speed float,bearing float,"
			+"address nchar(64),province nchar(16),city nchar(16),district nchar(20),street nchar(16),streetnum nchar(10),"
			+ "uploaded bit default(0)) ";
	private static final String CreateLogTableSQL = "create table matteo_log(guid integer primary key autoincrement,"
			+ "createtime TIMESTAMP default(datetime('now','localtime')),"
			+ "description char(256))";
	private static final String CreatePictureTableSQL = "create table picture(guid integer primary key autoincrement,"
			+ "createtime TIMESTAMP default(datetime('now','localtime')),loc_guid integer,"
			+ "filename char(20),uploaded bit default(0))";

	public DbConn() {
		db = SQLiteDatabase.openOrCreateDatabase(Matteo.DbPath, null);
		try {
			cursor = db.rawQuery("select count(*) as c from location_history",
					null);
			cursor.moveToFirst();
			this.Count = cursor.getInt(0);
		} catch (Exception e) {
			db.execSQL(DbConn.CreateLocationHistoryTableSQL);
			db.execSQL(DbConn.CreateLogTableSQL);
			db.execSQL(DbConn.CreatePictureTableSQL);
			this.Count = 0;
		}
	}

	public void Execute(String sql) {
		db.execSQL(sql);
	}

	public Cursor Query(String sql) {
		return db.rawQuery(sql, null);
	}

	public void Close() {
		if (this.db != null && this.db.isOpen()) {
			this.db.close();
		}
	}

	public static int GetMaxGuid(String tab) {
		try {
			String sql = "select max(guid) as m from " + tab;
			Cursor cur = Matteo.MtDb.Query(sql);
			cur.moveToFirst();
			return cur.getInt(0);
		} catch (Exception e) {
			return 0;
		}
	}
}
