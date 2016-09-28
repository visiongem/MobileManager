package com.pyn.mobilemanager.activity;

import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.db.dao.PrivacySmsDao;
import com.pyn.mobilemanager.domain.PrivacySmsInfo;

/**
 * ��˽�����е�ͨѶ¼����
 */
public class PrivacyNoteActivity extends BasicActivity implements
		OnClickListener {

	protected static final int LOAD_DATA_FINISH = 40;

	private Dialog dialog;

	// ���ֶ����˽����ϵ�˵Ŀؼ�
	private TextView tvHandAdd;
	private EditText etHandNumber, etHandName;
	private Button btnHandSure, btnHandCancel;
	private Dialog handDialog;

	// ����˽����ϵ�˵Ŀռ�
	private Dialog updateDialog;
	private EditText etUpdateNumber, etUpdateName;
	private Button btnUpdateSure, btnUpdateCancel;

	private ImageView ivPrevious;
	private Button btnAdd;
	private LinearLayout llHand;
	private LinearLayout llContact;
	private LinearLayout llLoading;
	private ListView lvPrivacyNote; // ��չ����˽���������listView
	private PrivacySmsDao dao; // ������˽�����������ݿ�Ķ���
	// ����˽������������ݿ���һ����ȡ�����뻺�漯���У���������������Ƶ���Ĳ������ݿ⣩
	private List<PrivacySmsInfo> infos;
	// ��ʾ���������������������
	private PrivacyNumberAdapter adpater;

	// ���ڽ������̷߳��͹�������Ϣ��ʵ��UI�ĸ���
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_DATA_FINISH: // �����ݿ��м�����˽�����������
				llLoading.setVisibility(View.INVISIBLE);
				adpater = new PrivacyNumberAdapter();
				lvPrivacyNote.setAdapter(adpater);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.privacy_note);

		initViews();

		dao = new PrivacySmsDao(this);

		// Ϊ������Ŀ��ӵ���¼�
		lvPrivacyNote.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent smsIntent = new Intent(PrivacyNoteActivity.this,
						PrivacySmsActivity.class);
				String smscontact;
				if (!infos.get(position).getName().equals("")
						&& infos.get(position).getName() != null) {
					smscontact = infos.get(position).getName() + " ("
							+ infos.get(position).getNumber() + " )";
				} else {
					smscontact = infos.get(position).getNumber();
				}
				smsIntent
						.putExtra("smsnumber", infos.get(position).getNumber());
				smsIntent.putExtra("smscontact", smscontact);

				startActivity(smsIntent);

			}

		});

		// 1.Ϊlv_call_sms_safeע��һ�������Ĳ˵�
		registerForContextMenu(lvPrivacyNote);

		// һ���Ի�ȡ���ݿ��е��������ݵĲ�����һ���ȽϺ�ʱ�Ĳ��������������߳������
		new Thread() {
			public void run() {
				infos = dao.findAll();
				// ֪ͨ���̸߳��½���
				Message msg = Message.obtain();
				msg.what = LOAD_DATA_FINISH;
				handler.sendMessage(msg);
			};
		}.start();

	}

	// ��д���������Ĳ˵��ķ���
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// ���ó���Item��Ҫ��ʾ�Ĳ���
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.privacy_note_menu, menu);
	}

	// 3.��Ӧ�����Ĳ˵��ĵ���¼�
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// ��ȡ��Item��Ӧ�Ķ���
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = (int) info.id; // ��ǰ�����Ĳ˵���Ӧ��listview�������һ����Ŀ
		switch (item.getItemId()) {

		case R.id.item_delete:
			deletePrivacyNumber(position);
			return true;
		case R.id.item_update:
			updatePrivacyNumber(position);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		// ����˷���ǰһ��
		case R.id.privacy_note_iv_previous:
			Intent previousIntent = new Intent(PrivacyNoteActivity.this,
					PrivacyActivity.class);
			startActivity(previousIntent);
			finish();
			break;

		// ��������
		case R.id.privacy_note_btn_add:
			dialog = new Dialog(PrivacyNoteActivity.this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.show();
			Window window = dialog.getWindow();
			window.setLayout(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			window.setContentView(R.layout.privacy_note_choice);
			llHand = (LinearLayout) window.findViewById(R.id.ll_hand);
			llLoading = (LinearLayout) window.findViewById(R.id.ll_contact);

			llHand.setOnClickListener(PrivacyNoteActivity.this);
			llLoading.setOnClickListener(PrivacyNoteActivity.this);
			break;

		// ����˴���ϵ�����
		case R.id.ll_contact:
			Intent intent = new Intent(PrivacyNoteActivity.this,
					SelectContactActivity.class);
			startActivityForResult(intent, 0); // ����һ�����з���ֵ�Ľ���
			dialog.dismiss();
			break;

		// ������ֶ����
		case R.id.ll_hand:
			handDialog = new Dialog(PrivacyNoteActivity.this);
			handDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			handDialog.show();
			Window handWindow = handDialog.getWindow();
			handWindow.setLayout(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			handWindow.setContentView(R.layout.hand_addnumber);

			tvHandAdd = (TextView) handWindow.findViewById(R.id.tv_hand_add);
			tvHandAdd.setText("�ֶ����˽����ϵ��");
			etHandNumber = (EditText) handWindow
					.findViewById(R.id.et_hand_number);
			etHandName = (EditText) handWindow.findViewById(R.id.et_hand_name);
			btnHandSure = (Button) handWindow.findViewById(R.id.bt_hand_sure);
			btnHandCancel = (Button) handWindow
					.findViewById(R.id.bt_hand_cancel);

			btnHandSure.setOnClickListener(this);
			btnHandCancel.setOnClickListener(this);

			dialog.dismiss();
			break;

		// �ֶ���Ӻ����ȡ����ť
		case R.id.bt_hand_cancel:
			handDialog.dismiss();
			break;

		// �ֶ���Ӻ����ȷ����ť
		case R.id.bt_hand_sure:

			boolean result = false;

			if (etHandNumber.getText().toString().trim().equals("")) {
				Toast.makeText(PrivacyNoteActivity.this, "���벻��Ϊ�գ�", 0).show();
			} else {
				String number = etHandNumber.getText().toString().trim();
				String name = etHandName.getText().toString().trim();

				PrivacySmsInfo info = new PrivacySmsInfo();
				info.setName(name);
				info.setNumber(number);
				result = dao.add(number, name);

				if (result) { // ��ӻ��޸����ݳɹ�����ʱ��Ҫ���½����б��е�����
					// ������ӵ�������ӵ������У���Ϊ�������ǴӼ�����ȡ���ݵ�
					infos.add(info);
					// ֪ͨ������������ʾ���ݣ���ʱ�������ϵ����ݱ�ˢ�£�
					adpater.notifyDataSetChanged();
					Toast.makeText(PrivacyNoteActivity.this, "�ѳɹ���Ӻ���!", 0)
							.show();
				} else {
					Toast.makeText(PrivacyNoteActivity.this, "�����Ѵ���!", 0)
							.show();
				}
				handDialog.dismiss();
			}
			break;

		// �޸ĺ����ȡ����ť
		case R.id.b_privacy_update_cancel:
			updateDialog.dismiss();
			break;

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {
			boolean result = false;
			String number = data.getStringExtra("privacy_number");
			String name = data.getStringExtra("privacy_name");
			PrivacySmsInfo info = new PrivacySmsInfo();
			info.setName(name);
			info.setNumber(number);
			result = dao.add(number, name);

			if (result) { // ��ӻ��޸����ݳɹ�����ʱ��Ҫ���½����б��е�����
				// ������ӵ�������ӵ������У���Ϊ�������ǴӼ�����ȡ���ݵ�
				infos.add(info);
				// ֪ͨ������������ʾ���ݣ���ʱ�������ϵ����ݱ�ˢ�£�
				adpater.notifyDataSetChanged();
				Toast.makeText(PrivacyNoteActivity.this, "�ѳɹ���Ӻ���!", 0).show();
			} else {
				Toast.makeText(PrivacyNoteActivity.this, "�����Ѵ���!", 0).show();
			}
		}
	}

	/**
	 * ����һ����˽������¼
	 * 
	 * @param position
	 */
	private void updatePrivacyNumber(int position) {
		final PrivacySmsInfo updateInfo = (PrivacySmsInfo) lvPrivacyNote
				.getItemAtPosition(position);
		final String oldNumber = updateInfo.getNumber();
		final String oldName = updateInfo.getName();

		updateDialog = new Dialog(PrivacyNoteActivity.this);
		updateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		updateDialog.show();
		Window updateWindow = updateDialog.getWindow();
		updateWindow.setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		updateWindow.setContentView(R.layout.privacy_update);

		etUpdateNumber = (EditText) updateWindow
				.findViewById(R.id.et_update_number);
		etUpdateName = (EditText) updateWindow
				.findViewById(R.id.et_update_name);
		btnUpdateSure = (Button) updateWindow
				.findViewById(R.id.b_privacy_update_sure);
		btnUpdateCancel = (Button) updateWindow
				.findViewById(R.id.b_privacy_update_cancel);

		etUpdateNumber.setText(oldNumber);
		etUpdateName.setText(oldName);

		btnUpdateSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String newNumber = etUpdateNumber.getText().toString().trim();
				String newName = etUpdateName.getText().toString().trim();

				if (newNumber.equals("")) {
					Toast.makeText(PrivacyNoteActivity.this, "���벻��Ϊ��!", 0)
							.show();
				} else {
					updateInfo.setNumber(newNumber);
					updateInfo.setName(newName);

					dao.update(oldNumber, newNumber, newName);
					adpater.notifyDataSetChanged();
					Toast.makeText(PrivacyNoteActivity.this, "�ѳɹ��޸ĺ���!", 0)
							.show();
					updateDialog.dismiss();
				}
			}
		});

		btnUpdateCancel.setOnClickListener(this);

	}

	/**
	 * ɾ��һ����˽������¼
	 * 
	 * @param position
	 */
	private void deletePrivacyNumber(int position) {
		PrivacySmsInfo info = (PrivacySmsInfo) lvPrivacyNote
				.getItemAtPosition(position);
		String number = info.getNumber();
		dao.delete(number); // ɾ���� ���ݿ�����ļ�¼
		infos.remove(info); // ɾ����ǰlistview���������
		Toast.makeText(PrivacyNoteActivity.this, "��ɾ����", 0).show();
		adpater.notifyDataSetChanged();
	}

	/**
	 * Ϊ��˽���������е�listView�е�Item��������
	 */
	private class PrivacyNumberAdapter extends BaseAdapter {
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
			View view;
			ViewHolder holder;
			// ������ʷ�����View����
			if (convertView == null) {
				// ��Itemת��View����
				view = View.inflate(getApplicationContext(),
						R.layout.privacy_note_item, null);
				holder = new ViewHolder();
				holder.tv_privacy_note_number = (TextView) view
						.findViewById(R.id.tv_privacy_note_number);
				view.setTag(holder); // �ѿؼ�id������ �����view��������
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			PrivacySmsInfo info = infos.get(position);

			if (info.getName() != null && !info.getName().equals("")) {
				holder.tv_privacy_note_number.setText(info.getNumber() + " ( "
						+ info.getName() + " )");
			} else {
				holder.tv_privacy_note_number.setText(info.getNumber());
			}
			return view;
		}
	}

	// ��Item�еĿؼ�ʹ��static���Σ���static���ε�����ֽ�����JVM��ֻ�����һ�ݡ�tv_number��tv_mode��ջ��Ҳ��ֻ����һ��
	private static class ViewHolder {
		TextView tv_privacy_note_number;
	}

	@Override
	protected void initViews() {
		// ����ǰһ��
		ivPrevious = (ImageView) findViewById(R.id.privacy_note_iv_previous);
		ivPrevious.setOnClickListener(PrivacyNoteActivity.this);
		lvPrivacyNote = (ListView) findViewById(R.id.privacy_note_lv); // չ����˽�����绰������listview
		llLoading = (LinearLayout) findViewById(R.id.privacy_note_ll_loading);
		llLoading.setVisibility(View.VISIBLE);
		btnAdd = (Button) findViewById(R.id.privacy_note_btn_add);
		btnAdd.setOnClickListener(PrivacyNoteActivity.this);
	}

}
