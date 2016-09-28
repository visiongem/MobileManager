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
 * ������˽������ȫ�������activity,�����ڽ�����˽����֮ʱ���������ˣ��ش�ȫ��������ȷ�Ļ��Ϳ����һ�����
 */
public class SetupPrivacyQuestionActivity extends BasicActivity implements
		OnClickListener {

	private ImageView ivPrevious; // ����
	private EditText etAnswer; // ��
	private Button btnFinish; // ���
	private List<String> questions; // ���⼯��
	private SharedPreferences sp;
	private EditText etQuestion; // ����
	private QuestionsAdapter adapter; // ����������
	private PopupWindow popupWindow;
	private ImageButton ibCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.privacy_setup_question);

		sp = getSharedPreferences("config", Context.MODE_PRIVATE); // �õ�SharedPreferences
		initViews();

	}

	/**
	 * �õ����⼯��
	 * 
	 * @return ���⼯��
	 */
	private List<String> getQuestions() {
		List<String> question = new ArrayList<String>();

		question.add("��ĸ�׵�������?");
		question.add("�Ҹ��׵�������?");
		question.add("����ݺ���λ��?");
		question.add("��ĸ�׵�������?");
		question.add("�Ҹ��׵�������?");

		return question;
	}

	/**
	 * ��ʼ���ؼ�
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
	 * ����¼�
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/***** ���� *****/
		case R.id.privacy_setup_question_iv_previous:
			Intent passwordIntent = new Intent(
					SetupPrivacyQuestionActivity.this,
					SetupPrivacyPwdActivity.class);
			startActivity(passwordIntent);
			finish();
			break;
		/***** ��� *****/
		case R.id.privacy_setup_btn_finish:
			String answer = etAnswer.getText().toString().trim();
			String question = etQuestion.getText().toString().trim();

			if (answer.equals("")) {
				Toast.makeText(getApplicationContext(), "�𰸲���Ϊ��", 0).show();
			} else {
				saveInfo(answer, question);
				Intent appLockServiceIntent = new Intent(
						SetupPrivacyQuestionActivity.this, AppLockService.class);
				startService(appLockServiceIntent); // ��������������
				Intent privacyIntent = new Intent(
						SetupPrivacyQuestionActivity.this,
						PrivacyActivity.class);
				startActivity(privacyIntent);
				Toast.makeText(SetupPrivacyQuestionActivity.this,
						"������ɣ��ɹ�������˽������", 0).show();
				finish();
			}
			break;
		/***** ������ť *****/
		case R.id.privacy_setup_ib_arrow:
			// ����ѡ������Ի���
			showSelectQuestionDialog();
			break;
		}
	}

	/**
	 * ������Ϣ
	 * @param answer
	 * @param question
	 */
	private void saveInfo(String answer, String question) {
		Editor editor = sp.edit();
		editor.putString("privacy_answer", answer); // ����ȫ�����������
		editor.putBoolean("isFirstEnterPrivacy", false); // ��¼�û������ǵ�һ�ν�����˽����
		editor.putString("privacy_question", question); // ����ȫ�����������
		editor.commit();
	}

	/**
	 * ��ʾ����Ի���
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
		// ���õ���ⲿ���Ա��ر�
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		// ����popupWindow���Եõ�����
		popupWindow.setFocusable(true);

		popupWindow.showAsDropDown(etQuestion, 4, -5); // ��ʾ
	}

	/**
	 * ���⼯�ϵ�������
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
