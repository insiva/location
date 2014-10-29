package com.mlh;

import android.app.Application;
import com.baidu.mapapi.SDKInitializer;
import com.mlh.communication.UploadTask;

/**
 * @author Matteo
 *������Application��Ӧ���࣬��������ʱ���ã���ʼ��һЩ����
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
	 * ��ʼ�����������в�����ʼ��
	 */
	private void Initialize() {
		if (MApplication.IsInitialized)//ȷ���Ƿ��Ѿ���ʼ��
			return;
		MApplication.IsInitialized = true;
		SDKInitializer.initialize(this);//��ʼ���ٶȵ�ͼSDK
		Config.initialize(this);//��ʼ��Ӧ�ò���
		UploadTask.startUploadTaskThread();//�����ϴ��ļ��߳�
	}

}
