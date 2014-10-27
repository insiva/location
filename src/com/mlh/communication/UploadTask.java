package com.mlh.communication;

import com.mlh.Config;
import com.mlh.model.Album;
import com.mlh.model.Route;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;


@SuppressLint("HandlerLeak")
public class UploadTask extends Thread {
	public static final String UPLOAD_BUNDLE_KEY="UPLOAD_BUNDLE_KEY";
	public static final long UPLOADING_INTEVAL=60*60*1000;
	public static Thread SendingThread;
	private static UploadTask task;
	private UploadTask() {
	}

	private Handler UploadHandler;
	
	@Override
	public void run() {
		Looper.prepare();
		this.UploadHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bundle bdl = msg.getData();
				ITask obj=(ITask)bdl.getParcelable(UploadTask.UPLOAD_BUNDLE_KEY);
				obj.upload();
			}
		};
		Looper.loop();
	}
	
	private static synchronized void initTask(){
		if(UploadTask.task==null){
			UploadTask.task=new UploadTask();
			UploadTask.task.start();
		}
	}
	
	public static UploadTask getInstance(){
		if(UploadTask.task==null){
			UploadTask.initTask();
		}
		return UploadTask.task;
	}
	

	public static void addToUploadingQueue(ITask u) {
		UploadTask ut = UploadTask.getInstance();
		while (ut.UploadHandler == null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
		Message msg=new Message();
		Bundle bdl=new Bundle();
		Parcelable p=(Parcelable)u;
		bdl.putParcelable(UploadTask.UPLOAD_BUNDLE_KEY, p);
		msg.setData(bdl);
		ut.UploadHandler.sendMessage(msg);
	}
	
	private static void sendUnUploadedData(){
		Album.sendUnUploadedToUploadTask();
		Route.sendUnUploadedToUploadTask();
	}
	
	public static void startUploadTaskThread(){
		UploadTask.initTask();
		if (UploadTask.SendingThread != null) {
			UploadTask.SendingThread.interrupt();
			UploadTask.SendingThread = null;
		}
		UploadTask.SendingThread = new Thread() {
			@Override
			public void run() {
				while (!isInterrupted()) {
					if (Config.getWifiState()) {
						UploadTask.sendUnUploadedData();
					}
					try {
						sleep(UploadTask.UPLOADING_INTEVAL);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
				}
			}
		};
		UploadTask.SendingThread.start();
	}
}
