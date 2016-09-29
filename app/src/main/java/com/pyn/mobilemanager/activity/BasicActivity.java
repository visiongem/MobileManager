package com.pyn.mobilemanager.activity;

import android.app.Activity;
import android.os.Bundle;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.util.ActivityColletor;

public abstract class BasicActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityColletor.addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityColletor.removeActivity(this);
	}

	/**
	 * 得到佈局中的各個控件
	 */
	protected abstract void initViews();

	@Override
	protected void onResume() {
		// 放到onResume就可以实现android 返回键动画
		overridePendingTransition(R.anim.enter, R.anim.exit);
		super.onResume();
	}

}
