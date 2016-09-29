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
 * 隐私保护中的通讯录保护
 */
public class PrivacyNoteActivity extends BasicActivity implements
		OnClickListener {

	protected static final int LOAD_DATA_FINISH = 40;

	private Dialog dialog;

	// 　手动添加私密联系人的控件
	private TextView tvHandAdd;
	private EditText etHandNumber, etHandName;
	private Button btnHandSure, btnHandCancel;
	private Dialog handDialog;

	// 更改私密联系人的空间
	private Dialog updateDialog;
	private EditText etUpdateNumber, etUpdateName;
	private Button btnUpdateSure, btnUpdateCancel;

	private ImageView ivPrevious;
	private Button btnAdd;
	private LinearLayout llHand;
	private LinearLayout llContact;
	private LinearLayout llLoading;
	private ListView lvPrivacyNote; // 　展现隐私保护号码的listView
	private PrivacySmsDao dao; // 操作隐私保护号码数据库的对象
	// 将隐私保护号码从数据库中一次性取出存入缓存集合中（避免在适配器中频繁的操作数据库）
	private List<PrivacySmsInfo> infos;
	// 显示黑名单号码的适配器对象
	private PrivacyNumberAdapter adpater;

	// 用于接收子线程发送过来的消息，实现UI的更新
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case LOAD_DATA_FINISH: // 从数据库中加载隐私保护号码完成
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

		// 为号码条目添加点击事件
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

		// 1.为lv_call_sms_safe注册一个上下文菜单
		registerForContextMenu(lvPrivacyNote);

		// 一次性获取数据库中的所有数据的操作是一个比较耗时的操作，建议在子线程中完成
		new Thread() {
			public void run() {
				infos = dao.findAll();
				// 通知主线程更新界面
				Message msg = Message.obtain();
				msg.what = LOAD_DATA_FINISH;
				handler.sendMessage(msg);
			};
		}.start();

	}

	// 重写创建上下文菜单的方法
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// 设置长按Item后要显示的布局
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.privacy_note_menu, menu);
	}

	// 3.响应上下文菜单的点击事件
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// 获取到Item对应的对象
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = (int) info.id; // 当前上下文菜单对应的listview里面的哪一个条目
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

			// 点击了返回前一步
			case R.id.privacy_note_iv_previous:
				Intent previousIntent = new Intent(PrivacyNoteActivity.this,
						PrivacyActivity.class);
				startActivity(previousIntent);
				finish();
				break;

			// 点击了添加
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

			// 点击了从联系人添加
			case R.id.ll_contact:
				Intent intent = new Intent(PrivacyNoteActivity.this,
						SelectContactActivity.class);
				startActivityForResult(intent, 0); // 激活一个带有返回值的界面
				dialog.dismiss();
				break;

			// 点击了手动添加
			case R.id.ll_hand:
				handDialog = new Dialog(PrivacyNoteActivity.this);
				handDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				handDialog.show();
				Window handWindow = handDialog.getWindow();
				handWindow.setLayout(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
				handWindow.setContentView(R.layout.hand_addnumber);

				tvHandAdd = (TextView) handWindow.findViewById(R.id.tv_hand_add);
				tvHandAdd.setText("手动添加私密联系人");
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

			// 手动添加号码的取消按钮
			case R.id.bt_hand_cancel:
				handDialog.dismiss();
				break;

			// 手动添加号码的确定按钮
			case R.id.bt_hand_sure:

				boolean result = false;

				if (etHandNumber.getText().toString().trim().equals("")) {
					Toast.makeText(PrivacyNoteActivity.this, "号码不能为空！", 0).show();
				} else {
					String number = etHandNumber.getText().toString().trim();
					String name = etHandName.getText().toString().trim();

					PrivacySmsInfo info = new PrivacySmsInfo();
					info.setName(name);
					info.setNumber(number);
					result = dao.add(number, name);

					if (result) { // 添加或修改数据成功，此时需要更新界面列表中的数据
						// 将新添加的数据添加到集合中，因为适配器是从集合中取数据的
						infos.add(info);
						// 通知适配器重新显示数据（此时，界面上的数据被刷新）
						adpater.notifyDataSetChanged();
						Toast.makeText(PrivacyNoteActivity.this, "已成功添加号码!", 0)
								.show();
					} else {
						Toast.makeText(PrivacyNoteActivity.this, "号码已存在!", 0)
								.show();
					}
					handDialog.dismiss();
				}
				break;

			// 修改号码的取消按钮
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

			if (result) { // 添加或修改数据成功，此时需要更新界面列表中的数据
				// 将新添加的数据添加到集合中，因为适配器是从集合中取数据的
				infos.add(info);
				// 通知适配器重新显示数据（此时，界面上的数据被刷新）
				adpater.notifyDataSetChanged();
				Toast.makeText(PrivacyNoteActivity.this, "已成功添加号码!", 0).show();
			} else {
				Toast.makeText(PrivacyNoteActivity.this, "号码已存在!", 0).show();
			}
		}
	}

	/**
	 * 更新一条隐私保护记录
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
					Toast.makeText(PrivacyNoteActivity.this, "号码不能为空!", 0)
							.show();
				} else {
					updateInfo.setNumber(newNumber);
					updateInfo.setName(newName);

					dao.update(oldNumber, newNumber, newName);
					adpater.notifyDataSetChanged();
					Toast.makeText(PrivacyNoteActivity.this, "已成功修改号码!", 0)
							.show();
					updateDialog.dismiss();
				}
			}
		});

		btnUpdateCancel.setOnClickListener(this);

	}

	/**
	 * 删除一条隐私保护记录
	 *
	 * @param position
	 */
	private void deletePrivacyNumber(int position) {
		PrivacySmsInfo info = (PrivacySmsInfo) lvPrivacyNote
				.getItemAtPosition(position);
		String number = info.getNumber();
		dao.delete(number); // 删除了 数据库里面的记录
		infos.remove(info); // 删除当前listview里面的数据
		Toast.makeText(PrivacyNoteActivity.this, "已删除！", 0).show();
		adpater.notifyDataSetChanged();
	}

	/**
	 * 为隐私保护号码中的listView中的Item适配数据
	 */
	private class PrivacyNumberAdapter extends BaseAdapter {
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
			View view;
			ViewHolder holder;
			// 复用历史缓存的View对象
			if (convertView == null) {
				// 将Item转成View对象
				view = View.inflate(getApplicationContext(),
						R.layout.privacy_note_item, null);
				holder = new ViewHolder();
				holder.tv_privacy_note_number = (TextView) view
						.findViewById(R.id.tv_privacy_note_number);
				view.setTag(holder); // 把控件id的引用 存放在view对象里面
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

	// 将Item中的控件使用static修饰，被static修饰的类的字节码在JVM中只会存在一份。tv_number与tv_mode在栈中也会只存在一份
	private static class ViewHolder {
		TextView tv_privacy_note_number;
	}

	@Override
	protected void initViews() {
		// 返回前一步
		ivPrevious = (ImageView) findViewById(R.id.privacy_note_iv_previous);
		ivPrevious.setOnClickListener(PrivacyNoteActivity.this);
		lvPrivacyNote = (ListView) findViewById(R.id.privacy_note_lv); // 展现隐私保护电话名单的listview
		llLoading = (LinearLayout) findViewById(R.id.privacy_note_ll_loading);
		llLoading.setVisibility(View.VISIBLE);
		btnAdd = (Button) findViewById(R.id.privacy_note_btn_add);
		btnAdd.setOnClickListener(PrivacyNoteActivity.this);
	}

}
