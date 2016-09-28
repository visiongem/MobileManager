package com.pyn.mobilemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * ����һ�����ݿ���������˽ͨѶ¼����ĳ���˶���ͨѶ������
 */
public class PrivacySmsDetailDBHelper extends SQLiteOpenHelper {

	public PrivacySmsDetailDBHelper(Context context) {
		super(context, "privacysmscontent.db", null, 1);
	}

	/**
	 * ��һ�δ������ݿ��ʱ��ִ�� oncreate����
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE privacysmscontent (_id integer primary key autoincrement,"
				+ " number varchar(20) , time varchar(20), content varchar(1000))");
	}

	/**
	 * �������ݿ�Ĳ���
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
