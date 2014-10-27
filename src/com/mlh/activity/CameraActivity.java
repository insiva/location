package com.mlh.activity;

import java.io.File;
import java.util.Calendar;

import com.mlh.Config;
import com.mlh.R;
import com.mlh.communication.UploadTask;
import com.mlh.component.PictureFragment;
import com.mlh.database.DaoFactory;
import com.mlh.model.Location;
import com.mlh.model.Picture;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;

public class CameraActivity extends Activity {

	private String picName;
	private boolean bWaitForNewLocation;
	private PictureFragment pfPicture;

	BroadcastReceiver receiverLocation = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (!intent.getAction().equals(Location.ACTION_NEW_LOCATION))
				return;
			if (bWaitForNewLocation) {
				bWaitForNewLocation=false;
				startCameraActivity();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		this.bWaitForNewLocation = false;
		IntentFilter filter = new IntentFilter(Location.ACTION_NEW_LOCATION);
		registerReceiver(this.receiverLocation, filter);
		Location loc = Location.getCurrentLocation();
		Config.startLocate();
		if (loc != null) {
			this.startCameraActivity();
		} else {// 获取GPS并获取地址
			this.bWaitForNewLocation = true;
		}
		// this.Upload();
	}

	private void startCameraActivity() {
		Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		it.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		this.picName = DateFormat.format("yyyyMMddHHmmss",
				Calendar.getInstance())
				+ ".jpg";
		// File f = new File(Matteo.TempPicPath);
		File f = new File(Config.getPictureDirectoryPath() + picName);
		it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		startActivityForResult(it, Activity.DEFAULT_KEYS_DIALER);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Picture pic=DaoFactory.getPictureDaoInstance().insert(this.picName,Location.getCurrentLocation().getGuid());
		UploadTask.startUploadTaskThread();
		this.pfPicture = new PictureFragment(pic.getGuid(), true);
		getFragmentManager().beginTransaction()
				.add(R.id.picture_container, this.pfPicture).commit();
	}

	@SuppressLint("NewApi")
	public void continueCapture() {
		getFragmentManager().beginTransaction().remove(this.pfPicture).commit();
		this.startCameraActivity();
	}
}
