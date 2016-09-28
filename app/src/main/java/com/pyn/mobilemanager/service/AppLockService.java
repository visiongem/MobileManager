package com.pyn.mobilemanager.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.pyn.mobilemanager.activity.AppLockScreenActivity;
import com.pyn.mobilemanager.db.dao.AppLockDao;
import com.pyn.mobilemanager.util.LogUtil;

/**
 * ����������
 */
public class AppLockService extends Service {

	public static final String TAG = "AppLockService";
	private AppLockDao dao;
	private List<String> lockApps;
	private ActivityManager mActivityManager;
	private Intent lockAppIntent;
	private boolean flag = true;
	private MyBinder myBinder;
	private KeyguardManager keyguardManager; // ���̵Ĺ�����
	private List<String> tempStopApps; // �����ʱҪֹͣ������Ӧ��

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	public class MyBinder extends Binder implements IService {

		@Override
		public void callAppProtectStart(String packName) {
			appProtectStart(packName);
		}

		@Override
		public void callAppProtecteStop(String packName) {
			appProtectStop(packName);
		}

	}

	/**
	 * ���¿�����Ӧ�õı���
	 */
	public void appProtectStart(String packName) {
		if (tempStopApps.contains(packName)) {
			tempStopApps.remove(packName);
		}
	}

	/**
	 * ��ʱֹͣ��ĳ��Ӧ�õı���
	 */
	public void appProtectStop(String packName) {
		tempStopApps.add(packName);
	}

	/**
	 * �����һ�δ�����ʱ����õķ���
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		LogUtil.i(TAG, "��ǰ����  service");

		keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		// ע��һ�����ݹ۲���
		getContentResolver().registerContentObserver(
				Uri.parse("content://com.pyn.mobilemanager.applockprovider"), true,
				new MyObserver(new Handler()));

		myBinder = new MyBinder();
		dao = new AppLockDao(this);
		tempStopApps = new ArrayList<String>();

		// �õ����е�Ҫ������Ӧ�ó���
		lockApps = dao.getAllLockApps();
		lockAppIntent = new Intent(this, AppLockScreenActivity.class);
		// ����������û������ջ�ģ�����Ҫָ��һ���µ�����ջ����Ȼ���޷��ڷ�����������activity��
		lockAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		new Thread() {

			@Override
			public void run() {
				// �������񣬼�ظ���Ӧ�õ�����
				while (flag) {
					try {
						// �ж���Ļ�Ƿ�������״̬
						if (keyguardManager.inKeyguardRestrictedInputMode()) {
							// �����ʱ�ļ���
							tempStopApps.clear();
						}

						// �õ���ǰ���е�����ջ���������ǵõ����ٸ�����ջ��1����ֻ��һ������ջ
						// 1��Ӧ��Ҳ�����������е�����ջ��
						List<RunningTaskInfo> taskInfos = mActivityManager.getRunningTasks(1);
						// �õ���ǰ���е�����ջ
						RunningTaskInfo currentTask = taskInfos.get(0);
						// �õ�Ҫ���е�Activity�İ���
						String packName = currentTask.topActivity
								.getPackageName();

						LogUtil.i(TAG, "��ǰ����" + packName);

						if (lockApps.contains(packName)) {
							// �����ǰ��Ӧ�ó��� ��Ҫ��ʱ�ı���ֹ����
							if (tempStopApps.contains(packName)) {
								sleep(1000);
								continue;
							}
							// ������һ�������Ľ��� ���û���������,�������򣬲�����ֹ�˳���
							ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
							am.killBackgroundProcesses(packName);
							lockAppIntent.putExtra("packName", packName);
							startActivity(lockAppIntent);
						} else {
							// ������ִ��
						}

						sleep(500);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent appLockServiceIntent = new Intent(this,
				AppLockService.class);
		appLockServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startService(appLockServiceIntent);
	}

	private class MyObserver extends ContentObserver {

		public MyObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {

			super.onChange(selfChange);
			// ���¸���lockapps�������������
			lockApps = dao.getAllLockApps();
			LogUtil.i(TAG, "���ݿ�����ݷ����˸ı�...");
		}
	}

}
