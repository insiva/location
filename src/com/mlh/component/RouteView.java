package com.mlh.component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.mlh.Config;
import com.mlh.activity.HomeActivity;
import com.mlh.model.Route;
import com.mlh.model.Trajectory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 * @author Matteo
 *一个路线的示意图，能够展示路线距离的折线图
 */
@SuppressLint("SimpleDateFormat")
public class RouteView extends View {
	public static final int HEIGHT = 160;
	public static final int WIDTH = 600;
	public static final int VIEW_HEIGHT = 240;
	public static final int VIEW_WIDTH = 700;
	public static final int MARGIN_BOTTOM = 50;
	public static final int MARGIN_TOP = 20;
	public static final int MARGIN_RIGHT = 20;
	public static final int VIEW_MARGIN_TOP = 20;
	public static final int DISTANCE_UNIT_COUNT = 4;
	public static final int TIME_UNIT_COUNT = 4;
	public static final float SCALE_LENGTH = 4;
	public static final long DEFAULT_TIME_SPAN = 3 * 24 * 3600 * 1000;
	public static final long DAY_DURATION = 24 * 3600 * 1000;
	public static final long HOUR_DURATION = 3600 * 1000;
	public static final long MINUTE_DURATION = 60 * 1000;
	public static final SimpleDateFormat DayFormatter = new SimpleDateFormat(
			"yyyy-M-d");
	public static final SimpleDateFormat HourFormatter = new SimpleDateFormat(
			"yyyy-M-d,H点");
	public static final SimpleDateFormat MinuteFormatter = new SimpleDateFormat(
			"yyyy-M-d H:m");
	public static final SimpleDateFormat TimeFormatter = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	
	//private Date dtStartDay, dtEndDay;
	private LayoutParams lpView;
	private ArrayList<TextToDraw> arrayText;
	private float LTX, LTY, LDX, LDY, RTX, RDX, RDY,TimeLeftX,TimeRightX;
	public static float TextHeight;
	private HashMap<Integer,Float> hmLeftViewX;
	private HashMap<Integer,Float> hmViewX;
	private Context ctxParent;
	private Route route;
	private Paint paintRoute;
	private Path pathLocation, pathFill, pathFrame, pathTimeScale, pathTime;
	private double stepWidth;
	
