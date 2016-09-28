package com.pyn.mobilemanager.view;

import com.pyn.mobilemanager.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 联系人字母表
 */
public class MyContactListView extends View {

	/*
	 * 触摸事件
	 */
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	/*
	 * 26个字母
	 */
	private String[] b = { "#", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };
	private int choose = -1;
	private Paint paint = new Paint();
	private boolean showBkg = false;

	public MyContactListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyContactListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyContactListView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (showBkg) {
			canvas.drawColor(Color.parseColor("#40000000"));
		}
		// 获取对应高度
		int height = getHeight();
		// 获取对应宽度
		int width = getWidth();
		// 获取每一个字母的高度
		int singleHeight = height / b.length;
		for (int i = 0; i < b.length; i++) {
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			paint.setTextSize(25);
			// 选中的状态
			if (i == choose) {
				// 设置被选中的字母变大的颜色
				paint.setColor(Color.parseColor("#3399ff"));
				paint.setFakeBoldText(true);
			}
			// x坐标等于中间-字符串宽度的一半
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(b[i], xPos, yPos, paint);
			// 重置画笔
			paint.reset();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		// 点击y坐标
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
		final int c = (int) (y / getHeight() * b.length);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			// 设置右侧字母列表[A,B,C,D,E....]的背景颜色
			setBackgroundResource(R.drawable.contact_list_bg);
			if (oldChoose != c && listener != null) {
				if (c > 0 && c < b.length) {
					listener.onTouchingLetterChanged(b[c]);
					choose = c;
					invalidate();
				}
			}

			break;

		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c && listener != null) {
				if (c > 0 && c < b.length) {
					listener.onTouchingLetterChanged(b[c]);
					choose = c;
					invalidate();
				}
			}
			break;

		case MotionEvent.ACTION_UP:
			setBackgroundDrawable(new ColorDrawable(0x00000000));
			showBkg = false;
			choose = -1;
			invalidate();
			break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	/**
	 * 向外公开的方法
	 * 
	 * @param listener
	 */
	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener listener) {
		this.onTouchingLetterChangedListener = listener;
	}

	/**
	 * 接口
	 */
	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}
