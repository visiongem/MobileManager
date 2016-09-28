package com.pyn.mobilemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * ����һ�����ݿ�������ÿ��ʹ�õ�������Ϣ
 */
public class FlowMonitorDBHelper extends SQLiteOpenHelper {

	public FlowMonitorDBHelper(Context context) {
		super(context, "flow.db", null, 1);
	}

	/**
	 * ��һ�δ������ݿ��ʱ��ִ�� oncreate����
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE flow (_id integer primary key autoincrement, flow varchar(20), month varchar(10))");
	}

	/**
	 * �������ݿ�Ĳ���
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
