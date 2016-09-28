package com.pyn.mobilemanager.domain;

import android.graphics.drawable.Drawable;

/**
 * app信息实体类
 */
public class AppInfo {

	private Drawable icon; 		// 应用程序的图标
	private String appName; 	// 应用程序的名字
	private String packName;	// 应用程序的包名
	private String version; 	// 应用程序版本号
	private boolean isSystemApp;// 应用程序是否系统自带的程序

	private int uid;
	private long appGprs; // app用的2G/3G总流量
	private long rxFlow;  // app用的2G/3G接收流量
	private long txFlow;  // app用的2G/3G上传流量

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
