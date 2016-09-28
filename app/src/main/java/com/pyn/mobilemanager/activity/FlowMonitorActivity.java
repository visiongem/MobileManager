package com.pyn.mobilemanager.activity;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.TrafficStats;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.db.dao.FlowMonitorDao;
import com.pyn.mobilemanager.view.MyProgressCircle;

/**
 * 流量监控的activity
 */
public class FlowMonitorActivity extends BasicActivity implements
		OnClickListener {

	private TextView tvFlowAll; // 套餐
	private LinearLayout llFlowSetting; // 设置套餐
	private SharedPreferences sp;

	private TextView tvFlowRemainder; // 剩余流量

	private TextView tvFlowUsed; // 已经使用流量数
	private Button btnFlowCheck; // 流量校正Button
	private Dialog checkDialog; // 流量校正对话框
	private EditText etComboCheckSize; // 流量校正输入数据
	private Button btnComboCheckSure, btnComboCheckCancel; // 流量校正的确定和取消按钮

	private Dialog comboDialog; // 　套餐对话框
	private EditText etComboSize; // 设置套餐大小
	private Button btnComboSure, btnComboCancel; // 套餐对话框的确定和取消按钮

	private Calendar calendar; // 为了获得系统日期
	private FlowMonitorDao dao; // 操作流量信息数据库的dao，为了查询当月的流量信息

	private MyProgressCircle myProgressCircle;	// 自定义画圈

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flow_monitor); // 加载布局

		initViews();

		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		dao = new FlowMonitorDao(this);

		long mobileRx = TrafficStats.getMobileRxBytes();
		long mobileTx = TrafficStats.getMobileTxBytes();
		// 本次开机用的2G/3G总流量
		long mobileTotal = mobileRx + mobileTx;

		DecimalFormat formater = new DecimalFormat("####.00");
		String total = formater.format(mobileTotal / 1024f / 1024f);

		String comboSize = sp.getString("combo", ""); // 得到设置过的流量套餐
		if (!comboSize.equals("")) { // 如果设置过套餐
			tvFlowAll.setText(comboSize + " MB");
			tvFlowAll.setTextColor(Color.BLACK);
		} else { // 没有设置套餐的情况,先跳出一个对话框提示一下用户还没设置过套餐
			AlertDialog.Builder alertbBuilder = new AlertDialog.Builder(this);
			alertbBuilder
					.setTitle("温馨提示")
					.setMessage("您还没有设置过套餐！现在是否需要设置一下?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									showComboDialog(); // 显示设置套餐对话框
									dialog.cancel(); // 使对话框消失
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel(); // 使对话框消失
								}
							}).create();

			alertbBuilder.show(); // show出对话框
			tvFlowAll.setText("还未设置套餐信息!");
			tvFlowAll.setTextColor(Color.RED);
		}

		calendar = Calendar.getInstance(); // 获得一个canlendar实例
		String month = String.valueOf(calendar.get(Calendar.MONTH)); // 得到当前月份
		String comboUsedSize = null;
		if (dao.find(month)) { // 如果当前月份有存数据
			comboUsedSize = dao.getFlow(month);
			comboUsedSize = String.valueOf(Float.parseFloat(comboUsedSize)
					+ Float.parseFloat(total));
			tvFlowUsed.setText("已用:" + comboUsedSize + " MB");
		} else {
			comboUsedSize = total;
			tvFlowUsed.setText("已用:0 MB");
		}

		String remainder = "0";
		if (!comboSize.equals("")) { // 如果设置过流量套餐

			if (Float.parseFloat(comboSize) < Float.parseFloat(comboUsedSize)) {
				remainder = "0"; // 如果已经使用的流量大于流量套餐
				myProgressCircle.startCartoom(100); // 画圈画满
			} else { // 算出剩余流量数据
				remainder = String.valueOf(Float.parseFloat(comboSize)
						- Float.parseFloat(comboUsedSize));
				int cartoom = (int) (Float.parseFloat(comboUsedSize) * 100 / Float
						.parseFloat(comboSize));

				myProgressCircle.startCartoom(cartoom); // 画圈
			}
		}
		// 设置剩余流量数据
		tvFlowRemainder.setText(remainder);

	}

	@Override
	protected void initViews() {
		// 流量套餐的TextView
		tvFlowAll = (TextView) findViewById(R.id.flow_monitor_all);
		// 设置套餐的那个LinearLayout
		llFlowSetting = (LinearLayout) findViewById(R.id.flow_monitor_ll_setting);
		llFlowSetting.setOnClickListener(this); // 设置点击事件
		// 已使用的流量
		tvFlowUsed = (TextView) findViewById(R.id.flow_monitor_tv_used);
		myProgressCircle = (MyProgressCircle) findViewById(R.id.hadUsedFlowProgress);
		// 剩余流量
		tvFlowRemainder = (TextView) findViewById(R.id.flow_monitor_tv_remainder);
		// 流量校正按钮
		btnFlowCheck = (Button) findViewById(R.id.flow_monitor_btn_check);
		btnFlowCheck.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		// 套餐设置
		case R.id.flow_monitor_ll_setting:

			showComboDialog();

			break;

		// 确认套餐设置
		case R.id.b_combo_sure:

			if (etComboSize.getText().toString().trim().equals("")) {
				Toast.makeText(FlowMonitorActivity.this, "套餐设置不能为空!", 0).show();
			} else {
				String comboSize = etComboSize.getText().toString().trim(); // 得到输入的套餐数据
				String pattern = "^([0-9]*)$"; // 正则表达式，表示正整数
				if (!comboSize.matches(pattern)) {
					Toast.makeText(FlowMonitorActivity.this, "请输入正确的套餐大小!", 0)
							.show();
				} else {
					Editor editor = sp.edit(); // 得到Editor
					editor.putString("combo", comboSize); // 存起来套餐数
					editor.commit(); // 提交
					// 马上更新一下设置的套餐数据
					tvFlowAll.setText(comboSize + " MB");
					tvFlowAll.setTextColor(Color.BLACK);

					// 设置完套餐后，剩余流量数更新一下，用套餐数减去已经使用的流量
					String remainder = "0";

					String month = String.valueOf(calendar.get(Calendar.MONTH)); // 得到当前月份

					if (dao.find(month)) { // 如果当前月份没有存数据
						String comboUsedSize = dao.getFlow(month);

						if (Float.parseFloat(comboSize) < Float
								.parseFloat(comboUsedSize)) {
							remainder = "0"; // 套餐流量小于已经使用的流量情况
							myProgressCircle.startCartoom(100);
						} else {
							// 计算出剩余流量数据
							remainder = String.valueOf(Float
									.parseFloat(comboSize)
									- Float.parseFloat(comboUsedSize));
							int cartoom = Integer.parseInt(comboUsedSize) * 100
									/ Integer.parseInt(comboSize);
							myProgressCircle.startCartoom(cartoom); // 画圈
						}

					}

					// 设置剩余流量
					tvFlowRemainder.setText(remainder);

				}
				// 跳出个吐司提示用户设置完成
				Toast.makeText(FlowMonitorActivity.this, "设置成功!", 0).show();
				comboDialog.dismiss(); // 关闭对话框
			}
			break;

		// 套餐设置取消
		case R.id.b_combo_cancel:
			comboDialog.dismiss(); // 关闭对话框
			break;

		// 点击了流量校正
		case R.id.flow_monitor_btn_check:

			// 一个校正流量的对话框
			checkDialog = new Dialog(FlowMonitorActivity.this);
			checkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			checkDialog.show();
			Window checkWindow = checkDialog.getWindow();
			checkWindow.setLayout(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			checkWindow.setContentView(R.layout.combo_check);
			etComboCheckSize = (EditText) checkWindow
					.findViewById(R.id.et_combo_check_size);
			btnComboCheckSure = (Button) checkWindow
					.findViewById(R.id.b_combo_check_sure);
			btnComboCheckCancel = (Button) checkWindow
					.findViewById(R.id.b_combo_check_cancel);

			btnComboCheckSure.setOnClickListener(FlowMonitorActivity.this); // 确定
			btnComboCheckCancel.setOnClickListener(FlowMonitorActivity.this); // 　取消

			break;

		// 流量校正取消
		case R.id.b_combo_check_cancel:
			checkDialog.dismiss(); // 关闭对话框
			break;

		// 　流量校正确定按钮
		case R.id.b_combo_check_sure:

			// 没有输入校正值
			if (etComboCheckSize.getText().toString().trim().equals("")) {
				Toast.makeText(FlowMonitorActivity.this, "校正值不能为空!", 0).show();
			} else {
				// 得到校正值
				String combo_used_Size = etComboCheckSize.getText()
						.toString().trim();// 得到校正值
				String pattern = "^[0-9]+(.[0-9]{2})?$"; // 正则表达式，表示正浮点数
				if (!combo_used_Size.matches(pattern)) { // 输入的校正值不为数字
					Toast.makeText(FlowMonitorActivity.this, "请输入正确的数字!", 0)
							.show();
				} else {

					String month = String.valueOf(calendar.get(Calendar.MONTH)); // 得到当前月份

					if (!dao.find(month)) { // 如果当前月份没有存数据
						dao.add(combo_used_Size, month); // 则插入当前月份流量使用数据
					} else {
						dao.updateFlow(combo_used_Size, month); // 否则更新当前月份流量使用数据
					}

					tvFlowUsed.setText("已用:" + combo_used_Size + " MB");

					// 得到套餐大小
					String comboSize = sp.getString("combo", "");

					// 设置完已经流量也要更新一下剩余流量数据
					String remainder = "0";
					if (!comboSize.equals("")) {

						if (Float.parseFloat(comboSize) < Float
								.parseFloat(combo_used_Size)) {
							myProgressCircle.startCartoom(100); // 画圈画满
							comboSize = "0";
						} else {
							remainder = String.valueOf(Float
									.parseFloat(comboSize)
									- Float.parseFloat(combo_used_Size));
							int cartoom = Integer.parseInt(combo_used_Size)
									* 100 / Integer.parseInt(comboSize);
							myProgressCircle.startCartoom(cartoom); // 画圈
						}
					}
					tvFlowRemainder.setText(remainder);

					Toast.makeText(FlowMonitorActivity.this, "设置成功!", 0).show();
					checkDialog.dismiss(); // 关闭对话框
				}
			}
			break;
		}
	}

	private void showComboDialog() {
		// 一个设置流量套餐的对话框
		comboDialog = new Dialog(FlowMonitorActivity.this);
		comboDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		comboDialog.show();
		Window window = comboDialog.getWindow();
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		window.setContentView(R.layout.combo);
		etComboSize = (EditText) window.findViewById(R.id.et_combo_size);
		btnComboSure = (Button) window.findViewById(R.id.b_combo_sure);
		btnComboCancel = (Button) window.findViewById(R.id.b_combo_cancel);

		btnComboSure.setOnClickListener(FlowMonitorActivity.this);
		btnComboCancel.setOnClickListener(FlowMonitorActivity.this);
	}

}
