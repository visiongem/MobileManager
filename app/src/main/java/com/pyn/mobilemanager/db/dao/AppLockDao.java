package com.pyn.mobilemanager.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pyn.mobilemanager.db.AppLockDBHelper;

/**
 * 对程序锁数据库进行操作的类
 */
public class AppLockDao {

	private Context context;
	private AppLockDBHelper dbHelper;

	public AppLockDao(Context context) {
		this.context = context;
		dbHelper = new AppLockDBHelper(context);
	}

	/**
	 * 查询
	 */
	public boolean find(String packName) {
		boolean result = false;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select packname from applock where packname = ?",
					new String[] { packName });
			if (cursor.moveToNext()) {
				result = true;
			}
			cursor.close();
			db.close();
		}
		return result;
	}

	/**
	 * 添加
	 */
	public void add(String packName) {
		if (find(packName)) {
			return;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("insert into applock (packname) values (?)",
					new Object[] { packName });
			db.close();
		}
	}

	/**
	 * 删除
	 */
	public void delete(String packName) {

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from applock where packname=?",
					new Object[] { packName });
			db.close();
		}
	}

	/**
	 * 查找全部包名
	 */
	public List<String> getAllLockApps() {

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<String> packNames = new ArrayList<String>();

		if (db.isOpen()) {

			Cursor cursor = db.rawQuery("select packname from applock", null);
			while (cursor.moveToNext()) {
				String packname = cursor.getString(0);
				packNames.add(packname);
			}
			cursor.close();
			db.close();
		}
		return packNames;
	}

}
