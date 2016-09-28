package com.pyn.mobilemanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.pyn.mobilemanager.service.AppLockService;
import com.pyn.mobilemanager.util.ServiceUtil;

/**
 * ����һ�������ֻ���صĹ㲥�������������仯ʱ�����ǻ������������������������ǿ��ܴ�ʱ����ȫɱ���ķ�������
 */
public class BatteryReceiver extends BroadcastReceiver {

	private static final String TAG = "BatteryReceiver";
	private SharedPreferences sp;
	private ServiceUtil serviceUtil;

	@Override
	public void onReceive(Context context, Intent intent) {

		serviceUtil = new ServiceUtil(context);
		
		if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
			// ������������˱仯
			// ��MyServiceû�п���ʱ������MyService
			// �ж��ֻ��Ƿ��ڱ���״̬
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
