package com.pyn.mobilemanager.engine;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.pyn.mobilemanager.domain.UpdateInfo;

/**
 * ������xml�ļ�
 */
public class UpdateInfoParser {

	public static UpdateInfo getUpdateInfo(InputStream is) {

		XmlPullParser parser = Xml.newPullParser(); // ����һ��xml������
		UpdateInfo updateInfo = new UpdateInfo();
		try {
			parser.setInput(is, "utf-8"); // ��ʼ��parser����������һ��������һ��InputStrem���󣬼���������Դ���ڶ������������
			int type = parser.getEventType(); // ��ȡ�¼���������Ϣ����λ��xml�ļ���ͷ

			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG: // ��ʼԪ��
					if ("version".equals(parser.getName())) {
						// ��Ϊ����Ҳ�൱��һ���ڵ㣬���Ի�ȡ����ʱ��Ҫ����parser�����nextText()�����ſ��Եõ�����
						String version = parser.nextText(); // ��ȡ��������ı�
						updateInfo.setVersion(version); // ���ð汾��
					} else if ("description".equals(parser.getName())) {
						String description = parser.nextText();
						updateInfo.setDescription(description);
					} else if ("apkurl".equals(parser.getName())) {
						String apkurl = parser.nextText();
						updateInfo.setApkUrl(apkurl);
					}
					break;
				}
				type = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return updateInfo; // ����UpdateInfo����
	}
}
