package com.matteo.matteolocationhistory;

import java.util.Date;

import com.matteo.matteolocationhistory.component.RefreshListView;
import com.matteo.matteolocationhistory.model.CallBack;
import com.matteo.matteolocationhistory.model.Conf;
import com.matteo.matteolocationhistory.model.Fun;
import com.matteo.matteolocationhistory.model.MtLocation;
import com.matteo.matteolocationhistory.model.PicCategory;
import com.matteo.matteolocationhistory.model.PicListAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;

public class PicListActivity extends Activity {

	private RefreshListView lvPic;
	private PicListAdapter plaList;
	private Handler hdlGetXML;
	private static final int LenUnit=10;
	private int CurrentIndex;
	private PicCategory Cate;
	private Button btnCate;
	
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
			if(position==plaList.aryPics.size()){
				return;
			}
			Intent intent=new Intent(PicListActivity.this,SwitchPicActivity.class);
			Bundle bundle = new Bundle();
		    bundle.putInt(Conf.PICTUREINDEX, position);
		    bundle.putParcelableArrayList(Conf.PICTUREARRAY, plaList.aryPics);
		    intent.putExtras(bundle);
			startActivity(intent);
		}
		
	};
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pic_list);
		this.Cate=(PicCategory)this.getIntent().getExtras().getSerializable(PicCategory.CATEGORY);
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
		Fun.GetXMLByThread(this.GetURL(0,LenUnit), this.hdlGetXML);
		this.btnCate=(Button)this.findViewById(R.id.btnCate);
		this.btnCate.setText(this.Cate.GetDescription());
		this.btnCate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent intent=new Intent(PicListActivity.this,PicCategoryActivity.class);
				//startActivity(intent);
				finish();
			}
			
		});
	}
	
	private String GetURL(int s,int l){
		String u=Conf.PicListURL+"?d="+Fun.Encode(this.Cate.CateStr)+"&si="+s+"&len="+l+"&t="+this.Cate.Type;
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
