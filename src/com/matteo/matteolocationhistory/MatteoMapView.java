package com.matteo.matteolocationhistory;

//import com.matteo.matteolocationhistory.MatteoMap.MapImage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.View;
import android.widget.FrameLayout;


public class MatteoMapView  extends View {

	public MatteoMapView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}/*
	GestureDetector detector;
	MatteoMap m;
	private int T;
	FrameLayout.LayoutParams MapViewLayoutParams;// =
	MapActivity atyMap;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == Matteo.FlagNewMap) {
				invalidate();
				atyMap.UnLockScreen();
			}
		}
	};

	public MatteoMapView(Context context) {
		super(context);
		this.atyMap = (MapActivity) context;
		this.Init();
	}

	public MatteoMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.atyMap = (MapActivity) context;
		this.Init();
	}

	public void Init() {
		T = 0;
		this.MapViewLayoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		m = new MatteoMap(Matteo.BeijingLatitude, Matteo.BeijingLongitude, 7,
				this.handler);
		//MatteoMap.Amplify=1;
		m.StartGetMap();
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		for (MapImage img : m.MapsArray) {
			if (m.ScreenUDLR.Contains(img.ImgUDLR)) {
				canvas.drawBitmap(img.Img, null,img.GetRect(), null);
			}
		}
	}

	public void ScrollMap(int DistanceX, int DistanceY) {
		this.m.Scroll(DistanceX, DistanceY);
	}
	
	public void Amplify(int dx,int dy,int amp)
	{
		//this.scrollBy(dx, dy);
		//MatteoMap.Amplify=amp;
		//this.invalidate();
		this.m.Clear();
		this.setScrollX(0);
		this.setScrollY(0);
		int zo=this.m.Zoom+amp;
		Matteo.MtLog("ccsc",m.CurrentCenterScreenCoord.X,m.CurrentCenterScreenCoord.Y);
		double lat=this.m.CenterGpsCoord.Latitude-((double)(m.TotalDistanceY+dy))*this.m.GetLatPerPixel();
		double lon=this.m.CenterGpsCoord.Longitude+((double)(m.TotalDistanceX+dx))*this.m.GetLonPerPixel();
		Matteo.MtLog(Double.toString(lat)+","+Double.toString(lon));
		this.m.Init(lat, lon, zo, handler);
		this.m.StartGetMap();
	}
*/
}