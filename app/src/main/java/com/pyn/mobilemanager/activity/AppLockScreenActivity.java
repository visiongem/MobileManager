package com.pyn.mobilemanager.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.service.AppLockService;
import com.pyn.mobilemanager.service.IService;
import com.pyn.mobilemanager.util.MD5Encoder;

/**
 * 为某些应用设置程序锁后，进入应用前跳出的程序锁界面的activity
 */
public class AppLockScreenActivity extends BasicActivity implements
		OnClickListener {

	private ImageView ivIcon;
	private TextView tvName;
	private EditText etPassword;
	private Button btnSure;
	private SharedPreferences sp;
	private String realPassword; // 实际正确的密码
	private String packName; // 包名
	private IService iService;
	private MyConn myConn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_lock_password);

		initViews();

		myConn = new MyConn();
		// 绑定服务，主要是为了能够调用服务里面的方法
		Intent intent = new Intent(this, AppLockService.class);
		bindService(intent, myConn, BIND_AUTO_CREATE); // 绑定服务

		sp = getSharedPreferences("config", MODE_PRIVATE);
		realPassword = sp.getString("privacy_password", ""); // 从sp中得到之前设置的密码
		packName = getIntent().getStringExtra("packName"); // 得到包名

		// 完成界面的初始化
		ApplicationInfo appInfo;

		try {
			// 通过包名拿到ApplicationInfo
			appInfo = getPackageManager().getPackageInfo(packName, 0).applicationInfo;
			Drawable appIcon = appInfo.loadIcon(getPackageManager()); // 应用图标
			String appName = appInfo.loadLabel(getPackageManager()).toString(); // 应用的名字
			ivIcon.setImageDrawable(appIcon);
			tvName.setText(appName);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true; // 阻止按键事件继续向下分发
		}
		return super.onKeyDown(keyCode, event);
	}

	class MyConn implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// 在Service里面已经实现了IService接口了
			iService = (IService) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		if (myConn != null) {
			unbindService(myConn); // 解除绑定
			myConn = null;
		}
	}

	@Override
	protected void initViews() {
		ivIcon = (ImageView) findViewById(R.id.app_lock_pwd_iv_icon); // 应用图标
		tvName = (TextView) findViewById(R.id.app_lock_pwd_tv_name); // 应用名称
		etPassword = (EditText) findViewById(R.id.app_lock_pwd_et); // 得到输入密码的EditText
		btnSure = (Button) findViewById(R.id.app_lock_btn_sure); // 得到确定button
		btnSure.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.app_lock_btn_sure:

			// 得到用户输入的密码
			String password = etPassword.getText().toString().trim();

			if (TextUtils.isEmpty(password)) { // 如果什么都没有输入就直接点击确定
				Toast.makeText(AppLockScreenActivity.this, "密码不能为空", 1).show();
			} else {
				if (MD5Encoder.encode(password).equals(realPassword)) {
					// 通知程序锁服务，临时取消对这个程序的保护
					iService.callAppProtecteStop(packName);
					finish();
					try {
						PackageInfo info = getPackageManager().getPackageInfo(
								packName,
								PackageManager.GET_UNINSTALLED_PACKAGES
										| PackageManager.GET_ACTIVITIES);
						ActivityInfo[] activityInfos = info.activities; // 存放这个包中所有activity的信息，这样就可以得到一个ActivityInfo的集合
						if (activityInfos.length > 0) {
							ActivityInfo startActivity = activityInfos[0]; // 得到具有启动属性的activity
							Intent intent = new Intent();
							intent.setClassName(packName, startActivity.name); // startActivity.name得到当前activity的名字
							startActivity(intent); // 启动这个程序
						}

					} catch (Exception e) {
						// Toast.makeText(AppLockScreenActivity.this,
						// "应用程序无法启动", 0).show();
						e.printStackTrace();
					}
				} else {
					Toast.makeText(AppLockScreenActivity.this, "密码错误", 1)
							.show();
				}
			}

			break;
		}
	}
}
