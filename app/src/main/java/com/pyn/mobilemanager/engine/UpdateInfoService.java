package com.pyn.mobilemanager.engine;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;

import com.pyn.mobilemanager.domain.UpdateInfo;

/**
 * �ӷ�������ȡ������Ϣ����
 */
public class UpdateInfoService {
	
	private Context mContext;

	public UpdateInfoService(Context mContext) {
		this.mContext = mContext;
	}
	
	/**
	 * @param urlid ������·��string��Ӧ��id
	 * @return ���µ���Ϣ
	 * һ��ҵ�񷽷��е��쳣����ʾ���׳��������ĵ�����ȥ������Щ�쳣
	 */
	public UpdateInfo getUpdateInfo(int urlId){
		String path = mContext.getResources().getString(urlId);
		InputStream is = null;
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();	// �õ�HttpURLConnection
			conn.setConnectTimeout(5000);	// ����5s����û�л�÷�����Ϣ��ʱ
			conn.setRequestMethod("GET");	// ���÷��ط���
			is = conn.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		// ����xml�ļ�
		return UpdateInfoParser.getUpdateInfo(is);
	}

}
