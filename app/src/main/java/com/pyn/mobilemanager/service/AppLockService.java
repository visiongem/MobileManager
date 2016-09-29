package com.pyn.mobilemanager.service;

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

import java.util.ArrayList;
import java.util.List;

/**
 * 程序锁服务
 */
public class AppLockService extends Service {

	public static final String TAG = "AppLockService";
	private AppLockDao dao;
	private List<String> lockApps;
	private ActivityManager mActivityManager;
	private Intent lockAppIntent;
	private boolean flag = true;
	private MyBinder myBinder;
	private KeyguardManager keyguardManager; // 键盘的管理器
	private List<String> tempStopApps; // 存放临时要停止保护的应用

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
	 * 重新开启对应用的保护
	 */
	public void appProtectStart(String packName) {
		if (tempStopApps.contains(packName)) {
			tempStopApps.remove(packName);
		}
	}

	/**
	 * 临时停止对某个应用的保护
	 */
	public void appProtectStop(String packName) {
		tempStopApps.add(packName);
	}

	/**
	 * 服务第一次创建的时候调用的方法
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		LogUtil.i(TAG, "当前运行  service");

		keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		// 注册一个内容观察者
		getContentResolver().registerContentObserver(
				Uri.parse("content://com.pyn.mobilemanager.applockprovider"), true,
				new MyObserver(new Handler()));

		myBinder = new MyBinder();
		dao = new AppLockDao(this);
		tempStopApps = new ArrayList<String>();

		// 得到所有的要锁定的应用程序
		lockApps = dao.getAllLockApps();
		lockAppIntent = new Intent(this, AppLockScreenActivity.class);
		// 服务里面是没有任务栈的，所以要指定一个新的任务栈，不然是无法在服务里面启动activity的
		lockAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		new Thread() {

			@Override
			public void run() {
				// 开启服务，监控各个应用的启动
				while (flag) {
					try {
						// 判断屏幕是否是锁屏状态
						if (keyguardManager.inKeyguardRestrictedInputMode()) {
							// 清空临时的集合
							tempStopApps.clear();
						}

						// 得到当前运行的任务栈，参数就是得到多少个任务栈，1就是只拿一个任务栈
						// 1对应的也就是正在运行的任务栈啦
						List<RunningTaskInfo> taskInfos = mActivityManager.getRunningTasks(1);
						// 拿到当前运行的任务栈
						RunningTaskInfo currentTask = taskInfos.get(0);
						// 拿到要运行的Activity的包名
						String packName = currentTask.topActivity
								.getPackageName();

						LogUtil.i(TAG, "当前运行" + packName);

						if (lockApps.contains(packName)) {
							// 如果当前的应用程序 需要临时的被终止保护
							if (tempStopApps.contains(packName)) {
								sleep(1000);
								continue;
							}
							// 弹出来一个锁定的界面 让用户输入密码,锁定程序，并且终止此程序
							ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
							am.killBackgroundProcesses(packName);
							lockAppIntent.putExtra("packName", packName);
							startActivity(lockAppIntent);
						} else {
							// 　放行执行
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
			// 重新更新lockapps集合里面的内容
			lockApps = dao.getAllLockApps();
			LogUtil.i(TAG, "数据库的内容发生了改变...");
		}
	}

}
