package com.pyn.mobilemanager.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.pyn.mobilemanager.db.PrivacySmsDBHelper;
import com.pyn.mobilemanager.domain.PrivacySmsInfo;

public class PrivacySmsDao {
	
	private PrivacySmsDBHelper helper;
	
	public PrivacySmsDao(Context context) {
		helper = new PrivacySmsDBHelper(context);
	}
	
	/**
	 * 查找一条隐私保护号码（其返回值是用于判断数据库中是否存在该号码）
	 */
	public boolean find(String number) {
		// 默认情况下是没有该条数据
		boolean result = false;
		// 打开数据库
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			// 执行查询语句后，返回一个结果集
			Cursor cursor = db.rawQuery(
					"select * from privacysms where number =?",
					new String[] { number });
			// 默认情况下，游标指针指向在第一条数据的上方
			if (cursor.moveToFirst()) {
				// 返回true，说明数据库中已经存在了该条数据
				result = true;
			}
			// 关闭数据库
			cursor.close();
			db.close();
		}
		return result;
	}
	
	/**
	 * 查询全部的隐私保护名单
	 */
	public List<PrivacySmsInfo> findAll(){
		//定义好要返回的对象
		List<PrivacySmsInfo> infos = new ArrayList<PrivacySmsInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		
		if(db.isOpen()){
			// 查询privacysms表中的所有号码
			Cursor cursor = db.rawQuery("select number, name from privacysms", null);
			// 循环遍历结果集，将每个结果集封装后添加到集合中
			while (cursor.moveToNext()) {
				PrivacySmsInfo info = new PrivacySmsInfo();
				info.setNumber(cursor.getString(0));
				info.setName(cursor.getString(1));
				infos.add(info);
				info = null;
			}
			cursor.close();
			db.close();
		}
		return infos;
	}
	
	/**
	 * 更改隐私保护号码
	 * @param oldnumber
	 *            旧的的电话号码
	 * @param newnumber
	 *            新的号码 可以留空
	 * @param name
	 *            新的名字
	 */
	public void update(String oldnumber, String newnumber, String name) {

		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			if (TextUtils.isEmpty(newnumber)) {
				// 如果新的号码为空的话，则说明用户并没有修改该号码（ListView中的item设置有删除功能）
				newnumber = oldnumber;
			}
			// 执行更新操作
			db.execSQL(
					"update privacysms set number=?, name=? where number=?",
					new Object[] { newnumber, name, oldnumber });
			db.close();
		}
	}
	
	/**
	 * 删除一条隐私保护号码
	 */
	public void delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			// 执行删除操作
			db.execSQL("delete from privacysms where number=?",
					new Object[] { number });
			db.close();
		}
	}
	
	/**
	 * 添加一条隐私保护号码
	 */
	public boolean add(String number, String name) {
		// 首先判断数据库中是否已经存在该条数据， 防止添加重复的数据显示到黑名单列表中
		if (find(number))
			// 如果数据库中已经存在要添加的数据，直接停止掉该方法的执行
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			// 执行添加数据的SQL语句
			db.execSQL("insert into privacysms (number, name) values (?,?)",
					new Object[] { number, name });
			db.close();
		}
		// 如果代码能够执行到这一步，说明上面的添加操作也执行了。所以查询的返回值必定为true
		return find(number);
	}
	
}
