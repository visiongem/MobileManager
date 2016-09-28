package com.pyn.mobilemanager.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;

/**
 * 下载apk文件的的类
 * @param path 服务器文件路径
 * @param filePath 本地文件路径
 * @return 本地文件对象
 * @throws Exception
 */
public class DownLoadFileTask {

	public static File getFile(String path, String filePath, ProgressDialog pd) throws Exception{
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();		// 得到HttpURLConnection
		conn.setRequestMethod("GET");				// 设置通过get方法发送数据
		conn.setConnectTimeout(5000);				// 设置连接超时时间为5秒
		if(conn.getResponseCode() == 200){			//　如果返回代码是２００，这是为了判断是否正常响应请求数据
			int total = conn.getContentLength();	// 得到内容长度
			pd.setMax(total);						// 设置进度条的进度最大长度
			InputStream is = conn.getInputStream();
			File file = new File(filePath);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			int process = 0;
			while((len = is.read(buffer)) != -1){
				fos.write(buffer, 0, len);
				process += len;
				pd.setProgress(process);		// 重置进度条进度
				Thread.sleep(50);				// 使线程睡眠50毫秒
			}
			fos.flush();
			fos.close();
			is.close();
			
			return file;
		}
		return null;
	}

}
