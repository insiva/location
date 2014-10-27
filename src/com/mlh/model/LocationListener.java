package com.mlh.model;

import android.content.Intent;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.mlh.Config;
import com.mlh.database.DaoFactory;

public class LocationListener  implements BDLocationListener {

	@Override
	public void onReceiveLocation(BDLocation bloc) {
		//Receive Location 
		if (bloc == null)
			return;
		Location loc=DaoFactory.getLocationDaoInstance().insert(bloc);
		Location.setCurrentLocation(loc);
		Intent intent = new Intent();
		intent.setAction(Location.ACTION_NEW_LOCATION);
		Config.getApplication().sendBroadcast(intent);
	}

	@Override
	public void onReceivePoi(BDLocation arg0) {
		// TODO Auto-generated method stub
		
	}

}
