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
 * 进入隐私保护根据不同的设置情况，加载不同的界面
 */
public class EnterPrivacyActivity extends BasicActivity implements
		OnClickListener {

	private SharedPreferences sp;
	private boolean isFirstEnterPrivacy; // 判断是否第一次进入隐私保护

	/***** 第一次进入隐私保护界面的组件 *****/
	private ImageView ivFirstEnterPrevious;
	private Button btnFirstEnterStart;

	/***** 非第一次进入隐私保护界面的组件 *****/
	private ImageView ivStartPrivacyPrevious;
	private EditText etStartPrivacyPassword;
	private Button btnStartPrivacy;
	private TextView tvStartPrivacyForgetpwd;

	/***** 重置密码界面的组件 *****/
	private EditText etResetPwdQuestion;
	private EditText etResetPwdAnswer;
	private Button btnResetNext;
	private ImageView ivResetPwdPrevious;

	/***** 修改密码界面的组件 *****/
	private ImageView ivAlterpwdPrevious;
	private EditText etAlterPassword, etAlterPasswordConfirm;
	private Button btnAlterFinish;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sp = getSharedPreferences("config", Context.MODE_PRIVATE); // 得到SharedPreferences
		isFirstEnterPrivacy = sp.getBoolean("isFirstEnterPrivacy", true); // 默认为第一次进入隐私保护

		// 判断用户是否第一次进入隐私保护
		if (isFirstEnterPrivacy) {
			showFirstEnter(); // 显示第一次进入隐私保护界面，开启隐私保护
		} else {
			ServiceUtil serviceUtil = new ServiceUtil(this);
			if (!serviceUtil
					.isWorked("com.pyn.mobilemanager.service.AppLockService")) {
				Intent appLockServiceIntent = new Intent(this,
						AppLockService.class);
				appLockServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				this.startService(appLockServiceIntent);
			}
			showNormalEnter(); // 显示输入密码界面，只有输入密码成功，才能进入隐私保护
		}

	}

	@Override
	protected void initViews() {

	}

	/**
	 * 第一次进入隐私保护，首先需要开启一下隐私保护，设置一下密码
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
	 * 非首次进入隐私保护，则输入第一次设置的密码，进入隐私保护页面
	 */
	private void showNormalEnter() {

		View view = View.inflate(this, R.layout.privacy_start, null);
		ivStartPrivacyPrevious = (ImageView) view
				.findViewById(R.id.privacy_start_iv_previous);
		etStartPrivacyPassword = (EditText) view
				.findViewById(R.id.privacy_start_et_password);
		btnStartPrivacy = (Button) view.findViewById(R.id.b_start_privacy);
		tvStartPrivacyForgetpwd = (TextView) view
				.findViewById(R.id.privacy_start_tv_forgetpwd); // 忘记密码
		tvStartPrivacyForgetpwd
				.setText(Html.fromHtml("<u>" + "忘记密码?" + "</u>")); // 为忘记密码设置下划线
		tvStartPrivacyForgetpwd.setOnClickListener(this); // 为忘记密码注册监听器
		ivStartPrivacyPrevious.setOnClickListener(this);
		btnStartPrivacy.setOnClickListener(this);
		setContentView(view);
	}

	/**
	 * 显示修改密码界面
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
	 * 显示重置密码界面
	 */
	private void showResetPassword() {
		View resetView = View.inflate(this, R.layout.privacy_reset_pwd, null);
		etResetPwdQuestion = (EditText) resetView
				.findViewById(R.id.et_reset_privacy_password_question);
		String question = sp.getString("privacy_question", "");
		etResetPwdQuestion.setText(question); // 设置之前选择的问题
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
		case R.id.privacy_first_enter_iv_previous: // 第一次进入隐私保护的返回按钮

			Intent previousIntent = new Intent(EnterPrivacyActivity.this,
					MainActivity.class);
			startActivity(previousIntent);
			finish();

			break;

		case R.id.privacy_first_enter_btn_start:// 第一次进入隐私保护的开启按钮

			Intent setupPrivacyIntent = new Intent(EnterPrivacyActivity.this,
					SetupPrivacyPwdActivity.class);
			startActivity(setupPrivacyIntent);
			finish();

			break;
		case R.id.iv_reset_privacy_password_previous: // 重置密码中的前一步
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
				Toast.makeText(getApplicationContext(), "密码不能为空", 0).show();
			} else if (MD5Encoder.encode(password).equals(realPassword)) {

				Intent privacyIntent = new Intent(EnterPrivacyActivity.this,
						PrivacyActivity.class);
				startActivity(privacyIntent);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "密码错误", 0).show();
			}

			break;

		// 非首次进入隐私保护界面中的忘记密码
		case R.id.privacy_start_tv_forgetpwd:

			showResetPassword();

			break;

		case R.id.b_reset_privacy_next:

			String realAnswer = sp.getString("privacy_answer", ""); // 得到真实的答案
			String answer = etResetPwdAnswer.getText().toString(); // 得到用户输入的答案

			if (answer.equals("")) {
				Toast.makeText(getApplicationContext(), "答案不能为空", 0).show();
			} else if (realAnswer.equals(answer)) {
				showAlterPassword();
			} else {
				Toast.makeText(getApplicationContext(), "答案错误!", 0).show();
			}

			break;

		case R.id.alter_privacy_btn_finish:

			String alterPassword = etAlterPassword.getText().toString().trim();
			String passwordConfirm = etAlterPasswordConfirm.getText()
					.toString().trim();

			if ("".equals(alterPassword) || "".equals(passwordConfirm)) { // 密码为空的情况
				Toast.makeText(getApplicationContext(), "密码不能为空", 0).show();
				return;
			} else {
				if (alterPassword.equals(passwordConfirm)) { // 两次密码输入相同，就把
																// 密码存在sharedpreference
					Editor editor = sp.edit();
					editor.putString("privacy_password",
							MD5Encoder.encode(alterPassword)); // 这里存密码需要加密一下
					editor.commit();
					Intent privacyIntent = new Intent(
							EnterPrivacyActivity.this, PrivacyActivity.class);
					startActivity(privacyIntent);
					Toast.makeText(getApplicationContext(),
							"成功修改密码，进入隐私保护！	现在隐私保护密码为 " + alterPassword,
							Toast.LENGTH_LONG).show();
					finish();
				} else {
					Toast.makeText(getApplicationContext(), "两次密码不一致", 0)
							.show();
					return;
				}
			}
			break;
		}

	}

}
