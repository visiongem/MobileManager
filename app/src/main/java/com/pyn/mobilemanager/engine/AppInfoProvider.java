package com.pyn.mobilemanager.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;

import com.pyn.mobilemanager.domain.AppInfo;

/**
 * �ṩ�ֻ���װ��Ӧ����Ϣ
 */
public class AppInfoProvider {

	private Context mContext;
	private PackageManager packageManager; // PackageManager��Ҫ�ǹ���Ӧ�ó������ͨ�����Ϳ��Ի�ȡӦ�ó�����Ϣ

	private List<AppInfo> appInfos;
	private List<PackageInfo> packageInfos;
	private List<AppInfo> flowInfos;

	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;

	public AppInfoProvider(Context context) {
		mContext = context;
		packageManager = mContext.getPackageManager();
	}

	/**
	 * ���ص�ǰ�ֻ����氲װ������Ӧ�ó�����Ϣ�ļ���
	 * 
	 * @return Ӧ�ó���ļ���
	 */
	public List<AppInfo> getAllApps() {

		appInfos = new ArrayList<AppInfo>(); // appInfos��������ֻ������е�Ӧ�ó�����Ϣ
		// ��ȡ�����а�װ�˵�Ӧ�ó������Ϣ��������Щж���˵ģ���û��������ݵ�Ӧ�ó���
		packageInfos = packageManager
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);

		for (PackageInfo packageInfo : packageInfos) {
			AppInfo myApp = new AppInfo();
			// �õ�Ӧ�ó���İ���
			String packName = packageInfo.packageName;
			myApp.setPackName(packName);
			// �õ�Ӧ�ó���İ汾��
			String version = packageInfo.versionName;
			myApp.setVersion(version);
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			// �õ�Ӧ�ó����ͼ��
			Drawable icon = applicationInfo.loadIcon(packageManager);
			myApp.setIcon(icon);
			// �õ�Ӧ�ó��������
			String appName = applicationInfo.loadLabel(packageManager)
					.toString();
			myApp.setAppName(appName);

			// �жϳ����Ƿ����������
			if (filterApp(applicationInfo)) {
				myApp.setSystemApp(false);
			} else {
				myApp.setSystemApp(true);
			}

			appInfos.add(myApp);
		}

		return appInfos; // �����ֻ�������Ӧ�ó���
	}

	/**
	 * �˷��������õ��ֻ������е��û�Ӧ�ó���
	 * 
	 * @return �û�Ӧ�ó��򼯺�
	 */
	public List<AppInfo> getAllUserApps() {

		userAppInfos = new ArrayList<AppInfo>(); // ���ڴ���ֻ��е�����Ӧ�ó��򼯺�

		List<AppInfo> Infos = getAllApps();

		for (int i = 0; i < Infos.size(); i++) {
			AppInfo appInfo = Infos.get(i);
			if (!appInfo.isSystemApp()) {
				userAppInfos.add(appInfo);
			}
		}
		return userAppInfos;
	}

	/**
	 * �˷��������õ��ֻ��е�ϵͳӦ�ó���
	 * 
	 * @return ϵͳӦ�ó��򼯺�
	 */
	public List<AppInfo> getAllSystemApps() {

		systemAppInfos = new ArrayList<AppInfo>(); // ���ڴ���ֻ��е�ϵͳӦ�ó���

		List<AppInfo> Infos = getAllApps();

		for (int i = 0; i < Infos.size(); i++) {
			AppInfo appInfo = Infos.get(i);
			if (appInfo.isSystemApp()) {
				systemAppInfos.add(appInfo);
			}
		}
		return systemAppInfos;
	}

	public List<AppInfo> getAppFlowInfo() {

		// ��ȡ������Ȩ����Ϣ��Ӧ�ó���
		List<PackageInfo> packInfos = packageManager
				.getInstalledPackages(PackageManager.GET_PERMISSIONS);

		flowInfos = new ArrayList<AppInfo>();

		for (PackageInfo packInfo : packInfos) {

			// ��ȡ��Ӧ�õ�����Ȩ����Ϣ
			String[] permissions = packInfo.requestedPermissions;

			if (permissions != null && permissions.length > 0) {

				for (String permission : permissions) {
					// ɸѡ������InternetȨ�޵�Ӧ�ó���
					if ("android.permission.INTERNET".equals(permission)) {

						int uid = 0;

						AppInfo appInfo = new AppInfo();
						// ��װӦ����Ϣ
						uid = packInfo.applicationInfo.uid;

						appInfo.setUid(uid);
						appInfo.setAppName(packInfo.applicationInfo.loadLabel(
								packageManager).toString());
						appInfo.setIcon(packInfo.applicationInfo
								.loadIcon(packageManager));
						appInfo.setRxFlow(TrafficStats.getUidRxBytes(uid));
						appInfo.setTxFlow(TrafficStats.getUidTxBytes(uid));
						flowInfos.add(appInfo);
						appInfo = null;
						break;
					}
				}
			}
		}
		return flowInfos;
	}

	/**
	 * �ж�ĳ��Ӧ�ó����� ����������Ӧ�ó���
	 * 
	 * @param info
	 * @return true ����Ӧ�� false ϵͳӦ��
	 * */
	public boolean filterApp(ApplicationInfo info) {
		// flags�ֶΣ� FLAG_SYSTEM��ϵͳӦ�ó���FLAG_EXTERNAL_STORAGE����ʾ��Ӧ�ð�װ��sdcard��
		// ������ϵͳ���򣬱��û��ֶ����º󣬸�ϵͳ����Ҳ��Ϊ������Ӧ�ó�����
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {// ������û���Ӧ��
			return true;
		}
		return false;
	}

}
