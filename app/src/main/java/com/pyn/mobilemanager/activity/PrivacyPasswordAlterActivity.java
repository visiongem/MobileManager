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
 * 隐私保护密码修改
 */
public class PrivacyPasswordAlterActivity extends BasicActivity implements
		OnClickListener {

	private ImageView ivPrevious;
	private EditText etAlterPwd;
	private EditText etAlterPwdConfirm;
	private Button btnAlterFinish;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.privacy_alter_pwd);

		sp = getSharedPreferences("config", Context.MODE_PRIVATE);

		initViews();
	}

	@Override
	protected void initViews() {
		ivPrevious = (ImageView) findViewById(R.id.alter_privacy_pwd_iv_previous);
		ivPrevious.setOnClickListener(this);

		etAlterPwd = (EditText) findViewById(R.id.alter_privacy_pwd_et);
		etAlterPwdConfirm = (EditText) findViewById(R.id.alter_privacy_pwd_et_confirm);

		btnAlterFinish = (Button) findViewById(R.id.alter_privacy_btn_finish);
		btnAlterFinish.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.alter_privacy_pwd_iv_previous:

				Intent previousIntent = new Intent(
						PrivacyPasswordAlterActivity.this, PrivacyActivity.class);
				startActivity(previousIntent);
				finish();

				break;

			case R.id.alter_privacy_btn_finish:

				String password = etAlterPwd.getText().toString().trim();
				String passwordConfirm = etAlterPwdConfirm.getText().toString()
						.trim();

				if ("".equals(password) || "".equals(passwordConfirm)) { // 密码为空的情况
					Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				} else {
					if (password.equals(passwordConfirm)) { // 两次密码输入相同，就把
						// 密码存在sharedpreference
						Editor editor1 = sp.edit();
						editor1.putString("privacy_password",
								MD5Encoder.encode(password)); // 这里存密码需要加密一下
						editor1.commit();
						Toast.makeText(getApplicationContext(), "修改密码成功", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						Toast.makeText(getApplicationContext(), "两次密码不一致", Toast.LENGTH_SHORT)
								.show();
						return;
					}
				}
				break;
		}
	}
}
