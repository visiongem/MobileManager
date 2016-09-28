package com.pyn.mobilemanager.domain;

import android.graphics.drawable.Drawable;

/**
 * app��Ϣʵ����
 */
public class AppInfo {

	private Drawable icon; 		// Ӧ�ó����ͼ��
	private String appName; 	// Ӧ�ó��������
	private String packName;	// Ӧ�ó���İ���
	private String version; 	// Ӧ�ó���汾��
	private boolean isSystemApp;// Ӧ�ó����Ƿ�ϵͳ�Դ��ĳ���

	private int uid;
	private long appGprs; // app�õ�2G/3G������
	private long rxFlow;  // app�õ�2G/3G��������
	private long txFlow;  // app�õ�2G/3G�ϴ�����

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isSystemApp() {
		return isSystemApp;
	}

	public void setSystemApp(boolean isSystemApp) {
		this.isSystemApp = isSystemApp;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public long getAppGprs() {
		return appGprs;
	}

	public void setAppGprs(long appGprs) {
		this.appGprs = appGprs;
	}

	public long getRxFlow() {
		return rxFlow;
	}

	public void setRxFlow(long rxFlow) {
		this.rxFlow = rxFlow;
	}

	public long getTxFlow() {
		return txFlow;
	}

	public void setTxFlow(long txFlow) {
		this.txFlow = txFlow;
	}

}
