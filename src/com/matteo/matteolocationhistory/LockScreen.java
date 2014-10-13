package com.matteo.matteolocationhistory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class LockScreen extends View {
	public LayoutParams lpView;
	public LayoutParams lpPbWait;
	ProgressBar pbWait;
	public static final int FlagLockEnd=0x122;
	public LockScreen(Context context) {
		super(context);
		this.lpView = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		this.pbWait=new ProgressBar(context);
		this.lpPbWait=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		this.lpPbWait.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
	}

	@SuppressLint({ "NewApi", "DrawAllocation" })
	@Override
	protected void onDraw(Canvas canvas) {
		Path p=new Path();
		Paint pt=new Paint();
		p.moveTo(0, 0);
		p.lineTo(0, Matteo.ScreenWidth);
		p.lineTo(Matteo.ScreenHeight, Matteo.ScreenWidth);
		//p.lineTo(Matteo.ScreenHeight, 0);
		p.lineTo(0, 0);
		pt.setColor(Color.BLACK);
		pt.setStrokeWidth(0);
		pt.setStyle(Paint.Style.FILL);
		pt.setAlpha(100);
		canvas.drawPath(p, pt);
		//this.setAlpha(50);
		//this.setBackgroundColor(Color.BLACK);
		//this.getBackground().setAlpha(50);
	}
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public static void Lock(final LockActivity la){
		final LockScreen ls=new LockScreen(la);
		la.AddView(ls);
		/*la.Do();
				la.lyActivity.removeView(ls.pbWait);
				la.lyActivity.removeView(ls);*/
		LockScreanThread lst=new LockScreanThread(la,ls);
		lst.start();
		//Matteo.WaitThreadFinish(lst);
	}
}
