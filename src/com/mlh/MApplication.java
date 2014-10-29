package com.mlh;

import android.app.Application;
import com.baidu.mapapi.SDKInitializer;
import com.mlh.communication.UploadTask;

/**
 * @author Matteo
 *派生自Application的应用类，程序启动时调用，初始化一些参数
 */
public class MApplication extends Application {
	private static boolean IsInitialized = false;

	@Override
	public void onCreate() {
		super.onCreate();
		super.onCreate();
		this.Initialize();
	}

	/**
	 * 初始化函数，进行参数初始化
	 */
	private void Initialize() {
		if (MApplication.IsInitialized)//确定是否已经初始化
			return;
		MApplication.IsInitialized = true;
		SDKInitializer.initialize(this);//初始化百度地图SDK
		Config.initialize(this);//初始化应用参数
		UploadTask.startUploadTaskThread();//开启上传文件线程
	}

}
