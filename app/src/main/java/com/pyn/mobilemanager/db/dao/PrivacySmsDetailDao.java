package com.pyn.mobilemanager.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pyn.mobilemanager.db.PrivacySmsDetailDBHelper;
import com.pyn.mobilemanager.domain.PrivacySmsDetailInfo;

public class PrivacySmsDetailDao {

	private PrivacySmsDetailDBHelper helper;

	public PrivacySmsDetailDao(Context context) {
		helper = new PrivacySmsDetailDBHelper(context);
	}

	/**
	 * ��ѯȫ���Ķ�������
	 */
	public List<PrivacySmsDetailInfo> findAll(String number) {
		// �����Ҫ���صĶ���
		List<PrivacySmsDetailInfo> infos = new ArrayList<PrivacySmsDetailInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();

		if (db.isOpen()) {
			// ��ѯprivacysms���е����к���
			Cursor cursor = db
					.rawQuery(
							"select time, content from privacysmscontent where number = ?",
							new String[] { number });
			// ѭ���������������ÿ���������װ����ӵ�������
			while (cursor.moveToNext()) {
				PrivacySmsDetailInfo info = new PrivacySmsDetailInfo();
				info.setTime(cursor.getString(0));
				info.setContent(cursor.getString(1));
				infos.add(info);
				info = null;
			}
			cursor.close();
			db.close();
		}
		return infos;
	}

	/**
	 * ɾ��һ����Ϣ
	 */
	public void delete(String content) {
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			// ִ��ɾ������
			db.execSQL("delete from privacysmscontent where content=?",
					new Object[] { content });
			db.close();
		}
	}

	/**
	 * ���һ����Ϣ
	 */
	public void add(String number, String time, String content) {

		SQLiteDatabase db = helper.getWritableDatabase();

		if (db.isOpen()) {
			// ִ��������ݵ�SQL���
			db.execSQL(
					"insert into privacysmscontent (number, time, content) values (?,?,?)",
					new Object[] { number, time, content });
			db.close();
		}

	}

}