	@SuppressLint("UseSparseArrays")
	public RouteView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.ctxParent=context;
		this.lpView = new LayoutParams(RouteView.VIEW_WIDTH,
				RouteView.VIEW_HEIGHT);
		this.lpView.addRule(RelativeLayout.CENTER_HORIZONTAL,
				RelativeLayout.TRUE);
		this.lpView.addRule(RelativeLayout.ALIGN_PARENT_TOP,
				RelativeLayout.TRUE);
		this.lpView.setMargins(0, RouteView.VIEW_MARGIN_TOP, 0, 0);
		this.hmLeftViewX=new HashMap<Integer,Float>();
		this.hmViewX=new HashMap<Integer,Float>();
	}
	
	public void setRoute(Route r){
		this.setVisibility(View.VISIBLE);
		this.route=r;
		if(this.route.size()<=1)
		{
			this.setVisibility(View.INVISIBLE);
			return;
		}
		this.startPath();
		this.invalidate();
	}

	private void startPath() {
		this.paintRoute = new Paint();
		this.arrayText = new ArrayList<TextToDraw>();
		this.LTX = RouteView.VIEW_WIDTH-RouteView.MARGIN_RIGHT-RouteView.WIDTH;
		this.LTY = RouteView.MARGIN_TOP;
		this.LDX = this.LTX;
		this.LDY = this.LTY + RouteView.HEIGHT;
		this.RTX = this.LTX + RouteView.WIDTH;
		//this.RTY = this.LTY;
		this.RDX = this.RTX;
		this.RDY = this.LDY;
		this.TimeLeftX = this.TimeRightX = 0;
		this.startPathFrame();
		this.startTimeScale();
		this.startPathLocation();
		this.pathTime = null;
	}

	private void startPathFrame() {
		this.pathFrame = new Path();
		this.pathFrame.moveTo(this.LDX, 0);
		this.pathFrame.lineTo(this.LDX, this.LDY);
		this.pathFrame.lineTo(this.RDX, this.RDY);
		this.pathFrame.lineTo(this.RTX, 0);
		this.pathFrame.lineTo(this.LTX, 0);

		float x = this.LDX, y, YUnit = ((float) (RouteView.VIEW_HEIGHT - RouteView.MARGIN_BOTTOM - RouteView.MARGIN_TOP))
				/ (float) RouteView.DISTANCE_UNIT_COUNT;
		int i = 1;
		int distanceUnit = (int) Math.ceil(this.getDistanceUnit());
		for (y = this.LDY - YUnit; y >= this.LTY; y -= YUnit) {
			this.pathFrame.moveTo(x, y);
			this.pathFrame.lineTo(x + RouteView.SCALE_LENGTH, y);
			String t = Route.getDistanceText((i++) * distanceUnit);
			TextToDraw ttd = new TextToDraw(t, x - RouteView.SCALE_LENGTH, y,Align.RIGHT);
			this.arrayText.add(ttd);
		}
	}

	private void startTimeScale() {
		this.pathTimeScale = new Path();
		long timeUnit = this.getTimeUnit();
		long startT = this.route.getStartTime();
		if (timeUnit >= RouteView.DAY_DURATION) {
			startT = startT + RouteView.DAY_DURATION - startT
					% RouteView.DAY_DURATION;
		} else if (timeUnit >= RouteView.HOUR_DURATION) {
			startT = startT + RouteView.HOUR_DURATION - startT
					% RouteView.HOUR_DURATION;
		} else {
			startT = startT + RouteView.MINUTE_DURATION - startT
					% RouteView.MINUTE_DURATION;
		}
		long t = startT;
		float x = this.LDX;
		long dura=this.route.getDuration();
		//long endT=this.route.getEndTime();
		for (int i = 0; i < RouteView.TIME_UNIT_COUNT; i++) {
			t = startT + i * timeUnit;
			x = this.LDX
					+ (float) (((double) (t - startT) / (double) dura) * (double) RouteView.WIDTH);
			String ts = getTimeScaleText(t, timeUnit);
			TextToDraw ttd = new TextToDraw(ts, x, this.LDY, Align.CENTER);
			this.arrayText.add(ttd);
			this.pathTimeScale.moveTo(x, 0);
			this.pathTimeScale.lineTo(x, this.LDY);
		}
	}

	private void startPathLocation() {
		this.stepWidth = (double) RouteView.WIDTH / (double) this.route.getDuration();
		// 创建、并初始化Path
		pathLocation = new Path();
		pathFill = new Path();
		float startX = this.LDX;
		float startY = this.LDY, x = 0, y = 0;
		float leftx = 0;
		pathLocation.moveTo(startX, startY);
		pathFill.moveTo(startX, startY);
		for (int i = 0; i < this.route.size(); i++) {
			Trajectory traj=this.route.get(i);
			x = (float) ((float) (traj.getCreateTime().getTime() - this.route.getStartTime())
					* this.stepWidth) + startX;
			y = (float) (startY - (traj.getDistance()/this.route.getMaxDistance()) * RouteView.HEIGHT);
			pathLocation.lineTo(x, y);
			pathFill.lineTo(x, y);
			if (i != 0) {
				leftx = (x + leftx) / 2;
			}
			this.hmLeftViewX.put(i, leftx);
			this.hmViewX.put(i, x);
		}
		pathLocation.lineTo(startX + RouteView.WIDTH, y);
		pathFill.lineTo(startX + RouteView.WIDTH, y);
		pathFill.lineTo(startX + RouteView.WIDTH, startY);
		pathFill.lineTo(startX, startY);
	}

	private double getDistanceUnit() {
		double md = this.route.getMaxDistance();
		return md / RouteView.DISTANCE_UNIT_COUNT;
	}
	
	private long getTimeUnit() {
		double r = (double) this.route.getDuration()
				/ (double) RouteView.TIME_UNIT_COUNT;
		long s = RouteView.MINUTE_DURATION;
		if (r > RouteView.DAY_DURATION) {
			s = RouteView.DAY_DURATION;
		} else if (r > RouteView.HOUR_DURATION) {
			s = RouteView.HOUR_DURATION;
		} else if (r > RouteView.MINUTE_DURATION) {
			s = RouteView.MINUTE_DURATION;
		} else {
			return RouteView.MINUTE_DURATION;
		}
		double rr = Math.ceil(r / (double) s);
		return (long) (rr * s);
	}

	private static String getTimeScaleText(long t, long u) {
		Date d = new Date();
		d.setTime(t);
		if (u >= RouteView.DAY_DURATION) {
			return RouteView.DayFormatter.format(d);
		} else if (u >= RouteView.HOUR_DURATION) {
			return RouteView.HourFormatter.format(d);
		} else {
			return RouteView.MinuteFormatter.format(d);
		}
	}
	
	private int getTimeIndex(float x){
		for(int i=0;i<this.route.size();i++){
			this.TimeLeftX=this.hmLeftViewX.get(i);
			if(i<this.route.size()-1){
				this.TimeRightX=this.hmViewX.get(i+1);
			}else{
				this.TimeRightX=RouteView.VIEW_WIDTH;
			}
			if(x>=this.TimeLeftX&&x<this.TimeRightX){
				return i;
			}
		}
		return 0;
	}
	
	public LayoutParams getLayoutParams(){
		return this.lpView;
	}
	
	private void drawText(Canvas canvas) {
		this.paintRoute.setColor(Color.GRAY);
		this.paintRoute.setTextSize(20);
		FontMetrics fontMetrics = this.paintRoute.getFontMetrics();
		RouteView.TextHeight = fontMetrics.bottom - fontMetrics.top;
		for (TextToDraw ttd : this.arrayText) {
			this.paintRoute.setTextAlign(ttd.TextAlign);
			canvas.drawText(ttd.Text, ttd.getX(), ttd.getY(), this.paintRoute);
		}
	}

	public void setTimeIndex(int i){
		float x=this.hmViewX.get(i);
		this.pathTime = new Path();
		this.pathTime.moveTo(x, 0);
		//this.pathTime.
		this.pathTime.lineTo(x, this.LDY);
		this.invalidate();
	}
	
	public void setTraceTime(int i,float r){
		float x1=this.hmViewX.get(i);
		float x2=this.hmViewX.get(i+1);
		float x=x1+(x2-x1)*r;
		this.pathTime = new Path();
		this.pathTime.moveTo(x, 0);
		this.pathTime.lineTo(x, this.LDY);
		//this.invalidate();
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Matteo.MtLog("Touch!");
		if(this.ctxParent.getClass()!=HomeActivity.class){
			return false;
		}
		HomeActivity actHome=(HomeActivity)this.ctxParent;
		if(this.getVisibility()==View.INVISIBLE){
			return false;
		}
		float x = event.getX();
		if (x >= this.TimeLeftX && x < this.TimeRightX)
			return true;
		int i=this.getTimeIndex(x);
		x=this.hmViewX.get(i);
		this.pathTime = new Path();
		this.pathTime.moveTo(x, 0);
		this.pathTime.lineTo(x, this.LDY);
		this.invalidate();
		actHome.popupLocationButton(i);
		return true;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onDraw(Canvas canvas) {
		// Matteo.MtLog("Draw!");
		if(this.route==null||this.route.size()<2)return;
		canvas.drawColor(Color.TRANSPARENT);
		this.paintRoute.setColor(Color.GRAY);
		this.paintRoute.setStyle(Paint.Style.STROKE);
		this.paintRoute.setStrokeWidth(2);
		canvas.drawPath(this.pathFrame, this.paintRoute);
		this.paintRoute.setColor(Color.BLUE);
		this.paintRoute.setStyle(Paint.Style.STROKE);
		this.paintRoute.setStrokeWidth(2);
		canvas.drawPath(pathLocation, this.paintRoute);
		// pathLocation.close();
		this.paintRoute.setColor(Color.RED);
		this.paintRoute.setStyle(Paint.Style.STROKE);
		this.paintRoute.setStrokeWidth(1);
		canvas.drawPath(this.pathTimeScale, this.paintRoute);
		// pathTimeScale.close();
		this.paintRoute.setColor(Color.BLUE);
		this.paintRoute.setStrokeWidth(0);
		this.paintRoute.setStyle(Paint.Style.FILL);
		this.paintRoute.setAlpha(50);
		canvas.drawPath(pathFill, this.paintRoute);
		
		if (this.pathTime != null) {
			this.paintRoute.setColor(Config.getColor(com.mlh.R.color.DarkGreen));
			this.paintRoute.setStrokeWidth(2);
			this.paintRoute.setStyle(Paint.Style.STROKE);
			canvas.drawPath(this.pathTime, this.paintRoute);
		}
		this.drawText(canvas);
	}
	
	public class TextToDraw {
		private String Text;
		private float X,Y;
		//private Paint TextPaint;
		private Align TextAlign;
		public TextToDraw(String t,float x,float y,Align a){
			this.Text=t;
			this.X=x;this.Y=y;
			//this.TextPaint=p;
			this.TextAlign=a;
		}
		
		/*		private float getWidth(){
			return this.TextPaint.measureText(this.Text);
		}
		
		private float getAlignRightX(){
			return this.X-this.getWidth();
		}
		
		private float getAlignRightY(){
			return this.Y+TextHeight/3;
		}*/
		
		private float getX(){
			if(this.TextAlign.equals(Align.RIGHT)){
				return this.X-RouteView.SCALE_LENGTH;
			}else if(this.TextAlign.equals(Align.CENTER)){
				return this.X;
			}
			return this.X;
		}
		
		private float getY(){
			if(this.TextAlign.equals(Align.RIGHT)){
				return this.Y+RouteView.TextHeight/3;
			}else if(this.TextAlign.equals(Align.CENTER)){
				return this.Y+RouteView.TextHeight;
			}
			return this.Y;
		}
	}
}
