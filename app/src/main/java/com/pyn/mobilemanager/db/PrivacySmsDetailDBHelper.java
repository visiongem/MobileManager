package com.pyn.mobilemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建一个数据库用来存隐私通讯录中与某个人短信通讯的内容
 */
public class PrivacySmsDetailDBHelper extends SQLiteOpenHelper {

	public PrivacySmsDetailDBHelper(Context context) {
		super(context, "privacysmscontent.db", null, 1);
	}

	/**
	 * 第一次创建数据库的时候执行 oncreate方法
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE privacysmscontent (_id integer primary key autoincrement,"
				+ " number varchar(20) , time varchar(20), content varchar(1000))");
	}

	/**
	 * 更新数据库的操作
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
