package com.mlh.database;

import com.mlh.Config;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Matteo
 *SQLite数据库连接类，采用单例模式
 */
public class DataBaseConnection {
	private static final String dbName="mlh.db3";
	private static final String CreateLocationHistoryTableSQL = "create table location_history(guid long primary key,"
			+ "createtime TIMESTAMP default(datetime('now', 'localtime')),"
			+ "longitude double,latitude double,altitude double,"
			+ "accuracy double,speed float,bearing float,"
			+"address nchar(64),province nchar(16),city nchar(16),district nchar(20),street nchar(16),streetnum nchar(10),"
			+ "uploaded bit default(0)) ";
	private static final String CreateLogTableSQL = "create table matteo_log(guid integer primary key autoincrement,"
			+ "createtime TIMESTAMP default(datetime('now','localtime')),"
			+ "description char(256))";
	private static final String CreatePictureTableSQL = "create table picture(guid long primary key,"
			+ "createtime TIMESTAMP default(datetime('now','localtime')),loc_guid long,"
			+ "filename char(20),uploaded bit default(0))";
	
	private static DataBaseConnection dbConn=null;
	private SQLiteDatabase sqlLiteDb;
	private String dbPath;
	
	/**
	 * 实例化一个数据库连接，如果数据库不存在，则初始化数据库，生成相应的表
	 */
	private DataBaseConnection(){
		this.dbPath=Config.getDirectoryPath()+DataBaseConnection.dbName;
		this.sqlLiteDb=SQLiteDatabase.openOrCreateDatabase(this.dbPath,null);
		try {
			this.sqlLiteDb.rawQuery("select count(*) as c from location_history",null);
		} catch (Exception e) {
			this.sqlLiteDb.execSQL(DataBaseConnection.CreateLocationHistoryTableSQL);
			this.sqlLiteDb.execSQL(DataBaseConnection.CreateLogTableSQL);
			this.sqlLiteDb.execSQL(DataBaseConnection.CreatePictureTableSQL);
		}
	}
	
	/**
	 * 获取数据库连接单例
	 */
	private static DataBaseConnection getInstance(){
		if(DataBaseConnection.dbConn==null){
			DataBaseConnection.InitDb();
		}
		return DataBaseConnection.dbConn;
	}
	
	/**
	 * 初始化数据库连接实例
	 */
	private static synchronized void InitDb(){
		if(DataBaseConnection.dbConn==null){
			DataBaseConnection.dbConn=new DataBaseConnection();
		}
	}
	
	/**
	 * 执行sql语句
	 */
	public static void execute(String sql){
		DataBaseConnection.getInstance().sqlLiteDb.execSQL(sql);
	}
	
	/**
	 * 查询sql语句
	 */
	public static Cursor query(String sql){
		return DataBaseConnection.getInstance().sqlLiteDb.rawQuery(sql, null);
	}
	
	/**
	 * 插入一个条记录，并获取该记录的ID
	 * @param insertSql 执行插入操作的sql语句
	 * @param querySql 执行获取最新插入记录ID的sql语句
	 * @return 最新插入的记录的ID
	 */
	public static synchronized long insertAndGetID(String insertSql,String querySql){
		DataBaseConnection.execute(insertSql);
		Cursor cur=DataBaseConnection.query(querySql);
		cur.moveToFirst();
		long l=cur.getLong(0);
		return l;
	}
}
