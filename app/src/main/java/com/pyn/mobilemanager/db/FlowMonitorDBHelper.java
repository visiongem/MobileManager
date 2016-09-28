package com.pyn.mobilemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建一个数据库用来存每月使用的流量信息
 */
public class FlowMonitorDBHelper extends SQLiteOpenHelper {

	public FlowMonitorDBHelper(Context context) {
		super(context, "flow.db", null, 1);
	}

	/**
	 * 第一次创建数据库的时候执行 oncreate方法
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE flow (_id integer primary key autoincrement, flow varchar(20), month varchar(10))");
	}

	/**
	 * 更新数据库的操作
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
