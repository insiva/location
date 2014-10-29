package com.mlh.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.mlh.Config;
import com.mlh.R;
import com.mlh.communication.HttpConnnection;
import com.mlh.component.RouteView;
import com.mlh.database.DaoFactory;
import com.mlh.model.Album;
import com.mlh.model.Location;
import com.mlh.model.Picture;
import com.mlh.model.Route;
import com.mlh.model.Trajectory;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Matteo
 *首页Activity，默认显示照片地图和历史位置地图
 */
@SuppressLint("InflateParams")
public class HomeActivity extends Activity implements OnClickListener,
		OnMarkerClickListener, OnMapClickListener {

	public static final int VIEW_WIDTH = 700, VIEW_HEIGHT = 1000;
	MapView mapHistory;
	BaiduMap bmHistory;
	UiSettings uisHistory;
	RelativeLayout rlDayPicker, rlPlay, rlStop, rlHome;
	LinearLayout llStatus, llCapture, llList, llNav;
	RouteView routeView;
	// TrajectoryView trajView;
	boolean RlDayPickerClecked = false, RlPlayClicked = false;
	private PopupWindow pwDayPicker;
	private PopupWindow pwStop;
	private TextView tvStartDay, tvEndDay, tvStatus;
	private MtDay StartDay, EndDay;
	private DatePickerDialog dpdDay;
	private Button btnConfirmDay;
	private MapStatus Status = MapStatus.Picture;
	private Album mAlbum;
	private Route mRoute;
	private ArrayList<Marker> MarkerArray;
	private InfoWindow iwLocation;
	public boolean TrajViewCanTouch;

	// public static Trajectory PlayTraj;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		this.Init();
		Config.logCurrentThreadID("HomeActivity");
	}

	private void Init() {
		// this.htHome=new HomeTask(TaskType.XML);
		this.rlHome = (RelativeLayout) this.findViewById(R.id.rlHome);
		this.rlDayPicker = (RelativeLayout) this.findViewById(R.id.rlDayPicker);
		this.rlPlay = (RelativeLayout) this.findViewById(R.id.rlPlay);
		this.rlDayPicker.setOnClickListener(this);
		this.rlPlay.setOnClickListener(this);
		this.getPwDaypickerInstance();
		this.getPwStopInstance();

		this.tvStatus = (TextView) this.findViewById(R.id.tvStatus);
		this.llStatus = (LinearLayout) this.findViewById(R.id.llStatus);
		this.llStatus.setOnClickListener(this);
		this.llCapture = (LinearLayout) this.findViewById(R.id.llCapture);
		this.llCapture.setOnClickListener(this);
		this.llList = (LinearLayout) this.findViewById(R.id.llList);
		this.llList.setOnClickListener(this);
		this.llNav = (LinearLayout) this.findViewById(R.id.llNav);

		Date d = new Date();
		this.EndDay = new MtDay(d);
		this.StartDay = new MtDay(new Date(d.getTime() - 7 * 24 * 3600 * 1000));

		this.MarkerArray = new ArrayList<Marker>();
		// this.mRoute = new Route();
		// this.mAlbum = new ArrayList<Picture>();

		this.getTrajViewInstance();

		this.rlPlay.setVisibility(View.INVISIBLE);
		this.initMap();
		this.load();
	}

	private void initMap() {
		mapHistory = (MapView) findViewById(R.id.mapHistory);
		bmHistory = mapHistory.getMap();
		this.uisHistory = this.bmHistory.getUiSettings();
		this.uisHistory.setOverlookingGesturesEnabled(false);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(Config.BEIJING_LATLNG);
		this.bmHistory.setMapStatus(msu);
		float z = 13;// Matteo.BaiduAKh.GetZoom();
		msu = MapStatusUpdateFactory.zoomTo(z);
		this.bmHistory.setMapStatus(msu);
		this.bmHistory.setOnMarkerClickListener(this);
		this.bmHistory.setOnMapClickListener(this);
	}

	public void load() {
		this.clearAll();
		new HomeTask().execute();
	}

	private void loadPictureMarker(Picture pic) {
		Bitmap bm = pic.fetchImage(Picture.ICON_PICTURE_FLAG);
		BitmapDescriptor bdA = null;
		if (bm == null) {
			bdA = BitmapDescriptorFactory.fromResource(R.drawable.picicon);
		} else {
			bdA = BitmapDescriptorFactory.fromBitmap(bm);
		}

		OverlayOptions oo = new MarkerOptions().position(pic.getLatLng())
				.icon(bdA).zIndex(9);
		Marker mr = (Marker) (this.bmHistory.addOverlay(oo));
		this.MarkerArray.add(mr);
	}

	private void loadLocationMarker(Location loc) {
		BitmapDescriptor bdA = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_mark);
		OverlayOptions oo = new MarkerOptions().position(loc.getLatLng())
				.icon(bdA).zIndex(9);
		Marker mr = (Marker) (this.bmHistory.addOverlay(oo));
		this.MarkerArray.add(mr);
	}

	private void clearAll() {
		this.clearMarkers();
		// this.mAlbum.clear();
		// this.mRoute.clear();
	}

	private void clearMarkers() {
		for (Marker m : this.MarkerArray) {
			m.remove();
		}
		this.MarkerArray.clear();
		this.clearPopupWindow();
	}

	private boolean popupLocationButton(final Marker marker) {
		bmHistory.hideInfoWindow();
		int i = this.MarkerArray.indexOf(marker);
		Button button = new Button(getApplicationContext());
		button.setTextColor(Color.BLACK);
		button.setBackgroundResource(R.drawable.popup);
		final LatLng ll = marker.getPosition();
		Point p = bmHistory.getProjection().toScreenLocation(ll);

		p.y -= 17;
		p.x -= 5;
		LatLng llInfo = bmHistory.getProjection().fromScreenLocation(p);
		button.setText(this.mRoute.get(i).getDescription());
		OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
			public void onInfoWindowClick() {
				bmHistory.hideInfoWindow();
			}
		};
		iwLocation = new InfoWindow(button, llInfo, listener);
		bmHistory.showInfoWindow(iwLocation);
		return true;
	}

	public boolean popupLocationButton(int i) {
		Marker marker = this.MarkerArray.get(i);
		Point p = bmHistory.getProjection().toScreenLocation(
				marker.getPosition());
		if (p.y > 1100 || p.y < 180 || p.x > 720 || p.x < 200) {
			MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(marker
					.getPosition());
			bmHistory.setMapStatus(msu);
		}
		return this.popupLocationButton(marker);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rlDayPicker:
			this.clickRlDayPicker();
			break;
		case R.id.rlPlay:
			this.clickRlPlay();
			break;
		case R.id.tvStartDay:
			this.clickTvDay(R.id.tvStartDay);
			break;
		case R.id.tvEndDay:
			this.clickTvDay(R.id.tvEndDay);
			break;
		case R.id.btnConfirmDay:
			this.clickBtnConfirmDay();
			break;
		case R.id.llStatus:
			this.clickLlStatus();
			break;
		case R.id.llCapture:
			this.clickLlCapture();
			break;
		case R.id.llList:
			this.clickLlList();
			break;
		default:
			break;
		}
	}

	private void clickRlDayPicker() {
		if (this.RlDayPickerClecked) {
			this.rlDayPicker
					.setBackgroundResource(R.drawable.daypickerunclicked);
			this.pwDayPicker.dismiss();
		} else {
			this.rlDayPicker.setBackgroundResource(R.drawable.daypickerclicked);
			this.pwDayPicker.showAtLocation(this.rlDayPicker, Gravity.LEFT
					| Gravity.BOTTOM, this.rlDayPicker.getLeft(),
					this.rlDayPicker.getHeight() + 18);
		}
		this.tvStartDay.setText(this.StartDay.toString());
		this.tvEndDay.setText(this.EndDay.toString());
		this.RlDayPickerClecked = !this.RlDayPickerClecked;
	}

	private void clickRlPlay() {
		if (!this.pwStop.isShowing()) {
			this.pwDayPicker.dismiss();
		}
		Intent intent = new Intent(HomeActivity.this, TraceActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable(Route.ROUTE, this.mRoute);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void clickTvDay(int vid) {
		MtDay d;
		if (this.tvStartDay.getId() == vid) {
			d = new MtDay(this.tvStartDay.getText().toString());
		} else {
			d = new MtDay(this.tvEndDay.getText().toString());
		}
		this.dpdDay = new DatePickerDialog(this, new MtOnDateSetListener(vid),
				d.Year, d.Month, d.Day);
		this.dpdDay.show();
	}

	private void clickBtnConfirmDay() {
		this.StartDay.init(this.tvStartDay.getText().toString());
		this.EndDay.init(this.tvEndDay.getText().toString());
		this.clearDayPicker();
		this.clearAll();
		this.clearPopupWindow();
		this.load();
	}

	private void clickLlStatus() {
		if (this.Status == MapStatus.Picture) {
			this.Status = MapStatus.Trajectory;
			this.tvStatus.setText(Config.getString(R.string.Picture));
			this.routeView.setVisibility(View.VISIBLE);
			this.rlPlay.setVisibility(View.VISIBLE);
		} else {
			this.Status = MapStatus.Picture;
			this.tvStatus.setText(Config.getString(R.string.Trajectory));
			this.routeView.setVisibility(View.INVISIBLE);
			this.rlPlay.setVisibility(View.INVISIBLE);
		}
		this.load();
	}

	private void clickLlCapture() {
		Intent intent = new Intent(HomeActivity.this, CameraActivity.class);
		startActivity(intent);
	}

	private void clickLlList() {
		Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
		startActivity(intent);
	}

	@SuppressLint("InflateParams")
	private void getPwDaypickerInstance() {
		if (this.pwDayPicker == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			View popupWindow = layoutInflater.inflate(
					R.layout.layout_daypicker, null);

			this.tvEndDay = (TextView) popupWindow.findViewById(R.id.tvEndDay);
			this.tvStartDay = (TextView) popupWindow
					.findViewById(R.id.tvStartDay);
			this.tvEndDay.setOnClickListener(this);
			this.tvStartDay.setOnClickListener(this);
			this.btnConfirmDay = (Button) popupWindow
					.findViewById(R.id.btnConfirmDay);
			this.btnConfirmDay.setOnClickListener(this);

			this.pwDayPicker = new PopupWindow(popupWindow, 400, 160);
			this.pwDayPicker.setAnimationStyle(R.style.AnimDaypicker);
		}
	}

	private void getPwStopInstance() {
		if (this.pwStop == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			View popupWindow = layoutInflater.inflate(R.layout.layout_stop,
					null);
			this.rlStop = (RelativeLayout) popupWindow
					.findViewById(R.id.rlStop);
			this.rlStop.setOnClickListener(this);
			this.pwStop = new PopupWindow(popupWindow, 80, 80);
			this.pwStop.setAnimationStyle(R.style.AnimStop);
		}
	}

	private void getTrajViewInstance() {
		this.routeView = new RouteView(this);
		this.rlHome.addView(this.routeView, this.routeView.getLayoutParams());
		this.routeView.setVisibility(View.INVISIBLE);
	}

	public class MtOnDateSetListener implements OnDateSetListener {
		public int TvDayID;

		public MtOnDateSetListener(int vid) {
			this.TvDayID = vid;
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			String d = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
			if (this.TvDayID == R.id.tvStartDay) {
				tvStartDay.setText(d);
			} else {
				tvEndDay.setText(d);
			}
		}
	}

	@SuppressLint("SimpleDateFormat")
	public class MtDay {
		public int Year, Month, Day;
		public static final String DAY_DATEFORMAT="yyyy-M-d";
		public final SimpleDateFormat DayFormatter=new SimpleDateFormat(MtDay.DAY_DATEFORMAT);
		public MtDay(Date d) {
			Calendar cld = Calendar.getInstance();
			cld.setTime(d);
			this.init(cld);
		}

		public MtDay(String s) {
			this.init(s);
		}

		public void init(String s) {
			try {
				Date d = this.DayFormatter.parse(s);
				Calendar cld = Calendar.getInstance();
				cld.setTime(d);
				this.init(cld);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public String toString() {
			String s = this.Year + "-" + (this.Month + 1) + "-" + this.Day;
			return s;
		}

		private void init(Calendar d) {
			this.Year = d.get(Calendar.YEAR);
			this.Month = d.get(Calendar.MONTH);
			this.Day = d.get(Calendar.DAY_OF_MONTH);
		}
	}

	public enum MapStatus {
		Picture, Trajectory
	}

	public class HomeTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			this.loadXmlTask();
			return null;
		}

		@Override
		public void onProgressUpdate(Void... voids) {

			if (isCancelled())
				return;
			if (Status == MapStatus.Trajectory) {
				if (mRoute.size() > 1) {
					routeView.setVisibility(View.VISIBLE);
					routeView.setRoute(mRoute);
					rlPlay.setVisibility(View.VISIBLE);
				} else {
					routeView.setVisibility(View.INVISIBLE);
					rlPlay.setVisibility(View.INVISIBLE);
				}
				LatLngBounds.Builder b = new LatLngBounds.Builder();
				for(Trajectory traj:mRoute){
					loadLocationMarker(traj);
					b.include(traj.getLatLng());
				}
				LatLngBounds llb = b.build();
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngBounds(
						llb, HomeActivity.VIEW_WIDTH, HomeActivity.VIEW_HEIGHT);
				bmHistory.setMapStatus(msu);
			}
		}

		private void loadXmlTask() {
			if (Status == MapStatus.Picture) {
				this.loadPicture();
				LatLngBounds.Builder b = new LatLngBounds.Builder();
				for (Picture pic : mAlbum) {
					loadPictureMarker(pic);
					b.include(pic.getLatLng());
				}
				LatLngBounds llb = b.build();
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngBounds(
						llb, HomeActivity.VIEW_WIDTH, HomeActivity.VIEW_HEIGHT);
				bmHistory.setMapStatus(msu);
			} else {
				this.loadRoute();
			}
			this.publishProgress();
		}

		private void loadPicture() {
			String u = Picture.PICTURE_HISTORY_PAGE + "?sd="
					+ HttpConnnection.encode(StartDay.toString()) + "&ed="
					+ HttpConnnection.encode(EndDay.toString());
			String xs = HttpConnnection.getXml(u);
			mAlbum = DaoFactory.getAlbumDaoInstance().getByXmlString(xs);
		}

		private void loadRoute() {
			String u = Route.PAGE_ROUTE_HISTORY + "?sd="
					+ HttpConnnection.encode(StartDay.toString()) + "&ed="
					+ HttpConnnection.encode(EndDay.toString());
			String xs = HttpConnnection.getXml(u);
			mRoute = DaoFactory.getRouteDaoInstance().getByXmlString(xs);
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		this.clearDayPicker();
		if (this.Status == MapStatus.Trajectory) {
			Point p = bmHistory.getProjection().toScreenLocation(
					marker.getPosition());
			if (p.y > 1100 || p.y < 180 || p.x > 720 || p.x < 200) {
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(marker
						.getPosition());
				bmHistory.setMapStatus(msu);
			}
			int i = this.MarkerArray.indexOf(marker);
			if (this.routeView != null)
				this.routeView.setTimeIndex(i);
			return this.popupLocationButton(marker);
		} else {
			int i = this.MarkerArray.indexOf(marker);
			Intent intent = new Intent(HomeActivity.this,
					AlbumActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(Album.ALBUM_INDEX, i);
			bundle.putParcelable(Album.ALBUM,  this.mAlbum);
			intent.putExtras(bundle);
			startActivity(intent);
			return true;
		}
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		this.clearPopupWindow();
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	private void clearPopupWindow() {
		if (this.bmHistory != null)
			this.bmHistory.hideInfoWindow();
		this.clearDayPicker();
	}

	private void clearDayPicker() {
		// this.ClearMarkers();
		// this.ClearPopupWindow();
		this.RlDayPickerClecked = false;
		this.rlDayPicker.setBackgroundResource(R.drawable.daypickerunclicked);
		this.pwDayPicker.dismiss();
	}
}
