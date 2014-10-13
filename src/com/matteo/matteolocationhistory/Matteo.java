package com.matteo.matteolocationhistory;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.app.Activity;

public class Matteo {
	public static MatteoApplication ThisApplication;
	public static String BaiduAK = "OB2sh66oSzrBUosn3WsmMy8A";
	public static Gps MtGPS;
	public static DbConn MtDb;
	public static final int GpsInterval = 1000*1000;// 获取GPS的时间间隔，单位“毫秒”
	public static String SDDir;
	private static final String DbName = "matteo.db3";
	public static final String ApplicationName = "MatteoLocationHistory";
	public static final String MapApi = "http://api.map.baidu.com/staticimage";
	public static final String MapApi2 = "http://192.168.1.102/map/Default.aspx";
	public static final String ActionNewLocation="com.matteo.action.newlocation";
	public static String DbPath;
	public static String PictureDirectory;
	public static String TempPicDirectory;
	public static String TempPicPath;
	public static boolean IsInitialized = false;
	//public static CurrentLocation CL;
	public static int ScreenWidth = 720;
	public static int ScreenHeight = 1280;
	public static int ScreenDensityDPI;
	public static float ScreenDensity;
	public static int MapRowsCount;
	public static int MapColumnsCount;
	public static int ScreenMapsCount;
	public static final int MapWidth = 512;
	public static final int MapHeight = 256;
	public static ScreenCoordinate ScreenCenterCoordinate;
	public static final double BeijingLatitude = 39.914888;
	public static final double BeijingLongitude = 116.403874;
	public static final int FlagNewMap = 0x123;
	public static final long DoubleClickTimeSpan = 1000;
	public static final int SleepTimeSpan = 1000;

	public static void Initialize(LocationManager lm) {
		if (Matteo.IsInitialized)
			return;

		Matteo.InitializeDb();
		Matteo.InitializeGPS(lm);
		
		Matteo.InitializePictureDirectory();
		
		Matteo.IsInitialized = true;
	}

	public static void Initialize() {
		if (Matteo.IsInitialized)
			return;
		Matteo.InitializeDb();
		
		Matteo.InitializePictureDirectory();
		
		Matteo.IsInitialized = true;
	}

	public static void SetWindowSize(Activity atv) {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		atv.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		// ctx.getwi
		int w=mDisplayMetrics.widthPixels;
		int h=mDisplayMetrics.heightPixels;
		Matteo.ScreenWidth = w>h?h:w;
		Matteo.ScreenHeight = h>w?h:w;
		Matteo.ScreenDensityDPI = mDisplayMetrics.densityDpi;
		Matteo.ScreenDensity = mDisplayMetrics.density;
		Matteo.ScreenCenterCoordinate = new ScreenCoordinate(
				Matteo.ScreenWidth / 2, Matteo.ScreenHeight / 2);
		/* 横屏时计算方法 */
		Matteo.MapRowsCount = Matteo.UpperOdd(Matteo.ScreenHeight,
				Matteo.MapHeight);// (int)
		Matteo.MapColumnsCount = Matteo.UpperOdd(Matteo.ScreenWidth,
				Matteo.MapWidth);// (int)
		Matteo.ScreenMapsCount = Matteo.MapColumnsCount
				* Matteo.MapColumnsCount;
	}

	public static int UpperOdd(int p1, int p2)// 上取奇数
	{
		int p3 = (int) Math.ceil((double) p1 / (double) p2);
		return p3 % 2 == 0 ? (p3 + 1) : p3;
	}

	private static void InitializeGPS(LocationManager lm) {
		// Matteo.mtGPS=new Gps(lm);
	}

	private static void InitializeDb() {
		while (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				Thread.sleep(Matteo.SleepTimeSpan);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Matteo.SDDir = Environment.getExternalStorageDirectory().toString()
				+ "/" + Matteo.ApplicationName;
		File sdFile = new File(Matteo.SDDir);
		if (!sdFile.exists()) {
			sdFile.mkdir();
		}
		Matteo.DbPath = Matteo.SDDir + "/" + Matteo.DbName;
		Matteo.MtDb = new DbConn();
	}
	
	public static void InitializePictureDirectory(){
		Matteo.PictureDirectory=Matteo.SDDir+"/Pictures/";
		File picFile=new File(Matteo.PictureDirectory);
		if(!picFile.exists()){
			picFile.mkdir();
		}
		Matteo.TempPicDirectory=Matteo.PictureDirectory+"Temp/";
				//Matteo.ThisApplication.getApplicationContext().getFilesDir().toString()+"/temp/";//
		picFile=new File(Matteo.TempPicDirectory);
		if(!picFile.exists()){
			picFile.mkdir();
		}
		Matteo.TempPicPath=Matteo.TempPicDirectory+"temp.jpg";
	}

	public static int UpperInteger(double d) // 上取整
	{
		return (int) Math.ceil(d);
	}

	public static void WaitAllThreadFinish(ThreadGroup threadGroup) {
		while (threadGroup.activeCount() > 0) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void WaitThreadFinish(Thread tr) {
		while (tr.isAlive()) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean FileExists(String fp) {
		try {
			File f = new File(fp);
			if (!f.exists()) {
				return false;
			}

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	public static void Log(String l){
		String sql="insert into matteo_log(description) values('"+l.replace("'","''")+"')";
		Matteo.MtDb.Execute(sql);
	}
	
	public static double Log2(double v){
		return Math.log(v)/Math.log(2);
	}
	
	public static double GetNumberScale(double d){
		double x=1;
		while((d/Math.pow(10, x))>10){
			x=x+1;
		}
		return Math.pow(10, x);
	}
	
	public static int GetUpperEven(double d){
		int u=Matteo.UpperInteger(d);
		return (u%2)==0?u:(u+1);
	}
}
