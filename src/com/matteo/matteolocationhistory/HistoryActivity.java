package com.matteo.matteolocationhistory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.model.LatLng;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HistoryActivity extends LockActivity{

	MapView mapHistory;
	BaiduMap bmHistory;
	UiSettings uisHistory;
	MatteoHistory h;
	TextView tvStartDay, tvEndDay;
	//RelativeLayout rlHistory;
	HistoryPathView hpvHistory;
	boolean HistoryPathViewContained;
	ArrayList<Marker> arrayMarker;
	private InfoWindow iwLocation;
	@SuppressLint("HandlerLeak")
	public Handler hdlHistory = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == LockScreen.FlagLockEnd) {
				hpvHistory.SetHistory(h);
				if (!HistoryPathViewContained) {
					lyActivity.addView(hpvHistory, hpvHistory.ViewParams);
				}
				if (h.size() > 1) {
					hpvHistory.setVisibility(View.VISIBLE);
				} else {
					hpvHistory.setVisibility(View.INVISIBLE);
				}
				lyActivity.removeView(v);
				lyActivity.removeView(v.pbWait);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		this.tvEndDay = (TextView) this.findViewById(R.id.tvEndDay);
		this.tvStartDay = (TextView) this.findViewById(R.id.tvStartDay);
		this.lyActivity = (RelativeLayout) this.findViewById(R.id.rlHistory);
		h = new MatteoHistory();
		mapHistory = (MapView) findViewById(R.id.mapHistory);
		this.arrayMarker = new ArrayList<Marker>();
		this.HistoryPathViewContained = false;
		this.hpvHistory = new HistoryPathView(this);
		this.InitButtons();
		this.lyActivity.addView(hpvHistory, hpvHistory.ViewParams);
		this.HistoryPathViewContained=true;
		this.Init();
		//LockScreen.Lock(this);
	}

	private void Init() {
		this.InitializeMap();
		this.InitLocations();
		this.InitMarkerButton();
		this.InitHistoryPathView();
		this.bmHistory.hideInfoWindow();
	}

	private void InitializeMap() {
		bmHistory = mapHistory.getMap();
		this.uisHistory = this.bmHistory.getUiSettings();
		this.uisHistory.setOverlookingGesturesEnabled(false);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(h.CenterLatlng);
		this.bmHistory.setMapStatus(msu);
		float z = h.GetZoom();
		msu = MapStatusUpdateFactory.zoomTo(z);
		this.bmHistory.setMapStatus(msu);
	}

	private void InitLocations() {
		this.ClearMarkers();
		if(this.h.size()<1)return;
		BitmapDescriptor bdA = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_mark);
		LatLng ll = null;
		Marker mr = null;
		OverlayOptions oo = null;
		for (MatteoLocation mloc : h) {
			oo = new MarkerOptions().position(mloc.Coordiante).icon(bdA)
					.zIndex(9);
			mr = (Marker) (this.bmHistory.addOverlay(oo));
			this.arrayMarker.add(mr);
		}
	}

	private void InitButtons() {
		this.tvStartDay.setText(MatteoHistory.DayFormatter2.format(h.StartDay));
		this.tvEndDay.setText(MatteoHistory.DayFormatter2.format(h.GetEndDay()));
		this.tvStartDay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar cld = Calendar.getInstance();
				cld.setTime(h.StartDay);
				new DatePickerDialog(HistoryActivity.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker dp, int year,
									int month, int dayOfMonth) {
								String d = year + "-" + (month + 1) + "-"
										+ dayOfMonth;
								try {
									h.SetDaySpan(
											MatteoHistory.DayFormatter.parse(d),
											h.EndDay);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								tvStartDay.setText(d);
								Init();
							}
						}, cld.get(Calendar.YEAR), cld.get(Calendar.MONTH), cld
								.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		this.tvEndDay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar cld = Calendar.getInstance();
				cld.setTime(h.GetEndDay());
				new DatePickerDialog(HistoryActivity.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker dp, int year,
									int month, int dayOfMonth) {
								String d = year + "-" + (month + 1) + "-"
										+ dayOfMonth;
								// tvEndDay.setText(d);
								try {
									h.SetDaySpan(h.StartDay,
											MatteoHistory.DayFormatter.parse(d));
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								tvEndDay.setText(d);
								Init();
							}
						}, cld.get(Calendar.YEAR), cld.get(Calendar.MONTH), cld
								.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
	}

	private void InitHistoryPathView() {
		hpvHistory.SetHistory(h);
		if (!HistoryPathViewContained) {
			lyActivity.addView(hpvHistory, hpvHistory.ViewParams);
		}
		if (h.size() > 1) {
			hpvHistory.setVisibility(View.VISIBLE);
		} else {
			hpvHistory.setVisibility(View.INVISIBLE);
		}
	}

	private void InitMarkerButton() {
		this.bmHistory.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				Point p = bmHistory.getProjection().toScreenLocation(
						marker.getPosition());
				if (p.x > 1100 || p.x < 180 || p.y > 720 || p.y < 200) {
					MapStatusUpdate msu = MapStatusUpdateFactory
							.newLatLng(marker.getPosition());
					bmHistory.setMapStatus(msu);
				}
				int i = arrayMarker.indexOf(marker);
				if (hpvHistory != null)
					hpvHistory.SetTimeIndex(i);
				return PopupButton(marker);
			}
		});
	}

	private boolean PopupButton(final Marker marker) {
		bmHistory.hideInfoWindow();
		int i = this.arrayMarker.indexOf(marker);
		Button button = new Button(getApplicationContext());
		button.setTextColor(Color.BLACK);
		button.setBackgroundResource(R.drawable.popup);
		final LatLng ll = marker.getPosition();
		Point p = bmHistory.getProjection().toScreenLocation(ll);

		String s = h.get(i).GetDescription();
		p.y -= 17;
		p.x-=5;
		LatLng llInfo = bmHistory.getProjection().fromScreenLocation(p);
		button.setText(s);
		OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
			public void onInfoWindowClick() {
				bmHistory.hideInfoWindow();
			}
		};
		iwLocation = new InfoWindow(button, llInfo, listener);
		bmHistory.showInfoWindow(iwLocation);
		return true;
	}

	public boolean PopupButton(int i) {
		Marker marker = this.arrayMarker.get(i);
		Point p = bmHistory.getProjection().toScreenLocation(
				marker.getPosition());
		if (p.x > 1100 || p.x < 180 || p.y > 720 || p.y < 200) {
			MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(marker
					.getPosition());
			bmHistory.setMapStatus(msu);
		}
		return this.PopupButton(marker);
	}
	
	private void ClearMarkers(){
		for(Marker m:this.arrayMarker){
			m.remove();
		}
		this.arrayMarker.clear();
	}

	@Override
	public void Do() {
		// TODO Auto-generated method stub
		this.Init();
	}

	@Override
	public void SendMessage() {
		// TODO Auto-generated method stub
		
	}
}
