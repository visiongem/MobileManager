package com.pyn.mobilemanager.domain;

/**
 * ��˽�����ж���ʵ����
 */
public class PrivacySmsDetailInfo {

	private String number; // �绰����
	private String time; // ͨѶʱ��
	private String content; // ͨѶ����

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
