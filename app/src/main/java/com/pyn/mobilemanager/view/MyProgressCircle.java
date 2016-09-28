package com.pyn.mobilemanager.view;


import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.pyn.mobilemanager.R;

/**
 * �Զ�����ͼ�ؼ��ࡣ����Բ�ν�����
 * @author MrRight
 *
 */
public class MyProgressCircle extends View{

	
	 private  static final int DEFAULT_MAX_VALUE = 100;					// Ĭ�Ͻ��������ֵ
	 private  static final int DEFAULT_PAINT_WIDTH = 10;				// Ĭ�ϻ��ʿ��
	 private  static final int DEFAULT_PAINT_COLOR = 0xff25BFA0;		// Ĭ�ϻ�����ɫ
	 private  static final boolean DEFAULT_FILL_MODE = true;			// Ĭ�����ģʽ
	 private  static final int DEFAULT_INSIDE_VALUE = 0;				// Ĭ����������
	
	 private CircleAttribute mCircleAttribute;			// Բ�ν�������������

	 private int mMaxProgress;							// ���������ֵ	 	 																												
	 private int mMainCurProgress;						// ����������ǰֵ 
	 private int mSubCurProgress;						// �ӽ�������ǰֵ 
	 private int progress;
	 
	 private CartoomEngine mCartoomEngine;				// ��������
	
	 private Drawable mBackgroundPicture;				// ����ͼ
	 
	 public MyProgressCircle(Context context)
	 {
			super(context);		
			defaultParam();
	}

	public MyProgressCircle(Context context, AttributeSet attrs) {
		super(context, attrs);

		defaultParam();
		
		
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);      
	        
	    mMaxProgress = array.getInteger(R.styleable.CircleProgressBar_max, DEFAULT_MAX_VALUE); 				// ��ȡ���������ֵ	
	    
	    boolean bFill = array.getBoolean(R.styleable.CircleProgressBar_fill, DEFAULT_FILL_MODE);			// ��ȡ���ģʽ
	    int paintWidth = array.getInt(R.styleable.CircleProgressBar_Paint_Width, DEFAULT_PAINT_WIDTH);		// ��ȡ���ʿ��	    
        mCircleAttribute.setFill(bFill);
        if (bFill == false)
        {
        	mCircleAttribute.setPaintWidth(paintWidth);
        }
        
        int paintColor = array.getColor(R.styleable.CircleProgressBar_Paint_Color, DEFAULT_PAINT_COLOR);	 // ��ȡ������ɫ

        mCircleAttribute.setPaintColor(paintColor);
        
	    mCircleAttribute.mSidePaintInterval = array.getInt(R.styleable.CircleProgressBar_Inside_Interval, DEFAULT_INSIDE_VALUE);// Բ����������

