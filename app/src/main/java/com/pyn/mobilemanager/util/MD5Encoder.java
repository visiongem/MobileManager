package com.pyn.mobilemanager.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ��ϢժҪ�㷨,�������ܵ���,��Ҫ�Ǽ����û����õ�����
 * MD5���������ô�������Ϣ��������ǩ�����ǩ��˽����Կǰ��"ѹ��"��һ�ֱ��ܵĸ�ʽ�����ǰ�һ�����ⳤ�ȵ��ֽڴ��任��һ������ʮ���������ִ���
 */
public class MD5Encoder {
	public static String encode(String pwd) {
		try {
			// �õ�һ��MD5ת�����������ҪSHA1�������ɡ�SHA1����
			MessageDigest digest = MessageDigest.getInstance("MD5");
			// pwd.getBytes()�ǽ��ַ���ת�����ֽ����飬���ת�������ؽ����Ҳ���ֽ����飬����16��Ԫ��
			byte[] bytes = digest.digest(pwd.getBytes());
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < bytes.length; i++) {
				// ת����16���Ƶ��ַ���
				String s = Integer.toHexString(0xff & bytes[i]);
				if (s.length() == 1) {
					sb.append("0" + s);
				} else {
					sb.append(s);
				}
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("���ᷢ��");
		}

	}
}
