package com.matteo.matteolocationhistory.component;

import com.matteo.matteolocationhistory.R;
import com.matteo.matteolocationhistory.model.Fun;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

public class LockScreenView extends View {
	private static int LockColor=Color.argb(50, 0, 0, 0);
	public boolean Locked=false;
	public ProgressBar pbWait;
	
	public LockScreenView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.pbWait=new ProgressBar(context);
		//this.pbWait.set
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(!this.Locked)return;
		canvas.drawColor(LockScreenView.LockColor);
	}
	
	public void Lock(){
		this.Locked=true;
		this.invalidate();
	}
	
	public void Unlock(){
		this.Locked=false;
		this.invalidate();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Matteo.MtLog("Touch!");
		
		return this.Locked;
	}
}
