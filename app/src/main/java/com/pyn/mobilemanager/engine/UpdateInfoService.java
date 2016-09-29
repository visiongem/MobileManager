package com.pyn.mobilemanager.engine;

import android.content.Context;

import com.pyn.mobilemanager.domain.UpdateInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 从服务器获取更新信息的类
 */
public class UpdateInfoService {

	private Context mContext;

	public UpdateInfoService(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * @param urlId 服务器路径string对应的id
	 * @return 更新的信息
	 * 一般业务方法中的异常都显示的抛出，让它的调用者去处理这些异常
	 */
	public UpdateInfo getUpdateInfo(int urlId){
		String path = mContext.getResources().getString(urlId);
		InputStream is = null;
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();	// 得到HttpURLConnection
			conn.setConnectTimeout(5000);	// 设置5s内若没有获得返回信息则超时
			conn.setRequestMethod("GET");	// 设置返回方法
			is = conn.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 解析xml文件
		return UpdateInfoParser.getUpdateInfo(is);
	}

}
