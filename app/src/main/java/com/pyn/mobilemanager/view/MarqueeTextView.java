package com.pyn.mobilemanager.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/**
 * 实现了跑马灯的TextView
 */
public class MarqueeTextView extends TextView {
	public MarqueeTextView(Context context) {
		super(context);
	}

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return true;
	}

}
