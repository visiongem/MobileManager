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
 * 私密联系人的短信通讯界面
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

	private ImageView ivCall; // 拨打电话
	private int index;

	private PrivacySmsDetailDao dao;
	private ListView lvPrivacySms;
	private PrivacySmsDetailInfo info;
	private List<PrivacySmsDetailInfo> infos;
	private PrivacySmsAdapter adpater;

	// 用于接收子线程发送过来的消息，实现UI的更新
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
				case LOAD_SMS_FINISH: // 从数据库中加载隐私保护号码完成
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

		// 一次性获取数据库中的所有数据的操作是一个比较耗时的操作，建议在子线程中完成
		new Thread() {
			public void run() {

				infos = dao.findAll(number);
				// 通知主线程更新界面
				Message msg = Message.obtain();
				msg.what = LOAD_SMS_FINISH;
				handler.sendMessage(msg);
			};
		}.start();

	}

	/**
	 * 控件的点击事件
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

				// 得到此时的系统时间
				SimpleDateFormat sDateFormat = new SimpleDateFormat("MM月dd日  HH:mm");
				String nowTime = sDateFormat.format(new java.util.Date());

				if (etContent.getText().toString().trim().equals("")) {
					Toast.makeText(PrivacySmsActivity.this, "短信内容不能为空", 0).show();
				} else {
					// 得到要发送的短信内容
					String content = etContent.getText().toString().trim();
					SmsManager smsManager = SmsManager.getDefault(); // 得到一个短信发送器
					etContent.setText(""); // 重置短信内容
					smsManager.sendTextMessage(number, null, content, null, null); // 发送短信到目的地

					dao.add(number, nowTime, content); // 加入数据库
					PrivacySmsDetailInfo newInfo = new PrivacySmsDetailInfo();
					newInfo.setTime(nowTime);
					newInfo.setContent(content);
					infos.add(newInfo);
					adpater.notifyDataSetChanged();
				}

				break;

			case R.id.privacy_sms_iv_call:
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ number)); // 创建一个意图
				PrivacySmsActivity.this.startActivity(intent); // 开始意图(及拨打电话)

				break;

			case R.id.ll_retry_sms: // 重发此条短信

				// 得到此时的系统时间
				SimpleDateFormat sDateFormat1 = new SimpleDateFormat(
						"MM月dd日  HH:mm");
				String nowTime1 = sDateFormat1.format(new java.util.Date());
				String content = tempContent;
				SmsManager smsManager = SmsManager.getDefault(); // 得到一个短信发送器
				smsManager.sendTextMessage(number, null, content, null, null); // 发送短信到目的地
				dao.add(number, nowTime1, content); // 加入数据库
				PrivacySmsDetailInfo reNewInfo = new PrivacySmsDetailInfo();
				reNewInfo.setTime(nowTime1);
				reNewInfo.setContent(content);
				infos.add(reNewInfo);
				adpater.notifyDataSetChanged();
				dialog.dismiss();
				break;

			case R.id.ll_delete_sms: // 删除一条短信

				deleteInfo = new PrivacySmsDetailInfo();
				deleteInfo = (PrivacySmsDetailInfo) lvPrivacySms
						.getItemAtPosition(index); // 由点击条目的位置得到所点击条目的信息

				String deleteContent = deleteInfo.getContent().trim();
				dao.delete(deleteContent); // 删除了 数据库里面的记录
				infos.remove(deleteInfo); // 删除当前listview里面的数据
				Toast.makeText(PrivacySmsActivity.this, "已删除！", 0).show();
				adpater.notifyDataSetChanged();
				dialog.dismiss();
				break;

			case R.id.b_privacy_sms_cancel: // 　取消短信操作
				dialog.dismiss();
				break;
		}

	}

	/**
	 * 为listView中的Item适配数据
	 */
	private class PrivacySmsAdapter extends BaseAdapter {
		// 获取Item的数目
		public int getCount() {
			return infos.size();
		}

		// 获取Item的对象
		public Object getItem(int position) {
			return infos.get(position);
		}

		// 获取Item对应的id
		public long getItemId(int position) {
			return position;
		}

		// 在屏幕上，每显示一个Item就调用一次该方法
		public View getView(int position, View convertView, ViewGroup parent) {

			info = infos.get(position);
			View view;
			// 复用历史缓存的View对象
			if (convertView == null) {
				// 将Item转成View对象
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
	 * 长点击一条短信，显示出来的对话框
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
	 * 初始化控件
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
