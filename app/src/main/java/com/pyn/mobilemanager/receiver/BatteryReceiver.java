package com.pyn.mobilemanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.pyn.mobilemanager.service.AppLockService;
import com.pyn.mobilemanager.util.ServiceUtil;

/**
 * 这是一个关于手机电池的广播，当电量发生变化时，我们会重新启动程序锁服务，让我们可能此时被完全杀死的服务开起来
 */
public class BatteryReceiver extends BroadcastReceiver {

	private static final String TAG = "BatteryReceiver";
	private SharedPreferences sp;
	private ServiceUtil serviceUtil;

	@Override
	public void onReceive(Context context, Intent intent) {

		serviceUtil = new ServiceUtil(context);

		if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
			// 如果电量发生了变化
			// 当MyService没有开启时，启动MyService
			// 判断手机是否处于保护状态
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			if (!sp.getBoolean("isFirstEnterPrivacy", true)) {
				if (!serviceUtil
						.isWorked("com.pyn.mobilemanager.service.AppLockService")) {
					Intent appLockServiceIntent = new Intent(context,
							AppLockService.class);
					appLockServiceIntent
							.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startService(appLockServiceIntent);
				}
			}
		}
	}
}
