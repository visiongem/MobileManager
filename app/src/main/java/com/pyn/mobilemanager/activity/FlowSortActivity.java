package com.pyn.mobilemanager.activity;

import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ListView;
import android.widget.TextView;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.adapter.FlowSortListAdapter;
import com.pyn.mobilemanager.domain.AppInfo;
import com.pyn.mobilemanager.engine.AppInfoProvider;
import com.pyn.mobilemanager.util.TextFormater;
import com.pyn.mobilemanager.view.LoadingDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 流量管理中的流量排行
 */
public class FlowSortActivity extends BasicActivity {

	private ListView lvFlowSort;

	// 当天开机起共产生的数据流量和wifi流量
	private TextView tvGprs;
	private TextView tvWlan;
	private TextView tvDate; // 日期
	private LoadingDialog dialog;
	private FlowSortListAdapter adapter;

	private AppInfoProvider provider;
	private boolean flag = true;
	private List<AppInfo> infos;

	/**** 动画 ****/
	private Animation animation;
	private LayoutAnimationController lac;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			adapter = new FlowSortListAdapter(FlowSortActivity.this, infos);
			lvFlowSort.setAdapter(adapter);
			dialog.dismiss();

			lvFlowSort.setLayoutAnimation(lac);
			lvFlowSort.startLayoutAnimation();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flow_sort);

		dialog = new LoadingDialog(this);

		initViews();
		initAnimation();
		initInfo();

		infos = new ArrayList<AppInfo>();

		provider = new AppInfoProvider(this);

		dialog.show();
		new Thread() {

			@Override
			public void run() {

				infos = provider.getAppFlowInfo();
				Message msg = Message.obtain();
				handler.sendMessage(msg);
			}
		}.start();

	}

	/**
	 * 初始化动画
	 */
	private void initAnimation() {
		animation = AnimationUtils.loadAnimation(this, R.anim.listview_in);
		lac = new LayoutAnimationController(animation);
		lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
	}

	@Override
	protected void initViews() {
		// 展现各个软件的流量信息的listview
		lvFlowSort = (ListView) findViewById(R.id.flow_sort_lv);
		// 显示的当天日期
		tvDate = (TextView) findViewById(R.id.flow_sort_tv_date);
		// 本次开机用的2G/3G流量
		tvGprs = (TextView) findViewById(R.id.flow_sort_tv_gprs);
		// 本次开机用的wlan流量
		tvWlan = (TextView) findViewById(R.id.flow_sort_tv_wlan);
	}

	private void initInfo() {

		// 得到此时的系统时间
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String nowTime = sDateFormat.format(new java.util.Date());
		tvDate.setText(nowTime); // 设置今日日期

		long mobileRx = TrafficStats.getMobileRxBytes();
		long mobileTx = TrafficStats.getMobileTxBytes();
		// 本次开机用的2G/3G总流量
		long mobileTotal = mobileRx + mobileTx;

		long totalRx = TrafficStats.getTotalRxBytes();
		long totalTx = TrafficStats.getTotalTxBytes();
		// 本次开机用的总流量
		long total = totalTx + totalRx;
		// 本次开机用的wlan总流量
		long wlanTotal = total - mobileTotal;

		tvGprs.setText(TextFormater.getDataSize(mobileTotal));
		tvWlan.setText(TextFormater.getDataSize(wlanTotal));

	}

}
