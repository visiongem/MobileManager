package com.pyn.mobilemanager.util;

import java.text.DecimalFormat;

/**
 * �����࣬���ڷ������ݴ�С��Ӧ���ı�
 */
public class TextFormater {

	/**
	 * ����byte�����ݴ�С��Ӧ���ı�
	 */
	public static String getDataSize(long size) {

		DecimalFormat formater = new DecimalFormat("####.00");

		if (size < 0) {
			return "0";
		} else if (size < 1024) {
			return size + "bytes";
		} else if (size < 1024 * 1024) {
			float kbSize = size / 1024f;
			return formater.format(kbSize) + "KB";
		} else if (size < 1024 * 1024 * 1024) {
			float mSize = size / 1024f / 1024f;
			return formater.format(mSize) + "MB";
		} else if (size < 1024 * 1024 * 1024 * 1024) {
			float gbSize = size / 1024f / 1024f / 1024f;
			return formater.format(gbSize) + "GB";
		} else {
			return "size: error";
		}
	}

	/**
	 * ����kb�����ݴ�С��Ӧ���ı�
	 */
	public static String getKBDataSize(long size) {
		return getDataSize(size * 1024);
	}

}
