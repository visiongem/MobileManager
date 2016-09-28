package com.pyn.mobilemanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.pyn.mobilemanager.service.AppLockService;
import com.pyn.mobilemanager.util.LogUtil;
import com.pyn.mobilemanager.util.ServiceUtil;

/**
 * �����㲥�����ࣨ���ڿ�����������
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompleteReceiver";
	private SharedPreferences sp;
	private ServiceUtil serviceUtil;

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction(); // ��ȡ�㲥����

		serviceUtil = new ServiceUtil(context);

		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) { // �ж��Ƿ��ǿ����㲥

			LogUtil.i(TAG, "������~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

			// �ж��ֻ��Ƿ��ڱ���״̬
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

			// �����������˽�����������Ϳ����������ķ���
			if (!sp.getBoolean("isFirstEnterPrivacy", true)) {
				Intent appLockServiceIntent = new Intent(context,
						AppLockService.class);
				appLockServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startService(appLockServiceIntent);
			}
		}
	}
}
