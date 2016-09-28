package com.pyn.mobilemanager.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * �̳�һ��TextView����Ϊ�����ƽ����Ҫview�Ŀ��
 */
public class MyAppLockTextView extends TextView {

	private LinearGradient mLinearGradient;
	private Matrix mGradientMatrix;
	private Paint mPaint;
	private int mViewWidth = 0;
	private int mTranslate = 0;

	private boolean mAnimating = true;

	public MyAppLockTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * ��onSizeChanged�ｫ������Ҫ��Ԫ�س�ʼ����
	 * ��linearGradient�ﶨ���˽������ɫ����ɫ�ı仯λ�ã�Ȼ�����ø���ͼʹ�õ�paint
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mViewWidth == 0) {
			mViewWidth = getMeasuredWidth();
			if (mViewWidth > 0) {
				mPaint = getPaint();
				// ����LinearGradient�����ý�����ɫ����
				// ��һ��,�ڶ���������ʾ������� ������������յ��ڶԽǵ�����λ��
				// ������,���ĸ�������ʾ�����յ�
				// �����������ʾ������ɫ
				// ��������������Ϊ��,��ʾ����,ֵΪ0-1 new float[] {0.25f, 0.5f, 0.75f, 1 }
				// ������ǿյģ���ɫ���ȷֲ������ݶ��ߡ�
				// ���߸���ʾƽ�̷�ʽ
				// CLAMP�ظ����һ����ɫ�����
				// MIRROR�ظ���ɫ��ͼ��ˮƽ��ֱ�����Ѿ���ʽ�����з�תЧ��
				// REPEAT�ظ���ɫ��ͼ��ˮƽ��ֱ����
				mLinearGradient = new LinearGradient(-mViewWidth, 0, 0, 0,
						new int[] { 0x77FFFFFF, 0xffFFFFFF, 0x77FFFFFF },
						new float[] { 0, 0.5f, 1 }, Shader.TileMode.CLAMP);
				mPaint.setShader(mLinearGradient);
				mGradientMatrix = new Matrix();
			}
		}
	}

	/**
	 * onDraw()���������ƫ�����ļ��� ����ÿ���ƶ���ȵ�1/10 postInvalidateDelay()������ÿһ֡���Ƶ�ʱ��
	 * Ҳ���ǿ��������Ŀ���
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mAnimating && mGradientMatrix != null) {
			mTranslate += mViewWidth / 10;
			if (mTranslate > 2 * mViewWidth) {
				mTranslate = -mViewWidth;
			}
			mGradientMatrix.setTranslate(mTranslate, 0);
			mLinearGradient.setLocalMatrix(mGradientMatrix);
			postInvalidateDelayed(50);
		}
	}

}
