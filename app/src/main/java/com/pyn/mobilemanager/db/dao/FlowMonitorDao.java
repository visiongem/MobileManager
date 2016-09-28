package com.pyn.mobilemanager.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pyn.mobilemanager.db.FlowMonitorDBHelper;

/**
 * �Դ洢��ÿ��ʹ���������ݵ����ݿ���в�������
 */
public class FlowMonitorDao {

	private Context context;
	private FlowMonitorDBHelper dbHelper;

	public FlowMonitorDao(Context context) {
		this.context = context;
		dbHelper = new FlowMonitorDBHelper(context);
	}

	/**
	 * �����·�����ѯ�Ƿ���д��·ݵ�������Ϣ
	 */
	public boolean find(String month) {
		boolean result = false;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select flow from flow where month = ?",
					new String[] { month });
			if (cursor.moveToNext()) {
				result = true;
			}
			cursor.close();
			db.close();
		}
		return result;
	}

	/**
	 * ���ĳ��������ʹ�����
	 */
	public void add(String flow, String month) {

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("insert into flow (flow, month) values (?, ?)",
					new Object[] { flow, month });
			db.close();
		}
	}

	/**
	 * ����ĳ��������ʹ�����
	 * 
	 * @param month
	 *            �·�
	 * @return ĳ����ʹ�õ���������
	 */
	public String getFlow(String month) {

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String flow = null;

		if (db.isOpen()) {

			Cursor cursor = db.rawQuery(
					"select flow from flow where month = ?",
					new String[] { month });
			while (cursor.moveToNext()) {
				flow = cursor.getString(0);
			}
			cursor.close();
			db.close();
		}
		return flow;
	}

	/**
	 * ����ĳ����������С
	 * 
	 * @param month
	 * @return
	 */
	public boolean updateFlow(String flow, String month) {

		boolean result = false;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		if (db.isOpen()) {

			db.execSQL("update flow set flow = ? where month = ?",
					new String[] { flow, month });
			result = true;
			db.close();
		}
		return result;
	}

}
