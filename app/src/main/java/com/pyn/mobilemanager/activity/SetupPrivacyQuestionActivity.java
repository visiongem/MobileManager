package com.pyn.mobilemanager.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.service.AppLockService;

/**
 * 设置隐私保护安全性问题的activity,若是在进入隐私保护之时忘记密码了，回答安全性问题正确的话就可以找回密码
 */
public class SetupPrivacyQuestionActivity extends BasicActivity implements
		OnClickListener {

	private ImageView ivPrevious; // 返回
	private EditText etAnswer; // 答案
	private Button btnFinish; // 完成
	private List<String> questions; // 问题集合
	private SharedPreferences sp;
	private EditText etQuestion; // 问题
	private QuestionsAdapter adapter; // 问题适配器
	private PopupWindow popupWindow;
	private ImageButton ibCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.privacy_setup_question);

		sp = getSharedPreferences("config", Context.MODE_PRIVATE); // 得到SharedPreferences
		initViews();

	}

	/**
	 * 得到问题集合
	 * 
	 * @return 问题集合
	 */
	private List<String> getQuestions() {
		List<String> question = new ArrayList<String>();

		question.add("我母亲的姓名是?");
		question.add("我父亲的姓名是?");
		question.add("我身份后六位是?");
		question.add("我母亲的生日是?");
		question.add("我父亲的生日是?");

		return question;
	}

	/**
	 * 初始化控件
	 */
	@Override
	protected void initViews() {
		ivPrevious = (ImageView) findViewById(R.id.privacy_setup_question_iv_previous);
		ivPrevious.setOnClickListener(this);
		etAnswer = (EditText) findViewById(R.id.privacy_setup_et_answer);
		btnFinish = (Button) findViewById(R.id.privacy_setup_btn_finish);
		btnFinish.setOnClickListener(this);

		etQuestion = (EditText) findViewById(R.id.privacy_setup_et_question);
		ibCheck = (ImageButton) findViewById(R.id.privacy_setup_ib_arrow);
		ibCheck.setOnClickListener(this);
	}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/***** 返回 *****/
		case R.id.privacy_setup_question_iv_previous:
			Intent passwordIntent = new Intent(
					SetupPrivacyQuestionActivity.this,
					SetupPrivacyPwdActivity.class);
			startActivity(passwordIntent);
			finish();
			break;
		/***** 完成 *****/
		case R.id.privacy_setup_btn_finish:
			String answer = etAnswer.getText().toString().trim();
			String question = etQuestion.getText().toString().trim();

			if (answer.equals("")) {
				Toast.makeText(getApplicationContext(), "答案不能为空", 0).show();
			} else {
				saveInfo(answer, question);
				Intent appLockServiceIntent = new Intent(
						SetupPrivacyQuestionActivity.this, AppLockService.class);
				startService(appLockServiceIntent); // 开启程序锁服务
				Intent privacyIntent = new Intent(
						SetupPrivacyQuestionActivity.this,
						PrivacyActivity.class);
				startActivity(privacyIntent);
				Toast.makeText(SetupPrivacyQuestionActivity.this,
						"设置完成，成功进入隐私保护！", 0).show();
				finish();
			}
			break;
		/***** 下拉按钮 *****/
		case R.id.privacy_setup_ib_arrow:
			// 弹出选择问题对话框
			showSelectQuestionDialog();
			break;
		}
	}

	/**
	 * 保存信息
	 * @param answer
	 * @param question
	 */
	private void saveInfo(String answer, String question) {
		Editor editor = sp.edit();
		editor.putString("privacy_answer", answer); // 将安全性问题存下来
		editor.putBoolean("isFirstEnterPrivacy", false); // 记录用户并不是第一次进入隐私保护
		editor.putString("privacy_question", question); // 将安全性问题存下来
		editor.commit();
	}

	/**
	 * 显示问题对话框
	 */
	private void showSelectQuestionDialog() {
		questions = getQuestions();
		ListView lv = new ListView(this);
		lv.setBackgroundResource(R.drawable.icon_spinner_listview_background);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String question = questions.get(position);
				etQuestion.setText(question);

				popupWindow.dismiss();
			}

		});

		adapter = new QuestionsAdapter();
		lv.setAdapter(adapter);

		popupWindow = new PopupWindow(lv, etQuestion.getWidth() - 4, 400);
		// 设置点击外部可以被关闭
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		// 设置popupWindow可以得到焦点
		popupWindow.setFocusable(true);

		popupWindow.showAsDropDown(etQuestion, 4, -5); // 显示
	}

	/**
	 * 问题集合的适配器
	 */
	class QuestionsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return questions.size();
		}

		@Override
		public Object getItem(int position) {
			return questions.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			QuestionViewHolder mHolder = null;
			if (convertView == null) {
				mHolder = new QuestionViewHolder();
				convertView = LayoutInflater.from(
						SetupPrivacyQuestionActivity.this).inflate(
						R.layout.spinner_item, null);
				mHolder.tvQuestion = (TextView) convertView
						.findViewById(R.id.spinner_tv_question);
				convertView.setTag(mHolder);
			} else {
				mHolder = (QuestionViewHolder) convertView.getTag();
			}

			mHolder.tvQuestion.setText(questions.get(position));

			return convertView;
		}

	}

	public class QuestionViewHolder {
		public TextView tvQuestion;
	}

}
