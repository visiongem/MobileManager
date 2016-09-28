package com.pyn.mobilemanager.activity;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.db.dao.PrivacySmsDetailDao;
import com.pyn.mobilemanager.domain.PrivacySmsDetailInfo;

/**
 * ˽����ϵ�˵Ķ���ͨѶ����
 */
public class PrivacySmsActivity extends BasicActivity implements
		OnClickListener {

	protected static final int LOAD_SMS_FINISH = 30;
	private TextView tvContact;
	private ImageView ivPrevious;
	private EditText etContent;
	private Button btnSend;
	private Intent intent;
	private String number;

	private static TextView tvTime;
	private static TextView tvContent;

	private Dialog dialog;
	private LinearLayout llRetrySms;
	private LinearLayout llDeleteSms;
	private Button btnCancel;
	private String tempContent;
	private PrivacySmsDetailInfo deleteInfo;

	private ImageView ivCall; // ����绰
	private int index;

	private PrivacySmsDetailDao dao;
	private ListView lvPrivacySms;
	private PrivacySmsDetailInfo info;
	private List<PrivacySmsDetailInfo> infos;
	private PrivacySmsAdapter adpater;

	// ���ڽ������̷߳��͹�������Ϣ��ʵ��UI�ĸ���
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case LOAD_SMS_FINISH: // �����ݿ��м�����˽�����������
				adpater = new PrivacySmsAdapter();
				lvPrivacySms.setAdapter(adpater);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.privacy_sms);

		initViews();

		intent = getIntent();
		tvContact.setText(intent.getStringExtra("smscontact"));
		number = intent.getStringExtra("smsnumber");

		dao = new PrivacySmsDetailDao(this);

		lvPrivacySms.setEnabled(false);

		// һ���Ի�ȡ���ݿ��е��������ݵĲ�����һ���ȽϺ�ʱ�Ĳ��������������߳������
		new Thread() {
			public void run() {

				infos = dao.findAll(number);
				// ֪ͨ���̸߳��½���
				Message msg = Message.obtain();
				msg.what = LOAD_SMS_FINISH;
				handler.sendMessage(msg);
			};
		}.start();

	}

	/**
	 * �ؼ��ĵ���¼�
	 */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.privacy_sms_iv_previous:
			Intent previousIntent = new Intent(PrivacySmsActivity.this,
					PrivacyNoteActivity.class);
			startActivity(previousIntent);
			finish();
			break;

		case R.id.privacy_sms_btn_send:

			// �õ���ʱ��ϵͳʱ��
			SimpleDateFormat sDateFormat = new SimpleDateFormat("MM��dd��  HH:mm");
			String nowTime = sDateFormat.format(new java.util.Date());

			if (etContent.getText().toString().trim().equals("")) {
				Toast.makeText(PrivacySmsActivity.this, "�������ݲ���Ϊ��", 0).show();
			} else {
				// �õ�Ҫ���͵Ķ�������
				String content = etContent.getText().toString().trim();
				SmsManager smsManager = SmsManager.getDefault(); // �õ�һ�����ŷ�����
				etContent.setText(""); // ���ö�������
				smsManager.sendTextMessage(number, null, content, null, null); // ���Ͷ��ŵ�Ŀ�ĵ�

				dao.add(number, nowTime, content); // �������ݿ�
				PrivacySmsDetailInfo newInfo = new PrivacySmsDetailInfo();
				newInfo.setTime(nowTime);
				newInfo.setContent(content);
				infos.add(newInfo);
				adpater.notifyDataSetChanged();
			}

			break;

		case R.id.privacy_sms_iv_call:
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ number)); // ����һ����ͼ
			PrivacySmsActivity.this.startActivity(intent); // ��ʼ��ͼ(������绰)

			break;

		case R.id.ll_retry_sms: // �ط���������

			// �õ���ʱ��ϵͳʱ��
			SimpleDateFormat sDateFormat1 = new SimpleDateFormat(
					"MM��dd��  HH:mm");
			String nowTime1 = sDateFormat1.format(new java.util.Date());
			String content = tempContent;
			SmsManager smsManager = SmsManager.getDefault(); // �õ�һ�����ŷ�����
			smsManager.sendTextMessage(number, null, content, null, null); // ���Ͷ��ŵ�Ŀ�ĵ�
			dao.add(number, nowTime1, content); // �������ݿ�
			PrivacySmsDetailInfo reNewInfo = new PrivacySmsDetailInfo();
			reNewInfo.setTime(nowTime1);
			reNewInfo.setContent(content);
			infos.add(reNewInfo);
			adpater.notifyDataSetChanged();
			dialog.dismiss();
			break;

		case R.id.ll_delete_sms: // ɾ��һ������

			deleteInfo = new PrivacySmsDetailInfo();
			deleteInfo = (PrivacySmsDetailInfo) lvPrivacySms
					.getItemAtPosition(index); // �ɵ����Ŀ��λ�õõ��������Ŀ����Ϣ

			String deleteContent = deleteInfo.getContent().trim();
			dao.delete(deleteContent); // ɾ���� ���ݿ�����ļ�¼
			infos.remove(deleteInfo); // ɾ����ǰlistview���������
			Toast.makeText(PrivacySmsActivity.this, "��ɾ����", 0).show();
			adpater.notifyDataSetChanged();
			dialog.dismiss();
			break;

		case R.id.b_privacy_sms_cancel: // ��ȡ�����Ų���
			dialog.dismiss();
			break;
		}

	}

	/**
	 * ΪlistView�е�Item��������
	 */
	private class PrivacySmsAdapter extends BaseAdapter {
		// ��ȡItem����Ŀ
		public int getCount() {
			return infos.size();
		}

		// ��ȡItem�Ķ���
		public Object getItem(int position) {
			return infos.get(position);
		}

		// ��ȡItem��Ӧ��id
		public long getItemId(int position) {
			return position;
		}

		// ����Ļ�ϣ�ÿ��ʾһ��Item�͵���һ�θ÷���
		public View getView(int position, View convertView, ViewGroup parent) {

			info = infos.get(position);
			View view;
			// ������ʷ�����View����
			if (convertView == null) {
				// ��Itemת��View����
				view = View.inflate(getApplicationContext(),
						R.layout.privacy_sms_item, null);

			} else {
				view = convertView;
			}

			tvTime = (TextView) view.findViewById(R.id.tv_privacy_sms_time);
			tvContent = (TextView) view
					.findViewById(R.id.tv_privacy_sms_content);

			tvTime.setText(info.getTime());
			tvContent.setText(info.getContent());

			tvContent.setTag(position);

			tvContent.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {

					index = (Integer) v.getTag();

					TextView tv = (TextView) v;
					showDialog(tv);

					return false;
				}
			});

			return view;
		}
	}

	/**
	 * �����һ�����ţ���ʾ�����ĶԻ���
	 * 
	 * @param tv
	 */
	private void showDialog(TextView tv) {

		tempContent = tv.getText().toString().trim();

		dialog = new Dialog(PrivacySmsActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.show();
		Window window = dialog.getWindow();
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		window.setContentView(R.layout.privacy_sms_content_choice);

		llRetrySms = (LinearLayout) window.findViewById(R.id.ll_retry_sms);
		llDeleteSms = (LinearLayout) window.findViewById(R.id.ll_delete_sms);
		btnCancel = (Button) window.findViewById(R.id.b_privacy_sms_cancel);

		llRetrySms.setOnClickListener(PrivacySmsActivity.this);
		llDeleteSms.setOnClickListener(PrivacySmsActivity.this);
		btnCancel.setOnClickListener(PrivacySmsActivity.this);
	}

	/**
	 * ��ʼ���ؼ�
	 */
	@Override
	protected void initViews() {
		tvContact = (TextView) findViewById(R.id.privacy_sms_tv_contact);
		lvPrivacySms = (ListView) findViewById(R.id.privacy_sms_lv);
		etContent = (EditText) findViewById(R.id.privacy_sms_et_content);

		btnSend = (Button) findViewById(R.id.privacy_sms_btn_send);
		btnSend.setOnClickListener(this);

		ivPrevious = (ImageView) findViewById(R.id.privacy_sms_iv_previous);
		ivPrevious.setOnClickListener(this);

		ivCall = (ImageView) findViewById(R.id.privacy_sms_iv_call);
		ivCall.setOnClickListener(this);
	}

}
