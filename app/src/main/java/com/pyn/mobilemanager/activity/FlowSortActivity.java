package com.pyn.mobilemanager.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.adapter.FlowSortListAdapter;
import com.pyn.mobilemanager.domain.AppInfo;
import com.pyn.mobilemanager.engine.AppInfoProvider;
import com.pyn.mobilemanager.util.TextFormater;
import com.pyn.mobilemanager.view.LoadingDialog;

/**
 * ���������е���������
 */
public class FlowSortActivity extends BasicActivity {

	private ListView lvFlowSort;

	// ���쿪���𹲲���������������wifi����
	private TextView tvGprs;
	private TextView tvWlan;
	private TextView tvDate; // ����
	private LoadingDialog dialog;
	private FlowSortListAdapter adapter;

	private AppInfoProvider provider;
	private boolean flag = true;
	private List<AppInfo> infos;

	/**** ���� ****/
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
	 * ��ʼ������
	 */
	private void initAnimation() {
		animation = AnimationUtils.loadAnimation(this, R.anim.listview_in);
		lac = new LayoutAnimationController(animation);
		lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
	}

	@Override
	protected void initViews() {
		// չ�ָ��������������Ϣ��listview
		lvFlowSort = (ListView) findViewById(R.id.flow_sort_lv);
		// ��ʾ�ĵ�������
		tvDate = (TextView) findViewById(R.id.flow_sort_tv_date);
		// ���ο����õ�2G/3G����
		tvGprs = (TextView) findViewById(R.id.flow_sort_tv_gprs);
		// ���ο����õ�wlan����
		tvWlan = (TextView) findViewById(R.id.flow_sort_tv_wlan);
	}

	private void initInfo() {

		// �õ���ʱ��ϵͳʱ��
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String nowTime = sDateFormat.format(new java.util.Date());
		tvDate.setText(nowTime); // ���ý�������

		long mobileRx = TrafficStats.getMobileRxBytes();
		long mobileTx = TrafficStats.getMobileTxBytes();
		// ���ο����õ�2G/3G������
		long mobileTotal = mobileRx + mobileTx;

		long totalRx = TrafficStats.getTotalRxBytes();
		long totalTx = TrafficStats.getTotalTxBytes();
		// ���ο����õ�������
		long total = totalTx + totalRx;
		// ���ο����õ�wlan������
		long wlanTotal = total - mobileTotal;

		tvGprs.setText(TextFormater.getDataSize(mobileTotal));
		tvWlan.setText(TextFormater.getDataSize(wlanTotal));

	}

}
