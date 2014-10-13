package com.matteo.matteolocationhistory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Handler;

public class MatteoMap {
	public int Zoom;//
	public ScreenCoordinate CurrentCenterScreenCoord;
	//public double LonPerPixel, LatPerPixel;
	public GpsCoordinate CenterGpsCoord;
	public static final double LonPerPixel19 = 0.00000448;// Zoom=19时，每像素相差经度,(116.403449-116.402554)/200
	public static final double LatPerPixel19 = 0.00000346;// Zoom=19时，每像素相差纬度,(39.915051-39.914359)/200
	public static final int MaxZoom = 19;
	public static final int MinZoom = 3;
	public MapStack MapsStack;
	public ArrayList<MapImage> MapsArray;
	private Handler HdlMapView;
	public UDLR ScreenUDLR;
	public int ThreadNum;
	public int AllThreadNum;
	public ArrayList<MapCoordinate> FetchingMapsArray;
	public static int Amplify;
	public int TotalDistanceX,TotalDistanceY;
	//private ThreadGroup GetMapImageThreadGroup;

	public MatteoMap(double lat, double lon, int zo, Handler hdl) {
		this.Init(lat, lon, zo, hdl);
	}
	
	public void Init(double lat, double lon, int zo, Handler hdl){
		this.CenterGpsCoord = new GpsCoordinate(lat, lon);
		this.Zoom = zo;
		this.CurrentCenterScreenCoord = new ScreenCoordinate(Matteo.ScreenCenterCoordinate.X,
				Matteo.ScreenCenterCoordinate.Y);
		this.MapsStack = new MapStack();
		this.MapsArray = new ArrayList<MapImage>();
		this.HdlMapView = hdl;
		this.ScreenUDLR = new UDLR(this.CurrentCenterScreenCoord,Matteo.ScreenWidth,Matteo.ScreenHeight,this,true);
		this.FetchingMapsArray=new ArrayList<MapCoordinate>();
		this.AllThreadNum=0;
		//MatteoMap.ThreadNum=0;
		MatteoMap.Amplify=0;
		this.TotalDistanceX=0;
		this.TotalDistanceY=0;
	}

	public void Scroll(int DistanceX, int DistanceY) {
		this.CurrentCenterScreenCoord.X = this.CurrentCenterScreenCoord.X - DistanceX;
		this.CurrentCenterScreenCoord.Y = this.CurrentCenterScreenCoord.Y - DistanceY;
		this.TotalDistanceX+=DistanceX;
		this.TotalDistanceY+=DistanceY;
		this.ScreenUDLR.Scroll(DistanceX, DistanceY);
		StartGetMap();
	}

	public void StartGetMap() {
		new Thread(){
			@Override
			public void run()
			{
				StartGetMapThread();
			}
		}.start();
	}
	
	public void StartGetMapThread(){
		ThreadGroup GetMapImageThreadGroup=new ThreadGroup("ThreadGroup to Get MapImage");
		ThreadNum=0;
		StartGetMapThread(new ScreenCoordinate(ScreenUDLR.GetL(),ScreenUDLR.GetU()),GetMapImageThreadGroup,true);
		Matteo.WaitAllThreadFinish(GetMapImageThreadGroup);
		this.FetchingMapsArray.clear();
		HdlMapView.sendEmptyMessage(Matteo.FlagNewMap);
	}
	
	public void StartGetMapThread(ScreenCoordinate sc,ThreadGroup tg,boolean IsRight){
		if(sc.X>=this.ScreenUDLR.GetR()||sc.Y>=this.ScreenUDLR.GetD())return;
		synchronized(this){
		MapCoordinate mc=sc.toMapCoordinate();
			if(this.ContainedInFetchingMapImageArray(mc))
			{
				return;
			}
			else
			{
				this.FetchingMapsArray.add(mc);
			}
		}
		ScreenCoordinate rsc=new ScreenCoordinate(0,0);
		ScreenCoordinate dsc=new ScreenCoordinate(0,0);
		MapImage img=this.ContainsMapImages(sc);
		if(img!=null)
		{
			if(IsRight)
			{
				rsc.Y=sc.Y;
				rsc.X=img.ImgUDLR.GetR()+1;
				this.StartGetMapThread(rsc,tg,true);
			}
				dsc.X=sc.X;
				dsc.Y=img.ImgUDLR.GetD()+1;
				this.StartGetMapThread(dsc,tg,false);
		}
		else
		{
			GetMapImageThread gmit = new GetMapImageThread(tg,sc);
			gmit.start();
			if(IsRight)
			{
				rsc.Y=sc.Y;
				rsc.X=sc.GetMapR()+1;
				this.StartGetMapThread(rsc,tg,true);
			}
				dsc.X=sc.X;
				dsc.Y=sc.GetMapD()+1;
				this.StartGetMapThread(dsc,tg,false);
		}
	}
	
