package com.matteo.matteolocationhistory;

import java.util.Date;

import android.content.Intent;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.matteo.matteolocationhistory.model.Fun;
import com.matteo.matteolocationhistory.model.MtLocation;

public class MatteoLocationListener implements BDLocationListener {

	public MatteoLocationListener(){
	}
	
	@Override
	public void onReceiveLocation(BDLocation location) {
		//Receive Location 
		if (location == null)
			return;
		//this.Print(location);
		this.InsertGpsData(location);
	}
	

	private void InsertGpsData(BDLocation loc) {
		// this.et.setText(Double.toString(this.mtLocation.getLongitude())+","+Double.toString(this.mtLocation.getLatitude()));
		MtLocation.Insert(loc);
	}
	
	@Override
	public void onReceivePoi(BDLocation arg0) {
		// TODO Auto-generated method stub
		
	}
}