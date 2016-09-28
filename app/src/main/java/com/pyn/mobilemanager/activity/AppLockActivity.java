package com.pyn.mobilemanager.activity;

import java.util.List;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.db.dao.AppLockDao;
import com.pyn.mobilemanager.domain.AppInfo;
import com.pyn.mobilemanager.engine.AppInfoProvider;
import com.pyn.mobilemanager.view.LoadingDialog;

/**
 * ��������������activity
 */
public class AppLockActivity extends BasicActivity implements OnClickListener {

	private ListView lvAppLock; // ���ǳ�����Ӧ�õ��б�
	private ImageView ivPrevious;
	private List<AppInfo> userAppInfos; // ����Ӧ�ó������Ϣ,������ϵͳ����
	private AppInfoProvider provider; // �ṩ�ֻ�Ӧ�õ���
	private AppLockAdapter adapter;
	private AppLockDao dao;
	private LoadingDialog dialog;
	private List<String> lockAppInfos; // �����˳�������app�ļ���
	private Animation animation;
	private LayoutAnimationController lac;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dialog.dismiss();
			adapter = new AppLockAdapter();
			lvAppLock.setAdapter(adapter);
			
			lvAppLock.setLayoutAnimation(lac);
			lvAppLock.startLayoutAnimation();
			
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_lock);
		
		initAnimation();
		
		dialog = new LoadingDialog(this);
		initViews();
		dao = new AppLockDao(AppLockActivity.this);
		lockAppInfos = dao.getAllLockApps(); // �õ������Ѿ�������Ӧ�ó��򼯺�

		provider = new AppInfoProvider(this);

		initUI();

		// Ϊÿ��app��Ŀ��ӵ���¼�
		lvAppLock.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ��Ӷ���Ч�������������󣬾Ͱ�����ͼƬ�ı�
				TranslateAnimation translateAnimation = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 0.0f);
				translateAnimation.setDuration(500);
				view.startAnimation(translateAnimation);
				ImageView iv_status = (ImageView) view
						.findViewById(R.id.app_lock_item_iv_status);

				// ���ݵ�ǰҪ��������İ���
				AppInfo info = (AppInfo) lvAppLock.getItemAtPosition(position);
				String packName = info.getPackName(); // �õ���ǰҪ�����İ���
				if (dao.find(packName)) {
					// �Ƴ������Ŀ
					getContentResolver()
							.delete(Uri
									.parse("content://com.pyn.mobilemanager.applockprovider/delete"),
									null, new String[] { packName });
					lockAppInfos.remove(packName);
					iv_status.setImageResource(R.drawable.unlock);

				} else {
					lockAppInfos.add(packName);
					ContentValues values = new ContentValues();
					values.put("packName", packName);
					getContentResolver()
							.insert(Uri
									.parse("content://com.pyn.mobilemanager.applockprovider/insert"),
									values);
					iv_status.setImageResource(R.drawable.lock);
				}

			}

		});

	}
	
	/**
	 * ��ʼ������
	 */
	private void initAnimation() {
		animation  = AnimationUtils.loadAnimation(this, R.anim.listview_in);
		lac = new LayoutAnimationController(animation);
		lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
	}

	/**
	 * ��ʼ������
	 */
	private void initUI() {

		dialog.show();
		new Thread() {
			@Override
			public void run() {

				try {
					sleep(800);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				userAppInfos = provider.getAllUserApps();
				handler.sendEmptyMessage(0);
			}

		}.start();
	}

	/**
	 * ������
	 */
	private class AppLockAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return userAppInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return userAppInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			AppInfo info = userAppInfos.get(position);
			if (convertView == null) {
				View view = View.inflate(getApplicationContext(),
						R.layout.app_lock_item, null);
				AppManagerViews views = new AppManagerViews();
				// ����view�����״̬

				views.ivAppIcon = (ImageView) view
						.findViewById(R.id.app_lock_item_iv_icon);
				views.tvAppName = (TextView) view
						.findViewById(R.id.app_lock_item_tv_name);
				views.tvAppPackname = (TextView) view
						.findViewById(R.id.app_lock_item_tv_packname);
				views.ivAppLock = (ImageView) view
						.findViewById(R.id.app_lock_item_iv_status);

				views.ivAppIcon.setImageDrawable(info.getIcon());
				views.tvAppName.setText(info.getAppName());
				views.tvAppPackname.setText(info.getPackName());

				if (lockAppInfos.contains(info.getPackName())) { // ����Ǽ����ĳ�����ʹ�ұߵ�ͼ���ɼ�����ͼƬ
					views.ivAppLock.setImageResource(R.drawable.lock);
				} else {
					views.ivAppLock.setImageResource(R.drawable.unlock);
				}

				view.setTag(views);
				return view;
			} else {
				AppManagerViews views = (AppManagerViews) convertView.getTag();
				views.ivAppIcon.setImageDrawable(info.getIcon());
				views.tvAppName.setText(info.getAppName());
				views.tvAppPackname.setText(info.getPackName());
				if (lockAppInfos.contains(info.getPackName())) {
					views.ivAppLock.setImageResource(R.drawable.lock);
				} else {
					views.ivAppLock.setImageResource(R.drawable.unlock);
				}
				return convertView;
			}

		}

	}

	// �����Ż�listview����
	private class AppManagerViews {
		ImageView ivAppIcon;
		TextView tvAppName;
		TextView tvAppPackname;
		ImageView ivAppLock;
	}

	@Override
	protected void initViews() {
		ivPrevious = (ImageView) findViewById(R.id.app_lock_iv_previous);
		ivPrevious.setOnClickListener(this);
		lvAppLock = (ListView) findViewById(R.id.app_lock_lv);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.app_lock_iv_previous:
			Intent previousIntent = new Intent(AppLockActivity.this,
					PrivacyActivity.class);
			startActivity(previousIntent);
			finish();

			break;
		}

	}

}


