package com.pyn.mobilemanager.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

import java.util.ArrayList;

/**
 * 用来判断一个service是否正在运行的工具类
 */
public class ServiceUtil {

	private Context mContext;

	public ServiceUtil(Context context) {
		this.mContext = context;
	}

	/**
	 * 本方法判断自己些的一个Service是否已经运行
	 *
	 * @param service
	 * @return
	 */
	public boolean isWorked(String service) {

		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);

		ArrayList<RunningServiceInfo> runningServices = (ArrayList<RunningServiceInfo>) am
				.getRunningServices(100);

		for (RunningServiceInfo runningService : runningServices) {

			if (runningService.service.getClassName().toString()
					.equals(service)) {
				return true;
			}
		}
		return false;
	}
}
