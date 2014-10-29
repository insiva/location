package com.mlh.activity;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.mlh.Config;
import com.mlh.R;
import com.mlh.component.RouteView;
import com.mlh.model.Route;
import com.mlh.model.Trajectory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

/**
 * @author Matteo
 *¶¯Ì¬ÑÝÊ¾¹ì¼£Activity
 */
public class TraceActivity extends Activity implements OnClickListener {
	private static final long TRACE_INTERVAL=100;
	MapView mapTrace;
	BaiduMap bmTrace;
	UiSettings uisTrace;
	RelativeLayout rlPlay, rlTrace;
	RouteView routeView;
	boolean RlPlayClicked = false;
	private Route mRoute;
	private boolean Playing = false;
	private boolean Stoped = false;
	public boolean TrajViewCanTouch;
	private TraceThread ttTrace;
	private static final int CHANGE_RLPLAY_BG = 0x100;
	@SuppressLint("HandlerLeak")
	Handler hdlPlay = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == CHANGE_RLPLAY_BG) {
				RlPlayClicked = false;
				Playing = false;
				rlPlay.setBackgroundResource(R.drawable.bg_play);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trace);
		this.mRoute = this.getIntent().getExtras().getParcelable(Route.ROUTE);
		this.init();
	}

	private void init() {
		// this.htHome=new HomeTask(TaskType.XML);
		this.Playing = true;
		this.RlPlayClicked = true;
		this.rlTrace = (RelativeLayout) this.findViewById(R.id.rlTrace);
		this.rlPlay = (RelativeLayout) this.findViewById(R.id.rlPlay);
		this.rlPlay.setOnClickListener(this);
		this.getTrajViewInstance();
		this.initMap();
		this.routeView.setRoute(this.mRoute);
		this.ttTrace = new TraceThread();
		this.ttTrace.start();
	}

	private void initMap() {
		BaiduMapOptions bmo = new BaiduMapOptions().compassEnabled(false)
				.overlookingGesturesEnabled(false).rotateGesturesEnabled(false)
				.scaleControlEnabled(true).scrollGesturesEnabled(true)
				.zoomControlsEnabled(true).zoomGesturesEnabled(true);
		this.mapTrace = new MapView(this, bmo);
		this.rlTrace.addView(this.mapTrace, 0);
		// this.mapHistory.
		bmTrace = mapTrace.getMap();
		this.uisTrace = this.bmTrace.getUiSettings();
		this.uisTrace.setOverlookingGesturesEnabled(false);
		MapStatusUpdate msu = MapStatusUpdateFactory
				.newLatLng(Config.BEIJING_LATLNG);
		this.bmTrace.setMapStatus(msu);
		float z = 13;
		msu = MapStatusUpdateFactory.zoomTo(z);
		this.bmTrace.setMapStatus(msu);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rlPlay:
			this.clickRlPlay();
			break;
		default:
			break;
		}
	}

	private void clickRlPlay() {
		if (!this.ttTrace.isAlive()) {
			this.rlPlay.setBackgroundResource(R.drawable.bg_pause);
			this.RlPlayClicked = true;
			this.Playing = true;
			this.ttTrace = new TraceThread();
			this.ttTrace.start();
			return;
		}
		if (this.RlPlayClicked) {
			this.rlPlay.setBackgroundResource(R.drawable.bg_play);
			this.tracePause();
		} else {
			this.rlPlay.setBackgroundResource(R.drawable.bg_pause);
			this.traceResume();
		}
		this.RlPlayClicked = !this.RlPlayClicked;
	}

	private void getTrajViewInstance() {
		this.routeView = new RouteView(this);
		this.rlTrace.addView(this.routeView, this.routeView.getLayoutParams());
		this.routeView.setVisibility(View.INVISIBLE);
	}

	public class TraceThread extends Thread {

		@Override
		public void run() {
			this.startTrace();
		}

		private void startTrace() {
			if(!computeTrace()){
				hdlPlay.sendEmptyMessage(CHANGE_RLPLAY_BG);
				return;
			}
			mapTrace.getMap().clear();
			int pn = 0;
			int i = 0;
			Trajectory traj=null;
			OverlayOptions oo = null;
			List<LatLng> points = new ArrayList<LatLng>();
			LatLng ll1, ll2 = null;
			while (true) {
				if (!Playing)
					continue;
				if (Stoped) {
					break;
				}
				if (pn == 0) {
					traj = mRoute.get(i);
					ll1 = traj.getLatLng();
					oo = new DotOptions().center(ll1).radius(10)
							.color(0xFFFF0000).zIndex(1);
					bmTrace.addOverlay(oo);
					if (traj.getPaceNum() == 0) {
						break;
					}
					points.clear();
					points.add(ll1);
					bmTrace.setMapStatus(getMapStatusUpdate(i));
				}
				ll1 = points.get(0);
				ll2 = new LatLng(ll1.latitude + traj.getLatPace(), ll1.longitude
						+ traj.getLngPace());
				points.add(ll2);
				oo = new PolylineOptions().width(5).color(0xFF80adff)
						.points(points).zIndex(2);
				bmTrace.addOverlay(oo);
				oo = null;
				points.remove(0);
				pn++;
				float r = (float) pn / (float) traj.getPaceNum();
				routeView.setTraceTime(i, r);
				routeView.postInvalidate();
				if (pn == traj.getPaceNum()) {
					pn = 0;
					i++;
				}
				try {
					Thread.sleep(TraceActivity.TRACE_INTERVAL);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			hdlPlay.sendEmptyMessage(CHANGE_RLPLAY_BG);
		}
	}

	private void traceResume() {
		this.Playing = true;
	}

	private void tracePause() {
		this.Playing = false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return false;
	}
	
	private MapStatusUpdate getMapStatusUpdate(int i){
		if(i<(this.mRoute.size()-1))
		{
			LatLngBounds.Builder b=new LatLngBounds.Builder();
			b.include(this.mRoute.get(i).getLatLng());
			b.include(this.mRoute.get(i+1).getLatLng());
			LatLngBounds llb=b.build();
			MapStatusUpdate msu=MapStatusUpdateFactory.newLatLngBounds(llb, HomeActivity.VIEW_WIDTH, HomeActivity.VIEW_HEIGHT);
			return msu;
		}
		MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(this.mRoute.get(i).getLatLng());
		return msu;
	}
	
	private boolean computeTrace(){
		if(this.mRoute.size()<2)return false;
		Trajectory traj1,traj2;
		for(int i=0;i<this.mRoute.size();i++){
			traj1=this.mRoute.get(i);
			if(i==(this.mRoute.size()-1)){
				traj1.setPaceNum(0);
				traj1.setLatPace(0);
				traj1.setLngPace(0);
				break;
			}
			traj2=this.mRoute.get(i+1);
			int pn=5+this.log2(traj1.getLength());
			traj1.setPaceNum(pn);
			double p=(traj2.getLatitude()-traj1.getLatitude())/pn;
			traj1.setLatPace(p);
			p=(traj2.getLongitude()-traj1.getLongitude())/pn;
			traj1.setLngPace(p);
		}
		return true;
	}
	
	public int log2(double d){
		if(d<=2)return 0;
		return (int) Math.ceil(Math.log(d)/Math.log(2));
	}
}
