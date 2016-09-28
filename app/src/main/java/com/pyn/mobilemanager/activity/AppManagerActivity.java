package com.pyn.mobilemanager.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.domain.AppInfo;
import com.pyn.mobilemanager.engine.AppInfoProvider;
import com.pyn.mobilemanager.view.LoadingDialog;

/**
 * �������
 */
public class AppManagerActivity extends BasicActivity implements
		OnClickListener {

	private static final String TAG = "AppManagerActivity";
	private static final int GET_SYSTEM_APP_FINISH = 80; // ��������һ������������õ�ϵͳ�ĳ���
	private static final int GET_USER_APP_FINISH = 81; // ������һ������������õ��û�����

	private AppInfoProvider provider;
	private AppManagerAdapter adapter;
	private PopupWindow localPopupWindow; // һ��С����

	private List<AppInfo> systemAppInfos; // ��ʾ�����ֻ�����ϵͳ��Ӧ�ó��򼯺�
	private List<AppInfo> userAppInfos; // ��ʾ�����û��������ص�Ӧ�ó��򼯺�

	private ListView lvUserApp;
	private ListView lvSystemApp;

	private ImageView ivPrevious;
	private LoadingDialog loadingDialog;// �Զ���ļ�����
	private String packageName; // ��ʾӦ�ó���İ���

	private ViewPager viewPager; // ҳ������
	private ImageView cursor; // ����ͼƬ
	private TextView tvUserApp, tvSystemApp;
	private List<View> views; // Tabҳ���б�
	private int offset = 0; // ����ͼƬƫ����
	private int currIndex = 0; // ��ǰҳ�����
	private int bmpW; // ����ͼƬ���

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_USER_APP_FINISH:
				loadingDialog.dismiss();
				// ���������ø�listview������������
				adapter = new AppManagerAdapter(userAppInfos,
						AppManagerActivity.this);
				lvUserApp.setAdapter(adapter);

				break;
			case GET_SYSTEM_APP_FINISH:
				loadingDialog.dismiss();
				// ���������ø�listview������������
				adapter = new AppManagerAdapter(systemAppInfos,
						AppManagerActivity.this);
				lvSystemApp.setAdapter(adapter);

				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_manager);

		provider = new AppInfoProvider(AppManagerActivity.this);
		userAppInfos = provider.getAllUserApps();
		systemAppInfos = provider.getAllSystemApps();
		loadingDialog = new LoadingDialog(this);

		initViews();
		ivPrevious.setOnClickListener(this);

		initImageView();
		initTextView();
		initViewPager();
	}

	@Override
	protected void initViews() {
		ivPrevious = (ImageView) findViewById(R.id.app_manager_iv_previous);
		tvUserApp = (TextView) findViewById(R.id.app_manager_tv_userapp);
		tvSystemApp = (TextView) findViewById(R.id.app_manager_tv_systemapp);
		viewPager = (ViewPager) findViewById(R.id.app_manager_vp); // ��ȡViewPager
	}

	/**
	 * ��ʼ���������������ҳ������ʱ������ĺ���Ҳ������Ч������������Ҫ����һЩ����
	 */
	private void initImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor)
				.getWidth(); // ��ȡͼƬ���
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels; // ��ȡ�ֱ��ʿ��
		offset = (screenW / 2 - bmpW) / 2; // ����ƫ����
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix); // ���ö�����ʼλ��
	}

	/**
	 * ��ʼ��ͷ��
	 */
	private void initTextView() {
		tvUserApp.setOnClickListener(new MyOnClickListener(0));
		tvSystemApp.setOnClickListener(new MyOnClickListener(1));
	}

	private void initViewPager() {
		views = new ArrayList<View>(); // Tabҳ���б�,views���������������ҳ������

		LayoutInflater inflater = getLayoutInflater(); // ʵ����һ��LayoutInflater����
		lvUserApp = (ListView) inflater.inflate(R.layout.app_manager_listview,
				null).findViewById(R.id.app_manager_lv); // ͨ��LayoutInflater��ʵ����������ҳ
		lvUserApp.setAdapter(new AppManagerAdapter(userAppInfos,
				AppManagerActivity.this));

		// Ϊ�û�Ӧ�õ�listview��ӵ���¼�
		lvUserApp.setOnItemClickListener(new OnItemClickListener() {
			// ��һ����������ʵ���ǵ�ǰ��listView �ڶ�����������ǰ��view���� ����������:��ǰλ��
			// ���ĸ�����:�кţ�����ô��
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				dismissPopupWindow();

				// ��ȡ��ǰview�����ڴ����е�λ��
				int[] arrayOfInt = new int[2];
				view.getLocationInWindow(arrayOfInt);

				int i = arrayOfInt[0] + 180;
				int j = arrayOfInt[1] + 8;

				showPopupWindow(view, position, i, j);

			}

		});

		/**
		 * ΪlistView������ʱ������¼���������listview��ʱ�򣬹ر�popupwindow
		 */
		lvUserApp.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				dismissPopupWindow();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
			}
		});

		views.add(lvUserApp); // ��ӷ�ҳ��list��

		lvSystemApp = (ListView) inflater.inflate(
				R.layout.app_manager_listview, null).findViewById(
				R.id.app_manager_lv);
		lvSystemApp.setAdapter(new AppManagerAdapter(systemAppInfos,
				AppManagerActivity.this));

		// ΪϵͳӦ�õ�listview��ӵ���¼�
		lvSystemApp.setOnItemClickListener(new OnItemClickListener() {
			// ��һ����������ʵ���ǵ�ǰ��listView �ڶ�����������ǰ��view���� ����������:��ǰλ��
			// ���ĸ�����:�кţ�����ô��
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dismissPopupWindow();

				// ��ȡ��ǰview�����ڴ����е�λ��
				int[] arrayOfInt = new int[2];
				view.getLocationInWindow(arrayOfInt);

				int i = arrayOfInt[0] + 180;
				int j = arrayOfInt[1] + 8;

				showPopupWindow(view, position, i, j);
			}

		});

		lvSystemApp.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				dismissPopupWindow();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
			}
		});

		views.add(lvSystemApp);

		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		viewPager.setOffscreenPageLimit(5);
	}

	private class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW; // ҳ��1 -> ҳ��2 ƫ����

		/**
		 * �˷�������״̬�ı��ʱ����ã�����arg0�������,������״̬��0��1��2�� arg0 ==
		 * 1��ʱ��Ĭʾ���ڻ�����arg0==2��ʱ��Ĭʾ��������ˣ�arg0==0��ʱ��Ĭʾʲô��û��
		 */
		public void onPageScrollStateChanged(int state) {

		}

		/**
		 * ��ҳ���ڻ�����ʱ�����ô˷������ڻ�����ֹ֮ͣǰ���˷�����һֱ�õ����� arg0:��ǰҳ�棬������������ҳ��
		 * arg1:��ǰҳ��ƫ�Ƶİٷֱ� arg2:��ǰҳ��ƫ�Ƶ�����λ��
		 */
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {

		}

		/**
		 * �˷�����ҳ����ת���õ����ã�arg0���㵱ǰѡ�е�ҳ���Position��λ�ñ�ţ�
		 */
		public void onPageSelected(int position) {
			Animation animation = new TranslateAnimation(one * currIndex, one
					* position, 0, 0);
			currIndex = position;
			animation.setFillAfter(true); // True:ͼƬͣ�ڶ�������λ��
			animation.setDuration(300); // ���ö�������ʱ��
			cursor.startAnimation(animation); // ��cursor���ö���

			if (viewPager.getCurrentItem() == 0) {
				initUI(true);
			} else {
				Toast.makeText(AppManagerActivity.this, "ϵͳ�����������ж�أ�", 0)
						.show();
				initUI(false);
			}
		}
	}

	/**
	 * true ������Ǹ����û����� false ������Ǹ���ϵͳ�ĳ���
	 */
	private void initUI(final boolean flag) {
		loadingDialog.show();
		new Thread() {
			@Override
			public void run() {
				if (!flag) {
					systemAppInfos = provider.getAllSystemApps();
					Message msg = new Message();
					msg.what = GET_SYSTEM_APP_FINISH;
					try {
						sleep(800);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					handler.sendMessage(msg);
				} else {
					userAppInfos = provider.getAllUserApps();
					Message msg = new Message();
					msg.what = GET_USER_APP_FINISH;
					try {
						sleep(800);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					handler.sendMessage(msg);
				}
			}
		}.start();

	}

	private class MyViewPagerAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews; // ���췽�������������ǵ�ҳ���������ȽϷ���
		}

		/**
		 * ��viewPager���ƶ���ǰ��view
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(mListViews.get(position)); // ɾ��ҳ��
		}

		/**
		 * �����������ʵ����ҳ��,�����������һ�����󣬸ö������PagerAdapterѡ���ĸ�������ڵ�ǰ��ViewPager��
		 */
		@Override
		public Object instantiateItem(View view, int position) {
			try {
				if (mListViews.get(position).getParent() == null)
					((ViewPager) view).addView(mListViews.get(position), 0);
				else {
					// �����������ӽ�����view���Զ���һ�����࣬����һ������view����������������أ����Եý��
					// ���������������� viewpager java.lang.IllegalStateException: The
					// specified child already has a parent. You must call
					// removeView() on the child's parent first.
					// ����һ�ַ�����viewPager.setOffscreenPageLimit(3); ���ַ��������ж�parent
					// �ǲ����Ѿ����ڣ��������listview���ܱ�destroy
					((ViewGroup) mListViews.get(position).getParent())
							.removeView(mListViews.get(position));
					((ViewPager) view).addView(mListViews.get(position), 0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mListViews.get(position);
		}

		/**
		 * ���ص�ǰ��ҳ��
		 */
		@Override
		public int getCount() {
			return mListViews.size(); // ����ҳ��������
		}

		/**
		 * �÷����ж��Ƿ��ɸö������ɽ���
		 */
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1; // �ٷ���ʾ����д
		}
	}

	/**
	 * ͷ��������
	 */
	private class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			viewPager.setCurrentItem(index);

			switch (v.getId()) {
			case R.id.app_manager_tv_userapp:
				break;

			case R.id.app_manager_tv_systemapp:
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		dismissPopupWindow();
		super.onDestroy();
	}

	/**
	 * ��ʾPopupWindow�ķ���
	 * 
	 * @param view
	 * @param position
	 * @param i
	 * @param j
	 */
	private void showPopupWindow(View view, int position, int i, int j) {

		View popupView = View.inflate(AppManagerActivity.this,
				R.layout.popup_item, null);

		// ��ȡ����LinearLayout
		LinearLayout ll_start = (LinearLayout) popupView
				.findViewById(R.id.ll_start);
		LinearLayout ll_share = (LinearLayout) popupView
				.findViewById(R.id.ll_share);

		// �ѵ�ǰ��Ŀ��listview�е�λ�����ø�view����,Ψһ��ʶһ�����ĸ������С������ʾ����
		ll_share.setTag(position);
		ll_start.setTag(position);

		// Ϊÿ����Ŀ�������ֵ�С����ĸ���LinearLayout��ӵ���¼�
		ll_start.setOnClickListener(AppManagerActivity.this);
		ll_share.setOnClickListener(AppManagerActivity.this);

		LinearLayout ll = (LinearLayout) popupView.findViewById(R.id.ll_popup);
		ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f);
		sa.setDuration(200); // ���ö���ʱ��
		localPopupWindow = new PopupWindow(popupView, 500, 210);
		// һ��Ҫ�ǵø�popupWindow���ñ�����ɫ,��Ȼ�е�ʱ������Ī������Ĵ���
		Drawable background = getResources().getDrawable(
				R.drawable.local_popup_bg);
		localPopupWindow.setBackgroundDrawable(background);
		localPopupWindow.setFocusable(true);
		// Ĭ����false��Ϊfalseʱ��PopupWindowû�л�ý�������
		localPopupWindow.showAtLocation(view, Gravity.LEFT | Gravity.TOP, i, j);
		ll.startAnimation(sa);
	}

	/**
	 * �ر�popupwindow , Ϊ�˱�ֻ֤��һ��popupwindow��ʵ������
	 */
	private void dismissPopupWindow() {
		if (localPopupWindow != null) {
			localPopupWindow.dismiss();
			localPopupWindow = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (viewPager.getCurrentItem() == 0) {
			initUI(true);
		} else {
			initUI(false);
		}
	}

	/**
	 * ������
	 */
	private class AppManagerAdapter extends BaseAdapter {

		private List<AppInfo> appInfos;
		private Context mContext;
		private AppInfo info;

		private ImageView ivUnload;

		public AppManagerAdapter(List<AppInfo> appInfos, Context context) {
			this.appInfos = appInfos;
			this.mContext = context;
		}

		/**
		 * ��������������������
		 * 
		 * @param appinfos
		 */
		public void setAppInfos(List<AppInfo> appInfos) {
			this.appInfos = appInfos;
		}

		@Override
		public int getCount() {
			return appInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return appInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			info = appInfos.get(position);
			View view;
			ViewHolder viewHolder;
			if (convertView == null) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.app_manager_item, null);
				viewHolder = new ViewHolder();
				viewHolder.ivAppIcon = (ImageView) view
						.findViewById(R.id.iv_appmanager_icon);
				viewHolder.tvAppName = (TextView) view
						.findViewById(R.id.tv_appmanager_name);
				viewHolder.tvAppVersion = (TextView) view
						.findViewById(R.id.tv_appmanager_version);
				view.setTag(viewHolder);

			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}

			ivUnload = (ImageView) view.findViewById(R.id.iv_appmanager_unload);

			viewHolder.ivAppIcon.setImageDrawable(info.getIcon());
			viewHolder.tvAppName.setText(info.getAppName());
			viewHolder.tvAppVersion.setText(info.getVersion());

			ivUnload.setTag(position);
			ivUnload.setOnClickListener(AppManagerActivity.this);

			return view;
		}

	}

	class ViewHolder {
		ImageView ivAppIcon;
		TextView tvAppName;
		TextView tvAppVersion;
	}

	@Override
	public void onClick(View view) {
		int position = 0;
		AppInfo appInfoItem = new AppInfo();

		if (viewPager.getCurrentItem() == 0) {
			if (view.getTag() != null) {
				position = (Integer) view.getTag(); // �õ��������Ŀ��λ��
			}
			appInfoItem = userAppInfos.get(position); // �ɵ����Ŀ��λ�õõ��������Ŀ����Ϣ
			packageName = appInfoItem.getPackName(); // �ɵ����Ŀ����Ϣ�õ������Ŀ�İ���

		} else {
			if (view.getTag() != null) {
				position = (Integer) view.getTag(); // �õ��������Ŀ��λ��
			}
			appInfoItem = systemAppInfos.get(position); // �ɵ����Ŀ��λ�õõ��������Ŀ����Ϣ
			packageName = appInfoItem.getPackName(); // �ɵ����Ŀ����Ϣ�õ������Ŀ�İ���
		}

		switch (view.getId()) {

		case R.id.ll_start:
			try {
				PackageInfo info = getPackageManager().getPackageInfo(
						packageName,
						PackageManager.GET_UNINSTALLED_PACKAGES
								| PackageManager.GET_ACTIVITIES);
				ActivityInfo[] activityInfos = info.activities; // �����Ϳ��Եõ�һ��ActivityInfo�ļ���
				if (activityInfos.length > 0) {
					ActivityInfo startActivity = activityInfos[0];
					Intent intent = new Intent();
					intent.setClassName(packageName, startActivity.name);
					startActivity(intent);
					dismissPopupWindow();
				} else {
					Toast.makeText(this, "��ǰӦ�ó����޷�����", 0).show();
				}

			} catch (Exception e) {
				Toast.makeText(this, "Ӧ�ó����޷�����", 0).show();
				e.printStackTrace();
			}
			break;

		case R.id.ll_share:
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.setType("text/plain"); // ��Ҫָ����ͼ����������
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, "����");
			shareIntent.putExtra(Intent.EXTRA_TEXT, "�Ƽ���ʹ��һ��Ӧ�ó���		"
					+ appInfoItem.getAppName()); // ��������
			shareIntent = Intent.createChooser(shareIntent, "����");
			startActivity(shareIntent);
			dismissPopupWindow();
			break;

		case R.id.iv_appmanager_unload:
			if (appInfoItem.isSystemApp()) {
				Toast.makeText(this, "ϵͳӦ�ò��ܱ�ɾ��", 0).show();
			} else {
				Log.i(TAG, "ж��" + packageName);
				String uriString = "package:" + packageName;
				Uri uri = Uri.parse(uriString);
				Intent deleteIntent = new Intent();
				deleteIntent.setAction(Intent.ACTION_DELETE);
				deleteIntent.setData(uri);
				startActivityForResult(deleteIntent, 0);
			}
			break;

		case R.id.app_manager_iv_previous:
			Intent previousIntent = new Intent(AppManagerActivity.this,
					MainActivity.class);
			startActivity(previousIntent);
			finish();
			break;
		}
	}
}
