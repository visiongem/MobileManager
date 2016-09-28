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
 * 提供手机安装的应用信息
 */
public class AppInfoProvider {

	private Context mContext;
	private PackageManager packageManager; // PackageManager主要是管理应用程序包，通过它就可以获取应用程序信息

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
	 * 返回当前手机里面安装的所有应用程序信息的集合
	 * 
	 * @return 应用程序的集合
	 */
	public List<AppInfo> getAllApps() {

		appInfos = new ArrayList<AppInfo>(); // appInfos用来存放手机中所有的应用程序信息
		// 获取到所有安装了的应用程序的信息，包括那些卸载了的，但没有清除数据的应用程序
		packageInfos = packageManager
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);

		for (PackageInfo packageInfo : packageInfos) {
			AppInfo myApp = new AppInfo();
			// 得到应用程序的包名
			String packName = packageInfo.packageName;
			myApp.setPackName(packName);
			// 得到应用程序的版本号
			String version = packageInfo.versionName;
			myApp.setVersion(version);
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			// 得到应用程序的图标
			Drawable icon = applicationInfo.loadIcon(packageManager);
			myApp.setIcon(icon);
			// 得到应用程序的名称
			String appName = applicationInfo.loadLabel(packageManager)
					.toString();
			myApp.setAppName(appName);

			// 判断程序是否第三方程序
			if (filterApp(applicationInfo)) {
				myApp.setSystemApp(false);
			} else {
				myApp.setSystemApp(true);
			}

			appInfos.add(myApp);
		}

		return appInfos; // 返回手机中所有应用程序
	}

	/**
	 * 此方法用来得到手机中所有的用户应用程序
	 * 
	 * @return 用户应用程序集合
	 */
	public List<AppInfo> getAllUserApps() {

		userAppInfos = new ArrayList<AppInfo>(); // 用于存放手机中第三方应用程序集合

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
	 * 此方法用来得到手机中的系统应用程序
	 * 
	 * @return 系统应用程序集合
	 */
	public List<AppInfo> getAllSystemApps() {

		systemAppInfos = new ArrayList<AppInfo>(); // 用于存放手机中的系统应用程序

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

		// 获取到配置权限信息的应用程序
		List<PackageInfo> packInfos = packageManager
				.getInstalledPackages(PackageManager.GET_PERMISSIONS);

		flowInfos = new ArrayList<AppInfo>();

		for (PackageInfo packInfo : packInfos) {

			// 获取该应用的所有权限信息
			String[] permissions = packInfo.requestedPermissions;

			if (permissions != null && permissions.length > 0) {

				for (String permission : permissions) {
					// 筛选出具有Internet权限的应用程序
					if ("android.permission.INTERNET".equals(permission)) {

						int uid = 0;

						AppInfo appInfo = new AppInfo();
						// 封装应用信息
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
	 * 判断某个应用程序是 不是三方的应用程序
	 * 
	 * @param info
	 * @return true 三方应用 false 系统应用
	 * */
	public boolean filterApp(ApplicationInfo info) {
		// flags字段： FLAG_SYSTEM　系统应用程序、FLAG_EXTERNAL_STORAGE　表示该应用安装在sdcard中
		// 本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {// 代表的用户的应用
			return true;
		}
		return false;
	}

}
