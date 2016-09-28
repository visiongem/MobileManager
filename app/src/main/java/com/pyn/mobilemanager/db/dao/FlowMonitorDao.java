package com.pyn.mobilemanager.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pyn.mobilemanager.db.FlowMonitorDBHelper;

/**
 * 对存储了每月使用流量数据的数据库进行操作的类
 */
public class FlowMonitorDao {

	private Context context;
	private FlowMonitorDBHelper dbHelper;

	public FlowMonitorDao(Context context) {
		this.context = context;
		dbHelper = new FlowMonitorDBHelper(context);
	}

	/**
	 * 根据月份来查询是否存有此月份的流量信息
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
	 * 添加某个月流量使用情况
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
	 * 查找某个月流量使用情况
	 * 
	 * @param month
	 *            月份
	 * @return 某个月使用的流量总数
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
	 * 更新某个月流量大小
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
