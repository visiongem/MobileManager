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
 * ΪĳЩӦ�����ó������󣬽���Ӧ��ǰ�����ĳ����������activity
 */
public class AppLockScreenActivity extends BasicActivity implements
		OnClickListener {

	private ImageView ivIcon;
	private TextView tvName;
	private EditText etPassword;
	private Button btnSure;
	private SharedPreferences sp;
	private String realPassword; // ʵ����ȷ������
	private String packName; // ����
	private IService iService;
	private MyConn myConn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_lock_password);

		initViews();

		myConn = new MyConn();
		// �󶨷�����Ҫ��Ϊ���ܹ����÷�������ķ���
		Intent intent = new Intent(this, AppLockService.class);
		bindService(intent, myConn, BIND_AUTO_CREATE); // �󶨷���

		sp = getSharedPreferences("config", MODE_PRIVATE);
		realPassword = sp.getString("privacy_password", ""); // ��sp�еõ�֮ǰ���õ�����
		packName = getIntent().getStringExtra("packName"); // �õ�����

		// ��ɽ���ĳ�ʼ��
		ApplicationInfo appInfo;

		try {
			// ͨ�������õ�ApplicationInfo
			appInfo = getPackageManager().getPackageInfo(packName, 0).applicationInfo;
			Drawable appIcon = appInfo.loadIcon(getPackageManager()); // Ӧ��ͼ��
			String appName = appInfo.loadLabel(getPackageManager()).toString(); // Ӧ�õ�����
			ivIcon.setImageDrawable(appIcon);
			tvName.setText(appName);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return true; // ��ֹ�����¼��������·ַ�
		}
		return super.onKeyDown(keyCode, event);
	}

	class MyConn implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// ��Service�����Ѿ�ʵ����IService�ӿ���
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
			unbindService(myConn); // �����
			myConn = null;
		}
	}

	@Override
	protected void initViews() {
		ivIcon = (ImageView) findViewById(R.id.app_lock_pwd_iv_icon); // Ӧ��ͼ��
		tvName = (TextView) findViewById(R.id.app_lock_pwd_tv_name); // Ӧ������
		etPassword = (EditText) findViewById(R.id.app_lock_pwd_et); // �õ����������EditText
		btnSure = (Button) findViewById(R.id.app_lock_btn_sure); // �õ�ȷ��button
		btnSure.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.app_lock_btn_sure:

			// �õ��û����������
			String password = etPassword.getText().toString().trim();

			if (TextUtils.isEmpty(password)) { // ���ʲô��û�������ֱ�ӵ��ȷ��
				Toast.makeText(AppLockScreenActivity.this, "���벻��Ϊ��", 1).show();
			} else {
				if (MD5Encoder.encode(password).equals(realPassword)) {
					// ֪ͨ������������ʱȡ�����������ı���
					iService.callAppProtecteStop(packName);
					finish();
					try {
						PackageInfo info = getPackageManager().getPackageInfo(
								packName,
								PackageManager.GET_UNINSTALLED_PACKAGES
										| PackageManager.GET_ACTIVITIES);
						ActivityInfo[] activityInfos = info.activities; // ��������������activity����Ϣ�������Ϳ��Եõ�һ��ActivityInfo�ļ���
						if (activityInfos.length > 0) {
							ActivityInfo startActivity = activityInfos[0]; // �õ������������Ե�activity
							Intent intent = new Intent();
							intent.setClassName(packName, startActivity.name); // startActivity.name�õ���ǰactivity������
							startActivity(intent); // �����������
						}

					} catch (Exception e) {
						// Toast.makeText(AppLockScreenActivity.this,
						// "Ӧ�ó����޷�����", 0).show();
						e.printStackTrace();
					}
				} else {
					Toast.makeText(AppLockScreenActivity.this, "�������", 1)
							.show();
				}
			}

			break;
		}
	}
}
