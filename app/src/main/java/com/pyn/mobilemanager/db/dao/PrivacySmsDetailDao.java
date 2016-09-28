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
	 * 查询全部的短信详情
	 */
	public List<PrivacySmsDetailInfo> findAll(String number) {
		// 定义好要返回的对象
		List<PrivacySmsDetailInfo> infos = new ArrayList<PrivacySmsDetailInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();

		if (db.isOpen()) {
			// 查询privacysms表中的所有号码
			Cursor cursor = db
					.rawQuery(
							"select time, content from privacysmscontent where number = ?",
							new String[] { number });
			// 循环遍历结果集，将每个结果集封装后添加到集合中
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
	 * 删除一条信息
	 */
	public void delete(String content) {
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			// 执行删除操作
			db.execSQL("delete from privacysmscontent where content=?",
					new Object[] { content });
			db.close();
		}
	}

	/**
	 * 添加一条信息
	 */
	public void add(String number, String time, String content) {

		SQLiteDatabase db = helper.getWritableDatabase();

		if (db.isOpen()) {
			// 执行添加数据的SQL语句
			db.execSQL(
					"insert into privacysmscontent (number, time, content) values (?,?,?)",
					new Object[] { number, time, content });
			db.close();
		}

	}

}
