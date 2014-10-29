package com.mlh.service;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.mlh.Config;
import com.mlh.model.LocationListener;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author Matteo
 *定位Service
 */
public class LocateService extends Service {
	public static final String ADDRESS_TYPE="all";
	private LocationClient mLocClient;
	private LocationListener mListener;
	
	public LocateService() {
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Config.setLocateService(this);
		this.mListener=new LocationListener();
		this.startLocate();
	}
	
	public void startLocate(){
		if(this.mLocClient!=null){
			this.mLocClient.stop();
			this.mLocClient=null;
		}
		this.mLocClient = new LocationClient(Config.getApplication());
		this.mLocClient.registerLocationListener(this.mListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(Config.LOCATE_INTERVAL);
		option.setAddrType(LocateService.ADDRESS_TYPE);
		this.mLocClient.setLocOption(option);
		this.mLocClient.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
