package com.pyn.mobilemanager.activity;

import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.domain.UpdateInfo;
import com.pyn.mobilemanager.engine.DownLoadFileTask;
import com.pyn.mobilemanager.engine.UpdateInfoService;

/**
 * ��ӭ����
 */
public class SplashActivity extends BasicActivity {

	private TextView mTvVersion;
	private ProgressDialog mPd;
	private String mVersion; // �汾��
	private UpdateInfo mUpdateInfo; // ���°汾��Ϣ��ʵ����
	private SharedPreferences sp;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// �жϷ������汾����ͻ��˰汾���Ƿ���ͬ
			if (isNeedUpdate(mVersion)) {
				showUpdateDialog();
			} else {
				Toast.makeText(SplashActivity.this, "δ��⵽�°汾������������", 0).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// ���4.0�з������粻�������߳��н��д���
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork() // ��������滻ΪdetectAll()
																		// �Ͱ����˴��̶�д������I/O
				.penaltyLog() // ��ӡlogcat��ͨ���ļ�������Ӧ��log
				.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects() // ̽��SQLite���ݿ����
				.detectLeakedClosableObjects().penaltyLog() // ��ӡlogcat
				.penaltyDeath().build());

		super.onCreate(savedInstanceState);
		// ȡ��������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		initViews();
		init();
		// ��ɴ����ȫ����ʾ
		// ȡ����״̬��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}

	/**
	 * ��ʼ��
	 */
	private void init() {
		mPd = new ProgressDialog(this);
		mPd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // ���ý�����������
		mPd.setMessage("��������..."); // ���ý�������ʾ����
		mVersion = getVersion();

		// �õ�ǰ��activity��ʱ1.5���� ������
		new Thread() {
			public void run() {
				super.run();
				try {
					sleep(1500);
					mHandler.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

		mTvVersion.setText(mVersion);
	}

	/**
	 * ��ʼ���ؼ�
	 */
	@Override
	protected void initViews() {
		mTvVersion = (TextView) findViewById(R.id.splash_tv_version);
	}

	/**
	 * ��ʾ���¶Ի���ķ���
	 */
	private void showUpdateDialog() {
		Builder builder = new Builder(this); // ����һ���Ի������
		builder.setTitle("��������"); // ���öԻ������
		builder.setMessage(mUpdateInfo.getDescription()); // ���öԻ�������
		builder.setCancelable(false); // ���û�����ȡ�����Ի���
		builder.setPositiveButton("��������", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) { // ȷ����ť�ļ�����
				if (Environment.getExternalStorageState().equals(
			 			Environment.MEDIA_MOUNTED)) { // ����ڴ濨�Ƿ���ã������õĻ�
					DownLoadFileThreadTask task = new DownLoadFileThreadTask(
							mUpdateInfo.getApkUrl(),
							"/sdcard/MobileManager.apk"); // �������ļ�
					mPd.show(); // ʹ��������ʾ����
					new Thread(task).start(); // �����߳�
				} else {
					Toast.makeText(getApplicationContext(), "sd��������", 1).show(); // �ڴ濨�����õĻ�����ʾһ�������õ���Ϣ
					loadMain(); // ֱ�� ����������
				}
			}
		});

		builder.setNegativeButton("ȡ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) { // ȡ����ť�ļ�����
				Toast.makeText(getApplicationContext(), "ȡ������, ����Ӧ��������", 1)
						.show();
				loadMain(); // �û����ȡ���Ļ�ֱ�ӽ���������
			}
		});
		builder.create().show(); // �����Ի�����ʾ����
	}

	/**
	 * @param versionText��ǰ�û��İ汾��Ϣ
	 * @return �Ƿ���Ҫ����
	 */
	private boolean isNeedUpdate(String versionText) {
		try {
			UpdateInfoService service = new UpdateInfoService(
					getApplicationContext()); // ����������Ϣ�������
			mUpdateInfo = service.getUpdateInfo(R.string.updateurl); // �õ��������еĸ�����Ϣ
			String version = mUpdateInfo.getVersion(); // �õ�������������Ϣ�еİ汾��
			if (version.equals(versionText)) { // ���������еİ汾�źܿͻ��˵İ汾����ͬ������Ҫ����
				loadMain(); // ����������
				return false;
			} else { // ���������еİ汾�źܿͻ��˵İ汾�Ų���ͬ�����и���
				return true;
			}
		} catch (Exception e) { // ���쳣����
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "����°汾�����쳣,����������", 0)
					.show();
			loadMain(); // ����������
			return false;
		}
	}

	/**
	 * ��ȡ��ǰӦ�ó���İ汾��
	 * 
	 * @return
	 */
	private String getVersion() {
		try {
			// ��������񣬻�ȡpackagemanager��ʵ��
			PackageManager manager = getPackageManager();
			// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "�汾��δ֪";
		}
	}

	/**
	 * ����������
	 */
	private void loadMain() {
		Intent intent = new Intent(this, MainActivity.class); // �����������Intent
		startActivity(intent); // ����intent
		finish(); // �ѵ�ǰactivity������ջ�����Ƴ�
	}

	/**
	 * ��װapk
	 */
	private void install(File file) {
		Intent intent = new Intent(); // ����һ����ͼ
		intent.setAction(Intent.ACTION_VIEW); // Ϊ��ͼ����action����
		// ���ô򿪵��ļ�������intent��data��Type���ԣ�����apk��MIME������"application/vnd.android.package-archive"
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		finish(); // �ѵ�ǰactivity������ջ�����Ƴ�
		startActivity(intent); // ������ͼ
	}

	private class DownLoadFileThreadTask implements Runnable {

		private String path; // ������·��
		private String filePath; // �����ļ�·��

		public DownLoadFileThreadTask(String path, String filePath) { // ���췽��
			this.path = path;
			this.filePath = filePath;
		}

		@Override
		public void run() {
			try {
				File file = DownLoadFileTask.getFile(path, filePath, mPd);
				mPd.dismiss(); // ʹ��������ʧ
				install(file); // ����װ
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "�����ļ�ʧ��", 0).show();
				mPd.dismiss(); // ʹ��������ʧ
				loadMain(); // ����������
			}
		}
	}
}
