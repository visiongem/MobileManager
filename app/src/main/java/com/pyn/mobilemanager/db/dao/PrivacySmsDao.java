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
	 * ����һ����˽�������루�䷵��ֵ�������ж����ݿ����Ƿ���ڸú��룩
	 */
	public boolean find(String number) {
		// Ĭ���������û�и�������
		boolean result = false;
		// �����ݿ�
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			// ִ�в�ѯ���󣬷���һ�������
			Cursor cursor = db.rawQuery(
					"select * from privacysms where number =?",
					new String[] { number });
			// Ĭ������£��α�ָ��ָ���ڵ�һ�����ݵ��Ϸ�
			if (cursor.moveToFirst()) {
				// ����true��˵�����ݿ����Ѿ������˸�������
				result = true;
			}
			// �ر����ݿ�
			cursor.close();
			db.close();
		}
		return result;
	}
	
	/**
	 * ��ѯȫ������˽��������
	 */
	public List<PrivacySmsInfo> findAll(){
		//�����Ҫ���صĶ���
		List<PrivacySmsInfo> infos = new ArrayList<PrivacySmsInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		
		if(db.isOpen()){
			// ��ѯprivacysms���е����к���
			Cursor cursor = db.rawQuery("select number, name from privacysms", null);
			// ѭ���������������ÿ���������װ����ӵ�������
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
	 * ������˽��������
	 * @param oldnumber
	 *            �ɵĵĵ绰����
	 * @param newnumber
	 *            �µĺ��� ��������
	 * @param name
	 *            �µ�����
	 */
	public void update(String oldnumber, String newnumber, String name) {

		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			if (TextUtils.isEmpty(newnumber)) {
				// ����µĺ���Ϊ�յĻ�����˵���û���û���޸ĸú��루ListView�е�item������ɾ�����ܣ�
				newnumber = oldnumber;
			}
			// ִ�и��²���
			db.execSQL(
					"update privacysms set number=?, name=? where number=?",
					new Object[] { newnumber, name, oldnumber });
			db.close();
		}
	}
	
	/**
	 * ɾ��һ����˽��������
	 */
	public void delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			// ִ��ɾ������
			db.execSQL("delete from privacysms where number=?",
					new Object[] { number });
			db.close();
		}
	}
	
	/**
	 * ���һ����˽��������
	 */
	public boolean add(String number, String name) {
		// �����ж����ݿ����Ƿ��Ѿ����ڸ������ݣ� ��ֹ����ظ���������ʾ���������б���
		if (find(number))
			// ������ݿ����Ѿ�����Ҫ��ӵ����ݣ�ֱ��ֹͣ���÷�����ִ��
			return false;
		
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			// ִ��������ݵ�SQL���
			db.execSQL("insert into privacysms (number, name) values (?,?)",
					new Object[] { number, name });
			db.close();
		}
		// ��������ܹ�ִ�е���һ����˵���������Ӳ���Ҳִ���ˡ����Բ�ѯ�ķ���ֵ�ض�Ϊtrue
		return find(number);
	}
	
}
