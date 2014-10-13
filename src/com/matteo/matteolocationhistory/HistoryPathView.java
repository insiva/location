package com.matteo.matteolocationhistory;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.SumPathEffect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class HistoryPathView extends View {
	HistoryActivity actHistory;
	private Paint paint;
	Path pathLocation, pathFill, pathFrame, pathTimeScale, pathTime;
	MatteoHistory h;
	double stepWidth;
	// public static final double DistanceUnit = 50;
	public static final int Height = 200;
	public static final int Width = 800;
	public static final int ViewHeight = 270;
	public static final int ViewWidth = 1000;
	public static final int MarginBottom = 50;
	public static final int MarginTop = 20;
	public static final int MarginRight = 50;
	public static final int ViewMarginRight = 328;
	public static final int DistanceUnitCount = 4;
	public static final int TimeUnitCount = 4;
	public static final float ScaleLength = 4;
	public LayoutParams ViewParams;
	private ArrayList<TextToDraw> TextArray;
	private float LTX, LTY, LDX, LDY, RTX, RTY, RDX, RDY, TimeLeftX,
			TimeRightX;

	public HistoryPathView(Context context) {
		super(context);
		this.actHistory = (HistoryActivity) context;
		//this.h = _h;
		this.ViewParams = new LayoutParams(HistoryPathView.ViewWidth,
				HistoryPathView.ViewHeight);
		this.ViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
				RelativeLayout.TRUE);
		this.ViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
				RelativeLayout.TRUE);
		this.ViewParams.setMargins(0, 0, ViewMarginRight, 0);
		this.h=null;
		//this.StartPath();
	}
	
	public void SetHistory(MatteoHistory _h){
		this.h=_h;
		if(h.size()<=1)return;
		this.StartPath();
		this.invalidate();
	}

	private void StartPath() {
		paint = new Paint();
		this.TextArray = new ArrayList<TextToDraw>();
		this.LTX = ViewWidth - MarginRight - Width;
		this.LTY = MarginTop;
		this.LDX = this.LTX;
		this.LDY = this.LTY + Height;
		this.RTX = this.LTX + Width;
		this.RTY = this.LTY;
		this.RDX = this.RTX;
		this.RDY = this.LDY;
		this.TimeLeftX = this.TimeRightX = 0;
		this.StartPathFrame();
		this.StartTimeScale();
		this.StartPathLocation();
		this.pathTime = null;
	}

	private void StartPathFrame() {
		this.pathFrame = new Path();
		this.pathFrame.moveTo(this.LDX, 0);
		this.pathFrame.lineTo(this.LDX, this.LDY);
		this.pathFrame.lineTo(this.RDX, this.RDY);
		this.pathFrame.lineTo(this.RTX, 0);
		this.pathFrame.lineTo(this.LTX, 0);

		float x = this.LDX, y, YUnit = ((float) (ViewHeight - MarginBottom - MarginTop))
				/ (float) DistanceUnitCount;
		int i = 1;
		int distanceUnit = (int) Math.ceil(h.GetDistanceUnit());
		for (y = this.LDY - YUnit; y >= this.LTY; y -= YUnit) {
			this.pathFrame.moveTo(x, y);
			this.pathFrame.lineTo(x + ScaleLength, y);
			String t = MatteoHistory.GetDistanceText((i++) * distanceUnit);
			TextToDraw ttd = new TextToDraw(t, x - ScaleLength, y, this.paint,
					Align.RIGHT);
			this.TextArray.add(ttd);
		}
	}

	private void StartTimeScale() {
		this.pathTimeScale = new Path();
		long timeUnit = h.GetTimeUnit();
		long startT = h.StartDay.getTime();
		if (timeUnit >= MatteoHistory.DaySpan) {
			startT = startT + MatteoHistory.DaySpan - startT
					% MatteoHistory.DaySpan;
		} else if (timeUnit >= MatteoHistory.HourSpan) {
			startT = startT + MatteoHistory.HourSpan - startT
					% MatteoHistory.HourSpan;
		} else {
			startT = startT + MatteoHistory.MinuteSpan - startT
					% MatteoHistory.MinuteSpan;
		}
		long t = startT;
		float x = this.LDX;
		for (int i = 0; i < TimeUnitCount; i++) {
			t = startT + i * timeUnit;
			x = this.LDX
					+ (float) (((double) (t - h.StartDay.getTime()) / (double) h.TimeSpan) * (double) Width);
			String ts = MatteoHistory.GetTimeScaleText(t, timeUnit);
			TextToDraw ttd = new TextToDraw(ts, x, this.LDY, this.paint,
					Align.CENTER);
			this.TextArray.add(ttd);
			this.pathTimeScale.moveTo(x, 0);
			this.pathTimeScale.lineTo(x, this.LDY);
		}
	}

	private void StartPathLocation() {
		this.stepWidth = (double) Width / (double) h.TimeSpan;
		// 创建、并初始化Path
		pathLocation = new Path();
		pathFill = new Path();
		float startX = this.LDX;
		float startY = this.LDY, x = 0, y = 0;
		float leftx = 0;
		pathLocation.moveTo(startX, startY);
		pathFill.moveTo(startX, startY);
		for (int i = 0; i < h.size(); i++) {
			x = (float) ((float) (h.get(i).CreateTime.getTime() - h.FirstLocation.CreateTime
					.getTime()) * this.stepWidth) + startX;
			y = (float) (startY - (h.get(i).Distance / h.MaxDistance) * Height);
			pathLocation.lineTo(x, y);
			pathFill.lineTo(x, y);
			if (i != 0) {
				leftx = (x + leftx) / 2;
			}
			h.get(i).LeftViewX = leftx;
			h.get(i).ViewX=x;
		}
		pathLocation.lineTo(startX + Width, y);
		pathFill.lineTo(startX + Width, y);
		pathFill.lineTo(startX + Width, startY);
		pathFill.lineTo(startX, startY);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Matteo.MtLog("Draw!");
		if(h==null)return;
		canvas.drawColor(Color.TRANSPARENT);
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		canvas.drawPath(this.pathFrame, paint);
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		canvas.drawPath(pathLocation, paint);
		// pathLocation.close();
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1);
		canvas.drawPath(this.pathTimeScale, paint);
		// pathTimeScale.close();
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(0);
		paint.setStyle(Paint.Style.FILL);
		paint.setAlpha(50);
		canvas.drawPath(pathFill, paint);
		// pathFill.close();
		if (this.pathTime != null) {
			paint.setColor(Color.YELLOW);
			paint.setStrokeWidth(1);
			// paint.setAlpha(80);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawPath(this.pathTime, paint);
			// this.pathTime.close();
		}
		this.DrawText(canvas);
	}

	private void DrawText(Canvas canvas) {
		paint.setColor(Color.GRAY);
		paint.setTextSize(22);
		FontMetrics fontMetrics = paint.getFontMetrics();
		TextToDraw.TextHeight = fontMetrics.bottom - fontMetrics.top;
		for (TextToDraw ttd : this.TextArray) {
			paint.setTextAlign(ttd.TextAlign);
			canvas.drawText(ttd.Text, ttd.GetX(), ttd.GetY(), paint);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Matteo.MtLog("Touch!");
		float x = event.getX();
		if (x >= this.TimeLeftX && x < this.TimeRightX)
			return true;
		int i=this.GetTimeIndex(x);
		x=h.get(i).ViewX;
		this.pathTime = new Path();
		this.pathTime.moveTo(x, 0);
		this.pathTime.lineTo(x, this.LDY);
		this.invalidate();
		this.actHistory.PopupButton(i);
		return true;
	}
	
	public int GetTimeIndex(float x){
		for(int i=0;i<h.size();i++){
			this.TimeLeftX=h.get(i).LeftViewX;
			if(i<h.size()-1){
				this.TimeRightX=h.get(i+1).LeftViewX;
			}else{
				this.TimeRightX=ViewWidth;
			}
			if(x>=this.TimeLeftX&&x<this.TimeRightX){
				return i;
			}
		}
		return 0;
	}

	public void SetTimeIndex(int i){
		float x=h.get(i).ViewX;
		this.pathTime = new Path();
		this.pathTime.moveTo(x, 0);
		this.pathTime.lineTo(x, this.LDY);
		this.invalidate();
	}
}
