package com.matteo.matteolocationhistory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends Activity implements
		OnGetGeoCoderResultListener {
	SurfaceView sView;
	SurfaceHolder surfaceHolder;
	// ����ϵͳ���õ������
	Camera camera;
	// �Ƿ���Ԥ����
	boolean isPreview = false;
	private TextView tvAddress;
	private ProgressBar pbWait;
	GeoCoder mSearch = null;
	BroadcastReceiver receiverLocation = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (!intent.getAction().equals(Matteo.ActionNewLocation))
				return;
			tvAddress.setVisibility(View.INVISIBLE);
			pbWait.setVisibility(View.VISIBLE);
			LatLng ll = new LatLng(CurrentLocation.getInstance().Latitude,
					CurrentLocation.getInstance().Longitude);
			mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
		}
	};

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����ȫ��
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_camera);
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// ��ȡ���ڹ�����

		this.tvAddress = (TextView) this.findViewById(R.id.tvAddress);
		this.pbWait = (ProgressBar) this.findViewById(R.id.pbWait);

		if (CurrentLocation.NotNull()) {
			mSearch.reverseGeoCode(new ReverseGeoCodeOption()
					.location(new LatLng(
							CurrentLocation.getInstance().Latitude,
							CurrentLocation.getInstance().Longitude)));
		}
		IntentFilter filter = new IntentFilter(Matteo.ActionNewLocation);
		registerReceiver(this.receiverLocation, filter);

		mSearch = GeoCoder.newInstance();
		// this.mMgcr=new MatteoGeoCoderResultlister();
		mSearch.setOnGetGeoCodeResultListener(this);

		WindowManager wm = getWindowManager();
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();

		// ��ȡ������SurfaceView���
		sView = (SurfaceView) findViewById(R.id.sView);
		// ���ø�Surface����Ҫ�Լ�ά��������
		sView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// ���SurfaceView��SurfaceHolder
		surfaceHolder = sView.getHolder();
		// ΪsurfaceHolder���һ���ص�������
		surfaceHolder.addCallback(new Callback() {
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// ������ͷ
				initCamera();
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// ���camera��Ϊnull ,�ͷ�����ͷ
				if (camera != null) {
					if (isPreview)
						camera.stopPreview();
					camera.release();
					camera = null;
				}
			}
		});
	}

	@SuppressLint("NewApi")
	private void initCamera() {
		if (!isPreview) {
			// �˴�Ĭ�ϴ򿪺�������ͷ��
			// ͨ������������Դ�ǰ������ͷ
			camera = Camera.open(0); // ��
			camera.setDisplayOrientation(90);
		}
		if (camera != null && !isPreview) {
			try {
				Camera.Parameters parameters = camera.getParameters();
				// ����Ԥ����Ƭ�Ĵ�С
				parameters.setPreviewSize(Matteo.ScreenWidth, Matteo.ScreenHeight);
				// ����Ԥ����Ƭʱÿ����ʾ����֡����Сֵ�����ֵ
				parameters.setPreviewFpsRange(4, 10);
				// ����ͼƬ��ʽ
				parameters.setPictureFormat(ImageFormat.JPEG);
				// ����JPG��Ƭ������
				parameters.set("jpeg-quality", 85);
				// ������Ƭ�Ĵ�С
				Size ps=parameters.getSupportedPictureSizes().get(0);
				parameters.setPictureSize(ps.width, ps.height);
				// ͨ��SurfaceView��ʾȡ������
				camera.setPreviewDisplay(surfaceHolder); // ��
				// ��ʼԤ��
				camera.startPreview(); // ��
			} catch (Exception e) {
				e.printStackTrace();
			}
			isPreview = true;
		}
	}

	public void capture(View source) {
		if (camera != null) {
			// ��������ͷ�Զ��Խ��������
			camera.autoFocus(autoFocusCallback); // ��
		}
	}

	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
		// ���Զ��Խ�ʱ�����÷���
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if (success) {
				// takePicture()������Ҫ����3������������
				// ��1�������������û����¿���ʱ�����ü�����
				// ��2�����������������ȡԭʼ��Ƭʱ�����ü�����
				// ��3�����������������ȡJPG��Ƭʱ�����ü�����
				camera.takePicture(new ShutterCallback() {
					public void onShutter() {
						// ���¿���˲���ִ�д˴�����
					}
				}, new PictureCallback() {
					public void onPictureTaken(byte[] data, Camera c) {
						// �˴�������Ծ����Ƿ���Ҫ����ԭʼ��Ƭ��Ϣ
					}
				}, myJpegCallback); // ��
			}
		}
	};

	PictureCallback myJpegCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// �����������õ����ݴ���λͼ
			final Bitmap bm = BitmapFactory.decodeByteArray(data, 0,
					data.length);
			String picName=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".jpg";
			File file = new File(Matteo.PictureDirectory, picName);
			FileOutputStream outStream = null;
			try {
				// ��ָ���ļ���Ӧ�������
				outStream = new FileOutputStream(file);
				// ��λͼ�����ָ���ļ���
				bm.compress(CompressFormat.JPEG, 100, outStream);
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// �������
			camera.stopPreview();
			camera.startPreview();
			isPreview = true;
			InsertPicGps(picName);
		}
	};
	
	private void InsertPicGps(String picName){
/*		String sql="insert into picture(filename,loc_guid,province,city,district,street,streetnum)"
				+ "values('"+picName+"',"+CurrentLocation.getInstance().Guid
				+",'"+CurrentLocation.getAddresComponent().province+"','"
				+CurrentLocation.getAddresComponent().city+"','"+CurrentLocation.getAddresComponent().district+"','"
				+CurrentLocation.getAddresComponent().street+"','"+CurrentLocation.getAddresComponent().streetNumber+"')";
		Matteo.MtDb.Execute(sql);*/
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			return;
		}
		this.pbWait.setVisibility(View.INVISIBLE);
		this.tvAddress.setVisibility(View.VISIBLE);
		this.tvAddress.setText(result.getAddress());
	}
}
