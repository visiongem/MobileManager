package com.pyn.mobilemanager.domain;

/**
 * 隐私保护中短信实体类
 */
public class PrivacySmsDetailInfo {

	private String number; // 电话号码
	private String time; // 通讯时间
	private String content; // 通讯内容

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
