package com.matteo.matteolocationhistory;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MaskSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback, Runnable {

	// 定义SurfaceHolder对象
	private SurfaceHolder mSurfaceHolder;
	// 循环标记
	private boolean loop = true;
	// 循环间隔
	private static final long TIME = 300;
	// 计数器
	private int mCount;
	// 绘制方式
	private int mode;

	public MaskSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mSurfaceHolder = getHolder(); // 获取SurfaceHolder
		mSurfaceHolder.addCallback(this); // 添加回调
		mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT); // 设置透明
	}

	// 在surface创建时激发
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mode = new Random().nextInt(2); // 随机一个[0-2)数
		new Thread(this).start(); // 开始绘制
	}

	// 在surface的大小发生改变时激发
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	// 在surface销毁时激发
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		loop = false;
	}

	@Override
	public void run() {
		while (loop) {
			try {
				Thread.sleep(TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (mSurfaceHolder) {
				drawMask();
			}
		}
	}

	/**
	 * 绘制蒙版
	 */
	private void drawMask() {
		// 锁定画布，得到canvas
		Canvas mCanvas = mSurfaceHolder.lockCanvas();

		// 避免surface销毁后，线程唤醒仍进入绘制
		if (mSurfaceHolder == null || mCanvas == null)
			return;

		int w = mCanvas.getWidth();
		int h = mCanvas.getHeight();

		/* 创建一个画笔 */
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true); // 设置抗锯齿
		mPaint.setColor(0x00000000); // 设置透明黑色
		mCanvas.drawRect(0, 0, w, h, mPaint); // 重绘背景

		setPaintColor(mPaint); // 循环设置画笔颜色
		mPaint.setStyle(Paint.Style.STROKE); // 描边

		if (0 == mode) {
			drawHeart2D(mCanvas, mPaint, w / 2, h / 2, h / 2); // 画一个2d爱心
		} else {
			drawHeart3D(mCanvas, mPaint); // 画一个3d爱心
		}

		// 绘制后解锁，绘制后必须解锁才能显示
		mSurfaceHolder.unlockCanvasAndPost(mCanvas);
	}

	/** 画一个2d爱心（半圆+sin曲线） */
	private void drawHeart2D(Canvas mCanvas, Paint mPaint, int centerX,
			int centerY, float height) {

		float r = height / 4;
		/* 心两半圆结点处 */
		float topX = centerX;
		float topY = centerY - r;

		/* 左上半圆 */
		RectF leftOval = new RectF(topX - 2 * r, topY - r, topX, topY + r);
		mCanvas.drawArc(leftOval, 180f, 180f, false, mPaint);
		/* 右上半圆 */
		RectF rightOval = new RectF(topX, topY - r, topX + 2 * r, topY + r);
		mCanvas.drawArc(rightOval, 180f, 180f, false, mPaint);

		/* 下半两sin曲线 */
		float base = 3 * r;
		double argu = Math.PI / 2 / base;
		float y = base, value;
		while (y >= 0) {
			value = (float) (2 * r * Math.sin(argu * (base - y)));
			mCanvas.drawPoint(topX - value, topY + y, mPaint);
			mCanvas.drawPoint(topX + value, topY + y, mPaint);
			y -= 1;
		}

		// 1）心形函数：x²+(y-³√x²)²=1
		// >> x^2+(y-(x^2)^(1/3))^2=1
		//
		// 2）心形的各种画法：
		// >> http://woshao.com/article/1a855728bda511e0b40e000c29fa3b3a/
		//
		// 3）笛卡尔情书的秘密——心形函数的绘制
		// >> http://www.cssass.com/blog/index.php/2010/808.html
	}

	/** 画一个3d爱心 */
	private void drawHeart3D(Canvas mCanvas, Paint mPaint) {

		int w = mCanvas.getWidth();
		int h = mCanvas.getHeight();

		/* 画一个3d爱心 */
		int i, j;
		double x, y, r;
		for (i = 0; i <= 90; i++) {
			for (j = 0; j <= 90; j++) {
				r = Math.PI / 45 * i * (1 - Math.sin(Math.PI / 45 * j)) * 20;
				x = r * Math.cos(Math.PI / 45 * j) * Math.sin(Math.PI / 45 * i)
						+ w / 2;
				y = -r * Math.sin(Math.PI / 45 * j) + h / 4;
				mCanvas.drawPoint((float) x, (float) y, mPaint);
			}
		}
	}

	/** 循环设置画笔颜色 */
	private void setPaintColor(Paint mPaint) {
		mCount = mCount < 100 ? mCount + 1 : 0;
		switch (mCount % 6) {
		case 0:
			mPaint.setColor(Color.BLUE);
			break;
		case 1:
			mPaint.setColor(Color.GREEN);
			break;
		case 2:
			mPaint.setColor(Color.RED);
			break;
		case 3:
			mPaint.setColor(Color.YELLOW);
			break;
		case 4:
			mPaint.setColor(Color.argb(255, 255, 181, 216));
			break;
		case 5:
			mPaint.setColor(Color.argb(255, 0, 255, 255));
			break;
		default:
			mPaint.setColor(Color.WHITE);
			break;
		}
	}

}