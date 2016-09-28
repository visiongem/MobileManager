package com.pyn.mobilemanager.receiver;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;

import com.pyn.mobilemanager.db.dao.FlowMonitorDao;

public class ShutdownReceiver extends BroadcastReceiver {

	private FlowMonitorDao dao;
	private Calendar calendar;

	@Override
	public void onReceive(Context context, Intent intent) {

		calendar = Calendar.getInstance();
		String month = String.valueOf(calendar.get(Calendar.MONTH)); // �õ���ǰ�·�

		float mobileRx = TrafficStats.getMobileRxBytes();
		float mobileTx = TrafficStats.getMobileTxBytes();
		// ���ο����õ�2G/3G������
		float mobileTotal = mobileRx + mobileTx;

		if (dao.find(month)) { // �����ǰ�·��д�����

			if (mobileTotal >= 1024 * 1024) {

				DecimalFormat formater = new DecimalFormat("####.00");
				String total = formater.format(mobileTotal / 1024f / 1024f);

				String oldFlow = dao.getFlow(month);
				String newFlow = String.valueOf(Float.parseFloat(oldFlow)
						+ Float.parseFloat(total));

				dao.updateFlow(newFlow, month);
			}

		} else {

			if (mobileTotal >= 1024 * 1024) {

				DecimalFormat formater = new DecimalFormat("####.00");
				String total = formater.format(mobileTotal / 1024f / 1024f);

				dao.add(total, month);
			}
		}
	}
}
