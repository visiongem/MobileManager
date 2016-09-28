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
	 * �õ��Ѿ��еĸ����ؼ�
	 */
	protected abstract void initViews();

	@Override
	protected void onResume() {
		// �ŵ�onResume�Ϳ���ʵ��android ���ؼ�����
		overridePendingTransition(R.anim.enter, R.anim.exit);
		super.onResume();
	}

}
