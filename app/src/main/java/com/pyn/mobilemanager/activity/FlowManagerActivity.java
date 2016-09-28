package com.pyn.mobilemanager.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.pyn.mobilemanager.R;

/**
 * 流量管理的activity
 */
public class FlowManagerActivity extends TabActivity implements OnClickListener {

	private ImageView ivPrevious; // 前一步的imageview
	private TextView tvTitle; // 题目
	private TabHost tabHost;
	private TabWidget tabWidget;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flow_manager);

		initViews();

		tabHost = getTabHost();
		tabWidget = getTabWidget();
		LayoutInflater inflater = LayoutInflater.from(this);

		View v1 = inflater.inflate(R.layout.flow_widget_item1, null);
		View v2 = inflater.inflate(R.layout.flow_widget_item2, null);

		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(v1)
				.setContent(new Intent(this, FlowMonitorActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(v2)
				.setContent(new Intent(this, FlowSortActivity.class)));

		tabWidget.setOrientation(LinearLayout.HORIZONTAL);
		tabHost.setCurrentTab(0);
		tabHost.setup();

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {

				for (int i = 0; i < tabWidget.getChildCount(); i++) {
					if (tabHost.getCurrentTab() == 0) {
						tvTitle.setText("流量监控");
					} else if (tabHost.getCurrentTab() == 1) {
						tvTitle.setText("流量详情");
					}
				}
			}
		});

	}

	/**
	 * 初始化控件
	 */
	private void initViews() {
		tvTitle = (TextView) findViewById(R.id.flow_tv_title);
		ivPrevious = (ImageView) findViewById(R.id.flow_iv_previous);
		ivPrevious.setOnClickListener(this); 
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.flow_iv_previous:
			Intent previousIntent = new Intent(FlowManagerActivity.this,
					MainActivity.class);
			startActivity(previousIntent);
			finish();
			break;

		}
	}
}
