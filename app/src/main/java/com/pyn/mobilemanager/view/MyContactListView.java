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
 * ��ϵ����ĸ��
 */
public class MyContactListView extends View {

	/*
	 * �����¼�
	 */
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	/*
	 * 26����ĸ
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
		// ��ȡ��Ӧ�߶�
		int height = getHeight();
		// ��ȡ��Ӧ���
		int width = getWidth();
		// ��ȡÿһ����ĸ�ĸ߶�
		int singleHeight = height / b.length;
		for (int i = 0; i < b.length; i++) {
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			paint.setTextSize(25);
			// ѡ�е�״̬
			if (i == choose) {
				// ���ñ�ѡ�е���ĸ������ɫ
				paint.setColor(Color.parseColor("#3399ff"));
				paint.setFakeBoldText(true);
			}
			// x��������м�-�ַ�����ȵ�һ��
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(b[i], xPos, yPos, paint);
			// ���û���
			paint.reset();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		// ���y����
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		// ���y������ռ�ܸ߶ȵı���*b����ĳ��Ⱦ͵��ڵ��b�еĸ���.
		final int c = (int) (y / getHeight() * b.length);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			// �����Ҳ���ĸ�б�[A,B,C,D,E....]�ı�����ɫ
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
	 * ���⹫���ķ���
	 * 
	 * @param listener
	 */
	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener listener) {
		this.onTouchingLetterChangedListener = listener;
	}

	/**
	 * �ӿ�
	 */
	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}
