package com.mlh.component;

import com.mlh.Config;
import com.mlh.R;
import com.mlh.activity.PicListActivity;
import com.mlh.communication.HttpConnnection;
import com.mlh.model.Category;
import com.mlh.model.ICallBack;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class CategoryFragment extends Fragment {
	int cateType;
    RefreshListView rlvCate;
    Context actCate;
	private CategoryListAdapter claList;
	private Handler hdlGetXML;
	private static final int LenUnit=19;
	private int CurrentIndex;
	
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
			if(position==claList.aryCate.size()){
				return;
			}
			Intent intent=new Intent(actCate,PicListActivity.class);
			Bundle bundle = new Bundle();
		    bundle.putParcelable(Category.CATEGORY, claList.aryCate.get(position));
		    intent.putExtras(bundle);
			startActivity(intent);
		}
		
	};

    public static CategoryFragment newInstance(int pc,Context ctx) {
    	CategoryFragment newFragment = new CategoryFragment(pc,ctx);
        return newFragment;

    }

    public CategoryFragment(int pc,Context ctx)
    {
    	this.cateType=pc;
    	this.actCate=ctx;
    	//this.rlvCate=new RefreshListView(ctx);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("HandlerLeak")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        this.rlvCate=(RefreshListView)view.findViewById(R.id.rlvCate);
		this.rlvCate.SetCallBack(this.cbLoadMore);
		this.claList=new CategoryListAdapter(this.actCate,this.rlvCate);
		this.rlvCate.setAdapter(this.claList);
		this.hdlGetXML=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				String xmlStr=msg.getData().getString(Config.XML);
				readXML(xmlStr);
			}
		};
		HttpConnnection.getXMLByThread(this.getURL(0,LenUnit), this.hdlGetXML);
        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
	
	private void loadMore(){
		String u=this.getURL(this.CurrentIndex, LenUnit);
		HttpConnnection.getXml(u, this.hdlGetXML);
	}

	
	private String getURL(int s,int l){
		String u=null;
		if(this.cateType==Category.DAY_TYPE)
		{
			u=Category.PAGE_DAY+"?si="+s+"&len="+l;
		}else{
			u=Category.PAGE_SITE+"?si="+s+"&len="+l;
		}
		//this.CurrentIndex=s+l;
		return u;
	}
	
	private void readXML(String xmlStr){
		this.claList.readXML(xmlStr);
		this.CurrentIndex=this.claList.aryCate.size();
		this.claList.notifyDataSetChanged();
	}
}
