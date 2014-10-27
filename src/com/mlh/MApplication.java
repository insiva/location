package com.mlh;

import android.app.Application;
import com.baidu.mapapi.SDKInitializer;
import com.mlh.communication.UploadTask;

public class MApplication extends Application {
	private static boolean IsInitialized = false;

	@Override
	public void onCreate() {
		super.onCreate();
		super.onCreate();
		this.Initialize();
	}

	private void Initialize() {
		if (MApplication.IsInitialized)
			return;
		MApplication.IsInitialized = true;
		SDKInitializer.initialize(this);
		Config.initialize(this);
		UploadTask.startUploadTaskThread();
	}

}
