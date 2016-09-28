package com.pyn.mobilemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * ����һ�����ݿ���������˽ͨѶ¼
 */
public class PrivacySmsDBHelper extends SQLiteOpenHelper{

	public PrivacySmsDBHelper(Context context) {
		super(context, "privacysms.db", null, 1);
	}
	
	/**
	 * ��һ�δ������ݿ��ʱ��ִ�� oncreate����
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE privacysms (_id integer primary key autoincrement, number varchar(20) not null, name varchar(20))");	
	}

	/**
	 * �������ݿ�Ĳ���
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
