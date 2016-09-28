package com.pyn.mobilemanager.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.service.AppLockService;
import com.pyn.mobilemanager.util.MD5Encoder;
import com.pyn.mobilemanager.util.ServiceUtil;

/**
 * ������˽�������ݲ�ͬ��������������ز�ͬ�Ľ���
 */
public class EnterPrivacyActivity extends BasicActivity implements
		OnClickListener {

	private SharedPreferences sp;
	private boolean isFirstEnterPrivacy; // �ж��Ƿ��һ�ν�����˽����

	/***** ��һ�ν�����˽������������ *****/
	private ImageView ivFirstEnterPrevious;
	private Button btnFirstEnterStart;

	/***** �ǵ�һ�ν�����˽������������ *****/
	private ImageView ivStartPrivacyPrevious;
	private EditText etStartPrivacyPassword;
	private Button btnStartPrivacy;
	private TextView tvStartPrivacyForgetpwd;

	/***** ��������������� *****/
	private EditText etResetPwdQuestion;
	private EditText etResetPwdAnswer;
	private Button btnResetNext;
	private ImageView ivResetPwdPrevious;

	/***** �޸������������ *****/
	private ImageView ivAlterpwdPrevious;
	private EditText etAlterPassword, etAlterPasswordConfirm;
	private Button btnAlterFinish;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sp = getSharedPreferences("config", Context.MODE_PRIVATE); // �õ�SharedPreferences
		isFirstEnterPrivacy = sp.getBoolean("isFirstEnterPrivacy", true); // Ĭ��Ϊ��һ�ν�����˽����

		// �ж��û��Ƿ��һ�ν�����˽����
		if (isFirstEnterPrivacy) {
			showFirstEnter(); // ��ʾ��һ�ν�����˽�������棬������˽����
		} else {
			ServiceUtil serviceUtil = new ServiceUtil(this);
			if (!serviceUtil
					.isWorked("com.pyn.mobilemanager.service.AppLockService")) {
				Intent appLockServiceIntent = new Intent(this,
						AppLockService.class);
				appLockServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				this.startService(appLockServiceIntent);
			}
			showNormalEnter(); // ��ʾ����������棬ֻ����������ɹ������ܽ�����˽����
		}

	}

	@Override
	protected void initViews() {

	}

	/**
	 * ��һ�ν�����˽������������Ҫ����һ����˽����������һ������
	 */
	private void showFirstEnter() {

		View view = View.inflate(this, R.layout.privacy_first_enter, null);
		ivFirstEnterPrevious = (ImageView) view
				.findViewById(R.id.privacy_first_enter_iv_previous);
		btnFirstEnterStart = (Button) view
				.findViewById(R.id.privacy_first_enter_btn_start);
		ivFirstEnterPrevious.setOnClickListener(this);
		btnFirstEnterStart.setOnClickListener(this);
		setContentView(view);

	}

	/**
	 * ���״ν�����˽�������������һ�����õ����룬������˽����ҳ��
	 */
	private void showNormalEnter() {

		View view = View.inflate(this, R.layout.privacy_start, null);
		ivStartPrivacyPrevious = (ImageView) view
				.findViewById(R.id.privacy_start_iv_previous);
		etStartPrivacyPassword = (EditText) view
				.findViewById(R.id.privacy_start_et_password);
		btnStartPrivacy = (Button) view.findViewById(R.id.b_start_privacy);
		tvStartPrivacyForgetpwd = (TextView) view
				.findViewById(R.id.privacy_start_tv_forgetpwd); // ��������
		tvStartPrivacyForgetpwd
				.setText(Html.fromHtml("<u>" + "��������?" + "</u>")); // Ϊ�������������»���
		tvStartPrivacyForgetpwd.setOnClickListener(this); // Ϊ��������ע�������
		ivStartPrivacyPrevious.setOnClickListener(this);
		btnStartPrivacy.setOnClickListener(this);
		setContentView(view);
	}

	/**
	 * ��ʾ�޸��������
	 */
	private void showAlterPassword() {
		View alterView = View.inflate(this, R.layout.privacy_alter_pwd, null);
		ivAlterpwdPrevious = (ImageView) alterView
				.findViewById(R.id.alter_privacy_pwd_iv_previous);
		ivAlterpwdPrevious.setOnClickListener(this);
		etAlterPassword = (EditText) alterView
				.findViewById(R.id.alter_privacy_pwd_et);
		etAlterPasswordConfirm = (EditText) alterView
				.findViewById(R.id.alter_privacy_pwd_et_confirm);
		btnAlterFinish = (Button) alterView
				.findViewById(R.id.alter_privacy_btn_finish);
		btnAlterFinish.setOnClickListener(this);
		setContentView(alterView);
	}

	/**
	 * ��ʾ�����������
	 */
	private void showResetPassword() {
		View resetView = View.inflate(this, R.layout.privacy_reset_pwd, null);
		etResetPwdQuestion = (EditText) resetView
				.findViewById(R.id.et_reset_privacy_password_question);
		String question = sp.getString("privacy_question", "");
		etResetPwdQuestion.setText(question); // ����֮ǰѡ�������
		etResetPwdAnswer = (EditText) resetView
				.findViewById(R.id.et_reset_privacy_password_answer);
		btnResetNext = (Button) resetView
				.findViewById(R.id.b_reset_privacy_next);
		btnResetNext.setOnClickListener(this);
		ivResetPwdPrevious = (ImageView) resetView
				.findViewById(R.id.iv_reset_privacy_password_previous);
		ivResetPwdPrevious.setOnClickListener(this);
		setContentView(resetView);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.privacy_first_enter_iv_previous: // ��һ�ν�����˽�����ķ��ذ�ť

			Intent previousIntent = new Intent(EnterPrivacyActivity.this,
					MainActivity.class);
			startActivity(previousIntent);
			finish();

			break;

		case R.id.privacy_first_enter_btn_start:// ��һ�ν�����˽�����Ŀ�����ť

			Intent setupPrivacyIntent = new Intent(EnterPrivacyActivity.this,
					SetupPrivacyPwdActivity.class);
			startActivity(setupPrivacyIntent);
			finish();

			break;
		case R.id.iv_reset_privacy_password_previous: // ���������е�ǰһ��
			Intent previousIntent1 = new Intent(EnterPrivacyActivity.this,
					MainActivity.class);
			startActivity(previousIntent1);
			finish();
			break;

		case R.id.alter_privacy_pwd_iv_previous:
			Intent previousIntent2 = new Intent(EnterPrivacyActivity.this,
					MainActivity.class);
			startActivity(previousIntent2);
			finish();
			break;

		case R.id.privacy_start_iv_previous:
			Intent previousIntent4 = new Intent(EnterPrivacyActivity.this,
					MainActivity.class);
			startActivity(previousIntent4);
			finish();
			break;

		case R.id.b_start_privacy:

			String realPassword = sp.getString("privacy_password", null);
			String password = etStartPrivacyPassword.getText().toString();

			if (password.equals("")) {
				Toast.makeText(getApplicationContext(), "���벻��Ϊ��", 0).show();
			} else if (MD5Encoder.encode(password).equals(realPassword)) {

				Intent privacyIntent = new Intent(EnterPrivacyActivity.this,
						PrivacyActivity.class);
				startActivity(privacyIntent);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "�������", 0).show();
			}

			break;

		// ���״ν�����˽���������е���������
		case R.id.privacy_start_tv_forgetpwd:

			showResetPassword();

			break;

		case R.id.b_reset_privacy_next:

			String realAnswer = sp.getString("privacy_answer", ""); // �õ���ʵ�Ĵ�
			String answer = etResetPwdAnswer.getText().toString(); // �õ��û�����Ĵ�

			if (answer.equals("")) {
				Toast.makeText(getApplicationContext(), "�𰸲���Ϊ��", 0).show();
			} else if (realAnswer.equals(answer)) {
				showAlterPassword();
			} else {
				Toast.makeText(getApplicationContext(), "�𰸴���!", 0).show();
			}

			break;

		case R.id.alter_privacy_btn_finish:

			String alterPassword = etAlterPassword.getText().toString().trim();
			String passwordConfirm = etAlterPasswordConfirm.getText()
					.toString().trim();

			if ("".equals(alterPassword) || "".equals(passwordConfirm)) { // ����Ϊ�յ����
				Toast.makeText(getApplicationContext(), "���벻��Ϊ��", 0).show();
				return;
			} else {
				if (alterPassword.equals(passwordConfirm)) { // ��������������ͬ���Ͱ�
																// �������sharedpreference
					Editor editor = sp.edit();
					editor.putString("privacy_password",
							MD5Encoder.encode(alterPassword)); // �����������Ҫ����һ��
					editor.commit();
					Intent privacyIntent = new Intent(
							EnterPrivacyActivity.this, PrivacyActivity.class);
					startActivity(privacyIntent);
					Toast.makeText(getApplicationContext(),
							"�ɹ��޸����룬������˽������	������˽��������Ϊ " + alterPassword,
							Toast.LENGTH_LONG).show();
					finish();
				} else {
					Toast.makeText(getApplicationContext(), "�������벻һ��", 0)
							.show();
					return;
				}
			}
			break;
		}

	}

}
