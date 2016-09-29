package com.pyn.mobilemanager.activity;

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

import java.util.ArrayList;
import java.util.List;

/**
 * 软件管理
 */
public class AppManagerActivity extends BasicActivity implements
		OnClickListener {

	private static final String TAG = "AppManagerActivity";
	private static final int GET_SYSTEM_APP_FINISH = 80; // 　定义了一个常数，代表得到系统的程序
	private static final int GET_USER_APP_FINISH = 81; // 定义了一个常数，代表得到用户程序

	private AppInfoProvider provider;
	private AppManagerAdapter adapter;
	private PopupWindow localPopupWindow; // 一个小窗体

	private List<AppInfo> systemAppInfos; // 表示的是手机里面系统的应用程序集合
	private List<AppInfo> userAppInfos; // 表示的是用户自行下载的应用程序集合

	private ListView lvUserApp;
	private ListView lvSystemApp;

	private ImageView ivPrevious;
	private LoadingDialog loadingDialog;// 自定义的加载条
	private String packageName; // 表示应用程序的包名

	private ViewPager viewPager; // 页卡内容
	private ImageView cursor; // 动画图片
	private TextView tvUserApp, tvSystemApp;
	private List<View> views; // Tab页面列表
	private int offset = 0; // 动画图片偏移量
	private int currIndex = 0; // 当前页卡编号
	private int bmpW; // 动画图片宽度

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case GET_USER_APP_FINISH:
					loadingDialog.dismiss();
					// 把数据设置给listview的数组适配器
					adapter = new AppManagerAdapter(userAppInfos,
							AppManagerActivity.this);
					lvUserApp.setAdapter(adapter);

					break;
				case GET_SYSTEM_APP_FINISH:
					loadingDialog.dismiss();
					// 把数据设置给listview的数组适配器
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
		viewPager = (ViewPager) findViewById(R.id.app_manager_vp); // 获取ViewPager
	}

	/**
	 * 初始化动画，这个就是页卡滑动时，下面的横线也滑动的效果，在这里需要计算一些数据
	 */
	private void initImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor)
				.getWidth(); // 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels; // 获取分辨率宽度
		offset = (screenW / 2 - bmpW) / 2; // 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix); // 设置动画初始位置
	}

	/**
	 * 初始化头标
	 */
	private void initTextView() {
		tvUserApp.setOnClickListener(new MyOnClickListener(0));
		tvSystemApp.setOnClickListener(new MyOnClickListener(1));
	}

	private void initViewPager() {
		views = new ArrayList<View>(); // Tab页面列表,views对象来保存各个分页的内容

		LayoutInflater inflater = getLayoutInflater(); // 实例化一个LayoutInflater对象
		lvUserApp = (ListView) inflater.inflate(R.layout.app_manager_listview,
				null).findViewById(R.id.app_manager_lv); // 通过LayoutInflater来实例化各个分页
		lvUserApp.setAdapter(new AppManagerAdapter(userAppInfos,
				AppManagerActivity.this));

		// 为用户应用的listview添加点击事件
		lvUserApp.setOnItemClickListener(new OnItemClickListener() {
			// 第一个参数：其实就是当前的listView 第二个参数：当前的view对象 第三个参数:当前位置
			// 第四个参数:行号，不怎么用
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				dismissPopupWindow();

				// 获取当前view对象在窗体中的位置
				int[] arrayOfInt = new int[2];
				view.getLocationInWindow(arrayOfInt);

				int i = arrayOfInt[0] + 180;
				int j = arrayOfInt[1] + 8;

				showPopupWindow(view, position, i, j);

			}

		});

		/**
		 * 为listView滚动的时候添加事件，当滚动listview的时候，关闭popupwindow
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

		views.add(lvUserApp); // 添加分页到list中

		lvSystemApp = (ListView) inflater.inflate(
				R.layout.app_manager_listview, null).findViewById(
				R.id.app_manager_lv);
		lvSystemApp.setAdapter(new AppManagerAdapter(systemAppInfos,
				AppManagerActivity.this));

		// 为系统应用的listview添加点击事件
		lvSystemApp.setOnItemClickListener(new OnItemClickListener() {
			// 第一个参数：其实就是当前的listView 第二个参数：当前的view对象 第三个参数:当前位置
			// 第四个参数:行号，不怎么用
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				dismissPopupWindow();

				// 获取当前view对象在窗体中的位置
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

		int one = offset * 2 + bmpW; // 页卡1 -> 页卡2 偏移量

		/**
		 * 此方法是在状态改变的时候调用，其中arg0这个参数,有三种状态（0，1，2） arg0 ==
		 * 1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做
		 */
		public void onPageScrollStateChanged(int state) {

		}

		/**
		 * 当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法会一直得到调用 arg0:当前页面，及你点击滑动的页面
		 * arg1:当前页面偏移的百分比 arg2:当前页面偏移的像素位置
		 */
		public void onPageScrolled(int position, float positionOffset,
								   int positionOffsetPixels) {

		}

		/**
		 * 此方法是页面跳转完后得到调用，arg0是你当前选中的页面的Position（位置编号）
		 */
		public void onPageSelected(int position) {
			Animation animation = new TranslateAnimation(one * currIndex, one
					* position, 0, 0);
			currIndex = position;
			animation.setFillAfter(true); // True:图片停在动画结束位置
			animation.setDuration(300); // 设置动画持续时间
			cursor.startAnimation(animation); // 给cursor设置动画

			if (viewPager.getCurrentItem() == 0) {
				initUI(true);
			} else {
				Toast.makeText(AppManagerActivity.this, "系统软件，不建议卸载！", Toast.LENGTH_SHORT).show();
				initUI(false);
			}
		}
	}

	/**
	 * true 代表的是更新用户程序 false 代表的是更新系统的程序
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
			this.mListViews = mListViews; // 构造方法，参数是我们的页卡，这样比较方便
		}

		/**
		 * 从viewPager中移动当前的view
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(mListViews.get(position)); // 删除页卡
		}

		/**
		 * 这个方法用来实例化页卡,这个方法返回一个对象，该对象表明PagerAdapter选择哪个对象放在当前的ViewPager中
		 */
		@Override
		public Object instantiateItem(View view, int position) {
			try {
				if (mListViews.get(position).getParent() == null)
					((ViewPager) view).addView(mListViews.get(position), 0);
				else {
					// 很难理解新添加进来的view会自动绑定一个父类，由于一个儿子view不能与两个父类相关，所以得解绑
					// 不这样做否则会产生 viewpager java.lang.IllegalStateException: The
					// specified child already has a parent. You must call
					// removeView() on the child's parent first.
					// 还有一种方法是viewPager.setOffscreenPageLimit(3); 这种方法不用判断parent
					// 是不是已经存在，但多余的listview不能被destroy
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
		 * 返回当前分页数
		 */
		@Override
		public int getCount() {
			return mListViews.size(); // 返回页卡的数量
		}

		/**
		 * 该方法判断是否由该对象生成界面
		 */
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1; // 官方提示这样写
		}
	}

	/**
	 * 头标点击监听
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
	 * 显示PopupWindow的方法
	 *
	 * @param view
	 * @param position
	 * @param i
	 * @param j
	 */
	private void showPopupWindow(View view, int position, int i, int j) {

		View popupView = View.inflate(AppManagerActivity.this,
				R.layout.popup_item, null);

		// 获取各个LinearLayout
		LinearLayout ll_start = (LinearLayout) popupView
				.findViewById(R.id.ll_start);
		LinearLayout ll_share = (LinearLayout) popupView
				.findViewById(R.id.ll_share);

		// 把当前条目在listview中的位置设置给view对象,唯一标识一下是哪个软件的小窗体显示出来
		ll_share.setTag(position);
		ll_start.setTag(position);

		// 为每个条目点击后出现的小窗体的各个LinearLayout添加点击事件
		ll_start.setOnClickListener(AppManagerActivity.this);
		ll_share.setOnClickListener(AppManagerActivity.this);

		LinearLayout ll = (LinearLayout) popupView.findViewById(R.id.ll_popup);
		ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f);
		sa.setDuration(200); // 设置动画时间
		localPopupWindow = new PopupWindow(popupView, 500, 210);
		// 一定要记得给popupWindow设置背景颜色,不然有的时候会出现莫名其妙的错误
		Drawable background = getResources().getDrawable(
				R.drawable.local_popup_bg);
		localPopupWindow.setBackgroundDrawable(background);
		localPopupWindow.setFocusable(true);
		// 默认是false，为false时，PopupWindow没有获得焦点能力
		localPopupWindow.showAtLocation(view, Gravity.LEFT | Gravity.TOP, i, j);
		ll.startAnimation(sa);
	}

	/**
	 * 关闭popupwindow , 为了保证只有一个popupwindow的实例存在
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
	 * 适配器
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
		 * 设置数据适配器的数据
		 *
		 * @param appInfos
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
				position = (Integer) view.getTag(); // 得到所点击条目的位置
			}
			appInfoItem = userAppInfos.get(position); // 由点击条目的位置得到所点击条目的信息
			packageName = appInfoItem.getPackName(); // 由点击条目的信息得到点击条目的包名

		} else {
			if (view.getTag() != null) {
				position = (Integer) view.getTag(); // 得到所点击条目的位置
			}
			appInfoItem = systemAppInfos.get(position); // 由点击条目的位置得到所点击条目的信息
			packageName = appInfoItem.getPackName(); // 由点击条目的信息得到点击条目的包名
		}

		switch (view.getId()) {

			case R.id.ll_start:
				try {
					PackageInfo info = getPackageManager().getPackageInfo(
							packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES
									| PackageManager.GET_ACTIVITIES);
					ActivityInfo[] activityInfos = info.activities; // 这样就可以得到一个ActivityInfo的集合
					if (activityInfos.length > 0) {
						ActivityInfo startActivity = activityInfos[0];
						Intent intent = new Intent();
						intent.setClassName(packageName, startActivity.name);
						startActivity(intent);
						dismissPopupWindow();
					} else {
						Toast.makeText(this, "当前应用程序无法启动", Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					Toast.makeText(this, "应用程序无法启动", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				break;

			case R.id.ll_share:
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.setType("text/plain"); // 需要指定意图的数据类型
				shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
				shareIntent.putExtra(Intent.EXTRA_TEXT, "推荐你使用一个应用程序		"
						+ appInfoItem.getAppName()); // 发送内容
				shareIntent = Intent.createChooser(shareIntent, "分享");
				startActivity(shareIntent);
				dismissPopupWindow();
				break;

			case R.id.iv_appmanager_unload:
				if (appInfoItem.isSystemApp()) {
					Toast.makeText(this, "系统应用不能被删除", Toast.LENGTH_SHORT).show();
				} else {
					Log.i(TAG, "卸载" + packageName);
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
