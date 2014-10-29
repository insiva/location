package com.mlh.activity;


import com.mlh.Config;
import com.mlh.R;
import com.mlh.communication.HttpConnnection;
import com.mlh.component.PicListAdapter;
import com.mlh.component.RefreshListView;
import com.mlh.model.Album;
import com.mlh.model.Category;
import com.mlh.model.ICallBack;
import com.mlh.model.Picture;

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
import android.widget.LinearLayout;

/**
 * @author Matteo
 *照片列表Activity
 */
public class PicListActivity extends Activity {

	private RefreshListView lvPic;
	private PicListAdapter plaList;
	private Handler hdlGetXML;
	private static final int LenUnit=10;
	private int CurrentIndex;
	private Category mCate;
	private Button btnCate;
	private LinearLayout llRlv;
	
	private ICallBack cbLoadMore=new ICallBack(){

		@Override
		public void refresh(final Handler hdl) {
			// TODO Auto-generated method stub
            new Thread() {
                public void run(){
                    loadMore();
                    Message msg = hdl.obtainMessage();
                    msg.what = RefreshListView.REFRESH_DONE;
                    //通知主线程加载数据完成
                    hdl.sendMessage(msg);
                };
            }.start();
		}

		@Override
		public void clickItem(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			if(position==plaList.getAlbum().size()){
				return;
			}
			Intent intent=new Intent(PicListActivity.this,AlbumActivity.class);
			Bundle bundle = new Bundle();
		    bundle.putInt(Album.ALBUM_INDEX, position);
		    bundle.putParcelable(Album.ALBUM, plaList.getAlbum());
		    intent.putExtras(bundle);
			startActivity(intent);
		}
		
	};
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pic_list);
		Config.logCurrentThreadID("PicListActivity");
		this.mCate=(Category)this.getIntent().getExtras().getParcelable(Category.CATEGORY);
		this.lvPic=(RefreshListView)this.findViewById(R.id.lvPic);
		this.llRlv=(LinearLayout)this.findViewById(R.id.llRlv);
		this.llRlv.measure(0, 0);
		this.lvPic.SetCallBack(this.cbLoadMore);
		this.plaList=new PicListAdapter(this,this.lvPic);
		this.lvPic.setAdapter(this.plaList);
		this.hdlGetXML=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				if(msg.what!=Config.WHAT_XML)
					return;
				String xmlStr=msg.getData().getString(Config.XML);
				readXML(xmlStr);
			}
		};
		HttpConnnection.getXMLByThread(this.getURL(0,LenUnit), this.hdlGetXML);
		this.btnCate=(Button)this.findViewById(R.id.btnCate);
		this.btnCate.setText(this.mCate.getDescription());
		this.btnCate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
	}
	
	private String getURL(int s,int l){
		String u=Picture.PAGE_PICTURE_LIST+"?d="+HttpConnnection.encode(this.mCate.getCategoryValue())+"&si="+s+"&len="+l+"&t="+this.mCate.getType();
		//this.CurrentIndex=s+l;
		return u;
	}
	
	private void readXML(String xmlStr){
		this.plaList.readXML(xmlStr);
		this.CurrentIndex=this.plaList.getAlbum().size();
		this.plaList.loadImage();
		//this.lvPic.AddFooter();
	}
	
	private void loadMore(){
		String u=this.getURL(this.CurrentIndex, LenUnit);
		HttpConnnection.getXml(u, this.hdlGetXML);
	}
}
