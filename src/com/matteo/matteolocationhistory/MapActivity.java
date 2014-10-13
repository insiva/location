package com.matteo.matteolocationhistory;

//import com.matteo.matteolocationhistory.MatteoMap.MapImage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.OnGestureListener;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MapActivity extends Activity implements OnGestureListener {

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}
	/*GestureDetector detector;
	MatteoMapView MMapView;
	boolean Scrolling;
	volatile boolean ScreenLocked;
	int ScrollDistanceX,ScrollDistanceY;
	ProgressBar pbWait;
	FrameLayout.LayoutParams lpPbWait;
	static int T;
	FrameLayout mapLayout;
	int ClickCount;
	long ClickTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Matteo.SetWindowSize(this);
		T=0;
		this.Scrolling=false;
		this.ScrollDistanceX=this.ScrollDistanceY=0;
		this.mapLayout=(FrameLayout)this.findViewById(R.id.mapLayout);
		//this.mvMap = (MapView) this.findViewById(R.id.mvMap);
		this.setContentView(R.layout.activity_map);
		this.MMapView=new MatteoMapView(this);
		this.addContentView(MMapView,this.MMapView.MapViewLayoutParams);
		detector = new GestureDetector(this, this);
		this.pbWait=new ProgressBar(MapActivity.this);//(ProgressBar)this.findViewById(R.id.pbWait);
		this.lpPbWait=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		this.lpPbWait.leftMargin=Matteo.ScreenWidth/2;
		this.lpPbWait.topMargin=Matteo.ScreenHeight/2-100;
		this.addContentView(this.pbWait, lpPbWait);
		this.LockScreen();
		this.ClickCount=0;
		this.ClickTime=0;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(this.ScreenLocked) return true;
		switch(event.getAction()){
		case MotionEvent.ACTION_UP:
			if(this.Scrolling)
			{
				this.Scrolling=false;
				this.LockScreen();
				this.MMapView.ScrollMap(this.ScrollDistanceX, this.ScrollDistanceY);
				this.ScrollDistanceX=0;
				this.ScrollDistanceY=0;
				Matteo.MtLog("Thread,Image,",this.MMapView.m.AllThreadNum,this.MMapView.m.MapsArray.size());
			}
			break;
		case MotionEvent.ACTION_DOWN:
			this.ClickCount++;
			if(this.ClickCount == 1){  
                this.ClickTime = System.currentTimeMillis();  
            } else if (this.ClickCount == 2){  
                long ClickTimeSpan = System.currentTimeMillis()-this.ClickTime;  
                if(ClickTimeSpan < Matteo.DoubleClickTimeSpan){  
                    this.DoubleClick(event);  
                      
                }  
                this.ClickCount = 0;  
                this.ClickTime=0;
            }  
			break;
		}
		return detector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {

		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		//Matteo.MtLog("scrolling");
        this.ClickCount = 0;  
        this.ClickTime=0;
		this.Scrolling=true;
		int dx=Math.round(distanceX);
		int dy=Math.round(distanceY);
		this.ScrollDistanceX+=dx;
		this.ScrollDistanceY+=dy;
		this.MMapView.scrollBy(dx,dy);
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void LockScreen(){
		this.ScreenLocked=true;
		this.pbWait.setVisibility(View.VISIBLE);
	}
	
	public void UnLockScreen(){
		this.ScreenLocked=false;
		this.pbWait.setVisibility(View.INVISIBLE);
	}
	
	public void DoubleClick(MotionEvent event){
		if(this.MMapView.m.Zoom==MatteoMap.MaxZoom)
		{
			Toast toast = Toast.makeText(this, "已经放大到最大级别！", Toast.LENGTH_SHORT); 
			toast.show(); 
			return;
		}
		else if(this.MMapView.m.Zoom==MatteoMap.MinZoom){
			Toast toast = Toast.makeText(this, "已经缩小到最小级别！", Toast.LENGTH_SHORT); 
			toast.show(); 
			return;
		}
		this.LockScreen();
		int dx=-(Matteo.ScreenCenterCoordinate.X-Math.round(event.getX()));
		int dy=-(Matteo.ScreenCenterCoordinate.Y-Math.round(event.getY()));
		//this.mvMap.scrollBy(dx, dy);
		this.MMapView.Amplify(dx,dy,1);
		//Matteo.MtLog("DoubleClick-",Math.round(event.getX()),Math.round(event.getY()));
	}*/
}
