package com.matteo.matteolocationhistory;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
import com.matteo.matteolocationhistory.model.Conf;
import com.matteo.matteolocationhistory.model.Fun;
import com.matteo.matteolocationhistory.model.Picture;
import com.matteo.matteolocationhistory.model.PicListAdapter.PictureHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PicHistoryActivity extends Activity {

	MapView mapHistory;
	BaiduMap bmHistory;
	UiSettings uisHistory;
	MatteoHistory h;
	TextView tvStartDay, tvEndDay;
	ArrayList<Marker> arrayMarker;
	RelativeLayout rlThis;
	private Date StartDay,EndDay;
	private static int DefaultDaysSpan=7;
	private ArrayList<Picture> aryPics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pic_history);
		this.tvEndDay = (TextView) this.findViewById(R.id.tvEndDay);
		this.tvStartDay = (TextView) this.findViewById(R.id.tvStartDay);
		this.rlThis = (RelativeLayout) this.findViewById(R.id.rlPicHistory);
		h = new MatteoHistory();
		mapHistory = (MapView) findViewById(R.id.mapHistory);
		this.arrayMarker = new ArrayList<Marker>();
		this.aryPics=new ArrayList<Picture>();
		this.Init();
	}

	private void Init() {
		this.InitDatePicker();
		this.InitializeMap();
		new PictureLoadTask().execute();
		this.InitMarkerButton();
	}

	private void InitializeMap() {
		bmHistory = mapHistory.getMap();
		this.uisHistory = this.bmHistory.getUiSettings();
		this.uisHistory.setOverlookingGesturesEnabled(false);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(new LatLng(Matteo.BeijingLatitude,Matteo.BeijingLongitude));
		this.bmHistory.setMapStatus(msu);
		float z = 13;//Matteo.BaiduAKh.GetZoom();
		msu = MapStatusUpdateFactory.zoomTo(z);
		this.bmHistory.setMapStatus(msu);
	}

	private void InitDatePicker() {
		this.EndDay=new Date();
		this.StartDay=new Date(this.EndDay.getTime()-DefaultDaysSpan*24*3600*1000);
		this.tvStartDay.setText(MatteoHistory.DayFormatter2.format(this.StartDay));
		this.tvEndDay.setText(MatteoHistory.DayFormatter2.format(this.EndDay));
		this.tvStartDay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar cld = Calendar.getInstance();
				cld.setTime(StartDay);
				new DatePickerDialog(PicHistoryActivity.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker dp, int year,
									int month, int dayOfMonth) {
								String d = year + "-" + (month + 1) + "-"
										+ dayOfMonth;
								tvStartDay.setText(d);
								SetStartDay(d);
							}
						}, cld.get(Calendar.YEAR), cld.get(Calendar.MONTH), cld
								.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		this.tvEndDay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar cld = Calendar.getInstance();
				cld.setTime(EndDay);
				new DatePickerDialog(PicHistoryActivity.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker dp, int year,
									int month, int dayOfMonth) {
								String d = year + "-" + (month + 1) + "-"
										+ dayOfMonth;
								tvEndDay.setText(d);
								SetEndDay(d);
							}
						}, cld.get(Calendar.YEAR), cld.get(Calendar.MONTH), cld
								.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
	}

	private void InitMarkerButton() {
		this.bmHistory.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				int i = arrayMarker.indexOf(marker);
				Intent intent=new Intent(PicHistoryActivity.this,SwitchPicActivity.class);
				Bundle bundle = new Bundle();
			    bundle.putInt(Conf.PICTUREINDEX, i);
			    bundle.putParcelableArrayList(Conf.PICTUREARRAY, aryPics);
			    intent.putExtras(bundle);
				startActivity(intent);
				return true;
			}
		});
	}
	
	private void ClearMarkers(){
		for(Marker m:this.arrayMarker){
			m.remove();
		}
		this.arrayMarker.clear();
	}
	
	private void LoadPictureMarker(Picture pic){
		Bitmap bm=pic.GetIImage();
		//BitmapDescriptor bdA = BitmapDescriptorFactory.fromBitmap(bm);
		BitmapDescriptor bdA = BitmapDescriptorFactory.fromBitmap(bm);
		OverlayOptions oo = new MarkerOptions().position(pic.GetLatLng()).icon(bdA)
				.zIndex(9);
		Marker mr = (Marker) (this.bmHistory.addOverlay(oo));
		this.arrayMarker.add(mr);
	}
	
	private void SetStartDay(String d){
		
	}
	
	private void SetEndDay(String d){
		
	}
	
	private String GetUrl(){
		String u=Conf.PicHistoryURL+"?sd="+Fun.Encode(Fun.ParseDate(StartDay, "yyyy-M-d"))
				+"&ed="+Fun.Encode(Fun.ParseDate(this.EndDay, "yyyy-M-d"));
		return u;
	}
	
	public class PictureLoadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String xs=Fun.GetXML(GetUrl());
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser=null;
			try {
				parser = factory.newSAXParser();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			PictureHandler handler = new PictureHandler();
			StringReader read = new StringReader(xs);
			// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
			InputSource source = new InputSource(read);
			try {
				parser.parse(source, handler);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			read.close();
			//publishProgress();
			return null;
		}

		@Override
		public void onProgressUpdate(Void... voids) {

			if (isCancelled())
				return;
			// 更新UI

		}
	}
	
	public class PictureHandler extends DefaultHandler {
		// private ArrayList<Picture> aryPics;

		public PictureHandler() {
			if (aryPics == null) {
				aryPics = new ArrayList<Picture>();
			}
		}

		@Override
		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {
			if(localName.equals("list")){
				double lat=Double.parseDouble(atts.getValue("centerlat"));
				double lon=Double.parseDouble(atts.getValue("centerlon"));
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(new LatLng(lat,lon));
				float zoom=Float.parseFloat(atts.getValue("zoom"));
				bmHistory.setMapStatus(msu);
				msu=MapStatusUpdateFactory.zoomTo(zoom);
				bmHistory.setMapStatus(msu);
			}
			else if (localName.equals("picture")) {
				Picture pic = new Picture(atts);
				aryPics.add(pic);
				LoadPictureMarker(pic);
			}
		}

		@Override
		public void endElement(String namespaceURI, String localName,
				String qName) throws SAXException {
			if (!localName.equals("picture")) {
				return;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {

		}
	}
}
