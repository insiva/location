package com.matteo.matteolocationhistory;

import java.util.Date;

import com.matteo.matteolocationhistory.component.RefreshListView;
import com.matteo.matteolocationhistory.model.CallBack;
import com.matteo.matteolocationhistory.model.CategoryListAdapter;
import com.matteo.matteolocationhistory.model.Conf;
import com.matteo.matteolocationhistory.model.Fun;
import com.matteo.matteolocationhistory.model.PicCategory;
import com.matteo.matteolocationhistory.model.PicListAdapter;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

@SuppressLint("NewApi")
public class PicCategoryActivity extends Activity implements OnNavigationListener {

	private RefreshListView lvCate;
	private CategoryListAdapter claList;
	private Handler hdlGetXML;
	private static final int LenUnit=19;
	private int CurrentIndex;
	private ActionBar abTitle;
	private int CurrentType=0;
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
			if(position==claList.aryCate.size()){
				return;
			}
			Intent intent=new Intent(PicCategoryActivity.this,PicListActivity.class);
			Bundle bundle = new Bundle();
		    bundle.putSerializable(PicCategory.CATEGORY, claList.aryCate.get(position));
		    //bundle.putInt(Conf.CATEGORYTYPE, CurrentType);
		    intent.putExtras(bundle);
			startActivity(intent);
		}
		
	};
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pic_category);
		this.abTitle=this.getActionBar();
		this.abTitle.setDisplayShowTitleEnabled(false);
		this.lvCate=(RefreshListView)this.findViewById(R.id.lvCate);
		this.lvCate.SetCallBack(this.cbLoadMore);
		this.claList=new CategoryListAdapter(this,this.lvCate);
		this.lvCate.setAdapter(this.claList);
		this.hdlGetXML=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				if(msg.what!=Conf.GETXMLWHAT)
					return;
				String xmlStr=msg.getData().getString(Conf.XML);
				ReadXML(xmlStr);
			}
		};
		SpinnerAdapter sa=ArrayAdapter.createFromResource(this,R.array.CategoryList,android.R.layout.simple_spinner_dropdown_item);
        this.abTitle.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        // 为ActionBar设置下拉菜单和监听器
        this.abTitle.setListNavigationCallbacks(sa, this);
        
        this.SetCategoryType(0);
        
	}
	
	private String GetURL(int s,int l){
		String u=null;
		if(this.CurrentType==Conf.DayType)
		{
			u=Conf.DayListURL+"?si="+s+"&len="+l;
		}else{
			u=Conf.SiteListURL+"?si="+s+"&len="+l;
		}
		//this.CurrentIndex=s+l;
		Fun.Log(u);
		return u;
	}
	
	private void ReadXML(String xmlStr){
		this.claList.ReadXML(xmlStr);
		this.CurrentIndex=this.claList.aryCate.size();
		Fun.Log(claList.aryCate.get(0).CateStr);
		this.claList.notifyDataSetChanged();
		//this.lvCate.AddFooter();
	}
	
	private void LoadMore(){
		Fun.GetXML(this.GetURL(this.CurrentIndex, LenUnit), this.hdlGetXML);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		//Fun.Log("Item:"+itemPosition);
		if(this.CurrentType==itemPosition){
			return false;
		}
		Fun.Log("onNavigationItemSelected:"+itemPosition);
		this.SetCategoryType(itemPosition);
		return false;
	}
	
	private void SetCategoryType(int t){
		this.CurrentType=t;
		this.claList.ClearArray();
		Fun.GetXMLByThread(this.GetURL(0,LenUnit), this.hdlGetXML);
	}
}
