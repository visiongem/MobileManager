package com.pyn.mobilemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pyn.mobilemanager.R;

/**
 * 设置好一切或者输入正确密码进入到隐私保护的activity
 */
public class PrivacyActivity extends BasicActivity implements OnClickListener {
	private LinearLayout llNote;
	private LinearLayout llLock;
	private ImageView ivPrevious;
	private LinearLayout llAlterPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.privacy);

		initViews();

	}

	@Override
	protected void initViews() {
		ivPrevious = (ImageView) findViewById(R.id.privacy_iv_previous);
		llNote = (LinearLayout) findViewById(R.id.privacy_ll_note);
		llLock = (LinearLayout) findViewById(R.id.privacy_ll_lock);
		llAlterPwd = (LinearLayout) findViewById(R.id.privacy_ll_alter_pwd);

		llNote.setOnClickListener(this);
		llLock.setOnClickListener(this);
		ivPrevious.setOnClickListener(this);
		llAlterPwd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.privacy_iv_previous: // 点击了前一步

				Intent previousIntent = new Intent(PrivacyActivity.this,
						MainActivity.class);
				startActivity(previousIntent);
				finish();

				break;

			case R.id.privacy_ll_lock: // 点击了程序锁

				Intent lockIntent = new Intent(PrivacyActivity.this,
						AppLockActivity.class);
				startActivity(lockIntent);

				break;

			case R.id.privacy_ll_note: // 点击了隐私通讯录

				Intent noteIntent = new Intent(PrivacyActivity.this,
						PrivacyNoteActivity.class);
				startActivity(noteIntent);

				break;

			case R.id.privacy_ll_alter_pwd: // 点击了修改密码

				Intent alterPwdIntent = new Intent(PrivacyActivity.this,
						PrivacyPasswordAlterActivity.class);
				startActivity(alterPwdIntent);

				break;
		}

	}

}
