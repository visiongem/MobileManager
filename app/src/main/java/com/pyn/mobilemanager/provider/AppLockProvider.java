package com.pyn.mobilemanager.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.pyn.mobilemanager.db.dao.AppLockDao;

/**
 * 程序锁的内容提供者
 */
public class AppLockProvider extends ContentProvider {
	// 分别定义两个返回值
	private static final int INSERT = 1;
	private static final int DELETE = 2;
	// 先new一个UriMatcher出来，参数就是当没有匹配到的时候，返回的值是什么
	private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	private static Uri changeUri = Uri.parse("content://com.pyn.mobilemanager.applockprovider");
	private AppLockDao dao;
	static {
		matcher.addURI("com.pyn.mobilemanager.applockprovider", "insert", INSERT);
		matcher.addURI("com.pyn.mobilemanager.applockprovider", "delete", DELETE);
	}

	@Override
	public boolean onCreate() {
		dao = new AppLockDao(getContext());		// 初始化
		return false;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int result = matcher.match(uri);
		if (result == DELETE) {
			String packName = selectionArgs[0];
			dao.delete(packName);
			getContext().getContentResolver().notifyChange(changeUri, null);
		} else {
			throw new IllegalArgumentException("uri地址不正确");
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int result = matcher.match(uri);
		if (result == INSERT) {
			String packName = (String) values.get("packName");
			dao.add(packName);
			// 如果数据发生了改变就通知
			getContext().getContentResolver().notifyChange(changeUri, null);
		} else {
			throw new IllegalArgumentException("uri地址不正确");
		}
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
						String sortOrder) {
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

}
