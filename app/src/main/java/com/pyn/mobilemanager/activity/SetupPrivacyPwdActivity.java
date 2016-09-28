package com.pyn.mobilemanager.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.util.MD5Encoder;

/**
 * 设置隐私保护密码
 */
public class SetupPrivacyPwdActivity extends BasicActivity implements
		OnClickListener {

	private ImageView ivPrevious;
	private EditText etPwd, etPwdConfirm;
	private Button btnNext;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.privacy_setup_password);

		sp = getSharedPreferences("config", Context.MODE_PRIVATE); // 得到SharedPreferences

		initViews();

	}

	@Override
	protected void initViews() {
		ivPrevious = (ImageView) findViewById(R.id.privacy_setup_pwd_iv_previous);
		ivPrevious.setOnClickListener(this);
		etPwd = (EditText) findViewById(R.id.privacy_setup_et_pwd);
		etPwdConfirm = (EditText) findViewById(R.id.privacy_setup_et_pwd_confirm);
		btnNext = (Button) findViewById(R.id.privacy_setup_btn_next);
		btnNext.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.privacy_setup_pwd_iv_previous:
			Intent previousIntent = new Intent(SetupPrivacyPwdActivity.this,
					EnterPrivacyActivity.class);
			startActivity(previousIntent);
			finish();

			break;

		case R.id.privacy_setup_btn_next:
			String pwd = etPwd.getText().toString().trim();
			String pwdConfirm = etPwdConfirm.getText().toString().trim();

			if ("".equals(pwd) || "".equals(pwdConfirm)) { // 密码为空的情况
				Toast.makeText(getApplicationContext(), "密码不能为空", 0).show();
				return;
			} else {
				if (pwd.equals(pwdConfirm)) { // 两次密码输入相同，就把
												// 密码存在sharedpreference
					Editor editor = sp.edit();
					editor.putString("privacy_password", MD5Encoder.encode(pwd)); // 这里存密码需要加密一下
					editor.commit();
					Intent questionIntent = new Intent(
							SetupPrivacyPwdActivity.this,
							SetupPrivacyQuestionActivity.class);
					startActivity(questionIntent);
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
