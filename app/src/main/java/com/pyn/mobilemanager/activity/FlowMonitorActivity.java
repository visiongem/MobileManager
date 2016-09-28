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
 * ������ص�activity
 */
public class FlowMonitorActivity extends BasicActivity implements
		OnClickListener {

	private TextView tvFlowAll; // �ײ�
	private LinearLayout llFlowSetting; // �����ײ�
	private SharedPreferences sp;

	private TextView tvFlowRemainder; // ʣ������

	private TextView tvFlowUsed; // �Ѿ�ʹ��������
	private Button btnFlowCheck; // ����У��Button
	private Dialog checkDialog; // ����У���Ի���
	private EditText etComboCheckSize; // ����У����������
	private Button btnComboCheckSure, btnComboCheckCancel; // ����У����ȷ����ȡ����ť

	private Dialog comboDialog; // ���ײͶԻ���
	private EditText etComboSize; // �����ײʹ�С
	private Button btnComboSure, btnComboCancel; // �ײͶԻ����ȷ����ȡ����ť

	private Calendar calendar; // Ϊ�˻��ϵͳ����
	private FlowMonitorDao dao; // ����������Ϣ���ݿ��dao��Ϊ�˲�ѯ���µ�������Ϣ

	private MyProgressCircle myProgressCircle;	// �Զ��廭Ȧ

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flow_monitor); // ���ز���

		initViews();

		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		dao = new FlowMonitorDao(this);

		long mobileRx = TrafficStats.getMobileRxBytes();
		long mobileTx = TrafficStats.getMobileTxBytes();
		// ���ο����õ�2G/3G������
		long mobileTotal = mobileRx + mobileTx;

		DecimalFormat formater = new DecimalFormat("####.00");
		String total = formater.format(mobileTotal / 1024f / 1024f);

		String comboSize = sp.getString("combo", ""); // �õ����ù��������ײ�
		if (!comboSize.equals("")) { // ������ù��ײ�
			tvFlowAll.setText(comboSize + " MB");
			tvFlowAll.setTextColor(Color.BLACK);
		} else { // û�������ײ͵����,������һ���Ի�����ʾһ���û���û���ù��ײ�
			AlertDialog.Builder alertbBuilder = new AlertDialog.Builder(this);
			alertbBuilder
					.setTitle("��ܰ��ʾ")
					.setMessage("����û�����ù��ײͣ������Ƿ���Ҫ����һ��?")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									showComboDialog(); // ��ʾ�����ײͶԻ���
									dialog.cancel(); // ʹ�Ի�����ʧ
								}
							})
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel(); // ʹ�Ի�����ʧ
								}
							}).create();

			alertbBuilder.show(); // show���Ի���
			tvFlowAll.setText("��δ�����ײ���Ϣ!");
			tvFlowAll.setTextColor(Color.RED);
		}

		calendar = Calendar.getInstance(); // ���һ��canlendarʵ��
		String month = String.valueOf(calendar.get(Calendar.MONTH)); // �õ���ǰ�·�
		String comboUsedSize = null;
		if (dao.find(month)) { // �����ǰ�·��д�����
			comboUsedSize = dao.getFlow(month);
			comboUsedSize = String.valueOf(Float.parseFloat(comboUsedSize)
					+ Float.parseFloat(total));
			tvFlowUsed.setText("����:" + comboUsedSize + " MB");
		} else {
			comboUsedSize = total;
			tvFlowUsed.setText("����:0 MB");
		}

		String remainder = "0";
		if (!comboSize.equals("")) { // ������ù������ײ�

			if (Float.parseFloat(comboSize) < Float.parseFloat(comboUsedSize)) {
				remainder = "0"; // ����Ѿ�ʹ�õ��������������ײ�
				myProgressCircle.startCartoom(100); // ��Ȧ����
			} else { // ���ʣ����������
				remainder = String.valueOf(Float.parseFloat(comboSize)
						- Float.parseFloat(comboUsedSize));
				int cartoom = (int) (Float.parseFloat(comboUsedSize) * 100 / Float
						.parseFloat(comboSize));

				myProgressCircle.startCartoom(cartoom); // ��Ȧ
			}
		}
		// ����ʣ����������
		tvFlowRemainder.setText(remainder);

	}

	@Override
	protected void initViews() {
		// �����ײ͵�TextView
		tvFlowAll = (TextView) findViewById(R.id.flow_monitor_all);
		// �����ײ͵��Ǹ�LinearLayout
		llFlowSetting = (LinearLayout) findViewById(R.id.flow_monitor_ll_setting);
		llFlowSetting.setOnClickListener(this); // ���õ���¼�
		// ��ʹ�õ�����
		tvFlowUsed = (TextView) findViewById(R.id.flow_monitor_tv_used);
		myProgressCircle = (MyProgressCircle) findViewById(R.id.hadUsedFlowProgress);
		// ʣ������
		tvFlowRemainder = (TextView) findViewById(R.id.flow_monitor_tv_remainder);
		// ����У����ť
		btnFlowCheck = (Button) findViewById(R.id.flow_monitor_btn_check);
		btnFlowCheck.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		// �ײ�����
		case R.id.flow_monitor_ll_setting:

			showComboDialog();

			break;

		// ȷ���ײ�����
		case R.id.b_combo_sure:

			if (etComboSize.getText().toString().trim().equals("")) {
				Toast.makeText(FlowMonitorActivity.this, "�ײ����ò���Ϊ��!", 0).show();
			} else {
				String comboSize = etComboSize.getText().toString().trim(); // �õ�������ײ�����
				String pattern = "^([0-9]*)$"; // ������ʽ����ʾ������
				if (!comboSize.matches(pattern)) {
					Toast.makeText(FlowMonitorActivity.this, "��������ȷ���ײʹ�С!", 0)
							.show();
				} else {
					Editor editor = sp.edit(); // �õ�Editor
					editor.putString("combo", comboSize); // �������ײ���
					editor.commit(); // �ύ
					// ���ϸ���һ�����õ��ײ�����
					tvFlowAll.setText(comboSize + " MB");
					tvFlowAll.setTextColor(Color.BLACK);

					// �������ײͺ�ʣ������������һ�£����ײ�����ȥ�Ѿ�ʹ�õ�����
					String remainder = "0";

					String month = String.valueOf(calendar.get(Calendar.MONTH)); // �õ���ǰ�·�

					if (dao.find(month)) { // �����ǰ�·�û�д�����
						String comboUsedSize = dao.getFlow(month);

						if (Float.parseFloat(comboSize) < Float
								.parseFloat(comboUsedSize)) {
							remainder = "0"; // �ײ�����С���Ѿ�ʹ�õ��������
							myProgressCircle.startCartoom(100);
						} else {
							// �����ʣ����������
							remainder = String.valueOf(Float
									.parseFloat(comboSize)
									- Float.parseFloat(comboUsedSize));
							int cartoom = Integer.parseInt(comboUsedSize) * 100
									/ Integer.parseInt(comboSize);
							myProgressCircle.startCartoom(cartoom); // ��Ȧ
						}

					}

					// ����ʣ������
					tvFlowRemainder.setText(remainder);

				}
				// ��������˾��ʾ�û��������
				Toast.makeText(FlowMonitorActivity.this, "���óɹ�!", 0).show();
				comboDialog.dismiss(); // �رնԻ���
			}
			break;

		// �ײ�����ȡ��
		case R.id.b_combo_cancel:
			comboDialog.dismiss(); // �رնԻ���
			break;

		// ���������У��
		case R.id.flow_monitor_btn_check:

			// һ��У�������ĶԻ���
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

			btnComboCheckSure.setOnClickListener(FlowMonitorActivity.this); // ȷ��
			btnComboCheckCancel.setOnClickListener(FlowMonitorActivity.this); // ��ȡ��

			break;

		// ����У��ȡ��
		case R.id.b_combo_check_cancel:
			checkDialog.dismiss(); // �رնԻ���
			break;

		// ������У��ȷ����ť
		case R.id.b_combo_check_sure:

			// û������У��ֵ
			if (etComboCheckSize.getText().toString().trim().equals("")) {
				Toast.makeText(FlowMonitorActivity.this, "У��ֵ����Ϊ��!", 0).show();
			} else {
				// �õ�У��ֵ
				String combo_used_Size = etComboCheckSize.getText()
						.toString().trim();// �õ�У��ֵ
				String pattern = "^[0-9]+(.[0-9]{2})?$"; // ������ʽ����ʾ��������
				if (!combo_used_Size.matches(pattern)) { // �����У��ֵ��Ϊ����
					Toast.makeText(FlowMonitorActivity.this, "��������ȷ������!", 0)
							.show();
				} else {

					String month = String.valueOf(calendar.get(Calendar.MONTH)); // �õ���ǰ�·�

					if (!dao.find(month)) { // �����ǰ�·�û�д�����
						dao.add(combo_used_Size, month); // ����뵱ǰ�·�����ʹ������
					} else {
						dao.updateFlow(combo_used_Size, month); // ������µ�ǰ�·�����ʹ������
					}

					tvFlowUsed.setText("����:" + combo_used_Size + " MB");

					// �õ��ײʹ�С
					String comboSize = sp.getString("combo", "");

					// �������Ѿ�����ҲҪ����һ��ʣ����������
					String remainder = "0";
					if (!comboSize.equals("")) {

						if (Float.parseFloat(comboSize) < Float
								.parseFloat(combo_used_Size)) {
							myProgressCircle.startCartoom(100); // ��Ȧ����
							comboSize = "0";
						} else {
							remainder = String.valueOf(Float
									.parseFloat(comboSize)
									- Float.parseFloat(combo_used_Size));
							int cartoom = Integer.parseInt(combo_used_Size)
									* 100 / Integer.parseInt(comboSize);
							myProgressCircle.startCartoom(cartoom); // ��Ȧ
						}
					}
					tvFlowRemainder.setText(remainder);

					Toast.makeText(FlowMonitorActivity.this, "���óɹ�!", 0).show();
					checkDialog.dismiss(); // �رնԻ���
				}
			}
			break;
		}
	}

	private void showComboDialog() {
		// һ�����������ײ͵ĶԻ���
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
