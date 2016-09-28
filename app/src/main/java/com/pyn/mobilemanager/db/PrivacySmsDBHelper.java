package com.pyn.mobilemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建一个数据库用来存隐私通讯录
 */
public class PrivacySmsDBHelper extends SQLiteOpenHelper{

	public PrivacySmsDBHelper(Context context) {
		super(context, "privacysms.db", null, 1);
	}
	
	/**
	 * 第一次创建数据库的时候执行 oncreate方法
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE privacysms (_id integer primary key autoincrement, number varchar(20) not null, name varchar(20))");	
	}

	/**
	 * 更新数据库的操作
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
