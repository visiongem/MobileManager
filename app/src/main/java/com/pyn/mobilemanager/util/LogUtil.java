package com.pyn.mobilemanager.util;

import android.util.Log;

/**
 * 日志工具类
 */
public class LogUtil {

	public static final int VERBOSE = 1;

	public static final int DEBUG = 2;

	public static final int INFO = 3;

	public static final int WARN = 4;

	public static final int ERROR = 5;

	public static final int NOTHING = 6;

	public static final int LEVER = NOTHING;

	public static void v(String tag, String msg) {
		if (LEVER <= VERBOSE) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (LEVER <= DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (LEVER <= INFO) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (LEVER <= WARN) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (LEVER <= ERROR) {
			Log.e(tag, msg);
		}
	}

}
