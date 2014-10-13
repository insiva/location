package com.matteo.matteolocationhistory;

import java.util.List;

import com.matteo.matteolocationhistory.model.Conf;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class GpsService extends Service {


	@Override
	public void onCreate() {
		super.onCreate();
		Matteo.Initialize();
		CurrentLocation.StartLocate();
		Conf.V="1";
	}

	public static boolean isServiceRunning() {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) Matteo.ThisApplication
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (serviceList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(
					GpsService.class.getName()) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public static void Start(Context context) {
		if (!GpsService.isServiceRunning()) {
			Intent i = new Intent(context, GpsService.class);
			// i.setAction(ACTION_START);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(i);
		}
	}
}
