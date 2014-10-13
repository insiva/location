package com.matteo.matteolocationhistory;

import java.util.Date;

import com.matteo.matteolocationhistory.component.RefreshListView;
import com.matteo.matteolocationhistory.model.CallBack;
import com.matteo.matteolocationhistory.model.Conf;
import com.matteo.matteolocationhistory.model.Fun;
import com.matteo.matteolocationhistory.model.PicListAdapter;
import com.matteo.matteolocationhistory.model.Picture;
import com.matteo.matteolocationhistory.model.PictureItem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ListPicActivity extends Activity {

	private RefreshListView lvPic;
	private PicListAdapter plaList;
	private Handler hdlGetXML;
	private String StartTime;
	private static final int LenUnit=19;
	private int CurrentIndex;
	private CallBack cbLoadMore=new CallBack(){

		@Override
		public void Refresh(final Handler hdl) {
			// TODO Auto-generated method stub
            new Thread() {
                public void run(){
                    LoadMore();
                    Message msg = hdl.obtainMessage();
                    msg.what = RefreshListView.REFRESH_DONE;
                    //通知主线程加载数据完成
                    hdl.sendMessage(msg);
                };
            }.start();
		}

		@Override
		public void ClickItem(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_pic);
		this.lvPic=(RefreshListView)this.findViewById(R.id.lvPic);
		this.lvPic.SetCallBack(this.cbLoadMore);
		this.plaList=new PicListAdapter(this,this.lvPic);
		this.lvPic.setAdapter(this.plaList);
		this.hdlGetXML=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				if(msg.what!=Conf.GETXMLWHAT)
					return;
				String xmlStr=msg.getData().getString(Conf.XML);
				ReadXML(xmlStr);
			}
		};
		this.StartTime=Fun.Encode(Fun.ParseDate(new Date()));
		Fun.GetXMLByThread(this.GetURL(0,LenUnit), this.hdlGetXML);
	}
	
	private String GetURL(int s,int l){
		String u=Conf.PicListURL+"?st="+this.StartTime+"&si="+s+"&len="+l;
		//this.CurrentIndex=s+l;
		return u;
	}
	
	private void ReadXML(String xmlStr){
		this.plaList.ReadXML(xmlStr);
		this.CurrentIndex=this.plaList.aryPics.size();
		this.plaList.LoadImage();
		//this.lvPic.AddFooter();
	}
	
	private void LoadMore(){
		Fun.GetXML(this.GetURL(this.CurrentIndex, LenUnit), this.hdlGetXML);
	}
}
