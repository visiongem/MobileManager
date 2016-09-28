package com.pyn.mobilemanager.util;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * �����ж�һ��service�Ƿ��������еĹ�����
 */
public class ServiceUtil {

	private Context mContext;

	public ServiceUtil(Context context) {
		this.mContext = context;
	}

	/**
	 * �������ж��Լ�Щ��һ��Service�Ƿ��Ѿ�����
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
