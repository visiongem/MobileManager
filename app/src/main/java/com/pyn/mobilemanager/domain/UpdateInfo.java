package com.pyn.mobilemanager.domain;

/**
 * 更新信息的实体类
 */
public class UpdateInfo {

	private String version;		// 版本号
	private String description;	// 版本描述
	private String apkUrl;		// apk的url

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getApkUrl() {
		return apkUrl;
	}

	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}

}
