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

/**
 * @author Matteo
 * *记录系统运行中一些常用的配置参数，包括：
 * 1、服务器IP地址。
 * 2、本地文件夹目录。
 * 3、常用标志。
 * 4、本机参数。
 */
public class Config {	
	public static final String DEFAULT_DATEFORMAT="yyyy-MM-dd HH:mm:ss";
	
	public static final String SUCCESS="1";
	/**
	 * 服务器地址
	 */
	public static final String HOST="http://123.57.36.224/locationhistory/";
	/**
	 * 服务器端保存文件地址
	 */
	public static final String HOST_FILE=Config.HOST+"files/";
	/**
	 * 服务器端保存照片地址
	 */
	public static final String HOST_PICTURE=Config.HOST_FILE+"picture/";
	/**
	 * 服务器端页面文件夹地址
	 */
	public static final String HOST_MOBILE=Config.HOST+"mobile/";
	/**
	 * 应用名称
	 */
	public static final String APPLICATION_NAME="matteo";
	/**
	 * 北京坐标
	 */
	public static final LatLng BEIJING_LATLNG=new LatLng(39.914888,116.403874);
	public static final String XML="XML";
	/**
	 * XML标志，用于Message中
	 */
	public static final int WHAT_XML=0x100;
	/**
	 * 定位间隔，默认为10分钟
	 */
	public static final int LOCATE_INTERVAL=10*60*1000;

	/**
	 * 当前应用的引用
	 */
	private static MApplication ThisApplication;
	/**
	 * WIFI状态，TRUE时表示WIFI可用且连接上，FALSE表示WIFI不可用
	 */
	private static boolean WifiState;
	/**
	 * 本地目录地址
	 */
	private static String DirectoryPath;
	/**
	 * 本地照片目录地址
	 */
	private static String PictureDirectoryPath;
	/**
	 * 本地临时文件夹地址
	 */
	private static String TempDirectoryPath;
	/**
	 * 本地临时照片目录地址
	 */
	private static String TempPicturePath;
	/**
	 * 定位服务引用
	 */
	private static LocateService MLocateService;
	/**
	 * 屏幕高度
	 */
	private static int ScreenHeight;
	/**
	 * 屏幕宽度
	 */
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
	
	/**
	 * 初始化函数
	 */
	public static void initialize(MApplication app){
		Config.ThisApplication=app;
		Config.initParams();
		Config.initScreenSize();
		Config.initWifiState();
		Config.startLocateService();
	}
	
	/**
	 * 初始化参数
	 */
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
	
	/**
	 * 初始化Wifi状态
	 */
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

	/**
	 * 初始化屏幕参数
	 */
	private static void initScreenSize() {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager mWm = (WindowManager) Config.ThisApplication.getSystemService(Context.WINDOW_SERVICE);
		mWm.getDefaultDisplay().getMetrics(dm);
		Config.ScreenHeight = dm.heightPixels;
		Config.ScreenWidth = dm.widthPixels;
	}
	
	/**
	 * 开启LocateService，启动实时定位
	 */
	private static void startLocateService(){
		if(Config.MLocateService==null){
			Intent it = new Intent(Config.ThisApplication, LocateService.class);
			// i.setAction(ACTION_START);
			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Config.ThisApplication.startService(it);
		}
	}
	
	/**
	 * 开启LocateService，启动实时定位
	 */
	public static void startLocate(){
		Config.MLocateService.startLocate();
	}
	
	public static void setLocateService(LocateService ls){
		Config.MLocateService=ls;
	}

	
	/**
	 * 获取String资源
	 */
	public static String getString(int resId){
		return Config.ThisApplication.getString(resId);
	}

	
	/**
	 * 获取Color资源
	 */
	public static int getColor(int resId){
		return Config.ThisApplication.getResources().getColor(resId);
	}
	
	public static void log(String l) {
		System.out.println("------------------");
		System.out.println(l);
		System.out.println("------------------");
	}
	
	public static void logCurrentThreadID(String t){
		System.out.println("------------------");
		System.out.println(t+",CurrentThreadID:"+Thread.currentThread().getId());
		System.out.println("------------------");
	}
}
