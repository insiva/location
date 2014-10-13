package com.matteo.matteolocationhistory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.matteo.matteolocationhistory.PicHistoryActivity.PictureHandler;
import com.matteo.matteolocationhistory.model.Conf;
import com.matteo.matteolocationhistory.model.Fun;
import com.matteo.matteolocationhistory.model.Picture;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeActivity extends Activity {
	RelativeLayout rlSite,rlPic,rlCamera,rlDistance;
	TextView tvSite,tvPic,tvCamera,tvDistance;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		this.rlCamera=(RelativeLayout)this.findViewById(R.id.rlCamera);
		this.rlPic=(RelativeLayout)this.findViewById(R.id.rlPic);
		this.rlSite=(RelativeLayout)this.findViewById(R.id.rlSite);
		this.rlDistance=(RelativeLayout)this.findViewById(R.id.rlDistance);
		this.tvCamera=(TextView)this.findViewById(R.id.tvCarema);
		this.tvSite=(TextView)this.findViewById(R.id.tvSite);
		this.tvPic=(TextView)this.findViewById(R.id.tvPic);
		this.tvDistance=(TextView)this.findViewById(R.id.tvDistance);
		this.SetClick();
		new MainLoadTask().execute();
		this.SetClick();
	}
	
	private void SetText(Attributes atts){
		this.tvCamera.setText("拍照");
		int d=(int)Double.parseDouble(atts.getValue("distance"));
		this.tvDistance.setText("您一共旅行了"+Fun.GetDistanceText(d));
		this.tvPic.setText("拍摄了"+atts.getValue("piccount")+"张照片");
		this.tvSite.setText("曾去过"+atts.getValue("sitecount")+"个地点");
	}
	
	private void SetClick(){
		this.rlCamera.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(HomeActivity.this,SystemCameraActivity.class);
				startActivity(intent);
				
			}});
		this.rlDistance.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(HomeActivity.this,HistoryActivity.class);
				startActivity(intent);
			}});
		this.rlPic.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(HomeActivity.this,PicCategoryActivity.class);
				startActivity(intent);
			}});
		this.rlSite.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(HomeActivity.this,PicHistoryActivity.class);
				startActivity(intent);
			}});
	}
	

	public class MainLoadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String xs=Fun.GetXML(Conf.MainURL);
			Fun.Log(xs);
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
			MainHandler handler = new MainHandler();
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
	
	public class MainHandler extends DefaultHandler {
		// private ArrayList<Picture> aryPics;

		public MainHandler() {
		}

		@Override
		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {
			if(localName.equals("matteo")){
				Fun.Log("startElement");
				SetText(atts);
			}
		}

		@Override
		public void endElement(String namespaceURI, String localName,
				String qName) throws SAXException {
			if (!localName.equals("matteo")) {
				return;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {

		}
	}
}
