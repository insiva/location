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


/**
 * @author Matteo
 *单例模式，用以执行上传图片、位置信息等操作
 */
@SuppressLint("HandlerLeak")
public class UploadTask extends Thread {
	public static final String UPLOAD_BUNDLE_KEY="UPLOAD_BUNDLE_KEY";
	/**
	 * 两次上传的时间间隔，默认为一个小时
	 */
	public static final long UPLOADING_INTEVAL=60*60*1000;
	
	/**
	 * 一个定时把需要上传的Picture或Location加入上传队列的线程
	 */
	public static Thread SendingThread;
	private static UploadTask task;
	private UploadTask() {
	}

	/**
	 * 上传Handler，采用Looper机制，实时处理其他线程送来的上传信息（图片和位置信息），每当有新消息送达，则分析消息内容进行上传
	 */
	private Handler UploadHandler;


	/**
	 * 上传线程，采用Looper机制，实时处理其他线程送来的上传信息（图片和位置信息），每当有新消息送达，则分析消息内容进行上传
	 */
	@Override
	public void run() {
		Looper.prepare();
		this.UploadHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bundle bdl = msg.getData();
				ITask obj=(ITask)bdl.getParcelable(UploadTask.UPLOAD_BUNDLE_KEY);
				obj.upload();//obj可能是Picture或者Location
				Config.logCurrentThreadID("UploadTask");
			}
		};
		Looper.loop();
	}
	
	/**
	 * 初始化上传线程
	 */
	private static synchronized void initTask(){
		if(UploadTask.task==null){
			UploadTask.task=new UploadTask();
			UploadTask.task.start();
		}
	}
	
	/**
	 * 取得上传线程单例
	 */
	public static UploadTask getInstance(){
		if(UploadTask.task==null){
			UploadTask.initTask();
		}
		return UploadTask.task;
	}
	

	/**
	 * 将一个需要上传的实例（Picture或者Location）加入上传队列，由上传Handler进行处理
	 */
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
	
	/**
	 * 将没有上传的Picture和Location加入上传队列
	 */
	private static void sendUnUploadedData(){
		Album.sendUnUploadedToUploadTask();
		Route.sendUnUploadedToUploadTask();
	}
	
	/**
	 * 启动定时上传线程UploadTask.SendingThread
	 */
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
