package com.pyn.mobilemanager.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;

/**
 * ����apk�ļ��ĵ���
 * @param path �������ļ�·��
 * @param filePath �����ļ�·��
 * @return �����ļ�����
 * @throws Exception
 */
public class DownLoadFileTask {

	public static File getFile(String path, String filePath, ProgressDialog pd) throws Exception{
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();		// �õ�HttpURLConnection
		conn.setRequestMethod("GET");				// ����ͨ��get������������
		conn.setConnectTimeout(5000);				// �������ӳ�ʱʱ��Ϊ5��
		if(conn.getResponseCode() == 200){			//��������ش����ǣ�����������Ϊ���ж��Ƿ�������Ӧ��������
			int total = conn.getContentLength();	// �õ����ݳ���
			pd.setMax(total);						// ���ý������Ľ�����󳤶�
			InputStream is = conn.getInputStream();
			File file = new File(filePath);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			int process = 0;
			while((len = is.read(buffer)) != -1){
				fos.write(buffer, 0, len);
				process += len;
				pd.setProgress(process);		// ���ý���������
				Thread.sleep(50);				// ʹ�߳�˯��50����
			}
			fos.flush();
			fos.close();
			is.close();
			
			return file;
		}
		return null;
	}

}
