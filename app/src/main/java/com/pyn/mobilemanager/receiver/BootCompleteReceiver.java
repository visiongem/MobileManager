package com.pyn.mobilemanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.pyn.mobilemanager.service.AppLockService;
import com.pyn.mobilemanager.util.LogUtil;
import com.pyn.mobilemanager.util.ServiceUtil;

/**
 * 开机广播监听类（用于开机自启服务）
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompleteReceiver";
	private SharedPreferences sp;
	private ServiceUtil serviceUtil;

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction(); // 获取广播类型

		serviceUtil = new ServiceUtil(context);

		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) { // 判断是否是开机广播

			LogUtil.i(TAG, "开机啦~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

			// 判断手机是否处于保护状态
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

			// 如果启动过隐私保护，则进入就开启程序锁的服务
			if (!sp.getBoolean("isFirstEnterPrivacy", true)) {
				Intent appLockServiceIntent = new Intent(context,
						AppLockService.class);
				appLockServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startService(appLockServiceIntent);
			}
		}
	}
}
