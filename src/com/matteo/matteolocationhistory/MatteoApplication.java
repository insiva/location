
package com.matteo.matteolocationhistory;

import android.app.Application;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.matteo.matteolocationhistory.model.Fun;
//import com.baidu.mapapi.SDKInitializer;
import com.matteo.matteolocationhistory.model.UploadThread;

public class MatteoApplication extends Application {
	public static int C=0;
	@Override
	public void onCreate() {
		if(C==1)
		{
			//throw new RuntimeException();
		}
		C++;
		super.onCreate();
		Matteo.ThisApplication=this;
		// ��ʹ�� SDK �����֮ǰ��ʼ�� context ��Ϣ������ ApplicationContext
		SDKInitializer.initialize(this);
		super.onCreate();
		this.Initialize();
		Matteo.Log("Application Start");
	}
	
	private void Initialize(){
		Matteo.Initialize();
		GpsService.Start(this);
		MatteoApplication.ApplicationInitEnd();
	}
	
	public static void ApplicationInitEnd()
	{
		//Fun.Log("Application Uploading!");
		UploadThread.getInstance();
		UploadThread.UploadData();
	}

}

