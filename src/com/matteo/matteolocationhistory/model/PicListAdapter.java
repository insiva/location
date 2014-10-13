package com.matteo.matteolocationhistory.model;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.matteo.matteolocationhistory.R;
import com.matteo.matteolocationhistory.component.RefreshListView;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PicListAdapter extends BaseAdapter {
	private LayoutInflater lifPicItem;
	private Context ctLPA;
	private String xmlStr;
	public ArrayList<Picture> aryPics = null;
	private RefreshListView rlvPic;

	public PicListAdapter(Context context,RefreshListView v) {
		this.lifPicItem = LayoutInflater.from(context);
		this.ctLPA = context;
		this.aryPics = new ArrayList<Picture>();
		this.rlvPic=v;
	}

	public void ReadXML(String xs)  {
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
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.aryPics.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		PictureItemHolder hdPic;
		if (convertView == null) {
			hdPic = new PictureItemHolder(this);
			convertView = this.lifPicItem.inflate(R.layout.pic_item_layout,
					null);
			hdPic.ivPic = (ImageView) convertView.findViewById(R.id.ivPicture);
			// hdPic.ivPic.setImageResource(R.drawable.testpic);
			hdPic.tvAddress = (TextView) convertView
					.findViewById(R.id.tvAddress);
			hdPic.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			convertView.setTag(hdPic);
		} else {
			hdPic = (PictureItemHolder) convertView.getTag();
		}
		hdPic.SetValues(position);

		return convertView;
	}
	
	public void LoadImage(){
		new ImageLoadTask().execute();
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
			if (!localName.equals("picture")) {
				return;
			}
			Picture pic = new Picture(atts);
			aryPics.add(pic);
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

	public static class PictureItemHolder {

		public TextView tvAddress, tvTime;
		public ImageView ivPic;
		public PicListAdapter pla;

		public PictureItemHolder(PicListAdapter l) {
			this.pla=l;
		}

		public void SetValues(int p) {
			if (this.pla.aryPics.get(p).SBmPic != null) {
				this.ivPic.setImageBitmap(this.pla.aryPics.get(p).SBmPic);
			}
			this.tvAddress.setText(p+":"+this.pla.aryPics.get(p).Loc.Address);
			this.tvTime.setText(Fun.ParseDate(this.pla.aryPics.get(p).CreateTime, Conf.DateF));
		}
	}

	public class ImageLoadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			for (int i = 0; i < aryPics.size(); i++) {
				Picture pic = aryPics.get(i);
				pic.GetSImage();
				publishProgress();
			}
			return null;
		}

		@Override
		public void onProgressUpdate(Void... voids) {

			if (isCancelled())
				return;
			// 更新UI
			notifyDataSetChanged();

		}
	}
}
