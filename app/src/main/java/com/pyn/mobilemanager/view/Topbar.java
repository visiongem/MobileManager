package com.pyn.mobilemanager.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pyn.mobilemanager.R;

/**
 * �Զ����϶˵������ؼ�
 */
@SuppressLint("NewApi")
public class Topbar extends RelativeLayout {

	private Button leftBtn, rightBtn;
	private TextView tvTitle;

	/****** ��߿ؼ�    ******/
	private int leftTextColor;
	private Drawable leftBackground;
	private String leftText;
	/****** �ұ߿ؼ�    ******/
	private int rightTextColor;
	private Drawable rightBackground;
	private String rightText;
	/****** �м����    ******/
	private float titleTextSize;
	private int titleTextColor;
	private String title;

	private Context mContext;

	private LayoutParams leftParams, rightParams, titleParams;

	private TopbarClickListener listener;

	/**
	 * ���ⲿ�ṩ�Ľӿڵ���
	 */
	public interface TopbarClickListener {
		public void leftClick();

		public void rightClick();
	}

	public void setOnTopbarClickListener(TopbarClickListener listener) {
		this.listener = listener;
	}

	@SuppressLint("NewApi")
	public Topbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		
		getAttrs(attrs);

		initView();
		
	}
	
	/**
	 * ��ʼ������
	 */
	private void initView() {
		leftBtn = new Button(mContext);
		rightBtn = new Button(mContext);
		tvTitle = new TextView(mContext);

		leftBtn.setTextColor(leftTextColor);
		leftBtn.setBackground(leftBackground);
		leftBtn.setText(leftText);

		rightBtn.setTextColor(rightTextColor);
		rightBtn.setBackground(rightBackground);
		rightBtn.setText(rightText);

		tvTitle.setTextColor(titleTextColor);
		tvTitle.setTextSize(titleTextSize);
		tvTitle.setText(title);
		tvTitle.setGravity(Gravity.CENTER);

		setBackgroundColor(0xFF009688);

		leftParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);

		addView(leftBtn, leftParams);

		rightParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);

		addView(rightBtn, rightParams);

		titleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		titleParams.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);

		addView(tvTitle, titleParams);

		leftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.leftClick();
			}
		});

		rightBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.rightClick();
			}
		});
		
	}

	public void setLeftVisible(boolean flag){
		if(flag){
			leftBtn.setVisibility(View.VISIBLE);
		}else{
			leftBtn.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setRightVisible(boolean flag){
		if(flag){
			rightBtn.setVisibility(View.VISIBLE);
		}else{
			rightBtn.setVisibility(View.INVISIBLE);
		}
	}
	
	/** 
	 * �õ��Զ��������
	 */
	private void getAttrs(AttributeSet attrs){
		
		TypedArray ta = mContext.obtainStyledAttributes(attrs,
				R.styleable.Topbar);

		leftTextColor = ta.getColor(R.styleable.Topbar_leftTextColor, 0);
		leftBackground = ta.getDrawable(R.styleable.Topbar_leftBackground);
		leftText = ta.getString(R.styleable.Topbar_leftText);

		rightTextColor = ta.getColor(R.styleable.Topbar_rightTextColor, 0);
		rightBackground = ta.getDrawable(R.styleable.Topbar_rightBackground);
		rightText = ta.getString(R.styleable.Topbar_rightText);

		titleTextSize = ta.getDimension(R.styleable.Topbar_titleTextSize, 0);
		titleTextColor = ta.getColor(R.styleable.Topbar_titleTextColor, 0);
		title = ta.getString(R.styleable.Topbar_title);

		ta.recycle();
	}
}
