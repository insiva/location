package com.mlh;

import java.io.File;

import com.baidu.mapapi.model.LatLng;
import com.mlh.service.LocateService;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class Config {	
	public static final String DEFAULT_DATEFORMAT="yyyy-MM-dd HH:mm:ss";
	
	public static final String SUCCESS="1";
	public static final String HOST="http://123.57.36.224/locationhistory/";
	public static final String HOST_FILE=Config.HOST+"files/";
	public static final String HOST_PICTURE=Config.HOST_FILE+"picture/";
	public static final String HOST_MOBILE=Config.HOST+"mobile/";
	public static final String APPLICATION_NAME="matteo";
	public static final LatLng BEIJING_LATLNG=new LatLng(39.914888,116.403874);
	public static final String XML="XML";
	public static final int WHAT_XML=0x100;
	public static final int LOCATE_INTERVAL=10*60*1000;

	private static MApplication ThisApplication;
	private static boolean WifiState;
	private static String DirectoryPath;
	private static String PictureDirectoryPath;
	private static String TempDirectoryPath;
	private static String TempPicturePath;
	private static LocateService MLocateService;
	private static int ScreenHeight;
	private static int ScreenWidth;
	
	public static String getDirectoryPath(){
		return Config.DirectoryPath;
	}
	
	public static String getPictureDirectoryPath(){
		return Config.PictureDirectoryPath;
	}
	
	public static String getTempDirectoryPath(){
		return Config.TempDirectoryPath;
	}
	
	public static MApplication getApplication(){
		return Config.ThisApplication;
	}
	
	public static void setWifiState(boolean state){
		Config.WifiState=state;
	}
	
	public static boolean getWifiState(){
		return Config.WifiState;
	}
	
	public static int getScreenHeight(){
		return Config.ScreenHeight;
	}
	
	public static int getScreenWidth(){
		return Config.ScreenWidth;
	}
	
	public static String getTempPicturePath(){
		return Config.TempPicturePath;
	}
	
	public static void initialize(MApplication app){
		Config.ThisApplication=app;
		Config.initParams();
		Config.initScreenSize();
		Config.initWifiState();
		Config.startLocateService();
	}
	
	private static void initParams(){
		int c=0;
		while (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			if (c >= 2) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			c++;
		}
		if (c >= 2) {
			Config.DirectoryPath = Config.ThisApplication.getFilesDir().getAbsolutePath().toString()
					+ "/" + Config.APPLICATION_NAME;
		} else {
			Config.DirectoryPath = Environment.getExternalStorageDirectory().toString()
					+ "/" + Config.APPLICATION_NAME+"/";
		}
		File sdFile = new File(Config.DirectoryPath);
		if (!sdFile.exists()) {
			sdFile.mkdir();
		}
		Config.PictureDirectoryPath = Config.DirectoryPath + "Pictures/";
		File picFile = new File(Config.PictureDirectoryPath);
		if (!picFile.exists()) {
			picFile.mkdir();
		}
		Config.TempDirectoryPath = Config.DirectoryPath + "Temp/";
		picFile = new File(Config.TempDirectoryPath);
		if (!picFile.exists()) {
			picFile.mkdir();
		}
		Config.TempPicturePath = Config.TempDirectoryPath + "temp.jpg";
	}
	
	@SuppressWarnings("static-access")
	private static void initWifiState(){
		WifiManager wifiManager = (WifiManager) Config.ThisApplication
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null) {
			int wifiState = wifiManager.getWifiState();
			if (wifiState == wifiManager.WIFI_STATE_ENABLED) {
				Config.WifiState = true;
				ConnectivityManager connManager = (ConnectivityManager) Config.ThisApplication
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo mWifi = connManager
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (mWifi.isConnected()) {
					Config.WifiState = true;
				}
			} else {
				Config.WifiState = false;
			}
		} else {
			Config.WifiState = false;
		}
	}

	private static void initScreenSize() {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager mWm = (WindowManager) Config.ThisApplication.getSystemService(Context.WINDOW_SERVICE);
		mWm.getDefaultDisplay().getMetrics(dm);
		Config.ScreenHeight = dm.heightPixels;
		Config.ScreenWidth = dm.widthPixels;
	}
	
	private static void startLocateService(){
		if(Config.MLocateService==null){
			Intent it = new Intent(Config.ThisApplication, LocateService.class);
			// i.setAction(ACTION_START);
			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Config.ThisApplication.startService(it);
		}
	}
	
	public static void startLocate(){
		Config.MLocateService.startLocate();
	}
	
	public static void setLocateService(LocateService ls){
		Config.MLocateService=ls;
	}
	
	public static String getString(int resId){
		return Config.ThisApplication.getString(resId);
	}
	
	public static int getColor(int resId){
		return Config.ThisApplication.getResources().getColor(resId);
	}
	
	public static void Log(String l) {
		System.out.println("------------------");
		System.out.println(l);
		System.out.println("------------------");
	}
}
