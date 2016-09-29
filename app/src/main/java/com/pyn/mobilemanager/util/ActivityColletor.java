package com.pyn.mobilemanager.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动管理器
 */
public class ActivityColletor {

	public static List<Activity> activities = new ArrayList<Activity>();

	/**
	 * 添加一个活动
	 * @param activity
	 */
	public static void addActivity(Activity activity){
		activities.add(activity);
	}

	/**
	 * 移除一个活动
	 * @param activity
	 */
	public static void removeActivity(Activity activity){
		activities.remove(activity);
	}

	public static void finishAll(){
		for (Activity activity : activities){
			if(!activity.isFinishing()){
				activity.finish();
			}
		}
	}
}