	public class GetMapImageThread extends Thread {
		ScreenCoordinate RelativeScreenCoordinate;

		public GetMapImageThread(ThreadGroup tg,ScreenCoordinate sc) {
			super(tg,Integer.toString(++ThreadNum));
			this.RelativeScreenCoordinate=sc.clone();
		}

		public void run() {
			MapImage img=new MapImage(this.RelativeScreenCoordinate);
			synchronized(this){
				AllThreadNum++;
			}
			MapsArray.add(img);
		}
	}
	
	public boolean ContainedInFetchingMapImageArray(MapCoordinate mc){
		for(MapCoordinate mc2:this.FetchingMapsArray){
			if(mc.Compare(mc2)==0){
				return true;
			}
		}
		return false;
	}
	

	public MapImage ContainsMapImages(ScreenCoordinate sc) {
		for (int i = 0; i < this.MapsArray.size(); i++) {
			MapImage img = this.MapsArray.get(i);
			if (img.ImgUDLR.Contains(sc)) {
				return img;
			}
		}
		return null;
	}

	public static String GenMapURL(GpsCoordinate gc, int zo) {
		String url = Matteo.MapApi + "?copyright=1&center="
				+ Double.toString(gc.Longitude) + ","
				+ Double.toString(gc.Latitude) + "&width="
				+ Integer.toString(Matteo.MapWidth) + "&height=" + Integer.toString(Matteo.MapHeight)
				+ "&zoom=" + Integer.toString(zo);
		return url;
	}
/*
	public static String GenMapURL2(GpsCoordinate gc, int zo,int x,int y) {
		String url = Matteo.MapApi2 + "?copyright=1&center="
				+ Double.toString(gc.Longitude) + ","
				+ Double.toString(gc.Latitude) + "&width="
				+ Integer.toString(Matteo.MapWidth) + "&height=" + Integer.toString(Matteo.MapHeight)
				+ "&zoom=" + Integer.toString(zo)+"&x="+Integer.toString(x)
				+"&y="+Integer.toString(y)+"&ws=50";
		return url;
	}*/

	public static Bitmap GetBitmap(String s) {
		Bitmap bitmap = null;
		try {
			URL url = new URL(s);
			bitmap = BitmapFactory.decodeStream(url.openStream());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}

	public static int CompareMC(MapCoordinate mc1, MapCoordinate mc2) {
		if (mc1.Y > mc2.Y)
			return 1;
		if (mc1.Y < mc2.Y)
			return -1;
		else {
			if (mc1.X > mc2.X)
				return 1;
			if (mc1.X < mc2.X)
				return -1;
		}
		return 0;
	}

	public static double GpsDistance(GpsCoordinate gc1, GpsCoordinate gc2) {
		return MatteoMap.GpsDistance(gc1.Latitude, gc1.Longitude, gc2.Latitude,
				gc2.Longitude);
	}

	public static double GpsDistance(double lat1, double lon1, double lat2,
			double lon2) {
		return 0;
	}

	public class MapImage {
		public GpsCoordinate GpsCoord;
		public MapCoordinate MapCoord;
		public ScreenCoordinate RelativeScreenCenterCoord;
		public Bitmap Img;
		public UDLR ImgUDLR;

		public MapImage(MapCoordinate mc) {
			this.GpsCoord = MapCoordinateToMapImageCenterGpsCoordinate(mc);
			this.MapCoord = mc.clone();
			this.RelativeScreenCenterCoord=this.MapCoord.toScreenCoordinate();
			this.ImgUDLR = new UDLR(this.RelativeScreenCenterCoord,Matteo.MapWidth,Matteo.MapHeight,MatteoMap.this);
			this.GetMap();
		}
		
		public MapImage(ScreenCoordinate sc) {
			this.MapCoord=sc.toMapCoordinate();
			this.GpsCoord = MapCoordinateToMapImageCenterGpsCoordinate(this.MapCoord);
			this.RelativeScreenCenterCoord=this.MapCoord.toScreenCoordinate();
			this.ImgUDLR = new UDLR(this.RelativeScreenCenterCoord,Matteo.MapWidth,Matteo.MapHeight,MatteoMap.this);
			this.GetMap();
		}

		public void GetMap() {
			String url = MatteoMap.GenMapURL(this.GpsCoord, Zoom);
			//String url = MatteoMap.GenMapURL2(this.GpsCoord, Zoom,this.MapCoord.X,this.MapCoord.Y);
			this.Img = MatteoMap.GetBitmap(url);
			//AllThreadNum++;
			//this.Img.
		}
		
		public Rect GetRect(){
			return this.GetRect(Amplify);
		}
		
		public Rect GetRect(int amp)
		{
			Rect r=new Rect();
			r.left=Matteo.ScreenCenterCoordinate.X+this.MapCoord.X*MatteoMap.GetMapWidth()-MatteoMap.GetMapWidth()/2;
			r.top=Matteo.ScreenCenterCoordinate.Y+this.MapCoord.Y*MatteoMap.GetMapHeight()-MatteoMap.GetMapHeight()/2;
			r.right=r.left+MatteoMap.GetMapWidth();
			r.bottom=r.top+MatteoMap.GetMapHeight();
			return r;
		}
	}

	public class MapMatrix {
		public int RowCount, ColumnCount, U, D, L, R;

		public MapMatrix(int rc, int cc) {
			this.RowCount = rc;
			this.ColumnCount = cc;

		}

		public class MapUnit {
			public int U, D, L, R;

			public MapUnit(int u, int d, int l, int r) {
				this.U = u;
				this.D = d;
				this.L = l;
				this.R = r;
			}
		}

	}

	enum Direction {
		LU, RU, LD, RD, CC;
	}

	public static Direction GetDirection(int DistanceX, int DistanceY) {
		if (DistanceX > 0 && DistanceY > 0)
			return Direction.LU;
		if (DistanceX > 0 && DistanceY <= 0)
			return Direction.LD;
		if (DistanceX <= 0 && DistanceY > 0)
			return Direction.RU;
		if (DistanceX <= 0 && DistanceY <= 0)
			return Direction.RD;
		return Direction.CC;
	}

	public class MapStack extends Stack<MapImage> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MapStack() {
			super();
		}

		@Override
		public MapImage push(MapImage img) {
			super.addElement(img);
			if (this.size() == Matteo.ScreenMapsCount) {
				//this.clear();
				HdlMapView.sendEmptyMessage(Matteo.FlagNewMap);
			}
			return img;
		}
	}
	
