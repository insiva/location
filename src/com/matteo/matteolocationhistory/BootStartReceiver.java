package com.matteo.matteolocationhistory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.util.Log;
import android.widget.Toast;

public class BootStartReceiver extends BroadcastReceiver {
	public BootStartReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		 //后边的XXX.class就是要启动的服务  
		GpsService.Start(context);
	}
}
