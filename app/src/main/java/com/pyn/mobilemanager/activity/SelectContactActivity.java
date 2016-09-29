package com.pyn.mobilemanager.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.view.MyContactListView;
import com.pyn.mobilemanager.view.MyContactListView.OnTouchingLetterChangedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 选择联系人的activity
 */
public class SelectContactActivity extends BasicActivity implements
		OnClickListener {

	private TextView contact_overlay;
	private HashMap<String, Integer> alphaIndex; // 存放存在的汉语拼音首字母和与之对应的列表位置
	private String[] sections; // 存放存在的汉语拼音首字母
	private Handler handler;
	private OverlayThread overlayThread;
	private MyContactListView myContactlv;
	private AsyncQueryHandler asyncQuery;
	private static final String NAME = "name", NUMBER = "number",
			SORT_KEY = "sort_key";
	private BaseAdapter adapter;
	private List<ContentValues> list;

	private ImageView ivPrevious;
	private ListView lvSelectContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_contact);

		initViews();

		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		alphaIndex = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		initOverlay();

		lvSelectContact.setOnItemClickListener(new OnItemClickListener() { // 为每个条目设置点击事件，返回当前电话号码

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				String phone = list.get(position).get("number")
						.toString().trim();
				String name = list.get(position).get("name").toString()
						.trim();
				Intent intent = new Intent();
				intent.putExtra("privacy_number", phone);
				intent.putExtra("privacy_name", name);
				intent.putExtra("number", phone);
				intent.putExtra("name", name);
				setResult(0, intent);
				finish();
			}

		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		Uri uri = Uri.parse("content://com.android.contacts/data/phones");
		String[] projection;
		// 获取当前系统的android版本号
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= 19) {
			projection = new String[] { "_id", "display_name", "data1",
					"phonebook_label" };
		} else {
			projection = new String[] { "_id", "display_name", "data1",
					"sort_key" };
		}

		asyncQuery.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc");
	}

	// 查询联系人,AsyncQueryHandler:异步的查询操作帮助类，其实它同样可以处理增删改
	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver contentResolver) {
			super(contentResolver);

		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				list = new ArrayList<ContentValues>();
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					ContentValues values = new ContentValues(); // 通过ContentResolver调用系统URI实现通讯录查询,
					// 通过上下文对象得到这个对象
					cursor.moveToPosition(i);
					String name = cursor.getString(1).trim();
					String number1 = cursor.getString(2).trim();
					String number = "";
					number = number1.replaceAll(" ", ""); // 替换掉电话号码中的空格
					String sortKey = cursor.getString(3).trim();
					if (number.startsWith("+86")) { // 去掉电话号码中的+86
						values.put(NAME, name);
						values.put(NUMBER, number.substring(3)); // 去掉+86
						values.put(SORT_KEY, sortKey);
					} else {
						values.put(NAME, name);
						values.put(NUMBER, number);
						values.put(SORT_KEY, sortKey);
					}
					if (list.contains(values)) { // 把重复的号码去掉
						continue;
					} else {
						list.add(values);
					}
				}
				if (list.size() > 0) {
					setAdapter(list);
				}
			}
		}
	}

	private void setAdapter(List<ContentValues> list) {
		adapter = new SelectContactAdapter(this, list);
		lvSelectContact.setAdapter(adapter);
	}

	/**
	 * 适配器
	 */
	private class SelectContactAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<ContentValues> list;

		public SelectContactAdapter(Context context, List<ContentValues> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndex = new HashMap<String, Integer>();
			sections = new String[list.size()];

			for (int i = 0; i < list.size(); i++) {
				// 当前汉语拼音首字母
				String currentStr = getAlpha(list.get(i)
						.getAsString("sort_key"));
				// 上一个汉语拼音首字母，如果不存在为
				String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1)
						.getAsString(SORT_KEY)) : " ";
				if (!previewStr.equals(currentStr)) {
					String name = getAlpha(list.get(i).getAsString(SORT_KEY));
					alphaIndex.put(name, i);
					sections[i] = name;
				}
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.contact_item, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView
						.findViewById(R.id.tv_contact_item_name);
				holder.number = (TextView) convertView
						.findViewById(R.id.tv_contact_item_number);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ContentValues values = list.get(position);
			holder.name.setText(values.getAsString(NAME));
			holder.number.setText(values.getAsString(NUMBER));
			String currentStr = getAlpha(list.get(position).getAsString(
					SORT_KEY));
			String previewStr = (position - 1) >= 0 ? getAlpha(list.get(
					position - 1).getAsString(SORT_KEY)) : " ";
			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			return convertView;

		}
	}

	private class ViewHolder {
		TextView alpha;
		TextView name;
		TextView number;
	}

	// 初始化汉语拼音首字母弹出提示框
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		contact_overlay = (TextView) inflater.inflate(R.layout.contact_overlay,
				null);
		contact_overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(contact_overlay, lp);
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndex.get(s) != null) {
				int position = alphaIndex.get(s);
				lvSelectContact.setSelection(position);
				contact_overlay.setText(sections[position]);
				contact_overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				// 延迟一秒后执行，让overlay为不可见
				handler.postDelayed(overlayThread, 1000);
			}
		}
	}

	/**
	 * 设置overlay不可见
	 *
	 * @author hp
	 */
	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			contact_overlay.setVisibility(View.GONE);
		}

	}

	/**
	 * 获得汉语拼音首字母
	 *
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}

	/**
	 * 初始化控件
	 */
	@Override
	protected void initViews() {
		ivPrevious = (ImageView) findViewById(R.id.select_contact_iv_previous);
		ivPrevious.setOnClickListener(this);
		lvSelectContact = (ListView) findViewById(R.id.select_contact_lv);
		myContactlv = (MyContactListView) findViewById(R.id.select_contact_mylv);
		myContactlv
				.setOnTouchingLetterChangedListener(new LetterListViewListener());
	}

	/**
	 * 控件点击事件
	 */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			/**
			 * 返回
			 */
			case R.id.select_contact_iv_previous:

				Intent previousIntent = new Intent(SelectContactActivity.this,
						MainActivity.class);
				startActivity(previousIntent);
				finish();

				break;
		}
	}
}
