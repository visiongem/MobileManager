package com.pyn.mobilemanager.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 消息摘要算法,用来加密的类,主要是加密用户设置的密码
 * MD5的作用是让大容量信息在用数字签名软件签署私人密钥前被"压缩"成一种保密的格式（就是把一个任意长度的字节串变换成一定长的十六进制数字串）
 */
public class MD5Encoder {
	public static String encode(String pwd) {
		try {
			// 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
			MessageDigest digest = MessageDigest.getInstance("MD5");
			// pwd.getBytes()是将字符串转换成字节数组，最后转换并返回结果，也是字节数组，包含16个元素
			byte[] bytes = digest.digest(pwd.getBytes());
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < bytes.length; i++) {
				// 转换成16进制的字符串
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
			throw new RuntimeException("不会发生");
		}

	}
}