	public static int GetMapWidth(){
		int wid=(int)((double)Matteo.MapWidth*Math.pow(2, (double)MatteoMap.Amplify));
		return wid;
	}
	
	public static int GetMapHeight(){
		int hei=(int)((double)Matteo.MapHeight*Math.pow(2, (double)MatteoMap.Amplify));
		return hei;
	}
	
	public static MapCoordinate RelativeScreenCoordinateToMapCoordinate(ScreenCoordinate sc)
	{
		int x=(int) Math.round(((double)(sc.X-Matteo.ScreenCenterCoordinate.X))/((double)MatteoMap.GetMapWidth()));
		int y=(int) Math.round(((double)(sc.Y-Matteo.ScreenCenterCoordinate.Y))/((double)MatteoMap.GetMapHeight()));
		return new MapCoordinate(x,y);
	}
	
	public static ScreenCoordinate MapCoordinateToMapImageRelativeCenterScreenCoordinate(MapCoordinate mc)
	{
		int x=Matteo.ScreenCenterCoordinate.X+mc.X*MatteoMap.GetMapWidth();
		int y=Matteo.ScreenCenterCoordinate.Y+mc.Y*MatteoMap.GetMapHeight();
		return new ScreenCoordinate(x,y);
	}
	
	public GpsCoordinate MapCoordinateToMapImageCenterGpsCoordinate(MapCoordinate mc){
		double lat=this.CenterGpsCoord.Latitude-mc.Y*Matteo.MapHeight*this.GetLatPerPixel();
		double lon=this.CenterGpsCoord.Longitude+mc.X*Matteo.MapWidth*this.GetLonPerPixel();
		return new GpsCoordinate(lat,lon);
	}
	
	public static int GetMapImageRByScreenCoordinate(ScreenCoordinate sc){
		MapCoordinate mc=sc.toMapCoordinate();
		return Matteo.ScreenCenterCoordinate.X+mc.X*MatteoMap.GetMapWidth()+MatteoMap.GetMapWidth()/2;
	}
	
	public static int GetMapImageDByScreenCoordinate(ScreenCoordinate sc){
		MapCoordinate mc=sc.toMapCoordinate();
		return Matteo.ScreenCenterCoordinate.Y+mc.Y*MatteoMap.GetMapHeight()+MatteoMap.GetMapHeight()/2;
	}
	
	public double GetAmplify()
	{
		return Math.pow(2, (double)MatteoMap.Amplify);
	}
	
	public void Clear()
	{
		this.FetchingMapsArray.clear();
		this.MapsArray.clear();
		this.MapsStack.clear();
	}
	
	public static double GetLatPerPixel(int zo){
		return MatteoMap.LatPerPixel19
		* Math.pow(2, MatteoMap.MaxZoom - zo);
	}
	
	public static double GetLonPerPixel(int zo){
		return MatteoMap.LonPerPixel19
				* Math.pow(2, MatteoMap.MaxZoom - zo);
	}
	
	public double GetLatPerPixel(){
		return MatteoMap.GetLatPerPixel(this.Zoom);
	}
	
	public double GetLonPerPixel(){
		return MatteoMap.GetLonPerPixel(this.Zoom);
	}
}