	    array.recycle(); //һ��Ҫ���ã������������
		
	}

	/*
	 * Ĭ�ϲ���
	 */
	private void defaultParam()
	{
		mCircleAttribute = new CircleAttribute();
		 
		mCartoomEngine = new CartoomEngine();
		
		mMaxProgress = DEFAULT_MAX_VALUE;								 																												
		mMainCurProgress = 0;						
	    mSubCurProgress = 0;					
		 
	}
	 
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {		// ������ͼ��С
		
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		mBackgroundPicture = getBackground();
		if (mBackgroundPicture != null)
		{
			width = mBackgroundPicture.getMinimumWidth();
			height = mBackgroundPicture.getMinimumHeight();
		}
		
		setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(width, heightMeasureSpec));
	}
	 
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		mCircleAttribute.autoFix(w, h);

	}
	
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		       	
		if (mBackgroundPicture == null)									// û����ͼ�Ļ��ͻ��Ƶ�ɫ
		{
			canvas.drawArc(mCircleAttribute.mRoundOval, 0, 360, mCircleAttribute.mBRoundPaintsFill, mCircleAttribute.mBottomPaint);
		}
		
		
		float subRate = (float)mSubCurProgress / mMaxProgress;
		float subSweep = 360 * subRate;
		canvas.drawArc(mCircleAttribute.mRoundOval, mCircleAttribute.mDrawPos, subSweep, mCircleAttribute.mBRoundPaintsFill, mCircleAttribute.mSubPaint);
		
		float rate = (float)mMainCurProgress / mMaxProgress;
		float sweep = 360 * rate;
		canvas.drawArc(mCircleAttribute.mRoundOval, mCircleAttribute.mDrawPos, sweep, mCircleAttribute.mBRoundPaintsFill, mCircleAttribute.mMainPaints);
	
	
	}
	
	
	/*
	 * ����������ֵ
	 */
	 public synchronized void setMainProgress (int progress)
	 {
	    	mMainCurProgress = progress;
	    	if (mMainCurProgress < 0)
	    	{
	    		mMainCurProgress = 0;
	    	}
	    	
	    	if (mMainCurProgress > mMaxProgress)
	    	{
	    		mMainCurProgress = mMaxProgress;
	    	}
	    	
	    	invalidate();
	}
	    
    public synchronized int getMainProgress()
    {
    	return mMainCurProgress;
    }
	   
    /*
     *  �����ӽ���ֵ
     */
    public synchronized void setSubProgress (int progress)
    {
    	mSubCurProgress = progress;
    	if (mSubCurProgress < 0)
    	{
    		mSubCurProgress = 0;
    	}
    	
    	if (mSubCurProgress > mMaxProgress)
    	{
    		mSubCurProgress = mMaxProgress;
    	}
    	
    	invalidate();
    }
	    
    public synchronized int getSubProgress()
    {
    	return mSubCurProgress;
    }
    
    /*
     * ��������
     */
	public  void  startCartoom(int mainprogress)
	{
		progress=mainprogress;
		mCartoomEngine.startCartoom();
	}
	
	/*
	 * ��������
	 */
	public  void  stopCartoom()
	{
		mCartoomEngine.stopCartoom();
	}

	class CircleAttribute
	{
		 public RectF  	mRoundOval;					// Բ�����ھ�������
		 public boolean mBRoundPaintsFill;			// �Ƿ���������ģʽ����Բ��
		 public int  	mSidePaintInterval;			// Բ�����������ľ���
		 public int    	mPaintWidth;				// Բ�λ��ʿ�ȣ����ģʽ�����ӣ�
		 public int     mPaintColor;				// ������ɫ  ������������������ɫ���ӽ�����������ɫΪ���͸��ֵ�� 
		 public int     mDrawPos;					// ����Բ�ε���㣨Ĭ��Ϊ-90�ȼ�12���ӷ���
		 
		 public Paint   mMainPaints;				// ������������ 	    
		 public Paint   mSubPaint;    				// �ӽ��������� 
		 
		 public Paint   mBottomPaint;				// �ޱ���ͼʱ�������û���

		 
		 public CircleAttribute()
		 {
			mRoundOval = new RectF();
			mBRoundPaintsFill = DEFAULT_FILL_MODE;
			mSidePaintInterval = DEFAULT_INSIDE_VALUE;
			mPaintWidth = 0;
			mPaintColor = DEFAULT_PAINT_COLOR;
			mDrawPos = -90;
			 
			mMainPaints = new Paint();		
			mMainPaints.setAntiAlias(true);
			mMainPaints.setStyle(Paint.Style.FILL);
			mMainPaints.setStrokeWidth(mPaintWidth);
			mMainPaints.setColor(mPaintColor);
		     
			mSubPaint = new Paint();
			mSubPaint.setAntiAlias(true);
			mSubPaint.setStyle(Paint.Style.FILL);
			mSubPaint.setStrokeWidth(mPaintWidth);
			mSubPaint.setColor(mPaintColor);			
				
			mBottomPaint = new Paint();
			mBottomPaint.setAntiAlias(true);
			mBottomPaint.setStyle(Paint.Style.FILL);
			mBottomPaint.setStrokeWidth(mPaintWidth);
			mBottomPaint.setColor(Color.GRAY);
			
		 }
	
		 
		 /*
		  * ���û��ʿ��
		  */
		 public void setPaintWidth(int width)
		 {
			 mMainPaints.setStrokeWidth(width);
			 mSubPaint.setStrokeWidth(width);
			 mBottomPaint.setStrokeWidth(width);
		 }
		 
		 /*
		  * ���û�����ɫ
		  */
		 public void setPaintColor(int color)
		 {
			 mMainPaints.setColor(color);
		     int color1 = color & 0x00ffffff | 0x66000000;
		     mSubPaint.setColor(color1);	        
		 }
		 
		 /*
		  * �������ģʽ
		  */
		 public void setFill(boolean fill)
		 {
			 mBRoundPaintsFill = fill;
			 if (fill)
			 {
				 mMainPaints.setStyle(Paint.Style.FILL);
				 mSubPaint.setStyle(Paint.Style.FILL);	 
				 mBottomPaint.setStyle(Paint.Style.FILL);	
			 }else{
				 mMainPaints.setStyle(Paint.Style.STROKE);
				 mSubPaint.setStyle(Paint.Style.STROKE);	
				 mBottomPaint.setStyle(Paint.Style.STROKE);	
			 }	
		 }
		 
		/*
		 * �Զ�����
		 */
		 public void autoFix(int w, int h)
		 {
			if (mSidePaintInterval != 0)
			{
				mRoundOval.set(mPaintWidth/2 + mSidePaintInterval, mPaintWidth/2 + mSidePaintInterval,
				w - mPaintWidth/2 - mSidePaintInterval, h - mPaintWidth/2 - mSidePaintInterval);	
			}else{

				int sl = getPaddingLeft();
				int sr = getPaddingRight();
				int st = getPaddingTop();
				int sb = getPaddingBottom();
			
				mRoundOval.set(sl + mPaintWidth/2, st + mPaintWidth/2, w - sr - mPaintWidth/2, h - sb - mPaintWidth/2);	
			}	
		 }
	}
	
	class CartoomEngine
	{
		public Handler mHandler; 
		public boolean mBCartoom;					// �Ƿ����������� 
		public Timer   mTimer;						// ������������TIMER 
		public MyTimerTask	mTimerTask;				// ��������
		public int 	 mSaveMax;						// ��������ʱ����ʱ�ı�MAXֵ���ñ������ڱ���ֵ�Ա�ָ�	 
		public int     mTimerInterval;				// ��ʱ���������ʱ��(ms)	 
		public float   mCurFloatProcess;			// ������ʱ��ǰ����ֵ 

		private long timeMil;
		
		public CartoomEngine()
		{
			mHandler = new Handler()
			{
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					switch(msg.what)
					{
						case TIMER_ID:
						{
							if (mBCartoom == false)
							{
								return ;
							}
							
							mCurFloatProcess += 1;
							setMainProgress((int) mCurFloatProcess);
							
							long curtimeMil = System.currentTimeMillis();
									
							
							timeMil = curtimeMil;
							
							if (mCurFloatProcess >= mMaxProgress)
							{
								stopCartoom();
							}else if(mCurFloatProcess==progress){
								stopCartoom();
							}
						}
						break;
					}
				}
				
			};
			
			mBCartoom = false;
			mTimer = new Timer();
			mSaveMax = 0;
			mTimerInterval = 25;
			mCurFloatProcess = 0;
			
		}
		
		public synchronized void  startCartoom()
		{
			if ( mBCartoom == true)
			{
				return ;
			}
			
			timeMil = 0;
			
			mBCartoom = true;

			setMainProgress(0);
			setSubProgress(0);
			
			mSaveMax = mMaxProgress;
			mCurFloatProcess = 0;
		
			
			mTimerTask = new MyTimerTask();
			mTimer.schedule(mTimerTask, mTimerInterval, mTimerInterval);

		}
		
		public synchronized void  stopCartoom()
		{

			if (mBCartoom == false)
			{
				return ;
			}
			
			mBCartoom = false;
			mMaxProgress = mSaveMax;
			
			if (mTimerTask != null)
			{
				mTimerTask.cancel();
				mTimerTask = null;
			}
		}
		
		private final static int TIMER_ID = 0x0010;
		
		class MyTimerTask extends TimerTask{

			@Override
			public void run() {
				Message msg = mHandler.obtainMessage(TIMER_ID);
				msg.sendToTarget();
		
			}
		}
	}
}
