package com.pyn.mobilemanager.engine;

import android.util.Xml;

import com.pyn.mobilemanager.domain.UpdateInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * 解析器xml文件
 */
public class UpdateInfoParser {

	public static UpdateInfo getUpdateInfo(InputStream is) {

		XmlPullParser parser = Xml.newPullParser(); // 创建一个xml解析器
		UpdateInfo updateInfo = new UpdateInfo();
		try {
			parser.setInput(is, "utf-8"); // 初始化parser解析器，第一个参数是一个InputStrem对象，即解析数据源，第二参数代表编码
			int type = parser.getEventType(); // 获取事件的类型信息，定位到xml文件开头

			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
					case XmlPullParser.START_TAG: // 开始元素
						if ("version".equals(parser.getName())) {
							// 因为内容也相当于一个节点，所以获取内容时需要调用parser对象的nextText()方法才可以得到内容
							String version = parser.nextText(); // 获取到里面的文本
							updateInfo.setVersion(version); // 设置版本号
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
		return updateInfo; // 返回UpdateInfo对象
	}
}
