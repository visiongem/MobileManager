package com.pyn.mobilemanager.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

/**
 * 主界面中popwindons的设置
 */
public class MainPopMenu {
	public static PopupWindow getPopupWindow(View view) {
		final PopupWindow pop = new PopupWindow(view,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setOutsideTouchable(true);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();
			}
		});

		return pop;

	}
}
