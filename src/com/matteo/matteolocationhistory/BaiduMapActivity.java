package com.matteo.matteolocationhistory;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfigeration.LocationMode;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class BaiduMapActivity extends Activity {
	private static final String LTAG = BaiduMapActivity.class.getSimpleName();
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private SDKReceiver mReceiver;
	
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	boolean isFirstLoc = true;// 是否首次定位
	

	
	//  鏋勯�犲箍鎾洃鍚被锛岀洃鍚� SDK key 楠岃瘉浠ュ強缃戠粶寮傚父骞挎挱
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//SDKInitializer.initialize(this);
		// 娉ㄥ唽 SDK 骞挎挱鐩戝惉鑰�
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
		
		
		Intent intent = getIntent();
		if (intent.hasExtra("x") && intent.hasExtra("y")) {
			Bundle b = intent.getExtras();
			LatLng p = new LatLng(b.getDouble("y"), b.getDouble("x"));
			mMapView = new MapView(this,
					new BaiduMapOptions().mapStatus(new MapStatus.Builder()
							.target(p).build()));
		} else {
			BaiduMapOptions bmps=new BaiduMapOptions();
			mMapView = new MapView(this, bmps);
		}
		//this.mMapView.set
		

		mCurrentMode = LocationMode.NORMAL;
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		setContentView(mMapView);
		
		
		mBaiduMap = mMapView.getMap();
	}

	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			//Matteo.MtLog("action: " + s);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				//Matteo.MtLog("key 楠岃瘉鍑洪敊! 璇峰湪 AndroidManifest.xml 鏂囦欢涓鏌� key 璁剧疆");
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				//Matteo.MtLog("缃戠粶鍑洪敊");
			}
		}
	}


	@Override
	protected void onPause() {
		super.onPause();
		// activity 閺嗗倸浠犻弮璺烘倱閺冭埖娈忛崑婊冩勾閸ョ偓甯舵禒锟�
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity 閹垹顦查弮璺烘倱閺冭埖浠径宥呮勾閸ョ偓甯舵禒锟�
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// activity 闁匡拷濮ｄ焦妞傞崥灞炬闁匡拷濮ｄ礁婀撮崶鐐付娴狅拷
		mMapView.onDestroy();
	}

	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
			
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
}
