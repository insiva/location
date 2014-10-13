package com.matteo.matteolocationhistory;

import java.io.File;
import java.util.Calendar;

import com.matteo.matteolocationhistory.model.Fun;
import com.matteo.matteolocationhistory.model.Picture;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.widget.ProgressBar;

@SuppressLint("NewApi")
public class SystemCameraActivity extends Activity {

	private String picName;
	private ProgressBar pbWait;
	private boolean AfterCapture;
	private boolean CameraActivityStarted;
	private int picGuid;
	private PictureFragment pfPicture;

	BroadcastReceiver receiverLocation = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (!intent.getAction().equals(Matteo.ActionNewLocation))
				return;
			StartCameraActivity();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_camera);

		this.pbWait = (ProgressBar) this.findViewById(R.id.pbWait);
		this.CameraActivityStarted = false;
		this.AfterCapture=false;
		IntentFilter filter = new IntentFilter(Matteo.ActionNewLocation);
		registerReceiver(this.receiverLocation, filter);
		if (CurrentLocation.NotNull()) {
			this.StartCameraActivity();
		} else {// 获取GPS并获取地址
			this.GetLocation();
		}
		//this.Upload();
	}
	
	private void Upload(){
		final Picture pic1=new Picture(6,false);
		final Picture pic2=new Picture(8,false);
		Thread tr=new Thread(new Runnable()  
        {  
            @Override  
            public void run()  
            {  
            	pic1.BeginUpload();
            	pic2.BeginUpload();
            }  
        });
		tr.start();
	}

	private void GetLocation() {
		CurrentLocation.StartLocate();
	}

	private void StartCameraActivity() {
		if (this.CameraActivityStarted) {
			return;
		}
		Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		it.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		this.picName = DateFormat.format("yyyyMMddHHmmss",
				Calendar.getInstance())
				+ ".jpg";
		// File f = new File(Matteo.TempPicPath);
		File f = new File(Matteo.PictureDirectory + picName);
		it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		startActivityForResult(it, Activity.DEFAULT_KEYS_DIALER);
		this.CameraActivityStarted = true;
	}

	private int InsertPicGps(String picName) {
		String sql = "insert into picture(filename,loc_guid)"
				+ "values('"+ picName+ "',"+ CurrentLocation.getInstance().Guid+")";
		Matteo.MtDb.Execute(sql);
		int g = DbConn.GetMaxGuid("picture");
		return g;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.picGuid = this.InsertPicGps(this.picName);
		this.CameraActivityStarted = false;
		this.AfterCapture=true;
		this.pfPicture = new PictureFragment(this.picGuid,true);
		getFragmentManager().beginTransaction()
				.add(R.id.picture_container, this.pfPicture).commit();
	}
	
	@SuppressLint("NewApi")
	public void ContinueCapture(){
		getFragmentManager().beginTransaction().remove(this.pfPicture).commit();
		this.StartCameraActivity();
	}
}
