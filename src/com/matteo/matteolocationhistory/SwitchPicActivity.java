package com.matteo.matteolocationhistory;

import java.io.File;
import java.util.ArrayList;

import com.matteo.matteolocationhistory.model.Conf;
import com.matteo.matteolocationhistory.model.Fun;
import com.matteo.matteolocationhistory.model.Picture;
import com.matteo.matteolocationhistory.model.PicListAdapter.ImageLoadTask;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

@SuppressLint("NewApi")
public class SwitchPicActivity extends Activity implements OnGestureListener {
	private GestureDetector detector;
	private ViewFlipper flipper;
	private LayoutInflater inflater;
	private TextView tvAddress,tvDate;
	private Button btnPre,btnRaw,btnNext;
	Intent getMainActivity = null;
	int count = 1;
	private int CurrentIndex=5;
	private ArrayList<Picture> aryPics;

	private Handler hdlRawPic = new Handler() {
		public void handleMessage(Message msg) {
			Fun.Log("hdlRawPic!!!");
			if (msg.what != Conf.PICTURETLOADED)
				return;
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			String p=Matteo.TempPicDirectory+Conf.PICTURETYPESTRH+CIP.P.Name;
			Fun.Log(p);
			File f=new File(p);
			intent.setDataAndType(Uri.fromFile(f), "image/*");
			startActivity(intent);
		}
	};
	
	private ImagePic AIP,BIP,CIP;
	
	
	@SuppressLint("NewApi")
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.CurrentIndex=this.getIntent().getExtras().getInt(Conf.PICTUREINDEX);
		this.aryPics=this.getIntent().getExtras().getParcelableArrayList(Conf.PICTUREARRAY);
		this.inflater = LayoutInflater.from(this);
		final View layout = inflater
				.inflate(R.layout.activity_switch_pic, null);
		setContentView(layout);
		flipper = (ViewFlipper) findViewById(R.id.view_flipper);
		detector = new GestureDetector(this);
		
		View v=this.inflater.inflate(R.layout.switch_item_layout, null);
		ImageView iv=(ImageView)v.findViewById(R.id.ivPic);
		ProgressBar pb=(ProgressBar)v.findViewById(R.id.pbWait);
		this.AIP=new ImagePic(v,iv,pb);
		v=this.inflater.inflate(R.layout.switch_item_layout, null);
		iv=(ImageView)v.findViewById(R.id.ivPic);
		pb=(ProgressBar)v.findViewById(R.id.pbWait);
		this.BIP=new ImagePic(v,iv,pb);
		
		this.tvAddress=(TextView)this.findViewById(R.id.tvAddress);
		this.tvDate=(TextView)this.findViewById(R.id.tvDate);
		this.btnNext=(Button)this.findViewById(R.id.btnNextPic);
		this.btnRaw=(Button)this.findViewById(R.id.btnRawPic);
		this.btnPre=(Button)this.findViewById(R.id.btnPrePic);
		
		this.SetPicView(0);
		this.flipper.setDisplayedChild(0);
		
		this.btnNext.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MoveNext();
			}});
		this.btnRaw.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(aryPics.get(CurrentIndex).FileExists(Conf.PICTURETYPEH))
				{
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(aryPics.get(CurrentIndex).GetTempUri(Conf.PICTURETYPEH), "image/*");
					startActivity(intent);
				}else{
					Fun.GetImageByThread(CIP.P.Name, Conf.PICTURETYPEH, hdlRawPic);
				}
			}});
		this.btnPre.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MovePrevious();
			}});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.detector.onTouchEvent(event);
	}

	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float arg2,
			float arg3) {
		if (e1.getX() - e2.getX() > 5) {
			this.MoveNext();
			return true;
		} else if (e1.getX() - e2.getX() < -5) {
			this.MovePrevious();
			return true;
		}
		return true;
	}

	private void MovePrevious(){
		if(this.CurrentIndex==0){
			return;
		}
		this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_right_in));
		this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_right_out));
		this.SetPicView(-1);
		this.flipper.showPrevious();
	}
	
	private void MoveNext(){
		if(this.CurrentIndex==(this.aryPics.size()-1)){
			return;
		}
		this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_in));
		this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_out));
		this.SetPicView(1);
		this.flipper.showNext();
	}
	
	private void SetPicView(int d){
        this.CurrentIndex=this.CurrentIndex+d;
        ImagePic OIP=null;
        if(this.CIP==this.AIP){
        	OIP=this.BIP;
        }else{
        	OIP=this.AIP;
        }
        OIP.RecyclePic();
    	this.flipper.removeView(OIP.V);
    	Picture pic=this.aryPics.get(this.CurrentIndex);
        OIP.ShowWait();
    	OIP.SetPicture(pic);
    	this.CIP=OIP;
        if(d>0)//向右
        {
        	this.flipper.addView(OIP.V,1);
        }else if(d<0){//向左
        	this.flipper.addView(OIP.V,0);
        }else{
        	this.AIP.IV.setImageResource(R.drawable.wait);
        	this.flipper.addView(this.AIP.V);
    		this.flipper.addView(this.BIP.V);
        }
        this.LoadPictureBegin();
        this.tvAddress.setText(this.CIP.P.Loc.Address);
        this.tvDate.setText(Fun.ParseDate(this.CIP.P.CreateTime));
	}
	
	private void LoadPictureBegin(){
		new ImageLoadTask().execute();
	}
	
	private void LoadPictureEnd(){
		this.CIP.IV.setImageBitmap(this.CIP.P.MBmPic);
		this.CIP.HideWait();
	}
	
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public class ImagePic{
		public View V;
		public ImageView IV;
		public Picture P=null;
		public ProgressBar PB;
		public ImagePic(View v,ImageView iv,Picture p){
			this.V=v;
			this.IV=iv;
			this.P=p;
		}
		public ImagePic(View v,ImageView iv,ProgressBar pb){
			this.V=v;
			this.IV=iv;
			this.PB=pb;
		}
		public void SetPicture(Picture p){
			this.P=p;
		}
		
		public void RecyclePic(){
			if(this.P==null){
				return;
			}
			this.P.RecycleMImage();
		}
		public void ShowWait(){
			this.IV.setVisibility(View.INVISIBLE);
			this.PB.setVisibility(View.VISIBLE);
		}
		public void HideWait(){
			this.IV.setVisibility(View.VISIBLE);
			this.PB.setVisibility(View.INVISIBLE);
		}
	}

	public class ImageLoadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if(CIP.P.MBmPic!=null)
				return null;
			CIP.P.GetMImage();
			publishProgress();
			return null;
		}

		@Override
		public void onProgressUpdate(Void... voids) {

			if (isCancelled())
				return;
			// 更新UI
			LoadPictureEnd();

		}
	}
}