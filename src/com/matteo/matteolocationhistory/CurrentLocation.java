package com.matteo.matteolocationhistory;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.matteo.matteolocationhistory.model.Conf;
import com.matteo.matteolocationhistory.model.MtLocation;

public class CurrentLocation extends MtLocation {
	//public BDLocation Location;
	private static CurrentLocation cl;
	private LocationClient mLocClient;
	private MatteoLocationListener myListener;


	private CurrentLocation() {
		super();
		this.myListener = new MatteoLocationListener();
		this.mLocClient = new LocationClient(Matteo.ThisApplication);
		this.mLocClient.registerLocationListener(this.myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(Matteo.GpsInterval);
		option.setAddrType(Conf.AddrTypeAll);
		this.mLocClient.setLocOption(option);
		this.mLocClient.start();
	}
	
	public static CurrentLocation getInstance(){
		if(cl==null){
			cl=new CurrentLocation();
		}
		return CurrentLocation.cl;
	}
	
	public static double Latitude(){
		return CurrentLocation.getInstance().Latitude;
	}
	
	public static double Longitude(){
		return CurrentLocation.getInstance().Longitude;
	}
	
	public static boolean NotNull(){
		return CurrentLocation.getInstance().Guid!=0;
	}
	
	public static void Set(MtLocation loc){
		CurrentLocation l=CurrentLocation.getInstance();
		l.Accuracy=loc.Accuracy;
		l.Address=loc.Address;
		l.Altitude=loc.Accuracy;
		l.Bearing=loc.Bearing;
		l.City=loc.City;
		l.CreateTime=loc.CreateTime;
		l.District=loc.District;
		l.Guid=loc.Guid;
		l.Latitude=loc.Accuracy;
		l.Longitude=loc.Accuracy;
		l.Province=loc.Province;
		l.Speed=loc.Speed;
		l.Street=loc.Street;
		l.StreetNum=loc.StreetNum;
	}
	
	public static void StartLocate()
	{
		CurrentLocation c=CurrentLocation.getInstance();
		//c.mLocClient.start();
	}
}
