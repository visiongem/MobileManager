package com.pyn.mobilemanager.activity;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pyn.mobilemanager.R;
import com.pyn.mobilemanager.util.MainPopMenu;

/**
 * 主界面
 */
public class MainActivity extends BasicActivity implements OnClickListener {

	private final static String TAG = "MainActivity";
	private LinearLayout llSoft;
	private LinearLayout llPrivacy;
	private LinearLayout llFlow;
	private ImageView ivSetting;
	private PopupWindow pop;
	private View popView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initViews();
		initpop();
	}

	protected void initViews() {
		ivSetting = (ImageView) findViewById(R.id.main_iv_settings);
		ivSetting.setOnClickListener(this);
		llSoft = (LinearLayout) findViewById(R.id.main_ll_soft);
		llSoft.setOnClickListener(this);
		llPrivacy = (LinearLayout) findViewById(R.id.main_ll_privacy);
		llPrivacy.setOnClickListener(this);
		llFlow = (LinearLayout) findViewById(R.id.main_ll_flow);
		llFlow.setOnClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AlertDialog.Builder alertbBuilder = new AlertDialog.Builder(this);
			alertbBuilder
					.setTitle("温馨提示")
					.setMessage("你确定要离开？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									int nPid = android.os.Process.myPid(); // 结束这个Activity
									android.os.Process.killProcess(nPid);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).create();

			alertbBuilder.show();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_iv_settings:
			if (pop.isShowing()) {
				pop.dismiss();
			} else {
				pop.showAtLocation(v, Gravity.RIGHT | Gravity.TOP, 20, 60);
			}
			break;
		case R.id.main_ll_soft:

			Intent appManagerIntent = new Intent(this, AppManagerActivity.class);
			startActivity(appManagerIntent);
			break;
		case R.id.main_ll_privacy:
			Intent enterPrivacyIntent = new Intent(this,
					EnterPrivacyActivity.class);
			startActivity(enterPrivacyIntent);
			break;
		case R.id.main_ll_flow:
			Intent flowManagerIntent = new Intent(this,
					FlowManagerActivity.class);
			startActivity(flowManagerIntent);
			break;
		}
	}

	/**
	 * popwindons初始化和内容按钮的点击事件
	 */
	private void initpop() {

		popView = LayoutInflater.from(this).inflate(R.layout.main_pop, null);
		pop = MainPopMenu.getPopupWindow(popView);
		pop.setFocusable(true);
		Bitmap bitmap = null;
		pop.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
		pop.setAnimationStyle(R.style.AnimationPreview);

		TextView tv_share = (TextView) popView
				.findViewById(R.id.tv_pop_main2_share);
		TextView tv_about = (TextView) popView
				.findViewById(R.id.tv_pop_main2_about);

		// 点击了分享，为分享添加点击事件
		tv_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.setType("text/plain"); // 需要指定意图的数据类型
				shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
				shareIntent.putExtra(Intent.EXTRA_TEXT,
						"推荐你使用一个应用程序  哎哟手机小助手 管理您的手机！"); // 发送内容
				shareIntent = Intent.createChooser(shareIntent, "分享");
				startActivity(shareIntent);

				pop.dismiss();
			}
		});

		// 点击了关于
		tv_about.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(MainActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.show();
				Window window = dialog.getWindow();
				window.setLayout(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
				window.setContentView(R.layout.about);
				Button b_about_sure = (Button) window
						.findViewById(R.id.btn_about_sure);
				b_about_sure.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				pop.dismiss();
			}
		});

	}

}
