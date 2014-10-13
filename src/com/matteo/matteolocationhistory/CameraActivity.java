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
	// 定义系统所用的照相机
	Camera camera;
	// 是否在预览中
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
		// 设置全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_camera);
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// 获取窗口管理器

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

		// 获取界面中SurfaceView组件
		sView = (SurfaceView) findViewById(R.id.sView);
		// 设置该Surface不需要自己维护缓冲区
		sView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// 获得SurfaceView的SurfaceHolder
		surfaceHolder = sView.getHolder();
		// 为surfaceHolder添加一个回调监听器
		surfaceHolder.addCallback(new Callback() {
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// 打开摄像头
				initCamera();
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// 如果camera不为null ,释放摄像头
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
			// 此处默认打开后置摄像头。
			// 通过传入参数可以打开前置摄像头
			camera = Camera.open(0); // ①
			camera.setDisplayOrientation(90);
		}
		if (camera != null && !isPreview) {
			try {
				Camera.Parameters parameters = camera.getParameters();
				// 设置预览照片的大小
				parameters.setPreviewSize(Matteo.ScreenWidth, Matteo.ScreenHeight);
				// 设置预览照片时每秒显示多少帧的最小值和最大值
				parameters.setPreviewFpsRange(4, 10);
				// 设置图片格式
				parameters.setPictureFormat(ImageFormat.JPEG);
				// 设置JPG照片的质量
				parameters.set("jpeg-quality", 85);
				// 设置照片的大小
				Size ps=parameters.getSupportedPictureSizes().get(0);
				parameters.setPictureSize(ps.width, ps.height);
				// 通过SurfaceView显示取景画面
				camera.setPreviewDisplay(surfaceHolder); // ②
				// 开始预览
				camera.startPreview(); // ③
			} catch (Exception e) {
				e.printStackTrace();
			}
			isPreview = true;
		}
	}

	public void capture(View source) {
		if (camera != null) {
			// 控制摄像头自动对焦后才拍照
			camera.autoFocus(autoFocusCallback); // ④
		}
	}

	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
		// 当自动对焦时激发该方法
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if (success) {
				// takePicture()方法需要传入3个监听器参数
				// 第1个监听器：当用户按下快门时激发该监听器
				// 第2个监听器：当相机获取原始照片时激发该监听器
				// 第3个监听器：当相机获取JPG照片时激发该监听器
				camera.takePicture(new ShutterCallback() {
					public void onShutter() {
						// 按下快门瞬间会执行此处代码
					}
				}, new PictureCallback() {
					public void onPictureTaken(byte[] data, Camera c) {
						// 此处代码可以决定是否需要保存原始照片信息
					}
				}, myJpegCallback); // ⑤
			}
		}
	};

	PictureCallback myJpegCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// 根据拍照所得的数据创建位图
			final Bitmap bm = BitmapFactory.decodeByteArray(data, 0,
					data.length);
			String picName=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".jpg";
			File file = new File(Matteo.PictureDirectory, picName);
			FileOutputStream outStream = null;
			try {
				// 打开指定文件对应的输出流
				outStream = new FileOutputStream(file);
				// 把位图输出到指定文件中
				bm.compress(CompressFormat.JPEG, 100, outStream);
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 重新浏览
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
