package com.pyn.mobilemanager.domain;

/**
 * ������Ϣ��ʵ����
 */
public class UpdateInfo {

	private String version;		// �汾��
	private String description;	// �汾����
	private String apkUrl;		// apk��url
	
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
