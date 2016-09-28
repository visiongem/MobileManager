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
 * 继承一个TextView，因为渐变的平移需要view的宽度
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
	 * 在onSizeChanged里将各个需要的元素初始化，
	 * 在linearGradient里定义了渐变的颜色和颜色的变化位置，然后设置给绘图使用的paint
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mViewWidth == 0) {
			mViewWidth = getMeasuredWidth();
			if (mViewWidth > 0) {
				mPaint = getPaint();
				// 创建LinearGradient并设置渐变颜色数组
				// 第一个,第二个参数表示渐变起点 可以设置起点终点在对角等任意位置
				// 第三个,第四个参数表示渐变终点
				// 第五个参数表示渐变颜色
				// 第六个参数可以为空,表示坐标,值为0-1 new float[] {0.25f, 0.5f, 0.75f, 1 }
				// 如果这是空的，颜色均匀分布，沿梯度线。
				// 第七个表示平铺方式
				// CLAMP重复最后一个颜色至最后
				// MIRROR重复着色的图像水平或垂直方向已镜像方式填充会有翻转效果
				// REPEAT重复着色的图像水平或垂直方向
				mLinearGradient = new LinearGradient(-mViewWidth, 0, 0, 0,
						new int[] { 0x77FFFFFF, 0xffFFFFFF, 0x77FFFFFF },
						new float[] { 0, 0.5f, 1 }, Shader.TileMode.CLAMP);
				mPaint.setShader(mLinearGradient);
				mGradientMatrix = new Matrix();
			}
		}
	}

	/**
	 * onDraw()方法里控制偏移量的计算 这里每次移动宽度的1/10 postInvalidateDelay()里设置每一帧绘制的时间
	 * 也就是控制闪动的快慢
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
